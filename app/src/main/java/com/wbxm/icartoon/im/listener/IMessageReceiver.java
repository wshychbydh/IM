package com.wbxm.icartoon.im.listener;

/**
 * 从服务器接收到的数据
 *
 * @author ycb
 * @date 2018/8/23
 */
public interface IMessageReceiver {

    /**
     * Callback method for when the client receives a message from the server.
     *
     * @param packageLength
     * @param headLength
     * @param version
     * @param operation  @OperationCode
     * @param sequenceId 客户端上传的seqId，用于识别消息
     * @param message  服务器返回的消息，可能是消息体(JSON),可能是消息反馈(id).
     */
    void messageReceived(int packageLength, int headLength, int version, int operation, int sequenceId, String message);

}
