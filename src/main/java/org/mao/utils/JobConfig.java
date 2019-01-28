package org.mao.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * 读取配置类
 *
 * @author mhh
 */
public class JobConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobConfig.class);

    private Integer jobId;

    private String netMasterIp;

    private String netRole;

    private Long jobRunInterval;

    private static JobConfig jobConfig = null;

    private JobConfig() {

    }

    public static JobConfig getInstance() {
        if (jobConfig == null) {
            jobConfig = new JobConfig();
            loadConfig();
        }
        return jobConfig;
    }

    private static void loadConfig(InputStream inputStream) throws IOException, IllegalAccessException {
        Properties prop = new Properties();
        prop.load(new InputStreamReader(inputStream, "UTF-8"));

        Field[] fields = JobConfig.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String porName = field.getName();
            String porType = field.getType().toString();

            String val = prop.getProperty(getPropertiesKeyName(porName));
            if (StringUtils.isNotBlank(val)) {
                switch (porType) {
                    case "class java.lang.Integer":
                        field.set(jobConfig, Integer.parseInt(val));
                        break;
                    case "class java.lang.String":
                        field.set(jobConfig, val);
                        break;
                    case "class java.lang.Boolean":
                        field.set(jobConfig, Boolean.valueOf(val));
                        break;
                    case "class java.lang.Double":
                        field.set(jobConfig, Double.valueOf(val));
                        break;
                    case "class java.lang.Byte":
                        field.set(jobConfig, Byte.valueOf(val));
                        break;
                    case "class java.lang.Long":
                        field.set(jobConfig, Long.valueOf(val));
                        break;
                    default:
                        break;
                }
            }
        }

        inputStream.close();
    }

    private static void loadConfig() {
        try {
            InputStream inputStream = JobConfig.class.getResourceAsStream("/conf/job.properties");
            loadConfig(inputStream);
            String path = JobConfig.getInstance().getConfigPath();
            if (StringUtils.isNotBlank(path)) {
                loadConfig(path);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public static void loadConfig(String configPath) {
        try {
            File file = new File(configPath);
            if (file.exists()) {
                InputStream inputStream = new FileInputStream(file);
                loadConfig(inputStream);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    private static String getPropertiesKeyName(String fieldName) {
        char[] charaters = fieldName.toCharArray();
        StringBuilder sb = new StringBuilder(fieldName.length() + 3);
        for (char charater : charaters) {
            if (charater >= 65 && charater <= 90) {
                sb.append(".");
                charater = (char) ((int) charater + 32);
            }
            sb.append(charater);
        }
        return sb.toString();
    }

    private String configPath;

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getNetMasterIp() {
        return netMasterIp;
    }

    public void setNetMasterIp(String netMasterIp) {
        this.netMasterIp = netMasterIp;
    }

    public String getNetRole() {
        return netRole;
    }

    public void setNetRole(String netRole) {
        this.netRole = netRole;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }
}
