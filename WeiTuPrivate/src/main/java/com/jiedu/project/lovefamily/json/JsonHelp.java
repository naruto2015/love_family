package com.jiedu.project.lovefamily.json;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.jiedu.project.lovefamily.message.MessageInfoUtil;
import com.jiedu.project.lovefamily.mode.InvitationCode;
import com.jiedu.project.lovefamily.mode.Monitor;
import com.jiedu.project.lovefamily.mode.PayStyleMode;
import com.jiedu.project.lovefamily.mode.PositionBean;
import com.jiedu.project.lovefamily.mode.SafeRange;
import com.jiedu.project.lovefamily.mode.ServiceMode;
import com.jiedu.project.lovefamily.mode.User;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;
import com.jiedu.project.lovefamily.sqlite.FriendDao;
import com.jiedu.project.lovefamily.sqlite.UserDao;
import com.jiedu.project.lovefamily.sqlite.UserDaoImpl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/22.
 */
public class JsonHelp {
    /**
     * 处理登录信息
     *
     * @param json
     * @param context
     * @param verification
     * @return
     */
    public boolean dealLoginJson(String json, Context context, String verification) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.optBoolean("ok")) {
                JSONObject data = new JSONObject(jsonObject.optString("data"));
                String phoneUrl="";
                if(!TextUtils.isEmpty(data.optString("portrait"))){
                    phoneUrl=RequestHelp.HTTP_HEAD + RequestHelp.HTTP_ACTION + "/wttp_portrait/" + data.optString("portrait");
                }

                UserDao userDao=new UserDaoImpl(context);
                User user=new User();
                user.setUserName(data.optString("nickName"));
                user.code=data.optString("inviteCode");
                user.login=1;
                userDao.insertUserInfo(user);

                //SharedPreferencesUtil.saveUserLoginInfo(context, data.optString("customerId"), data.optString("phone"),data.optString("nickName") ,data.optString("sex"),phoneUrl,data.optString("birthday").replace("00:00:00",""),data.optString("address"),data.optString("inviteCode"), verification, data.optString("needUploadPostion"), data.optInt("frequency"),data.optString("useStatus"),data.optString("orderMsg"));
                SharedPreferencesUtil.saveUserLoginInfo2(context, data.optString("customerId"), data.optString("phone"),
                        data.optString("nickName") ,data.optString("sex"),phoneUrl,data.optString("birthday").replace("00:00:00",""),
                        data.optString("address"),data.optString("inviteCode"), verification, data.optString("needUploadPostion"),
                        data.optInt("frequency"),data.optString("useStatus"),data.optString("orderMsg"),data.optString("isUploadLocation"),data.optString("ctccPhone"),data.optString("isExpired"));
                return true;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private PositionBean positionBean;
    public void dealLocationResult(String json, Handler handler,int index) {
        try {
            JSONObject jsonObject = new JSONObject(json);
//            Log.e("0011","定位信息"+json);
            if (!TextUtils.isEmpty(jsonObject.optString("data"))) {
                JSONObject dataObject = new JSONObject(jsonObject.optString("data"));
                LatLng latLng = new LatLng(dataObject.optDouble("latitude"), dataObject.optDouble("longitude"));
                String uploadTime=dataObject.optString("uploadTime");
                Message message = handler.obtainMessage();
                positionBean=new PositionBean();
                message.arg1 = MessageInfoUtil.GET_LOCATION;
                message.arg2=index;
                //message.obj = latLng;
                positionBean.latLng=latLng;
                positionBean.uploadTime=uploadTime;
                message.obj=positionBean;
                message.sendToTarget();
            }else{
//                Log.e("0011","空数据")  ;
                handler.sendEmptyMessage(MessageInfoUtil.SHOW_TOAST);
            }
        } catch (JSONException e) {
            e.printStackTrace();
           ;
        }

    }

    public void dealAddMember(String json, Handler handler) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            if (jsonObject.optBoolean("ok")) {
                handler.sendEmptyMessage(MessageInfoUtil.ADD_MEMBER);
            } else {
                Message message = handler.obtainMessage();
                message.what = MessageInfoUtil.ADD_MEMBER_FAIL;
                message.obj = jsonObject.optString("msg");
                message.sendToTarget();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public void dealMemberList(String json, Context context, Handler handler, ArrayList<User> list) {
        try {
            JSONObject jsonObject = new JSONObject(json);

            if (jsonObject.optBoolean("ok")) {
                list.clear();
                String data = jsonObject.optString("data");
                JSONArray dataArray = new JSONArray(data);
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject userObjece = dataArray.getJSONObject(i);
                    User user = new User();
                    user.setUserName(userObjece.optString("nickName"));
                    user.setUserStatue(userObjece.optString("statusName"));
                    user.setId(userObjece.optString("id"));
                    user.setMonitoredId(userObjece.optString("monitoredId"));
                    user.setMonitorId(userObjece.optString("monitorId"));
                    user.setStatueNum(userObjece.optString("status"));
                    user.setPhone(userObjece.optString("phone"));
                    user.uploadTime=userObjece.optString("uploadTime");
                    if(!TextUtils.isEmpty( userObjece.optString("portrait"))){
                        user.setPhotoUrl(RequestHelp.HTTP_HEAD + RequestHelp.HTTP_ACTION + "/wttp_portrait/" + userObjece.optString("portrait"));
                    }
                    user.setSex(userObjece.optString("sex"));
                    user.setAddress(userObjece.optString("address"));
                    user.setBirthday(userObjece.optString("birthday").replace("00:00:00","").trim());
                    list.add(0, user);
                }


                Message message = handler.obtainMessage();
                message.arg1 = MessageInfoUtil.MEMBER;
                message.sendToTarget();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("0011", "Json解析错误", e);
            list.clear();
            Message message = handler.obtainMessage();
            message.arg1 = MessageInfoUtil.MEMBER;
            message.sendToTarget();
        }
    }

    public void dealEdit(String json, Handler handler) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.optBoolean("ok")) {
                handler.sendEmptyMessage(MessageInfoUtil.EDIT);
            } else {
                Message message = handler.obtainMessage();
                message.what = MessageInfoUtil.EDIT_FAIL;
                message.obj = jsonObject.optString("msg");
                message.sendToTarget();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = MessageInfoUtil.EDIT_FAIL;
            message.obj ="修改信息失败";
            message.sendToTarget();
        }
    }


    public void dealSubmitInvitationCode(String json, Handler handler) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.optBoolean("ok")) {
                handler.sendEmptyMessage(MessageInfoUtil.SUBMIT_INVITATION_CODE);
            } else {
                Message message = handler.obtainMessage();
                message.what = MessageInfoUtil.SUBMIT_INVITATION_CODE_FAIL;
                message.obj = jsonObject.optString("msg");
                message.sendToTarget();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void dealGetMonitorList(String json, Handler handler, ArrayList<InvitationCode> list) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.optBoolean("ok")) {
                list.clear();
                ;
                JSONArray jsonArray = new JSONArray(jsonObject.optString("data"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject invitation = jsonArray.getJSONObject(i);
                    InvitationCode invitationCode = new InvitationCode(invitation.optString("customerId"), invitation.optString("phone"), invitation.optString("type"), invitation.optString("inviteCode"), invitation.optString("needUploadPostion"), invitation.optString("frequency"));
                    list.add(invitationCode);
                }
                handler.sendEmptyMessage(MessageInfoUtil.GET_MONITOR_LIST);
            } else {
                handler.sendEmptyMessage(MessageInfoUtil.GET_MONITOR_LIST_FAIL);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void dealDeleteMonitor(String json, Handler handler) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.optBoolean("ok")) {
                handler.sendEmptyMessage(MessageInfoUtil.DELETE_MONITOR);
            } else {
                Message message = handler.obtainMessage();
                message.what = MessageInfoUtil.DELETE_MONITOR_FAIL;
                message.obj = jsonObject.optString("data");
                message.sendToTarget();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void dealGetSafeRangeList(String json, Handler handler, ArrayList<SafeRange> list) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.optBoolean("ok")) {
                list.clear();
                JSONArray data = new JSONArray(jsonObject.optString("data"));
                for (int i = 0; i < data.length(); i++) {
                    JSONObject safeJsonObject = data.getJSONObject(i);
                    SafeRange safeRange = new SafeRange();
                    safeRange.init(safeJsonObject.optDouble("latitude"), safeJsonObject.optDouble("longitude"), safeJsonObject.optString("address"), safeJsonObject.optInt("radius"), safeJsonObject.optString("refId"), safeJsonObject.optString("title"), safeJsonObject.optString("id"));
                    list.add(safeRange);
                }
                handler.sendEmptyMessage(MessageInfoUtil.SAFE_RANGE_LIST);
            } else {
                handler.sendEmptyMessage(MessageInfoUtil.SAFE_RANGE_LIST_FAIL);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(MessageInfoUtil.SAFE_RANGE_LIST_FAIL);
        }

    }

    public void dealDeleteSafeRange(String json, Handler handler) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.optBoolean("ok")) {
                handler.sendEmptyMessage(MessageInfoUtil.DELETE_SAFE_RANGE);

            } else {
                handler.sendEmptyMessage(MessageInfoUtil.DELETE_SAFE_RANGE_FAIL);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void dealGetMonitorRequestList(String json,Handler handler){
        try {
            JSONObject jsonObject=new JSONObject(json);
            if(jsonObject.optBoolean("ok")){
                JSONArray array=new JSONArray(jsonObject.optString("data"));
                if(array.length()!=0){
                    Message message=handler.obtainMessage();
                    message.what=MessageInfoUtil.SHOW_DIAAOLG;
                    message.obj=json;
                    message.sendToTarget();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
public ArrayList<Monitor> drealMonitorJson(String json,ArrayList<Monitor> list){
    try {
        JSONObject jsonObject=new JSONObject(json);
        JSONArray data=new JSONArray(jsonObject.optString("data"));
        for (int i = 0; i < data.length(); i++) {
            JSONObject monitorJson=data.getJSONObject(i);
            Monitor monitor=new Monitor();
            monitor.setBirthday(monitorJson.optString("birthday"));
            monitor.setMonitorId(monitorJson.optString("monitorId"));
            monitor.setAddress(monitorJson.optString("address"));
            monitor.setSex(monitorJson.optString("sex"));
            monitor.setNickName(monitorJson.optString("nickName"));
            monitor.setId(monitorJson.optString("id"));
            monitor.setStatus(monitorJson.optString("status"));
            monitor.setPhone(monitorJson.optString("phone"));
            list.add(monitor);

        }

    } catch (JSONException e) {
        e.printStackTrace();
    }

    return list;
}
    public boolean isAgreeOrRefuse(String json){
        try {
            JSONObject jsonObject=new JSONObject(json);
            if(jsonObject.optBoolean("ok")){
                return  true;
            }else{
                return false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }

public void drealPayInfo(String json,ArrayList<PayStyleMode>list,Handler handle){
    try {
        JSONObject jsonObject=new JSONObject(json);
        if(jsonObject.optBoolean("ok")){
            JSONArray dataArray=new JSONArray(jsonObject.optString("data"));
            for (int i = 0; i <dataArray.length() ; i++) {
            JSONObject payModeObj=dataArray.getJSONObject(i);
                PayStyleMode payMode=new PayStyleMode();
                payMode.setId(payModeObj.optString("id"));
                payMode.setImgUrl(RequestHelp.HTTP_HEAD + RequestHelp.HTTP_ACTION + "/wttppay/" + payModeObj.optString("ico"));
                payMode.setName(payModeObj.optString("name"));
                payMode.setRemark(payModeObj.optString("remark"));
                ArrayList<ServiceMode>serviceList=new ArrayList<>();
                JSONArray lstBusiType=new JSONArray(payModeObj.optString("lstBusiType"));
                for (int j = 0; j < lstBusiType.length(); j++) {
                    JSONObject serviceObject=lstBusiType.getJSONObject(j);
                    ServiceMode serviceMode=new ServiceMode();
                    serviceMode.setCode(serviceObject.optString("code"));
                    serviceMode.setRemark(serviceObject.optString("remark"));
                    serviceMode.setName(serviceObject.optString("name"));
                    serviceMode.setId(serviceObject.optString("id"));
                    serviceMode.setBusiExpense(serviceObject.optString("busiExpense"));
                    serviceMode.setCreditsExpense(serviceObject.optString("creditsExpense"));
                    serviceMode.setMaxPersonNum(serviceObject.optString("maxPersonNum"));
                    serviceList.add(serviceMode);

                }
                payMode.setList(serviceList);
                list.add(payMode);

                handle.sendEmptyMessage(MessageInfoUtil.GET_PAY_INFO);
            }




        }




    } catch (JSONException e) {
        e.printStackTrace();
    }
}

}
