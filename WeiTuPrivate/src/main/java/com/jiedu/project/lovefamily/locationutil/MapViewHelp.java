package com.jiedu.project.lovefamily.locationutil;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.Point;
import com.jiedu.project.lovefamily.R;

public class MapViewHelp {
/**
 * 添加图片覆盖物
 * @param lat
 * @param ic 图标
 * @param lon
 * @param baiduMap
 */
	public void setPointIcon(double lat,double lon,int ic,BaiduMap baiduMap){
		LatLng latLng=new LatLng(lat, lon);
		BitmapDescriptor bitmapDescriptor=BitmapDescriptorFactory.fromResource(ic);
	   //设置标记
		MarkerOptions markerOptions=new MarkerOptions();
		markerOptions.position(latLng);
		markerOptions.icon(bitmapDescriptor);
		markerOptions.flat(true);
		markerOptions.draggable(false);
		baiduMap.addOverlay(markerOptions);
	}
	/**
	 * 添加文字覆盖物
	 * @param lat
	 * @param lon
	 * @param baiduMap
	 * @param text
	 */
	public void setPointText(double lat,double lon,BaiduMap baiduMap,String text){
		LatLng latLng=new LatLng(lat, lon);
		//设置文字
		TextOptions  textOptions=new TextOptions();
		textOptions.fontColor(Color.RED);
		textOptions.position(latLng);
		if(!TextUtils.isEmpty(text)){
			textOptions.text(text);
		}
		baiduMap.addOverlay(textOptions);
	}
	/**
	 * 重新设置地图状态
	 * @param lat
	 * @param lon
	 * @param baiduMap
	 */
	public void refresh(double lat,double lon,BaiduMap baiduMap){
		LatLng latLng=new LatLng(lat, lon);
		MapStatus mapStatus=new MapStatus.Builder().target(latLng).build();
		MapStatusUpdate mapStatusUpdate=MapStatusUpdateFactory.newMapStatus(mapStatus);
		baiduMap.setMapStatus(mapStatusUpdate);
	}
	/**
	 * 设置标记，并设置为中心
	 */
	public void setMarkPoint(double lat,double lon,String text,BaiduMap baiduMap){
		setPointIcon(lat, lon, R.mipmap.dituicon ,baiduMap);
		setPointText(lat, lon, baiduMap, text);
		refresh(lat, lon, baiduMap);
	}
	
	public  void setMarkLine(List<LatLng>list,List<Integer> colors,BaiduMap baiduMap ){
		List<Integer>color=new ArrayList<Integer>();
		color.add(Color.GREEN);
		color.add(Color.GREEN);
		color.add(Color.GREEN);
		OverlayOptions overlayOptions=new PolylineOptions().width(5).points(list).colorsValues(color);
		baiduMap.addOverlay(overlayOptions);
	}

	/**
	 * 设置地图比例尺
	 * @param ll   中心点
	 * @param baiduMap
	 * @param size
	 */
	public void setScale(LatLng ll,BaiduMap baiduMap,int size ){
		float f = baiduMap.getMaxZoomLevel();//19.0 最小比例尺
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, f-size);
		baiduMap.setMapStatus(u);
	}


	}
	

