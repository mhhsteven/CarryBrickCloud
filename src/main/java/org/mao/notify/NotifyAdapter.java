package org.mao.notify;

import io.netty.channel.Channel;
import org.mao.job.bean.BaseDTO;

import java.io.Serializable;

/**
 * 消息通知类
 *
 * @param <T>
 * @author mhh
 */
public class NotifyAdapter<T extends Serializable> {

    private Notify<BaseDTO> notify;

    public NotifyAdapter(Channel channel) {
        notify = new Notify<>(channel);
    }

    public void sendTask(T t) {
        BaseDTO baseDTO = new BaseDTO();
        baseDTO.setCode("10000");
        baseDTO.setMsg("from server");
        baseDTO.setContent(t);
        notify.send(baseDTO);
    }

    public void doneTask() {
        BaseDTO baseDTO = new BaseDTO();
        baseDTO.setCode("20000");
        baseDTO.setMsg("complete by client");
        notify.send(baseDTO);
    }
}
