package com.wbxm.icartoon.im.listener;

/**
 * @author ycb
 * @date 2018/8/22
 */
public interface Callable<T> {

    /**
     * call on sub thread
     * @return
     */
    T runAsync();

    /**
     * call on ui thread
     * @param data
     */
    void runOnUi(T data);
}
