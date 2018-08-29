package com.wbxm.icartoon.im.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 记录发送消息的seqId
 *
 * @author ycb
 * @date 2018/8/27
 */
public class SequenceIdRecorder {

    private static AtomicInteger counter = new AtomicInteger(0);

    public static int getSeqId() {
        return counter.getAndIncrement();
    }
}
