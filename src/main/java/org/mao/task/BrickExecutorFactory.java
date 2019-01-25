package org.mao.task;

import io.netty.channel.Channel;

import java.io.Serializable;

/**
 * 搬砖工人工厂
 *
 * @author mhh
 */
public class BrickExecutorFactory {

    public static <T extends Serializable> BrickExecutor newLocalExecutor() {
        return new BrickExecutor<T>();
    }

    public static <T extends Serializable> BrickExecutor newRemoteExecutor(Channel channel) {
        return new BrickExecutor<T>(channel);
    }

    public static <T extends Serializable> BrickExecutor newSlaveExecutor(Channel channel) {
        return new BrickExecutor<T>(false, channel);
    }
}
