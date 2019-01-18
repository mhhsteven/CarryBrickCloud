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
 * 服务端handler处理类
 *
 * @author mhh
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<BaseDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerHandler.class);

    private BrickDispatcher brickDispatcher;

    public HttpServerHandler() {
        this.brickDispatcher = ApplicationContextUtils.getBean(BrickDispatcher.class);
    }

    /**
     * 客户端连接时调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        LOGGER.info("one channel active: {}", channel.id());
        brickDispatcher.addChannel(channel);
    }

    /**
     * 客户端失连时调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        LOGGER.info("one channel inactive: {}", channel.id());
        brickDispatcher.removeChannel(channel);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("one handler add: {}", ctx.channel().id());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("one handler remove: {}", ctx.channel().id());
    }

    /**
     * 接受到客户端消息时调用
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
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

    /**
     * 连接异常时调用
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
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
