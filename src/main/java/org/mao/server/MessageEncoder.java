package org.mao.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class MessageEncoder extends MessageToByteEncoder<MessageDTO> {
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

    protected void encode(ChannelHandlerContext ctx, MessageDTO msg, ByteBuf buf) throws Exception {
        if (msg != null) {
            byte[] b = msg.toString().getBytes(this.charset);
            buf.writeInt(b.length);
            buf.writeBytes(b);
        }
    }
}
