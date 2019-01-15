package org.mao.job;

import org.apache.log4j.PropertyConfigurator;
import org.mao.net.HttpClient;
import org.mao.net.HttpServer;
import org.mao.task.BrickDispatcher;
import org.mao.task.TaskRoleEnum;
import org.mao.task.TaskStatusEnum;
import org.mao.utils.AsyncExecuteUtils;
import org.mao.utils.IpAddressUtils;
import org.mao.utils.JobConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.PostConstruct;
import java.util.List;

public class BaseBatchJob<T> implements IBatchJob<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseBatchJob.class);

    static {
        PropertyConfigurator.configure("CarryBrickCloud/src/main/resources/conf/log4j.properties");
    }

    private TaskStatusEnum statusEnum;

    private HttpServer httpServer;

    private String host = "127.0.0.1";
    private Integer port = 60000;

    @PostConstruct
    public void init() {
        port = port + JobConfig.getInstance().getJobId();
        host = JobConfig.getInstance().getNetMasterIp();
    }

    @Override
    public List<T> bunch() {
        return null;
    }

    @Override
    public void process(T t) {

    }

    public boolean dispose(T t) {
        LOGGER.info("dispose1: {}", t);
        this.statusEnum = TaskStatusEnum.RUNNING;
        LOGGER.info("dispose2: {}", t);
        this.process(t);
        LOGGER.info("dispose3: {}", t);
        this.statusEnum = TaskStatusEnum.WAIT;
        LOGGER.info("dispose4: {}", t);
        return true;
    }

    public static void main(String[] args) {
        AbstractApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        BaseBatchJob batchJob = (BaseBatchJob) context.getBean("job");
        batchJob.run();
    }

    private void run() {
        BrickDispatcher brickDispatcher = new BrickDispatcher(this);
        if (this.isMaster()) {
            this.startServer(brickDispatcher);
            this.statusEnum = TaskStatusEnum.WAIT;
            AsyncExecuteUtils.execute("调度器任务", new Runnable() {
                @Override
                public void run() {
                    brickDispatcher.dispatcher();
                }
            });
        } else {
            this.startClient(brickDispatcher);
        }
    }

    private void startServer(final BrickDispatcher brickDispatcher) {
        AsyncExecuteUtils.execute("服务端", new Runnable() {
            @Override
            public void run() {
                try {
                    LOGGER.info("以master方式启动，监听端口:{}", port);
                    httpServer = new HttpServer();
                    httpServer.start(port, brickDispatcher);
                } catch (Exception e) {
                    LOGGER.error("以master方式启动失败", e);
                }
            }
        });
    }

    private void startClient(final BrickDispatcher brickDispatcher) {
        AsyncExecuteUtils.execute("客户端", new Runnable() {
            @Override
            public void run() {
                try {
                    LOGGER.info("以slave方式启动，连接ip:{},端口:{}", host, port);
                    HttpClient client = new HttpClient();
                    client.connect(host, port, brickDispatcher);
                } catch (Exception e) {
                    LOGGER.error("以slave方式启动失败", e);
                }
            }
        });
    }

    private boolean isMaster() {
        boolean isLocal = IpAddressUtils.isLocal(host);
        if (isLocal && TaskRoleEnum.MASTER.getRole().equals(JobConfig.getInstance().getNetRole())) {
            return true;
        }
        return false;
    }

    public boolean processOver() {
        return this.statusEnum == TaskStatusEnum.WAIT;
    }
}
