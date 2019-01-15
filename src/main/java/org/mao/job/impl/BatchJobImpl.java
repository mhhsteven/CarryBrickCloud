package org.mao.job.impl;

import com.google.common.collect.Lists;
import org.mao.job.BaseBatchJob;
import org.mao.job.impl.bean.MessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class BatchJobImpl extends BaseBatchJob<MessageDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchJobImpl.class);

    private boolean flag = true;

    private String[] nameArray = new String[]{"任务1", "任务2", "任务3", "任务4", "任务5", "任务6", "任务7", "任务8", "任务9", "任务10"};

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
        LOGGER.info("开始处理: {}", messageDTO);
        Random random = new Random();
        int delay = random.nextInt(5) + 5;
        try {
            Thread.sleep(delay * 1000);
        } catch (Exception e) {

        }
        LOGGER.info("结束处理");
    }
}