package org.grain.tools.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author laowu
 * @version 5/10/2019 6:22 PM
 */
public class CustomThreadPool {


    public static boolean is_daemon_false = false;
    public static int maximum_pool_size = 128;
    public static int core_pool_size = 8;
    public static long keep_alive_time = 3600L;

    public static ThreadFactory createThreadFactory(String namePrefix, boolean isDaemon, int priority) {
        return new CustomThreadFactoryBuilder()
                .setNamePrefix(namePrefix)
                .setDaemon(isDaemon)
                .setPriority(priority)
                .build();
    }

    public static ThreadPoolExecutor createThreadPool(String namePrefix) {
        return createThreadPool(namePrefix, core_pool_size, maximum_pool_size, is_daemon_false, Thread.NORM_PRIORITY);
    }

    public static ThreadPoolExecutor createThreadPool(String namePrefix, int corePoolSize, int maximumPoolSize, boolean isDaemon, long keepAliveTime) {
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(102400),
                createThreadFactory(namePrefix, isDaemon, Thread.NORM_PRIORITY),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
