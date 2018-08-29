package com.wbxm.icartoon.im.listener;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.wbxm.icartoon.im.model.Message;

/**
 * 处理包装后的消息回调
 *
 * @author ycb
 * @date 2018/8/22
 */
public interface IMessageHandler {

    @WorkerThread
    void handleMessage(@NonNull Message message);

}
