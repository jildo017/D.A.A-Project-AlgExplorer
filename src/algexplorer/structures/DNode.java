package algexplorer.structures;

/**
 * One box of a doubly linked list: a value, an arrow back (prev) and an arrow
 * forward (next).
 */
class DNode {
    int value;
    DNode prev;
    DNode next;

    DNode(int value) {
        this.value = value;
    }
}