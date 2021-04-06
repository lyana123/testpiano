package com.example.mypiano.fragement.SongList;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.mypiano.R;
import com.example.mypiano.musiccut.MusicCutPopupWindow;
import com.example.mypiano.musiccut.MusicUtil;
import com.example.mypiano.piano.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SongListAdapter extends BaseAdapter {

    Context context;
    List<File> list;
    MediaPlayer mediaPlayer;
    int mediaPlayerstate = 1; //1--从未开始播放音乐，2--正在播放音乐，3--暂停音乐还没播完
    int mdediaPlayerposition = -1; //mdediaPlayer播放哪个位置的歌
    int mselect = -1; //哪个位置是暂停图标

    MusicCutPopupWindow musicCutPopupWindow;
    int CutPosition = -1; //点击哪个位置的剪辑按钮

    private Dialog dialog;
    private View dialogView;
    private Button cancel;
    private Button rename;
    private EditText editText;

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

//        final File parentfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/songbeforecut");
        final File parentfile = new File(context.getExternalFilesDir(null)+ "/songbeforecut");
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

        viewHolder.renamebutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
//                final EditText et = new EditText(context);
//                new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT).setTitle("RENAME")
////                        .setIcon(android.R.drawable.sym_def_app_icon)
//                        .setView(et)
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                //按下确定键后的事件
//                                Toast.makeText(context.getApplicationContext(), et.getText().toString(),Toast.LENGTH_LONG).show();
//                            }
//                        }).setNegativeButton("取消",null).show();
                dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rename,
                        null);
                dialog = new Dialog(new ContextThemeWrapper(context,R.style.AlertDialogCustom));
                dialog.setCancelable(false);
                dialog.setContentView(dialogView);
                dialog.setTitle("  RENAME");

                cancel = (Button) dialogView.findViewById(R.id.btn_cancel);
                rename = (Button) dialogView.findViewById(R.id.btn_rename);
                editText = (EditText) dialogView.findViewById(R.id.et_passwd);
                editText.setText(list.get(position).getName());

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                rename.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        File from = new File(parentfile,list.get(position).getName());
                        File to = new File(parentfile,editText.getText().toString());
                        from.renameTo(to);
                        list= FileUtils.getbeforeMidFiles();
                        notifyDataSetChanged();
                    }
                });

                dialog.show();
            }
        });

        viewHolder.cutbutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                CutPosition = position;
                musicCutPopupWindow = new MusicCutPopupWindow(context, itemsOnClick);
                // 设置音乐信息
                if(mediaPlayer != null){
                    mediaPlayer.reset();
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(destinationPath);
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            musicCutPopupWindow.setMusicInfo(list.get(position).getName(), mediaPlayer.getDuration() );
                            // 显示窗口
                            musicCutPopupWindow.showAtLocation(((SongListActivity) context)
                                    .findViewById(R.id.SongListActivity_root), Gravity.BOTTOM, 0, 0);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }

    //为弹出窗口实现监听类
    private OnClickListener  itemsOnClick = new OnClickListener(){

        public void onClick(View v) {
            switch (v.getId()){
                //确定按钮
                case R.id.popupWindow_music_cut_btn_sure:
                    //获取最小值
                    float min=musicCutPopupWindow.getMinRule();
                    //获取最大值
                    float max=musicCutPopupWindow.getMaxRule();
                    //开始剪切音乐
                    //要剪切的音乐路径
                    String inputPath=list.get(CutPosition).getPath();
                    //剪切后音乐文件路径
                    File flie = new File(context.getExternalFilesDir(null)+ "/songaftercut");
                    if(!flie.exists()){
                        flie.mkdirs();
                    }
                    String outputPath=flie.getPath()+"/new"+"_"+list.get(CutPosition).getName();
                    File audioPath=new File(outputPath);
                    //文件存在就先删除
                    if(audioPath.exists()){
                        try {
                            audioPath.delete();
                            audioPath.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    boolean flag= MusicUtil.cutWav(inputPath, outputPath, (int)min*1000, (int)max*1000);
                    if(flag){
                        Toast.makeText(context, "剪辑成功,保存路径："+outputPath, Toast.LENGTH_SHORT).show();
                        musicCutPopupWindow.dismiss();
                    }else{
                        Toast.makeText(context, "剪辑失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                //取消按钮
                case R.id.popupWindow_music_cut_btn_cancel:
                    //关闭弹窗
                    musicCutPopupWindow.dismiss();
                    //重置音乐
                    mediaPlayer.reset();
                    break;
                //试听按钮
                case R.id.popupWindow_music_cut_tv_musicTest:
                    cutMusicStartProgress=(long)((int)musicCutPopupWindow.getMinRule()*1000);
                    cutMusicEndProgress=(long)((int)musicCutPopupWindow.getMaxRule()*1000);
                    // 设置音乐信息
                    if(mediaPlayer != null){
                        mediaPlayer.reset();
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(list.get(CutPosition).getPath());
                        mediaPlayer.prepareAsync();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                if(cutMusicStartProgress!=0){
                                    mediaPlayer.seekTo((int) cutMusicStartProgress);
                                }
                                mediaPlayer.start();
                                handler.post(run);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

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

    // 剪辑歌曲开始进度
    long cutMusicStartProgress;
    // 剪辑歌曲结束进度
    long cutMusicEndProgress = 0;
    Handler handler = new Handler();
    /**
     * 当歌曲播放到结束位置时，使用Handler来停止播放
     */
    public Runnable run = new Runnable() {
        public void run() {
            if(cutMusicEndProgress!=0){
                if(mediaPlayer.isPlaying()&&mediaPlayer.getCurrentPosition()>=cutMusicEndProgress){
                    mediaPlayer.reset();
                    handler.removeCallbacks(run);
                }
                handler.postDelayed(run, 1000);
            }

        }
    };

    /**
     * 转换文件大小
     * @param fileS
     * @return
     */
    public String FormetFileSize(long fileS) {
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

    /**
     * 转换歌曲时间的格式
     * @param time
     * @return
     */
    public static String formatTime(long time) {
        if (time / 1000 % 60 < 10) {
            String t = time / 1000 / 60 + ":0" + time / 1000 % 60;
            return t;
        } else {
            String t = time / 1000 / 60 + ":" + time / 1000 % 60;
            return t;
        }
    }
}
