package org.mao.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    public void connect(String host, int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap boot = new Bootstrap();
            boot.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HttpClientInitializer());
            Channel channel = boot.connect(host, port).sync().channel();
            channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
