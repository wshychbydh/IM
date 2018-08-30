package com.wbxm.icartoon.im;

import android.os.Looper;

import com.wbxm.icartoon.im.listener.ISendListener;
import com.wbxm.icartoon.im.listener.IUploadExecutor;
import com.wbxm.icartoon.im.listener.IUploadListener;
import com.wbxm.icartoon.im.listener.RejectionDef;
import com.wbxm.icartoon.im.model.Message;
import com.wbxm.icartoon.im.model.RejectionCode;
import com.wbxm.icartoon.im.util.Constant;
import com.wbxm.icartoon.im.util.ThreadUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件上传,限定同时上传文件数
 *
 * @author ycb
 * @date 2018/8/24
 */
public class FileWriter implements IUploadListener {

    private Set<Message> fileSet = Collections.synchronizedSet(new HashSet<Message>());

    private ISendListener sendListener;  //发送文件监听器
    private IUploadExecutor uploadExecutor; // 上传文件执行器
    private IUploadListener uploadListener; //上传文件监听器

    public FileWriter(IUploadListener uploadListener) {
        this.uploadListener = uploadListener;
    }

    public void setSendListener(ISendListener sendListener) {
        this.sendListener = sendListener;
    }

    public void setUploadExecutor(IUploadExecutor uploadExecutor) {
        this.uploadExecutor = uploadExecutor;
    }

    public synchronized void sendMessage(Message message) {
        if (fileSet.size() >= Constant.DEFAULT_FILE_SIZE) {
            onSendMessageRejected(message, RejectionCode.FILE_PEAK, "Only " + Constant
                    .DEFAULT_FILE_SIZE + " files can be uploaded at the same time!");
            return;
        }
        boolean result = fileSet.add(message);
        if (result && uploadExecutor != null) {
            uploadExecutor.uploadFile(message, this);
        }
    }

    private void onSendMessageRejected(final Message message, final @RejectionDef int errorCode,
                                       final String errorMsg) {
        if (sendListener != null) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                sendListener.onRejected(message, errorCode, errorMsg);
            } else {
                ThreadUtil.runOnUi(new Runnable() {
                    @Override
                    public void run() {
                        sendListener.onRejected(message, errorCode, errorMsg);
                    }
                });
            }
        }
    }

    @Override
    public void onUploadSucceed(Message message, String url) {
        fileSet.remove(message);
        if (uploadListener != null) {
            uploadListener.onUploadSucceed(message, url);
        }
    }

    @Override
    public void onUploadFailed(Message message, Throwable throwable) {
        fileSet.remove(message);
        if (uploadListener != null) {
            uploadListener.onUploadFailed(message, throwable);
        }
    }
}
