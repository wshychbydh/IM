package com.wbxm.icartoon.im.listener;

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

/**
 * 发送消息状态监听
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
     * @param data      提交的数据对象
     * @param errorCode 提交失败错误码 @RejectionCode
     * @param errorMsg  提交失败原因，可能为null
     */
    @UiThread
    void onRejected(T data, @RejectionDef int errorCode, @Nullable String errorMsg);
}