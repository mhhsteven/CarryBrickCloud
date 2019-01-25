package org.mao.task;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 任务队列<br>
 * 1. 数据DTO列表（待处理）<br>
 * 2. 列表（待处理）拿一个放到（处理中）列表<br>
 * 3. master处理完了，从（处理中）列表中移除；slave处理完了，通知master，从（处理中）列表中移除<br>
 * 4. 客户端断开连接时，从（处理中）列表中重新移动到（待处理）列表<br>
 *
 * @param <T>
 * @author mhh
 */
@Service
public class TaskQueue<T extends Serializable> {

    private List<T> sourceDataList;

    private List<T> processDataList;

    private Map<BrickExecutor, T> channelDataMap;

    private Map<BrickExecutor, TaskStatusEnum> channelTaskStatusMap;

    public TaskQueue() {
        this.channelDataMap = Maps.newHashMap();
        this.channelTaskStatusMap = Maps.newHashMap();
        this.processDataList = Lists.newArrayList();
    }

    public void fillData(List<T> sourceDataList) {
        this.sourceDataList = sourceDataList;
    }

    public boolean hasNext() {
        return CollectionUtils.isNotEmpty(sourceDataList);
    }

    /**
     * 从数据列表中返回一个未处理的数据
     *
     * @return
     */
    private T next() {
        T t = null;
        if (this.hasNext()) {
            t = sourceDataList.remove(0);
            processDataList.add(t);
        }
        return t;
    }

    public T pending(BrickExecutor executor) {
        T t = this.next();
        if (t != null) {
            channelDataMap.put(executor, t);
            channelTaskStatusMap.put(executor, TaskStatusEnum.RUNNING);
        }
        return t;
    }

    public void recycle(BrickExecutor executor) {
        T t = channelDataMap.get(executor);
        processDataList.remove(t);
        sourceDataList.add(t);
        channelTaskStatusMap.remove(executor);
    }

    public void done(BrickExecutor executor) {
        T t = channelDataMap.get(executor);
        processDataList.remove(t);
        channelTaskStatusMap.remove(executor);
    }

    public boolean isFree() {
        TaskStatusEnum statusEnum = channelTaskStatusMap.get(BrickExecutorFactory.newLocalExecutor());
        return this.isFree(statusEnum);
    }

    public boolean isFree(Channel channel) {
        TaskStatusEnum statusEnum = channelTaskStatusMap.get(BrickExecutorFactory.newRemoteExecutor(channel));
        return this.isFree(statusEnum);
    }

    private boolean isFree(TaskStatusEnum statusEnum) {
        return statusEnum == null || statusEnum == TaskStatusEnum.WAIT;
    }
}
