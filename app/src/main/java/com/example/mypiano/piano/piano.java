package com.example.mypiano.piano;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.text.method.ScrollingMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypiano.R;


public class piano extends Activity  {

    private Button button[];// 按钮数组
    private Button setting;// 设置按钮
    private TextView textView;// 简谱
    private MyMusicUtils utils;// 工具类
    private View parent;// 父视图
    private int buttonId[];// 按钮id
    private boolean havePlayed[];// 是否已经播放了声音，当手指在同一个按钮内滑动，且已经发声，就为true
    private View keys;// 按钮们所在的视图
    private int pressedkey[];

    private Dialog dialog;
    private View dialogView;
    private Button cancel;
    private Button quit;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piano);
        Intent intent = getIntent();
        // 新建工具类
        utils = new MyMusicUtils(getApplicationContext());
        textView = (TextView) findViewById(R.id.text);
        textView.setClickable(true);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        // 注意如果想要滚动条时刻显示, 必须加上以下语句:
        textView.setScrollbarFadingEnabled(false);

        // 按钮资源Id
        buttonId = new int[21];
        buttonId[0] = R.id.button1;
        buttonId[1] = R.id.button2;
        buttonId[2] = R.id.button3;
        buttonId[3] = R.id.button4;
        buttonId[4] = R.id.button5;
        buttonId[5] = R.id.button6;
        buttonId[6] = R.id.button7;
        buttonId[7] = R.id.button11;
        buttonId[8] = R.id.button22;
        buttonId[9] = R.id.button33;
        buttonId[10] = R.id.button44;
        buttonId[11] = R.id.button55;
        buttonId[12] = R.id.button66;
        buttonId[13] = R.id.button77;
        buttonId[14] = R.id.button111;
        buttonId[15] = R.id.button222;
        buttonId[16] = R.id.button333;
        buttonId[17] = R.id.button444;
        buttonId[18] = R.id.button555;
        buttonId[19] = R.id.button666;
        buttonId[20] = R.id.button777;

        button = new Button[21];
        havePlayed = new boolean[21];

        // 获取按钮对象
        for (int i = 0; i < button.length; i++) {
            button[i] = (Button) findViewById(buttonId[i]);
            button[i].setClickable(false);
            havePlayed[i] = false;
        }

        pressedkey = new int[5];
        for (int j = 0; j < pressedkey.length; j++) {
            pressedkey[j] = -1;
        }

        parent = (View) findViewById(R.id.parent);
        parent.setClickable(true);

        // 不知道为什么有时候会卡键，可能是由于接触点大于5个
        parent.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int temp;
                int tempIndex;
                int pointercount;

                pointercount = event.getPointerCount();
                for (int count = 0; count < pointercount; count++) {
                    boolean moveflag = false;// 标记是否是在按键上移动
                    temp = isInAnyScale(event.getX(count), event.getY(count),
                            button);
                    if (temp != -1) {// 事件对应的是当前点
                        switch (event.getActionMasked()) {
                            case MotionEvent.ACTION_DOWN:
                                // // 单独一根手指或最先按下的那个
                                // pressedkey = temp;
                            case MotionEvent.ACTION_POINTER_DOWN:
                                // Log.i("--", "count" + count);
                                if (count != 0) {
                                    if (pressedkey[count - 1] == -1) {
                                        pressedkey[count - 1] = temp;
                                    } else {
                                        pressedkey[count] = temp;

                                    }
                                } else {
                                    pressedkey[count] = temp;

                                }
                                if (!havePlayed[temp]) {// 在某个按键范围内
                                    button[temp]
                                            .setBackgroundResource(R.drawable.button_pressed);
                                    // 播放音阶
                                    utils.soundPlay(temp);
                                    // Log.i("--", "sound" + temp);
                                    havePlayed[temp] = true;
                                }
                                break;
                            case MotionEvent.ACTION_MOVE:
                                // 事件与点对应
                                // if (temp!=pressedkey[count])
                                // {//移动的按钮和当前响应事件的按钮不匹配
                                // break;
                                // }
                                // Log.i("--", "currentkey" + temp + "move");
                                temp = pressedkey[count];
                                // Log.i("--", "KEY" + temp + "move");
                                for (int i = temp + 1; i >= temp - 1; i--) {
                                    // 当在两端的按钮时，会有一边越界
                                    if (i < 0 || i >= button.length) {
                                        continue;
                                    }
                                    if (isInScale(event.getX(count),
                                            event.getY(count), button[i], i)) {// 在某个按键内
                                        moveflag = true;
                                        if (i != temp) {// 在相邻按键内
                                            boolean laststill = false;
                                            boolean nextstill = false;
                                            // 假设手指已经从上一个位置抬起，但是没有真的抬起，所以不移位
                                            pressedkey[count] = -1;
                                            for (int j = 0; j < pointercount; j++) {
                                                if (pressedkey[j] == temp) {
                                                    laststill = true;
                                                }
                                                if (pressedkey[j] == i) {
                                                    nextstill = true;
                                                }
                                            }

                                            if (!nextstill) {// 移入的按键没有按下
                                                // 设置当前按键
                                                button[i]
                                                        .setBackgroundResource(R.drawable.button_pressed);
                                                // 发音
                                                utils.soundPlay(i);
                                                // Log.i("--", "soundmove" + temp
                                                // + "to" + i);
                                                havePlayed[i] = true;
                                            }

                                            pressedkey[count] = i;

                                            if (!laststill) {// 没有手指按在上面
                                                // 设置上一个按键
                                                button[temp]
                                                        .setBackgroundResource(R.drawable.button);
                                                havePlayed[temp] = false;
                                            }

                                            break;
                                        }
                                    }
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_POINTER_UP:
                                // 事件与点对应
                                tempIndex = event.getActionIndex();
                                if (tempIndex == count) {
                                    // Log.i("--", "index" + tempIndex);
                                    boolean still = false;
                                    // 当前点已抬起
                                    for (int t = count; t < 5; t++) {
                                        if (t != 4) {
                                            if (pressedkey[t + 1] >= 0) {
                                                pressedkey[t] = pressedkey[t + 1];
                                            } else {
                                                pressedkey[t] = -1;
                                            }
                                        } else {
                                            pressedkey[t] = -1;
                                        }

                                    }
                                    for (int i = 0; i < pressedkey.length; i++) {// 是否还有其他点
                                        if (pressedkey[i] == temp) {
                                            still = true;
                                            break;
                                        }
                                    }

                                    // String string = "";
                                    // for (int i = 0; i < pressedkey.length; i++) {
                                    // string += pressedkey[i];
                                    // }
                                    // Log.i("--", "pressedkey" + string);
                                    if (!still) {// 已经没有手指按在该键上
                                        button[temp]
                                                .setBackgroundResource(R.drawable.button);
                                        havePlayed[temp] = false;
                                        // Log.i("--", "button" + temp + "up");
                                    }
                                    break;
                                }
                        }
                    }
                    //
                    if (event.getActionMasked() == MotionEvent.ACTION_MOVE
                            && !moveflag) {
                        // String string = "";
                        // for (int i = 0; i < pressedkey.length; i++) {
                        // string += pressedkey[i];
                        // }
                        // Log.i("--", "pressedkey" + string + "count" + count);
                        if (pressedkey[count] != -1) {
                            button[pressedkey[count]]
                                    .setBackgroundResource(R.drawable.button);
                            havePlayed[pressedkey[count]] = false;
                            // Log.i("--", "button" + pressedkey[count] +
                            // "ddup");
                            for (int t = count; t < 5; t++) {
                                if (t != 4) {
                                    if (pressedkey[t + 1] >= 0) {
                                        pressedkey[t] = pressedkey[t + 1];
                                    } else {
                                        pressedkey[t] = -1;
                                    }
                                } else {
                                    pressedkey[t] = -1;
                                }
                            }
                        }
                    }
                }
                return false;
            }
        });

        keys = (View) findViewById(R.id.Keys);

        dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_setting,
                null);

        dialog = new Dialog(new ContextThemeWrapper(this,
                R.style.AlertDialogCustom));
        dialog.setCancelable(false);
        dialog.setContentView(dialogView);
        dialog.setTitle("设置");

        cancel = (Button) dialogView.findViewById(R.id.buttoncancel);
        quit = (Button) dialogView.findViewById(R.id.buttonquit);
        spinner = (Spinner) dialogView.findViewById(R.id.spinner1);

        List<String> list = new ArrayList<String>();
        list.add("祝你生日快乐");
        list.add("两只老虎");
        list.add("小星星");
        list.add("铃儿响叮当");
        list.add("世上只有妈妈好");
        list.add("我是一个粉刷匠");
        list.add("上学歌");
        list.add("无");

        spinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                R.layout.item, list));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                switch (position) {
                    case 0:
                        textView.setText(piano.this
                                .getString(R.string.happybirthday));
                        break;
                    case 1:
                        textView.setText(piano.this
                                .getString(R.string.twotigers));
                        break;
                    case 2:
                        textView.setText(piano.this
                                .getString(R.string.stars));
                        break;
                    case 3:
                        textView.setText(piano.this.getString(R.string.ding));
                        break;
                    case 4:
                        textView.setText(piano.this.getString(R.string.mom));
                        break;
                    case 5:
                        textView.setText(piano.this
                                .getString(R.string.brush));
                        break;
                    case 6:
                        textView.setText(piano.this
                                .getString(R.string.schooling));
                        break;
                    case 7:
                        textView.setText(piano.this
                                .getString(R.string.title));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
                textView.setText(piano.this.getString(R.string.title));
            }
        });
        spinner.setSelection(7);

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        quit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                piano.this.finish();
            }
        });

        // 设置界面
        setting = (Button) findViewById(R.id.setting);

        setting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.show();
            }
        });
    }

    /**
     * 判断某个点是否在某个按钮的范围内
     *
     * @param x
     *            横坐标
     * @param y
     *            纵坐标
     * @param button
     *            按钮对象
     * @return 在：true；不在：false
     */
    private boolean isInScale(float x, float y, Button button, int i) {
        // keys.getTop()是获取按钮所在父视图相对其父视图的右上角纵坐标

        if (x > button.getLeft()
                && x < button.getRight()
                && y > button.getTop() + keys.getTop() + i / 7
                * button.getHeight()
                && y < button.getBottom() + keys.getTop() + i / 7
                * button.getHeight()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断某个点是否在一个按钮集合中的某个按钮内
     *
     * @param x
     *            横坐标
     * @param y
     *            纵坐标
     * @param button
     *            按钮数组
     * @return
     */
    private int isInAnyScale(float x, float y, Button[] button) {
        // keys.getTop()是获取按钮所在父视图相对其父视图的右上角纵坐标

        for (int i = 0; i < button.length; i++) {
            // Log.i("--",
            // "data" + i + ":" + button[i].getLeft() + ","
            // + button[i].getRight() + ","
            // + (button[i].getTop() + keys.getTop()) + ","
            // + (button[i].getBottom() + keys.getTop()));
            if (x > button[i].getLeft()
                    && x < button[i].getRight()
                    && y > (button[i].getTop() + keys.getTop() + i / 7
                    * button[0].getHeight())
                    && y < (button[i].getBottom() + keys.getTop() + i / 7
                    * button[0].getHeight())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(getApplicationContext(), "请从设置中退出",
                    Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
