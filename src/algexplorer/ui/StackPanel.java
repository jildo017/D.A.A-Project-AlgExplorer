package algexplorer.ui;

import algexplorer.core.StopWatch;
import algexplorer.structures.ArrayStack;
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
 * Stack screen: a button for every operation, a drawing of the stack and a
 * history list.
 */
public class StackPanel extends JPanel {
    private static final Color GREEN = new Color(0, 128, 0);

    private final JTextField capacityField = new JTextField(4);
    private final JTextField valueField = new JTextField(6);
    private final JLabel messageLabel = new JLabel(" ");
    private final JLabel infoLabel = new JLabel(" ");
    private final JLabel stateLabel = new JLabel("No stack yet. Type a capacity and press Create stack.");
    private final JTextArea history = new JTextArea();
    private final ArrayBoard board = new ArrayBoard();

    private final JButton createButton = new JButton("Create stack");
    private final JButton pushButton = new JButton("Push");
    private final JButton popButton = new JButton("Pop");
    private final JButton peekButton = new JButton("Peek");
    private final JButton sizeButton = new JButton("Size");
    private final JButton emptyButton = new JButton("Is empty?");
    private final JButton fullButton = new JButton("Is full?");
    private final JButton displayButton = new JButton("Display all");

    private ArrayStack stack; // null until the user creates one
    private int actionCount = 0;

    public StackPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel createRow = new JPanel();
        createRow.add(new JLabel("Capacity (1-" + ArrayStack.MAX_CAPACITY + "):"));
        createRow.add(capacityField);
        createRow.add(createButton);

        JPanel opRow = new JPanel();
        opRow.add(new JLabel("Value:"));
        opRow.add(valueField);
        opRow.add(pushButton);
        opRow.add(popButton);
        opRow.add(peekButton);
        opRow.add(sizeButton);
        opRow.add(emptyButton);
        opRow.add(fullButton);
        opRow.add(displayButton);

        JPanel north = new JPanel(new GridLayout(0, 1));
        north.add(createRow);
        north.add(opRow);
        north.add(messageLabel);
        north.add(infoLabel);
        north.add(stateLabel);

        history.setEditable(false);
        history.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, board, new JScrollPane(history));
        split.setResizeWeight(0.4);

        add(north, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        createButton.addActionListener(e -> create());
        capacityField.addActionListener(e -> create());
        pushButton.addActionListener(e -> doPush());
        valueField.addActionListener(e -> doPush());
        popButton.addActionListener(e -> doPop());
        peekButton.addActionListener(e -> doPeek());
        sizeButton.addActionListener(e -> doSize());
        emptyButton.addActionListener(e -> doIsEmpty());
        fullButton.addActionListener(e -> doIsFull());
        displayButton.addActionListener(e -> doDisplay());

        setOperationsEnabled(false);
    }

    private void setOperationsEnabled(boolean on) {
        pushButton.setEnabled(on);
        popButton.setEnabled(on);
        peekButton.setEnabled(on);
        sizeButton.setEnabled(on);
        emptyButton.setEnabled(on);
        fullButton.setEnabled(on);
        displayButton.setEnabled(on);
    }

    // ---------- creating ----------

    private void create() {
        try {
            int capacity = Inputs.readInt(capacityField.getText(), "Capacity", 1, ArrayStack.MAX_CAPACITY);
            stack = new ArrayStack(capacity);
            history.setText("");
            actionCount = 0;
            setOperationsEnabled(true);
            say("Created an empty stack with capacity " + capacity + ".", true, "", -1);
        } catch (IllegalArgumentException ex) {
            say(ex.getMessage(), false, "", -1);
        }
    }

    // ---------- the seven operations ----------

    private void doPush() {
        if (stack == null) {
            return;
        }
        try {
            int value = Inputs.readInt(valueField.getText(), "Value", -9999, 9999);
            StopWatch watch = new StopWatch();
            watch.start();
            stack.push(value);
            watch.stop();
            say("Pushed " + value, true, cost(stack.lastOperations(), watch), stack.size() - 1);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            say(ex.getMessage(), false, "", -1);
        }
    }

    private void doPop() {
        if (stack == null) {
            return;
        }
        try {
            StopWatch watch = new StopWatch();
            watch.start();
            int value = stack.pop();
            watch.stop();
            say("Popped " + value, true, cost(stack.lastOperations(), watch), -1);
        } catch (IllegalStateException ex) {
            say(ex.getMessage(), false, "", -1);
        }
    }

    private void doPeek() {
        if (stack == null) {
            return;
        }
        try {
            StopWatch watch = new StopWatch();
            watch.start();
            int value = stack.peek();
            watch.stop();
            say("Top element is " + value + " (it stays on the stack).", true,
                    cost(stack.lastOperations(), watch), stack.size() - 1);
        } catch (IllegalStateException ex) {
            say(ex.getMessage(), false, "", -1);
        }
    }

    private void doSize() {
        if (stack == null) {
            return;
        }
        StopWatch watch = new StopWatch();
        watch.start();
        int size = stack.size();
        watch.stop();
        say("Size = " + size + " of " + stack.capacity() + ".", true, cost(1, watch), -1);
    }

    private void doIsEmpty() {
        if (stack == null) {
            return;
        }
        StopWatch watch = new StopWatch();
        watch.start();
        boolean answer = stack.isEmpty();
        watch.stop();
        say("Is the stack empty? " + (answer ? "Yes" : "No") + ".", true, cost(1, watch), -1);
    }

    private void doIsFull() {
        if (stack == null) {
            return;
        }
        StopWatch watch = new StopWatch();
        watch.start();
        boolean answer = stack.isFull();
        watch.stop();
        say("Is the stack full? " + (answer ? "Yes" : "No") + ".", true, cost(1, watch), -1);
    }

    private void doDisplay() {
        if (stack == null) {
            return;
        }
        StopWatch watch = new StopWatch();
        watch.start();
        String text = stack.display();
        watch.stop();
        say("Stack from bottom to top: " + text, true, cost(stack.lastOperations(), watch), -1);
    }

    // ---------- showing things ----------

    private static String cost(long steps, StopWatch watch) {
        return "Basic steps: " + steps + "   Time: " + String.format("%.4f", watch.getMillis()) + " ms";
    }

    /**
     * Shows a message (green or red), the cost, adds a history line and redraws the
     * stack.
     */
    private void say(String message, boolean ok, String costText, int highlight) {
        messageLabel.setForeground(ok ? GREEN : Color.RED);
        messageLabel.setText(message);
        infoLabel.setText(costText.isEmpty() ? " "
                : costText
                        + "   (push, pop, peek, size, is empty and is full are O(1); display is O(n))");
        if (stack != null) {
            actionCount++;
            history.append(actionCount + ". " + (ok ? "" : "[refused] ") + message + "\n");
            history.setCaretPosition(history.getDocument().getLength());
            redraw(highlight);
        }
    }

    /** Redraws the boxes and the one-line summary from the stack. */
    private void redraw(int highlight) {
        int capacity = stack.capacity();
        String[] labels = new String[capacity];
        String[] above = new String[capacity];
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < stack.size(); i++) {
            labels[i] = String.valueOf(stack.get(i));
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(stack.get(i));
        }
        sb.append("]");
        if (stack.size() > 0) {
            above[stack.size() - 1] = "TOP";
        }
        board.setContents(labels, above, highlight);
        stateLabel.setText("Stack (bottom to top): " + sb + "     Size " + stack.size()
                + " of " + capacity);
    }
}