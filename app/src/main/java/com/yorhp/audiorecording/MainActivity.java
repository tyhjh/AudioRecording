package com.yorhp.audiorecording;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yorhp.audiolibrary.AudioRecordUtil;

import permison.PermissonUtil;

public class MainActivity extends AppCompatActivity {

    Button btn_audio;
    AudioRecordUtil recordUtil;
    float y;
    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AudioRecordUtil.init(Environment.getExternalStorageDirectory() + "/audio/");
        recordUtil = AudioRecordUtil.getInstance();
        PermissonUtil.checkPermission(this, null, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE});
        btn_audio = findViewById(R.id.btn_audio);
        record();
        btn_audio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        y = event.getRawY();
                        time = System.currentTimeMillis();
                        mPop.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                        try {
                            recordUtil.startRecord(MainActivity.this);
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                            Snackbar.make(btn_audio, "先允许调用系统录音权限", Snackbar.LENGTH_SHORT).show();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() - time < 1000) {
                            Snackbar.make(btn_audio, "录音时间过短，请重试", Snackbar.LENGTH_SHORT).show();
                            recordUtil.cancelRecord(MainActivity.this);
                            mPop.dismiss();
                            break;
                        } else if (y - event.getRawY() > 300) {
                            Snackbar.make(btn_audio, "已取消录制语音", Snackbar.LENGTH_SHORT).show();
                            recordUtil.cancelRecord(MainActivity.this);
                            mPop.dismiss();
                            break;
                        } else {
                            try {
                                recordUtil.stopRecord(MainActivity.this);        //结束录音（保存录音文件）
                            } catch (Exception e) {
                                e.printStackTrace();
                                Snackbar.make(btn_audio, "先允许调用系统录音权限", Snackbar.LENGTH_SHORT);
                            }
                            mPop.dismiss();
                            break;
                        }
                    case MotionEvent.ACTION_CANCEL:
                        recordUtil.cancelRecord(MainActivity.this); //取消录音（不保存录音文件）
                        mPop.dismiss();
                        break;
                }
                return true;
            }
        });
    }


    PopupWindowFactory mPop;

    /**
     * 语音录制
     */
    private void record() {
        if (mPop != null) {
            return;
        }

        final View view = View.inflate(this, R.layout.dialog_record, null);
        final ImageView mImageView = (ImageView) view.findViewById(R.id.zeffect_recordbutton_dialog_imageview);
        final TextView mTextView = (TextView) view.findViewById(R.id.zeffect_recordbutton_dialog_time_tv);
        mPop = new PopupWindowFactory(this, view);

        AudioRecordUtil.getInstance().setOnAudioStatusUpdateListener(new AudioRecordUtil.OnAudioStatusUpdateListener() {
            @Override
            public void onUpdate(double db, long time) {
                //根据分贝值来设置录音时话筒图标的上下波动，下面有讲解
                mImageView.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
                mTextView.setText(TimeUtil.getTimeE(time).substring(14, 19));
            }

            @Override
            public void onStop(final String filePath) {
                Snackbar.make(btn_audio, "语音保存路径为：" + filePath, Snackbar.LENGTH_SHORT).setAction("查看", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, filePath, Toast.LENGTH_LONG).show();
                    }
                }).show();
            }
        });

    }

}
