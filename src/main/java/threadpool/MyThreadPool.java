package threadpool;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyThreadPool implements Executor {

    private static final Runnable SHUTDOWN_TASK = () -> {};

    private final BlockingQueue<Runnable> queue = new LinkedTransferQueue<>();
    private final AtomicBoolean started = new AtomicBoolean();
    private final AtomicBoolean shutdown = new AtomicBoolean();
    private final AtomicInteger numActiveThreads = new AtomicInteger();
    private final Set<Thread> threads = new HashSet<>();
    private final Lock threadsLock = new ReentrantLock();

    public MyThreadPool(int numThreads) {
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                for (;;) {
                    try {
                        final Runnable task = queue.take();
                        if (task == SHUTDOWN_TASK) {
                            break;
                        } else {
                            task.run();
                        }
                    } catch (Throwable t) {
                        if (!(t instanceof InterruptedException)) {
                            System.err.println("Unexpected exception: ");
                            t.printStackTrace();
                        }
                    }
                }

                System.err.println("Shutting thread '" + Thread.currentThread().getName() + '\'');
            });
        }
    }

    @Override
    public void execute(Runnable command) {
        if (started.compareAndSet(false, true)) {
            for (Thread thread : threads) {
                thread.start();
            }
        }

        if (shutdown.get()) {
            throw new RejectedExecutionException();
        }

        queue.add(command);
        addThreadIfNecessary();

        if (shutdown.get()) {
            queue.remove(command);
        }
    }

    // note 왜 ActiveThreads 갯수가 실제 threads 갯수 보다 많을 때 쓰레드가 추가되어야 하는걸까?
    private void addThreadIfNecessary() {
        if (numActiveThreads.get() >=  threads.size()) {
            threadsLock.lock();
            try {
                if (numActiveThreads.get() >= threads.size()) {
                    final Thread newThread = new Thread();
                    threads.add(newThread);
                    newThread.start();
                }
            } finally {
                threadsLock.unlock();
            }
        }
    }

    public void shutdown() {
        if (shutdown.compareAndSet(false, true)) {
            for (int i = 0; i < threads.length; i++) {
                queue.add(SHUTDOWN_TASK);
            }
        }

        for (Thread thread : threads) {
            do {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    // Do not propagate to prevent incomplete shutdown.
                }
            } while (thread.isAlive());
        }
    }
}

