package com.wbxm.icartoon.im.util;

/**
 * @author ycb
 * @date 2018/8/21
 */
public interface Constant {

    String HOST = "47.97.120.250";
    int PORT = 7017;
    int VERSION = 1;

    int DEFAULT_HEARTBEAT_TIMEOUT = 20000;  //心跳间隔时间
    int DEFAULT_SOCKET_TIMEOUT = 3 * 60 * 1000;
    int DEFAULT_MESSAGE_SIZE = 1024;
    long DEFAULT_READ_DELAY = 100L; //从服务器读取消息延迟时间(递增)
    int DEFAULT_WRITE_SIZE = 99;  //同时发送消息数
    int DEFAULT_FILE_SIZE = 10;  //同时上传文件数量
    int AUTO_CONNECT_TIMES = 20;  //连接失败后自动重连次数，连接成功后会重置次数
}
