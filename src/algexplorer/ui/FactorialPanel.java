package algexplorer.ui;

import algexplorer.core.Result;
import algexplorer.recursion.Factorial;
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
 * Factorial screen. It only reads input, calls the back end and shows the
 * results.
 */
public class FactorialPanel extends JPanel {
    private final JTextField inputField = new JTextField(8);
    private final JLabel statusLabel = new JLabel(" ");
    private final JTextArea output = new JTextArea();

    public FactorialPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputRow = new JPanel();
        inputRow.add(new JLabel("Enter n:"));
        inputRow.add(inputField);
        JButton runButton = new JButton("Compute");
        inputRow.add(runButton);

        JLabel note = new JLabel("<html>n! means 1 x 2 x 3 x ... x n, and 0! = 1. "
                + "Negative numbers are not allowed. The largest n whose answer fits in a "
                + "long is " + Factorial.MAX_N + ".</html>");

        JPanel north = new JPanel(new BorderLayout());
        north.add(inputRow, BorderLayout.NORTH);
        north.add(note, BorderLayout.CENTER);
        north.add(statusLabel, BorderLayout.SOUTH);

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);

        runButton.addActionListener(e -> compute());
        inputField.addActionListener(e -> compute());
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
            showError("\"" + text + "\" is not a whole number. Try something like 5.");
            return;
        }

        try {
            Result recursive = Factorial.runRecursive(n);
            Result iterative = Factorial.runIterative(n);

            StringBuilder sb = new StringBuilder();
            sb.append(Factorial.describe(n)).append("\n\n");
            sb.append(String.format("%-12s %-28s %-14s %s%n",
                    "Method", "Result", "Calls/Loops", "Time"));
            sb.append(row("Recursive", recursive));
            sb.append(row("Iterative", iterative));
            sb.append("\nRecursive counts calls (n + 1, including the base case 0!).\n");
            sb.append("Iterative counts loop steps (n).\n");

            output.setText(sb.toString());
            statusLabel.setText(" ");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private String row(String name, Result r) {
        return String.format("%-12s %-28s %-14s %.4f ms%n", name,
                String.format("%,d", r.value),
                String.format("%,d", r.operations),
                r.nanos / 1_000_000.0);
    }

    private void showError(String message) {
        statusLabel.setForeground(Color.RED);
        statusLabel.setText(message);
        output.setText("");
    }
}