package com.wbxm.icartoon.im.listener;

import com.wbxm.icartoon.im.model.Message;

/**
 * 上传文件监听
 *
 * @author ycb
 * @date 2018/8/27
 */
public interface IUploadListener {

    /**
     * @param message
     * @param url     返回服务器的文件地址
     */
    void onUploadSucceed(Message message, String url);

    void onUploadFailed(Message message, Throwable throwable);
}
