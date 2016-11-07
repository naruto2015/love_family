package com.jiedu.project.lovefamily.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.fragment.MyFragment;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;
import com.jiedu.project.lovefamily.utils.CustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PhoneCollectionActivity extends BaseActivity {



    private EditText phone;
    private EditText code;
    private TextView getcode;
    private TextView pay;

    private RequestHelp requestHelp;

    String busiTypeId;
    String typeId;
    String busiTypeCode;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_free02);

        phone= (EditText) findViewById(R.id.phone);
        code= (EditText) findViewById(R.id.et_code);
        getcode= (TextView) findViewById(R.id.getCode);
        pay= (TextView) findViewById(R.id.pay);
        back= (ImageView) findViewById(R.id.back);

        busiTypeId=getIntent().getStringExtra("busiTypeId");
        typeId=getIntent().getStringExtra("typeId");
        busiTypeCode=getIntent().getStringExtra("busiTypeCode");

        requestHelp=new RequestHelp();


        getcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HashMap map=new HashMap<String,String>();
                        map.put("phone",phone.getText().toString().trim());
                        String json=requestHelp.requestPost(RequestHelp.GET_VERIY_CODE,map);
                        Log.i("TAG",json+"");
                        try {
                            JSONObject jsonObject=new JSONObject(json);
                            if(json!=null){
                                Message msg=Message.obtain();
                                msg.obj=jsonObject.optString("msg");
                                msg.what=3;
                                handler.sendMessage(msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                CustomDialog cd=new CustomDialog(PhoneCollectionActivity.this);
                cd.setTitle("温馨提示");
                cd.setMessage("是否确认支付？");
                cd.setPositiveButton("支付",new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HashMap map=new HashMap<String,String>();
                                map.put("phone",phone.getText().toString().trim());
                                map.put("verifycode",code.getText().toString().trim());
                                String json=requestHelp.requestPost(RequestHelp.CHECK,map);
                                Log.i("TAG",json+"");
                                JSONObject jsonObject=null;
                                try {
                                    jsonObject=new JSONObject(json);
                                    String msg=jsonObject.optString("msg");
                                    if(msg.equals("验证成功")){
                                        handler.sendEmptyMessage(2);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();

                    }
                });

                cd.setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    HashMap<String,String> hashMap2;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    hashMap2= SharedPreferencesUtil.getUserLoginInfo(PhoneCollectionActivity.this);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap map=new HashMap<String,String>();
                            map.put("customerId",SharedPreferencesUtil.getInfo(PhoneCollectionActivity.this,"customerId"));
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
                                    String verification=hashMap2.get("verification");
                                    // SharedPreferencesUtil.saveUserLoginInfo(HomeActivity.this, data.optString("customerId"), data.optString("phone"),data.optString("nickName") ,data.optString("sex"),phoneUrl,data.optString("birthday").replace("00:00:00",""),data.optString("address"),data.optString("inviteCode"), verification, data.optString("needUploadPostion"), data.optInt("frequency"),data.optString("useStatus"),data.optString("orderMsg"));

                                    SharedPreferencesUtil.saveUserLoginInfo2(PhoneCollectionActivity.this, data.optString("customerId"), data.optString("phone"),
                                            data.optString("nickName") ,data.optString("sex"),phoneUrl,data.optString("birthday").replace("00:00:00",""),
                                            data.optString("address"),data.optString("inviteCode"), verification, data.optString("needUploadPostion"),
                                            data.optInt("frequency"),data.optString("useStatus"),data.optString("orderMsg"),data.optString("isUploadLocation"),data.optString("ctccPhone"),data.optString("isExpired"));

                                    Intent intent=new Intent();
                                    intent.setAction("USER_STATUS");
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.putExtra("useStatus",data.optString("useStatus"));
                                    sendBroadcast(intent);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }).start();
                    break;
                case 2:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap map=new HashMap<String,String>();
                            map.put("customerId", SharedPreferencesUtil.getInfo(PhoneCollectionActivity.this,"customerId"));
                            map.put("phone", phone.getText().toString().trim());
                            map.put("busiTypeId", busiTypeId);
                            map.put("typeId",typeId);
                            map.put("busiTypeCode", busiTypeCode);
                            String result=requestHelp.requestPost(RequestHelp.GET_MY_PAY,map);
                            Log.i("TAG",result+"");
                            setResult(1001);

                            finish();
                        //    handler.sendEmptyMessage(1);
                        }
                    }).start();
                    break;
                case 3:
                    Toast.makeText(PhoneCollectionActivity.this,msg.obj+"",Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };



}
