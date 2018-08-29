package com.wbxm.icartoon.im;

import com.wbxm.icartoon.im.model.Packet;
import com.wbxm.icartoon.im.model.ServerParameter;
import com.wbxm.icartoon.im.listener.IAuthListener;
import com.wbxm.icartoon.im.listener.IConnectListener;
import com.wbxm.icartoon.im.listener.IHeartListener;
import com.wbxm.icartoon.im.listener.IMessageReceiver;
import com.wbxm.icartoon.im.listener.IReconnectListener;
import com.wbxm.icartoon.im.listener.IWriteListener;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * 通信包装类，对接状态
 */
public final class ClientWrapper extends AbstractBlockingClient implements IWriteListener {

    private ServerParameter serverParameter; //与服务器建立连接需要的参数
    private IHeartListener heartListener;  //获取心跳回调
    private IAuthListener authListener;    //获取认证状态回调
    private IConnectListener connectListener;  //连接状态监听
    private IMessageReceiver messageListener;  //消息接收者
    private IReconnectListener reconnectListener;  //重连接监听
    private Listener listener = new Listener();

    public ClientWrapper(ServerParameter parameter) {
        super(parameter.getHost(), parameter.getPort(), parameter.getToken(), parameter.getBufferSize());
        this.serverParameter = parameter;
    }


    private class Listener implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            deleteObserver(listener);
            stop();
            ClientWrapper pc = new ClientWrapper(serverParameter);
            listener = this;
            pc.setReconnectListener(reconnectListener);
            pc.setHeartListener(heartListener);
            pc.setConnectListener(connectListener);
            pc.setMessageListener(messageListener);
            pc.setAuthListener(authListener);
            addObserver(listener);
            new Thread(pc).start();
            reconnectListener.onReconnected(pc);
        }
    }

    /**
     * 建立连接
     */
    public void start() {
        addObserver(listener);
        new Thread(this).start();
    }

    /**
     * 发送信息
     *
     * @param protocol
     * @throws IOException
     */
    @Override
    public boolean write(Packet protocol) throws IOException {
        if (isRunning()) {
            return messageWrite(protocol);
        }
        return false;
    }

    /**
     * 接收到服务信息
     *
     * @param version
     * @param operation
     * @param sequenceId
     * @param message    the message from the server.
     */

    @Override
    protected void messageReceived(int packageLength, int headLength, int version, int operation, int sequenceId, String message) {
        if (messageListener != null) {
            messageListener.messageReceived(packageLength, headLength, version, operation, sequenceId, message);
        }
    }

    /**
     * 接收到服务器心跳
     */
    @Override
    protected void heartBeatReceived() {
        if (heartListener != null) {
            heartListener.heartBeatReceived();
        }
    }

    /**
     * 发送验证信息成功回调
     */
    @Override
    protected void authSuccess() {
        if (authListener != null) {
            authListener.authSuccess();
        }
    }

    /**
     * 与服务器的连接状态回调
     *
     * @param alreadyConnected whether the client was already connected to the server.
     */
    @Override
    protected void connected(boolean alreadyConnected) {
        if (connectListener != null) {
            connectListener.connected();
        }
    }

    /**
     * 与服务器断开连接
     */
    @Override
    protected void disconnected() {
        if (connectListener != null) {
            connectListener.disconnected();
        }
    }


    public void setHeartListener(IHeartListener heartListener) {
        this.heartListener = heartListener;
    }

    public void setAuthListener(IAuthListener authListener) {
        this.authListener = authListener;
    }

    public void setConnectListener(IConnectListener connectListener) {
        this.connectListener = connectListener;
    }

    public void setMessageListener(IMessageReceiver messageListener) {
        this.messageListener = messageListener;
    }

    public void setReconnectListener(IReconnectListener reconnectListener) {
        this.reconnectListener = reconnectListener;
    }
}
