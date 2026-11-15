package algexplorer.structures;

import algexplorer.core.OpCounter;

/**
 * A singly linked list built from our own Node objects. It keeps only a head
 * arrow, so reaching the end
 * means walking the whole chain. Basic steps: one for every node visited, plus
 * one for each link changed.
 */
public class SinglyLinkedList implements IntList {
    public static final int MAX_NODES = 12; // a display limit so the picture fits the screen

    private Node head = null; // the first node (null = empty list)
    private int size = 0;
    private final OpCounter counter = new OpCounter();

    // ---------- small helpers ----------

    private void checkRoom(int value) {
        if (size >= MAX_NODES) {
            throw new IllegalStateException("The list is full (this demo allows at most "
                    + MAX_NODES + " nodes), cannot insert " + value + ".");
        }
    }

    private static IllegalStateException notFound(int target) {
        return new IllegalStateException("Element " + target + " was not found in the list.");
    }

    private static IllegalStateException emptyList() {
        return new IllegalStateException("The list is empty, nothing to delete.");
    }

    // ---------- insertion ----------

    /** Puts a new node in front of the head. Time: O(1). */
    public void insertAtBeginning(int value) {
        counter.reset();
        checkRoom(value);
        Node node = new Node(value);
        node.next = head;
        head = node;
        size++;
        counter.add(); // the link change
    }

    /** Walks to the last node and links a new one after it. Time: O(n). */
    public void insertAtEnd(int value) {
        counter.reset();
        checkRoom(value);
        Node node = new Node(value);
        if (head == null) {
            head = node;
        } else {
            Node current = head;
            counter.add(); // visited the head
            while (current.next != null) {
                current = current.next;
                counter.add(); // visited another node
            }
            current.next = node;
        }
        size++;
        counter.add(); // the link change
    }

    /** Inserts after the first node holding target. Time: O(n). */
    public void insertAfter(int target, int value) {
        counter.reset();
        checkRoom(value);
        Node current = head;
        while (current != null) {
            counter.add(); // visited a node
            if (current.value == target) {
                break;
            }
            current = current.next;
        }
        if (current == null) {
            throw notFound(target);
        }
        Node node = new Node(value);
        node.next = current.next;
        current.next = node;
        size++;
        counter.add();
    }

    /** Inserts before the first node holding target. Time: O(n). */
    public void insertBefore(int target, int value) {
        counter.reset();
        checkRoom(value);
        if (head == null) {
            throw notFound(target);
        }
        Node node = new Node(value);
        counter.add(); // visited the head
        if (head.value == target) { // the target is the head: new first node
            node.next = head;
            head = node;
        } else {
            Node before = head; // the node just before the one we look at
            while (before.next != null) {
                counter.add(); // visited before.next
                if (before.next.value == target) {
                    break;
                }
                before = before.next;
            }
            if (before.next == null) {
                throw notFound(target);
            }
            node.next = before.next;
            before.next = node;
        }
        size++;
        counter.add();
    }

    // ---------- deletion ----------

    /** Removes the head. Time: O(1). */
    public int deleteAtBeginning() {
        counter.reset();
        if (head == null) {
            throw emptyList();
        }
        int value = head.value;
        head = head.next;
        size--;
        counter.add();
        return value;
    }

    /** Removes the last node; we must walk to the one before it. Time: O(n). */
    public int deleteAtEnd() {
        counter.reset();
        if (head == null) {
            throw emptyList();
        }
        int value;
        counter.add(); // visited the head
        if (head.next == null) { // only one node
            value = head.value;
            head = null;
        } else {
            Node before = head;
            while (before.next.next != null) {
                before = before.next;
                counter.add(); // visited another node
            }
            value = before.next.value;
            before.next = null;
        }
        size--;
        counter.add();
        return value;
    }

    /** Removes the first node holding value. Time: O(n). */
    public int deleteValue(int value) {
        counter.reset();
        if (head == null) {
            throw emptyList();
        }
        counter.add(); // visited the head
        if (head.value == value) {
            head = head.next;
        } else {
            Node before = head;
            while (before.next != null) {
                counter.add(); // visited before.next
                if (before.next.value == value) {
                    break;
                }
                before = before.next;
            }
            if (before.next == null) {
                throw notFound(value);
            }
            before.next = before.next.next;
        }
        size--;
        counter.add();
        return value;
    }

    /** Removes the node just after the first node holding target. Time: O(n). */
    public int deleteAfter(int target) {
        counter.reset();
        if (head == null) {
            throw emptyList();
        }
        Node current = head;
        while (current != null) {
            counter.add(); // visited a node
            if (current.value == target) {
                break;
            }
            current = current.next;
        }
        if (current == null) {
            throw notFound(target);
        }
        if (current.next == null) {
            throw new IllegalStateException("Nothing to delete: " + target
                    + " is the last element, so there is nothing after it.");
        }
        int value = current.next.value;
        current.next = current.next.next;
        size--;
        counter.add();
        return value;
    }

    /** Removes the node just before the first node holding target. Time: O(n). */
    public int deleteBefore(int target) {
        counter.reset();
        if (head == null) {
            throw emptyList();
        }
        counter.add(); // visited the head
        if (head.value == target) {
            throw new IllegalStateException("Nothing to delete: " + target
                    + " is the first element, so there is nothing before it.");
        }
        Node beforeBefore = null; // two nodes behind the one we look at
        Node before = head; // the node we will delete
        Node current = head.next; // the node we compare with target
        while (current != null) {
            counter.add(); // visited a node
            if (current.value == target) {
                break;
            }
            beforeBefore = before;
            before = current;
            current = current.next;
        }
        if (current == null) {
            throw notFound(target);
        }
        int value = before.value;
        if (beforeBefore == null) { // we are deleting the head
            head = current;
        } else {
            beforeBefore.next = current;
        }
        size--;
        counter.add();
        return value;
    }

    // ---------- searching and showing ----------

    /**
     * The position (0 = first) of the first node holding value, or -1. Time: O(n).
     */
    public int search(int value) {
        counter.reset();
        int index = 0;
        Node current = head;
        while (current != null) {
            counter.add(); // visited a node
            if (current.value == value) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** The value at a position (0 = first). Used for drawing. Time: O(n). */
    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("No element at position " + index + ".");
        }
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.value;
    }

    /** Like "head -> 10 -> 20 -> null". Time: O(n). */
    public String displayForward() {
        counter.reset();
        StringBuilder sb = new StringBuilder("head -> ");
        Node current = head;
        while (current != null) {
            sb.append(current.value).append(" -> ");
            current = current.next;
            counter.add();
        }
        return sb.append("null").toString();
    }

    public String displayBackward() {
        throw new UnsupportedOperationException("A singly linked list cannot be read backward.");
    }

    public boolean isDoubly() {
        return false;
    }

    public long lastOperations() {
        return counter.get();
    }
}