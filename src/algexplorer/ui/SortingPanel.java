package algexplorer.ui;

import algexplorer.sorting.ArrayTools;
import algexplorer.sorting.SortCatalog;
import algexplorer.sorting.SortResult;
import algexplorer.sorting.SortStep;
import algexplorer.sorting.Sorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

/** Sorting Lab screen: step-by-step view, Auto-play, counts, Compare all and the complexity help panel. */
public class SortingPanel extends JPanel {
    private static final int MAX_NUMBERS = 1000;
    private static final int MAX_VALUE = 1_000_000;

    private final Sorter[] sorters = SortCatalog.all();
    private final JTextField listField = new JTextField(34);
    private final JTextField countField = new JTextField("15", 4);
    private final JComboBox<String> algoBox = new JComboBox<>();
    private final BarBoard board = new BarBoard();
    private final JLabel messageLabel = new JLabel(" ");
    private final JLabel stepLabel = new JLabel(" ");
    private final JLabel countsLabel = new JLabel(" ");
    private final JButton prevButton = new JButton("< Prev");
    private final JButton nextButton = new JButton("Step >");
    private final JButton playButton = new JButton("Auto-play");
    private final JSlider speedSlider = new JSlider(1, 10, 5);
    private final DefaultTableModel compareModel = new DefaultTableModel(
            new String[] { "Algorithm", "Comparisons", "Swaps / moves", "Time (ms)", "Sorted?" }, 0);
    private final Timer timer;

    private SortResult result;
    private int stepIndex;

    public SortingPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < sorters.length; i++) {
            algoBox.addItem(sorters[i].name());
        }

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Numbers (comma or space separated):"));
        row1.add(listField);
        row1.add(new JLabel("or n ="));
        row1.add(countField);
        JButton randomButton = new JButton("Generate random");
        row1.add(randomButton);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Algorithm:"));
        row2.add(algoBox);
        JButton sortButton = new JButton("Sort");
        row2.add(sortButton);
        JButton compareButton = new JButton("Compare all");
        row2.add(compareButton);
        row2.add(Box.createHorizontalStrut(12));
        row2.add(prevButton);
        row2.add(nextButton);
        row2.add(playButton);
        row2.add(new JLabel("Speed:"));
        row2.add(speedSlider);

        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(row1);
        north.add(row2);
        north.add(messageLabel);

        JPanel info = new JPanel(new BorderLayout());
        info.add(stepLabel, BorderLayout.NORTH);
        info.add(countsLabel, BorderLayout.SOUTH);
        stepLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        countsLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JPanel center = new JPanel(new BorderLayout(4, 4));
        center.add(board, BorderLayout.CENTER);
        center.add(info, BorderLayout.SOUTH);

        JTable compareTable = new JTable(compareModel);
        compareTable.setEnabled(false);
        JTable helpTable = new JTable(new DefaultTableModel(SortCatalog.complexityRows(),
                new String[] { "Algorithm", "Best", "Average", "Worst", "Extra space", "Stable" }));
        helpTable.setEnabled(false);
        helpTable.getColumnModel().getColumn(0).setPreferredWidth(170);
        helpTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Compare all", new JScrollPane(compareTable));
        tabs.addTab("Complexity help", new JScrollPane(helpTable));
        tabs.setPreferredSize(new java.awt.Dimension(700, 165));

        add(north, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(tabs, BorderLayout.SOUTH);

        timer = new Timer(600, e -> autoStep());
        setButtonsEnabled(false);

        randomButton.addActionListener(e -> generateRandom());
        sortButton.addActionListener(e -> runSort());
        listField.addActionListener(e -> runSort());
        compareButton.addActionListener(e -> compareAll(tabs));
        prevButton.addActionListener(e -> {
            stopPlaying();
            move(-1);
        });
        nextButton.addActionListener(e -> {
            stopPlaying();
            move(1);
        });
        playButton.addActionListener(e -> togglePlay());
        speedSlider.addChangeListener(e -> timer.setDelay(1100 - speedSlider.getValue() * 100));
    }

    // ---------- input ----------

    /** Fills the text box with n random numbers (0 to 99 for small n, 0 to 999 otherwise). */
    private void generateRandom() {
        try {
            int n = Inputs.readInt(countField.getText(), "n", 1, MAX_NUMBERS);
            Random rnd = new Random();
            int range = n <= 30 ? 100 : 1000;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(rnd.nextInt(range));
            }
            listField.setText(sb.toString());
            showMessage("Generated " + n + " random numbers. Press Sort.", false);
        } catch (IllegalArgumentException ex) {
            showMessage(ex.getMessage(), true);
        }
    }

    /** Reads the list from the text box or throws a friendly message. Time: O(length of text). */
    private int[] readList() {
        String text = listField.getText().trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("The list is empty. Type some numbers or press Generate random.");
        }
        String[] parts = text.split("[,\\s]+");
        if (parts.length > MAX_NUMBERS) {
            throw new IllegalArgumentException("Too many numbers (" + parts.length + "). The limit is " + MAX_NUMBERS + ".");
        }
        int[] values = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                values[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("\"" + parts[i] + "\" is not a whole number.");
            }
            if (values[i] < -MAX_VALUE || values[i] > MAX_VALUE) {
                throw new IllegalArgumentException(parts[i] + " is out of range (use " + -MAX_VALUE + " to " + MAX_VALUE + ").");
            }
        }
        return values;
    }

    // ---------- sorting one algorithm ----------

    private void runSort() {
        stopPlaying();
        int[] data;
        try {
            data = readList();
        } catch (IllegalArgumentException ex) {
            showMessage(ex.getMessage(), true);
            return;
        }
        Sorter sorter = sorters[algoBox.getSelectedIndex()];
        result = sorter.sort(data, true);
        stepIndex = 0;
        setButtonsEnabled(result.steps.length > 0);
        showStep();
        countsLabel.setText(String.format("%s: comparisons = %,d   swaps/moves = %,d   time = %.4f ms",
                result.algorithm, result.comparisons, result.moves, result.nanos / 1_000_000.0));
        showMessage(result.stepsCut ? "Only the first " + result.steps.length
                + " steps are shown (the counts and time are for the whole sort)." : " ", false);
    }

    private void showStep() {
        if (result == null || result.steps.length == 0) {
            return;
        }
        SortStep s = result.steps[stepIndex];
        boolean last = stepIndex == result.steps.length - 1 && !result.stepsCut;
        board.setStep(s, last);
        stepLabel.setText("Step " + stepIndex + " of " + (result.steps.length - 1) + ": " + s.description);
        prevButton.setEnabled(stepIndex > 0);
        nextButton.setEnabled(stepIndex < result.steps.length - 1);
    }

    private void move(int delta) {
        int target = stepIndex + delta;
        if (result != null && target >= 0 && target < result.steps.length) {
            stepIndex = target;
            showStep();
        }
    }

    // ---------- auto-play ----------

    private void togglePlay() {
        if (timer.isRunning()) {
            stopPlaying();
            return;
        }
        if (result == null) {
            return;
        }
        if (stepIndex >= result.steps.length - 1) {
            stepIndex = 0;
            showStep();
        }
        timer.setDelay(1100 - speedSlider.getValue() * 100);
        timer.start();
        playButton.setText("Pause");
    }

    private void autoStep() {
        if (result == null || stepIndex >= result.steps.length - 1) {
            stopPlaying();
            return;
        }
        move(1);
        if (stepIndex >= result.steps.length - 1) {
            stopPlaying();
        }
    }

    private void stopPlaying() {
        timer.stop();
        playButton.setText("Auto-play");
    }

    private void setButtonsEnabled(boolean on) {
        prevButton.setEnabled(on);
        nextButton.setEnabled(on);
        playButton.setEnabled(on);
    }

    // ---------- compare all ----------

    private void compareAll(JTabbedPane tabs) {
        int[] data;
        try {
            data = readList();
        } catch (IllegalArgumentException ex) {
            showMessage(ex.getMessage(), true);
            return;
        }
        compareModel.setRowCount(0);
        for (int i = 0; i < sorters.length; i++) {
            SortResult r = sorters[i].sort(data, false);
            compareModel.addRow(new Object[] { r.algorithm, String.format("%,d", r.comparisons),
                    String.format("%,d", r.moves), String.format("%.4f", r.nanos / 1_000_000.0),
                    ArrayTools.isSorted(r.sorted) ? "Yes" : "NO" });
        }
        tabs.setSelectedIndex(0);
        showMessage("Compared all algorithms on the same " + data.length + " numbers.", false);
    }

    private void showMessage(String text, boolean error) {
        messageLabel.setForeground(error ? Color.RED : new Color(30, 110, 50));
        messageLabel.setText(text);
    }
}
