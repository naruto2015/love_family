package com.jiedu.project.lovefamily.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jiedu.project.lovefamily.mode.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by naruto on 2016/5/31.
 */
public class FriendDaoImpl implements FriendDao {

    public DBHelper dbHelper=null;
    public FriendDaoImpl(Context context){
        dbHelper=new DBHelper(context);
    }

    @Override
    public void addAll(List<User> list) {

        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();


        for(int i=0;i<list.size();i++){
            sqLiteDatabase.execSQL("insert into friends_info(nickName,statusName,img_url) values(?,?,?)",new Object[]{list.get(i).getUserName(),list.get(i).getUserStatue(),list.get(i).getPhotoUrl()});
        }

        sqLiteDatabase.close();


    }

    @Override
    public boolean deleteAll() {

        ArrayList<User> list=getAll();
        if(list==null && list.size()<1){
            return false;
        }else {
            SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
            sqLiteDatabase.execSQL("delete from friends_info");
            sqLiteDatabase.close();
        }

        return false;
    }

    @Override
    public ArrayList<User> getAll() {
        SQLiteDatabase sqLiteDatabase=dbHelper.getWritableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery("select * from friends_info",null);
        User user=null;
        ArrayList<User> list=new ArrayList<User>();

        while (cursor.moveToNext()){
            user=new User();
            user.setUserName(cursor.getString(cursor.getColumnIndex("nickName")));
            user.setUserStatue(cursor.getString(cursor.getColumnIndex("statusName")));
            user.setPhotoUrl(cursor.getString(cursor.getColumnIndex("img_url")));
            Log.e("user.getPhotoUrl","user.getPhotoUrl"+cursor.getString(cursor.getColumnIndex("img_url")));
            list.add(user);
        }
        sqLiteDatabase.close();

        return list;
    }


}
