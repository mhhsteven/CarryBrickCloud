package org.mao.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.mao.job.bean.BaseDTO;
import org.mao.task.BrickDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerHandler extends SimpleChannelInboundHandler<BaseDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerHandler.class);

    private BrickDispatcher brickDispatcher;

    public HttpServerHandler(BrickDispatcher brickDispatcher) {
        this.brickDispatcher = brickDispatcher;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        LOGGER.info("one channel active: {}", channel);
        brickDispatcher.addChannel(channel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        LOGGER.info("one channel inactive: {}", channel);
        brickDispatcher.removeChannel(channel);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("one handler add: {}", ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("one handler remove: {}", ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, BaseDTO msg) throws Exception {
        LOGGER.info("recevie msg from client: {}", msg);
        brickDispatcher.removeTask(ctx.channel());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        LOGGER.info("ServerChannelReadComplete");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.info("exceptionCaught");
        if (null != cause) {
            cause.printStackTrace();
        }
        if (null != ctx) {
            ctx.close();
        }
    }
}
