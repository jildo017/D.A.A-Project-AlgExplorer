package algexplorer.structures;

/**
 * One box of a singly linked list: a value and an arrow (next) to the following
 * box.
 */
class Node {
    int value;
    Node next;

    Node(int value) {
        this.value = value;
    }
}