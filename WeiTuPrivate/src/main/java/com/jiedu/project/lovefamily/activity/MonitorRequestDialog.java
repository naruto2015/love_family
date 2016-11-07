package com.jiedu.project.lovefamily.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import com.baidu.location.BDLocation;
import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.adapter.MonitorRequestListAdapter;
import com.jiedu.project.lovefamily.json.JsonHelp;
import com.jiedu.project.lovefamily.locationutil.BaiDuSimpleLocationHelp;
import com.jiedu.project.lovefamily.message.MessageInfoUtil;
import com.jiedu.project.lovefamily.mode.Monitor;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;
import com.jiedu.project.lovefamily.stringutil.IntentString;

import java.util.ArrayList;

public class MonitorRequestDialog extends BaseActivity implements View.OnClickListener {

    ListView listView;
    ArrayList<Monitor>list=new ArrayList<Monitor>();
    MonitorRequestListAdapter adapter;
    Intent intent;
    JsonHelp jsonHelp;
    ImageView back;
    BDLocation bdLocation;
    RequestHelp requestHelp;
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_monitor_request_dialog);
        listView=(ListView)findViewById(R.id.monitor_request_list);
        back=(ImageView)findViewById(R.id.back);
        requestHelp=new RequestHelp();
        intent=getIntent();
        jsonHelp=new JsonHelp();
        adapter=new MonitorRequestListAdapter(this,jsonHelp.drealMonitorJson(intent.getStringExtra(IntentString.JSON),list),handler);
        listView.setAdapter(adapter);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        finish();;
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MessageInfoUtil.DELETE:
                    list.remove(msg.arg1);
                    adapter.notifyDataSetChanged();
                    if(list.size()==0){
                        finish();
                    }
//                    if(){
//
//                    }
                    break;
            }

            switch (msg.arg1){
                case BaiDuSimpleLocationHelp.getLocationSucceed:
                    bdLocation= (BDLocation) msg.obj;
                    new Thread(new Runnable() {
                        public void run() {
                          String result=  requestHelp.upLocation(SharedPreferencesUtil.getInfo(getApplicationContext(), "customerId"),bdLocation.getLatitude(),bdLocation.getLongitude());
//                            Log.e("0011","同意监控请求时的定位上传请求结果"+result);
                        }
                    }).start();
                    break;
            }
        }
    };

}
