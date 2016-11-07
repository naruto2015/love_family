package com.jiedu.project.lovefamily.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.adapter.SafeRangeAdapter;
import com.jiedu.project.lovefamily.locationutil.BaiDuSimpleLocationHelp;
import com.jiedu.project.lovefamily.mode.User;
import com.jiedu.project.lovefamily.stringutil.IntentString;
import com.jiedu.project.lovefamily.json.JsonHelp;
import com.jiedu.project.lovefamily.message.MessageInfoUtil;
import com.jiedu.project.lovefamily.mode.SafeRange;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;

import java.util.ArrayList;

public class SafeListActivity extends BaseActivity implements View.OnClickListener {

    ImageView back;
    Button add;
    ListView listView;
    Intent intent;
    RequestHelp requestHelp;
    JsonHelp jsonHelp;
    SafeRangeAdapter adapter;
    int index;
    TextView prompt;
    ArrayList<SafeRange>list=new ArrayList<SafeRange>();

    BaiDuSimpleLocationHelp baiDuSimpleLocationHelp;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_list);
        baiDuSimpleLocationHelp=new BaiDuSimpleLocationHelp();
        baiDuSimpleLocationHelp.startGetLocation(this, handler2);

        //initLocation();

        initView();
        initUtil();

    }

    BDLocation location;
    private Handler handler2=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            location= (BDLocation) msg.obj;
            mLongtitude=location.getLongitude();
            mLatitude=location.getLatitude();
        }
    };


    private MyLocationConfiguration.LocationMode mLocationMode;
    // 定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;

    private double mLatitude=0;
    private double mLongtitude=0;
    private User  user=null;
    private void initLocation(){

        SDKInitializer.initialize(getApplicationContext());
        mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);


        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000);            //每隔一秒钟进行一次请求
        mLocationClient.setLocOption(option);
    }


    private class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation location) {
            MyLocationData data = new MyLocationData.Builder()//
                    //.direction(mCurrentX)//
                    .accuracy(location.getRadius())//
                    .latitude(location.getLatitude())//
                    .longitude(location.getLongitude())//
                    .build();


            mLatitude=location.getLatitude();
            mLongtitude=location.getLongitude();
        }
    }


    void initView(){
        intent=getIntent();
        user= (User) getIntent().getSerializableExtra("user");

        back= (ImageView) findViewById(R.id.back);
        add= (Button) findViewById(R.id.add_safearea);
        listView= (ListView) findViewById(R.id.listview_area);
        prompt=(TextView)findViewById(R.id.prompt);
        add.setOnClickListener(this);
        back.setOnClickListener(this);
    }
void initUtil(){
    requestHelp=new RequestHelp();
    jsonHelp=new JsonHelp();
}
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.add_safearea:
                Intent safe=new Intent(this,SafeAreaActivity.class);
                safe.putExtra(IntentString.ID,"");
                safe.putExtra(IntentString.REF_ID,intent.getStringExtra(IntentString.REF_ID));


                //设置默认中心点
//                SharedPreferencesUtil.getInfo(getApplicationContext(), "customerId",location.getLatitude(),location.getLongitude());
//                SharedPreferences sp=getSharedPreferences("location", Activity.MODE_PRIVATE);
              /*  safe.putExtra(IntentString.LAT,sp.getString("getLatitude", 31.973318+""));
                safe.putExtra(IntentString.LON,sp.getString("getLongitude", 118.75134+""));*/
                if (mLatitude==0){
                    mLatitude=31.973318;
                }

                if(mLongtitude==0){
                   mLongtitude=118.75134;
                }
                safe.putExtra(IntentString.LAT,mLatitude);
                safe.putExtra(IntentString.LON,mLongtitude);
                startActivity(safe);
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        startThread();
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MessageInfoUtil.SAFE_RANGE_LIST:
                    Log.e("0011", "查询安全范围结果"+list.size());
                    if(list.size()==0){
                        prompt.setVisibility(View.VISIBLE);
                    }else{
                        prompt.setVisibility(View.GONE);
                    }
                    adapter=new SafeRangeAdapter(SafeListActivity.this,list,handler);
                    listView.setAdapter(adapter);
                    break;
                case MessageInfoUtil.SAFE_RANGE_LIST_FAIL:
                    prompt.setVisibility(View.VISIBLE);
                    break;
                case MessageInfoUtil.DELETE:
                    index=msg.arg1;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                       String result= requestHelp.deleteSafeRange(list.get(index).id);
                        jsonHelp.dealDeleteSafeRange(result,handler);
                    }
                }).start();
                    break;
                case MessageInfoUtil.DELETE_SAFE_RANGE:
                    startThread();
                    break;
                case MessageInfoUtil.DELETE_SAFE_RANGE_FAIL:
                    break;
                case MessageInfoUtil.TO_SET_SAFE_RANGE_ACTIVITY:
                    Intent safe=new Intent(SafeListActivity.this,SafeAreaActivity.class);

                    safe.putExtra(IntentString.ID,list.get(msg.arg1).id);
//                    Log.e("0011","需要修改的安全范围id="+list.get(msg.arg1).id);
                    safe.putExtra(IntentString.REF_ID,intent.getStringExtra(IntentString.REF_ID));
                    safe.putExtra(IntentString.TITLE,list.get(msg.arg1).title);
                    safe.putExtra(IntentString.LAT,list.get(msg.arg1).lat);
                    safe.putExtra(IntentString.LON,list.get(msg.arg1).lon);
                    safe.putExtra(IntentString.RADIUS,list.get(msg.arg1).radius);

                    startActivity(safe);


                    break;
            }
        }
    };

    void startThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String result=  requestHelp.getSafeRangeList(SharedPreferencesUtil.getInfo(SafeListActivity.this, "customerId"), intent.getStringExtra(IntentString.CUSTOMER_ID));
                jsonHelp.dealGetSafeRangeList(result,handler,list);
            }
        }).start();
    }
}
