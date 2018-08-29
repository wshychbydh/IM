package com.wbxm.icartoon.im.listener;

import com.wbxm.icartoon.im.ClientWrapper;

/**
 * @author ycb
 * @date 2018/8/27
 */
public interface IReconnectListener {

    void onReconnected(ClientWrapper client);
}
