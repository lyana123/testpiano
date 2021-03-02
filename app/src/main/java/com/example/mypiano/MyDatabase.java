package com.example.mypiano;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


public class MyDatabase extends SQLiteOpenHelper {

    public static final String CREATE_TIEZI="create table Tiezi ("+"id integer primary key autoincrement, "
            +"title text, "+"content text, "+"img text, "+"song text)";

    private Context mContext;

    public MyDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TIEZI);
        dataBaseInitialized(sqLiteDatabase);
        Toast.makeText(mContext,"The database was initialized successfully",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//        sqLiteDatabase.execSQL("drop table if exists Tiezi");
//        onCreate(sqLiteDatabase);
    }
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int i, int i1){
//        sqLiteDatabase.execSQL("drop table if exists Tiezi");
//        onCreate(sqLiteDatabase);
    }

    public void dataBaseInitialized(SQLiteDatabase db){
        ContentValues values =new ContentValues();
        values.put("title","西红柿不爱吃椰子");
        values.put("content","主打歌《Absence》讲述的是即使是在一起的时间，也仍然像是在黑暗的海底一样，" +
                "相互埋怨不同发展的内心的时间，这样的真挚的歌词让人印象深刻。在感性的钢琴和原声吉他的旋律上，" +
                "合成音将危险的感情原封不动地呈现出来。");
        values.put("img","pic1");
        values.put("song","music1");
        db.insert("Tiezi",null,values);
        values.put("title","番茄到底爱不爱吃西红柿");
        values.put("content","自 ”Dancing With A Stranger“之后，灵魂金嗓Sam Smith发布" +
                "新单\"How Do You Sleep?\",联手白金音乐人Savan Kotecha（Ariana Grande，Katy Perry）, " +
                "ILYA(Taylor Swift,Ariana Grande)以及金曲制单机Max Martin共同谱写。与上一张专辑" +
                "《The Thrill Of It All 》风格迥然，告别以往忧伤抒情的Sam Smith， 两首新单以轻松的氛围，" +
                "融入电子元素，向乐迷呈现全新状态的自己。");
        values.put("img","pic2");
        values.put("song","music2");
        db.insert("Tiezi",null,values);
        values.put("title","椰子爱吃西红柿");
        values.put("content","今年五月，一股沉静而有力量的声音划破沉寂，「天黑黑」里22岁的孙燕姿以新人" +
                "之势在发片10天内拿下各大排行冠军，惊人的声势不仅让传媒见识到千禧乐坛又一新势力的掘起，三十" +
                "万张的销售更让天黑黑的唱片市场顿时拨云见日，缠斗一整年的新人激战，燕姿以横跨实力、偶像两大" +
                "范畴的个人特质，在舆论与市场罕见的共识下，获得一致的至高评价。\n" +
                "\n" +
                "半年来燕姿以首张专辑的精湛表现，不但占据各大排行TOP 10达数月之久，" +
                "更拿下亚洲各项年终音乐大奖：台湾民生报「十大偶像新人奖」、新加坡「933电台金曲新人奖」、" +
                "香港「TVB8最佳新人奖」…，排山倒海的实力认证，新世代的真天后已然揭晓。。");
        values.put("img","pic3");
        values.put("song","music3");
        db.insert("Tiezi",null,values);

    }
}
