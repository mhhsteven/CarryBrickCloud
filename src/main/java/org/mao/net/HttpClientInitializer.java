package org.mao.net;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.mao.task.BrickDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientInitializer.class);

    private BrickDispatcher brickDispatcher;

    public HttpClientInitializer(BrickDispatcher brickDispatcher) {
        this.brickDispatcher = brickDispatcher;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new MessageDecoder());
        pipeline.addLast("encoder", new MessageEncoder());
        pipeline.addLast("handler", new HttpClientHandler(brickDispatcher));
    }
}
