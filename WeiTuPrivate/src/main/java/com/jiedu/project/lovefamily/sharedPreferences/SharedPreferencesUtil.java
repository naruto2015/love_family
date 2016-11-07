package com.jiedu.project.lovefamily.sharedPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.jiedu.project.lovefamily.activity.HomeActivity;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/3/22.
 */
public class SharedPreferencesUtil {
    private static SharedPreferences sharedPreferences;
    private  static SharedPreferences.Editor editor;
    private static String SHARED_NAME="weitutong";

public static void saveUserLoginInfo(Context context,String customerId,String phone,
                                     String nickName,String sex,String photoUrl,String brithday,
                                     String address,String inviteCode,String verification,String needUploadPostion,
                                     int frequency,String useStatus,String orderMsg){
    sharedPreferences=context.getSharedPreferences(SHARED_NAME, Activity.MODE_PRIVATE);
    editor=sharedPreferences.edit();
    editor.putString("customerId",customerId);
    editor.putString("phone",phone);
    editor.putString("nickName",nickName);
    editor.putString("sex",sex);
    editor.putString("photoUrl",photoUrl);
    editor.putString("inviteCode",inviteCode);
    editor.putString("birthday",brithday);
    editor.putString("address",address);
    editor.putString("verification",verification);
    editor.putString("needUploadPostion",needUploadPostion);
    editor.putInt("frequency",frequency);
    editor.putString("useStatus",useStatus);
    editor.putString("orderMsg",orderMsg);

    Log.e("0011","保存验证码"+verification);
    editor.commit();
}


    public static void saveUserLoginInfo2(Context context,String customerId,String phone,
                                         String nickName,String sex,String photoUrl,String brithday,
                                         String address,String inviteCode,String verification,String needUploadPostion,
                                         int frequency,String useStatus,String orderMsg,String isUploadLocation,String ctccPhone,String isExpired){
        sharedPreferences=context.getSharedPreferences(SHARED_NAME, Activity.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        editor.putString("customerId",customerId);
        editor.putString("phone",phone);
        editor.putString("nickName",nickName);
        editor.putString("sex",sex);
        editor.putString("photoUrl",photoUrl);
        editor.putString("inviteCode",inviteCode);
        editor.putString("birthday",brithday);
        editor.putString("address",address);
        editor.putString("verification",verification);
        editor.putString("needUploadPostion",needUploadPostion);
        editor.putString("frequency",frequency+"");
        editor.putString("useStatus",useStatus);
        editor.putString("orderMsg",orderMsg);
        editor.putString("useStatus",useStatus);
        editor.putString("orderMsg",orderMsg);
        editor.putString("isUploadLocation",isUploadLocation);
        editor.putString("ctccPhone",ctccPhone);
        editor.putString("isExpired",isExpired);

        Log.e("0011","保存验证码"+verification);
        boolean i=editor.commit();

        Log.e("TAG","是否保存位置成功"+i+"-----"+SharedPreferencesUtil.getInfo(context,"isUploadLocation"));
        Log.e("0012","上传位置时间间隔"+(Integer.valueOf(SharedPreferencesUtil.getInfo(context,"frequency"))*1000));

    }

    /**
     * 获取登录信息
     * @param context
     * @return
     */
public static HashMap<String,String> getUserLoginInfo(Context context){
    sharedPreferences=context.getSharedPreferences(SHARED_NAME, Activity.MODE_PRIVATE);
HashMap<String,String>hashMap=new HashMap<>();
    hashMap.put("customerId", sharedPreferences.getString("customerId", ""));
    hashMap.put("phone", sharedPreferences.getString("phone", ""));
    hashMap.put("inviteCode", sharedPreferences.getString("inviteCode", ""));
    hashMap.put("verification", sharedPreferences.getString("verification", ""));
    return hashMap;
    };

    public  static  String getInfo(Context context,String key){
        try{
            sharedPreferences=context.getSharedPreferences(SHARED_NAME, Activity.MODE_PRIVATE);
        }catch (Exception e){
            return "";
        }

        return sharedPreferences.getString(key,"")+"";
    }
    public  static  int getIntInfo(Context context,String key){
        sharedPreferences=context.getSharedPreferences(SHARED_NAME, Activity.MODE_PRIVATE);
        return sharedPreferences.getInt(key,0);
    }
    public static void saveInfo(Context context,String key,String value){
        sharedPreferences=context.getSharedPreferences(SHARED_NAME, Activity.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }
}
