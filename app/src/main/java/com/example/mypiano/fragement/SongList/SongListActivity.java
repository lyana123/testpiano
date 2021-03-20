package com.example.mypiano.fragement.SongList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.mypiano.R;
import com.example.mypiano.piano.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SongListActivity extends Activity {
    ListView listView;
    List<File> list = new ArrayList<>();
    BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = (ListView) findViewById(R.id.listView);
        if("before".equals(getIntent().getStringExtra("type"))){
            list= FileUtils.getbeforeMidFiles();
            adapter = new SongListAdapter(this, list);

        }else{
            list= FileUtils.getaftereMidFiles();
            adapter = new AfterCutSongListAdapter(this, list);
        }
        listView.setAdapter(adapter);
    }
}
