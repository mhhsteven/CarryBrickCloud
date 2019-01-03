package org.mao.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MessageSubDTO implements Serializable {

    private List<String> nameList;

    private Map<Integer, String> keyMap;

    public List<String> getNameList() {
        return nameList;
    }

    public void setNameList(List<String> nameList) {
        this.nameList = nameList;
    }

    public Map<Integer, String> getKeyMap() {
        return keyMap;
    }

    public void setKeyMap(Map<Integer, String> keyMap) {
        this.keyMap = keyMap;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteDateUseDateFormat);
    }
}
