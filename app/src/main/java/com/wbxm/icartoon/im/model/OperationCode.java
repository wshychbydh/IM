package com.wbxm.icartoon.im.model;

/**
 * 客户端与服务器通信操作协议码
 *
 * @author ycb
 * @date 2018/8/28
 */
public class OperationCode {

    public static int OP_HANDSHARE = 0;
    public static int OP_HANDSHARE_REPLY = 1;
    public static int OP_HEARTBEAT = 2;  //客户端发起 跳(服务 判断是否在线，150 秒超时将断掉连接)
    public static int OP_HEARTBEAT_REPLY = 3;  //服务端心跳答复
    public static int OP_SEND_SMS = 4;   //私信消息上 (request)
    public static int OP_SEND_SMS_REPLY = 5;  //私信消息回复(request reply)
    public static int OP_DISCONNECT_REPLY = 6; //服务端通知客户端断掉连接(disconnect)
    public static int OP_AUTH = 7;   //auth认证
    public static int OP_AUTH_REPLY = 8;  //auth认证返回
    public static int OP_PULL_SMS = 15;  //客户端上线拉取未读消息(pull)
    public static int OP_ACK = 16;  //ACK ，客户端通知服务器已收到该消息，消息体待定
    public static int OP_NOTIFY = 17;  //服务私信消息下行通知客户端

    public static boolean isHeartbeatReplay(int operation) {
        return OP_HEARTBEAT_REPLY == operation;
    }

    public static boolean isAuthReplay(int operation) {
        return OP_AUTH_REPLY == operation;
    }

    public static boolean isSmsReplay(int operation) {
        return OP_SEND_SMS_REPLY == operation;
    }

    public static boolean isNotify(int operation) {
        return OP_NOTIFY == operation;
    }

    public static boolean isDisconnected(int operation) {
        return OP_DISCONNECT_REPLY == operation;
    }
}