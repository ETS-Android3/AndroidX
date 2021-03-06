package com.androidx.video;

import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.androidx.R;
import com.androidx.app.CoreActivity;
import com.androidx.app.CoreFragment;
import com.androidx.app.NavigationBar;
import com.androidx.view.CircleTimerView;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 视频录制
 */
public class VideoRecordAty extends CoreActivity implements SurfaceHolder.Callback, View.OnClickListener, CircleTimerView.OnTimerChangeListener {

    public final String TAG = VideoRecordAty.class.getSimpleName();
    public static final String VIDEO_PARAMS = "params";
    public static final int REQUEST_CODE = 324;

    private SurfaceView surfaceView;
    private CircleTimerView timerButton;
    private ImageView iv_finish;
    private LinearLayout ll_finish;
    private ImageView iv_flash;
    private ImageView iv_switch;
    private ImageView iv_ok;
    private ImageView iv_cancel;
    private TextView tv_time;
    private View v_right;

    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private MediaRecorder mediaRecorder;
    private boolean isFront;
    private boolean isRecording;

    private VideoRecordParams params;
    private String videoFolder;
    private String videoPath;
    private int width = 1920;
    private int height = 1080;
    private int duration = 15 * 1000;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int videoEncoder = MediaRecorder.VideoEncoder.H264;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.android_video_record;
    }

    /**
     * 跳转到视频录制页面
     *
     * @param activity 页面
     * @param params   参数
     */
    public static void start(CoreActivity activity, VideoRecordParams params) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(VIDEO_PARAMS, params);
        activity.startActivityForResult(VideoRecordAty.class, REQUEST_CODE, bundle);
    }

    /**
     * 跳转到视频录制页面
     *
     * @param fragment 页面
     * @param params   参数
     */
    public static void start(CoreFragment fragment, VideoRecordParams params) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(VIDEO_PARAMS, params);
        fragment.startActivityForResult(VideoRecordAty.class, REQUEST_CODE, bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, NavigationBar bar) {
        bar.hide();
        if (getIntent().getSerializableExtra(VIDEO_PARAMS) != null) {
            params = (VideoRecordParams) getIntent().getSerializableExtra(VIDEO_PARAMS);
            videoFolder = params.getVideoFolder();
            videoPath = params.getVideoPath();
            width = params.getWidth();
            height = params.getHeight();
            duration = params.getDuration();
            cameraId = params.getCameraId();
            videoEncoder = params.getVideoEncoder();
        }
        //find view by id
        surfaceView = findViewById(R.id.android_sv_camera);
        timerButton = findViewById(R.id.android_btn_start);
        iv_flash = findViewById(R.id.android_iv_flash);
        iv_switch = findViewById(R.id.android_iv_switch);
        iv_finish = findViewById(R.id.android_iv_finish);
        ll_finish = findViewById(R.id.android_ll_finish);
        tv_time = findViewById(R.id.android_tv_time);
        iv_ok = findViewById(R.id.android_iv_ok);
        iv_cancel = findViewById(R.id.android_iv_cancel);
        v_right = findViewById(R.id.v_right);
        //点击事件
        iv_finish.setOnClickListener(this);
        iv_cancel.setOnClickListener(this);
        iv_ok.setOnClickListener(this);
        iv_switch.setOnClickListener(this);
        iv_flash.setOnClickListener(this);
        //设置显示隐藏
        ll_finish.setVisibility(View.VISIBLE);
        iv_cancel.setVisibility(View.GONE);
        iv_ok.setVisibility(View.GONE);
        //初始化类
        handler = new VideoHandler();
        mediaRecorder = new MediaRecorder();
        //预览初始化
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                focusOnTouch(motionEvent);
                return false;
            }
        });
        //计时器按钮
        timerButton.setDuration(duration);
        timerButton.setOnTimerChangeListener(this);
    }

    @Override
    public void onTimerStart() {
        iv_flash.setEnabled(false);
        iv_switch.setEnabled(false);
        if (mediaRecorder != null && !isRecording && camera != null) {
            camera.unlock();
            mediaRecorder.start();
            startVideoTimer();
        }
    }

    @Override
    public void onTimerChanged(long duration, long millisInFuture) {

    }

    @Override
    public void onTimerFinish() {
        iv_flash.setEnabled(true);
        iv_switch.setEnabled(true);
        isRecording = false;
        iv_finish.setVisibility(View.GONE);
        timerButton.setVisibility(View.GONE);
        v_right.setVisibility(View.GONE);
        ll_finish.setVisibility(View.VISIBLE);
        iv_cancel.setVisibility(View.VISIBLE);
        iv_ok.setVisibility(View.VISIBLE);
        if (mediaRecorder != null) {
            mediaRecorder.setOnErrorListener(null);
            mediaRecorder.setPreviewDisplay(null);
            try {
                mediaRecorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        stopVideoTimer();
    }

    @Override
    public void surfaceCreated(final SurfaceHolder surfaceHolder) {
        sendSurfaceCreated();
    }


    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        releaseCamera();
        releaseMediaRecorder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
        releaseMediaRecorder();
        if (handler != null) {
            handler.removeMessages(0);
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    /**
     * 初始化摄像头
     *
     * @param cameraId eg for Camera.CameraInfo.CAMERA_FACING_BACK
     */
    private int[] initCamera(int cameraId) {
        if (camera != null) {
            camera.release();
        }
        camera = Camera.open(cameraId);
        //获取camera的parameter实例
        Camera.Parameters parameters = camera.getParameters();
        parameters.set("orientation", "portrait");
        //获取所有支持的camera尺寸
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        for (int i = 0; i < sizeList.size(); i++) {
            Camera.Size size = sizeList.get(i);
            if (size.width == width && size.height == height) {
                width = size.width;
                height = size.height;
            }
            Log.i("SupportedPreviewSizes", i + " - " + size.width + " x " + size.height);
        }
        Camera.Size optimalSize = getOptimalPreviewSize(sizeList, width, height);
        Log.i("SupportedPreviewSizes", "Target preview size " + optimalSize.width + " x " + optimalSize.height);
        //把camera.size赋值到parameters
        parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        //装换摄像头需要注释此代码
        //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        //把parameters设置给camera
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
        camera.cancelAutoFocus();
        return new int[]{width, height};
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    /**
     * 释放摄像头
     */
    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    /**
     * 摄像头聚焦
     *
     * @param event
     */
    public void focusOnTouch(MotionEvent event) {
        if (camera != null) {
            try {
                camera.cancelAutoFocus();
            } catch (Exception e) {

            }
            Rect focusRect = new Rect(-1000, -1000, 1000, 1000);
            Rect meteringRect = new Rect(-1000, -1000, 1000, 1000);
            Camera.Parameters parameters = null;
            try {
                parameters = camera.getParameters();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (parameters != null) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    List<Camera.Area> focus = new ArrayList<>();
                    focus.add(new Camera.Area(focusRect, 1000));
                    parameters.setFocusAreas(focus);
                    if (parameters.getMaxNumMeteringAreas() > 0) {
                        List<Camera.Area> metering = new ArrayList<>();
                        metering.add(new Camera.Area(meteringRect, 1000));
                        parameters.setMeteringAreas(metering);
                    }
                }
                try {
                    camera.setParameters(parameters);
                    camera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取媒体录制
     *
     * @return
     */
    public MediaRecorder getMediaRecorder() {
        return mediaRecorder;
    }

    /**
     * 设置媒体录制
     *
     * @param mediaRecorder
     */
    public void setMediaRecorder(MediaRecorder mediaRecorder) {
        this.mediaRecorder = mediaRecorder;
    }

    /**
     * 初始化录制对象
     *
     * @param camera        摄像头
     * @param mediaRecorder 录制对象
     * @param surfaceHolder 图像对象
     * @param width         显示的宽
     * @param height        显示的高度
     */
    private void initMediaRecorder(Camera camera, MediaRecorder mediaRecorder, SurfaceHolder surfaceHolder, int width, int height) {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }
        mediaRecorder.reset();
        if (camera != null) {
            mediaRecorder.setCamera(camera);
        }
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {

            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                // 发生错误，停止录制
                getMediaRecorder().stop();
                getMediaRecorder().release();
                setMediaRecorder(null);
            }
        });
        // 设置音频采集方式
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        //设置视频的采集方式
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //设置文件的输出格式
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //设置audio的编码格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //设置video的编码格式
        mediaRecorder.setVideoEncoder(videoEncoder);
        //设置录制的视频编码比特率
        mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        //设置要捕获的视频的宽度和高度
//        mediaRecorder.setVideoSize(width, height);//有些设备支持设置
        //设置记录会话的最大持续时间（毫秒）
//      mediaRecorder.setMaxDuration(60 * 1000);
        mediaRecorder.setOrientationHint(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK ? 90 : 270);//90 270
        if (videoFolder == null) {
            videoFolder = getExternalCacheDir().getAbsolutePath();
            params.setVideoFolder(videoFolder);
        }
        File dir = new File(videoFolder + "/Videos");
        if (!dir.exists()) {
            dir.mkdir();
        }
        if (videoPath == null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMDDHHmmss");
            String name = "VIDEO_" + format.format(new Date()) + ".mp4";
            videoPath = dir + "/" + name;
            params.setVideoPath(videoPath);
        }
        //设置输出文件的路径
        mediaRecorder.setOutputFile(videoPath);
        //准备录制
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放录制对象
     */
    protected void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.android_iv_switch) {
            cameraId = isFront ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT;
            int size[] = initCamera(cameraId);
            initMediaRecorder(camera, mediaRecorder, surfaceHolder, size[0], size[1]);
            isFront = !isFront;
        } else if (id == R.id.android_iv_finish) {
            finish();
        } else if (id == R.id.android_iv_cancel) {
            new File(videoPath).deleteOnExit();
            finish();
        } else if (id == R.id.android_iv_ok) {
            Intent intent = new Intent();
            intent.putExtra(VIDEO_PARAMS, params);
            setResult(RESULT_OK, intent);
            finish();
        }
        if (id == R.id.android_iv_flash) {
            isOpenFlash = !isOpenFlash;
            setFlash(isOpenFlash);
        }
    }

    private boolean isOpenFlash;

    /**
     * 设置闪光灯
     *
     * @param open 是否打开
     */
    protected void setFlash(boolean open) {
        //获取camera的parameter实例
        Camera.Parameters parameters = camera.getParameters();
        if (open) {
            //打开闪光灯
            iv_flash.setImageResource(R.drawable.android_ic_video_flash_uncheck);
            //开启
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        } else {
            //关闭闪光灯
            iv_flash.setImageResource(R.drawable.android_ic_video_flash_check);
            //关闭
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        camera.setParameters(parameters);
    }

    /**
     * 显示视频时间
     *
     * @param time   时间
     * @param tvShow 控件
     */
    private void showVideoTime(long time, TextView tvShow) {
        DecimalFormat format = new DecimalFormat("00");
        long second = time / 1000;
        long hour = second / 60 / 60;
        String timeText = "";
        if (hour > 0) {
            long videoMinutes = (second - hour * 3600) / 60;
            long videoSecond = second % 60;
            timeText = format.format(hour) + ":" + format.format(videoMinutes) + ":" + format.format(videoSecond);
        } else {
            long videoSecond = second % 60;
            long videoMinutes = second / 60;
            timeText = format.format(videoMinutes) + ":" + format.format(videoSecond);
        }
        tvShow.setText(timeText);
    }

    /**
     * 开始视频计时器
     */
    protected void startVideoTimer() {
        videoTime = System.currentTimeMillis();
        if (handler != null) {
            handler.sendEmptyMessage(WHAT_VIDEO_TIME);
        }
    }

    /**
     * 停止视频计时器
     */
    protected void stopVideoTimer() {
        if (handler != null) {
            handler.removeMessages(WHAT_VIDEO_TIME);
        }
    }

    /**
     * 发送SurfaceCreated消息
     */
    protected void sendSurfaceCreated() {
        handler.sendEmptyMessageDelayed(WHAT_SURFACE_CREATED, 50);
    }

    private long videoTime = 0;
    private VideoHandler handler;
    private boolean isOpenIndicator;
    private final int WHAT_VIDEO_TIME = 0;
    private final int WHAT_SURFACE_CREATED = 1;

    /**
     * VideoHandler中间类
     */
    private class VideoHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_SURFACE_CREATED:
                    int previewSizes[] = initCamera(cameraId);
                    initMediaRecorder(camera, mediaRecorder, surfaceHolder, previewSizes[0], previewSizes[1]);
                    break;
                case WHAT_VIDEO_TIME:
                    isOpenIndicator = !isOpenIndicator;
                    tv_time.setCompoundDrawablesWithIntrinsicBounds(isOpenIndicator ? R.drawable.android_video_red_dot : R.drawable.android_video_white_dot, 0, 0, 0);
                    long pass = System.currentTimeMillis() - videoTime;
                    showVideoTime(pass, tv_time);
                    handler.sendEmptyMessageDelayed(0, 100);
                    break;
            }
        }
    }

}
