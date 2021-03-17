package com.example.mypiano;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.example.mypiano.user.LoginActivity;

public class Open extends Activity {

    private static int SPLASH_DISPLAY_LENGHT= 3000;    //延迟2秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);//去掉标题
        setContentView(R.layout.activity_open);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(Open.this, LoginActivity.class);    //第二个参数即为执行完跳转后的Activity
                startActivity(intent);
                Open.this.finish();   //关闭splashActivity，将其回收，否则按返回键会返回此界面
            }
        }, SPLASH_DISPLAY_LENGHT);

    }
}
