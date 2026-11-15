package algexplorer.structures;

public class QueueDemo {
    public static void main(String[] args) {
        System.out.println("=== LINEAR QUEUE (capacity 5) ===");
        LinearQueue lq = new LinearQueue(5);
        for (int v = 10; v <= 50; v += 10) {
            lq.enqueue(v);
        }
        System.out.println("After 5 enqueues: " + lq.slots() + "  front=" + lq.getFrontIndex()
                + " rear=" + lq.getRearIndex());
        System.out.println("Dequeued " + lq.dequeue());
        System.out.println("Dequeued " + lq.dequeue());
        System.out.println("After 2 dequeues: " + lq.slots() + "  front=" + lq.getFrontIndex()
                + " rear=" + lq.getRearIndex() + "  size=" + lq.size());
        System.out.println("isFull() = " + lq.isFull() + ", wasted boxes = " + lq.wastedSlots());
        try {
            lq.enqueue(60);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        System.out.println();
        System.out.println("=== CIRCULAR QUEUE (capacity 5) ===");
        CircularQueue cq = new CircularQueue(5);
        for (int v = 10; v <= 50; v += 10) {
            cq.enqueue(v);
        }
        System.out.println("After 5 enqueues: " + cq.slots() + "  front=" + cq.getFrontIndex()
                + " rear=" + cq.getRearIndex());
        System.out.println("Dequeued " + cq.dequeue());
        System.out.println("Dequeued " + cq.dequeue());
        System.out.println("After 2 dequeues: " + cq.slots() + "  front=" + cq.getFrontIndex()
                + " rear=" + cq.getRearIndex() + "  size=" + cq.size());
        cq.enqueue(60);
        System.out.println("Enqueued 60: " + cq.slots() + "  front=" + cq.getFrontIndex()
                + " rear=" + cq.getRearIndex() + "   (the rear wrapped to the start)");
        cq.enqueue(70);
        System.out.println("Enqueued 70: " + cq.slots() + "  front=" + cq.getFrontIndex()
                + " rear=" + cq.getRearIndex() + "  full=" + cq.isFull());
        try {
            cq.enqueue(80);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Queue order front to rear: " + cq.display());
        StringBuilder out = new StringBuilder("Dequeue everything:");
        while (!cq.isEmpty()) {
            out.append(" ").append(cq.dequeue());
        }
        System.out.println(out);
        try {
            cq.dequeue();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }
}