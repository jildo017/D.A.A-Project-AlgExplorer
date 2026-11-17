package datastructures;

import utils.ExecutionTracker;

/** FIFO queue built from linked Nodes with front and rear pointers. */
public class CustomQueue {
    private Node front;
    private Node rear;

    private static void log(ExecutionTracker t, String message, boolean isOperation) {
        if (t == null) {
            return;
        }
        t.addStep(message);
        if (isOperation) {
            t.incrementOperations();
        }
    }

    public void enqueue(int val, ExecutionTracker tracker) {
        log(tracker, "ENQUEUE " + val + ": create a new Node(" + val + ")", true);
        Node newNode = new Node(val);
        if (rear == null) {
            log(tracker, "Queue is empty: front = rear = newNode", true);
            front = newNode;
            rear = newNode;
        } else {
            log(tracker, "rear.next = newNode (link after " + rear.data + ")", true);
            rear.next = newNode;
            log(tracker, "rear = newNode, so " + val + " is the new rear", true);
            rear = newNode;
        }
    }

    public int dequeue(ExecutionTracker tracker) {
        if (front == null) {
            log(tracker, "DEQUEUE failed: front is null, the queue is empty (underflow)", false);
            throw new IllegalStateException("Queue is empty (underflow)");
        }
        int value = front.data;
        log(tracker, "DEQUEUE: read the value at front = " + value, true);
        log(tracker, "front = front.next (" + (front.next == null ? "null" : String.valueOf(front.next.data)) + ")", true);
        front = front.next;
        if (front == null) {
            log(tracker, "Queue is now empty: rear = null", true);
            rear = null;
        }
        log(tracker, "Dequeued value " + value + " returned", false);
        return value;
    }

    public int peek(ExecutionTracker tracker) {
        if (front == null) {
            log(tracker, "PEEK failed: the queue is empty", false);
            throw new IllegalStateException("Queue is empty");
        }
        log(tracker, "PEEK: read front.data = " + front.data + " (nothing is removed)", true);
        return front.data;
    }

    /** Values from front to rear. */
    public int[] toArray() {
        int count = 0;
        for (Node cur = front; cur != null; cur = cur.next) {
            count++;
        }
        int[] result = new int[count];
        int i = 0;
        for (Node cur = front; cur != null; cur = cur.next) {
            result[i] = cur.data;
            i++;
        }
        return result;
    }

    public void clear() {
        front = null;
        rear = null;
    }
}
