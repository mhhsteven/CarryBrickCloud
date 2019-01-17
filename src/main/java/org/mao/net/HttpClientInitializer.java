package org.mao.net;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.mao.task.BrickDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class HttpClientInitializer<T extends Serializable> extends ChannelInitializer<SocketChannel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientInitializer.class);

    private BrickDispatcher<T> brickDispatcher;

    private Class clazz;

    public HttpClientInitializer(BrickDispatcher<T> brickDispatcher, Class clazz) {
        this.brickDispatcher = brickDispatcher;
        this.clazz = clazz;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new MessageDecoder<T>(clazz));
        pipeline.addLast("encoder", new MessageEncoder());
        pipeline.addLast("handler", new HttpClientHandler(brickDispatcher));
    }
}
