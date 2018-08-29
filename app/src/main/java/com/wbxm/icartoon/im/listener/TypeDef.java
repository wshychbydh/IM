package com.wbxm.icartoon.im.listener;

import android.support.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.wbxm.icartoon.im.model.Message.Type.AUDIO;
import static com.wbxm.icartoon.im.model.Message.Type.IMAGE;
import static com.wbxm.icartoon.im.model.Message.Type.TEXT;
import static com.wbxm.icartoon.im.model.Message.Type.VIDEO;

/**
 * @author ycb
 * @date 2018/8/27
 */
@StringDef({TEXT, IMAGE, AUDIO, VIDEO})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
public @interface TypeDef {
}
