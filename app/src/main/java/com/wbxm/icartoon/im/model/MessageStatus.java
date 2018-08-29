package com.wbxm.icartoon.im.model;

/**
 * 数据包状态,可能还会增加状态码。如果增删在@StatusDef中做相应增删
 *
 * @author ycb
 * @date 2018/8/29
 */
public final class MessageStatus {

    public static final int UN_SYNC = 11; //文件已上传，但消息未同步
    public static final int SYNCING = 12; //上传消息中
    public static final int SYNCED = 13; //消息已同步
    public static final int FAILED = 14; //上传失败
}
