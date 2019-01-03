package org.mao.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class HttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    static {
        PropertyConfigurator.configure("CarryBrickCloud/src/main/resources/conf/log4j.properties");
    }

    public static void main(String[] args) {
        final String host = "127.0.0.1";
        final int port = 8088;

        try {
            HttpClient client = new HttpClient();
            client.connect(host, port);
        } catch (Exception e) {
            LOGGER.info("", e);
        }
    }

    public void connect(String host, int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap boot = new Bootstrap();
            boot.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HttpClientInitializer());
            Channel channel = boot.connect(host, port).sync().channel();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000L);
                        for (int i = 0; i < 100; i++) {
                            MessageDTO messageDTO = new MessageDTO();
                            messageDTO.setMsg((new Random()).nextInt(1000000) + "");
                            channel.writeAndFlush(messageDTO);
                            Thread.sleep((new Random()).nextInt(300));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();

            channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
