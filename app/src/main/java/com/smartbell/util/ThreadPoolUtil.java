package com.smartbell.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolUtil {

    // cpu核个数
    private final static int DEFAULT_CPU_CORE_NUM = 2;
    // 单个cpu核允许核心线程数
    private final static int DEFAULT_PER_CPU_THREADNUM = 2;
    // 单个cpu核允许最多线程数
    private final static int DEFAULT_PER_CPU_MAX_THREADNUM = 4;
    // 任务队列容量
    private final static int DEFAULT_TASK_QUEUE_CAPACITY = 1024;

    // 线程池核心线程数
    private static int CORE_POOL_SIZE = DEFAULT_CPU_CORE_NUM * DEFAULT_PER_CPU_THREADNUM;
    // 线程池最大线程数
    private static int MAX_POOL_SIZE = DEFAULT_CPU_CORE_NUM * DEFAULT_PER_CPU_MAX_THREADNUM;
    // 空闲线程保留时间
    private final static int KEEP_ALIVE_TIME = 1;

    private ThreadPoolExecutor threadPool;
    private ArrayBlockingQueue<Runnable> arrayBlockingQueue;

    private static class SingletonHolder {
        private static final ThreadPoolUtil INSTANCE = new ThreadPoolUtil();
    }

    private ThreadPoolUtil() {
        int cpuCoreNum = CPUUtil.getNumCores();
        if(cpuCoreNum > 0) {
            CORE_POOL_SIZE = cpuCoreNum * DEFAULT_PER_CPU_THREADNUM;
            MAX_POOL_SIZE = cpuCoreNum * DEFAULT_PER_CPU_MAX_THREADNUM;
        }

        arrayBlockingQueue = new ArrayBlockingQueue<Runnable>(DEFAULT_TASK_QUEUE_CAPACITY);
        ThreadFactory threadFactory = new ThreadFactory() {
            private AtomicInteger mCount = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "YK-Thread-" + mCount.getAndIncrement());
            }
        };

        threadPool = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                arrayBlockingQueue,
                threadFactory,
                new ThreadPoolExecutor.DiscardPolicy()
        );
    }

    public static ThreadPoolExecutor getThreadPool() {
        return SingletonHolder.INSTANCE.threadPool;
    }

    public void clearBlockingQueue() {
        if ((arrayBlockingQueue != null) && (!arrayBlockingQueue.isEmpty())) {
            arrayBlockingQueue.clear();
        }
    }

}
