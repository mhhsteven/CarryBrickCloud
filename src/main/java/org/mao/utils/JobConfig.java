package org.mao.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 读取配置类
 *
 * @author mhh
 */
@Component
@PropertySource("classpath:conf/job.properties")
public class JobConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobConfig.class);

    @Value("${job.id}")
    private Integer jobId;

    @Value("${net.master.ip}")
    private String netMasterIp;

    @Value("${net.role}")
    private String netRole;

    @Value("${job.run.interval}")
    private Long jobRunInterval;

    private static JobConfig jobConfig = null;

    public static JobConfig getInstance() {
        if (jobConfig == null) {
            jobConfig = ApplicationContextUtils.getBean(JobConfig.class);
        }
        return jobConfig;
    }

    public Integer getJobId() {
        return jobId;
    }

    public String getNetMasterIp() {
        return netMasterIp;
    }

    public String getNetRole() {
        return netRole;
    }

    public Long getJobRunInterval() {
        return jobRunInterval;
    }
}
