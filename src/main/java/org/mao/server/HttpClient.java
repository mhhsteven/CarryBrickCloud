package org.mao.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    static {
        PropertyConfigurator.configure("CarryBrickCloud/src/main/resources/conf/log4j.properties");
    }

    public static void main(String[] args) {
        final String host = "127.0.0.1";
        final int port = 8088;
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpClient client = new HttpClient();
                        client.connect(host, port);
                    } catch (Exception e) {
                        LOGGER.info("客户端{}请求出现异常", Thread.currentThread().getId());
                    }
                }
            }).start();
        }
    }

    public void connect(String host, int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap boot = new Bootstrap();
            boot.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpClientHandler());
                        }
                    });
            ChannelFuture cf = boot.connect(host, port).sync();
            cf.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
