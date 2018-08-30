package com.wbxm.icartoon.im.listener;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.wbxm.icartoon.im.model.Message;

/**
 * 将服务器返回的消息解包成客户端识别的消息体
 *
 * @author ycb
 * @date 2018/8/22
 */
public interface IMessageHandler {

    /**
     * 客户端收到消息并处理，需要对所有收到的消息进行排序(根据message.id)
     * @param message 消息体
     */
    @WorkerThread
    void handleMessage(@NonNull Message message);

}
