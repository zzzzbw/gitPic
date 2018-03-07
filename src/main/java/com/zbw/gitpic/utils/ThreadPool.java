package com.zbw.gitpic.utils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zbw
 * @create 2018/3/5 16:59
 */
public class ThreadPool {

    private static final ExecutorService SERVICE = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new DefaultThreadFactory());

    public static ExecutorService getInstance() {
        return SERVICE;
    }

    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager var1 = System.getSecurityManager();
            this.group = var1 != null ? var1.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable var1) {
            Thread var2 = new Thread(this.group, var1, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
            if (var2.isDaemon()) {
                var2.setDaemon(false);
            }

            if (var2.getPriority() != 5) {
                var2.setPriority(5);
            }

            return var2;
        }
    }
}
