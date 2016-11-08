package com.jiedu.project.lovefamily.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.utils.SystemBarTintManager;
import com.jude.swipbackhelper.SwipeBackHelper;

import rtc.sdk.common.NetWorkUtil;

/**
 * Created by Administrator on 2016/4/5.
 */
public class BaseActivity  extends Activity{

    public  String LOGTAG = getClass().getName().substring(getClass().getName().lastIndexOf(".") + 1);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(true)
                .setSwipeSensitivity(0.5f)
                .setSwipeRelateEnable(true)
                .setSwipeRelateOffset(800);

//        SwipeBackHelper.getCurrentPage(this)//获取当前页面
//                .setSwipeBackEnable(true)//设置是否可滑动
//                .setSwipeEdge(200)//可滑动的范围。px。200表示为左边200px的屏幕
//                .setSwipeEdgePercent(0.2f)//可滑动的范围。百分比。0.2表示为左边20%的屏幕
//                .setSwipeSensitivity(0.5f)//对横向滑动手势的敏感程度。0为迟钝 1为敏感
//                .setScrimColor(Color.BLUE)//底层阴影颜色
//                .setClosePercent(0.8f)//触发关闭Activity百分比
//                .setSwipeRelateEnable(false)//是否与下一级activity联动。默认是
//                .setSwipeRelateOffset(500)//activity联动时的偏移量。默认500px。
//                .addListener(new SwipeListener() {//滑动监听
//
//                    @Override
//                    public void onScroll(float percent, int px) {//滑动的百分比与距离
//                    }
//
//                    @Override
//                    public void onEdgeTouch() {//当开始滑动
//                    }
//
//                    @Override
//                    public void onScrollToClose() {//当滑动关闭
//                    }
//                });

        initWindow();
    }

    private SystemBarTintManager tintManager;
    private void initWindow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.online));
            tintManager.setStatusBarTintEnabled(true);
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT){
            Window window=getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0).setFitsSystemWindows(true);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SwipeBackHelper.onDestroy(this);
    }


    /**
     * 隐藏软键盘
     */
    protected void goneKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView()
                    .getWindowToken(), 0);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
