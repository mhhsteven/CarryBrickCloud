package org.mao.job;

import org.mao.task.BrickDispatcher;
import org.mao.utils.ApplicationContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

/**
 * 任务处理基础类
 *
 * @param <T>
 * @author mhh
 */
public abstract class BaseBatchJob<T extends Serializable> implements IBatchJob<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseBatchJob.class);

    public static void main(String[] args) {
        ApplicationContextUtils.getBean(BrickDispatcher.class).run();
    }

    /**
     * 处理T(t)，调用实现类的具体处理方法process(t)
     *
     * @param t
     * @return
     */
    public void dispose(T t) {
        this.process(t);
    }

    /**
     * 为了获取实现类上定义的泛型的具体类
     *
     * @return
     */
    public Class getRealType() {
        // 获取当前new的对象的泛型的父类类型
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        // 获取第一个类型参数的真实类型
        return (Class<T>) pt.getActualTypeArguments()[0];
    }
}
