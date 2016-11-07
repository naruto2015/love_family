package com.jiedu.project.lovefamily.activitydailog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.activity.BaseActivity;
import com.jiedu.project.lovefamily.activity.EditActivity;
import com.jiedu.project.lovefamily.activity.LocationActivity;
import com.jiedu.project.lovefamily.activity.SafeListActivity;
import com.jiedu.project.lovefamily.json.JsonHelp;
import com.jiedu.project.lovefamily.message.MessageInfoUtil;
import com.jiedu.project.lovefamily.mode.PositionBean;
import com.jiedu.project.lovefamily.mode.User;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;
import com.jiedu.project.lovefamily.stringutil.IntentString;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeFourDialogActivity extends BaseActivity implements View.OnClickListener {

    private TextView edit;
    private TextView getLocation;
    private TextView setSafeArea;
    private  TextView delete;
    User user=null;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_four_dialog);
        jsonHelp=new JsonHelp();
        help=new RequestHelp();

        context=this;
        user= (User) getIntent().getSerializableExtra("user");
        edit = (TextView) findViewById(R.id.edit);
        getLocation = (TextView) findViewById(R.id.get_location);
        setSafeArea = (TextView) findViewById(R.id.set_safe_area);
        delete= (TextView) findViewById(R.id.delete);

        if(user.getStatueNum().equals("1")){
            getLocation.setVisibility(View.GONE);
            setSafeArea.setVisibility(View.GONE);

        }else{
            getLocation.setVisibility(View.VISIBLE);
            setSafeArea.setVisibility(View.VISIBLE);
        }
        getLocation.setOnClickListener(this);
        setSafeArea.setOnClickListener(this);
        edit.setOnClickListener(this);
        delete.setOnClickListener(this);

    }
    RequestHelp help;
    JsonHelp jsonHelp;

    ProgressDialog progressDialog=null;

    @Override
    public void onClick(View v) {
        {

            switch (v.getId()) {
                case R.id.edit:
                    Intent intent=new Intent(HomeFourDialogActivity.this, EditActivity.class);
                    intent.putExtra(IntentString.INTENT_TITLE,user.getUserName());
                    intent.putExtra(IntentString.CUSTOMER_ID,user.getMonitoredId());
                    intent.putExtra(IntentString.NICK_NAME,user.getUserName());
                    intent.putExtra(IntentString.BRITHDAY,user.getBirthday());
                    intent.putExtra(IntentString.SEX,user.getSex());
                    intent.putExtra(IntentString.ADDRESS,user.getAddress());
                    intent.putExtra(IntentString.PHONO_URL,user.getPhotoUrl());
                    intent.putExtra(IntentString.PHONE,user.getPhone());
                    ( (Activity) HomeFourDialogActivity.this).startActivityForResult(intent,2);

                    break;
                case R.id.get_location:

                    progressDialog=new ProgressDialog(context);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setMessage("正在获取实时位置.....");
                    //progressDialog.setCancelable(false);
                    progressDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String result=help.getLocation(user.getMonitoredId());

                            if(result!=null){
                                Message msg=Message.obtain();
                                msg.what=1;
                                msg.obj=result;
                                handler.sendMessage(msg);
                            }

                        }
                    }).start();

                    break;
                case R.id.set_safe_area:
                    Intent safeIntent=new Intent(context, SafeListActivity.class);
                    safeIntent.putExtra(IntentString.REF_ID,user.getId());
                    safeIntent.putExtra(IntentString.CUSTOMER_ID,user.getMonitoredId());
                    context.startActivity(safeIntent);
                    finish();
                    break;
                case R.id.delete:

                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setTitle("提示")
                            .setMessage("是否删除?")
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String result= help.deleteMonitor(SharedPreferencesUtil.getInfo(context,"customerId"),user.getMonitoredId());
                                            if(result!=null){
                                                Message msg=Message.obtain();
                                                msg.obj=result;
                                                msg.what=2;
                                                handler.sendMessage(msg);
                                            }

                                        }
                                    }).start();

                                }
                            })
                            .setNegativeButton("取消",null)
                            .show();

                    break;

            }
        }
    }

    private PositionBean positionBean=null;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
//                      Log.e("0011","定位信息"+json);
                        if (!TextUtils.isEmpty(jsonObject.optString("data"))) {
                            JSONObject dataObject = new JSONObject(jsonObject.optString("data"));
                            LatLng latLng = new LatLng(dataObject.optDouble("latitude"), dataObject.optDouble("longitude"));
                            String uploadTime=dataObject.optString("uploadTime");

                            Intent intent = new Intent(context, LocationActivity.class);
                            intent.putExtra("lat", latLng.latitude);
                            intent.putExtra("lon", latLng.longitude);
                            intent.putExtra(IntentString.NICK_NAME,user.getUserName());
                            intent.putExtra(IntentString.PHONO_URL,user.getPhotoUrl());
                            intent.putExtra("uploadTime",uploadTime);
                            context.startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ;
                    }
                    break;
                case 2:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        String ok=jsonObject.optString("ok");
                        if(ok.equals("true")){
                            setResult(1002);
                            finish();
                        }else{
                            Toast.makeText(HomeFourDialogActivity.this,"删除失败!",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1002){
            setResult(1002);
            finish();
        }
    }
}
