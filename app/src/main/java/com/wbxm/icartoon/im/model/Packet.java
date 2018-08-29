package com.wbxm.icartoon.im.model;


import com.alibaba.fastjson.annotation.JSONField;
import com.wbxm.icartoon.im.util.Constant;

/**
 * 协议规定的数据包，请求和返回数据一致。见pdf
 *
 * @author ycb
 * @date 2018/8/22
 */

public class Packet {

    @JSONField(name = "ver")
    private int version = Constant.VERSION;     //    `json:"ver"`  // protocol version
    @JSONField(name = "op")
    private int operation; //     `json:"op"`   // operation for request
    @JSONField(name = "seq")
    private int seqId = -1;    //      `json:"seq"`  // sequence number chosen by client
    @JSONField(name = "body")
    private String body;    // ``json:"body"` // binary body bytes(json.RawMessage is

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public int getSeqId() {
        return seqId;
    }

    public void setSeqId(int seqId) {
        this.seqId = seqId;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "version=" + version +
                ", operation=" + operation +
                ", seqId=" + seqId +
                ", body='" + body + '\'' +
                '}';
    }
}
