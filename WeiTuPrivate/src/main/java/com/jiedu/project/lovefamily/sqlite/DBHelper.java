package com.jiedu.project.lovefamily.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by naruto on 2016/5/24.
 */
public class DBHelper extends SQLiteOpenHelper{

    private static String DB_NAME="LOVE_FAMILY";
    private static final int VERSION=1;

    private static final String SQL_CREATE="create table user_info(id integer primary key autoincrement," +
            "username varchar(30),code varchar(10),islogin integer)";

    private static final String SQL_CREATE2="create table friends_info(id integer primary key autoincrement," +
            "nickName varchar(10),statusName varchar(10),img_url text)";

    //private static final String DROP_TABLE="drop table if exists user_info";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(SQL_CREATE);
        sqLiteDatabase.execSQL(SQL_CREATE2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        //sqLiteDatabase.execSQL(DROP_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE);

    }


}
