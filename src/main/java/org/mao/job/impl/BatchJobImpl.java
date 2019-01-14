package org.mao.job.impl;

import com.google.common.collect.Lists;
import org.mao.job.BaseBatchJob;
import org.mao.job.impl.bean.MessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class BatchJobImpl extends BaseBatchJob<MessageDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchJobImpl.class);

    private boolean flag = true;

    private String[] nameArray = new String[]{"小A", "按时发货", "idsg", "浪费", "丢法国", "忙啥呢个", "却无法", "能否", "欧冠", "经恢复"};

    private int age = 0;

    @Override
    public List<MessageDTO> bunch() {
        List<MessageDTO> messageDTOList = Lists.newArrayList();
        if (age < nameArray.length) {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setName(nameArray[age]);
            messageDTO.setAge(++age + 17);
            messageDTO.setBirthday(new Date());
            messageDTOList.add(messageDTO);
        }
        return messageDTOList;
    }

    @Override
    public void process(MessageDTO messageDTO) {
        LOGGER.info("开始处理");
        LOGGER.info("{}", messageDTO);
        LOGGER.info("结束处理");
    }
}
