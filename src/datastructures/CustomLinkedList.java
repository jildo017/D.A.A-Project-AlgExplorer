package datastructures;

import utils.ExecutionTracker;

/** Singly linked list with a head pointer. */
public class CustomLinkedList {
    private Node head;

    private static void log(ExecutionTracker t, String message, boolean isOperation) {
        if (t == null) {
            return;
        }
        t.addStep(message);
        if (isOperation) {
            t.incrementOperations();
        }
    }

    public void insertEnd(int val, ExecutionTracker tracker) {
        log(tracker, "INSERT END " + val + ": create a new Node(" + val + ")", true);
        Node newNode = new Node(val);
        if (head == null) {
            log(tracker, "List is empty: head = newNode", true);
            head = newNode;
            return;
        }
        Node cur = head;
        log(tracker, "Start traversal at head (" + cur.data + ")", true);
        while (cur.next != null) {
            log(tracker, "Traverse: " + cur.data + " -> " + cur.next.data, true);
            cur = cur.next;
        }
        log(tracker, "Reached the last node (" + cur.data + "): last.next = newNode", true);
        cur.next = newNode;
    }

    public void insertBeginning(int val, ExecutionTracker tracker) {
        log(tracker, "INSERT START " + val + ": create a new Node(" + val + ")", true);
        Node newNode = new Node(val);
        log(tracker, "newNode.next = head (" + (head == null ? "null" : String.valueOf(head.data)) + ")", true);
        newNode.next = head;
        log(tracker, "head = newNode, so " + val + " is the new head", true);
        head = newNode;
    }

    /** Deletes the first node holding val. Returns true if something was deleted. */
    public boolean deleteValue(int val, ExecutionTracker tracker) {
        log(tracker, "DELETE " + val + ": start at head", false);
        if (head == null) {
            log(tracker, "List is empty: nothing to delete", false);
            return false;
        }
        if (head.data == val) {
            log(tracker, "head holds " + val + ": head = head.next", true);
            head = head.next;
            return true;
        }
        Node prev = head;
        Node cur = head.next;
        while (cur != null) {
            log(tracker, "Traverse: compare " + cur.data + " with " + val, true);
            if (cur.data == val) {
                log(tracker, "Found " + val + ": prev.next = cur.next (unlink the node)", true);
                prev.next = cur.next;
                return true;
            }
            prev = cur;
            cur = cur.next;
        }
        log(tracker, "Reached the end (null): " + val + " is not in the list", false);
        return false;
    }

    /** Returns the zero-based index of val, or -1 if it is not found. */
    public int search(int val, ExecutionTracker tracker) {
        log(tracker, "SEARCH " + val + ": start at head", false);
        Node cur = head;
        int index = 0;
        while (cur != null) {
            log(tracker, "Traverse: node " + index + " holds " + cur.data + (cur.data == val ? " (match)" : " (no match)"), true);
            if (cur.data == val) {
                log(tracker, "Found " + val + " at index " + index, false);
                return index;
            }
            cur = cur.next;
            index++;
        }
        log(tracker, "Reached the end (null): " + val + " not found", false);
        return -1;
    }

    public int[] toArray() {
        int count = 0;
        for (Node cur = head; cur != null; cur = cur.next) {
            count++;
        }
        int[] result = new int[count];
        int i = 0;
        for (Node cur = head; cur != null; cur = cur.next) {
            result[i] = cur.data;
            i++;
        }
        return result;
    }

    public void clear() {
        head = null;
    }

    public Node getHead() {
        return head;
    }
}
