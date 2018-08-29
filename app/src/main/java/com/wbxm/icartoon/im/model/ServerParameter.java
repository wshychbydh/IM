package com.wbxm.icartoon.im.model;


import com.wbxm.icartoon.im.util.Constant;

/**
 * 连接服务器参数
 *
 * @author ycb
 * @date 2018/8/23
 */
public class ServerParameter {

    private String host;
    private int port;
    private int uid;
    private String token;
    private int bufferSize = Constant.DEFAULT_MESSAGE_SIZE;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    @Override
    public String toString() {
        return "ServerParameter{" +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", uid=" + uid +
                ", token='" + token + '\'' +
                ", bufferSize=" + bufferSize +
                '}';
    }
}
