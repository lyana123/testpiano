package com.example.mypiano;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadUtil {
    private static DownloadUtil instance;
    private OkHttpClient okHttpClient;
    private final static byte[] gSyncCode = new byte[0];

    public static DownloadUtil get(){
        if(instance == null){
            synchronized (DownloadUtil.class){
                if(instance == null){
                    instance = new DownloadUtil();
                }
            }
        }
        return instance;
    }
    private DownloadUtil(){
        okHttpClient = new OkHttpClient();
    }

    public void download(final String url, final String saveDir, final DownloadListener downloadListener){
        Request request = new Request.Builder().url(url).addHeader("Accept-Encoding", "identity").build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                downloadListener.onDownloadFailed();
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String saveFolder = isExistDir(saveDir);
                String tempPath = saveFolder + File.separator + "temp_" + getNameFromUrl(url);
                String savePath = saveFolder + File.separator + getNameFromUrl(url);
                try {
                  //  deleteFile(tempPath);
                    is = Objects.requireNonNull(response.body()).byteStream();
                    long total = Objects.requireNonNull(response.body()).contentLength();
                    Log.d("DonwloadUtil", "onResponse: total:  "+total);
                    File file = new File(tempPath);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        downloadListener.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    fileRename(tempPath, savePath);
                    downloadListener.onDownloadSuccess(savePath);
                } catch (Exception e) {
                    downloadListener.onDownloadFailed();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    @NonNull
    public static String getNameFromUrl(String url) {
        //return url.substring(url.lastIndexOf("/") + 1);
        String fileName = url.split("/")[url.split("/").length - 1];
        String realname=fileName.split("=")[1];
        return  realname;
    }
    /**
     * delete file or directory
     * <ul>
     * <li>if path is null or empty, return true</li>
     * <li>if path not exist, return true</li>
     * <li>if path exist, delete recursion. return true</li>
     * <ul>
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {

        synchronized (gSyncCode) {
            if (TextUtils.isEmpty(path)) {
                return true;
            }

            File file = new File(path);
            if (!file.exists()) {
                return true;
            }
            if (file.isFile()) {
                return file.delete();
            }
            if (!file.isDirectory()) {
                return false;
            }
            File[] filesList = file.listFiles();

            if (filesList != null) {
                for (File f : filesList) {
                    if (f.isFile()) {
                        f.delete();
                    } else if (f.isDirectory()) {
                        deleteFile(f.getAbsolutePath());
                    }
                }
            }

            return file.delete();
        }

    }

    /**
     * @Method: fileRename
     * @Description: 将文件从fromName命名为toName，由于使用的是File自带的renameTo()接口，需要注意： <li>读写存储器权限</li> <li>
     * fromName和toName这两个路径在相同的挂载点。如果不在同一挂载点，重命名失败。</li>
     * @param fromName 需要重命名的文件，为文件绝对路径
     * @param toName 要改成的名字，为文件绝对路径
     * @return boolean 成功或失败
     */
    public static boolean fileRename(String fromName, String toName) {
        synchronized (gSyncCode) {
            // TODO: 根据文件名判断是否属于同一挂载点
            File fromFile = new File(fromName);
            File toFile = new File(toName);
            if (!fromFile.exists()) {
                return false;
            }
            boolean result = fromFile.renameTo(toFile);
            if (result) {
            }
            return result;
        }

    }

}
