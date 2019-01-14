package org.mao.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.mao.job.bean.BaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerHandler<T extends BaseDTO> extends SimpleChannelInboundHandler<BaseDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("channelInactive");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("handlerAdded");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("handlerRemoved");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, BaseDTO msg) throws Exception {
        LOGGER.info("recevie msg from client: {}", msg);

        msg.setMsg(msg.getMsg() + " over");

        Channel channel = ctx.channel();
        channel.writeAndFlush(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("ServerChannelReadComplete");
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
