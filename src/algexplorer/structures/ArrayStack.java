package algexplorer.structures;

import algexplorer.core.OpCounter;

/**
 * A stack kept in a fixed-size array. Last in, first out. Every operation
 * except display is O(1).
 */
public class ArrayStack {
    public static final int MAX_CAPACITY = 20;

    private final int[] data; // the boxes; index 0 is the bottom
    private int size = 0; // how many boxes are in use
    private final OpCounter counter = new OpCounter();

    public ArrayStack(int capacity) {
        if (capacity < 1 || capacity > MAX_CAPACITY) {
            throw new IllegalArgumentException("Capacity must be from 1 to " + MAX_CAPACITY + ".");
        }
        data = new int[capacity];
    }

    /** Puts a value on top. Time: O(1). */
    public void push(int value) {
        counter.reset();
        if (isFull()) {
            throw new IllegalStateException("Stack overflow: the stack is full (capacity "
                    + data.length + "), cannot push " + value + ".");
        }
        data[size] = value;
        counter.add();
        size++;
    }

    /** Removes and returns the top value. Time: O(1). */
    public int pop() {
        counter.reset();
        if (isEmpty()) {
            throw new IllegalStateException("Stack underflow: the stack is empty, nothing to pop.");
        }
        size--;
        int value = data[size];
        counter.add();
        return value;
    }

    /** Returns the top value without removing it. Time: O(1). */
    public int peek() {
        counter.reset();
        if (isEmpty()) {
            throw new IllegalStateException("The stack is empty, nothing to peek at.");
        }
        counter.add();
        return data[size - 1];
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

    /** The value at a position, counting from the bottom (0). Time: O(1). */
    public int get(int position) {
        if (position < 0 || position >= size) {
            throw new IllegalArgumentException("No element at position " + position + ".");
        }
        return data[position];
    }

    /** All elements from bottom to top, like "[10, 20, 30]". Time: O(n). */
    public String display() {
        counter.reset();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(data[i]);
            counter.add();
        }
        return sb.append("]").toString();
    }

    /** How many basic steps the last operation used. */
    public long lastOperations() {
        return counter.get();
    }
}