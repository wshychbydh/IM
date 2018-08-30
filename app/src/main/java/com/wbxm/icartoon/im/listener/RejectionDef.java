package com.wbxm.icartoon.im.listener;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.wbxm.icartoon.im.model.RejectionCode.FILE_PEAK;
import static com.wbxm.icartoon.im.model.RejectionCode.SEND_ERROR;
import static com.wbxm.icartoon.im.model.RejectionCode.TEXT_PEAK;
import static com.wbxm.icartoon.im.model.RejectionCode.UPLOAD_ERROR;

/**
 * 发送消息失败的错误码约束
 * @author ycb
 * @date 2018/8/27
 */
@IntDef({TEXT_PEAK, FILE_PEAK, SEND_ERROR, UPLOAD_ERROR})
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RejectionDef {
}
