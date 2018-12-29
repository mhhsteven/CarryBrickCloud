package org.mao.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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

            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setMsg("request");
            channel.writeAndFlush(messageDTO);
            channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
