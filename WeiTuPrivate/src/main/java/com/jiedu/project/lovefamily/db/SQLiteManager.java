package com.jiedu.project.lovefamily.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;

import com.jiedu.project.lovefamily.HBaseApp;
import com.jiedu.project.lovefamily.application.MyApplication;
import com.jiedu.project.lovefamily.bean.TCallRecordInfo;
import com.jiedu.project.lovefamily.bean.TContactInfo;
import com.jiedu.project.lovefamily.config.SysConfig;
import com.jiedu.project.lovefamily.utils.CommFunc;
import com.jiedu.project.lovefamily.utils.PinYinManager;

public class SQLiteManager extends AbstractSQLManager {

    private String LOGTAG = "SQLiteManager";
    public static SQLiteManager getInstance() {
        return MyApplication.getInstance().getSqlManager();
    }

    /**
     * 保存一条通话记录
     * @param info
     * @param dbFlag
     */
    public void saveCallRecordInfo(TCallRecordInfo info, boolean dbFlag){

        ContentValues values = new ContentValues();
        values.put(TCallRecordInfo._CALL_RECORD_ID, info.getCallRecordId());
        values.put(TCallRecordInfo._DATE, info.getDate());
        values.put(TCallRecordInfo._START_TIME, info.getStartTime());
        values.put(TCallRecordInfo._END_TIME, info.getEndTime());
        values.put(TCallRecordInfo._TOTAL_TIME, info.getTotalTime());
        values.put(TCallRecordInfo._FROM_USER, info.getFromUser());
        values.put(TCallRecordInfo._TO_USER, info.getToUser());
        values.put(TCallRecordInfo._TYPE, info.getType());
        values.put(TCallRecordInfo._RESULT, info.getResult());
        values.put(TCallRecordInfo._DIRECTION, info.getDirection());

        long ret = sqliteDB().replaceOrThrow(TABLE_CALL_RECORD, null, values);
        CommFunc.PrintLog(5, LOGTAG, "saveCallRecordInfo:"+ret);
        if (dbFlag) {
            dbChanged(info);
        }
    }

    /**
     * 获取本帐号相关的呼叫记录-分组
     * @return
     */
    public List<TCallRecordInfo> getCallRecordInfo(String userNumber) {
        List<TCallRecordInfo> list = new ArrayList<TCallRecordInfo>();
        //		String sql = "select * from " + TABLE_CALL_RECORD + " where " + CallRecordInfo._FROM_USER + " ='" + userNumber + "' or " + CallRecordInfo._TO_USER + " = '" + userNumber + "' group by " + CallRecordInfo._FROM_USER + " , " + CallRecordInfo._TO_USER + " order by " + CallRecordInfo._DATE + " desc";
        String sql = "select * from " + TABLE_CALL_RECORD + " where " + TCallRecordInfo._FROM_USER + " ='" + userNumber + "' group by " + TCallRecordInfo._TO_USER + " order by " + TCallRecordInfo._DATE + " desc, " + TCallRecordInfo._START_TIME + " desc";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        try{
            while(cursor!=null && cursor.moveToNext()){
                TCallRecordInfo info = new TCallRecordInfo();
                info.setCallRecordId(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._CALL_RECORD_ID)));
                info.setDate(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._DATE)));
                info.setStartTime(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._START_TIME)));
                info.setEndTime(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._END_TIME)));
                info.setTotalTime(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._TOTAL_TIME)));
                info.setFromUser(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._FROM_USER)));
                info.setToUser(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._TO_USER)));

                info.setType(cursor.getInt(cursor.getColumnIndex(TCallRecordInfo._TYPE)));
                info.setResult(cursor.getInt(cursor.getColumnIndex(TCallRecordInfo._RESULT)));
                info.setDirection(cursor.getInt(cursor.getColumnIndex(TCallRecordInfo._DIRECTION)));
                list.add(info);
            } 
        }

        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }

        return list;
    }

    /**
     * 获取本帐号相关的呼叫记录-分组
     * @return
     */
    public List<TCallRecordInfo> getAllCallRecordInfoByUser(String userNumber) {
        List<TCallRecordInfo> list = new ArrayList<TCallRecordInfo>();
        String sql = "select * from " + TABLE_CALL_RECORD + " where " + TCallRecordInfo._FROM_USER + " ='" + userNumber + "' order by " + TCallRecordInfo._DATE + " desc, " + TCallRecordInfo._START_TIME + " desc";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        try{
            while(cursor!=null && cursor.moveToNext()){
                TCallRecordInfo info = new TCallRecordInfo();
                info.setCallRecordId(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._CALL_RECORD_ID)));
                info.setDate(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._DATE)));
                info.setStartTime(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._START_TIME)));
                info.setEndTime(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._END_TIME)));
                info.setTotalTime(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._TOTAL_TIME)));
                info.setFromUser(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._FROM_USER)));
                info.setToUser(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._TO_USER)));

                info.setType(cursor.getInt(cursor.getColumnIndex(TCallRecordInfo._TYPE)));
                info.setResult(cursor.getInt(cursor.getColumnIndex(TCallRecordInfo._RESULT)));
                info.setDirection(cursor.getInt(cursor.getColumnIndex(TCallRecordInfo._DIRECTION)));
                list.add(info);
            }   
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        return list;
    }

    /**
     * 获取呼叫记录详情
     * @return
     */
    public List<TCallRecordInfo> getCallRecordInfo(String fromUser, String toUser) {
        List<TCallRecordInfo> list = new ArrayList<TCallRecordInfo>();
        String sql = "select * from " + TABLE_CALL_RECORD + " where (" + TCallRecordInfo._FROM_USER + " = '" + fromUser +  "' and " + TCallRecordInfo._TO_USER + " ='" + toUser + "') or (" +  TCallRecordInfo._FROM_USER + " = '" + toUser +  "' and " + TCallRecordInfo._TO_USER + " ='" + fromUser + "') order by " + TCallRecordInfo._DATE + " desc, " + TCallRecordInfo._START_TIME + " desc";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        try{
            while(cursor!=null && cursor.moveToNext()){
                TCallRecordInfo info = new TCallRecordInfo();
                info.setCallRecordId(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._CALL_RECORD_ID)));
                info.setDate(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._DATE)));
                info.setStartTime(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._START_TIME)));
                info.setEndTime(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._END_TIME)));
                info.setTotalTime(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._TOTAL_TIME)));
                info.setFromUser(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._FROM_USER)));
                info.setToUser(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._TO_USER)));

                info.setType(cursor.getInt(cursor.getColumnIndex(TCallRecordInfo._TYPE)));
                info.setResult(cursor.getInt(cursor.getColumnIndex(TCallRecordInfo._RESULT)));
                info.setDirection(cursor.getInt(cursor.getColumnIndex(TCallRecordInfo._DIRECTION)));
                list.add(info);
            }     
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        return list;
    }


    /**
     * 删除一条通话记录
     * @param callId
     */
    public void delCallRecordInfo(String callId, boolean dbFlag){
        sqliteDB().delete(TABLE_CALL_RECORD, TCallRecordInfo._CALL_RECORD_ID + " = '" + callId + "'", null);
        if (dbFlag) {
            dbChanged(new TCallRecordInfo());
        }
    }

    /**
     * 获取呼叫次数
     * @param fromeUser
     * @param toUser
     * @return
     */
    public int getCallRecordCount(String fromeUser, String toUser) {
        String sql = "select * from " + TABLE_CALL_RECORD + " where (" + TCallRecordInfo._FROM_USER + " = '" + fromeUser +  "' and " + TCallRecordInfo._TO_USER + " ='" + toUser + "') or (" +  TCallRecordInfo._FROM_USER + " = '" + toUser +  "' and " + TCallRecordInfo._TO_USER + " ='" + fromeUser + "')";
        //		String sql = "select * from " + TABLE_CALL_RECORD + " where (" + CallRecordInfo._FROM_USER + " = '" + fromeUser +  "' or " + CallRecordInfo._TO_USER + " ='" + fromeUser + "') and (" +  CallRecordInfo._FROM_USER + " = '" + toUser +  "' or " + CallRecordInfo._TO_USER + " ='" + toUser + "')";
        Cursor cursor =  sqliteDB().rawQuery(sql, null);
        int count = 0;
        if(cursor!=null)
        {
            count= cursor.getCount();
            cursor.close();
            cursor= null;
        }

        return count;
    }

    /**
     * 更新通话记录
     * @param id
     * @param info
     */
    public void updateCallRecordInfo(String id, TCallRecordInfo info, boolean dbFlag) {
        ContentValues values = new ContentValues();
        values.put(TCallRecordInfo._TOTAL_TIME, info.getTotalTime());
        values.put(TCallRecordInfo._END_TIME, info.getEndTime());
        values.put(TCallRecordInfo._RESULT, info.getResult());
        sqliteDB().update(TABLE_CALL_RECORD, values,TCallRecordInfo._CALL_RECORD_ID + " = '" + id + "'", null);
        if (dbFlag) {
            dbChanged(new TCallRecordInfo());
        }
    }

    /**
     * 更新通话记录
     */
    public void updateCallRecordInfo(TCallRecordInfo info, boolean dbFlag) {
        ContentValues values = new ContentValues();
        values.put(TCallRecordInfo._TOTAL_TIME, info.getTotalTime());
        values.put(TCallRecordInfo._END_TIME, info.getEndTime());
        values.put(TCallRecordInfo._RESULT, info.getResult());
        sqliteDB().update(TABLE_CALL_RECORD, values,TCallRecordInfo._CALL_RECORD_ID + " = '" + info.getCallRecordId() + "'", null);
        if (dbFlag) {
            dbChanged(new TCallRecordInfo());
        }
    }

    /**
     * 通过呼叫记录ID获取呼叫记录
     * @return
     */
    public TCallRecordInfo getCallRecordInfoById(String callRecordId) {
        String sql = "select * from " + TABLE_CALL_RECORD + " where " + TCallRecordInfo._CALL_RECORD_ID + " = '" + callRecordId + "'";
        TCallRecordInfo info = new TCallRecordInfo();
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        try{
            if(cursor!=null && cursor.moveToNext()){
                info.setCallRecordId(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._CALL_RECORD_ID)));
                info.setDate(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._DATE)));
                info.setStartTime(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._START_TIME)));
                info.setEndTime(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._END_TIME)));
                info.setTotalTime(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._TOTAL_TIME)));
                info.setFromUser(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._FROM_USER)));
                info.setToUser(cursor.getString(cursor.getColumnIndex(TCallRecordInfo._TO_USER)));
                info.setType(cursor.getInt(cursor.getColumnIndex(TCallRecordInfo._TYPE)));
                info.setResult(cursor.getInt(cursor.getColumnIndex(TCallRecordInfo._RESULT)));
                info.setDirection(cursor.getInt(cursor.getColumnIndex(TCallRecordInfo._DIRECTION)));
            }   
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        return info;
    }

    /**
     * 更新int类型的通话记录某一项
     */
    public void updateCallRecordInfo(String id, String key, int value, boolean dbFlag) {
        ContentValues values = new ContentValues();
        values.put(key, value);
        sqliteDB().update(TABLE_CALL_RECORD, values,TCallRecordInfo._CALL_RECORD_ID + " = '" + id + "'", null);
        if (dbFlag) {
            dbChanged(new TCallRecordInfo());
        }
    }

    /**
     * 更新字符串类型的通话记录某一项
     */
    public void updateCallRecordInfo(String id, String key, String value, boolean dbFlag) {
        ContentValues values = new ContentValues();
        values.put(key, value);
        sqliteDB().update(TABLE_CALL_RECORD, values,TCallRecordInfo._CALL_RECORD_ID + " = '" + id + "'", null);
        if (dbFlag) {
            dbChanged(new TCallRecordInfo());
        }
    }



    /**
     * 获取所联系人信息
     * @return
     */
    public List<TContactInfo> getAllContactInfo() {
        List<TContactInfo> list = new ArrayList<TContactInfo>();
        // TODO Auto-generated method stub
        String sql = "select * from " + TABLE_CONTACT_INFO;// + " order by " + TContactInfo._CONTACT_SORT_KEY + " asc";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        try{
            while( cursor != null && cursor.moveToNext()){
                TContactInfo info = new TContactInfo();
                info.setContactId(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_ID)));
                info.setName(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_NAME)));
                info.setPhoneNum(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_NUMBER)));
                info.setFirstChar(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_SORT_KEY)));
                info.setPhotoId(cursor.getLong(cursor.getColumnIndex(TContactInfo._CONTACT_PHOTO_ID)));
                info.setLookUpKey(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_LOOK_UP_KEY)));
                info.setUsertype(cursor.getInt(cursor.getColumnIndex(TContactInfo._CONTACT_USERTYPE)));
                list.add(info);
            }            
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        return list;
    }

    public List<TContactInfo> getContactInfo_Search(String key)
    {
        List<TContactInfo> list = new ArrayList<TContactInfo>(); 

        String causeString = "";
        String sql = "";

        if(key==null|| key.equals("")==true)
        {
            sql = "select * from " + TABLE_CONTACT_INFO + " order by " + TContactInfo._CONTACT_SORT_KEY + " asc";
        }
        else 
        {
            causeString += "(" + TContactInfo._CONTACT_NAME + " like '%" + key
            + "%' or " + TContactInfo._CONTACT_NUMBER + " like '%" + key
            + "%' or " + TContactInfo._CONTACT_SORT_KEY + " like '%" + key
            + "%')";
            sql = "select * from " + TABLE_CONTACT_INFO + " where "+ causeString;
        }

        Cursor cursor = sqliteDB().rawQuery(sql, null);
        try{
            while(cursor!=null && cursor.moveToNext()){
                TContactInfo info = new TContactInfo();
                info.setContactId(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_ID)));
                info.setName(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_NAME)));
                info.setPhoneNum(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_NUMBER)));
                info.setFirstChar(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_SORT_KEY)));
                info.setPhotoId(cursor.getLong(cursor.getColumnIndex(TContactInfo._CONTACT_PHOTO_ID)));
                info.setLookUpKey(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_LOOK_UP_KEY)));
                info.setUsertype(cursor.getInt(cursor.getColumnIndex(TContactInfo._CONTACT_USERTYPE)));
                list.add(info);
            }   
        }

        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        return list;
    }

    /**
     * 根据联系人ID获取联系人
     * @param contactId
     * @return
     */
    public TContactInfo getContactInfoById(String contactId) {
        TContactInfo info = null;
        String sql = "select * from " + TABLE_CONTACT_INFO + " where " + TContactInfo._CONTACT_ID + " ='" + contactId + "'";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        try{
            if(cursor!=null && cursor.moveToNext()){
                info = new TContactInfo();
                info.setContactId(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_ID)));
                info.setName(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_NAME)));
                info.setPhoneNum(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_NUMBER)));
                info.setFirstChar(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_SORT_KEY)));
                info.setPhotoId(cursor.getLong(cursor.getColumnIndex(TContactInfo._CONTACT_PHOTO_ID)));
                info.setLookUpKey(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_LOOK_UP_KEY)));
                info.setUsertype(cursor.getInt(cursor.getColumnIndex(TContactInfo._CONTACT_USERTYPE)));
            }  
        }

        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        return info;
    }

    /**
     * 根据联系人号码获取联系人
     * @return
     */
    public TContactInfo getContactInfoByNumber(String number) {
        TContactInfo info = null;
        String sql = "select * from " + TABLE_CONTACT_INFO + " where " + TContactInfo._CONTACT_NUMBER + " ='" + number + "'";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        try{

            if(cursor!=null && cursor.moveToNext()){
                info = new TContactInfo();
                info.setContactId(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_ID)));
                info.setName(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_NAME)));
                info.setPhoneNum(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_NUMBER)));
                info.setFirstChar(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_SORT_KEY)));
                info.setPhotoId(cursor.getLong(cursor.getColumnIndex(TContactInfo._CONTACT_PHOTO_ID)));
                info.setLookUpKey(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_LOOK_UP_KEY)));
                info.setUsertype(cursor.getInt(cursor.getColumnIndex(TContactInfo._CONTACT_USERTYPE)));
            } 

        }

        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        if (info == null) {
            String[] pinyin = PinYinManager.toPinYin(number);
            info = new TContactInfo();
            info.setName(number);
            info.setPhoneNum(number);
            info.setContactId(number);
            info.setPhotoId(null);
            info.setFirstChar(pinyin[0]);
            info.setLookUpKey(pinyin[1]);
            info.setUsertype(SysConfig.USERTYPE_SYSTEM);
            saveContactInfo(info,true); 
        }

        return info;
    }

    /**
     * 根据联系人名称获取联系人
     * TODO 需要更改
     * @return
     */
    public TContactInfo getContactInfoByName(String name) {
        TContactInfo info = new TContactInfo();
        String sql = "select * from " + TABLE_CONTACT_INFO + " where " + TContactInfo._CONTACT_NAME + " ='" + name + "'";
        Cursor cursor = sqliteDB().rawQuery(sql, null);
        try{
            if(cursor!=null && cursor.moveToNext()){
                info.setContactId(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_ID)));
                info.setName(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_NAME)));
                info.setPhoneNum(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_NUMBER)));
                info.setFirstChar(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_SORT_KEY)));
                info.setPhotoId(cursor.getLong(cursor.getColumnIndex(TContactInfo._CONTACT_PHOTO_ID)));
                info.setLookUpKey(cursor.getString(cursor.getColumnIndex(TContactInfo._CONTACT_LOOK_UP_KEY)));
                info.setUsertype(cursor.getInt(cursor.getColumnIndex(TContactInfo._CONTACT_USERTYPE)));
            }    
        }

        finally {
            if(cursor!=null)
            {
                cursor.close();
                cursor = null;
            }
        }
        return info;
    }

    /**
     * 添加多条联系人
     * @param list
     */
    public void saveContactInfoList(final List<TContactInfo> list, final boolean dbFlag) {
        HBaseApp.post2WorkRunnable(new Runnable (){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                for (int i = 0; i < list.size(); i++) {
                    saveContactInfo(list.get(i), dbFlag);
                } 
            }

        });

    }


    public void SaveContactByID(String userId) {
        TContactInfo info = getContactInfoByNumber(userId);
        if (info == null) {
            String[] pinyin = PinYinManager.toPinYin(userId);
            TContactInfo contactInfo = new TContactInfo();
            contactInfo.setName(userId);
            contactInfo.setPhoneNum(userId);
            contactInfo.setContactId(userId);
            contactInfo.setPhotoId(null);
            contactInfo.setFirstChar(pinyin[0]);
            contactInfo.setLookUpKey(pinyin[1]);
            contactInfo.setUsertype(SysConfig.USERTYPE_SYSTEM);
            saveContactInfo(contactInfo,true); 
        }
    }

    //从服务器返回的用户信息进行刷新操作
    //如果系统通讯录有 天翼账号类型 登陆 算作天翼账号 但天翼账号的name 如果系统通讯录有采用系统通讯录。（天翼账号没有返回name）
    //新浪微博可以取到name
    public void save_updateContactList(List<TContactInfo> list)
    {
        for (int i = 0; i < list.size(); i++) {

            saveContactInfo(list.get(i), false);
        }
        dbChanged(new TContactInfo()); //全部保存后刷新
    }
    /**
     * 添加一条联系人信息
     * @param info
     */
    public void saveContactInfo(TContactInfo info, boolean dbFlag){
        //  CommFunc.PrintLog(5, LOGTAG, "name:" + info.getName());
        if(info.getPhoneNum().length()==0 || info.getPhoneNum() == null)
            return;
        {
            ContentValues values = new ContentValues();
            values.put(TContactInfo._CONTACT_ID, info.getContactId());
            values.put(TContactInfo._CONTACT_NAME, info.getName());
            values.put(TContactInfo._CONTACT_NUMBER, info.getPhoneNum());
            values.put(TContactInfo._CONTACT_SORT_KEY, info.getFirstChar());
            values.put(TContactInfo._CONTACT_PHOTO_ID, info.getPhotoId());
            values.put(TContactInfo._CONTACT_LOOK_UP_KEY, info.getLookUpKey());
            values.put(TContactInfo._CONTACT_USERTYPE, info.getUsertype());

            //CommFunc.PrintLog(5, LOGTAG, "saveContactInfo name:"+info.getName()+" number:"+info.getPhoneNum());
            sqliteDB().replaceOrThrow(TABLE_CONTACT_INFO, null, values);
            //   CommFunc.PrintLog(5, LOGTAG, "getContactId:"+info.getContactId() +" name:"+info.getName()+"  number:"+info.getPhoneNum());

        }
//        if (dbFlag) {
//            dbChanged(new TContactInfo());
//        }
    }

    /**
     * 更新联系人信息
     * @param info
     */
    public void updateContactInfo(TContactInfo info, boolean dbFlag){
        ContentValues values = new ContentValues();
        values.put(TContactInfo._CONTACT_ID, info.getContactId());
        values.put(TContactInfo._CONTACT_NAME, info.getName());
        values.put(TContactInfo._CONTACT_NUMBER, info.getPhoneNum());
        values.put(TContactInfo._CONTACT_SORT_KEY, info.getFirstChar());
        values.put(TContactInfo._CONTACT_PHOTO_ID, info.getPhotoId());
        values.put(TContactInfo._CONTACT_LOOK_UP_KEY, info.getLookUpKey());
        values.put(TContactInfo._CONTACT_USERTYPE, info.getUsertype());
        sqliteDB().update(TABLE_CONTACT_INFO, values,TContactInfo._CONTACT_ID + " = '" + info.getContactId() + "'", null);
        if (dbFlag) {
            dbChanged(new TContactInfo());
        }
    }

    /**
     * 更新联系人信息的某一项 value为字符串类型

     */
    public void updateContactInfo(String contactId, String key, String value, boolean dbFlag){
        ContentValues values = new ContentValues();
        values.put(key, value);
        sqliteDB().update(TABLE_CONTACT_INFO, values, key + " = '" + value + "'", null);
        if (dbFlag) {
            dbChanged(new TContactInfo());
        }
    }

    /**
     * 根据联系人ID删除联系人信息
     * @param contactId
     */
    public void deltetContactInfo(String contactId, boolean dbFlag){
        sqliteDB().delete(TABLE_CONTACT_INFO, TContactInfo._CONTACT_ID + " = '" + contactId + "'", null);
        if (dbFlag) {
            dbChanged(new TContactInfo());
        }
    }

    /**
     * 删除所有联系人信息
     */
    public void deleteAllContactInfo(boolean dbFlag) {
        sqliteDB().delete(TABLE_CONTACT_INFO, null, null);
        if (dbFlag) {
            dbChanged(new TContactInfo());
        }
    }





    /**
     * 关闭SQLiteDatabase
     */
    public void clearInstance() {
        if (sqliteDB != null)
            sqliteDB.close();
    }

    public void dbChanged(final Object object) {
        HBaseApp.post2UIRunnable(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                setChanged();
                notifyObservers(object); 
            }

        });

    }

}
