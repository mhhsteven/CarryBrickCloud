package org.mao.job;

import java.util.List;

/**
 * 批量处理任务接口
 *
 * @param <T>
 * @author mhh
 */
public interface IBatchJob<T> {

    /**
     * 抓取一批数据
     *
     * @return
     */
    public List<T> bunch();

    /**
     * 处理bunch()中抓取的每一个数据
     *
     * @param t
     */
    public void process(T t);
}
