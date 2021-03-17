package com.example.mypiano.fragement.SongList;

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

import com.example.mypiano.R;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class SongListAdapter extends BaseAdapter {

    Context context;
    List<File> list;
    MediaPlayer mediaPlayer;
    int mediaPlayerstate = 1; //1--从未开始播放音乐，2--正在播放音乐，3--暂停音乐还没播完
    int mdediaPlayerposition = -1;
    int mselect = -1;

    public SongListAdapter(Context context, List<File> list) {
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
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_song_list_before_cut, null);
            convertView.setTag(viewHolder);
            viewHolder.name = (TextView) convertView.findViewById(R.id.adapter_file_list_name);
            viewHolder.size = (TextView) convertView.findViewById(R.id.adapter_file_list_create_size);
            viewHolder.playbutton = (Button) convertView.findViewById(R.id.playmedia);
            viewHolder.cutbutton = (Button) convertView.findViewById(R.id.cut);
            viewHolder.renamebutton = (Button) convertView.findViewById(R.id.rename);
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

        File parentfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/songbeforecut");
        if(!parentfile.exists())
            parentfile.mkdirs();
        final String destinationPath=parentfile + "/" + list.get(position).getName();

        viewHolder.playbutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if(mediaPlayerstate == 1 && mdediaPlayerposition == -1){//从未播放音乐，要去播放音乐
                    mediaPlayer= new MediaPlayer();
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
                            mdediaPlayerposition = -1;
                            mediaPlayerstate = 1;
                            viewHolder.playbutton.setBackgroundResource(R.drawable.play);
                        }
                    });
                }else if(mediaPlayerstate == 2 && mdediaPlayerposition == position){//正在播放音乐 要去暂停音乐
                    mediaPlayer.pause();
                    mediaPlayerstate = 3;
                    viewHolder.playbutton.setBackgroundResource(R.drawable.play);
                }else if(mediaPlayerstate == 3 && mdediaPlayerposition == position){//音乐暂停 要继续播放音乐
                    mediaPlayer.start();
                    mediaPlayerstate = 2;
                    viewHolder.playbutton.setBackgroundResource(R.drawable.stop);
                }else if(mdediaPlayerposition != position){
                    changeBackGround(position);
                    mediaPlayer.reset();
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer= new MediaPlayer();
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

    static class ViewHolder {
        TextView name;
        TextView size;
        Button playbutton;
        Button cutbutton;
        Button renamebutton;
    }

    public void changeBackGround(int positionid){
        if(positionid != mselect){
            mselect = positionid;
            notifyDataSetChanged();
        }
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
}
