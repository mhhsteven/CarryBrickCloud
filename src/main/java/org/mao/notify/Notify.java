package org.mao.notify;

import io.netty.channel.Channel;

/**
 * 主从间的通知接口
 *
 * @param <T>
 * @author mhh
 */
public class Notify<BaseDTO> implements INotify<BaseDTO> {

    private Channel channel;

    public Notify(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void send(BaseDTO baseDTO) {
        channel.writeAndFlush(baseDTO);
    }
}
