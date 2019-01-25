package org.mao.task;

import io.netty.channel.Channel;
import org.mao.job.BaseBatchJob;
import org.mao.notify.NotifyAdapter;
import org.mao.utils.ApplicationContextUtils;
import org.mao.utils.AsyncExecuteUtils;

import java.io.Serializable;

/**
 * 负责数据的具体执行逻辑
 *
 * @param <T>
 * @author mhh
 */
public class BrickExecutor<T extends Serializable> {

    private BaseBatchJob<T> batchJob;

    private Channel channel;

    private boolean isMaster;

    private boolean isMain;

    public BrickExecutor() {
        batchJob = ApplicationContextUtils.getBean(BaseBatchJob.class);
        this.isMaster = true;
        this.isMain = true;
    }

    public BrickExecutor(Channel channel) {
        this.channel = channel;
        this.isMaster = true;
        this.isMain = false;
    }

    public BrickExecutor(boolean isMaster, Channel channel) {
        batchJob = ApplicationContextUtils.getBean(BaseBatchJob.class);
        this.channel = channel;
        this.isMaster = isMaster;
        this.isMain = true;
    }

    public void exec(T t) {
        AsyncExecuteUtils.execute("执行单元", () -> {
            if (isMaster) {
                if (isMain) {
                    batchJob.dispose(t);
                    TaskQueue taskQueue = ApplicationContextUtils.getBean(TaskQueue.class);
                    taskQueue.done(BrickExecutorFactory.newLocalExecutor());
                } else {
                    NotifyAdapter notifyAdapter = new NotifyAdapter(channel);
                    notifyAdapter.sendTask(t);
                }
            } else {
                batchJob.dispose(t);
                NotifyAdapter notifyAdapter = new NotifyAdapter(channel);
                notifyAdapter.doneTask();
            }
        });
    }

    @Override
    public boolean equals(Object executor) {
        if (!(executor instanceof BrickExecutor)) {
            return false;
        }
        BrickExecutor brickExecutor = (BrickExecutor) executor;
        if (isMain && brickExecutor.isMain) {
            return true;
        }
        if (!isMain && !brickExecutor.isMain) {
            return channel.equals(brickExecutor.channel);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (isMain) {
            return batchJob.hashCode();
        } else {
            return channel.hashCode();
        }
    }

    @Override
    public String toString() {
        return isMain ? "local job" : new StringBuilder().append("remote job: {").append(channel.id()).append("}").toString();
    }
}
