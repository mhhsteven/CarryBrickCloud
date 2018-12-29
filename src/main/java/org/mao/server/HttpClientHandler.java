package org.mao.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientHandler extends SimpleChannelInboundHandler<MessageDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientHandler.class);

    //在到服务器的连接已经建立之后将被调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
//        byte[] req = "HelloWorld".getBytes();
//        ByteBuf firstMessage = Unpooled.buffer(req.length);
//        firstMessage.writeBytes(req);
//        ctx.writeAndFlush(firstMessage);
    }

    //当从服务器接受到一条消息时被调用
    @Override
    public void channelRead0(ChannelHandlerContext ctx, MessageDTO msg) throws Exception {
        LOGGER.info("receive msg from server: {}", msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("ClientChannelReadComplete");
        ctx.close();
    }

    //处理过程中出现的异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
