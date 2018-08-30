package com.wbxm.icartoon.im.listener;

import com.wbxm.icartoon.im.ClientWrapper;

/**
 * 自动重连接监听
 * @author ycb
 * @date 2018/8/27
 */
public interface IReconnectListener {

    void onReconnected(ClientWrapper client);
}
