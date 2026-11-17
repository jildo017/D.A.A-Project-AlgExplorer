package ui;

import algorithms.SortingAlgorithms;
import utils.ArrayUtils;
import utils.ExecutionTracker;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

/** SCREEN 4: runs all 10 sorts on the same random array and compares them. */
public class BenchmarkPanel extends JPanel {
    private static final String[] NAMES = SortingAlgorithms.NAMES;
    private static final int MAX_SIZE = 10000;

    private final MainFrame frame;
    private final JTextField sizeField = new JTextField("2000", 6);
    private final JButton runBtn = MainFrame.makeButton("Run Benchmark", MainFrame.GREEN);
    private final JLabel status = new JLabel("Choose an array size (10 - " + MAX_SIZE + ") and press Run Benchmark.");
    private final ResultsModel model = new ResultsModel();
    private final ChartPanel chart = new ChartPanel();

    // results of the latest run
    private long[] nanos = new long[NAMES.length];
    private long[] ops = new long[NAMES.length];
    private boolean[] verified = new boolean[NAMES.length];
    private int[] rank = new int[NAMES.length];
    private boolean hasResults = false;

    public BenchmarkPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(0, 10));
        setBackground(MainFrame.BG);
        setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("Benchmark All Sorts");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        nav.setOpaque(false);
        JButton menu = MainFrame.makeButton("Back to Menu", MainFrame.BLUE);
        JButton home = MainFrame.makeButton("Home", MainFrame.BLUE);
        JButton exit = MainFrame.makeButton("Exit App", MainFrame.RED);
        menu.addActionListener(e -> frame.showScreen("CATEGORIES"));
        home.addActionListener(e -> frame.showScreen("HOME"));
        exit.addActionListener(e -> System.exit(0));
        nav.add(menu);
        nav.add(home);
        nav.add(exit);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);
        header.add(nav, BorderLayout.EAST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        controls.setBackground(MainFrame.CARD);
        JLabel sizeLabel = new JLabel("Array size:");
        sizeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        sizeLabel.setForeground(Color.WHITE);
        sizeField.setBackground(new Color(55, 55, 78));
        sizeField.setForeground(Color.WHITE);
        sizeField.setCaretColor(Color.WHITE);
        status.setForeground(MainFrame.MUTED);
        status.setFont(new Font("SansSerif", Font.PLAIN, 13));
        controls.add(sizeLabel);
        controls.add(sizeField);
        controls.add(runBtn);
        controls.add(status);
        runBtn.addActionListener(e -> runBenchmark());

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(controls, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        JTable table = new JTable(model);
        table.setBackground(MainFrame.CARD);
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(70, 70, 95));
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(59, 130, 246, 120));
        table.getTableHeader().setBackground(MainFrame.BG);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(MainFrame.CARD);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 95)));

        JPanel center = new JPanel(new GridLayout(1, 2, 12, 0));
        center.setOpaque(false);
        center.add(scroll);
        center.add(chart);
        add(center, BorderLayout.CENTER);

        JLabel note = new JLabel("All 10 algorithms sort the SAME random array (values 1-10000). Logging and animation are switched off so only the algorithm is timed.");
        note.setForeground(MainFrame.MUTED);
        note.setFont(new Font("SansSerif", Font.PLAIN, 12));
        add(note, BorderLayout.SOUTH);
    }

    private void runBenchmark() {
        int size;
        try {
            size = Integer.parseInt(sizeField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a whole number for the array size.", "Invalid size", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (size < 10 || size > MAX_SIZE) {
            JOptionPane.showMessageDialog(this, "Array size must be between 10 and " + MAX_SIZE + ".", "Invalid size", JOptionPane.WARNING_MESSAGE);
            return;
        }
        runBtn.setEnabled(false);
        final int n = size;
        final int[] base = ArrayUtils.generateRandomArray(n, 1, 10000);
        final long[] newNanos = new long[NAMES.length];
        final long[] newOps = new long[NAMES.length];
        final boolean[] newOk = new boolean[NAMES.length];

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                setStatus("Warming up...");
                int[] warm = ArrayUtils.generateRandomArray(300, 1, 10000);
                for (int i = 0; i < NAMES.length; i++) {
                    ExecutionTracker w = new ExecutionTracker();
                    w.setLogging(false);
                    SortingAlgorithms.run(NAMES[i], ArrayUtils.copyArray(warm), w, null);
                }
                for (int i = 0; i < NAMES.length; i++) {
                    setStatus("Running " + NAMES[i] + " (" + (i + 1) + "/" + NAMES.length + ") on " + n + " numbers...");
                    int[] work = ArrayUtils.copyArray(base);
                    ExecutionTracker t = new ExecutionTracker();
                    t.setLogging(false);
                    t.startTimer();
                    SortingAlgorithms.run(NAMES[i], work, t, null);
                    t.stopTimer();
                    newNanos[i] = t.getElapsedNano();
                    newOps[i] = t.getOperationCount();
                    newOk[i] = isSorted(work);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    nanos = newNanos;
                    ops = newOps;
                    verified = newOk;
                    rank = new int[NAMES.length];
                    for (int i = 0; i < NAMES.length; i++) {
                        int r = 1;
                        for (int j = 0; j < NAMES.length; j++) {
                            if (nanos[j] < nanos[i]) {
                                r++;
                            }
                        }
                        rank[i] = r;
                    }
                    hasResults = true;
                    model.fireTableDataChanged();
                    chart.repaint();
                    status.setText("Finished: " + n + " numbers sorted by all 10 algorithms.");
                } catch (Exception ex) {
                    status.setText("Benchmark failed: " + ex.getMessage());
                }
                runBtn.setEnabled(true);
            }
        }.execute();
    }

    private void setStatus(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                status.setText(text);
            }
        });
    }

    private static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1] > a[i]) {
                return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------------ table model

    private class ResultsModel extends AbstractTableModel {
        private final String[] columns = {"Rank", "Algorithm", "Operations", "Time (ms)", "Sorted correctly"};

        @Override
        public int getRowCount() {
            return NAMES.length;
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int c) {
            return columns[c];
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (col == 1) {
                return NAMES[row];
            }
            if (!hasResults) {
                return "-";
            }
            switch (col) {
                case 0: return "#" + rank[row];
                case 2: return String.format("%,d", ops[row]);
                case 3: return String.format("%.3f", nanos[row] / 1000000.0);
                default: return verified[row] ? "Yes" : "NO";
            }
        }
    }

    // ------------------------------------------------------------------ bar chart

    private class ChartPanel extends JPanel {
        ChartPanel() {
            setBackground(MainFrame.CARD);
            setBorder(BorderFactory.createLineBorder(new Color(70, 70, 95)));
            setPreferredSize(new Dimension(500, 320));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.setColor(Color.WHITE);
            g.drawString("Running time (ms) - shorter is faster", 14, 26);
            if (!hasResults) {
                g.setColor(MainFrame.MUTED);
                g.drawString("Run the benchmark to see the chart", 14, h / 2);
                g.dispose();
                return;
            }
            long max = 1;
            for (int i = 0; i < nanos.length; i++) {
                if (nanos[i] > max) {
                    max = nanos[i];
                }
            }
            int labelW = 110;
            int valueW = 80;
            int top = 44;
            int rowH = (h - top - 14) / NAMES.length;
            int barMax = w - labelW - valueW - 30;
            g.setFont(new Font("SansSerif", Font.PLAIN, 13));
            FontMetrics fm = g.getFontMetrics();
            for (int i = 0; i < NAMES.length; i++) {
                int y = top + i * rowH;
                g.setColor(Color.WHITE);
                g.drawString(NAMES[i], 14, y + rowH / 2 + fm.getAscent() / 2 - 2);
                int bw = (int) (nanos[i] * barMax / max);
                if (bw < 3) {
                    bw = 3;
                }
                g.setColor(rank[i] == 1 ? MainFrame.GREEN : MainFrame.BLUE);
                g.fillRect(labelW + 14, y + 4, bw, rowH - 8);
                g.setColor(Color.WHITE);
                g.drawString(String.format("%.3f", nanos[i] / 1000000.0), labelW + 14 + bw + 8, y + rowH / 2 + fm.getAscent() / 2 - 2);
            }
            g.dispose();
        }
    }
}
