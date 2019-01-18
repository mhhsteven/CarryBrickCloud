package org.mao.utils;

import org.apache.log4j.PropertyConfigurator;
import org.mao.job.BaseBatchJob;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 应用上下文工具类
 *
 * @author mhh
 */
public class ApplicationContextUtils {

    private static AbstractApplicationContext context;

    static {
        PropertyConfigurator.configure("CarryBrickCloud/src/main/resources/conf/log4j.properties");
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return (T) context.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz){
        return (T) context.getBean(clazz);
    }

    public static Class getGenericsType() {
        BaseBatchJob batchJob = getBean(BaseBatchJob.class);
        return batchJob.getRealType();
    }
}
