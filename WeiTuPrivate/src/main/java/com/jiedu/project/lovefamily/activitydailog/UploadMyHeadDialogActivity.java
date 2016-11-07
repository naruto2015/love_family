package com.jiedu.project.lovefamily.activitydailog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.activity.BaseActivity;

public class UploadMyHeadDialogActivity extends BaseActivity implements View.OnClickListener {

    private Button choose;
    private Button take;
    private  Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_choose_popupwindow);

        choose = (Button) findViewById(R.id.choose_photo_sd);
        take =    (Button) findViewById(R.id.take_photo);
        cancel=(Button) findViewById(R.id.cancel);

        choose.setOnClickListener(this);
        take.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_photo_sd:
                setResult(102);
                finish();
                break;
            case R.id.take_photo:
                 setResult(103);
                finish();
                break;
            case R.id.cancel:
                finish();
                break;

        }
    }
}
