package com.example.mypiano;

public interface DownloadListener {
    /**
     * 下载成功
     */
    void onDownloadSuccess(String path);

    /**
     * @param progress
     * 下载进度
     */
    void onDownloading(int progress);

    /**
     * 下载失败
     */
    void onDownloadFailed();



}
