package org.mao.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    //在到服务器的连接已经建立之后将被调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        byte[] req = "HelloWorld".getBytes();
        ByteBuf firstMessage = Unpooled.buffer(req.length);
        firstMessage.writeBytes(req);
        ctx.writeAndFlush(firstMessage);
    }

    //当从服务器接受到一条消息时被调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf)msg;
        byte[] resp = new byte[buf.readableBytes()];
        buf.readBytes(resp);
        String str = new String(resp, "UTF-8");
        LOGGER.info(str);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("ClientChannelReadComplete");
        ctx.flush();
        ctx.channel().close();
    }

    //处理过程中出现的异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
