package com.wbxm.icartoon.im.listener;

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

/**
 * 发送数据包失败的监听器
 *
 * @author ycb
 * @date 2018/8/23
 */
public interface ISendListener<T> {

    /**
     * 发送数据成功
     * @param data
     */
    @WorkerThread
    void onSendSucceed(T data);

    /**
     * 提交数据失败
     * @param data
     * @param errorCode
     * @param errorMsg
     */
    @UiThread
    void onRejected(T data, @RejectionDef int errorCode, @Nullable String errorMsg);
}