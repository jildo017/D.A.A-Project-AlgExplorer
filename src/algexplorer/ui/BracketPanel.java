package algexplorer.ui;

import algexplorer.structures.BracketChecker;
import algexplorer.structures.BracketResult;
import algexplorer.structures.BracketStep;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Bracket checker screen: type an expression, see the verdict, and the stack
 * after every symbol.
 */
public class BracketPanel extends JPanel {
    private static final Color GREEN = new Color(0, 128, 0);
    private static final String[] COLUMNS = { "Step", "Position", "Symbol", "What happened",
            "Stack after (bottom to top)" };

    private final JTextField expressionField = new JTextField(30);
    private final JButton checkButton = new JButton("Check");
    private final JButton previousButton = new JButton("Previous step");
    private final JButton nextButton = new JButton("Next step");
    private final JLabel verdictLabel = new JLabel(" ");
    private final JLabel messageLabel = new JLabel(" ");
    private final JLabel infoLabel = new JLabel(" ");
    private final JLabel stepLabel = new JLabel(" ");
    private final DefaultTableModel model = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final ArrayBoard board = new ArrayBoard();

    private BracketResult result; // null until the user presses Check
    private int depth = 1; // how many boxes to draw: the deepest the stack got

    public BracketPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputRow = new JPanel();
        inputRow.add(new JLabel("Expression:"));
        inputRow.add(expressionField);
        inputRow.add(checkButton);

        JPanel exampleRow = new JPanel();
        exampleRow.add(new JLabel("Examples:"));
        exampleRow.add(exampleButton("Balanced", "{ ( [ ] ) }"));
        exampleRow.add(exampleButton("Wrong order", "( [ ) ]"));
        exampleRow.add(exampleButton("Never closed", "((("));
        exampleRow.add(exampleButton("Extra closing", "())"));
        exampleRow.add(exampleButton("With letters", "a + (b * [c - d]) / {e}"));

        verdictLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

        JPanel rows = new JPanel(new GridLayout(0, 1));
        rows.add(inputRow);
        rows.add(exampleRow);
        rows.add(verdictLabel);
        rows.add(messageLabel);
        rows.add(infoLabel);

        table.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(45);
        table.getColumnModel().getColumn(1).setPreferredWidth(70);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(330);
        table.getColumnModel().getColumn(4).setPreferredWidth(200);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showStep(table.getSelectedRow());
            }
        });

        JPanel stepRow = new JPanel();
        stepRow.add(previousButton);
        stepRow.add(nextButton);
        stepRow.add(stepLabel);

        JPanel lower = new JPanel(new BorderLayout());
        lower.add(stepRow, BorderLayout.NORTH);
        lower.add(board, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(table), lower);
        split.setResizeWeight(0.45);

        add(rows, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        checkButton.addActionListener(e -> check());
        expressionField.addActionListener(e -> check());
        previousButton.addActionListener(e -> moveSelection(-1));
        nextButton.addActionListener(e -> moveSelection(1));
        previousButton.setEnabled(false);
        nextButton.setEnabled(false);
    }

    /** A button that fills the box with an example and checks it straight away. */
    private JButton exampleButton(String name, String expression) {
        JButton button = new JButton(name);
        button.addActionListener(e -> {
            expressionField.setText(expression);
            check();
        });
        return button;
    }

    // ---------- checking ----------

    private void check() {
        try {
            BracketResult r = BracketChecker.check(expressionField.getText());
            result = r;

            model.setRowCount(0);
            depth = 1;
            for (int i = 0; i < r.steps.length; i++) {
                BracketStep s = r.steps[i];
                model.addRow(new Object[] { i + 1, s.position, s.symbol, s.action, s.stackAfter });
                depth = Math.max(depth, itemCount(s.stackAfter));
            }

            verdictLabel.setForeground(r.balanced ? GREEN : Color.RED);
            verdictLabel.setText(r.balanced ? "BALANCED" : "NOT BALANCED");
            messageLabel.setForeground(r.balanced ? GREEN : Color.RED);
            messageLabel.setText(r.message);
            infoLabel.setText("Basic steps: " + r.operations + "   Time: "
                    + String.format("%.4f", r.nanos / 1_000_000.0)
                    + " ms   (one pass over the text, so O(n). Positions count every character, spaces included.)");

            if (r.steps.length > 0) {
                int last = r.steps.length - 1;
                table.setRowSelectionInterval(last, last); // show the final step
            } else {
                showStep(-1);
            }
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void showError(String message) {
        result = null;
        model.setRowCount(0);
        verdictLabel.setText(" ");
        messageLabel.setForeground(Color.RED);
        messageLabel.setText(message);
        infoLabel.setText(" ");
        showStep(-1);
    }

    // ---------- showing one step ----------

    /**
     * How many symbols are in a "stack after" text such as "{ ( [" or "(empty)".
     */
    private static int itemCount(String stackText) {
        if (stackText.equals("(empty)")) {
            return 0;
        }
        return (stackText.length() + 1) / 2; // symbols are one character, separated by one space
    }

    /** Draws the stack as it was after the chosen step. */
    private void showStep(int row) {
        if (result == null || row < 0 || row >= result.steps.length) {
            board.clear();
            stepLabel.setText(" ");
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
            return;
        }
        BracketStep s = result.steps[row];
        int count = itemCount(s.stackAfter);
        String[] labels = new String[depth];
        String[] above = new String[depth];
        for (int i = 0; i < count; i++) {
            labels[i] = String.valueOf(s.stackAfter.charAt(2 * i));
        }
        if (count > 0) {
            above[count - 1] = "TOP";
        }
        boolean pushed = s.action.startsWith("Push") && count > 0;
        board.setContents(labels, above, pushed ? count - 1 : -1);

        stepLabel.setText("Step " + (row + 1) + " of " + result.steps.length + ":  symbol '" + s.symbol
                + "' at position " + s.position + "  -  " + s.action);
        previousButton.setEnabled(row > 0);
        nextButton.setEnabled(row < result.steps.length - 1);
    }

    private void moveSelection(int change) {
        int row = table.getSelectedRow() + change;
        if (row >= 0 && row < model.getRowCount()) {
            table.setRowSelectionInterval(row, row);
            table.scrollRectToVisible(table.getCellRect(row, 0, true));
        }
    }
}