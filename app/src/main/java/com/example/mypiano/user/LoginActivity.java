package com.example.mypiano.user;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.mypiano.MainActivity;
import com.example.mypiano.Open;
import com.example.mypiano.R;

public class LoginActivity extends Activity implements View.OnClickListener  {
    private static final String TAG = "LoginActivity" ;
    private Button login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        login_btn=findViewById(R.id.login);
        login_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: 11111111111111111111111111");
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);    //第二个参数即为执行完跳转后的Activity
        startActivity(intent);
        LoginActivity.this.finish();   //关闭splashActivity，将其回收，否则按返回键会返回此界面
    }
}
