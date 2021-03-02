package com.example.mypiano;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

/*
首页的卡片布局,卡片详情
 */
public class postAdapter extends RecyclerView.Adapter<postAdapter.ViewHolder>{

    public Context mContext;

    public List<post> mSongList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView songImage;
        TextView songTitle;
        TextView songContext;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            songImage = (ImageView) view.findViewById(R.id.song_image);
            songContext = (TextView) view.findViewById(R.id.song_context);
            songTitle=(TextView) view.findViewById(R.id.song_title);
        }
    }

    public postAdapter(List<post> songList) {
        mSongList = songList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

//        //后面要对的内容
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = holder.getAdapterPosition();
//                post song = mSongList.get(position);
//
//                Intent intent = new Intent(mContext, SongActivity.class);
//                intent.putExtra(SongActivity.SONG_TITLE, song.getTitle());//向卡片详情界面传输歌曲的名字，图片名称
//                intent.putExtra(SongActivity.SONG_ID, song.getID());
//                mContext.startActivity(intent);
//            }
//        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        post song = mSongList.get(position);
        holder.songTitle.setText(song.getTitle());
        holder.songContext.setText(song.getSongContext());
        //加载图片
        int index=song.getImageId().charAt(3) - (int)('0');
      //  Glide.with(mContext).load(R.drawable.pic+index).into(holder.songImage);
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

}
