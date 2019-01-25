package org.mao.task;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.mao.job.BaseBatchJob;
import org.mao.net.HttpClient;
import org.mao.net.HttpServer;
import org.mao.utils.AsyncExecuteUtils;
import org.mao.utils.IpAddressUtils;
import org.mao.utils.JobConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * 任务调度器，只有master会执行：负责自己处理数据，并且分配数据给从任务；slave只负责连接上master
 *
 * @param <T>
 * @author mhh
 */
@Service
public class BrickDispatcher<T extends Serializable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrickDispatcher.class);

    private ChannelGroup channelGroup;

    @Autowired
    private BaseBatchJob<T> batchJob;

    @Autowired
    private TaskQueue<T> taskQueue;

    /**
     * 配置中master的地址
     */
    private String host;

    /**
     * 配置中的master的端口
     */
    private Integer port = 60000;

    public BrickDispatcher() {
        port = port + JobConfig.getInstance().getJobId();
        host = JobConfig.getInstance().getNetMasterIp();
        this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    /**
     * master启动服务器监听，开始调度
     * slave启动客户端连接服务器
     */
    public void run() {
        if (this.isMaster()) {
            this.startServer();
            AsyncExecuteUtils.execute("调度器任务", () -> {
                try {
                    dispatcher();
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            });
        } else {
            this.startClient();
        }
    }

    public void addChannel(Channel channel) {
        this.channelGroup.add(channel);
    }

    public void removeChannel(Channel channel) {
        this.channelGroup.remove(channel);
    }

    private void dispatcher() {
        while (taskQueue.hasNext()) {
            BrickExecutor<T> executor = this.nextExecutor();
            Long delay = 1000L;
            if (executor != null) {
                T t = taskQueue.pending(executor);
                LOGGER.info("分配任务{}给{}", t, executor);
                executor.exec(t);
                delay = 0L;
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ie) {
                LOGGER.error("", ie);
                Thread.currentThread().interrupt();
            }
        }
        LOGGER.info("=============================================");
        taskQueue.fillData(batchJob.bunch());
        if (taskQueue.hasNext()) {
            this.dispatcher();
        }
    }

    /**
     * 获取下一个可搬砖的工人
     *
     * @return
     */
    private BrickExecutor<T> nextExecutor() {
        BrickExecutor<T> executor = null;
        if (taskQueue.isFree()) {
            executor = BrickExecutorFactory.newLocalExecutor();
        } else {
            for (Channel channel : channelGroup) {
                if (taskQueue.isFree(channel)) {
                    executor = BrickExecutorFactory.newRemoteExecutor(channel);
                }
            }
        }
        return executor;
    }

    private boolean isMaster() {
        boolean isLocal = IpAddressUtils.isLocal(host);
        return isLocal && TaskRoleEnum.MASTER.getRole().equals(JobConfig.getInstance().getNetRole());
    }

    /**
     * 启动服务端监听
     */
    private void startServer() {
        AsyncExecuteUtils.execute("服务端", () -> {
            try {
                LOGGER.info("以master方式启动，监听端口:{}", port);
                HttpServer httpServer = new HttpServer();
                httpServer.start(port);
            } catch (Exception e) {
                LOGGER.error("以master方式启动失败", e);
            }
        });
    }

    /**
     * 启动客户端连接
     */
    private void startClient() {
        AsyncExecuteUtils.execute("客户端", () -> {
            try {
                LOGGER.info("以slave方式启动，连接ip:{}，端口:{}", host, port);
                HttpClient client = new HttpClient();
                client.connect(host, port);
            } catch (Exception e) {
                LOGGER.error("以slave方式启动失败", e);
            }
        });
    }
}