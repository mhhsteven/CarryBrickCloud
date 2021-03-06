package org.mao.job.impl.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.Serializable;
import java.util.Date;

/**
 * demo
 *
 * @author mhh
 */
public class MessageDTO implements Serializable {

    private String name;

    private Integer age;

    private Date birthday;

    private MessageSubDTO subMsg;

    private String processBy;

    private boolean processStatus;

    public MessageDTO() {
        this.processStatus = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public MessageSubDTO getSubMsg() {
        return subMsg;
    }

    public void setSubMsg(MessageSubDTO subMsg) {
        this.subMsg = subMsg;
    }

    public String getProcessBy() {
        return processBy;
    }

    public void setProcessBy(String processBy) {
        this.processBy = processBy;
    }

    public boolean isProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(boolean processStatus) {
        this.processStatus = processStatus;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteDateUseDateFormat);
    }
}
