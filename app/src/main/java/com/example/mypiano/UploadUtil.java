package com.example.mypiano;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class UploadUtil {
    private OkHttpClient okHttpClient;
    private UploadUtil(){
        okHttpClient = new OkHttpClient();
    }
    /**
     * 使用静态内部类的方式实现单例模式
     */
    private static class UploadUtilInstance{
        private static final UploadUtil INSTANCE = new UploadUtil();
    }
    public static UploadUtil getInstance(){
        return UploadUtilInstance.INSTANCE;
    }

    /**
     * @param url   服务器地址
     * @param songPath  所要上传的文件
     * @return      响应结果
     * @throws IOException
     */
    public ResponseBody upload(String url, String songPath) throws IOException {
        File file=new File(songPath);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.Companion.create(file,MediaType.parse("multipart/form-data")))
                .build();
        Request request = new Request.Builder()
                .header("Authorization", "ClientID" + UUID.randomUUID())
                .url(url+"/uploadsong")
                .post(requestBody)
                .build();

        Log.d("Util", "请求地址 " + url + "/uploadsong");
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);
        return response.body();
    }
}

