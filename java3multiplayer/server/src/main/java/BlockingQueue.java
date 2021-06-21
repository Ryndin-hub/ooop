import java.util.LinkedList;
import java.util.Queue;

public final class BlockingQueue<Type> {
    private final Queue<Type> queue = new LinkedList<>();
    private final int maxSize;

    public BlockingQueue(int size) {
        maxSize = size;
    }

    public synchronized void enqueue(Type task) throws InterruptedException {
        while (queue.size() == maxSize) {
            wait();
        }
        if (queue.isEmpty()) {
            notifyAll();
        }
        queue.offer(task);
    }

    public synchronized Type dequeue() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        if (queue.size() == maxSize) {
            notifyAll();
        }
        return queue.poll();
    }
}