package org.mao.job.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.mao.job.BaseBatchJob;
import org.mao.job.impl.bean.MessageDTO;
import org.mao.job.impl.bean.MessageSubDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * job实现类(demo)
 *
 * @author mhh
 */
@Service
public class BatchJobImpl extends BaseBatchJob<MessageDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchJobImpl.class);

    private int page = 0;

    private Random random = new Random();

    private List<MessageDTO> messageDTOList = Lists.newArrayList();

    @Override
    public List<MessageDTO> bunch() {
        List<MessageDTO> messageDTOSubList = Lists.newArrayList();
        if (page < 3) {
            int maxCount = random.nextInt(10) + 5;
            for (int i = 0; i < maxCount; i++) {
                MessageDTO messageDTO = new MessageDTO();
                String name = "任务" + (page * 10 + i + 1);
                messageDTO.setName(name);
                messageDTO.setAge(18);
                messageDTO.setBirthday(new Date());

                MessageSubDTO subMsg = new MessageSubDTO();
                List<BigDecimal> amountList = Lists.newArrayList(BigDecimal.valueOf(1), BigDecimal.valueOf(1.1));
                subMsg.setAmountList(amountList);
                Map<String, Date> dateMap = Maps.newHashMap();
                dateMap.put(name, new Date());
                subMsg.setDateMap(dateMap);
                //messageDTO.setSubMsg(subMsg);

                messageDTOSubList.add(messageDTO);
            }
            page++;
        }
        messageDTOList.addAll(messageDTOSubList);
        return messageDTOSubList;
    }

    @Override
    public void process(MessageDTO messageDTO) {
        LOGGER.info("开始处理: {}", messageDTO);
        int delay = random.nextInt(5) + 5;
        try {
            Thread.sleep(delay * 100L);
            messageDTO.setProcessBy("master");
            messageDTO.setProcessStatus(true);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        LOGGER.info("结束处理: {}", messageDTO);
    }

    @Override
    public void before() {
        LOGGER.info("start........................................");
    }

    @Override
    public void after() {
        LOGGER.info("本次数据处理结果：");
        messageDTOList.stream().forEach(dto -> LOGGER.info("{}", dto));
    }
}
