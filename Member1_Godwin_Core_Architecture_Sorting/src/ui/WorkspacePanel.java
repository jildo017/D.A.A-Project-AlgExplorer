package ui;

import algorithms.RecursiveAlgorithms;
import algorithms.SearchingAlgorithms;
import algorithms.SortingAlgorithms;
import algorithms.StepListener;
import datastructures.CustomLinkedList;
import datastructures.CustomQueue;
import datastructures.CustomStack;
import utils.ArrayUtils;
import utils.ComplexityInfo;
import utils.ExecutionTracker;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

/** SCREEN 3: input, controls, live visualization, step log and statistics. */
public class WorkspacePanel extends JPanel {
    private static final int SORT = 0;
    private static final int STACK = 1;
    private static final int QUEUE = 2;
    private static final int LIST = 3;
    private static final int FACT = 4;
    private static final int FIB = 5;
    private static final int HANOI = 6;
    private static final int BSEARCH = 7;
    private static final int SEARCH = 8;

    private static final int MAX_ARRAY = 200;
    private static final int MAX_LOG_LINES = 3000;
    private static final Color FIELD_BG = new Color(55, 55, 78);

    /** Thrown inside the worker thread to stop an animation when the user presses Reset. */
    private static class StopRun extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private static class Snapshot {
        final int[] arr;
        final int h1;
        final int h2;
        final int boundary;
        final String msg;

        Snapshot(int[] arr, int h1, int h2, int boundary, String msg) {
            this.arr = arr;
            this.h1 = h1;
            this.h2 = h2;
            this.boundary = boundary;
            this.msg = msg;
        }
    }

    private static class Job {
        int[] arr;
        int n;
        int target;
    }

    private final MainFrame frame;
    private int type = SORT;
    private String name = "";

    private final JLabel nameLabel = new JLabel("");
    private final JLabel complexityLabel = new JLabel("");
    private final JLabel explainLabel = new JLabel("");

    private final JLabel inputLabel = new JLabel("Input:");
    private final JTextField inputField = new JTextField(26);
    private final JLabel targetLabel = new JLabel("Target:");
    private final JTextField targetField = new JTextField(5);
    private final JPanel opPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    private final JTextField sizeField = new JTextField("15", 3);
    private final JSlider speed = new JSlider(0, 1500, 500);
    private final JLabel speedLabel = new JLabel("500 ms (Study Pace)");
    private final JButton genBtn = MainFrame.makeButton("Generate Random (1-100)", MainFrame.BLUE);
    private final JButton runBtn = MainFrame.makeButton("Run", MainFrame.GREEN);
    private final JButton resetBtn = MainFrame.makeButton("Reset", MainFrame.RED);

    private final VisualizerPanel visualizer = new VisualizerPanel();
    private final JTextArea logArea = new JTextArea();
    private final JTextArea resultArea = new JTextArea(3, 20);
    private final JLabel opsLabel = new JLabel("Operations: 0");
    private final JLabel stepsLabel = new JLabel("Steps: 0");
    private final JLabel timeLabel = new JLabel("Running time: -");
    private final JSplitPane split;

    private ExecutionTracker tracker = new ExecutionTracker();
    private CustomStack stack = new CustomStack();
    private CustomQueue queue = new CustomQueue();
    private CustomLinkedList list = new CustomLinkedList();

    private boolean busy = false;
    private volatile int runId = 0;
    private volatile Snapshot lastSnapshot = null;

    public WorkspacePanel(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(0, 8));
        setBackground(MainFrame.BG);
        setBorder(new EmptyBorder(10, 14, 10, 14));

        add(buildTop(), BorderLayout.NORTH);

        JPanel right = buildRight();
        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, visualizer, right);
        split.setResizeWeight(0.6);
        split.setDividerLocation(740);
        split.setBorder(null);
        split.setBackground(MainFrame.BG);
        add(split, BorderLayout.CENTER);
    }

    // ================================================================== UI construction

    private static void styleLabel(JLabel l, int size, int style, Color c) {
        l.setFont(new Font("SansSerif", style, size));
        l.setForeground(c);
    }

    private static void styleField(JTextField f) {
        f.setBackground(FIELD_BG);
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 90, 120)), new EmptyBorder(4, 6, 4, 6)));
    }

    private JPanel buildTop() {
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new BorderLayout());
        row1.setOpaque(false);
        styleLabel(nameLabel, 30, Font.BOLD, Color.WHITE);
        row1.add(nameLabel, BorderLayout.WEST);
        JButton back = MainFrame.makeButton("Back to Menu", MainFrame.BLUE);
        back.addActionListener(e -> {
            runId++;
            setBusy(false);
            frame.showScreen("CATEGORIES");
        });
        row1.add(back, BorderLayout.EAST);

        styleLabel(complexityLabel, 16, Font.BOLD, new Color(134, 239, 172));
        styleLabel(explainLabel, 13, Font.PLAIN, MainFrame.MUTED);
        JPanel infoBox = new JPanel(new GridLayout(2, 1));
        infoBox.setOpaque(false);
        infoBox.add(complexityLabel);
        infoBox.add(explainLabel);
        infoBox.setBorder(new EmptyBorder(4, 0, 8, 0));

        // control bar
        JPanel bar = new JPanel();
        bar.setBackground(MainFrame.CARD);
        bar.setLayout(new BoxLayout(bar, BoxLayout.Y_AXIS));
        bar.setBorder(new EmptyBorder(8, 10, 8, 10));

        JPanel c1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        c1.setOpaque(false);
        styleLabel(inputLabel, 14, Font.BOLD, Color.WHITE);
        styleLabel(targetLabel, 14, Font.BOLD, Color.WHITE);
        styleField(inputField);
        styleField(targetField);
        styleField(sizeField);
        opPanel.setOpaque(false);
        c1.add(inputLabel);
        c1.add(inputField);
        c1.add(targetLabel);
        c1.add(targetField);
        c1.add(opPanel);
        c1.add(genBtn);
        JLabel sizeLabel = new JLabel("Size:");
        styleLabel(sizeLabel, 14, Font.BOLD, Color.WHITE);
        c1.add(sizeLabel);
        c1.add(sizeField);

        JPanel c2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        c2.setOpaque(false);
        JLabel speedTitle = new JLabel("Animation Speed:");
        styleLabel(speedTitle, 14, Font.BOLD, Color.WHITE);
        speed.setBackground(MainFrame.CARD);
        speed.setPreferredSize(new Dimension(220, 28));
        styleLabel(speedLabel, 13, Font.PLAIN, MainFrame.MUTED);
        speedLabel.setPreferredSize(new Dimension(140, 20));
        speed.addChangeListener(e -> {
            int val = speed.getValue();
            String note = val == 0 ? "0 ms (Instant)" : (val < 250 ? val + " ms (Fast)" : (val > 900 ? val + " ms (Slow-Mo)" : val + " ms (Study Pace)"));
            speedLabel.setText(note);
        });
        JLabel hint = new JLabel("0 = Instant, 500 = Study Pace, 1500 = Slow-Mo");
        styleLabel(hint, 12, Font.PLAIN, MainFrame.MUTED);
        c2.add(speedTitle);
        c2.add(speed);
        c2.add(speedLabel);
        c2.add(hint);
        c2.add(runBtn);
        c2.add(resetBtn);

        bar.add(c1);
        bar.add(c2);

        genBtn.addActionListener(e -> generateRandom());
        runBtn.addActionListener(e -> run());
        resetBtn.addActionListener(e -> reset());

        top.add(row1);
        top.add(infoBox);
        top.add(bar);
        return top;
    }

    private JPanel buildRight() {
        JPanel right = new JPanel(new BorderLayout(0, 8));
        right.setBackground(MainFrame.CARD);
        right.setBorder(new EmptyBorder(8, 10, 8, 10));

        JPanel stats = new JPanel(new GridLayout(3, 1, 0, 2));
        stats.setOpaque(false);
        styleLabel(opsLabel, 15, Font.BOLD, Color.WHITE);
        styleLabel(stepsLabel, 15, Font.BOLD, Color.WHITE);
        styleLabel(timeLabel, 15, Font.BOLD, Color.WHITE);
        stats.add(opsLabel);
        stats.add(stepsLabel);
        stats.add(timeLabel);
        right.add(stats, BorderLayout.NORTH);

        logArea.setEditable(false);
        logArea.setBackground(new Color(24, 24, 36));
        logArea.setForeground(new Color(220, 225, 240));
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(titled("Step-by-step log"));
        right.add(logScroll, BorderLayout.CENTER);

        resultArea.setEditable(false);
        resultArea.setBackground(new Color(24, 24, 36));
        resultArea.setForeground(new Color(134, 239, 172));
        resultArea.setFont(new Font("SansSerif", Font.BOLD, 14));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane resScroll = new JScrollPane(resultArea);
        resScroll.setBorder(titled("Result"));
        right.add(resScroll, BorderLayout.SOUTH);
        return right;
    }

    private static TitledBorder titled(String title) {
        TitledBorder tb = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(90, 90, 120)), title);
        tb.setTitleColor(Color.WHITE);
        return tb;
    }

    // ================================================================== configuration

    private static int typeOf(String item) {
        switch (item) {
            case "Custom Stack": return STACK;
            case "Custom Queue": return QUEUE;
            case "Custom Linked List": return LIST;
            case "Factorial": return FACT;
            case "Fibonacci": return FIB;
            case "Tower of Hanoi": return HANOI;
            case "Recursive Binary Search": return BSEARCH;
            case "Linear Search":
            case "Binary Search":
            case "Jump Search":
            case "Interpolation Search": return SEARCH;
            default: return SORT;
        }
    }

    private static String modeOf(int t) {
        switch (t) {
            case STACK: return "STACK";
            case QUEUE: return "QUEUE";
            case LIST: return "LIST";
            case FACT: return "FACT";
            case FIB: return "FIB";
            case HANOI: return "HANOI";
            case BSEARCH:
            case SEARCH: return "BSEARCH";
            default: return "SORT";
        }
    }

    private static boolean isSearch(int t) {
        return t == BSEARCH || t == SEARCH;
    }

    private static boolean isDataStructure(int t) {
        return t == STACK || t == QUEUE || t == LIST;
    }

    private String hintText() {
        switch (type) {
            case SORT: return "Enter comma-separated whole numbers (e.g. 12, 5, 88, 3) and press Run.\nMax " + MAX_ARRAY + " numbers.";
            case BSEARCH: return "Enter comma-separated numbers and a Target, then press Run.\nIf the list is unsorted, a sorted copy is made first.";
            case SEARCH: return "Enter comma-separated numbers and a Target, then press Run.\nBinary, Jump and Interpolation search sort a copy first if needed.";
            case FACT: return "Enter one integer from 0 to 20 and press Run.";
            case FIB: return "Enter one integer from 0 to 15 and press Run.";
            case HANOI: return "Enter the number of disks (1 to 6) and press Run.";
            case STACK: return "Type an integer and use Push / Pop / Peek.\nRun pushes every number in the box (comma-separated is fine).";
            case QUEUE: return "Type an integer and use Enqueue / Dequeue / Peek.\nRun enqueues every number in the box (comma-separated is fine).";
            default: return "Type an integer and use Insert End / Insert Start / Delete / Search.\nRun inserts every number in the box at the end.";
        }
    }

    /** Prepares the screen for the selected algorithm or data structure. */
    public void configure(String item) {
        runId++;
        name = item;
        type = typeOf(item);
        stack = new CustomStack();
        queue = new CustomQueue();
        list = new CustomLinkedList();
        tracker = new ExecutionTracker();

        nameLabel.setText(item);
        String[] c = ComplexityInfo.getInfo(item);
        complexityLabel.setText("Time:  Best " + c[ComplexityInfo.BEST] + "   |   Average " + c[ComplexityInfo.AVERAGE]
                + "   |   Worst " + c[ComplexityInfo.WORST] + "        Space: " + c[ComplexityInfo.SPACE]);
        explainLabel.setText(c[ComplexityInfo.EXPLANATION]);

        inputField.setText("");
        targetField.setText("");
        boolean search = isSearch(type);
        targetLabel.setVisible(search);
        targetField.setVisible(search);
        boolean multi = type == SORT || search || isDataStructure(type);
        inputLabel.setText(multi ? "Input:" : (type == HANOI ? "Disks:" : "n:"));
        buildOpButtons();

        visualizer.setMode(modeOf(type));
        visualizer.clear("Enter your input and press Run to start the visualization.");
        setBusy(false);
        clearOutputs();
        SwingUtilities.invokeLater(() -> split.setDividerLocation(0.6));
    }

    private void clearOutputs() {
        logArea.setText(hintText());
        resultArea.setText("");
        opsLabel.setText("Operations: 0");
        stepsLabel.setText("Steps: 0");
        timeLabel.setText("Running time: -");
    }

    private void buildOpButtons() {
        opPanel.removeAll();
        String[][] ops;
        if (type == STACK) {
            ops = new String[][] {{"Push", "push"}, {"Pop", "pop"}, {"Peek", "peek"}};
        } else if (type == QUEUE) {
            ops = new String[][] {{"Enqueue", "enqueue"}, {"Dequeue", "dequeue"}, {"Peek", "peek"}};
        } else if (type == LIST) {
            ops = new String[][] {{"Insert End", "insertEnd"}, {"Insert Start", "insertStart"}, {"Delete", "delete"}, {"Search", "search"}};
        } else {
            ops = new String[0][];
        }
        for (final String[] op : ops) {
            JButton b = MainFrame.makeButton(op[0], MainFrame.BLUE);
            b.addActionListener(e -> dataStructureOperation(op[1]));
            opPanel.add(b);
        }
        opPanel.revalidate();
        opPanel.repaint();
    }

    private void setBusy(boolean b) {
        busy = b;
        runBtn.setEnabled(!b);
        genBtn.setEnabled(!b);
        for (Component comp : opPanel.getComponents()) {
            comp.setEnabled(!b);
        }
    }

    // ================================================================== buttons

    private static String joinValues(int[] a) {
        String s = ArrayUtils.arrayToString(a);
        return s.substring(1, s.length() - 1);
    }

    private void generateRandom() {
        int size = 15;
        try {
            size = Integer.parseInt(sizeField.getText().trim());
        } catch (NumberFormatException e) {
            sizeField.setText("15");
        }
        if (size < 1) {
            size = 1;
        }
        if (size > MAX_ARRAY) {
            size = MAX_ARRAY;
            sizeField.setText(String.valueOf(MAX_ARRAY));
        }
        switch (type) {
            case SORT:
                inputField.setText(joinValues(ArrayUtils.generateRandomArray(size, 1, 100)));
                break;
            case BSEARCH:
            case SEARCH:
                int[] arr = ArrayUtils.generateRandomArray(size, 1, 100);
                inputField.setText(joinValues(arr));
                int pick = ArrayUtils.generateRandomArray(1, 0, size - 1)[0];
                targetField.setText(String.valueOf(arr[pick]));
                break;
            case FACT:
                inputField.setText(String.valueOf(ArrayUtils.generateRandomArray(1, 1, 20)[0]));
                break;
            case FIB:
                inputField.setText(String.valueOf(ArrayUtils.generateRandomArray(1, 1, 15)[0]));
                break;
            case HANOI:
                inputField.setText(String.valueOf(ArrayUtils.generateRandomArray(1, 1, 6)[0]));
                break;
            default:
                inputField.setText(joinValues(ArrayUtils.generateRandomArray(Math.min(size, 12), 1, 100)));
                break;
        }
    }

    private void reset() {
        runId++;
        setBusy(false);
        stack = new CustomStack();
        queue = new CustomQueue();
        list = new CustomLinkedList();
        tracker = new ExecutionTracker();
        lastSnapshot = null;
        visualizer.setMode(modeOf(type));
        visualizer.clear("Reset. Enter your input and press Run.");
        clearOutputs();
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Please check your input", JOptionPane.WARNING_MESSAGE);
    }

    // ================================================================== input parsing

    private Job parseJob() {
        Job job = new Job();
        try {
            switch (type) {
                case SORT:
                    job.arr = ArrayUtils.parseInput(inputField.getText());
                    if (job.arr.length > MAX_ARRAY) {
                        warn("The visualizer accepts at most " + MAX_ARRAY + " numbers. Use the Benchmark screen for larger arrays.");
                        return null;
                    }
                    break;
                case BSEARCH:
                case SEARCH:
                    job.arr = ArrayUtils.parseInput(inputField.getText());
                    if (job.arr.length > MAX_ARRAY) {
                        warn("The visualizer accepts at most " + MAX_ARRAY + " numbers.");
                        return null;
                    }
                    job.target = Integer.parseInt(targetField.getText().trim());
                    break;
                case FACT:
                case FIB:
                case HANOI:
                    job.n = Integer.parseInt(inputField.getText().trim());
                    break;
                default:
                    job.arr = ArrayUtils.parseInput(inputField.getText());
                    break;
            }
        } catch (NumberFormatException ex) {
            warn("Invalid input: " + ex.getMessage() + "\nPlease enter whole numbers only"
                    + (type == SORT || isSearch(type) ? ", separated by commas (e.g. 12, 5, 88, 3)." : "."));
            return null;
        }
        return job;
    }

    // ================================================================== running (SwingWorker)

    private String executeJob(Job job, int ty, String nm, ExecutionTracker t, StepListener l) {
        switch (ty) {
            case SORT: {
                int[] work = ArrayUtils.copyArray(job.arr);
                SortingAlgorithms.run(nm, work, t, l);
                return "Sorted array: " + ArrayUtils.arrayToString(work);
            }
            case FACT: {
                long r = RecursiveAlgorithms.factorial(job.n, t, l);
                return "factorial(" + job.n + ") = " + r;
            }
            case FIB: {
                long r = RecursiveAlgorithms.fibonacci(job.n, t, l);
                return "fibonacci(" + job.n + ") = " + r;
            }
            case HANOI: {
                RecursiveAlgorithms.towerOfHanoi(job.n, 'A', 'C', 'B', t, l);
                return "Solved " + job.n + " disk(s) in " + ((1 << job.n) - 1) + " moves (peg A to peg C)";
            }
            case BSEARCH: {
                int idx = RecursiveAlgorithms.recursiveBinarySearch(job.arr, job.target, 0, job.arr.length - 1, t, l);
                return idx >= 0 ? "Target " + job.target + " found at index " + idx + " of the sorted array"
                        : "Target " + job.target + " was not found";
            }
            case SEARCH: {
                int idx = SearchingAlgorithms.run(nm, job.arr, job.target, t, l);
                String where = nm.equals("Linear Search") ? "" : " of the sorted array";
                return idx >= 0 ? "Target " + job.target + " found at index " + idx + where
                        : "Target " + job.target + " was not found";
            }
            case STACK: {
                for (int i = 0; i < job.arr.length; i++) {
                    stack.push(job.arr[i], t);
                }
                return "Pushed " + job.arr.length + " value(s). Stack (top to bottom): " + ArrayUtils.arrayToString(stack.toArray());
            }
            case QUEUE: {
                for (int i = 0; i < job.arr.length; i++) {
                    queue.enqueue(job.arr[i], t);
                }
                return "Enqueued " + job.arr.length + " value(s). Queue (front to rear): " + ArrayUtils.arrayToString(queue.toArray());
            }
            default: {
                for (int i = 0; i < job.arr.length; i++) {
                    list.insertEnd(job.arr[i], t);
                }
                return "Inserted " + job.arr.length + " value(s). List: " + ArrayUtils.arrayToString(list.toArray());
            }
        }
    }

    private StepListener makeListener(final int delay, final int myRun, final ExecutionTracker t) {
        return new StepListener() {
            @Override
            public void onStep(int[] arr, int h1, int h2, int boundary, String msg) {
                if (myRun != runId) {
                    throw new StopRun();
                }
                final Snapshot snap = new Snapshot(ArrayUtils.copyArray(arr), h1, h2, boundary, msg);
                lastSnapshot = snap;
                if (delay > 0) {
                    final long ops = t.getOperationCount();
                    final int steps = t.getStepCount();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (myRun != runId) {
                                return;
                            }
                            visualizer.setFrame(snap.arr, snap.h1, snap.h2, snap.boundary, snap.msg);
                            opsLabel.setText("Operations: " + ops);
                            stepsLabel.setText("Steps: " + steps);
                        }
                    });
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        throw new StopRun();
                    }
                }
            }
        };
    }

    private void run() {
        if (busy) {
            return;
        }
        final Job job = parseJob();
        if (job == null) {
            return;
        }
        final int delay = speed.getValue();
        final int myRun = ++runId;
        final int myType = type;
        final String myName = name;
        final ExecutionTracker t = new ExecutionTracker();
        tracker = t;
        lastSnapshot = null;
        visualizer.setMode(modeOf(myType));
        logArea.setText("Running...");
        resultArea.setText("");
        setBusy(true);

        new SwingWorker<String, Void>() {
            private long quietNano = 0;

            @Override
            protected String doInBackground() {
                StepListener listener = isDataStructure(myType) ? null : makeListener(delay, myRun, t);
                t.startTimer();
                String result = executeJob(job, myType, myName, t, listener);
                t.stopTimer();
                if (!isDataStructure(myType)) {
                    // Second silent run (no animation, no logging) gives the true running time.
                    ExecutionTracker quiet = new ExecutionTracker();
                    quiet.setLogging(false);
                    quiet.startTimer();
                    executeJob(job, myType, myName, quiet, null);
                    quiet.stopTimer();
                    quietNano = quiet.getElapsedNano();
                }
                return result;
            }

            @Override
            protected void done() {
                if (myRun != runId) {
                    return;
                }
                setBusy(false);
                try {
                    String result = get();
                    long nano = isDataStructure(myType) ? t.getElapsedNano() : quietNano;
                    showFinished(result, t, nano, myType);
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    String msg = cause.getMessage() != null ? cause.getMessage() : cause.toString();
                    logArea.setText("Run failed: " + msg);
                    resultArea.setText("Error: " + msg);
                    if (cause instanceof IllegalArgumentException || cause instanceof IllegalStateException) {
                        warn(msg);
                    }
                }
            }
        }.execute();
    }

    private void showFinished(String result, ExecutionTracker t, long nano, int ty) {
        Snapshot s = lastSnapshot;
        if (isDataStructure(ty)) {
            showStructure(-1, "");
        } else if (s != null) {
            visualizer.setFrame(s.arr, s.h1, s.h2, s.boundary, s.msg);
        }
        resultArea.setText(result);
        updateStats(t, nano);
        fillLog(t);
    }

    private void updateStats(ExecutionTracker t, long nano) {
        opsLabel.setText("Operations: " + t.getOperationCount());
        stepsLabel.setText("Steps: " + t.getStepCount());
        timeLabel.setText("Running time: " + String.format("%.4f", nano / 1000000.0) + " ms  (" + nano + " ns)");
    }

    private void fillLog(ExecutionTracker t) {
        String[] steps = t.getSteps();
        StringBuilder sb = new StringBuilder();
        int shown = Math.min(steps.length, MAX_LOG_LINES);
        for (int i = 0; i < shown; i++) {
            sb.append(i + 1).append(". ").append(steps[i]).append('\n');
        }
        if (steps.length > shown) {
            sb.append("... ").append(steps.length - shown).append(" more steps not shown");
        }
        logArea.setText(sb.toString());
        logArea.setCaretPosition(0);
    }

    // ================================================================== data structure buttons

    private void showStructure(int highlight, String msg) {
        int[] values;
        if (type == STACK) {
            values = stack.toArray();
        } else if (type == QUEUE) {
            values = queue.toArray();
        } else {
            values = list.toArray();
        }
        visualizer.setMode(modeOf(type));
        visualizer.setFrame(values, highlight, -1, -1, msg.length() == 0 ? "Size: " + values.length : msg);
    }

    private void dataStructureOperation(String op) {
        if (busy) {
            return;
        }
        int value = 0;
        boolean needsValue = !(op.equals("pop") || op.equals("peek") || op.equals("dequeue"));
        if (op.equals("peek")) {
            needsValue = false;
        }
        if (needsValue) {
            try {
                value = ArrayUtils.parseInput(inputField.getText())[0];
            } catch (NumberFormatException ex) {
                warn("Please type a whole number in the input box first.");
                return;
            }
        }
        ExecutionTracker t = new ExecutionTracker();
        tracker = t;
        String result = "";
        int highlight = -1;
        t.startTimer();
        try {
            switch (op) {
                case "push":
                    stack.push(value, t);
                    result = "Pushed " + value;
                    highlight = 0;
                    break;
                case "pop":
                    result = "Popped " + stack.pop(t);
                    break;
                case "enqueue":
                    queue.enqueue(value, t);
                    result = "Enqueued " + value;
                    highlight = queue.toArray().length - 1;
                    break;
                case "dequeue":
                    result = "Dequeued " + queue.dequeue(t);
                    break;
                case "insertEnd":
                    list.insertEnd(value, t);
                    result = "Inserted " + value + " at the end";
                    highlight = list.toArray().length - 1;
                    break;
                case "insertStart":
                    list.insertBeginning(value, t);
                    result = "Inserted " + value + " at the start";
                    highlight = 0;
                    break;
                case "delete":
                    result = list.deleteValue(value, t) ? "Deleted " + value : value + " was not found, nothing deleted";
                    break;
                case "search": {
                    int idx = list.search(value, t);
                    result = idx >= 0 ? value + " found at index " + idx : value + " was not found";
                    highlight = idx;
                    break;
                }
                case "peek":
                    result = "Peek: " + (type == STACK ? stack.peek(t) : queue.peek(t));
                    highlight = 0;
                    break;
                default:
                    break;
            }
        } catch (IllegalStateException ex) {
            result = "Error: " + ex.getMessage();
        }
        t.stopTimer();
        showStructure(highlight, result);
        resultArea.setText(result);
        updateStats(t, t.getElapsedNano());
        fillLog(t);
    }
}
