package com.example.mypiano.piano;
/**
 * 音乐播放帮助类
 */

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.example.mypiano.R;

public class MyMusicUtils {
    // 资源文件
//	int Music[] = { R.raw.do1, R.raw.re2, R.raw.mi3, R.raw.fa4, R.raw.sol5,
//			R.raw.la6, R.raw.si7, };
    int Music[] = {R.raw.c52,R.raw.d54,R.raw.e56,R.raw.f57,R.raw.g59,R.raw.a61,R.raw.b63,
            R.raw.c40,R.raw.d42,R.raw.e44,R.raw.f45,R.raw.g47,R.raw.a49,R.raw.b51,
            R.raw.c28,R.raw.d30,R.raw.e32,R.raw.f33,R.raw.g35,R.raw.a37,R.raw.b39,};
    SoundPool soundPool;
    HashMap<Integer, Integer> soundPoolMap;
    String MusicSeries[] = {"C5","D5","E5","F5","G5","A6","B6",
            "C4","D4","E4","F4","G4","A4","B5",
            "C2","D3","E3","F3","G3","A3","B3",};

    /**
     *
     * @param context
     *            用于soundpool.load
     */
    public MyMusicUtils(Context context) {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < Music.length; i++) {
            soundPoolMap.put(i, soundPool.load(context, Music[i], 1));
        }
    }

    /**
     *
     * @param no
     *              播放声音的编号
     */
    public int soundPlay(int no) {
        return soundPool.play(soundPoolMap.get(no), 100, 100, 1, 0, 1.0f);
    }

    public int soundOver() {
        return soundPool.play(soundPoolMap.get(1), 100, 100, 1, 0, 1.0f);
    }

    public String getmusicSeries(int no) {
        return MusicSeries[no];
    }

    @Override
    protected void finalize() throws Throwable {
        // TODO Auto-generated method stub
        soundPool.release();
        super.finalize();
    }
}
