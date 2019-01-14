package org.mao.job.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.Serializable;

/**
 * 消息DTO基类
 *
 * @author mhh
 */
public class BaseDTO<T> implements Serializable {

    private String code;

    private String msg;

    private T content;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteDateUseDateFormat);
    }
}
