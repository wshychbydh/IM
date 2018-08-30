package com.wbxm.icartoon.im.listener;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.wbxm.icartoon.im.model.MessageStatus.FAILED;
import static com.wbxm.icartoon.im.model.MessageStatus.SYNCED;
import static com.wbxm.icartoon.im.model.MessageStatus.SYNCING;
import static com.wbxm.icartoon.im.model.MessageStatus.UN_SYNC;


/**
 * 消息状态约束
 * @author ycb
 * @date 2018/8/27
 */
@IntDef({UN_SYNC, SYNCING, SYNCED, FAILED})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
public @interface StatusDef {
}
