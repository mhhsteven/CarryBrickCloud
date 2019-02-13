package org.mao.notify;

import java.io.Serializable;

/**
 * 主从间的通知接口
 *
 * @param <T>
 * @author mhh
 */
public interface INotify<T extends Serializable> {

    /**
     * 发送处理数据消息
     *
     * @param baseDTO
     */
    public void sendTask(T t);

    /**
     * 发送数据处理完成消息
     */
    public void doneTask();
}
