package org.mao.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

@Sharable
public class MessageEncoder extends MessageToMessageEncoder<MessageDTO> {
    private final Charset charset;

    public MessageEncoder() {
        this(Charset.defaultCharset());
    }

    public MessageEncoder(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        } else {
            this.charset = charset;
        }
    }

    protected void encode(ChannelHandlerContext ctx, MessageDTO msg, List<Object> out) throws Exception {
        System.out.println("encoder: " + msg);
        if (msg != null) {
            byte[] b = msg.toString().getBytes(this.charset);
            ByteBuf responseMsg = Unpooled.buffer(b.length);
            responseMsg.writeBytes(b);
            //out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg.toString()), this.charset));
            out.add(responseMsg);
        }
    }
}
