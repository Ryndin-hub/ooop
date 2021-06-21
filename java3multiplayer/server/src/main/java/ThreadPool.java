import java.util.ArrayList;
import java.util.List;

public final class ThreadPool {
    private final BlockingQueue<Task> queue;
    private final ArrayList<Integer> queueNum;
    private final List<Thread> threads;
    private final List<Executor> executors;
    private final int maxThreads;

    public ThreadPool(int _queueSize, int _maxThreads) {
        queue = new BlockingQueue<>(_queueSize);
        queueNum = new ArrayList<>();
        threads = new ArrayList<>();
        executors = new ArrayList<>();
        maxThreads = _maxThreads;
    }

    public void execute(Task task) {
        try {
            if (queueNum.contains(task.taskNum)) return;
            if (!freeThread() && executors.size() < maxThreads) {
                Executor executor = new Executor(queue,queueNum);
                Thread thread = new Thread(executor);
                executors.add(executor);
                threads.add(thread);
                thread.start();
            }
            queueNum.add(task.taskNum);
            queue.enqueue(task);
        } catch (Exception ignored) {
        }
    }

    private boolean freeThread() {
        for (int i = 0; i < executors.size(); i++){
            if (executors.get(i).isReady()) return true;
        }
        return false;
    }
}