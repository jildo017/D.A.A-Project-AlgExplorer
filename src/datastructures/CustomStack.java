package datastructures;

import utils.ExecutionTracker;

/** LIFO stack built from linked Nodes. */
public class CustomStack {
    private Node top;

    private static void log(ExecutionTracker t, String message, boolean isOperation) {
        if (t == null) {
            return;
        }
        t.addStep(message);
        if (isOperation) {
            t.incrementOperations();
        }
    }

    public void push(int val, ExecutionTracker tracker) {
        log(tracker, "PUSH " + val + ": create a new Node(" + val + ")", true);
        Node newNode = new Node(val);
        log(tracker, "newNode.next = top (" + (top == null ? "null" : String.valueOf(top.data)) + ")", true);
        newNode.next = top;
        log(tracker, "top = newNode, so " + val + " is now the top of the stack", true);
        top = newNode;
    }

    public int pop(ExecutionTracker tracker) {
        if (top == null) {
            log(tracker, "POP failed: top is null, the stack is empty (underflow)", false);
            throw new IllegalStateException("Stack is empty (underflow)");
        }
        int value = top.data;
        log(tracker, "POP: read the value at top = " + value, true);
        log(tracker, "top = top.next (" + (top.next == null ? "null" : String.valueOf(top.next.data)) + ")", true);
        top = top.next;
        log(tracker, "Node removed; popped value " + value + " returned", false);
        return value;
    }

    public int peek(ExecutionTracker tracker) {
        if (top == null) {
            log(tracker, "PEEK failed: the stack is empty", false);
            throw new IllegalStateException("Stack is empty");
        }
        log(tracker, "PEEK: read top.data = " + top.data + " (nothing is removed)", true);
        return top.data;
    }

    /** Values from top to bottom. */
    public int[] toArray() {
        int count = 0;
        for (Node cur = top; cur != null; cur = cur.next) {
            count++;
        }
        int[] result = new int[count];
        int i = 0;
        for (Node cur = top; cur != null; cur = cur.next) {
            result[i] = cur.data;
            i++;
        }
        return result;
    }

    public void clear() {
        top = null;
    }
}
