package com.jiedu.project.lovefamily.activitydailog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.activity.BaseActivity;

public class PhoneStyleActivity extends BaseActivity implements View.OnClickListener {


    private RelativeLayout android_relay;
    private RelativeLayout iphone_relay;

    private String myphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_style_popupwindow);

        android_relay= (RelativeLayout) findViewById(R.id.android_relay);
        iphone_relay= (RelativeLayout) findViewById(R.id.iphone_relay);

        android_relay.setOnClickListener(this);
        iphone_relay.setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iphone_relay:
                myphone="苹果";
                Intent intent=new Intent();
                intent.putExtra("myphone",myphone);
                setResult(101,intent);
                finish();
                break;

            case R.id.android_relay:
                myphone="安卓";
                Intent intent2=new Intent();
                intent2.putExtra("myphone",myphone);
                setResult(101,intent2);
                finish();
                break;
        }

    }
}
