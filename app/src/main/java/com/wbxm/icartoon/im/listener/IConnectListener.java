package com.wbxm.icartoon.im.listener;

import android.support.annotation.AnyThread;

/**
 * 监听服务器的连接状态
 * @author ycb
 * @date 2018/8/23
 */
public interface IConnectListener {

    /**
     * Callback method for when the client connects to the server.
     */
    @AnyThread
    void connected();

    /**
     * Callback method for when the client disconnects from the server.
     */
    @AnyThread
    void disconnected();
}
