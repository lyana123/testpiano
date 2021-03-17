package com.example.mypiano.piano;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import com.example.mypiano.R;

public class FileListAdapter extends BaseAdapter {
    Context context;
    List<File> list;
    MediaPlayer mediaPlayer;
    int mediaPlayerstate = 1; //1--从未开始播放音乐，2--正在播放音乐，3--暂停音乐还没播完
    int mdediaPlayerposition = -1;
    int mselect = -1;

    public FileListAdapter(Context context, List<File> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_file_list, null);
            convertView.setTag(viewHolder);
            viewHolder.name = (TextView) convertView.findViewById(R.id.adapter_file_list_name);
            viewHolder.size = (TextView) convertView.findViewById(R.id.adapter_file_list_create_size);
            viewHolder.playbutton = (Button) convertView.findViewById(R.id.playmedia);
            viewHolder.zuoqubutton = (Button) convertView.findViewById(R.id.zuoqu);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(list.get(position).getName());
        viewHolder.size.setText(FormetFileSize(list.get(position).length()));

        if(mselect == position){
            viewHolder.playbutton.setBackgroundResource(R.drawable.stop);
        }else{
            viewHolder.playbutton.setBackgroundResource(R.drawable.play);
        }

        File parentfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/test-piano-main1");
        if(!parentfile.exists())
            parentfile.mkdirs();
        final String destinationPath=parentfile + "/" + list.get(position).getName().replace(".pcm",".wav");

        viewHolder.playbutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if(mediaPlayerstate == 1 && mdediaPlayerposition == -1){//从未播放音乐，要去播放音乐
                    Log.e("AudioRecorder", "------------position:"+position+"------------");
                    mediaPlayer= new MediaPlayer();
                    if (FileUtils.makePCMFileToWAVFile(list.get(position).getPath(), destinationPath, false)) {
                        //操作成功 播放转换好的音频
                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(destinationPath);

                            // 通过异步的方式装载媒体资源
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    // 装载完毕回调
                                    mediaPlayer.start();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //操作失败
                        Log.e("AudioRecorder", "makePCMFileToWAVFile fail");
                        throw new IllegalStateException("makePCMFileToWAVFile fail");
                    }
                    mediaPlayerstate = 2;
                    mdediaPlayerposition = position;
                    viewHolder.playbutton.setBackgroundResource(R.drawable.stop);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                             //在播放完毕被回调
                            mediaPlayer.reset();
                            mediaPlayer.stop();
                            mediaPlayer.release();
//                            mediaPlayer=null;
                            Log.e("MediaPlayer", "------------mediaPlayer release------------");
                            mdediaPlayerposition = -1;
                            mediaPlayerstate = 1;
                            viewHolder.playbutton.setBackgroundResource(R.drawable.play);
                        }
                    });
                }else if(mediaPlayerstate == 2 && mdediaPlayerposition == position){//正在播放音乐 要去暂停音乐
                    mediaPlayer.pause();
                    mediaPlayerstate = 3;
                    viewHolder.playbutton.setBackgroundResource(R.drawable.play);
                    Log.e("MediaPlayer", "------------mediaPlayer pause------------");
                }else if(mediaPlayerstate == 3 && mdediaPlayerposition == position){//音乐暂停 要继续播放音乐
                    mediaPlayer.start();
                    mediaPlayerstate = 2;
                    viewHolder.playbutton.setBackgroundResource(R.drawable.stop);
                    Log.e("MediaPlayer", "------------mediaPlayer start again------------");
                }else if(mdediaPlayerposition != position){
                    changeBackGround(position);
                    mediaPlayer.reset();
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer= new MediaPlayer();
                    Log.e("AudioRecorder", "------------position:"+position+"------------");
                    if (FileUtils.makePCMFileToWAVFile(list.get(position).getPath(), destinationPath, false)) {
                        //操作成功 播放转换好的音频
                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(destinationPath);

                            // 通过异步的方式装载媒体资源
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    // 装载完毕回调
                                    mediaPlayer.start();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //操作失败
                        Log.e("AudioRecorder", "makePCMFileToWAVFile fail");
                        throw new IllegalStateException("makePCMFileToWAVFile fail");
                    }
                    mediaPlayerstate = 2;
                    mdediaPlayerposition = position;
                    viewHolder.playbutton.setBackgroundResource(R.drawable.stop);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            //在播放完毕被回调
                            mediaPlayer.reset();
                            mediaPlayer.stop();
                            mediaPlayer.release();
//                            mediaPlayer=null;
                            Log.e("MediaPlayer", "------------mediaPlayer release------------");
                            mdediaPlayerposition = -1;
                            mediaPlayerstate = 1;
                            viewHolder.playbutton.setBackgroundResource(R.drawable.play);
                        }
                    });
                }
            }
        });

        return convertView;
    }

    public String FormetFileSize(long fileS) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    static class ViewHolder {
        TextView name;
        TextView size;
        Button playbutton;
        Button zuoqubutton;
    }

    public void changeBackGround(int positionid){
        if(positionid != mselect){
            mselect = positionid;
            notifyDataSetChanged();
        }
    }

}
