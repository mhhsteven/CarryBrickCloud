package org.mao.notify;

/**
 * 主从间的通知接口
 *
 * @param <T>
 * @author mhh
 */
public interface INotify<BaseDTO> {

    /**
     * 发送消息
     *
     * @param baseDTO
     */
    public void send(BaseDTO baseDTO);
}
