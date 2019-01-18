package org.mao.task;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.mao.job.BaseBatchJob;
import org.mao.job.bean.BaseDTO;
import org.mao.net.HttpClient;
import org.mao.net.HttpServer;
import org.mao.utils.AsyncExecuteUtils;
import org.mao.utils.IpAddressUtils;
import org.mao.utils.JobConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 任务调度器，只有master会执行：负责自己处理数据，并且分配数据给从任务；salver只负责连接上master
 *
 * @param <T>
 * @author mhh
 */
@Service
public class BrickDispatcher<T extends Serializable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrickDispatcher.class);

    public ChannelGroup channelGroup;

    @Autowired
    private BaseBatchJob<T> batchJob;

    private List<T> dataList;

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
        this.taskQueue = new TaskQueue<>();
    }

    /**
     * master启动服务器监听，开始调度
     * salver启动客户端连接服务器
     */
    public void run() {
        if (this.isMaster()) {
            this.startServer();
            AsyncExecuteUtils.execute("调度器任务", new Runnable() {
                @Override
                public void run() {
                    dispatcher();
                }
            });
        } else {
            this.startClient();
        }
    }

    private void dispatcher() {
        if (CollectionUtils.isEmpty(dataList)) {
            dataList = batchJob.bunch();
            //没有数据，并且任务都处理完了
            if (CollectionUtils.isEmpty(dataList)) {
                return;
            }
        }

        T t = dataList.get(0);
        if (batchJob.processOver()) {
            dataList.remove(0);
            doJob(t, null);
        } else {
            for (Channel channel : channelGroup) {
                if (taskQueue.isFree(channel)) {
                    LOGGER.info("分配任务{}给{}", t, channel.id());
                    dataList.remove(0);
                    taskQueue.add(channel, t);
                    //分配任务
                    BaseDTO<T> baseDTO = new BaseDTO<T>();
                    baseDTO.setCode("10000");
                    baseDTO.setMsg("from server");
                    baseDTO.setContent(t);
                    channel.writeAndFlush(baseDTO);
                }
            }
        }
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ie) {
            LOGGER.error("", ie);
        }
        this.dispatcher();
    }

    public void addChannel(Channel channel) {
        this.channelGroup.add(channel);
    }

    public void removeChannel(Channel channel) {
        this.channelGroup.remove(channel);
    }

    public void doJob(T t, Channel channel) {
        AsyncExecuteUtils.execute("执行单元", new Runnable() {
            @Override
            public void run() {
                batchJob.dispose(t);
                if (channel != null) {
                    BaseDTO<T> baseDTO = new BaseDTO<>();
                    baseDTO.setCode("20000");
                    baseDTO.setMsg("complete by client");
                    channel.writeAndFlush(baseDTO);
                }
            }
        });
    }

    public void removeTask(Channel channel) {
        taskQueue.remove(channel);
    }

    private boolean isMaster() {
        boolean isLocal = IpAddressUtils.isLocal(host);
        if (isLocal && TaskRoleEnum.MASTER.getRole().equals(JobConfig.getInstance().getNetRole())) {
            return true;
        }
        return false;
    }

    private void startServer() {
        AsyncExecuteUtils.execute("服务端", new Runnable() {
            @Override
            public void run() {
                try {
                    LOGGER.info("以master方式启动，监听端口:{}", port);
                    HttpServer httpServer = new HttpServer();
                    httpServer.start(port);
                } catch (Exception e) {
                    LOGGER.error("以master方式启动失败", e);
                }
            }
        });
    }

    private void startClient() {
        AsyncExecuteUtils.execute("客户端", new Runnable() {
            @Override
            public void run() {
                try {
                    LOGGER.info("以slave方式启动，连接ip:{}，端口:{}", host, port);
                    HttpClient client = new HttpClient();
                    client.connect(host, port);
                } catch (Exception e) {
                    LOGGER.error("以slave方式启动失败", e);
                }
            }
        });
    }
}