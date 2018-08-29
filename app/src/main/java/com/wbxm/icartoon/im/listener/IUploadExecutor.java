package com.wbxm.icartoon.im.listener;

import com.wbxm.icartoon.im.model.Message;

/**
 * 上传文件执行类
 * @author ycb
 * @date 2018/8/27
 */
public interface IUploadExecutor {

    void uploadFile(Message message, IUploadListener listener);
}
