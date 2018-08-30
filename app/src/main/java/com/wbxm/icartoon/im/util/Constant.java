package com.wbxm.icartoon.im.util;

/**
 * @author ycb
 * @date 2018/8/21
 */
public class Constant {

    public static final String HOST = "47.97.120.250";
    public static final int PORT = 7017;
    public static final int VERSION = 1;

    public static final int DEFAULT_HEARTBEAT_TIMEOUT = 20000;  //心跳间隔时间
    public static final int DEFAULT_SOCKET_TIMEOUT = 3 * 60 * 1000;
    public static final int DEFAULT_MESSAGE_SIZE = 1024;
    public static final long DEFAULT_READ_DELAY = 100L; //从服务器读取消息延迟时间(递增)
    public static final int DEFAULT_WRITE_SIZE = 12;  //同时发送消息数
    public static final int DEFAULT_FILE_SIZE = 5;  //同时上传文件数量
    public static final int AUTO_CONNECT_DELAY = 3000;  //连接失败后自动重连延迟时间
}
