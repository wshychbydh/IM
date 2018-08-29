package com.wbxm.icartoon.im.model;

/**
 * 发送信息被拒时，返回的错误码
 *
 * @author ycb
 * @date 2018/8/27
 */
public final class RejectionCode {

    public static final int TEXT_PEAK = 4001; //未发送文字达到峰值
    public static final int FILE_PEAK = 4002; //未发送文件达到峰值
    public static final int SEND_ERROR = 4009; //发送信息失败，包括文本和文件
    public static final int UPLOAD_ERROR = 4010; //上传文件失败
}
