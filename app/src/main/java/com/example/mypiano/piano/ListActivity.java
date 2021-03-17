package com.example.mypiano.piano;

import android.app.Activity;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.mypiano.R;

public class ListActivity extends Activity {
    ListView listView;
    List<File> list = new ArrayList<>();
    FileListAdapter adapter;
//    MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = (ListView) findViewById(R.id.listView);
        if("pcm".equals(getIntent().getStringExtra("type"))){
            list=FileUtils.getPcmFiles();
        }

        adapter = new FileListAdapter(this, list);
        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            //点击播放pcm音频
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//
//                File parentfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/test-piano-main1");
//                if(!parentfile.exists())
//                    parentfile.mkdirs();
//                String destinationPath=parentfile + "/" + list.get(position).getName().replace(".pcm",".wav");;
//                if (FileUtils.makePCMFileToWAVFile(list.get(position).getPath(), destinationPath, false)) {
//                    //操作成功 播放转换好的音频
//                    try {
//                        Toast.makeText(getApplicationContext(),destinationPath,Toast.LENGTH_SHORT).show();
//                        mediaPlayer.reset();
////                        mediaPlayer.setDataSource(list.get(position).getPath());
//                        mediaPlayer.setDataSource(destinationPath);
//
//                        // 通过异步的方式装载媒体资源
//                        mediaPlayer.prepareAsync();
//                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                            @Override
//                            public void onPrepared(MediaPlayer mp) {
//                                // 装载完毕回调
//                                mediaPlayer.start();
//                            }
//                        });
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    //操作失败
//                    Log.e("AudioRecorder", "makePCMFileToWAVFile fail");
//                    throw new IllegalStateException("makePCMFileToWAVFile fail");
//                }
//
//            }
//        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
//        if(mediaPlayer != null){
//            mediaPlayer.stop();
//            mediaPlayer.release();
//        }
    }
}
