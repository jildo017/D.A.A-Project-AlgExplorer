package algexplorer.structures;

public class ListDemo {
    private static void say(IntList list, String what) {
        System.out.println(what + "   steps=" + list.lastOperations() + "   " + list.displayForward()
                + "   size=" + list.size());
    }

    private static void tryIt(String what, Runnable action) {
        try {
            action.run();
        } catch (IllegalStateException e) {
            System.out.println(what + "   REFUSED: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SinglyLinkedList list = new SinglyLinkedList();
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
        System.out.println();

        int at = list.search(25);
        System.out.println("search(25) -> position " + at + "   steps=" + list.lastOperations());
        at = list.search(99);
        System.out.println("search(99) -> " + at + " (not found)   steps=" + list.lastOperations());
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

        tryIt("insertAfter(99, 1)", () -> list.insertAfter(99, 1));
        tryIt("insertBefore(99, 1)", () -> list.insertBefore(99, 1));
        tryIt("deleteBefore(25)", () -> list.deleteBefore(25));
        tryIt("deleteAfter(25)", () -> list.deleteAfter(25));
        tryIt("deleteValue(99)", () -> list.deleteValue(99));
        list.deleteAtBeginning();
        tryIt("deleteAtBeginning() on empty", () -> list.deleteAtBeginning());
        tryIt("deleteAtEnd() on empty", () -> list.deleteAtEnd());
        System.out.println("search on empty -> " + list.search(1));
        System.out.println("display empty: " + list.displayForward());
        System.out.println();

        for (int i = 1; i <= SinglyLinkedList.MAX_NODES; i++) {
            list.insertAtBeginning(i);
        }
        tryIt("insert into a full list", () -> list.insertAtEnd(99));
    }
}