package com.wbxm.icartoon.im;

import android.support.annotation.Nullable;

import com.wbxm.icartoon.im.model.OperationCode;
import com.wbxm.icartoon.im.model.Packet;
import com.wbxm.icartoon.im.util.Constant;
import com.wbxm.icartoon.im.util.SequenceIdRecorder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An abstract blocking client, designed to connect to implementations of
 * AbstractServer in its own thread. Since the client only has a single
 * connection to a single server it can use blocking IO. This class provides a
 * set of callback methods for concrete implementations to know the state of the
 * client and its connection This client does not log, implementations should
 * handle this.
 * <p>
 * This client does not support SSL or UDP connections.
 */
public abstract class AbstractBlockingClient extends Observable implements Runnable {

    private enum State {
        STOPPED, STOPPING, RUNNING
    }

    private final AtomicReference<State> state = new AtomicReference<>(State.STOPPED);
    protected InetAddress server;
    protected String host;
    protected final int port;
    private final int defaultBufferSize;
    private int defaultHeartBeatTimeOut = Constant.DEFAULT_HEARTBEAT_TIMEOUT;
    private int defaultSocketTimeOut = Constant.DEFAULT_SOCKET_TIMEOUT;
    protected final String token;
    private final AtomicReference<DataOutputStream> out = new AtomicReference<DataOutputStream>();
    private final AtomicReference<DataInputStream> in = new AtomicReference<DataInputStream>();


    /**
     * Construct an unstarted client which will attempt to connect to the given
     * server on the given port.
     *
     * @param host the server address.
     * @param port the port on which to connect to the server.
     */
    public AbstractBlockingClient(String host, int port, String token) {
        this(host, port, token, Constant.DEFAULT_MESSAGE_SIZE);
    }

    /**
     * Construct an unstarted client which will attempt to connect to the given
     * server on the given port.
     *
     * @param host              the server address.
     * @param port              the port on which to connect to the server.
     * @param defaultBufferSize the default buffer size for reads. This should as small as
     *                          possible value that doesn't get exceeded often - see class
     *                          documentation.
     */
    public AbstractBlockingClient(String host, int port, String token, int
            defaultBufferSize) {
        this.host = host;
        this.port = port;
        this.token = token;
        this.defaultBufferSize = defaultBufferSize;
    }

    /**
     * Returns the port to which this client will connect.
     *
     * @return the port to which this client will connect.
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the host to which this client will connect.
     *
     * @return the host to which this client will connect.
     */
    @Nullable
    public InetAddress getServer() {
        return server;
    }

    /**
     * Returns true if this client is the running state (either connected or
     * trying to connect).
     *
     * @return true if this client is the running state (either connected or
     * trying to connect).
     */
    public boolean isRunning() {
        return state.get() == State.RUNNING;
    }

    /**
     * Returns true if this client is the stopped state.
     *
     * @return true if this client is the stopped state.
     */
    public boolean isStopped() {
        return state.get() == State.STOPPED;
    }

    /**
     * Attempt to connect to the server and receive messages. If the client is
     * already running, it will not be started again. This method is designed to
     * be called in its own thread and will not return until the client is
     * stopped.
     *
     * @throws RuntimeException if the client fails
     */
    public void run() {
        Socket socket = null;
        try {
            server = InetAddress.getByName(host);
            socket = new Socket(server, port);
            socket.setSoTimeout(defaultSocketTimeOut);

            out.set(new DataOutputStream(socket.getOutputStream()));
            in.set(new DataInputStream(socket.getInputStream()));

            if (!state.compareAndSet(State.STOPPED, State.RUNNING)) {
                connected(false);
                return;
            }
            authWrite();
            connected(true);
            while (state.get() == State.RUNNING) {
                byte[] inBuffer = new byte[defaultBufferSize];
                int readPoint = in.get().read(inBuffer);
                if (readPoint != -1) {
                    byte[] result = BruteForceCoding.tail(inBuffer, inBuffer.length - 16);

                    int operation = BruteForceCoding.decodeIntBigEndian(inBuffer, 8, 4);
                    if (OperationCode.isHeartbeatReplay(operation)) {
                        heartBeatReceived();
                    } else if (OperationCode.isAuthReplay(operation)) {
                        authSuccess();
                        heartBeat();
                    } else if (OperationCode.isSmsReplay(operation) || OperationCode.isNotify(operation)) {
                        int packageLength = BruteForceCoding.decodeIntBigEndian(inBuffer, 0, 4);
                        int headLength = BruteForceCoding.decodeIntBigEndian(inBuffer, 4, 2);
                        int version = BruteForceCoding.decodeIntBigEndian(inBuffer, 6, 2);
                        int sequenceId = BruteForceCoding.decodeIntBigEndian(inBuffer, 12, 4);
                        String content = new String(result).trim();
                        messageReceived(packageLength, headLength, version, operation, sequenceId, content);
                    } else if (OperationCode.isDisconnected(operation)) {
                        //断线重新连接
                        state.set(State.STOPPED);
                        disconnected();
                        restart();
                    }
                }
            }

        } catch (Exception ioe) {
            ioe.printStackTrace();
            state.set(State.STOPPED);
            disconnected();
            restart();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void restart() {
        if (true) {
            super.setChanged();
        }
        notifyObservers();
    }

    /**
     * Stop the client in a graceful manner. After this call the client may
     * spend some time in the process of stopping. A disconnected callback will
     * occur when the client actually stops.
     *
     * @return if the client was successfully set to stop.
     */
    public boolean stop() {
        if (state.compareAndSet(State.RUNNING, State.STOPPING)) {
            try {
                in.get().close();
            } catch (IOException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Send the given message to the server.
     *
     * @return true if the message was sent to the server.
     * @throws IOException
     */
    public synchronized Boolean authWrite() throws IOException {
        //  String msg = uid + "," + getCode(token);
        String msg = token;
        byte[] bytes = msg.getBytes();
        int packLength = bytes.length + 16;
        byte[] message = new byte[4 + 2 + 2 + 4 + 4];

        // package length
        int offset = BruteForceCoding.encodeIntBigEndian(message, packLength, 0, 4 * BruteForceCoding.BSIZE);
        // header length
        offset = BruteForceCoding.encodeIntBigEndian(message, 16, offset, 2 * BruteForceCoding.BSIZE);
        // ver
        offset = BruteForceCoding.encodeIntBigEndian(message, Constant.VERSION, offset, 2 * BruteForceCoding.BSIZE);
        // operation
        offset = BruteForceCoding.encodeIntBigEndian(message, OperationCode.OP_AUTH, offset, 4 * BruteForceCoding.BSIZE);
        // seqId
        offset = BruteForceCoding.encodeIntBigEndian(message, SequenceIdRecorder.getSeqId(), offset, 4 * BruteForceCoding.BSIZE);

        out.get().write(BruteForceCoding.add(message, bytes));
        out.get().flush();

        return true;
    }

    /**
     * Send the given message to the server.
     *
     * @return true if the message was sent to the server.
     * @throws IOException
     */
    public synchronized Boolean heartBeatWrite() throws IOException {
        if (isStopped()) return false;
        // String msg = uid + "," + getCode(token);
        String msg = ""; //心跳不做验证，只需发送空包
        byte[] bytes = msg.getBytes();
        int packLength = bytes.length + 16;
        byte[] message = new byte[4 + 2 + 2 + 4 + 4];

        // package length
        int offset = BruteForceCoding.encodeIntBigEndian(message, packLength, 0, 4 * BruteForceCoding.BSIZE);
        // header length
        offset = BruteForceCoding.encodeIntBigEndian(message, 16, offset, 2 * BruteForceCoding.BSIZE);
        // ver
        offset = BruteForceCoding.encodeIntBigEndian(message, Constant.VERSION, offset, 2 * BruteForceCoding.BSIZE);
        // operation
        offset = BruteForceCoding.encodeIntBigEndian(message, OperationCode.OP_HEARTBEAT, offset, 4 * BruteForceCoding.BSIZE);
        // seqId
        offset = BruteForceCoding.encodeIntBigEndian(message, SequenceIdRecorder.getSeqId(), offset, 4 * BruteForceCoding.BSIZE);

        out.get().write(BruteForceCoding.add(message, bytes));
        out.get().flush();

        return true;
    }

    /**
     * Send the given message to the server.
     *
     * @param packet
     * @return true if the message was sent to the server.
     * @throws IOException
     */
    public synchronized Boolean messageWrite(Packet packet) throws IOException {
        if (isStopped()) return false;
        // String msg = uid + "," + getCode(packet.getBody());
        String msg = packet.getBody();
        byte[] bytes = msg.getBytes();
        int packLength = bytes.length + 16;
        byte[] message = new byte[4 + 2 + 2 + 4 + 4];

        // package length
        int offset = BruteForceCoding.encodeIntBigEndian(message, packLength, 0, 4 * BruteForceCoding.BSIZE);
        // header length
        offset = BruteForceCoding.encodeIntBigEndian(message, 16, offset, 2 * BruteForceCoding.BSIZE);
        // ver
        offset = BruteForceCoding.encodeIntBigEndian(message, packet.getVersion(), offset, 2 * BruteForceCoding.BSIZE);
        // operation
        offset = BruteForceCoding.encodeIntBigEndian(message, packet.getOperation(), offset, 4 * BruteForceCoding.BSIZE);
        // seqId
        offset = BruteForceCoding.encodeIntBigEndian(message, packet.getSeqId(), offset, 4 * BruteForceCoding.BSIZE);

        out.get().write(BruteForceCoding.add(message, bytes));
        out.get().flush();

        return true;
    }

    /****
     * get code
     */
    private int getCode(String code) {
        int sum = 0;
        byte[] array = code.getBytes();
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum;
    }

    /*****
     * heart beat Thread
     */
    private void heartBeat() {
        Thread hbThread = new Thread(new HeartbeatTask());
        // hbThread.setDaemon(true); // auto-close when disconnected
        hbThread.start();
    }

    class HeartbeatTask implements Runnable {

        @Override
        public void run() {
            // !Thread.currentThread().isInterrupted()
            while (isRunning()) {
                try {
                    Thread.sleep(defaultHeartBeatTimeOut);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    heartBeatWrite();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Callback method for when the client receives a message from the server.
     *
     * @param message the message from the server.
     */
    protected abstract void messageReceived(int packageLength, int headLength, int version, int operation, int sequenceId, String message);

    /**
     * Callback method for when the client receives a message from the server.
     */
    protected abstract void heartBeatReceived();

    /**
     * Callback method for when the client receives a message from the server.
     */
    protected abstract void authSuccess();

    /**
     * Callback method for when the client connects to the server.
     *
     * @param alreadyConnected whether the client was already connected to the server.
     */
    protected abstract void connected(boolean alreadyConnected);

    /**
     * Callback method for when the client disconnects from the server.
     */
    protected abstract void disconnected();

}
