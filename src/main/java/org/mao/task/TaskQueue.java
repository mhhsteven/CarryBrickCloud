package org.mao.task;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskQueue.class);

    /**
     * 需要处理的数据列表
     */
    private List<T> sourceDataList;

    /**
     * 待处理的数据索引
     */
    private List<Integer> prepareIndexList;

    /**
     * 处理中的数据索引
     */
    private List<Integer> processIndexList;

    /**
     * 数据索引 - 任务状态
     */
    private Map<Integer, TaskStatusEnum> taskStatusMap;

    /**
     * 执行者 - 数据索引
     */
    private Map<BrickExecutor, Integer> brickTaskIndexMap;

    private void reset() {
        Integer dataCount = sourceDataList == null ? 0: sourceDataList.size();
        this.prepareIndexList = Lists.newArrayListWithCapacity(dataCount);
        for (int i = 0; i < dataCount; i++) {
            this.prepareIndexList.add(i);
        }
        this.processIndexList = Lists.newArrayList();
        this.taskStatusMap = Maps.newHashMap();
        this.brickTaskIndexMap = Maps.newHashMap();
    }

    public void fillData(List<T> sourceDataList) {
        this.sourceDataList = sourceDataList;
        this.reset();
    }

    public boolean hasNext() {
        return CollectionUtils.isNotEmpty(prepareIndexList) || CollectionUtils.isNotEmpty(processIndexList);
    }

    /**
     * 从待处理列表中返回一个未处理数据的索引
     *
     * @return
     */
    private Integer next() {
        Integer index = null;
        if (CollectionUtils.isNotEmpty(prepareIndexList)) {
            index = prepareIndexList.remove(0);
            processIndexList.add(index);
        }
        return index;
    }

    public T pending(BrickExecutor executor) {
        T t = null;
        Integer index = this.next();
        if (index != null) {
            t = sourceDataList.get(index);
            taskStatusMap.put(index, TaskStatusEnum.RUNNING);
            brickTaskIndexMap.put(executor, index);
        }
        return t;
    }

    public void recycle(BrickExecutor executor) {
        Integer index = brickTaskIndexMap.remove(executor);
        taskStatusMap.remove(index);
        processIndexList.remove(index);
        prepareIndexList.add(index);
    }

    public void done(BrickExecutor executor) {
        Integer index = brickTaskIndexMap.remove(executor);
        taskStatusMap.remove(index);
        processIndexList.remove(index);
    }

    public boolean isFree() {
        Integer index = brickTaskIndexMap.get(BrickExecutorFactory.newLocalExecutor());
        TaskStatusEnum statusEnum = taskStatusMap.get(index);
        return this.isFree(statusEnum);
    }

    public boolean isFree(Channel channel) {
        Integer index = brickTaskIndexMap.get(BrickExecutorFactory.newRemoteExecutor(channel));
        TaskStatusEnum statusEnum = taskStatusMap.get(index);
        return this.isFree(statusEnum);
    }

    private boolean isFree(TaskStatusEnum statusEnum) {
        return statusEnum == null || statusEnum == TaskStatusEnum.WAIT;
    }
}
