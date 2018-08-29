package com.wbxm.icartoon.im;

import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 延时任务
 * @author ycb
 * @date 2018/8/23
 */
public class DelayTask<Task> implements Delayed {

    private long delayTime;

    private Task task;

    private long counter;

    private static final AtomicLong atomic = new AtomicLong(0L);

    public DelayTask(Task task, long delayTime) {
        this.delayTime = delayTime;
        this.task = task;
        this.counter = atomic.getAndIncrement();

    }

    @Override
    public long getDelay(@NonNull TimeUnit unit) {
        return unit.convert(this.delayTime - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(@NonNull Delayed other) {
        if (other == this)
            return 0;

        if (other instanceof DelayTask) {
            DelayTask<?> message = (DelayTask<?>) other;
            long diff = delayTime - message.delayTime;
            if (diff < 0) {
                return -1;
            } else if (diff > 0) {
                return 1;
            } else if (task instanceof Comparator) {
                return ((Comparator) task).compare(task, message.task);
            } else if (counter < message.counter) {
                return -1;
            } else return 1;
        }

        long delay = (getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS));
        return (delay == 0) ? 0 : ((delay < 0) ? -1 : 1);
    }

    public Task getTask() {
        return task;
    }

    @Override
    public int hashCode() {
        return task.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DelayTask) {
            return object.hashCode() == hashCode() ? true : false;
        }
        return false;
    }

}
