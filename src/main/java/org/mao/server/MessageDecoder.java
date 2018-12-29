package org.mao.server;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

@Sharable
public class MessageDecoder extends MessageToMessageDecoder<ByteBuf> {

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
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        String json = msg.toString(this.charset);
        MessageDTO messageDTO = JSON.parseObject(json, MessageDTO.class);
        out.add(messageDTO);
    }
}
