package algexplorer.ui;

import algexplorer.core.StopWatch;
import algexplorer.structures.CircularQueue;
import algexplorer.structures.IntQueue;
import algexplorer.structures.LinearQueue;
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
 * Queue screen. One class serves both the linear queue and the circular queue.
 */
public class QueuePanel extends JPanel {
    private static final Color GREEN = new Color(0, 128, 0);

    private final boolean circular; // false = linear queue, true = circular queue
    private final String kind; // "linear" or "circular", used in messages

    private final JTextField capacityField = new JTextField(4);
    private final JTextField valueField = new JTextField(6);
    private final JLabel messageLabel = new JLabel(" ");
    private final JLabel infoLabel = new JLabel(" ");
    private final JLabel stateLabel = new JLabel("No queue yet. Type a capacity and press Create queue.");
    private final JLabel indexLabel = new JLabel(" ");
    private final JTextArea history = new JTextArea();
    private final ArrayBoard board = new ArrayBoard();

    private final JButton createButton = new JButton("Create queue");
    private final JButton enqueueButton = new JButton("Enqueue");
    private final JButton dequeueButton = new JButton("Dequeue");
    private final JButton frontButton = new JButton("Front");
    private final JButton sizeButton = new JButton("Size");
    private final JButton emptyButton = new JButton("Is empty?");
    private final JButton fullButton = new JButton("Is full?");
    private final JButton displayButton = new JButton("Display all");
    private final JButton demoButton;

    private IntQueue queue; // null until the user creates one
    private int actionCount = 0;

    public QueuePanel(boolean circular) {
        this.circular = circular;
        this.kind = circular ? "circular" : "linear";
        demoButton = new JButton(circular ? "Run wrap-around demo" : "Run false-full demo");

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea explanation = new JTextArea(circular
                ? "Circular queue. When the rear reaches the last box it wraps round to box 0 "
                        + "(next box = (front + size) % capacity), so every free box can be reused. "
                        + "It is only full when ALL boxes are used."
                : "Linear queue. The rear only moves to the right. After some dequeues, the boxes "
                        + "before FRONT are wasted. When the rear reaches the last box the queue says \"full\" "
                        + "even though there is free room: this is the FALSE FULL problem. "
                        + "The circular queue fixes it.");
        explanation.setEditable(false);
        explanation.setLineWrap(true);
        explanation.setWrapStyleWord(true);
        explanation.setOpaque(false);
        explanation.setRows(3);
        explanation.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));

        JPanel createRow = new JPanel();
        createRow.add(new JLabel("Capacity (1-" + LinearQueue.MAX_CAPACITY + "):"));
        createRow.add(capacityField);
        createRow.add(createButton);
        createRow.add(demoButton);

        JPanel opRow = new JPanel();
        opRow.add(new JLabel("Value:"));
        opRow.add(valueField);
        opRow.add(enqueueButton);
        opRow.add(dequeueButton);
        opRow.add(frontButton);
        opRow.add(sizeButton);
        opRow.add(emptyButton);
        opRow.add(fullButton);
        opRow.add(displayButton);

        JPanel rows = new JPanel(new GridLayout(0, 1));
        rows.add(createRow);
        rows.add(opRow);
        rows.add(messageLabel);
        rows.add(infoLabel);
        rows.add(stateLabel);
        rows.add(indexLabel);

        JPanel north = new JPanel(new BorderLayout());
        north.add(explanation, BorderLayout.NORTH);
        north.add(rows, BorderLayout.CENTER);

        history.setEditable(false);
        history.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, board, new JScrollPane(history));
        split.setResizeWeight(0.4);

        add(north, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        createButton.addActionListener(e -> create());
        capacityField.addActionListener(e -> create());
        demoButton.addActionListener(e -> runDemo());
        enqueueButton.addActionListener(e -> doEnqueue());
        valueField.addActionListener(e -> doEnqueue());
        dequeueButton.addActionListener(e -> doDequeue());
        frontButton.addActionListener(e -> doFront());
        sizeButton.addActionListener(e -> doSize());
        emptyButton.addActionListener(e -> doIsEmpty());
        fullButton.addActionListener(e -> doIsFull());
        displayButton.addActionListener(e -> doDisplay());

        setOperationsEnabled(false);
    }

    private void setOperationsEnabled(boolean on) {
        enqueueButton.setEnabled(on);
        dequeueButton.setEnabled(on);
        frontButton.setEnabled(on);
        sizeButton.setEnabled(on);
        emptyButton.setEnabled(on);
        fullButton.setEnabled(on);
        displayButton.setEnabled(on);
    }

    // ---------- creating ----------

    private void create() {
        try {
            int capacity = Inputs.readInt(capacityField.getText(), "Capacity", 1, LinearQueue.MAX_CAPACITY);
            queue = circular ? new CircularQueue(capacity) : new LinearQueue(capacity);
            history.setText("");
            actionCount = 0;
            setOperationsEnabled(true);
            say("Created an empty " + kind + " queue with capacity " + capacity + ".", true, "", -1);
        } catch (IllegalArgumentException ex) {
            say(ex.getMessage(), false, "", -1);
        }
    }

    /**
     * Builds a small queue and plays the steps that show the problem (or the fix).
     */
    private void runDemo() {
        capacityField.setText("5");
        create();
        for (int v = 10; v <= 50; v += 10) {
            enqueueValue(v);
        }
        doDequeue();
        doDequeue();
        enqueueValue(60); // linear: refused (false full). circular: works
        if (circular) {
            enqueueValue(70);
            enqueueValue(80); // now it is really full
        }
    }

    // ---------- the seven operations ----------

    private void doEnqueue() {
        if (queue == null) {
            return;
        }
        try {
            enqueueValue(Inputs.readInt(valueField.getText(), "Value", -9999, 9999));
        } catch (IllegalArgumentException ex) {
            say(ex.getMessage(), false, "", -1);
        }
    }

    private void enqueueValue(int value) {
        try {
            StopWatch watch = new StopWatch();
            watch.start();
            queue.enqueue(value);
            watch.stop();
            say("Enqueued " + value, true, cost(queue.lastOperations(), watch), queue.getRearIndex());
        } catch (IllegalStateException ex) {
            say(ex.getMessage(), false, "", -1);
        }
    }

    private void doDequeue() {
        if (queue == null) {
            return;
        }
        try {
            StopWatch watch = new StopWatch();
            watch.start();
            int value = queue.dequeue();
            watch.stop();
            say("Dequeued " + value, true, cost(queue.lastOperations(), watch), -1);
        } catch (IllegalStateException ex) {
            say(ex.getMessage(), false, "", -1);
        }
    }

    private void doFront() {
        if (queue == null) {
            return;
        }
        try {
            StopWatch watch = new StopWatch();
            watch.start();
            int value = queue.front();
            watch.stop();
            say("Front element is " + value + " (it stays in the queue).", true,
                    cost(queue.lastOperations(), watch), queue.getFrontIndex());
        } catch (IllegalStateException ex) {
            say(ex.getMessage(), false, "", -1);
        }
    }

    private void doSize() {
        if (queue == null) {
            return;
        }
        StopWatch watch = new StopWatch();
        watch.start();
        int size = queue.size();
        watch.stop();
        say("Size = " + size + " of " + queue.capacity() + ".", true, cost(1, watch), -1);
    }

    private void doIsEmpty() {
        if (queue == null) {
            return;
        }
        StopWatch watch = new StopWatch();
        watch.start();
        boolean answer = queue.isEmpty();
        watch.stop();
        say("Is the queue empty? " + (answer ? "Yes" : "No") + ".", true, cost(1, watch), -1);
    }

    private void doIsFull() {
        if (queue == null) {
            return;
        }
        StopWatch watch = new StopWatch();
        watch.start();
        boolean answer = queue.isFull();
        watch.stop();
        String extra = answer && queue.wastedSlots() > 0
                ? " (but " + queue.wastedSlots() + " box(es) at the front are free: this is a FALSE full)"
                : "";
        say("Is the queue full? " + (answer ? "Yes" : "No") + "." + extra, true, cost(1, watch), -1);
    }

    private void doDisplay() {
        if (queue == null) {
            return;
        }
        StopWatch watch = new StopWatch();
        watch.start();
        String text = queue.display();
        watch.stop();
        say("Queue from front to rear: " + text, true, cost(queue.lastOperations(), watch), -1);
    }

    // ---------- showing things ----------

    private static String cost(long steps, StopWatch watch) {
        return "Basic steps: " + steps + "   Time: " + String.format("%.4f", watch.getMillis()) + " ms";
    }

    /**
     * Shows a message (green or red), the cost, adds a history line and redraws the
     * queue.
     */
    private void say(String message, boolean ok, String costText, int highlight) {
        messageLabel.setForeground(ok ? GREEN : Color.RED);
        messageLabel.setText(message);
        infoLabel.setText(costText.isEmpty() ? " "
                : costText
                        + "   (enqueue, dequeue, front, size, is empty and is full are O(1); display is O(n))");
        if (queue != null) {
            actionCount++;
            history.append(actionCount + ". " + (ok ? "" : "[refused] ") + message + "\n");
            history.setCaretPosition(history.getDocument().getLength());
            redraw(highlight);
        }
    }

    /**
     * Redraws the boxes, the FRONT and REAR markers, and the summary lines from the
     * queue.
     */
    private void redraw(int highlight) {
        int capacity = queue.capacity();
        String[] labels = new String[capacity];
        String[] above = new String[capacity];
        for (int i = 0; i < capacity; i++) {
            if (queue.isOccupied(i)) {
                labels[i] = String.valueOf(queue.valueAt(i));
            }
        }
        boolean small = capacity > 10; // short markers when the boxes are narrow
        if (!queue.isEmpty()) {
            int f = queue.getFrontIndex();
            int r = queue.getRearIndex();
            if (f == r) {
                above[f] = small ? "F/R" : "FRONT/REAR";
            } else {
                above[f] = small ? "F" : "FRONT";
                above[r] = small ? "R" : "REAR";
            }
        }
        board.setContents(labels, above, highlight);

        StringBuilder order = new StringBuilder("[");
        for (int i = 0; i < queue.size(); i++) {
            if (i > 0) {
                order.append(", ");
            }
            order.append(queue.valueAt((queue.getFrontIndex() + i) % capacity));
        }
        order.append("]");
        stateLabel.setText("Queue (front to rear): " + order + "     Size " + queue.size()
                + " of " + capacity + (small ? "     (F = front, R = rear)" : ""));

        String rear = queue.isEmpty() ? "none" : String.valueOf(queue.getRearIndex());
        String text = "front index = " + queue.getFrontIndex() + "     rear index = " + rear;
        boolean falseFull = queue.isFull() && queue.wastedSlots() > 0;
        if (!circular) {
            text += "     wasted boxes = " + queue.wastedSlots()
                    + (falseFull ? "     FALSE FULL: free boxes exist but the rear is at the last box" : "");
        }
        indexLabel.setForeground(falseFull ? Color.RED : Color.BLACK);
        indexLabel.setText(text);
    }
}