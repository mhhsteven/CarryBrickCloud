package org.mao.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.mao.job.bean.BaseDTO;
import org.mao.task.BrickExecutor;
import org.mao.task.BrickExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端handler处理类
 *
 * @author mhh
 */
public class HttpClientHandler extends SimpleChannelInboundHandler<BaseDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientHandler.class);

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
        BrickExecutor executor = BrickExecutorFactory.newSlaveExecutor(ctx.channel());
        LOGGER.info("接受到master分配的任务，创建执行者: {}", executor);
        executor.exec(msg.getContent());
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
        LOGGER.error("", cause);
        ctx.close();
    }

}
