package com.yorhp.audiolibrary;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class AudioRecordUtil {

    //文件路径
    private  String filePath;
    //文件夹路径
    private static String folderPath;
    private MediaRecorder mMediaRecorder;
    private final String TAG = "RecordUtil";
    public static final int MAX_LENGTH = 1000 * 60 * 10;// 最大录音时长1000*60*10;
    private OnAudioStatusUpdateListener audioStatusUpdateListener;

    /**
     * 文件存储默认sdcard/record
     */
    private AudioRecordUtil() {
    }

    public static AudioRecordUtil getInstance() {
        return Holder.recordUtil;
    }


    public static void init(String folderPath) {
        File path = new File(folderPath);
        if (!path.exists())
            path.mkdirs();
        AudioRecordUtil.folderPath = folderPath;
    }

    private long startTime;
    private long endTime;

    /**
     * 开始录音 使用amr格式
     * 录音文件
     *
     * @return
     */
    public String startRecord(Context context) {
        muteAudioFocus(context, true);
        // 开始录音
        /* ①Initial：实例化MediaRecorder对象 */
        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setOnErrorListener(null);
        try {
            /* ②setAudioSource/setVedioSource */
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            filePath = folderPath + System.currentTimeMillis() + ".amr";
            /* ③准备 */
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setMaxDuration(MAX_LENGTH);
            mMediaRecorder.prepare();
            /* ④开始 */
            mMediaRecorder.start();
            // AudioRecord audioRecord.
            /* 获取开始时间* */
            startTime = System.currentTimeMillis();
            updateMicStatus();
            Log.e("fan", "startTime" + startTime);
        } catch (IllegalStateException e) {
            Log.i(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        } catch (IOException e) {
            Log.i(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        }
        return filePath;
    }

    /**
     * 停止录音
     */
    public long stopRecord(Context context) {
        muteAudioFocus(context, false);
        if (mMediaRecorder == null)
            return 0L;
        endTime = System.currentTimeMillis();
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        audioStatusUpdateListener.onStop(filePath);
        filePath = "";
        return endTime - startTime;
    }

    /**
     * 取消录音
     */
    public void cancelRecord(Context context) {
        muteAudioFocus(context, false);
        if (mMediaRecorder == null)
            return;
        mMediaRecorder.setOnErrorListener(null);
        mMediaRecorder.setPreviewDisplay(null);
        try {
            mMediaRecorder.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists())
                file.delete();
            filePath = "";
        }
    }


    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };
    private int BASE = 1;
    private int SPACE = 50;// 间隔取样时间

    public void setOnAudioStatusUpdateListener(OnAudioStatusUpdateListener audioStatusUpdateListener) {
        this.audioStatusUpdateListener = audioStatusUpdateListener;
    }

    /**
     * 更新麦克状态
     */
    private void updateMicStatus() {
        if (mMediaRecorder != null) {
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;
            double db = 0;// 分贝
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
                if (null != audioStatusUpdateListener) {
                    audioStatusUpdateListener.onUpdate(db, System.currentTimeMillis() - startTime);
                }
            }
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    public interface OnAudioStatusUpdateListener {
        /**
         * 录音中...
         *
         * @param db   当前声音分贝
         * @param time 录音时长
         */
        public void onUpdate(double db, long time);

        /**
         * 停止录音
         *
         * @param filePath 保存路径
         */
        public void onStop(String filePath);
    }


    //录音时候关闭或开启其他声音
    public static boolean muteAudioFocus(Context context, boolean bMute) {
        if (context == null) {
            return false;
        }
        boolean bool = false;
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (bMute) {
            int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            int result = am.abandonAudioFocus(null);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
        return bool;
    }


    static class Holder {
        static AudioRecordUtil recordUtil = new AudioRecordUtil();
    }

}
