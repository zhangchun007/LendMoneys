package com.haiermerchant.activity;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.haiermerchant.camera.CameraManager;
import com.haiermerchant.decoding.CaptureActivityHandler;
import com.haiermerchant.decoding.InactivityTimer;

import java.io.IOException;
import java.util.Vector;

import erweima.hunofox.com.twodimensioncode.R;

import com.haiermerchant.view.ViewfinderView;

import static android.content.Context.AUDIO_SERVICE;


/**
 * zxing   扫描二维码，识别相册中的二维码
 */
public class ZXingFragment extends Fragment implements SurfaceHolder.Callback {


    private ViewfinderView viewfinderView;//扫描控件
    private ImageButton btnFlash;//开启闪光灯
    private SurfaceView surfaceView;
    private boolean isFlashOn = false;//判断是否开启了闪光灯
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private boolean playBeep;
    private boolean vibrate;
    private CaptureActivityHandler handler;
    private MediaPlayer mediaPlayer;
    private static final float BEEP_VOLUME = 0.10f;

//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.zxing_activity);
//        CameraManager.init(getApplication());
//        findview();
////        getData();
//
//        hasSurface = false;
//        inactivityTimer = new InactivityTimer(this);
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.zxing_activity, container, false);
        CameraManager.init(getActivity().getApplication());
        findview(view);
//        getData();

        hasSurface = false;
        inactivityTimer = new InactivityTimer(getActivity());
        return view;

    }

    private void findview(View view){
        surfaceView =  view.findViewById(R.id.scanner_view);
        viewfinderView = view.findViewById(R.id.viewfinder_content);
        btnFlash =  view.findViewById(R.id.btn_flash);
        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    boolean isSuccess = CameraManager.get().setFlashLight(!isFlashOn);
                    if(!isSuccess){
                        Toast.makeText(getActivity(),"暂时无法开启闪光灯",Toast.LENGTH_LONG).show();;
                        return;
                    }
                    if (isFlashOn) {
                        // 关闭闪光灯
                        btnFlash.setImageResource(R.drawable.flash_off);
                        isFlashOn = false;
                    } else {
                        // 开启闪光灯
                        btnFlash.setImageResource(R.drawable.flash_on);
                        isFlashOn = true;
                    }
                }catch (Exception e){
                    Log.e("条形码扫描",e.toString());
                    e.printStackTrace();
                }
            }
        });

    }
    //--------------------------------------以下为zxing扫描方法----------------------------------------------------------
    @Override
    public void onResume() {
        super.onResume();


        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;


    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(com.google.zxing.Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();//扫描的内容

        successful(resultString);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Activity.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    /**
     * 连续扫描
     */
    protected void restartPreview(){
        if (handler != null)
        {//实现连续扫描
            Message mes = new Message();
            mes.what = R.id.restart_preview;
            handler.handleMessage(mes);
        }
    }

    //扫描成功后或者识别相册二维码成功后调用
    protected void successful(String str){}

}
