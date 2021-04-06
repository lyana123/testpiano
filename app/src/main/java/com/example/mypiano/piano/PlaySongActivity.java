package com.example.mypiano.piano;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mypiano.DownloadListener;
import com.example.mypiano.DownloadUtil;
import com.example.mypiano.MyApplication;
import com.example.mypiano.R;
import com.example.mypiano.UploadUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class PlaySongActivity extends Activity implements View.OnClickListener {
    private TextView musicName,musicLength,musicCur;//音乐相关的信息
    private SeekBar seekBar;//进度条
    private Button btnPlay, btnStop, btnList, btnAi, quit;//各种按钮

    private final static String base_URL="http://47.94.219.201:8080/ImageUploadServer";
    private final static String down_URL="http://47.94.219.201:8080/ImageUploadServer/downloadsong?download=";
    private DownloadListener dl;

    private MediaPlayer mediaPlayer=new MediaPlayer();

    private AudioManager audioManager;

    private Timer timer;

    private boolean isSeekBarChanging;//互斥变量，防止进度条与定时器冲突。
    private int currentPosition;//当前音乐播放的进度

    SimpleDateFormat format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playsong);

        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);

        format = new SimpleDateFormat("mm:ss");

        btnPlay=(Button)findViewById(R.id.playBtn);
        btnStop=(Button)findViewById(R.id.stopBtn);
        btnList=(Button)findViewById(R.id.listBtn);
        btnAi=(Button)findViewById(R.id.aiBtn);
        quit=(Button)findViewById(R.id.quit);

        musicName = (TextView) findViewById(R.id.musicname);
        musicLength = (TextView) findViewById(R.id.music_length);
        musicCur = (TextView) findViewById(R.id.music_cur);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new MySeekBar());

        btnList.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnAi.setOnClickListener(this);
        quit.setOnClickListener(this);

        //申请播放权限
        if(ContextCompat.checkSelfPermission(PlaySongActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(PlaySongActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else {
            initMediaPlayer();
        }

        dl=new DownloadListener() {
            @Override
            public void onDownloadSuccess(String path) {
                Log.d("DownloadListener", "onDownloadSuccess: 成功！"+path);
            }

            @Override
            public void onDownloading(int progress) {
                Log.d("DownloadListener", "onDownloadloading: 下载中！"+progress);
            }

            @Override
            public void onDownloadFailed() {
                Log.d("DownloadListener", "onDownloadFailed: 失败！");
            }
        };
    }

    private void initMediaPlayer(){
        try{
            //路径
            String path= getIntent().getStringExtra("path");
//            File file=new File(path,"起风了.mid");
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {//确保在prepare()之前调用了stop()
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
            //利用路径初始化播放器
            mediaPlayer.setDataSource(path);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    seekBar.setMax(mediaPlayer.getDuration());
                    musicLength.setText(format.format(mediaPlayer.getDuration())+"");
                    musicCur.setText("00:00");
                    musicName.setText(getIntent().getStringExtra("name"));
                    currentPosition=0;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.playBtn:
                if(!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                    mediaPlayer.seekTo(currentPosition);
                }
                //监听播放时回调函数
                timer = new Timer();
                timer.schedule(new TimerTask() {

                    Runnable updateUI = new Runnable() {
                        @Override
                        public void run() {
                            musicCur.setText(format.format(mediaPlayer.getCurrentPosition())+"");
                        }
                    };
                    @Override
                    public void run() {
                        if(!isSeekBarChanging){
                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            runOnUiThread(updateUI);
                        }
                    }
                },0,50);
                break;
            case R.id.stopBtn:
                //如果在播放中，立刻暂停。
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    currentPosition=mediaPlayer.getCurrentPosition();
                }
                break;
            case R.id.listBtn:
                //如果在播放中，立刻停止。
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.reset();
                    initMediaPlayer();//初始化播放器 MediaPlayer
                }
                break;
            case R.id.aiBtn:
              //  download(view);
                upload(view);
                break;
            case R.id.quit:
                PlaySongActivity.this.finish();
            default:
                break;
        }
    }

    /**
     *获取权限
     *@author wei
     *created at 2021/3/18 20:49
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initMediaPlayer();
                }else{
                    Toast.makeText(this, "拒绝权限，将无法使用程序。", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
        }

    }
    /**
     *清理资源
     *@author wei
     *created at 2021/3/18 20:52
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isSeekBarChanging = true;
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (timer != null){
            timer.cancel();
            timer = null;
        }
    }

    /*进度条处理*/
    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        /*滚动时,应当暂停后台定时器*/
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = true;
        }
        /*滑动结束后，重新设置值*/
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = false;
            mediaPlayer.seekTo(seekBar.getProgress());
            currentPosition=mediaPlayer.getCurrentPosition();
            musicCur.setText(format.format(mediaPlayer.getCurrentPosition())+"");
        }
    }

    /**
     *连接服务器
     *@author wei
     *created at 2021/4/5 17:24
     */
    public void upload(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UploadUtil.getInstance().upload(base_URL,getIntent().getStringExtra("path"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void download(View view){
        File file = new File(getExternalFilesDir(null)+ "/songbeforecut");
        if(!file.exists())
            file.mkdirs();
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Log.d("DownloadListener", MyApplication.context.getExternalFilesDir(null) + "/songbeforecut/");
                DownloadUtil.get().download(down_URL+"test.mid", MyApplication.context.getExternalFilesDir(null) + "/songbeforecut/",dl);
            }
        }).start();

    }
}


