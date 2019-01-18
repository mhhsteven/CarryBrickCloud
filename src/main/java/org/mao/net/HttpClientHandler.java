package org.mao.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.mao.job.bean.BaseDTO;
import org.mao.task.BrickDispatcher;
import org.mao.utils.ApplicationContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端handler处理类
 *
 * @author mhh
 */
public class HttpClientHandler extends SimpleChannelInboundHandler<BaseDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientHandler.class);

    private BrickDispatcher brickDispatcher;

    public HttpClientHandler() {
        this.brickDispatcher = ApplicationContextUtils.getBean(BrickDispatcher.class);
    }

    /**
     * 在到服务器的连接已经建立之后将被调用
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        LOGGER.info("connect to server success");
    }

    /**
     * 当从服务器接受到一条消息时被调用
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, BaseDTO msg) throws Exception {
        LOGGER.info("receive msg from server: {}", msg);
        Channel channel = ctx.channel();
        brickDispatcher.doJob(msg.getContent(), channel);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        LOGGER.info("ClientChannelReadComplete");
        ctx.flush();
    }

    /**
     * 连接异常时调用
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
