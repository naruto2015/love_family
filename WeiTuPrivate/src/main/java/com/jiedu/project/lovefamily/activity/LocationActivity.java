package com.jiedu.project.lovefamily.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroupOverlay;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.locationutil.BaiDuSimpleLocationHelp;
import com.jiedu.project.lovefamily.locationutil.MapViewHelp;
import com.jiedu.project.lovefamily.mode.User;
import com.jiedu.project.lovefamily.myview.MyBaiduMapIcon;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.stringutil.IntentString;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationActivity extends BaseActivity implements BaiduMap.OnMarkerClickListener, View.OnClickListener, OnGetGeoCoderResultListener {
    BaiDuSimpleLocationHelp simpleLocationHelp;
    Overlay circleOverlay;
    MapView mapView;
    BaiduMap baiduMap;
    GeoCoder geoCoderSearch;
    MapViewHelp mapViewHelp;
    ImageView back;
    InfoWindow infoWindow;
    Intent intent;
    LatLng latLng;
    TextView address,location_name;
    ImageView headImg;
    private ImageLoader loader;
    TextView title;

    TextView tv_time;

    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM-dd HH:mm");
    String time;
    User user=null;
    private RequestHelp requestHelp;
    private MyBaiduMapIcon icon;
    private String uploadTime,username,location;
    private Button safe_area_manager;

    Date date=new Date();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestHelp=new RequestHelp();

        setContentView(R.layout.activity_location);
        init();

    }


    class MyTask extends AsyncTask<Void,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return null;
        }



    }



    private void init() {
        user= (User) getIntent().getSerializableExtra("user");
        uploadTime=getIntent().getStringExtra("uploadTime");
        username=getIntent().getStringExtra(IntentString.NICK_NAME);
        geoCoderSearch=GeoCoder.newInstance();
        geoCoderSearch.setOnGetGeoCodeResultListener(this);

        loader=ImageLoader.getInstance().getInstance();
        intent=getIntent();
        latLng=new LatLng(intent.getDoubleExtra("lat",0),intent.getDoubleExtra("lon",0));
        geoCoderSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));

        mapView = (MapView) findViewById(R.id.map_view);
        back= (ImageView) findViewById(R.id.location_back);
        //title=(TextView)findViewById(R.id.title_location);
        safe_area_manager= (Button) findViewById(R.id.safe_area_manager);

        /**
         * 自定义百度地图放大缩小按钮
         */
        //获取缩放控件
        icon= (MyBaiduMapIcon) findViewById(R.id.mybaiduicon);
        icon.setMapView(mapView);//设置百度地图控件

      //  title.setText(intent.getStringExtra(IntentString.NICK_NAME));
        back.setOnClickListener(this);
        safe_area_manager.setOnClickListener(this);
        baiduMap=mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        mapView.showZoomControls(false);//隐藏缩放控件
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(19f));//设置地图状态
        baiduMap.setOnMarkerClickListener(this);
        simpleLocationHelp=new BaiDuSimpleLocationHelp();
        circleOverlay=   simpleLocationHelp.drawYellowCircle(latLng, 100, baiduMap, circleOverlay);



        mapViewHelp=new MapViewHelp();
//        Log.e("0011","设置坐标lat="+latLng.latitude+"\tlon"+latLng.longitude);
        mapViewHelp.setMarkPoint(latLng.latitude, latLng.longitude, "", baiduMap);
        mapViewHelp.setScale(latLng, baiduMap, 4);
        time=simpleDateFormat.format(new Date().getTime());

       // showWindow(latLng);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        geoCoderSearch.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        showWindow(marker.getPosition());
        return false;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.location_back:
                finish();
                break;
            case R.id.safe_area_manager:
                Intent safeIntent=new Intent(this, SafeListActivity.class);
                safeIntent.putExtra(IntentString.REF_ID,user.getId());
                safeIntent.putExtra(IntentString.CUSTOMER_ID,user.getMonitoredId());
                safeIntent.putExtra("address",address.getText().toString());
                startActivity(safeIntent);

                break;
        }
    }


    void showWindow(LatLng latLng){
        if(null==infoWindow){
            View view= getLayoutInflater().inflate(R.layout.location_window,null);
            address= (TextView) view.findViewById(R.id.address);
            headImg= (ImageView) view.findViewById(R.id.image);
            tv_time= (TextView) view.findViewById(R.id.tv_time);
            location_name= (TextView) view.findViewById(R.id.location_name);
            if(TextUtils.isEmpty(intent.getStringExtra(IntentString.PHONO_URL))){
                loader.displayImage("drawable://" + R.drawable.default_head,headImg);
            }else{
                loader.displayImage(intent.getStringExtra(IntentString.PHONO_URL),headImg);
            }
            tv_time.setText(uploadTime);
            location_name.setText(username);
            infoWindow=new InfoWindow(view,latLng,-100);
            baiduMap.showInfoWindow(infoWindow);
        }else {
            baiduMap.hideInfoWindow();
            infoWindow=null;
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(LocationActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if(null!=address){
            address.setText(result.getAddress());
        }


    }
}
