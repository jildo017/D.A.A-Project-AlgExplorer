package algexplorer.structures;

import algexplorer.core.OpCounter;

/**
 * A circular queue in a fixed-size array. The rear wraps round to the start, so
 * no box is wasted.
 */
public class CircularQueue implements IntQueue {
    public static final int MAX_CAPACITY = 20;

    private final int[] data;
    private int front = 0; // index of the first element
    private int size = 0; // how many elements are stored
    private final OpCounter counter = new OpCounter();

    public CircularQueue(int capacity) {
        if (capacity < 1 || capacity > MAX_CAPACITY) {
            throw new IllegalArgumentException("Capacity must be from 1 to " + MAX_CAPACITY + ".");
        }
        data = new int[capacity];
    }

    /** Adds a value at the rear, wrapping round if needed. Time: O(1). */
    public void enqueue(int value) {
        counter.reset();
        if (isFull()) {
            throw new IllegalStateException("Queue is full (capacity " + data.length
                    + "), cannot enqueue " + value + ".");
        }
        int rear = (front + size) % data.length; // the next free box, wrapping round
        data[rear] = value;
        counter.add();
        size++;
    }

    /** Removes and returns the first value. Time: O(1). */
    public int dequeue() {
        counter.reset();
        if (isEmpty()) {
            throw new IllegalStateException("Queue underflow: the queue is empty, nothing to dequeue.");
        }
        int value = data[front];
        counter.add();
        front = (front + 1) % data.length; // move the front, wrapping round
        size--;
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
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == data.length;
    }

    public int capacity() {
        return data.length;
    }

    public int getFrontIndex() {
        return front;
    }

    /** Index of the last element, or -1 if the queue is empty. */
    public int getRearIndex() {
        return size == 0 ? -1 : (front + size - 1) % data.length;
    }

    /** A circular queue never wastes a box. */
    public int wastedSlots() {
        return 0;
    }

    public boolean isOccupied(int index) {
        return ((index - front + data.length) % data.length) < size;
    }

    public int valueAt(int index) {
        return data[index];
    }

    /** The elements from front to rear, like "[10, 20, 30]". Time: O(n). */
    public String display() {
        counter.reset();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(data[(front + i) % data.length]);
            counter.add();
        }
        return sb.append("]").toString();
    }

    /** Every box in the array, with "_" for a free box. Time: O(capacity). */
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