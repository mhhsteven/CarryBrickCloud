package org.mao.task;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.mao.job.BaseBatchJob;
import org.mao.job.bean.BaseDTO;
import org.mao.utils.AsyncExecuteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class BrickDispatcher<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrickDispatcher.class);

    public ChannelGroup channelGroup;

    private BaseBatchJob<T> batchJob;

    private List<T> dataList;

    private TaskQueue<T> taskQueue;

    public BrickDispatcher(BaseBatchJob<T> batchJob) {
        this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        this.batchJob = batchJob;
        this.taskQueue = new TaskQueue<>();
    }

    public void addChannel(Channel channel) {
        this.channelGroup.add(channel);
    }

    public void removeChannel(Channel channel) {
        this.channelGroup.remove(channel);
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
        LOGGER.info("主任务完成情况{}", batchJob.processOver());
        if (batchJob.processOver()) {
            dataList.remove(0);
            this.doJob(t, null);
        } else {
            LOGGER.info("当前连接数量{}", channelGroup.size());
            for (Channel channel : channelGroup) {
                if (taskQueue.isFree(channel)) {
                    LOGGER.info("分配任务{}给{}", t, channel);
                    dataList.remove(0);
                    taskQueue.add(channel, t);
                    //分配任务
                    BaseDTO<T> baseDTO = new BaseDTO<>();
                    baseDTO.setCode("10000");
                    baseDTO.setMsg("from server");
                    baseDTO.setContent(t);
                    channel.writeAndFlush(baseDTO);
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

    public void doJob(T t, Channel channel) {
        AsyncExecuteUtils.execute("搬砖去吧，少年", new Runnable() {
            @Override
            public void run() {
                batchJob.dispose(t);
                if (channel != null) {
                    BaseDTO<T> baseDTO = new BaseDTO<>();
                    baseDTO.setCode("10000");
                    baseDTO.setMsg("from client");
                    baseDTO.setContent(t);
                    channel.writeAndFlush(baseDTO);
                }
            }
        });
    }

    public void removeTask(Channel channel) {
        taskQueue.remove(channel);
    }
}