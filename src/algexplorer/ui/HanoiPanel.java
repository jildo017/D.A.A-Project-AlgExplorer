package algexplorer.ui;

import algexplorer.recursion.Hanoi;
import algexplorer.recursion.HanoiMove;
import algexplorer.recursion.HanoiPlayer;
import algexplorer.recursion.HanoiResult;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 * Tower of Hanoi screen: input, list of moves, drawing and animation controls.
 */
public class HanoiPanel extends JPanel {
    private static final Color GREEN = new Color(0, 128, 0);

    private final JTextField disksField = new JTextField(4);
    private final JComboBox<String> sourceBox = new JComboBox<>(new String[] { "A", "B", "C" });
    private final JComboBox<String> destBox = new JComboBox<>(new String[] { "A", "B", "C" });
    private final JComboBox<String> auxBox = new JComboBox<>(new String[] { "A", "B", "C" });
    private final JLabel summaryLabel = new JLabel(" ");
    private final JLabel counterLabel = new JLabel(" ");
    private final JLabel statusLabel = new JLabel(" ");
    private final JTextArea movesArea = new JTextArea();

    private final HanoiBoard board = new HanoiBoard();
    private final JButton startButton = new JButton("Start");
    private final JButton pauseButton = new JButton("Pause");
    private final JButton stepButton = new JButton("Step");
    private final JButton resetButton = new JButton("Reset");
    private final JSlider speedSlider = new JSlider(1, 10, 5);
    private final Timer timer; // the alarm clock

    private HanoiPlayer player; // null until a puzzle is solved
    private int lastN;
    private char lastDestination;

    public HanoiPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        timer = new Timer(delayFor(speedSlider.getValue()), e -> advance());
        timer.setInitialDelay(0); // first move happens straight away

        sourceBox.setSelectedItem("A");
        destBox.setSelectedItem("C");
        auxBox.setSelectedItem("B");

        JPanel inputRow = new JPanel();
        inputRow.add(new JLabel("Disks (1-" + Hanoi.MAX_DISKS + "):"));
        inputRow.add(disksField);
        inputRow.add(new JLabel("Source:"));
        inputRow.add(sourceBox);
        inputRow.add(new JLabel("Destination:"));
        inputRow.add(destBox);
        inputRow.add(new JLabel("Auxiliary:"));
        inputRow.add(auxBox);
        JButton solveButton = new JButton("Solve");
        inputRow.add(solveButton);

        JLabel note = new JLabel("<html>Move the whole pile from the source peg to the "
                + "destination peg. One disk at a time, never a bigger disk on a smaller "
                + "one. The auxiliary peg is the spare.</html>");

        JPanel controlsRow = new JPanel();
        controlsRow.add(startButton);
        controlsRow.add(pauseButton);
        controlsRow.add(stepButton);
        controlsRow.add(resetButton);
        controlsRow.add(new JLabel("      Speed (slow to fast):"));
        controlsRow.add(speedSlider);

        JPanel messages = new JPanel(new GridLayout(4, 1));
        messages.add(controlsRow);
        messages.add(counterLabel);
        messages.add(summaryLabel);
        messages.add(statusLabel);

        JPanel north = new JPanel(new BorderLayout());
        north.add(inputRow, BorderLayout.NORTH);
        north.add(note, BorderLayout.CENTER);
        north.add(messages, BorderLayout.SOUTH);

        movesArea.setEditable(false);
        movesArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                board, new JScrollPane(movesArea));
        split.setResizeWeight(0.6);

        add(north, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        solveButton.addActionListener(e -> solve());
        disksField.addActionListener(e -> solve());
        startButton.addActionListener(e -> onStart());
        pauseButton.addActionListener(e -> onPause());
        stepButton.addActionListener(e -> onStep());
        resetButton.addActionListener(e -> onReset());
        speedSlider.addChangeListener(e -> timer.setDelay(delayFor(speedSlider.getValue())));

        updateButtons();
    }

    /**
     * Converts the slider (1 slow to 10 fast) into a pause between moves, in
     * milliseconds.
     */
    private static int delayFor(int speed) {
        return 1050 - 100 * speed; // speed 1 = 950 ms, speed 10 = 50 ms
    }

    /** Reads and validates the input, calls the solver, prepares the animation. */
    private void solve() {
        timer.stop(); // stop any old animation first

        String text = disksField.getText().trim();
        if (text.isEmpty()) {
            showError("Please type the number of disks (1 to " + Hanoi.MAX_DISKS + ").");
            return;
        }

        int n;
        try {
            n = Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            showError("\"" + text + "\" is not a whole number. Try something like 3.");
            return;
        }

        char source = ((String) sourceBox.getSelectedItem()).charAt(0);
        char destination = ((String) destBox.getSelectedItem()).charAt(0);
        char auxiliary = ((String) auxBox.getSelectedItem()).charAt(0);

        try {
            HanoiResult result = Hanoi.solve(n, source, destination, auxiliary);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < result.moves.length; i++) {
                sb.append(i + 1).append(". ").append(result.moves[i]).append("\n");
            }
            movesArea.setText(sb.toString());
            movesArea.setCaretPosition(0);

            long expected = (1L << n) - 1;
            boolean ok = result.moves.length == expected;
            summaryLabel.setForeground(ok ? GREEN : Color.RED);
            summaryLabel.setText("Total moves = " + result.moves.length
                    + "   Expected 2^" + n + " - 1 = " + expected
                    + "   " + (ok ? "Confirmed" : "MISMATCH")
                    + "   Calls = " + result.operations
                    + "   Time = " + String.format("%.4f", result.nanos / 1_000_000.0) + " ms");
            statusLabel.setText(" ");

            lastN = n;
            lastDestination = destination;
            player = new HanoiPlayer(n, source, destination, result.moves);
            refresh();
            updateButtons();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    // ---------- the four controls ----------

    private void onStart() {
        if (player == null) {
            return;
        }
        if (player.isFinished()) { // Start after the end = play again
            player.reset();
            statusLabel.setText(" ");
            refresh();
        }
        timer.start();
        updateButtons();
    }

    private void onPause() {
        timer.stop();
        updateButtons();
    }

    private void onStep() {
        timer.stop();
        advance();
    }

    private void onReset() {
        timer.stop();
        if (player != null) {
            player.reset();
            statusLabel.setText(" ");
            refresh();
        }
        updateButtons();
    }

    /** Plays one move. Called by the timer and by the Step button. */
    private void advance() {
        if (player == null) {
            return;
        }
        if (player.step()) {
            refresh();
        }
        if (player.isFinished()) {
            timer.stop();
            showFinished();
        }
        updateButtons();
    }

    // ---------- showing things ----------

    /** Redraws the board and the move counter from the player. */
    private void refresh() {
        HanoiMove last = player.getLastMove();
        board.setState(player.getState(), last == null ? 0 : last.disk);
        counterLabel.setForeground(Color.BLACK);
        counterLabel.setText("Moves made: " + player.getDone() + " of " + player.getTotal()
                + (last == null ? "" : "      Last move: " + last));
    }

    /**
     * The final check: moves made must equal 2^n - 1 and every disk must be on the
     * target.
     */
    private void showFinished() {
        long expected = (1L << lastN) - 1;
        boolean ok = player.getDone() == expected && player.isAtDestination();
        statusLabel.setForeground(ok ? GREEN : Color.RED);
        statusLabel.setText(ok
                ? "Finished! " + player.getDone() + " moves made = 2^" + lastN + " - 1 = "
                        + expected + ". All " + lastN + " disks are on peg " + lastDestination + "."
                : "Something is wrong: " + player.getDone() + " moves made, expected "
                        + expected + ".");
    }

    /** Switches the buttons on or off to match what is possible right now. */
    private void updateButtons() {
        boolean ready = player != null;
        boolean running = timer.isRunning();
        boolean finished = ready && player.isFinished();
        startButton.setEnabled(ready && !running);
        pauseButton.setEnabled(running);
        stepButton.setEnabled(ready && !running && !finished);
        resetButton.setEnabled(ready);
    }

    private void showError(String message) {
        timer.stop();
        player = null;
        statusLabel.setForeground(Color.RED);
        statusLabel.setText(message);
        summaryLabel.setText(" ");
        counterLabel.setText(" ");
        movesArea.setText("");
        board.setState(null);
        updateButtons();
    }
}