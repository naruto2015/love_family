package com.jiedu.project.lovefamily.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.myview.MyBaiduMapIcon;
import com.jiedu.project.lovefamily.stringutil.IntentString;
import com.jiedu.project.lovefamily.locationutil.BaiDuSimpleLocationHelp;
import com.jiedu.project.lovefamily.locationutil.MapViewHelp;
import com.jiedu.project.lovefamily.myview.SafeView;
import com.jiedu.project.lovefamily.net.RequestHelp;

import java.util.ArrayList;


public class SafeAreaActivity extends BaseActivity implements BaiduMap.OnMapClickListener, View.OnClickListener, OnGetPoiSearchResultListener, OnGetGeoCoderResultListener, SafeView.OnProgressChangeListener {

    MapView mapView;
    BaiduMap baiduMap;
    EditText address;
    ImageView search;
    ImageView safeBack;
    PoiSearch mSearch;
    GeoCoder geoCoderSearch;
    MapViewHelp mapViewHelp;
    Point point;
    Projection projection;
    BaiDuSimpleLocationHelp simpleLocationHelp;
    Overlay circleOverlay;
    boolean haveCircleConter=false;
    LatLng circleConter;
    ImageView save;
    SafeView safeView;
    Button down;
    Button up;
    RelativeLayout layout;
    String addressStr;
    int radius;
    ArrayList<String>list=new ArrayList<String>();
    RequestHelp requestHelp;
    Intent intent;
    EditText safe_title;
    String id;
    String refId;
    private MyBaiduMapIcon safe_mybaiduIcon;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_safe_area);

        init();


    }
    void init(){
        requestHelp=new RequestHelp();
        intent=getIntent();
        point=new Point();
        simpleLocationHelp=new BaiDuSimpleLocationHelp();
        mSearch=PoiSearch.newInstance();
        geoCoderSearch=GeoCoder.newInstance();
        geoCoderSearch.setOnGetGeoCodeResultListener(this);
        mapViewHelp=new MapViewHelp();
        mapView=(MapView)findViewById(R.id.safe_area_map);
        safeBack=(ImageView)findViewById(R.id.safe_back);
        down=(Button)findViewById(R.id.down);
        up=(Button)findViewById(R.id.up);
        safe_title=(EditText)findViewById(R.id.safe_title);
        layout=(RelativeLayout)findViewById(R.id.radio_space);
        mapView.showScaleControl(false);
        mapView.showZoomControls(false);
        mapView.removeViewAt(1);
        safe_mybaiduIcon= (MyBaiduMapIcon) findViewById(R.id.safe_mybaiduIcon);
        safe_mybaiduIcon.setMapView(mapView);

        address=(EditText)findViewById(R.id.address_edit);
        search=(ImageView)findViewById(R.id.search);
        save= (ImageView) findViewById(R.id.save_set);
        safeView=(SafeView)findViewById(R.id.safe_view);
        safeBack.setOnClickListener(this);
        list.add("0m");
        list.add("100m");
        list.add("200m");
        list.add("500m");
        list.add("1km");
        list.add("2km");
        list.add("5km");
        safeView.setList(list);
        safeView.setOnProgressChangeListener(this);
        safeView.setProgressBackground(Color.argb(255, 0, 183, 239), Color.argb(255, 217, 217, 217));
        baiduMap=mapView.getMap();

        baiduMap.setOnMapClickListener(this);

        search.setOnClickListener(this);
        mSearch.setOnGetPoiSearchResultListener(this);
        save.setOnClickListener(this);
        down.setOnClickListener(this);
        up.setOnClickListener(this);
        initMapAndView();
    }
    //初始化地图，画中心点，画圆，设置自定义进度条进度，设置标题
       private void initMapAndView(){

            LatLng latLng=new LatLng(intent.getDoubleExtra(IntentString.LAT,0),intent.getDoubleExtra(IntentString.LON,0));
           haveCircleConter=true;
           circleConter=latLng;
           baiduMap.clear();
           mapViewHelp.setMarkPoint(latLng.latitude, latLng.longitude, "安全中心", baiduMap);
           mapViewHelp.setScale(latLng, baiduMap, 4);
           circleOverlay=   simpleLocationHelp.drawCircle(circleConter, intent.getIntExtra(IntentString.RADIUS, 0), baiduMap, circleOverlay);
           safeView.setProgress(radiusToProgress(intent.getIntExtra(IntentString.RADIUS, 0)));
           safe_title.setText(intent.getStringExtra(IntentString.TITLE));

        }
    @Override
    public void onMapClick(LatLng latLng) {
        baiduMap.clear();
        mapViewHelp.setMarkPoint(latLng.latitude, latLng.longitude, "安全中心", baiduMap);
        mapViewHelp.setScale(latLng, baiduMap, 4);
        haveCircleConter=true;
        circleConter=latLng;
        geoCoderSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
//        if(null!=circleConter&&haveCircleConter){
//
//            circleOverlay=  simpleLocationHelp.drawCircle(circleConter,latLng,baiduMap,circleOverlay);
//        }
        circleOverlay=   simpleLocationHelp.drawCircle(circleConter,getRadius(safeView.getProgress()),baiduMap,circleOverlay);

    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        baiduMap.clear();
        mapViewHelp.setMarkPoint(mapPoi.getPosition().latitude, mapPoi.getPosition().longitude, "安全中心", baiduMap);
        haveCircleConter=true;
        circleConter= mapPoi.getPosition();
        geoCoderSearch.reverseGeoCode(new ReverseGeoCodeOption().location(circleConter));
//        if(null!=circleConter&&haveCircleConter){
//
//            circleOverlay=  simpleLocationHelp.drawCircle(circleConter, mapPoi.getPosition(),baiduMap,circleOverlay);
//        }
        circleOverlay=   simpleLocationHelp.drawCircle(circleConter,getRadius(safeView.getProgress()),baiduMap,circleOverlay);
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_set:
                if(safe_title.getText().toString().trim()==null || safe_title.getText().toString().trim().equals("")){
                    Toast.makeText(SafeAreaActivity.this,"标题不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        radius = intent.getIntExtra(IntentString.RADIUS,0);
                        id = intent.getStringExtra(IntentString.ID);
                        refId=intent.getStringExtra(intent.getStringExtra(IntentString.REF_ID));
//                        Log.e("0011", "初始化安全范围id=" + id + "\trefId=" + refId + "\tradius=" +  getRadius(safeView.getProgress()) + "intent是否为空" + (intent == null));
                        requestHelp.addSafeRange(intent.getStringExtra(IntentString.ID), intent.getStringExtra(IntentString.REF_ID), safe_title.getText().toString(), circleConter.latitude, circleConter.longitude, getRadius(safeView.getProgress()), addressStr);
                    }
                }).start();
                finish();;
                break;
            case R.id.search:
                mSearch.searchInCity(new PoiCitySearchOption().city("南京").keyword(address.getText().toString()));
                break;
            case R.id.safe_back:
                finish();
                break;
            case R.id.down:
                InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(safe_title.getWindowToken(), 0);
                layout.setVisibility(View.GONE);
                up.setVisibility(View.VISIBLE);
                break;
            case R.id.up:
                layout.setVisibility(View.VISIBLE);
                up.setVisibility(View.GONE);
                break;
        }
    }



    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null || poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(SafeAreaActivity.this,"没有结果",Toast.LENGTH_LONG).show();
        }else {

            LatLng latLng=   poiResult.getAllPoi().get(0).location;
            try {
                baiduMap.clear();
                mapViewHelp.setMarkPoint(latLng.latitude,latLng.longitude,"目标地点",baiduMap);
                haveCircleConter=true;
                circleConter=latLng;

            }catch (Exception e){

            }

        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
     LatLng latLng=   poiDetailResult.getLocation();

        Toast.makeText(SafeAreaActivity.this,"查询到的位置坐标 lat=  "+latLng.latitude+"\tlon"+latLng.longitude,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSearch.destroy();
        geoCoderSearch.destroy();
        mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(SafeAreaActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        addressStr=result.getAddress();

    }

    @Override
    public void onChange(int progress) {
//        Log.e("0011", "当前进度" + progress);
        if(haveCircleConter){
            circleOverlay=   simpleLocationHelp.drawCircle(circleConter,getRadius(progress),baiduMap,circleOverlay);
        }

    }

    int getRadius(int i){

        switch (i){
            case 0:
                radius=0;
                break;
            case 1:
                radius=100;
                break;
            case 2:
                radius=200;
                break;
            case 3:
                radius=500;
                break;
            case 4:
                radius=1000;
                break;
            case 5:
                radius=2000;
                break;
            case 6:
                radius=5000;
                break;
        }
        return radius;
    }
    int radiusToProgress(int r){
        switch (r){
            case 0:
                r=0;
                break;
            case 100:
                r=1;
                break;
            case 200:
                r=2;
                break;
            case 500:
                r=3;
                break;
            case 1000:
                r=4;
                break;
            case 2000:
                r=5;
                break;
            case 5000:
                r=6;
                break;
        }
return  r;
    }
}
