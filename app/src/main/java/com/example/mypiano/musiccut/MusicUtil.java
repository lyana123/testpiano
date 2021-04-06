package com.example.mypiano.musiccut;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.annotation.SuppressLint;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * 剪切mp3音乐的工具类
 */
public class MusicUtil {
    //适当的调整SAMPLE_SIZE可以更加精确的裁剪音乐
    private static final int SAMPLE_SIZE = 1024*200;

    @SuppressLint("InlinedApi")
    public static boolean clipMp3(String inputPath, String outputPath, int start, int end){
        MediaExtractor extractor = null;
        BufferedOutputStream outputStream = null;
        try {
            extractor = new MediaExtractor();
            extractor.setDataSource(inputPath);
            int track = getAudioTrack(extractor);
            if(track < 0){
                return false;
            }
            //选择音频轨道
            extractor.selectTrack(track);
            outputStream = new BufferedOutputStream(
                    new FileOutputStream(outputPath), SAMPLE_SIZE);
            start = start * 1000;
            end = end * 1000;
            //跳至开始裁剪位置
            extractor.seekTo(start, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
            while (true){
                ByteBuffer buffer = ByteBuffer.allocate(SAMPLE_SIZE);
                int sampleSize = extractor.readSampleData(buffer, 0);
                long timeStamp = extractor.getSampleTime();
                // >= 1000000是要裁剪停止和指定的裁剪结尾不小于1秒，否则可能产生需要9秒音频
                //裁剪到只有8.6秒，大多数音乐播放器是向下取整，这样对于播放器变成了8秒，
                // 所以要裁剪比9秒多一秒的边界
                if(timeStamp > end && timeStamp - end >= 1000000){
                    break;
                }
                if(sampleSize <= 0){
                    break;
                }
                byte[] buf = new byte[sampleSize];
                buffer.get(buf, 0, sampleSize);
                //写入文件
                outputStream.write(buf);
                //音轨数据往前读
                extractor.advance();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(extractor != null){
                extractor.release();
            }
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 剪切wav音乐的工具类
     */
    public static boolean cutWav(String sourcefile, String targetfile, int start, int end) {
        try {
            if (!sourcefile.toLowerCase().endsWith(".wav") || !targetfile.toLowerCase().endsWith(".wav")) {
                return false;
            }
            File wav = new File(sourcefile);
            if (!wav.exists()) {
                return false;
            }
            long t1 = getWavLength(wav);  //总时长(秒)
            if (start < 0 || end <= 0 || start >= t1 || end > t1 || start >= end) {
                return false;
            }
            FileInputStream fis = new FileInputStream(wav);
            long wavSize = wav.length() - 44;  //音频数据大小（44为128kbps比特率wav文件头长度）
            long splitSize = (wavSize / t1) * (end - start);  //截取的音频数据大小
            long skipSize = (wavSize / t1) * start;  //截取时跳过的音频数据大小
            int splitSizeInt = Integer.parseInt(String.valueOf(splitSize));
            int skipSizeInt = Integer.parseInt(String.valueOf(skipSize));

            ByteBuffer buf1 = ByteBuffer.allocate(4);  //存放文件大小,4代表一个int占用字节数
            buf1.putInt(splitSizeInt + 36);  //放入文件长度信息
            byte[] flen = buf1.array();  //代表文件长度
            ByteBuffer buf2 = ByteBuffer.allocate(4);  //存放音频数据大小，4代表一个int占用字节数
            buf2.putInt(splitSizeInt);  //放入数据长度信息
            byte[] dlen = buf2.array();  //代表数据长度
            flen = reverse(flen);  //数组反转
            dlen = reverse(dlen);
            byte[] head = new byte[44];  //定义wav头部信息数组
            fis.read(head, 0, head.length);  //读取源wav文件头部信息
            for (int i = 0; i < 4; i++) {  //4代表一个int占用字节数
                head[i + 4] = flen[i];  //替换原头部信息里的文件长度
                head[i + 40] = dlen[i];  //替换原头部信息里的数据长度
            }
            byte[] fbyte = new byte[splitSizeInt + head.length];  //存放截取的音频数据
            for (int i = 0; i < head.length; i++) {  //放入修改后的头部信息
                fbyte[i] = head[i];
            }
            byte[] skipBytes = new byte[skipSizeInt];  //存放截取时跳过的音频数据
            fis.read(skipBytes, 0, skipBytes.length);  //跳过不需要截取的数据
            fis.read(fbyte, head.length, fbyte.length - head.length);  //读取要截取的数据到目标数组
            fis.close();
            File target = new File(targetfile);
            if (target.exists()) {  //如果目标文件已存在，则删除目标文件
                target.delete();
            }
            FileOutputStream fos = new FileOutputStream(target);
            fos.write(fbyte);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取音频数据轨道
     * @param extractor
     * @return
     */
    @SuppressLint("InlinedApi")
    private static int getAudioTrack(MediaExtractor extractor) {
        for(int i = 0; i < extractor.getTrackCount(); i++){
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if(mime.startsWith("audio")){
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据MediaPlayer获取wav音频时长 ms
     *
     * @return
     */
    public static long getWavLength(File file) {
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(file.getPath());  //recordingFilePath（）为音频文件的路径
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long duration = player.getDuration();//获取音频的时间
        Log.d("ACETEST", "### duration: " + duration);
        player.release();//记得释放资源
        return duration;
    }
    /**
     * 数组反转
     *
     * @param array
     */
    public static byte[] reverse(byte[] array) {
        byte temp;
        int len = array.length;
        for (int i = 0; i < len / 2; i++) {
            temp = array[i];
            array[i] = array[len - 1 - i];
            array[len - 1 - i] = temp;
        }
        return array;
    }
}
