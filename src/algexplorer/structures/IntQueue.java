package algexplorer.structures;

/**
 * What every queue of whole numbers can do. LinearQueue and CircularQueue both
 * keep these promises.
 */
public interface IntQueue {
    void enqueue(int value);

    int dequeue();

    int front();

    int size();

    boolean isEmpty();

    boolean isFull();

    int capacity();

    int getFrontIndex();

    int getRearIndex();

    int wastedSlots();

    boolean isOccupied(int index);

    int valueAt(int index);

    String display();

    long lastOperations();
}