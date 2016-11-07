package com.jiedu.project.lovefamily.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;
import com.jiedu.project.lovefamily.sqlite.UserDao;
import com.jiedu.project.lovefamily.sqlite.UserDaoImpl;
import com.jiedu.project.lovefamily.utils.UpLoadDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SitePlaceActivity extends BaseActivity {


    private Switch open;
    private RequestHelp requestHelp=new RequestHelp();
    private RelativeLayout per_upload;
    private TextView myupload;
    private ImageView add_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_place);


        initView();
        getInfor();


    }



    HashMap<String,String> hashMap;
    public void getInfor(){
        {
            hashMap= SharedPreferencesUtil.getUserLoginInfo(this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HashMap map=new HashMap<String,String>();
                    map.put("customerId",SharedPreferencesUtil.getInfo(SitePlaceActivity.this,"customerId"));
                    String json=requestHelp.requestPost(RequestHelp.GET_USER_INFO,map);
                    Log.i("TAG",json);
                    if(json!=null){
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            JSONObject data = new JSONObject(jsonObject.optString("data"));
                            String phoneUrl="";
                            if(!TextUtils.isEmpty(data.optString("portrait"))){
                                phoneUrl=RequestHelp.HTTP_HEAD + RequestHelp.HTTP_ACTION + "/wttp_portrait/" + data.optString("portrait");
                            }

                            String verification=hashMap.get("verification");
                           // SharedPreferencesUtil.saveUserLoginInfo(SitePlaceActivity.this, data.optString("customerId"), data.optString("phone"),data.optString("nickName") ,data.optString("sex"),phoneUrl,data.optString("birthday").replace("00:00:00",""),data.optString("address"),data.optString("inviteCode"), verification, data.optString("needUploadPostion"), data.optInt("frequency"),data.optString("useStatus"),data.optString("orderMsg"));
                            SharedPreferencesUtil.saveUserLoginInfo2(SitePlaceActivity.this, data.optString("customerId"), data.optString("phone"),
                                    data.optString("nickName") ,data.optString("sex"),phoneUrl,data.optString("birthday").replace("00:00:00",""),
                                    data.optString("address"),data.optString("inviteCode"), verification, data.optString("needUploadPostion"),
                                    data.optInt("frequency"),data.optString("useStatus"),data.optString("orderMsg"),data.optString("isUploadLocation"),data.optString("ctccPhone"),data.optString("isExpired"));

//                            Intent intent=new Intent();
//                            intent.setAction("USER_STATUS");
//                            intent.addCategory(Intent.CATEGORY_DEFAULT);
//                            intent.putExtra("useStatus",data.optString("useStatus"));
//                            sendBroadcast(intent);

                            handler.sendEmptyMessage(1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();
        }
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    String isUploadLocation=SharedPreferencesUtil.getInfo(SitePlaceActivity.this,"isUploadLocation");
                    String frequency=SharedPreferencesUtil.getInfo(SitePlaceActivity.this,"frequency");
                    if(isUploadLocation.equals("0")){
                        open.setChecked(true);
                    }else{
                        open.setChecked(false);
                        per_upload.setVisibility(View.GONE);

                    }

                    myupload.setText(Integer.valueOf(frequency)/60+"");
                    break;
                case 2:
                   // getInfor();
                    break;
            }
        }
    };

    String isUploadLocation="";
    String customerId="";




    public void initView(){
        open= (Switch) findViewById(R.id.open);
        myupload= (TextView) findViewById(R.id.myupload);
        add_back= (ImageView) findViewById(R.id.add_back);


        add_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(1001);
                finish();
            }
        });

        open.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    isUploadLocation=0+"";
                    per_upload.setVisibility(View.VISIBLE);
                }else{
                    isUploadLocation=1+"";
                    per_upload.setVisibility(View.GONE);
                }
                new Thread(new Runnable() {
                     @Override
                     public void run() {
                         customerId= SharedPreferencesUtil.getInfo(SitePlaceActivity.this,"customerId");
                         HashMap map=new HashMap<String,String>();
                         map.put("customerId",customerId);
                         map.put("isUploadLocation",isUploadLocation);
                         requestHelp.requestPost(RequestHelp.IS_UPLOAD_LOCATION,map);
                         handler.sendEmptyMessage(2);
                     }
                 }).start();
            }
        });
//         open.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View view) {
//                 if(open.getText().toString().equals("开")){
//                     open.setText("关");
//                     isUploadLocation=1+"";
//                 }else{
//                     open.setText("开");
//                     isUploadLocation=0+"";
//                 }
//
//                 new Thread(new Runnable() {
//                     @Override
//                     public void run() {
//                         customerId= SharedPreferencesUtil.getInfo(SitePlaceActivity.this,"customerId");
//                         HashMap map=new HashMap<String,String>();
//                         map.put("customerId",customerId);
//                         map.put("isUploadLocation",isUploadLocation);
//                         requestHelp.requestPost(RequestHelp.IS_UPLOAD_LOCATION,map);
//                     }
//                 }).start();
//
//             }
//
//         });

        per_upload= (RelativeLayout) findViewById(R.id.per_upload);
        per_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

    }

    UpLoadDialog upLoadDialog=null;
    public void showDialog(){
        upLoadDialog=new UpLoadDialog(SitePlaceActivity.this);
        upLoadDialog.setYiluOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upLoadDialog.dismiss();
                myupload.setText("1");
                upInterval(1*60+"");
            }
        });
        upLoadDialog.setliushiOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upLoadDialog.dismiss();
                myupload.setText("60");
                upInterval(60*60+"");
            }
        });
        upLoadDialog.setsanshiOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upLoadDialog.dismiss();
                myupload.setText("30");
                upInterval(30*60+"");
            }
        });
        upLoadDialog.setShiOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upLoadDialog.dismiss();
                myupload.setText("10");
                upInterval(10*60+"");
            }
        });

        upLoadDialog.setCancleOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upLoadDialog.dismiss();
            }
        });


    }
    //String uploadInterval="";
    private void upInterval(final String uploadInterval){
        new Thread(new Runnable() {
            @Override
            public void run() {
                customerId= SharedPreferencesUtil.getInfo(SitePlaceActivity.this,"customerId");

                HashMap map=new HashMap<String,String>();
                map.put("customerId",customerId);
                map.put("uploadInterval",uploadInterval);
                String json2=requestHelp.requestPost(RequestHelp.UPLOAD_INTERVAL,map);
                Log.i("TAG",json2);

                handler.sendEmptyMessage(2);
            }
        }).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            setResult(1001);
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }
}