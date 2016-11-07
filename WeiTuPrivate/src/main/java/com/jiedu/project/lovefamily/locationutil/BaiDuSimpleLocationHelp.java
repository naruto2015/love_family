package com.jiedu.project.lovefamily.locationutil;

import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

public class BaiDuSimpleLocationHelp {
	private LocationClient locationClient;
	// 高精度
	private static final LocationMode HEIGHT = LocationMode.Hight_Accuracy;
	private static final String COOR_TYPE = "bd09ll";
	// 定位时间间隔，设置为0时仅定位一次
	private static int getLocationTime = 2000;
	private Vibrator mVibrator;
	private Handler handler;
	private Context context;
	/**
	 * 得到位置信息后发送信息的age1
	 */
	public static final int getLocationSucceed = 1000;
	public static final int getLocationFail = 1003;

	/**
	 * 调用此方法，启动定位
	 * 
	 * @param context
	 *            调用方法的上下文
	 * @param handler
	 *            处理结果的handler
	 */
	public void startGetLocation(Context context, Handler handler) {
		this.handler = handler;
		this.context = context;
		mVibrator = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);
		LocationClientOption option = new LocationClientOption();
		option.setTimeOut(3000);
		option.setLocationMode(HEIGHT);
		option.setCoorType(COOR_TYPE);
		option.setScanSpan(getLocationTime);
		// 设置是否需要位置信息
		option.setIsNeedAddress(true);
		// 设置是否使用gps
		option.setOpenGps(true);
		// 设置当gps生效时是否输出gps定位结果
		option.setLocationNotify(true);
		// 设置是否在定位结束后杀死定位服务，设置为true为不杀死
		option.setIgnoreKillProcess(true);
		locationClient = new LocationClient(context);
		locationClient.setLocOption(option);
//		LogUtil.e("是否打开gps"+option.isDisableCache());
		
		MyLocationListener listener = new MyLocationListener();
		locationClient.registerLocationListener(listener);
		locationClient.start();
		locationClient.requestLocation();
//		Log.e("0011","定位是否开始"+locationClient.isStarted());
		
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation arg0) {

 			Log.e("0011","定位：latitude=" + arg0.getLatitude() + "\tlongitude="
  					+ arg0.getLongitude());
			Message message = handler.obtainMessage();
			message.arg1 = getLocationSucceed;
			message.obj = arg0;
			message.sendToTarget();

			int i=locationClient.requestLocation();
			Log.e("0012","locationClient.requestLocation():"+i);

			locationClient.stop();

		}
		
		

	}
	/**
	 * 得到两个地点的距离
	 * @param latLng
	 * @param latLng2
	 * @return
	 */
	public double getDistance(LatLng latLng,LatLng latLng2){
		return DistanceUtil.getDistance(latLng, latLng2);
				
	}

	public void FailureLocation(){
		int i=locationClient.requestLocation();
		Log.e("0012","FailureLocation:"+i);
//		switch (i){
//			case 0:
//			case 1:
//			case 2:
//				sendFailureLocation();
//				Log.e("0012","sendFailureLocation");
//				break;
//		}
	}

	/**
	 * 当用户禁止获取位置信息时，发送默认地址
	 */
public void sendFailureLocation(){
	BDLocation location=new BDLocation();
	location.setLatitude(0);
	location.setLongitude(0);
	Message message = handler.obtainMessage();
	message.arg1 = getLocationSucceed;
	message.obj = location;
	message.sendToTarget();
	locationClient.stop();
}
	/**
	 * 重新开启定位
	 */
	public void restartGetLocation(){
		locationClient.start();
		locationClient.requestLocation();
	}

	public Overlay drawCircle(LatLng conter,LatLng latLng,BaiduMap baiduMap,Overlay overlay){
		if(null!=overlay){
			overlay.remove();
		}

		OverlayOptions ooCircle = new CircleOptions().fillColor(0x000000FF)
				.center(conter).stroke(new Stroke(5, 0xAA0000FF))
				.radius((int)getDistance(conter, latLng));
		return baiduMap.addOverlay(ooCircle);
	}
	public Overlay drawCircle(LatLng conter,int  radius,BaiduMap baiduMap,Overlay overlay){
		if(null!=overlay){
			overlay.remove();
		}
		OverlayOptions ooCircle = new CircleOptions().fillColor(0x000D63BC)
				.center(conter).stroke(new Stroke(5, 0xAA0D63BC))
				.radius(radius).fillColor(0xAA0D63BC);
		return baiduMap.addOverlay(ooCircle);
	}
	public Overlay drawYellowCircle(LatLng conter,int  radius,BaiduMap baiduMap,Overlay overlay){
		if(null!=overlay){
			overlay.remove();
		}
		OverlayOptions ooCircle = new CircleOptions().fillColor(0x00FFAF08)
				.center(conter).stroke(new Stroke(5, 0xaaFFAF08))
				.radius(radius).fillColor(0x00FFAF08);
		return baiduMap.addOverlay(ooCircle);
	}
}
