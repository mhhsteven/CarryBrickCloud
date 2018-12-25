package org.mao.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HttpClientHandler extends ChannelInboundHandlerAdapter {

    //在到服务器的连接已经建立之后将被调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        byte[] req = "Query time ?".getBytes();
        ByteBuf firstMessage = Unpooled.buffer(req.length);
        firstMessage.writeBytes(req);
        ctx.writeAndFlush(firstMessage);
    }

    //当从服务器接受到一条消息时被调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] resp = new byte[buf.readableBytes()];
        buf.readBytes(resp);
        String str = new String(resp, "UTF-8");
        System.out.println("服务器时间 : " + str);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    //处理过程中出现的异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
