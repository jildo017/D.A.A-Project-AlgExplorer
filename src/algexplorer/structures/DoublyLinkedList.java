package algexplorer.structures;

import algexplorer.core.OpCounter;

/**
 * A doubly linked list built from our own DNode objects. It keeps a head arrow
 * AND a tail arrow, and every
 * node knows the node before it. Basic steps: one for every node visited, plus
 * one for each relinking.
 */
public class DoublyLinkedList implements IntList {
    public static final int MAX_NODES = 12; // a display limit so the picture fits the screen

    private DNode head = null; // the first node (null = empty list)
    private DNode tail = null; // the last node
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

    /**
     * Walks from the head to the first node holding value (or null). One step per
     * node visited.
     */
    private DNode find(int value) {
        DNode current = head;
        while (current != null) {
            counter.add();
            if (current.value == value) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    /** Cuts one node out of the chain by joining its two neighbours. Time: O(1). */
    private void unlink(DNode node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next; // the node was the head
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev; // the node was the tail
        }
        size--;
        counter.add(); // the relinking
    }

    // ---------- insertion ----------

    /** Puts a new node in front of the head. Time: O(1). */
    public void insertAtBeginning(int value) {
        counter.reset();
        checkRoom(value);
        DNode node = new DNode(value);
        node.next = head;
        if (head != null) {
            head.prev = node;
        } else {
            tail = node; // the list was empty
        }
        head = node;
        size++;
        counter.add();
    }

    /** Puts a new node after the tail. No walking needed. Time: O(1). */
    public void insertAtEnd(int value) {
        counter.reset();
        checkRoom(value);
        DNode node = new DNode(value);
        node.prev = tail;
        if (tail != null) {
            tail.next = node;
        } else {
            head = node; // the list was empty
        }
        tail = node;
        size++;
        counter.add();
    }

    /**
     * Inserts after the first node holding target. Time: O(n) to find it, then
     * O(1).
     */
    public void insertAfter(int target, int value) {
        counter.reset();
        checkRoom(value);
        DNode found = find(target);
        if (found == null) {
            throw notFound(target);
        }
        DNode node = new DNode(value);
        node.prev = found; // first set the new node's own arrows
        node.next = found.next;
        if (found.next != null) {
            found.next.prev = node;
        } else {
            tail = node; // we added after the old tail
        }
        found.next = node; // last, change the old node's arrow
        size++;
        counter.add();
    }

    /**
     * Inserts before the first node holding target. Time: O(n) to find it, then
     * O(1).
     */
    public void insertBefore(int target, int value) {
        counter.reset();
        checkRoom(value);
        DNode found = find(target);
        if (found == null) {
            throw notFound(target);
        }
        DNode node = new DNode(value);
        node.next = found;
        node.prev = found.prev;
        if (found.prev != null) {
            found.prev.next = node;
        } else {
            head = node; // we added before the old head
        }
        found.prev = node;
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
        unlink(head);
        return value;
    }

    /**
     * Removes the tail. The tail knows the node before it, so no walking. Time:
     * O(1).
     */
    public int deleteAtEnd() {
        counter.reset();
        if (tail == null) {
            throw emptyList();
        }
        int value = tail.value;
        unlink(tail);
        return value;
    }

    /** Removes the first node holding value. Time: O(n) to find it, then O(1). */
    public int deleteValue(int value) {
        counter.reset();
        if (head == null) {
            throw emptyList();
        }
        DNode found = find(value);
        if (found == null) {
            throw notFound(value);
        }
        unlink(found);
        return value;
    }

    /**
     * Removes the node just after the first node holding target. Time: O(n) to
     * find, then O(1).
     */
    public int deleteAfter(int target) {
        counter.reset();
        if (head == null) {
            throw emptyList();
        }
        DNode found = find(target);
        if (found == null) {
            throw notFound(target);
        }
        if (found.next == null) {
            throw new IllegalStateException("Nothing to delete: " + target
                    + " is the last element, so there is nothing after it.");
        }
        int value = found.next.value;
        unlink(found.next);
        return value;
    }

    /**
     * Removes the node just before the first node holding target. Time: O(n) to
     * find, then O(1).
     */
    public int deleteBefore(int target) {
        counter.reset();
        if (head == null) {
            throw emptyList();
        }
        DNode found = find(target);
        if (found == null) {
            throw notFound(target);
        }
        if (found.prev == null) {
            throw new IllegalStateException("Nothing to delete: " + target
                    + " is the first element, so there is nothing before it.");
        }
        int value = found.prev.value; // the node knows its own predecessor
        unlink(found.prev);
        return value;
    }

    // ---------- searching and showing ----------

    /**
     * The position (0 = first) of the first node holding value, or -1. Time: O(n).
     */
    public int search(int value) {
        counter.reset();
        int index = 0;
        DNode current = head;
        while (current != null) {
            counter.add();
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
        DNode current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.value;
    }

    /**
     * Like "head -> 10 <-> 20 <-> 30 <- tail", reading with the next arrows. Time:
     * O(n).
     */
    public String displayForward() {
        counter.reset();
        if (head == null) {
            return "empty list (head = null, tail = null)";
        }
        StringBuilder sb = new StringBuilder("head -> ");
        DNode current = head;
        while (current != null) {
            sb.append(current.value);
            if (current.next != null) {
                sb.append(" <-> ");
            }
            current = current.next;
            counter.add();
        }
        return sb.append(" <- tail").toString();
    }

    /**
     * Like "tail -> 30 <-> 20 <-> 10 <- head", reading with the prev arrows. Time:
     * O(n).
     */
    public String displayBackward() {
        counter.reset();
        if (tail == null) {
            return "empty list (head = null, tail = null)";
        }
        StringBuilder sb = new StringBuilder("tail -> ");
        DNode current = tail;
        while (current != null) {
            sb.append(current.value);
            if (current.prev != null) {
                sb.append(" <-> ");
            }
            current = current.prev;
            counter.add();
        }
        return sb.append(" <- head").toString();
    }

    public boolean isDoubly() {
        return true;
    }

    public long lastOperations() {
        return counter.get();
    }

    /**
     * A test helper: true if every prev arrow mirrors a next arrow and head, tail
     * and size agree.
     */
    public boolean linksAreConsistent() {
        if (head == null || tail == null) {
            return head == null && tail == null && size == 0;
        }
        if (head.prev != null || tail.next != null) {
            return false;
        }
        int count = 0;
        DNode current = head;
        DNode last = null;
        while (current != null) {
            if (current.prev != last) {
                return false;
            }
            last = current;
            current = current.next;
            count++;
        }
        return last == tail && count == size;
    }
}