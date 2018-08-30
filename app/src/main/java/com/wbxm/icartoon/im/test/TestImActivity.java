package com.wbxm.icartoon.im.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.eye.chart.R;
import com.wbxm.icartoon.im.MessageClient;
import com.wbxm.icartoon.im.listener.Callable;
import com.wbxm.icartoon.im.listener.IConnectListener;
import com.wbxm.icartoon.im.listener.IMessageHandler;
import com.wbxm.icartoon.im.listener.ISendListener;
import com.wbxm.icartoon.im.listener.IUploadExecutor;
import com.wbxm.icartoon.im.listener.IUploadListener;
import com.wbxm.icartoon.im.listener.RejectionDef;
import com.wbxm.icartoon.im.model.Message;
import com.wbxm.icartoon.im.model.MessageStatus;
import com.wbxm.icartoon.im.model.RejectionCode;
import com.wbxm.icartoon.im.util.Constant;
import com.wbxm.icartoon.im.util.MessageFactory;
import com.wbxm.icartoon.im.util.ThreadUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * IM测试类
 * 用两个手机安装相应的包（只需修改uid和token）。可以实现通信
 *
 * @author ycb
 * @date 2018/8/27
 */
public class TestImActivity extends AppCompatActivity implements ISendListener<Message>,
        IMessageHandler, IUploadExecutor, IConnectListener {

    private static final int uid = 22222;
    private int toUid = 11111;
    //uid=22222的token
    private static final String token = "VYCU5WafEplIQNqtpIytxLf2zxpvcWpejij6p/Baw7f/tsGn/498fkxd4nQQHgWmZP5tLpCUecojc1Sl0B1o4mOv/ZE180U5NaQYjWlZ40EveG81RbkDtAnkyIEaGieXbeFNpfKcbI8/z7jRNofhIzeJqCFH/Tad6U0RMzLQPNI=";
    //uid=11111的token
    //private static final String token ="VYCU5WafEplIQNqtpIytxJXtaFW2+FjfDgH3PwX8WAH/tsGn/498fkxd4nQQHgWmZP5tLpCUecojc1Sl0B1o4mOv/ZE180U5NaQYjWlZ40EveG81RbkDtAnkyIEaGieXbeFNpfKcbI8/z7jRNofhIzeJqCFH/Tad6U0RMzLQPNI=";

    MessageClient client;

    private EditText editText;
    private List<Message> data = new ArrayList<>();
    private DataAdapter adapter = new DataAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im);
        editText = (EditText) findViewById(R.id.et);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        client = new MessageClient.Builder()
                .setHost(Constant.HOST)
                .setPort(Constant.PORT)
                .setToken(token)
                .setUid(uid)
                .build();
        client.setSendListener(this)
                .setMessageHandler(this)
                .setConnectListener(this)
                .setUploadExecutor(this);
        client.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.stop();
    }

    @Override
    @WorkerThread
    public void connected(boolean connected) {
        if (connected) {
            ThreadUtil.runOnUi(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(TestImActivity.this, "connect succeed", Toast.LENGTH_SHORT).show();
                }
            });

            //连接成功后自动上传未同步的消息
            for (Message message : data) {
                if (message.isNeedSync()) {
                    resend(message);
                }
            }
        } else {
            //TODO 重连
            client.restart();
        }
    }

    @Override
    @WorkerThread
    public void disconnected() {
        ThreadUtil.runOnUi(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TestImActivity.this, "disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    @WorkerThread
    public void handleMessage(@NonNull final Message message) {
        // FIXME 返回来的数据不一定是有序的，需要根据message.id对其进行排序
        // 排序的时候还需要考虑发送失败的数据，这些数据并没有message.id
        ThreadUtil.runOnUi(new Runnable() {
            @Override
            public void run() {
                data.add(message);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    @WorkerThread
    public void onSendSucceed(Message message) {
        //FIXME 每条信息同步到服务器后，服务器会返回一个数据id(message.id)，根据该id来排序。
        // 排序的时候还需要考虑发送失败的数据，这些数据并没有message.id
        ThreadUtil.runOnUi(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    @UiThread
    public void onRejected(Message message, @RejectionDef int errorCode, @Nullable String errorMsg) {
        switch (errorCode) {
            case RejectionCode.FILE_PEAK: {
                Toast.makeText(this, "发送文件太快,请稍后重试!", Toast.LENGTH_SHORT).show();
            }
            case RejectionCode.TEXT_PEAK: {
                Toast.makeText(this, "发送信息太快,请稍后重试!", Toast.LENGTH_SHORT).show();
            }
            case RejectionCode.UPLOAD_ERROR: {
                Toast.makeText(this, "发送文件失败,请重试!", Toast.LENGTH_SHORT).show();
            }
            case RejectionCode.SEND_ERROR: {
                Toast.makeText(this, "发送信息失败,请重试!", Toast.LENGTH_SHORT).show();
            }
        }

        // send failed
        adapter.notifyItemChanged(data.indexOf(message));
    }

    /**
     * 重新发送
     *
     * @param message
     */
    public void resend(Message message) {
        message.setSyncStatus(MessageStatus.SYNCING);
        adapter.notifyItemChanged(data.indexOf(message));
        client.sendMessage(message);
    }

    /**
     * 发送消息
     */
    public void sendText(View v) {
        String content = editText.getText().toString();
        content = TextUtils.isEmpty(content) ? "send content" : content;
        Message message = MessageFactory.createMessage(content, Message.Type.TEXT, uid, toUid);
        data.add(message);
        adapter.notifyDataSetChanged();
        client.sendMessage(message);
    }

    /**
     * 发送图片
     */
    public void sendImage(View v) {
        String localFile = "http://img05.tooopen.com/images/20150820/tooopen_sy_139205349641.jpg";
        Message message = MessageFactory.createMessage(localFile, Message.Type.IMAGE, uid, toUid);
        data.add(message);
        adapter.notifyDataSetChanged();
        client.sendMessage(message);
    }

    @Override
    public void uploadFile(final Message message, final IUploadListener listener) {
        //TODO 上传文件，上传成功后将文件在服务器存放的地址返回
        ThreadUtil.asyncTask(new Runnable() {
            @Override
            public void run() {
                try {
                    //mock upload file
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String serverUrl = "http://img.zcool.cn/community/0117e2571b8b246ac72538120dd8a4.jpg@1280w_1l_2o_100sh.jpg";
                listener.onUploadSucceed(message, serverUrl);
            }
        });
    }

    private class DataAdapter extends RecyclerView.Adapter<DataHolder> {
        @Override
        public DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DataHolder(LayoutInflater.from(TestImActivity.this).inflate(R.layout
                    .holder_item_chart, null));
        }

        @Override
        public void onBindViewHolder(DataHolder holder, int position) {
            Message message = data.get(position);
            if (message.isImage()) {
                holder.tv.setText(message.getFromId() + " : ");
                holder.iv.setVisibility(View.VISIBLE);
                loadBitmap(message.getContent(), holder.iv);
            } else {
                holder.tv.setText(message.getFromId() + " : " + message.getContent());
                holder.iv.setVisibility(View.GONE);
            }
            switch (message.getSyncStatus()) {
                case MessageStatus.UN_SYNC:
                case MessageStatus.FAILED:
                    holder.pb.setVisibility(View.GONE);
                    holder.failTv.setVisibility(View.VISIBLE);
                    holder.failTv.setTag(message);
                    holder.failTv.setOnClickListener(clickListener);
                    break;
                case MessageStatus.SYNCING:
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.failTv.setVisibility(View.GONE);
                    break;
                case MessageStatus.SYNCED:
                    holder.pb.setVisibility(View.GONE);
                    holder.failTv.setVisibility(View.GONE);
                    break;
            }
        }

        private View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resend((Message) v.getTag());
            }
        };

        @Override
        public int getItemCount() {
            return data.size();
        }

        /**
         * FIXME 请使用图片框架代替,这里加载图片会有延迟、卡顿（请忽略）
         *
         * @param imgUrl
         * @param iv
         */
        private void loadBitmap(final String imgUrl, final ImageView iv) {
            ThreadUtil.asyncTask(new Callable<Bitmap>() {
                @Override
                public Bitmap runAsync() {
                    InputStream inputStream = null;
                    try {
                        //对资源链接
                        URL url = new URL(imgUrl);
                        //打开输入流
                        inputStream = url.openStream();
                        //对网上资源进行下载转换位图图片
                        return BitmapFactory.decodeStream(inputStream);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return null;
                }

                @Override
                public void runOnUi(Bitmap data) {
                    if (data != null) {
                        iv.setImageBitmap(data);
                    }
                }
            });
        }
    }

    private class DataHolder extends RecyclerView.ViewHolder {

        TextView tv;
        ImageView iv;
        ProgressBar pb;
        TextView failTv;

        public DataHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_item);
            pb = (ProgressBar) itemView.findViewById(R.id.pb);
            failTv = (TextView) itemView.findViewById(R.id.failed);
            iv = (ImageView) itemView.findViewById(R.id.iv_item);
        }
    }
}
