package com.wbxm.icartoon.im;

import com.wbxm.icartoon.im.listener.IMessageHandler;
import com.wbxm.icartoon.im.listener.IMessageReceiver;
import com.wbxm.icartoon.im.model.Message;
import com.wbxm.icartoon.im.util.Constant;
import com.wbxm.icartoon.im.util.MessageFactory;
import com.wbxm.icartoon.im.util.ThreadUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.DelayQueue;

/**
 * 将从服务器获取到的消息存入消息队列，再从队列取出。对消息做缓冲防止短时间内接收到大量push消息而导致UI卡顿
 *
 * @author ycb
 * @date 2018/8/22
 */
public class MessageReader implements IMessageReceiver {

    /**
     * 记录从服务器获取到的所有消息id，根据id去重
     */
    private Set<Integer> ids = Collections.synchronizedSet(new HashSet<Integer>());

    private DelayQueue<DelayTask<Message>> readQueue = new DelayQueue<>();

    private volatile boolean isConnecting = false;

    private IMessageHandler listener; // 客户端接收服务器消息监听器

    public void setMessageHandler(IMessageHandler listener) {
        this.listener = listener;
    }

    public void start() {
        this.isConnecting = true;
        takeMessage();
    }

    public void stop() {
        isConnecting = false;
    }

    private void takeMessage() {
        ThreadUtil.readExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isConnecting) {
                        synchronized (readQueue) {
                            handleMessage(readQueue.take().getTask());
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 将从服务器读取到的消息存入消息队列
     *
     * @param message
     */
    private synchronized void pushMessage(Message message) {
        if (isConnecting) {
            //根据id去重
            if (!ids.contains(message.getId())) {
                long delay = readQueue.size() * Constant.DEFAULT_READ_DELAY;
                boolean result = readQueue.add(new DelayTask<>(message, delay));
                if (result) {
                    ids.add(message.getId());
                }
            }
        }
    }

    private void handleMessage(final Message message) {
        if (isConnecting && listener != null) {
            listener.handleMessage(message);
        }
    }

    @Override
    public void messageReceived(int packageLength, int headLength, int version, int operation, int sequenceId, String message) {
        pushMessage(MessageFactory.decode(message));
    }
}
