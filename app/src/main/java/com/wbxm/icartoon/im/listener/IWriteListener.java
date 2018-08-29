package com.wbxm.icartoon.im.listener;

import com.wbxm.icartoon.im.model.Packet;

import java.io.IOException;

/**
 * 发送数据包接口
 * @author ycb
 * @date 2018/8/22
 */
public interface IWriteListener {

    boolean write(Packet packet) throws IOException;

}
