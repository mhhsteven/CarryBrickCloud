package org.mao.task;

/**
 * 任务处理角色
 *
 * @author mhh
 */
public enum TaskRoleEnum {

    /**
     * 主
     */
    MASTER("master"),

    /**
     * 从
     */
    SLAVE("slave");

    private String role;

    public String getRole() {
        return role;
    }

    private TaskRoleEnum(String role) {
        this.role = role;
    }
}
