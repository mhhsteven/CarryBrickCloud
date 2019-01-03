package org.mao.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.Serializable;
import java.util.Date;

public class MessageDTO implements Serializable {

    private String msg;

    private Integer code;

    private Date time;

    private MessageSubDTO subMsg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public MessageSubDTO getSubMsg() {
        return subMsg;
    }

    public void setSubMsg(MessageSubDTO subMsg) {
        this.subMsg = subMsg;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteDateUseDateFormat);
    }
}
