package algexplorer.structures;

public class DoublyDemo {
    private static void say(DoublyLinkedList list, String what) {
        long steps = list.lastOperations();
        String forward = list.displayForward();
        String backward = list.displayBackward();
        System.out.println(what + "   steps=" + steps + "   size=" + list.size()
                + "   links " + (list.linksAreConsistent() ? "OK" : "BROKEN"));
        System.out.println("     " + forward);
        System.out.println("     " + backward);
    }

    private static void tryIt(String what, Runnable action) {
        try {
            action.run();
        } catch (IllegalStateException e) {
            System.out.println(what + "   REFUSED: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        DoublyLinkedList list = new DoublyLinkedList();
        list.insertAtEnd(20);
        say(list, "insertAtEnd(20)");
        list.insertAtEnd(30);
        say(list, "insertAtEnd(30)");
        list.insertAtBeginning(10);
        say(list, "insertAtBeginning(10)");
        list.insertAfter(20, 25);
        say(list, "insertAfter(20, 25)");
        list.insertBefore(10, 5);
        say(list, "insertBefore(10, 5)");
        list.insertBefore(30, 27);
        say(list, "insertBefore(30, 27)");
        list.insertAfter(30, 40);
        say(list, "insertAfter(30, 40)   (after the tail)");
        System.out.println();

        System.out.println("search(25) -> position " + list.search(25) + "   steps=" + list.lastOperations());
        System.out.println("search(99) -> " + list.search(99) + "   steps=" + list.lastOperations());
        System.out.println();

        System.out.println("deleteBefore(25) removed " + list.deleteBefore(25));
        say(list, "  after");
        System.out.println("deleteAfter(25) removed " + list.deleteAfter(25));
        say(list, "  after");
        System.out.println("deleteValue(10) removed " + list.deleteValue(10));
        say(list, "  after");
        System.out.println("deleteAtEnd() removed " + list.deleteAtEnd());
        say(list, "  after");
        System.out.println("deleteAtBeginning() removed " + list.deleteAtBeginning());
        say(list, "  after");
        System.out.println();

        tryIt("deleteBefore(25)", () -> list.deleteBefore(25));
        tryIt("insertAfter(99, 1)", () -> list.insertAfter(99, 1));
        tryIt("deleteAfter(30)", () -> list.deleteAfter(30));
        list.deleteAtEnd();
        list.deleteAtEnd();
        say(list, "emptied the list");
        tryIt("deleteAtBeginning() on empty", () -> list.deleteAtBeginning());
        tryIt("deleteAtEnd() on empty", () -> list.deleteAtEnd());
        System.out.println();

        // How much cheaper is the end of a doubly list? Build 11 nodes in each kind of
        // list.
        SinglyLinkedList singly = new SinglyLinkedList();
        DoublyLinkedList doubly = new DoublyLinkedList();
        for (int i = 1; i <= 10; i++) {
            singly.insertAtEnd(i);
            doubly.insertAtEnd(i);
        }
        singly.insertAtEnd(11);
        doubly.insertAtEnd(11);
        System.out.println("insertAtEnd on 10 nodes:   singly steps=" + singly.lastOperations()
                + "   doubly steps=" + doubly.lastOperations());
        singly.deleteAtEnd();
        doubly.deleteAtEnd();
        System.out.println("deleteAtEnd on 11 nodes:   singly steps=" + singly.lastOperations()
                + "   doubly steps=" + doubly.lastOperations());
    }
}