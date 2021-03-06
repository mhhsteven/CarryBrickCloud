package org.mao.net;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 服务端初始化，使用自定义的编码，解码
 *
 * @author mhh
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerInitializer.class);

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //回车符作为消息分隔符
        //pipeline.addLast("framer", new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new MessageDecoder());
        pipeline.addLast("encoder", new MessageEncoder());
        pipeline.addLast("handler", new HttpServerHandler());

        InetSocketAddress address = ch.remoteAddress();
        LOGGER.info("connect from {}|{}|{}|{}", address.getHostString(), address.getAddress().getHostAddress(), address.getHostName(), address.getPort());
    }
}
