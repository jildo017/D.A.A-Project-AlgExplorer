package algexplorer.ui;

import algexplorer.core.Result;
import algexplorer.recursion.Fibonacci;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Fibonacci screen. It only reads input, calls the back end and shows the
 * results.
 */
public class FibonacciPanel extends JPanel {
    private final JTextField inputField = new JTextField(8);
    private final JLabel statusLabel = new JLabel(" ");
    private final JTextArea output = new JTextArea();

    public FibonacciPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputRow = new JPanel();
        inputRow.add(new JLabel("Enter n:"));
        inputRow.add(inputField);
        JButton runButton = new JButton("Compute");
        inputRow.add(runButton);

        JLabel note = new JLabel("<html>Naive recursion is limited to n &lt;= 30. Its calls "
                + "multiply by about 1.6 for every +1 in n, so a bigger n would freeze the "
                + "program. Memoized and bottom-up work up to n = 92 (after that the answer "
                + "no longer fits in a long).</html>");

        JPanel north = new JPanel(new BorderLayout());
        north.add(inputRow, BorderLayout.NORTH);
        north.add(note, BorderLayout.CENTER);
        north.add(statusLabel, BorderLayout.SOUTH);

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);

        runButton.addActionListener(e -> compute());
        inputField.addActionListener(e -> compute()); // pressing Enter also works
    }

    /** Reads and validates the input, calls the back end, shows the results. */
    private void compute() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) {
            showError("Please type a value for n.");
            return;
        }

        int n;
        try {
            n = Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            showError("\"" + text + "\" is not a whole number. Try something like 10.");
            return;
        }

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Fibonacci(").append(n).append(")\n\n");
            sb.append(String.format("%-12s %-24s %-14s %s%n",
                    "Method", "Result", "Calls/Loops", "Time"));

            if (n <= Fibonacci.MAX_NAIVE) {
                sb.append(row("Naive", Fibonacci.runNaive(n)));
            } else {
                sb.append(String.format("%-12s %s%n", "Naive", "skipped (n is above 30)"));
            }
            sb.append(row("Memoized", Fibonacci.runMemo(n)));
            sb.append(row("Bottom-up", Fibonacci.runBottomUp(n)));

            output.setText(sb.toString());
            statusLabel.setText(" "); // clear any old error
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage()); // the back end's own message
        }
    }

    /** Formats one line of the results table. */
    private String row(String name, Result r) {
        return String.format("%-12s %-24s %-14s %.4f ms%n", name,
                String.format("%,d", r.value),
                String.format("%,d", r.operations),
                r.nanos / 1_000_000.0);
    }

    private void showError(String message) {
        statusLabel.setForeground(Color.RED);
        statusLabel.setText(message);
        output.setText(""); // remove old results so they can't mislead
    }
}