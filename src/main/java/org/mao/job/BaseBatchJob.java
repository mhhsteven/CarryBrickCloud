package org.mao.job;

import org.apache.log4j.PropertyConfigurator;
import org.mao.job.impl.bean.MessageDTO;
import org.mao.task.BrickDispatcher;
import org.mao.task.TaskStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class BaseBatchJob<T extends Serializable> implements IBatchJob<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseBatchJob.class);

    static {
        PropertyConfigurator.configure("CarryBrickCloud/src/main/resources/conf/log4j.properties");
    }

    private TaskStatusEnum statusEnum;

    @PostConstruct
    public void init() {
        this.statusEnum = TaskStatusEnum.WAIT;
    }

    public abstract List<T> bunch();

    public abstract void process(T t);

    public boolean dispose(T t) {
        this.statusEnum = TaskStatusEnum.RUNNING;
        this.process(t);
        this.statusEnum = TaskStatusEnum.WAIT;
        return true;
    }

    public static void main(String[] args) {
        BrickDispatcher<MessageDTO> brickDispatcher = new BrickDispatcher<MessageDTO>();
        brickDispatcher.run();
    }

    public boolean processOver() {
        return this.statusEnum == TaskStatusEnum.WAIT;
    }

    public Class getRealType() {
        // 获取当前new的对象的泛型的父类类型
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        // 获取第一个类型参数的真实类型
        Class clazz = (Class<T>) pt.getActualTypeArguments()[0];
        return clazz;
    }
}
