package com.example.mypiano;

import androidx.appcompat.app.ActionBar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.Resource;
import com.example.mypiano.fragement.MyinfoFragment;
import com.example.mypiano.fragement.addFragment;
import com.example.mypiano.fragement.msgFragment;
import com.example.mypiano.fragement.resourceFragment;
import com.example.mypiano.fragement.shouye;
import com.example.mypiano.piano.piano;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private TextView tv_main_title;
    private TextView tv_back;
    private RelativeLayout title_bar;

    private RelativeLayout main_body;
    private TextView bottom_bar_0_text_0;
    private ImageView bottom_bar_image_0;
    private TextView bottom_bar_1_text_1;
    private ImageView bottom_bar_image_1;
    private TextView bottom_bar_2_text_2;
    private ImageView bottom_bar_image_2;
    private TextView bottom_bar_3_text_3;
    private ImageView bottom_bar_image_3;
    private TextView bottom_bar_4_text_4;
    private ImageView bottom_bar_image_4;
    private LinearLayout main_bottom_bar;

    private RelativeLayout bottom_bar_0_btn;
    private RelativeLayout bottom_bar_1_btn;
    private RelativeLayout bottom_bar_2_btn;
    private RelativeLayout bottom_bar_3_btn;
    private RelativeLayout bottom_bar_4_btn;

    TextView bottom_bar_text[] = new TextView[5];
    ImageView bottom_bar_image[] = new ImageView[5];
    int picID[]=new int[5];
    ImageView unselected_bottom_bar_image[] = new ImageView[5];

    private MyDatabase mydb;
    private DrawerLayout mDrawerLayout;//滑动菜单
    private post[] posts = new post[20];//不知道在Android里怎么动态定义数组的长
    private List<post> songList = new ArrayList<>();
    private postAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;//下拉刷新

    //初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //创建数据库


        initBody();
        initView();
        setInitStatus();
    }


    //设置首页默认被选中
    private void setInitStatus() {
        clearBottomImageState(0);
        setSelectedStatus(0);
        getSupportFragmentManager().beginTransaction().add(R.id.main_body, new shouye()).commit();
    }


    //设置文字颜色
    private void clearBottomImageState(int selected) {
        for (int i = 0; i < 5; i++) {
            if (i == selected)
                continue;
            else {
                bottom_bar_text[i].setTextColor(Color.parseColor("#817fc8"));
                bottom_bar_image[i].setImageResource(picID[i]);

            }


        }


    }

    private void initBody() {
        main_body = (RelativeLayout) findViewById(R.id.main_body);
    }

    private void initView() {//获取界面上的元素
        //标题显示
        //tv_back=findViewById(R.id.tv_back);
        tv_main_title = findViewById(R.id.tv_main_title);
        //  title_bar=findViewById(R.id.title_bar);
        //底部导航栏
        bottom_bar_0_text_0 = (TextView) findViewById(R.id.bottom_bar_1_text_1);
        bottom_bar_image_0 = (ImageView) findViewById(R.id.bottom_bar_image_1);
        bottom_bar_1_text_1 = (TextView) findViewById(R.id.bottom_bar_2_text_2);
        bottom_bar_image_1 = (ImageView) findViewById(R.id.bottom_bar_image_2);
        bottom_bar_2_text_2 = (TextView) findViewById(R.id.bottom_bar_3_text_3);
        bottom_bar_image_2 = (ImageView) findViewById(R.id.bottom_bar_image_3);
        bottom_bar_3_text_3 = (TextView) findViewById(R.id.bottom_bar_4_text_4);
        bottom_bar_image_3 = (ImageView) findViewById(R.id.bottom_bar_image_4);
        bottom_bar_4_text_4 = (TextView) findViewById(R.id.bottom_bar_5_text_5);
        bottom_bar_image_4 = (ImageView) findViewById(R.id.bottom_bar_image_5);


        for (int i = 0; i < 5; i++)
            bottom_bar_text[i] = new TextView(this);
        bottom_bar_text[0] = bottom_bar_0_text_0;
        bottom_bar_text[1] = bottom_bar_1_text_1;
        bottom_bar_text[2] = bottom_bar_2_text_2;
        bottom_bar_text[3] = bottom_bar_3_text_3;
        bottom_bar_text[4] = bottom_bar_4_text_4;
        for (int i = 0; i < 5; i++)
            bottom_bar_image[i] = new ImageView(this);
        bottom_bar_image[0] = bottom_bar_image_0;
        bottom_bar_image[1] = bottom_bar_image_1;
        bottom_bar_image[2] = bottom_bar_image_2;
        bottom_bar_image[3] = bottom_bar_image_3;
        bottom_bar_image[4] = bottom_bar_image_4;

        //包含底部 android:id="@+id/main_bottom_bar"
        main_bottom_bar = findViewById(R.id.main_bottom_bar);

        bottom_bar_0_btn = findViewById(R.id.bottom_bar_1_btn);
        bottom_bar_1_btn = findViewById(R.id.bottom_bar_2_btn);
        bottom_bar_2_btn = findViewById(R.id.bottom_bar_3_btn);
        bottom_bar_3_btn = findViewById(R.id.bottom_bar_4_btn);
        bottom_bar_4_btn = findViewById(R.id.bottom_bar_5_btn);
        //添加点击事件
        bottom_bar_1_btn.setOnClickListener(this);
        bottom_bar_2_btn.setOnClickListener(this);
        bottom_bar_3_btn.setOnClickListener(this);
        bottom_bar_4_btn.setOnClickListener(this);
        bottom_bar_0_btn.setOnClickListener(this);

        //获取图片资源
        for (int i = 0; i < 5; i++)
            unselected_bottom_bar_image[i] = new ImageView(this);
        picID=getResIdArray(this,R.array.SelectedStrPic);
        Log.d("MainActivity", "initView: picID"+picID[0]+"  "+picID[1]);

        //tv_back.setVisibility(View.GONE);
        tv_main_title.setText("首页");
        // title_bar.setBackgroundColor(Color.parseColor("#30B4FF"));
    }


/**
*点击事件
*@author wei
*created at 2021/3/5 16:46
*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_bar_1_btn:
                clearBottomImageState(0);
                /**  replacing instead of adding **/
                getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new shouye()).commit();
                setSelectedStatus(0);
                break;
            case R.id.bottom_bar_2_btn:
                clearBottomImageState(1);
                setSelectedStatus(1);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new resourceFragment()).commit();
                break;
            case R.id.bottom_bar_3_btn:
                clearBottomImageState(2);
                setSelectedStatus(2);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new addFragment()).commit();
                break;
            case R.id.bottom_bar_4_btn:
                clearBottomImageState(3);
                setSelectedStatus(3);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new MyinfoFragment()).commit();
                //每次都重新创建fragment
                break;
            case R.id.bottom_bar_5_btn:
                clearBottomImageState(4);
                setSelectedStatus(4);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new msgFragment()).commit();
                break;
        }
    }
    
    /**
    *
    *@author wei
    *created at 2021/3/5 16:33
    */
    private void setSelectedStatus(int index) {
        switch (index) {
            case 0:
                //mCourseBtn.setSelected(true);
                Log.d("MainActivity", "setSelectedStatus: clickshouye");
                bottom_bar_image_0.setImageResource(R.drawable.shouye2);
                bottom_bar_0_text_0.setTextColor(Color.parseColor("#0097f7"));
                tv_main_title.setText("首页");
                break;
            case 1:
                //mExercisesBtn.setSelected(true);
                Log.d("MainActivity", "setSelectedStatus: clickziyuan");
                bottom_bar_image_1.setImageResource(R.drawable.ziyuan2);
                bottom_bar_1_text_1.setTextColor(Color.parseColor("#0097f7"));
                tv_main_title.setText("资源库");
                break;
            case 2:
                //mMyInfoBtn.setSelected(true);
                bottom_bar_image_2.setImageResource(R.drawable.tianjia2);
                Log.d("MainActivity", "setSelectedStatus: clicktinajia");
                bottom_bar_2_text_2.setTextColor(Color.parseColor("#0097f7"));
                // title_bar.setVisibility(View.VISIBLE);
                tv_main_title.setText("添加");
                Intent intent = new Intent(MainActivity.this, piano.class);
                startActivity(intent);
                break;
            case 3:
                //mMyInfoBtn.setSelected(true);
                bottom_bar_image_3.setImageResource(R.drawable.xinxi2);
                Log.d("MainActivity", "setSelectedStatus: clickxinxi");
                bottom_bar_3_text_3.setTextColor(Color.parseColor("#0097f7"));
                //   title_bar.setVisibility(View.VISIBLE);
                tv_main_title.setText("信息");

                break;
            case 4:
                //mMyInfoBtn.setSelected(true);
                bottom_bar_image_4.setImageResource(R.drawable.geren2);
                Log.d("MainActivity", "setSelectedStatus: clickgeren");
                bottom_bar_4_text_4.setTextColor(Color.parseColor("#0097f7"));

                tv_main_title.setText("个人");
                break;
        }

    }

    /**
    *获取图片数组
    *@author wei
    *created at 2021/3/5 16:35
    */
    private  int[]   getResIdArray(Context context, int arraysIndex) {


        TypedArray arrays = context.getResources().obtainTypedArray(
                arraysIndex);
        int len = arrays.length();
        int[] resIdArray = new int[len];
        for (int i = 0; i < len; i++) {
            resIdArray[i] = arrays.getResourceId(i, -1);
        }
        return resIdArray;
    }

}