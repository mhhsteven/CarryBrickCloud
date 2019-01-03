package org.mao.server;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {

    private final Charset charset;

    public MessageDecoder() {
        this(Charset.defaultCharset());
    }

    public MessageDecoder(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        } else {
            this.charset = charset;
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        int dataLength = buf.readInt();
        byte[] b = new byte[dataLength];
        buf.readBytes(b);
        String json = new String(b, this.charset);
        MessageDTO messageDTO = JSON.parseObject(json, MessageDTO.class);
        out.add(messageDTO);
    }
}
