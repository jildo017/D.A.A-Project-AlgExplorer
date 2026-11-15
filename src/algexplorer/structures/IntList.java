package algexplorer.structures;

/**
 * What every linked list of whole numbers can do. The singly and doubly lists
 * both keep these promises.
 */
public interface IntList {
    void insertAtBeginning(int value);

    void insertAtEnd(int value);

    void insertBefore(int target, int value);

    void insertAfter(int target, int value);

    int deleteAtBeginning();

    int deleteAtEnd();

    int deleteBefore(int target);

    int deleteAfter(int target);

    int deleteValue(int value);

    int search(int value);

    int size();

    boolean isEmpty();

    int get(int index);

    String displayForward();

    String displayBackward();

    boolean isDoubly();

    long lastOperations();
}