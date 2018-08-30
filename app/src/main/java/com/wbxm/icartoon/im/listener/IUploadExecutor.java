package com.wbxm.icartoon.im.listener;

import com.wbxm.icartoon.im.model.Message;

/**
 * 上传文件执行类
 * @author ycb
 * @date 2018/8/27
 */
public interface IUploadExecutor {

    /**
     * 上传文件
     * @param message  文件消息体
     * @param listener
     */
    void uploadFile(Message message, IUploadListener listener);
}
