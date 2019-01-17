package org.mao.task;

/**
 * 任务状态枚举
 *
 * @author mhh
 */
public enum TaskStatusEnum {

    /**
     * 空闲的
     */
    WAIT(0),

    /**
     * 正在运行
     */
    RUNNING(1);

    private Integer status;

    public Integer getStatus() {
        return status;
    }

    private TaskStatusEnum(Integer status) {
        this.status = status;
    }
}
