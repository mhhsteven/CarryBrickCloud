package org.mao.task;

import io.netty.channel.Channel;

import java.util.Map;

/**
 * 任务队列
 *
 * @param <T>
 * @author mhh
 */
public class TaskQueue<T> {

    private Map<Channel, T> channelDataMap;

    private Map<Channel, TaskStatusEnum> channelTaskStatusMap;

    public void add(Channel channel, T t) {
        channelDataMap.put(channel, t);
        channelTaskStatusMap.put(channel, TaskStatusEnum.RUNNING);
    }

    public boolean isFree(Channel channel) {
        TaskStatusEnum statusEnum = channelTaskStatusMap.get(channel);
        return statusEnum == null || statusEnum == TaskStatusEnum.WAIT;
    }
}
