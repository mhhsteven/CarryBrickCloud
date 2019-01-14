package org.mao.task;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.mao.job.BaseBatchJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class BrickDispatcher<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrickDispatcher.class);

    private ChannelGroup channelGroup;

    private BaseBatchJob<T> batchJob;

    private List<T> dataList;

    private TaskQueue<T> taskQueue;

    public BrickDispatcher(BaseBatchJob<T> batchJob) {
        this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        this.batchJob = batchJob;
        this.taskQueue = new TaskQueue<>();
    }

    public void dispatcher() {
        if (CollectionUtils.isEmpty(dataList)) {
            dataList = batchJob.bunch();
            //没有数据，并且任务都处理完了
            if (CollectionUtils.isEmpty(dataList)) {
                return;
            }
        }

        T t = dataList.get(0);
        if (batchJob.processOver()) {
            batchJob.dispose(t);
            dataList.remove(0);
        } else {
            for (Channel channel : channelGroup) {
                if (taskQueue.isFree(channel)) {
                    //分配任务
                    dataList.remove(0);
                }
            }
        }
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ie) {
            LOGGER.error("", ie);
        }
        this.dispatcher();
    }
}