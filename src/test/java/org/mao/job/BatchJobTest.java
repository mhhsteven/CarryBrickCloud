package org.mao.job;

import com.google.common.collect.Lists;
import org.apache.log4j.PropertyConfigurator;
import org.mao.job.bean.BaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class BatchJobTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchJobTest.class);

    static {
        PropertyConfigurator.configure("CarryBrickCloud/src/main/resources/conf/log4j.properties");
    }

    public static void main(String[] args) {
        BatchJobTest batchJob = new BatchJobTest();
        //启动服务
        //选一批数据
        //给儿子分配任务
        //接受任务处理结果
        //再选一批数据

        batchJob.bunch().forEach(o -> batchJob.process(o));
    }

    private List<BaseDTO<String>> bunch() {
        List<BaseDTO<String>> messageDTOList = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            BaseDTO<String> messageDTO = new BaseDTO<String>();
            messageDTO.setMsg("侬一扎，吾一扎，艾被妹妹切一扎");
            messageDTO.setCode("10000");
            messageDTO.setContent("Emma");

            messageDTOList.add(messageDTO);
        }
        return messageDTOList;
    }

    private void process(BaseDTO<String> messageDTO) {
        try {
            Random random = new Random();
            int consumingTime = random.nextInt(2000);
            LOGGER.info("batch{}开始处理DTO:{}", consumingTime, messageDTO);
            Thread.sleep(consumingTime);
            LOGGER.info("batch{}结束处理DTO:{}", consumingTime, messageDTO);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }
}
