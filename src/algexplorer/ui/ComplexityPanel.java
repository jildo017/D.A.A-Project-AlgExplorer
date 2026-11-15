package algexplorer.ui;

import algexplorer.analysis.ExperimentData;
import algexplorer.analysis.ExperimentRunner;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

/** Module D screen: runs the timing experiment, draws the two charts, shows the tables, saves a CSV file. */
public class ComplexityPanel extends JPanel {
    private final JButton runButton = new JButton("Run experiment");
    private final JButton cancelButton = new JButton("Cancel");
    private final JButton saveButton = new JButton("Save CSV...");
    private final JCheckBox bigBox = new JCheckBox("Also n = 50,000 and 100,000 (slow: first-pivot quicksort on sorted input)");
    private final JCheckBox logBox = new JCheckBox("Log scales");
    private final JComboBox<String> inputBox = new JComboBox<>(ExperimentRunner.INPUTS);
    private final JProgressBar bar = new JProgressBar(0, 100);
    private final JLabel statusLabel = new JLabel(" ");
    private final LineChartPanel quadraticChart = new LineChartPanel();
    private final LineChartPanel nlognChart = new LineChartPanel();
    private final JTextArea output = new JTextArea();

    private ExperimentData data;
    private volatile boolean cancelRequested;

    public ComplexityPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(runButton);
        row1.add(cancelButton);
        row1.add(saveButton);
        row1.add(bigBox);
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Chart for input:"));
        row2.add(inputBox);
        row2.add(logBox);
        row2.add(bar);
        row2.add(statusLabel);

        JPanel north = new JPanel(new BorderLayout());
        north.add(row1, BorderLayout.NORTH);
        north.add(row2, BorderLayout.SOUTH);

        JPanel charts = new JPanel(new GridLayout(1, 2, 8, 0));
        charts.add(quadraticChart);
        charts.add(nlognChart);

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(output);
        scroll.setPreferredSize(new java.awt.Dimension(700, 190));

        add(north, BorderLayout.NORTH);
        add(charts, BorderLayout.CENTER);
        add(scroll, BorderLayout.SOUTH);

        cancelButton.setEnabled(false);
        saveButton.setEnabled(false);
        runButton.addActionListener(e -> startExperiment());
        cancelButton.addActionListener(e -> cancelRequested = true);
        saveButton.addActionListener(e -> saveCsv());
        inputBox.addActionListener(e -> redraw());
        logBox.addActionListener(e -> redraw());
        redraw();
    }

    private void startExperiment() {
        cancelRequested = false;
        runButton.setEnabled(false);
        cancelButton.setEnabled(true);
        saveButton.setEnabled(false);
        bigBox.setEnabled(false);
        output.setText("Running... the screen may be quiet for a while on the large sizes.");
        boolean big = bigBox.isSelected();

        SwingWorker<ExperimentData, Object[]> worker = new SwingWorker<>() {
            @Override
            protected ExperimentData doInBackground() {
                return ExperimentRunner.run(big, new ExperimentRunner.Progress() {
                    public void update(int done, int total, String text) {
                        publish(new Object[] { done, total, text });
                    }

                    public boolean cancelled() {
                        return cancelRequested;
                    }
                });
            }

            @Override
            protected void process(java.util.List<Object[]> chunks) {
                Object[] last = chunks.get(chunks.size() - 1);
                int done = (Integer) last[0];
                int total = (Integer) last[1];
                bar.setValue(total == 0 ? 0 : done * 100 / total);
                statusLabel.setForeground(Color.DARK_GRAY);
                statusLabel.setText((String) last[2]);
            }

            @Override
            protected void done() {
                runButton.setEnabled(true);
                cancelButton.setEnabled(false);
                bigBox.setEnabled(true);
                try {
                    data = get();
                    saveButton.setEnabled(true);
                    statusLabel.setText(cancelRequested ? "Cancelled (partial results)" : "Finished");
                    redraw();
                } catch (Exception ex) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Experiment failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    /** Draws the two charts and fills the text area from the latest data. */
    private void redraw() {
        if (data == null) {
            quadraticChart.setData("O(n^2) sorts", new int[0], new String[0], new double[0][0], false);
            nlognChart.setData("O(n log n) sorts", new int[0], new String[0], new double[0][0], false);
            return;
        }
        int kind = inputBox.getSelectedIndex();
        boolean log = logBox.isSelected();
        String suffix = " - " + ExperimentRunner.INPUTS[kind] + " input";
        quadraticChart.setData("Bubble, selection, insertion" + suffix, data.sizes, pick(0, 3),
                lines(kind, 0, 3), log);
        // the n^2 chart stops at n = 10,000 because those sorts were not run on larger inputs
        nlognChart.setData("Merge and quicksort" + suffix, data.sizes, pick(3, 6), lines(kind, 3, 6), log);
        output.setText(data.toTables() + data.toGrowthTable());
        output.setCaretPosition(0);
    }

    private String[] pick(int from, int to) {
        String[] r = new String[to - from];
        for (int i = from; i < to; i++) {
            r[i - from] = data.algorithms[i];
        }
        return r;
    }

    private double[][] lines(int kind, int from, int to) {
        double[][] r = new double[to - from][];
        for (int i = from; i < to; i++) {
            r[i - from] = data.ms[i][kind];
        }
        return r;
    }

    private void saveCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("experiment_results.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try (FileWriter w = new FileWriter(chooser.getSelectedFile())) {
            w.write(data.toCsv());
            statusLabel.setForeground(new Color(30, 110, 50));
            statusLabel.setText("Saved " + chooser.getSelectedFile().getName());
        } catch (IOException ex) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Could not save: " + ex.getMessage());
        }
    }
}
