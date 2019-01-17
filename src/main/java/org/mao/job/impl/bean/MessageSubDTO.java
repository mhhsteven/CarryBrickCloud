package org.mao.job.impl.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MessageSubDTO implements Serializable {

    private List<BigDecimal> amountList;

    private Map<String, Date> dateMap;

    public List<BigDecimal> getAmountList() {
        return amountList;
    }

    public void setAmountList(List<BigDecimal> amountList) {
        this.amountList = amountList;
    }

    public Map<String, Date> getDateMap() {
        return dateMap;
    }

    public void setDateMap(Map<String, Date> dateMap) {
        this.dateMap = dateMap;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteDateUseDateFormat);
    }
}
