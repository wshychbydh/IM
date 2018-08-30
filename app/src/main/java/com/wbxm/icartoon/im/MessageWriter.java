package com.wbxm.icartoon.im;

import android.os.Looper;

import com.wbxm.icartoon.im.listener.IMessageReceiver;
import com.wbxm.icartoon.im.listener.ISendListener;
import com.wbxm.icartoon.im.listener.IUploadExecutor;
import com.wbxm.icartoon.im.listener.IUploadListener;
import com.wbxm.icartoon.im.listener.IWriteListener;
import com.wbxm.icartoon.im.listener.RejectionDef;
import com.wbxm.icartoon.im.model.Message;
import com.wbxm.icartoon.im.model.MessageStatus;
import com.wbxm.icartoon.im.model.Packet;
import com.wbxm.icartoon.im.model.RejectionCode;
import com.wbxm.icartoon.im.util.Constant;
import com.wbxm.icartoon.im.util.MessageFactory;
import com.wbxm.icartoon.im.util.ThreadUtil;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 缓冲发送消息类
 *
 * @author ycb
 * @date 2018/8/22
 */
public class MessageWriter implements IUploadListener, IMessageReceiver {

    private FileWriter fileWriter = new FileWriter(this);

    private LinkedBlockingQueue<Message> writeQueue = new LinkedBlockingQueue<>(Constant.DEFAULT_WRITE_SIZE);

    /**
     * 记录发送的消息，等待服务器反馈
     */
    private Map<Integer, Message> sendMap = new ConcurrentHashMap<>();

    private volatile boolean isConnecting = false;

    private ISendListener sendListener;
    private IWriteListener writeListener;


    public void setWriteListener(IWriteListener writeListener) {
        this.writeListener = writeListener;
    }

    public void setSendListener(ISendListener sendListener) {
        this.sendListener = sendListener;
        fileWriter.setSendListener(sendListener);
    }

    public void setUploadExecutor(IUploadExecutor executor) {
        fileWriter.setUploadExecutor(executor);
    }

    /**
     * 绑定到用户
     */
    public void start() {
        isConnecting = true;
        takeMessage();
    }

    public void stop() {
        isConnecting = false;
    }

    private void takeMessage() {
        ThreadUtil.writeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isConnecting) {
                        synchronized (writeQueue) {
                            syncMessage(writeQueue.take());
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 文本消息 @{Message.type}
     * <p>
     * 将从写入的消息存入消息队列
     *
     * @param message
     */
    public synchronized void pushMessage(final Message message) {
        if (isConnecting) {
            //如果是文本或者已上传到服务器的文件，则作为一条普通消息直接上传
            if (message.isText() || message.isUnSynced()) {
                try {
                    writeQueue.add(message);
                } catch (final Exception e) {
                    e.printStackTrace();
                    int errorCode = message.isText() ? RejectionCode.TEXT_PEAK : RejectionCode.SEND_ERROR;
                    onSendMessageRejected(message, errorCode, e.getMessage());
                }
            } else {
                //如果是文件，则需要先将文件上传到服务器后得到url，将url写入消息的content中，再上传。
                fileWriter.sendMessage(message);
            }
        } else {
            onSendMessageRejected(message, RejectionCode.SEND_ERROR, null);
        }
    }


    /**
     * 同步数据到服务器
     *
     * @param message
     */
    private void syncMessage(final Message message) {
        if (isConnecting) {
            if (writeListener != null) {
                ThreadUtil.asyncTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Packet packet = MessageFactory.encode(message);
                            if (writeListener.write(packet)) {
                                sendMap.put(packet.getSeqId(), message);
                            } else {
                                onSendMessageRejected(message, RejectionCode.SEND_ERROR, null);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            onSendMessageRejected(message, RejectionCode.SEND_ERROR, e.getMessage());
                        }
                    }
                });
            }
        } else {
            onSendMessageRejected(message, RejectionCode.SEND_ERROR, null);
        }
    }

    /**
     * 发送信息失败
     *
     * @param message   消息体
     * @param errorCode 失败错误码
     * @param errorMsg
     */
    private void onSendMessageRejected(final Message message, final @RejectionDef int errorCode,
                                       final String errorMsg) {
        if (sendListener != null) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                message.setSyncStatus(MessageStatus.FAILED);
                sendListener.onRejected(message, errorCode, errorMsg);
            } else {
                ThreadUtil.runOnUi(new Runnable() {
                    @Override
                    public void run() {
                        message.setSyncStatus(MessageStatus.FAILED);
                        sendListener.onRejected(message, errorCode, errorMsg);
                    }
                });
            }
        }
    }

    @Override
    public synchronized void onUploadSucceed(Message message, String url) {
        message.setSyncStatus(MessageStatus.UN_SYNC);
        message.setContent(url);
        syncMessage(message);
    }

    @Override
    public void onUploadFailed(Message message, Throwable throwable) {
        onSendMessageRejected(message, RejectionCode.UPLOAD_ERROR, throwable.getMessage());
    }

    @Override
    public void messageReceived(int packageLength, int headLength, int version, int operation,
                                int sequenceId, String msg) {
        Message message = sendMap.get(sequenceId);
        if (message != null) {
            message.setId(MessageFactory.decodeSmsReplayId(msg));
            message.setSyncStatus(MessageStatus.SYNCED);
            sendMap.remove(sequenceId);
            if (sendListener != null) {
                sendListener.onSendSucceed(message);
            }
        }
    }
}