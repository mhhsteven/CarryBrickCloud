package org.mao.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.mao.task.BrickDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class HttpServer<T extends Serializable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    public void start(Integer port, BrickDispatcher<T> brickDispatcher, Class clazz) throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 用来接收进来的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 用来处理已经被接收的连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpServerInitializer<T>(brickDispatcher, clazz))
                .option(ChannelOption.SO_BACKLOG, 128) // determining the number of connections queued
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

        ChannelFuture cf = bootstrap.bind(port).sync();
        //获取ChannelFuture，并且阻塞当前线程直到它完成。
        cf.channel().closeFuture().sync();
    }
}
