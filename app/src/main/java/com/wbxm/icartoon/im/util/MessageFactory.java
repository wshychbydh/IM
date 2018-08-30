package com.wbxm.icartoon.im.util;

import com.alibaba.fastjson.JSON;
import com.wbxm.icartoon.im.model.Message;
import com.wbxm.icartoon.im.model.MessageStatus;
import com.wbxm.icartoon.im.model.OperationCode;
import com.wbxm.icartoon.im.model.Packet;

/**
 * 消息生成和处理类
 *
 * @author ycb
 * @date 2018/8/21
 */
public class MessageFactory {

    /**
     * 创建消息数据包
     *
     * @param content     消息内容，如果消息类型是文件，则为文件的地址（本地/网络）
     * @param messageType 消息类型 @see Message.Type
     * @param fromUid
     * @param toUid
     * @return
     */
    public static Message createMessage(String content, String messageType, int fromUid, int toUid) {
        Message message = new Message();
        message.setContent(content);
        message.setFromId(fromUid);
        message.setType(messageType);
        message.setToId(toUid);
        message.setSyncStatus(MessageStatus.SYNCING);
        return message;
    }

    /**
     * 将服务器接收到的数据解码成消息包
     *
     * @param message
     * @return
     */
    public static int decodeSmsReplayId(String message) {
        try {
            return new org.json.JSONObject(message).getInt("Id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 将服务器接收到的数据解码成消息包
     *
     * @param msg
     * @return
     */
    public static Message decode(String msg) {
        Message message = JSON.parseObject(msg, Message.class);
        message.setSyncStatus(MessageStatus.SYNCED);
        return message;
    }

    /**
     * 将消息实体类转化成JSON数据
     *
     * @param message
     * @return
     */
    public static Packet encode(Message message) {
        Packet packet = new Packet();
        packet.setOperation(OperationCode.OP_SEND_SMS);
        packet.setBody(JSON.toJSONString(message));
        packet.setSeqId(SequenceIdRecorder.getSeqId());
        return packet;
    }
}
