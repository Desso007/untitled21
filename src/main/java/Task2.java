import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public interface Task2 extends AutoCloseable {
    void start();
    void execute(Runnable runnable);

    @Override
    void close() throws Exception;
}

class FixedThreadPool implements Task2 {
    private final Thread[] threads;
    private final Lock lock = new ReentrantLock();
    private final TaskQueue taskQueue;
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private FixedThreadPool(int threadCount) {
        this.threads = new Thread[threadCount];
        this.taskQueue = new TaskQueue();

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new Worker());
        }
    }

    public static Task2 create(int threadCount) {
        return new FixedThreadPool(threadCount);
    }

    @Override
    public void start() {
        for (Thread thread : threads) {
            thread.start();
        }
    }

    @Override
    public void execute(Runnable runnable) {
        if (shutdown.get()) {
            throw new IllegalStateException("ThreadPool is shutdown");
        }
        taskQueue.addTask(runnable);
    }

    @Override
    public void close() throws Exception {
        shutdown.set(true);
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    private class Worker implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                Runnable task = taskQueue.getTask();
                if (task != null) {
                    try {
                        task.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static class TaskQueue {
        private final Lock lock = new ReentrantLock();
        private Node head;
        private Node tail;

        public void addTask(Runnable task) {
            lock.lock();
            try {
                Node newNode = new Node(task);
                if (head == null) {
                    head = newNode;
                    tail = newNode;
                } else {
                    tail.next = newNode;
                    tail = newNode;
                }
            } finally {
                lock.unlock();
            }
        }

        public Runnable getTask() {
            lock.lock();
            try {
                if (head == null) {
                    return null;
                }

                Runnable task = head.task;
                head = head.next;
                if (head == null) {
                    tail = null;
                }
                return task;
            } finally {
                lock.unlock();
            }
        }

        private static class Node {
            private final Runnable task;
            private Node next;

            public Node(Runnable task) {
                this.task = task;
            }
        }
    }
}

