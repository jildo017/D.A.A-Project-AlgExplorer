package algexplorer.structures;

public class StackDemo {
    public static void main(String[] args) {
        ArrayStack s = new ArrayStack(3);
        s.push(10);
        s.push(20);
        s.push(30);
        System.out.println("After 3 pushes: " + s.display() + " (bottom to top), size="
                + s.size() + ", full=" + s.isFull());

        try {
            s.push(40);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Peek: " + s.peek() + " (size still " + s.size() + ")");
        System.out.println("Popped " + s.pop());
        System.out.println("Popped " + s.pop());
        System.out.println("Popped " + s.pop());
        System.out.println("Now empty = " + s.isEmpty());

        try {
            s.pop();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        try {
            new ArrayStack(0);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}