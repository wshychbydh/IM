package com.wbxm.icartoon.im.listener;

import com.wbxm.icartoon.im.model.Packet;

import java.io.IOException;

/**
 * 发送数据包接口
 * @author ycb
 * @date 2018/8/22
 */
public interface IWriteListener {

    /**
     * 向服务器写数据包
     * @param packet 包装消息生成的数据包
     * @return true上传成功；false上传失败/连接中断
     * @throws IOException
     */
    boolean write(Packet packet) throws IOException;

}
