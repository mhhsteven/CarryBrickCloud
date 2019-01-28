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

    /**
     * 处理数据前调用
     */
    public void before();

    /**
     * 处理数据后调用
     */
    public void after();
}
