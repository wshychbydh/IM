package com.wbxm.icartoon.im.listener;

/**
 * 接受原始数据包
 *
 * @author ycb
 * @date 2018/8/23
 */
public interface IMessageReceiver {

    /**
     * Callback method for when the client receives a message from the server.
     *
     * @param message the message from the server.
     */
    void messageReceived(int packageLength, int headLength, int version, int operation, int sequenceId, String message);

}
