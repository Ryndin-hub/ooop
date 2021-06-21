import java.util.ArrayList;

public final class Executor implements Runnable {
    private final BlockingQueue<Task> queue;
    private volatile boolean running = false;
    private final ArrayList<Integer> queueNum;

    public Executor(BlockingQueue<Task> _queue, ArrayList<Integer> _queueNum) {
        queue = _queue;
        queueNum = _queueNum;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Task task = queue.dequeue();
                running = true;
                task.task.run();
                queueNum.remove((Integer) task.taskNum);
                running = false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isReady() {
        return !running;
    }
}