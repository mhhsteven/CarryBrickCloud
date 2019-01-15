package org.mao.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.mao.job.bean.BaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class MessageEncoder extends MessageToByteEncoder<BaseDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageEncoder.class);

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

    protected void encode(ChannelHandlerContext ctx, BaseDTO msg, ByteBuf buf) throws Exception {
        if (msg != null) {
            LOGGER.info("encoder: {}", msg);
            byte[] b = msg.toString().getBytes(this.charset);
            buf.writeInt(b.length);
            buf.writeBytes(b);
        }
    }
}
