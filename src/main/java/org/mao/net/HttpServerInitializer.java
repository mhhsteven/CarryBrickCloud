package org.mao.net;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.mao.task.BrickDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.InetSocketAddress;

public class HttpServerInitializer<T extends Serializable> extends ChannelInitializer<SocketChannel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerInitializer.class);

    private BrickDispatcher brickDispatcher;

    private Class clazz;

    public HttpServerInitializer(BrickDispatcher brickDispatcher, Class clazz){
        this.brickDispatcher = brickDispatcher;
        this.clazz = clazz;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //回车符作为消息分隔符
        //pipeline.addLast("framer", new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new MessageDecoder<T>(this.clazz));
        pipeline.addLast("encoder", new MessageEncoder());
        pipeline.addLast("handler", new HttpServerHandler(brickDispatcher));

        InetSocketAddress address = ch.remoteAddress();
        LOGGER.info("connect from {}|{}|{}|{}", address.getHostString(), address.getAddress().getHostAddress(), address.getHostName(), address.getPort());
    }
}
