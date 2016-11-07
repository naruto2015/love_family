package com.jiedu.project.lovefamily.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jiedu.project.lovefamily.mode.User;

/**
 * Created by naruto on 2016/5/24.
 */
public class UserDaoImpl implements UserDao{


    private DBHelper dbHelper=null;
    public UserDaoImpl(Context context){
        dbHelper=new DBHelper(context);
    }

    @Override
    public void insertUserInfo(User user) {

        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.execSQL("insert into user_info(username,code,islogin) values(?,?,?)",new Object[]{user.getUserName(),user.code});

        db.close();
    }

    @Override
    public User selectUserInfo() {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        User user=new User();
        Cursor cursor=db.rawQuery("select * from user_info",null);
        while(cursor.moveToNext()){
            user.setUserName(cursor.getString(cursor.getColumnIndex("username")));
            user.code=cursor.getString(cursor.getColumnIndex("code"));
            user.login=cursor.getInt(cursor.getColumnIndex("islogin"));

        }

        db.close();
        return user;
    }

    @Override
    public void updateUserInfo(User user) {

        SQLiteDatabase db=dbHelper.getWritableDatabase();

        db.execSQL("update user_info set frequency=? isUploadLocation=?",new Object[]{user.frequency,user.isUploadLocation});

        db.close();
    }

    @Override
    public void deleteAllUserInfo() {


        User user=selectUserInfo();
        if(user.getUserName()!=null){
            SQLiteDatabase db=dbHelper.getWritableDatabase();
            db.execSQL("delete from user_info");
            db.close();
        }


    }

    @Override
    public int selectUserInfoFrequency() {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        //User user=new User();
        int frequency=60;
        Cursor cursor=db.rawQuery("select * from user_info",null);
        while(cursor.moveToNext()){
            frequency=Integer.valueOf(cursor.getString(cursor.getColumnIndex("frequency")));
            //user.isUploadLocation=cursor.getInt(cursor.getColumnIndex("isUploadLocation"));

            Log.e("TAG",frequency+"");
        }

        db.close();
        return frequency;
    }

}
