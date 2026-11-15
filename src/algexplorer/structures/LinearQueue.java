package algexplorer.structures;

import algexplorer.core.OpCounter;

/**
 * A linear queue in a fixed-size array. First in, first out. It shows the
 * "false full" problem.
 */
public class LinearQueue implements IntQueue {
    public static final int MAX_CAPACITY = 20;

    private final int[] data;
    private int front = 0; // index of the first element
    private int rear = -1; // index of the last element (-1 = nothing yet)
    private final OpCounter counter = new OpCounter();

    public LinearQueue(int capacity) {
        if (capacity < 1 || capacity > MAX_CAPACITY) {
            throw new IllegalArgumentException("Capacity must be from 1 to " + MAX_CAPACITY + ".");
        }
        data = new int[capacity];
    }

    /** Adds a value at the rear. Time: O(1). */
    public void enqueue(int value) {
        counter.reset();
        if (isFull()) {
            if (front > 0) {
                throw new IllegalStateException("Queue is full: the rear is at the last box, but "
                        + front + " empty box(es) at the front are wasted (the false full problem).");
            }
            throw new IllegalStateException("Queue is full (capacity " + data.length
                    + "), cannot enqueue " + value + ".");
        }
        rear++;
        data[rear] = value;
        counter.add();
    }

    /** Removes and returns the first value. Time: O(1). */
    public int dequeue() {
        counter.reset();
        if (isEmpty()) {
            throw new IllegalStateException("Queue underflow: the queue is empty, nothing to dequeue.");
        }
        int value = data[front];
        counter.add();
        front++;
        return value;
    }

    /** Returns the first value without removing it. Time: O(1). */
    public int front() {
        counter.reset();
        if (isEmpty()) {
            throw new IllegalStateException("The queue is empty, no front element.");
        }
        counter.add();
        return data[front];
    }

    public int size() {
        return rear - front + 1;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean isFull() {
        return rear == data.length - 1;
    } // true even if there is free room at the front

    public int capacity() {
        return data.length;
    }

    public int getFrontIndex() {
        return front;
    }

    public int getRearIndex() {
        return rear;
    }

    /** Boxes before the front that can never be used again. */
    public int wastedSlots() {
        return front;
    }

    public boolean isOccupied(int index) {
        return index >= front && index <= rear;
    }

    public int valueAt(int index) {
        return data[index];
    }

    /** The elements from front to rear, like "[10, 20, 30]". Time: O(n). */
    public String display() {
        counter.reset();
        StringBuilder sb = new StringBuilder("[");
        for (int i = front; i <= rear; i++) {
            if (i > front) {
                sb.append(", ");
            }
            sb.append(data[i]);
            counter.add();
        }
        return sb.append("]").toString();
    }

    /**
     * Every box in the array, with "_" for a free box, like "[_, _, 30, 40]". Time:
     * O(capacity).
     */
    public String slots() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < data.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(isOccupied(i) ? String.valueOf(data[i]) : "_");
        }
        return sb.append("]").toString();
    }

    public long lastOperations() {
        return counter.get();
    }
}