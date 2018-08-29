package com.wbxm.icartoon.im.model;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.annotation.JSONField;
import com.wbxm.icartoon.im.listener.StatusDef;
import com.wbxm.icartoon.im.listener.TypeDef;
import com.wbxm.icartoon.im.util.Constant;

/**
 * 消息内容实体类，见pdf
 *
 * @author ycb
 * @date 2018/8/21
 */

public class Message implements Comparable<Message> {

    public static class Type {
        public static final String TEXT = "text";
        public static final String IMAGE = "image";
        public static final String AUDIO = "audio";
        public static final String VIDEO = "video";
    }

    @JSONField(serialize = false)
    private int id;             // 可选，发送消息时不带，由服务生成，给接收消息的一方做去重
    @JSONField(name = "From_id")
    private int fromId;         // 发送消息uid , 必选
    @JSONField(name = "To_id")
    private int toId;
    @JSONField(name = "Action")
    private String action = "user";      //消息 action ，user 为用户私信 , 必选
    @JSONField(name = "Type")
    private String type = Type.TEXT;        //"text",text为 本，未来兼容 频，图 ,必选 。暂定512字节
    @JSONField(name = "Content")
    private String content;     //* 消息内容 , 必选
    @JSONField(name = "Platform")
    private int platform = 1;   //*0:IOS, 1:Android,2:web,必选
    @JSONField(name = "Version")
    private int version = Constant.VERSION;    //消息客户端版本号，发送端在协议 ，接收端在消息体内也会展现

    @JSONField(serialize = false)
    private int syncStatus;

    @StatusDef
    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(@StatusDef int syncStatus) {
        this.syncStatus = syncStatus;
    }

    public boolean isUnSynced() {
        return syncStatus == MessageStatus.UN_SYNC;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @TypeDef
    public String getType() {
        return type;
    }

    public void setType(@TypeDef String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isText() {
        return Type.TEXT.equals(type);
    }

    public boolean isImage() {
        return Type.IMAGE.equals(type);
    }

    @Override
    public int compareTo(@NonNull Message other) {
        int sn = id - other.getId();
        return (sn == 0) ? 0 : ((sn < 0) ? -1 : 1);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", fromId=" + fromId +
                ", toId=" + toId +
                ", action='" + action + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", platform=" + platform +
                ", version=" + version +
                '}';
    }
}
