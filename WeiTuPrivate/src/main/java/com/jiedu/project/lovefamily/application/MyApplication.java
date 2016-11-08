package com.jiedu.project.lovefamily.application;

import com.baidu.mapapi.SDKInitializer;
import com.jiedu.project.lovefamily.HBaseApp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.socialize.PlatformConfig;


/**
 * Created by Administrator on 2016/3/2.
 */
public class MyApplication extends HBaseApp {
    @Override
    public void onCreate() {
        super.onCreate();
//        initSdkLog();
//        init();
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);

        //PlatformConfig.setWeixin("wx530e106581e3f050", "95afd6d101e29cac8b76161dc0aa0d58");
        //PlatformConfig.setQQZone("1105261796", "BXsizUXA8hMmkNOw");
        PlatformConfig.setWeixin("wx1db1c4430257f8c9", "46ae98c644feeae0074a98a3744cec42");
        PlatformConfig.setQQZone("1105290281", "UPTNS17wXd6CjwY2");
        SDKInitializer.initialize(getApplicationContext());

        //queues= Volley.newRequestQueue(getApplicationContext());

    }

    private static  MyApplication mApplication;
    public  static MyApplication getInstance(){

        return mApplication;
    }


}









