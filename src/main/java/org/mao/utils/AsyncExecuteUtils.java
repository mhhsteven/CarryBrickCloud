package org.mao.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步执行工具
 *
 * @author mhh
 */
public class AsyncExecuteUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncExecuteUtils.class);

    private static final int CORE_POOL_SIZE = 10;

    private static final int MAXIMUM_POOL_SIZE = 50;

    private static final long KEEP_ALIVE_TIME = 10;

    private static ThreadPoolExecutor service;

    /**
     * 异步执行方法
     *
     * @param taskName
     * @param runnable
     */
    public static void execute(String taskName, Runnable runnable) {
        try {
            if (service == null) {
                ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat(taskName + "-pool-%d").build();
                service = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), namedThreadFactory);
            }
            service.submit(runnable);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }
}
