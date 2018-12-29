package org.mao.server;

import io.netty.buffer.ByteBufUtil;
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
        if (msg != null) {
            out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg.toString()), this.charset));
        }
    }
}
