package com.wbxm.icartoon.im.util;

import android.os.Handler;
import android.os.Looper;

import com.wbxm.icartoon.im.listener.Callable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ycb
 * @date 2018/8/22
 */
public class ThreadUtil {

    public static ExecutorService readExecutor = Executors.newSingleThreadExecutor();
    public static ExecutorService writeExecutor = Executors.newSingleThreadExecutor();
    private static ExecutorService executor = Executors.newCachedThreadPool();

    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void asyncTask(Runnable runnable) {
        executor.execute(runnable);
    }

    public static <T> void asyncTask(final Callable<T> callable) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final T data = callable.runAsync();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callable.runOnUi(data);
                    }
                });
            }
        });

    }

    public static void runOnUi(Runnable runnable) {
        mainHandler.post(runnable);
    }
}
