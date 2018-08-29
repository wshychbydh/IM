package com.wbxm.icartoon.im;

import android.text.TextUtils;

import com.wbxm.icartoon.im.model.Message;
import com.wbxm.icartoon.im.model.ServerParameter;
import com.wbxm.icartoon.im.listener.IAuthListener;
import com.wbxm.icartoon.im.listener.IConnectListener;
import com.wbxm.icartoon.im.listener.IHeartListener;
import com.wbxm.icartoon.im.listener.IMessageHandler;
import com.wbxm.icartoon.im.listener.IMessageReceiver;
import com.wbxm.icartoon.im.listener.IReconnectListener;
import com.wbxm.icartoon.im.listener.ISendListener;
import com.wbxm.icartoon.im.listener.IUploadExecutor;
import com.wbxm.icartoon.im.util.Constant;
import com.wbxm.icartoon.im.model.OperationCode;

import java.util.Observable;

/**
 * 聊天工具使用入口
 *
 * @author ycb
 * @date 2018/8/23
 */
public final class MessageClient extends Observable implements IConnectListener, IMessageReceiver,
        IReconnectListener {

    private ClientWrapper client;

    private MessageReader reader = new MessageReader();
    private MessageWriter writer = new MessageWriter();

    private volatile boolean isConnected = false;  //记录当前连接状态

    private ServerParameter parameter = new ServerParameter();

    private IConnectListener connectListener;

    private MessageClient() {
    }

    /**
     * 开启连接
     */
    public void start() {
        client = new ClientWrapper(parameter);
        configClient();
        client.start();
    }

    /**
     * 终止连接
     */
    public void stop() {
        isConnected = false;
        if (client != null) {
            client.stop();
        }
    }

    @Override
    public void connected() {
        isConnected = true;

        reader.start();
        writer.start();

        if (connectListener != null) {
            connectListener.connected();
        }
    }

    @Override
    public void disconnected() {
        isConnected = false;
        reader.stop();
        writer.stop();
        if (connectListener != null) {
            connectListener.disconnected();
        }
    }

    @Override
    public void onReconnected(ClientWrapper client) {
        this.client = client;
        configClient();
    }

    private void configClient() {
        client.setMessageListener(this);
        client.setReconnectListener(this);
        client.setConnectListener(this);
        writer.setWriteListener(client);
    }

    @Override
    public void messageReceived(int packageLength, int headLength, int version, int operation, int sequenceId, String message) {
        if (OperationCode.isSmsReplay(operation)) {
            writer.messageReceived(packageLength, headLength, version, operation, sequenceId, message);
        } else if (OperationCode.isNotify(operation)) {
            reader.messageReceived(packageLength, headLength, version, operation, sequenceId, message);
        }
    }

    /**
     * 发送消息
     *
     * @param message
     */
    public void sendMessage(Message message) {
        if (isConnected) {
            writer.pushMessage(message);
        }
    }

    /**
     * 接受消息回调
     *
     * @param messageHandler
     * @return
     */
    public MessageClient setMessageHandler(IMessageHandler messageHandler) {
        reader.setMessageHandler(messageHandler);
        return this;
    }

    /**
     * 发送消息失败回调
     *
     * @param sendListener
     * @return
     */
    public MessageClient setSendListener(ISendListener sendListener) {
        writer.setSendListener(sendListener);
        return this;
    }

    /**
     * 发送消息失败回调
     *
     * @param heartListener
     * @return
     */
    public MessageClient setHeartListener(IHeartListener heartListener) {
        client.setHeartListener(heartListener);
        return this;
    }

    /**
     * 发送消息失败回调
     *
     * @param authListener
     * @return
     */
    public MessageClient setAuthListener(IAuthListener authListener) {
        client.setAuthListener(authListener);
        return this;
    }

    /**
     * 上传文件执行回调
     *
     * @param uploadExecutor
     * @return
     */
    public MessageClient setUploadExecutor(IUploadExecutor uploadExecutor) {
        writer.setUploadExecutor(uploadExecutor);
        return this;
    }

    public MessageClient setConnectListener(IConnectListener connectListener) {
        this.connectListener = connectListener;
        return this;
    }

    public static class Builder {

        private String host;
        private int port;
        private int uid;
        private String token;
        private int bufferSize = Constant.DEFAULT_MESSAGE_SIZE;

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setUid(int uid) {
            this.uid = uid;
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        /**
         * 每次从服务器读取数据包大小
         *
         * @param bufferSize @Constant.DEFAULT_MESSAGE_SIZE
         * @return
         */
        public Builder setBufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }


        public MessageClient build() {
            check();
            MessageClient client = new MessageClient();
            client.parameter.setHost(host);
            client.parameter.setBufferSize(bufferSize);
            client.parameter.setPort(port);
            client.parameter.setToken(token);
            client.parameter.setUid(uid);
            return client;
        }

        private void check() {
            if (TextUtils.isEmpty(host)) {
                throw new IllegalArgumentException("Host can not be empty!");
            }

            if (port <= 0) {
                throw new IllegalArgumentException("Port can not be empty!");
            }

            if (TextUtils.isEmpty(token)) {
                throw new IllegalArgumentException("Token can not be empty!");
            }

            if (uid <= 0) {
                throw new IllegalArgumentException("Uid can not be empty!");
            }
        }
    }
}
