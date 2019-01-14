package org.mao.net;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.mao.job.bean.BaseDTO;

import java.nio.charset.Charset;
import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {

    private static final Integer HEAD_LENGTH = 4;

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
        //这个HEAD_LENGTH是我们用于表示头长度的字节数。  由于上面我们传的是一个int类型的值，所以这里HEAD_LENGTH的值为4.
        if (buf.readableBytes() < HEAD_LENGTH) {
            return;
        }
        //我们标记一下当前的readIndex的位置
        buf.markReaderIndex();

        // 读取传送过来的消息的长度。ByteBuf 的readInt()方法会让他的readIndex增加4
        int dataLength = buf.readInt();
        // 我们读到的消息体长度为0，这是不应该出现的情况，这里出现这情况，关闭连接。
        if (dataLength < 0) {
            ctx.close();
        }
        //读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex. 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
        if (buf.readableBytes() < dataLength) {
            buf.resetReaderIndex();
            return;
        }
        //  嗯，这时候，我们读到的长度，满足我们的要求了，把传送过来的数据，取出来吧~~
        byte[] b = new byte[dataLength];
        buf.readBytes(b);
        String json = new String(b, this.charset);
        BaseDTO messageDTO = JSON.parseObject(json, BaseDTO.class);
        out.add(messageDTO);
    }
}
