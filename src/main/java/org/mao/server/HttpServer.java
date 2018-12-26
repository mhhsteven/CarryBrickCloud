package org.mao.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    static {
        PropertyConfigurator.configure("CarryBrickCloud/src/main/resources/conf/log4j.properties");
    }

    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        LOGGER.info("Usage: {}, port: {}", HttpServer.class.getSimpleName(), args);
        if (args.length != 1) {
            return;
        }
        int port = Integer.parseInt(args[0]);
        new HttpServer(port).start();
    }

    public void start() throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();        // 用来接收进来的连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();    // 用来处理已经被接收的连接
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        LOGGER.info("initChannel ch: {}", ch);
                        ch.pipeline().addLast("handler", new HttpServerHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 1024) // determining the number of connections queued
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

        ChannelFuture cf = b.bind(port).sync();
        //获取ChannelFuture，并且阻塞当前线程直到它完成。
        cf.channel().closeFuture().sync();
    }
}
