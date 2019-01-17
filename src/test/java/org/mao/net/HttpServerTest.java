package org.mao.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerTest.class);

    static {
        PropertyConfigurator.configure("CarryBrickCloud/src/main/resources/conf/log4j.properties");
    }

    private final int port;

    public HttpServerTest(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        LOGGER.info("Usage: {}, port: {}", HttpServerTest.class.getSimpleName(), args);
        if (args.length != 1) {
            return;
        }
        int port = Integer.parseInt(args[0]);
        new HttpServerTest(port).start();
    }

    public void start() throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 用来接收进来的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 用来处理已经被接收的连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpServerInitializer(null, null))
                .option(ChannelOption.SO_BACKLOG, 128) // determining the number of connections queued
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

        ChannelFuture cf = bootstrap.bind(port).sync();
        //获取ChannelFuture，并且阻塞当前线程直到它完成。
        cf.channel().closeFuture().sync();
    }
}
