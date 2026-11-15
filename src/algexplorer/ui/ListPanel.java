package algexplorer.ui;

import algexplorer.core.StopWatch;
import algexplorer.structures.DoublyLinkedList;
import algexplorer.structures.IntList;
import algexplorer.structures.SinglyLinkedList;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Linked list screen. One class serves both the singly and the doubly linked
 * list.
 */
public class ListPanel extends JPanel {
    private static final Color GREEN = new Color(0, 128, 0);
    private static final Color BLUE = new Color(0, 90, 160);
    private static final Color ORANGE = new Color(245, 176, 65);
    private static final Color FOUND = new Color(130, 210, 130);

    private final boolean doubly;
    private final JTextField valueField = new JTextField(6);
    private final JTextField targetField = new JTextField(6);
    private final JLabel messageLabel = new JLabel(" ");
    private final JLabel infoLabel = new JLabel(" ");
    private final JLabel stateLabel = new JLabel(" ");
    private final JTextArea history = new JTextArea();
    private final ListBoard board = new ListBoard();

    private final JButton insertFirstButton = new JButton("Insert at beginning");
    private final JButton insertLastButton = new JButton("Insert at end");
    private final JButton insertBeforeButton = new JButton("Insert before element");
    private final JButton insertAfterButton = new JButton("Insert after element");
    private final JButton deleteFirstButton = new JButton("Delete first");
    private final JButton deleteLastButton = new JButton("Delete last");
    private final JButton deleteBeforeButton = new JButton("Delete before element");
    private final JButton deleteAfterButton = new JButton("Delete after element");
    private final JButton deleteValueButton = new JButton("Delete element");
    private final JButton searchButton = new JButton("Search element");
    private final JButton sizeButton = new JButton("Size");
    private final JButton forwardButton = new JButton("Display forward");
    private final JButton backwardButton = new JButton("Display backward");
    private final JButton exampleButton = new JButton("Fill with 10, 20, 30, 40");
    private final JButton clearButton = new JButton("New empty list");

    private IntList list;
    private int actionCount = 0;

    public ListPanel(boolean doubly) {
        this.doubly = doubly;
        this.list = newList();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea explanation = new JTextArea(doubly
                ? "Doubly linked list. Every node holds a value and TWO arrows: next and prev. We keep HEAD "
                        + "and TAIL, so both ends are reached in one step and the list can be read backward. "
                        + "\"Given element\" means the first node holding that value."
                : "Singly linked list. Every node holds a value and ONE arrow: next. We keep only HEAD, so "
                        + "reaching the end means walking the whole chain. "
                        + "\"Given element\" means the first node holding that value.");
        explanation.setEditable(false);
        explanation.setLineWrap(true);
        explanation.setWrapStyleWord(true);
        explanation.setOpaque(false);
        explanation.setRows(3);
        explanation.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));

        JPanel fieldRow = new JPanel();
        fieldRow.add(new JLabel("Value to insert:"));
        fieldRow.add(valueField);
        fieldRow.add(new JLabel("Given element:"));
        fieldRow.add(targetField);

        JPanel insertRow = new JPanel();
        insertRow.add(insertFirstButton);
        insertRow.add(insertLastButton);
        insertRow.add(insertBeforeButton);
        insertRow.add(insertAfterButton);

        JPanel deleteRow = new JPanel();
        deleteRow.add(deleteFirstButton);
        deleteRow.add(deleteLastButton);
        deleteRow.add(deleteBeforeButton);
        deleteRow.add(deleteAfterButton);
        deleteRow.add(deleteValueButton);

        JPanel otherRow = new JPanel();
        otherRow.add(searchButton);
        otherRow.add(sizeButton);
        otherRow.add(forwardButton);
        otherRow.add(backwardButton);
        otherRow.add(exampleButton);
        otherRow.add(clearButton);
        backwardButton.setEnabled(doubly); // a singly list cannot be read backward

        JPanel rows = new JPanel(new GridLayout(0, 1));
        rows.add(fieldRow);
        rows.add(insertRow);
        rows.add(deleteRow);
        rows.add(otherRow);
        rows.add(messageLabel);
        rows.add(infoLabel);
        rows.add(stateLabel);

        JPanel north = new JPanel(new BorderLayout());
        north.add(explanation, BorderLayout.NORTH);
        north.add(rows, BorderLayout.CENTER);

        history.setEditable(false);
        history.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, board, new JScrollPane(history));
        split.setResizeWeight(0.4);

        add(north, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        insertFirstButton.addActionListener(e -> doInsertFirst());
        insertLastButton.addActionListener(e -> doInsertLast());
        insertBeforeButton.addActionListener(e -> doInsertBefore());
        insertAfterButton.addActionListener(e -> doInsertAfter());
        deleteFirstButton.addActionListener(e -> doDeleteFirst());
        deleteLastButton.addActionListener(e -> doDeleteLast());
        deleteBeforeButton.addActionListener(e -> doDeleteBefore());
        deleteAfterButton.addActionListener(e -> doDeleteAfter());
        deleteValueButton.addActionListener(e -> doDeleteValue());
        searchButton.addActionListener(e -> doSearch());
        sizeButton.addActionListener(e -> doSize());
        forwardButton.addActionListener(e -> doDisplayForward());
        backwardButton.addActionListener(e -> doDisplayBackward());
        exampleButton.addActionListener(e -> fillExample());
        clearButton.addActionListener(e -> startOver());

        say("Started an empty " + (doubly ? "doubly" : "singly") + " linked list.", GREEN, "", -1, ORANGE);
    }

    private IntList newList() {
        return doubly ? new DoublyLinkedList() : new SinglyLinkedList();
    }

    private int readValue() {
        return Inputs.readInt(valueField.getText(), "Value to insert", -9999, 9999);
    }

    private int readTarget() {
        return Inputs.readInt(targetField.getText(), "Given element", -9999, 9999);
    }

    // ---------- insertion ----------

    private void doInsertFirst() {
        try {
            int value = readValue();
            StopWatch watch = new StopWatch();
            watch.start();
            list.insertAtBeginning(value);
            watch.stop();
            say("Inserted " + value + " at the beginning.", GREEN, cost(list.lastOperations(), watch), 0, ORANGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            say(ex.getMessage(), Color.RED, "", -1, ORANGE);
        }
    }

    private void doInsertLast() {
        try {
            int value = readValue();
            StopWatch watch = new StopWatch();
            watch.start();
            list.insertAtEnd(value);
            watch.stop();
            say("Inserted " + value + " at the end.", GREEN, cost(list.lastOperations(), watch),
                    list.size() - 1, ORANGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            say(ex.getMessage(), Color.RED, "", -1, ORANGE);
        }
    }

    private void doInsertBefore() {
        try {
            int value = readValue();
            int target = readTarget();
            int at = list.search(target); // only to know where the new node will be
            StopWatch watch = new StopWatch();
            watch.start();
            list.insertBefore(target, value);
            watch.stop();
            say("Inserted " + value + " before " + target + ".", GREEN, cost(list.lastOperations(), watch),
                    at, ORANGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            say(ex.getMessage(), Color.RED, "", -1, ORANGE);
        }
    }

    private void doInsertAfter() {
        try {
            int value = readValue();
            int target = readTarget();
            int at = list.search(target);
            StopWatch watch = new StopWatch();
            watch.start();
            list.insertAfter(target, value);
            watch.stop();
            say("Inserted " + value + " after " + target + ".", GREEN, cost(list.lastOperations(), watch),
                    at + 1, ORANGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            say(ex.getMessage(), Color.RED, "", -1, ORANGE);
        }
    }

    // ---------- deletion ----------

    private void doDeleteFirst() {
        try {
            StopWatch watch = new StopWatch();
            watch.start();
            int value = list.deleteAtBeginning();
            watch.stop();
            say("Deleted " + value + " (the first element).", GREEN, cost(list.lastOperations(), watch), -1, ORANGE);
        } catch (IllegalStateException ex) {
            say(ex.getMessage(), Color.RED, "", -1, ORANGE);
        }
    }

    private void doDeleteLast() {
        try {
            StopWatch watch = new StopWatch();
            watch.start();
            int value = list.deleteAtEnd();
            watch.stop();
            say("Deleted " + value + " (the last element).", GREEN, cost(list.lastOperations(), watch), -1, ORANGE);
        } catch (IllegalStateException ex) {
            say(ex.getMessage(), Color.RED, "", -1, ORANGE);
        }
    }

    private void doDeleteBefore() {
        try {
            int target = readTarget();
            StopWatch watch = new StopWatch();
            watch.start();
            int value = list.deleteBefore(target);
            watch.stop();
            say("Deleted " + value + " (the element before " + target + ").", GREEN,
                    cost(list.lastOperations(), watch), -1, ORANGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            say(ex.getMessage(), Color.RED, "", -1, ORANGE);
        }
    }

    private void doDeleteAfter() {
        try {
            int target = readTarget();
            StopWatch watch = new StopWatch();
            watch.start();
            int value = list.deleteAfter(target);
            watch.stop();
            say("Deleted " + value + " (the element after " + target + ").", GREEN,
                    cost(list.lastOperations(), watch), -1, ORANGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            say(ex.getMessage(), Color.RED, "", -1, ORANGE);
        }
    }

    private void doDeleteValue() {
        try {
            int target = readTarget();
            StopWatch watch = new StopWatch();
            watch.start();
            int value = list.deleteValue(target);
            watch.stop();
            say("Deleted " + value + ".", GREEN, cost(list.lastOperations(), watch), -1, ORANGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            say(ex.getMessage(), Color.RED, "", -1, ORANGE);
        }
    }

    // ---------- searching and showing ----------

    private void doSearch() {
        try {
            int target = readTarget();
            StopWatch watch = new StopWatch();
            watch.start();
            int at = list.search(target);
            watch.stop();
            String costText = cost(list.lastOperations(), watch);
            if (at >= 0) {
                say("Found " + target + " at position " + at + " (counting from 0).", GREEN, costText, at, FOUND);
            } else {
                say(target + " was not found in the list (all " + list.size() + " node(s) were checked).",
                        BLUE, costText, -1, ORANGE);
            }
        } catch (IllegalArgumentException ex) {
            say(ex.getMessage(), Color.RED, "", -1, ORANGE);
        }
    }

    private void doSize() {
        StopWatch watch = new StopWatch();
        watch.start();
        int size = list.size();
        watch.stop();
        say("Size = " + size + " (a linked list has no fixed capacity; this demo shows at most "
                + SinglyLinkedList.MAX_NODES + " nodes).", BLUE, cost(1, watch), -1, ORANGE);
    }

    private void doDisplayForward() {
        StopWatch watch = new StopWatch();
        watch.start();
        String text = list.displayForward();
        watch.stop();
        say("Forward: " + text, BLUE, cost(list.lastOperations(), watch), -1, ORANGE);
    }

    private void doDisplayBackward() {
        try {
            StopWatch watch = new StopWatch();
            watch.start();
            String text = list.displayBackward();
            watch.stop();
            say("Backward: " + text, BLUE, cost(list.lastOperations(), watch), -1, ORANGE);
        } catch (UnsupportedOperationException ex) {
            say(ex.getMessage(), Color.RED, "", -1, ORANGE);
        }
    }

    private void fillExample() {
        startOver();
        for (int v = 10; v <= 40; v += 10) {
            StopWatch watch = new StopWatch();
            watch.start();
            list.insertAtEnd(v);
            watch.stop();
            say("Inserted " + v + " at the end.", GREEN, cost(list.lastOperations(), watch),
                    list.size() - 1, ORANGE);
        }
    }

    private void startOver() {
        list = newList();
        history.setText("");
        actionCount = 0;
        say("Started an empty " + (doubly ? "doubly" : "singly") + " linked list.", GREEN, "", -1, ORANGE);
    }

    // ---------- showing things ----------

    private static String cost(long steps, StopWatch watch) {
        return "Basic steps: " + steps + "   Time: " + String.format("%.4f", watch.getMillis()) + " ms";
    }

    private String bigO() {
        return doubly
                ? "(both ends are O(1); finding an element, then inserting or deleting next to it, is O(n))"
                : "(the beginning is O(1); the end and anything next to an element is O(n))";
    }

    /** Shows a message, the cost, adds a history line and redraws the list. */
    private void say(String message, Color color, String costText, int highlight, Color highlightColor) {
        messageLabel.setForeground(color);
        messageLabel.setText(message);
        infoLabel.setText(costText.isEmpty() ? " " : costText + "   " + bigO());
        actionCount++;
        history.append(actionCount + ". " + (color == Color.RED ? "[refused] " : "") + message + "\n");
        history.setCaretPosition(history.getDocument().getLength());
        redraw(highlight, highlightColor);
    }

    /** Redraws the boxes and arrows, and the one-line summary, from the list. */
    private void redraw(int highlight, Color highlightColor) {
        int n = list.size();
        int[] values = new int[n];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            values[i] = list.get(i);
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(values[i]);
        }
        board.setContents(values, doubly, highlight, highlightColor);
        stateLabel.setText("List (first to last): " + (n == 0 ? "empty" : sb.toString()) + "     Size " + n);
    }
}