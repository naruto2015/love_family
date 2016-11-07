package com.jiedu.project.lovefamily.activity;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

public class MyScanActivity extends BaseActivity {


    private ImageView back;
    Bitmap bitmap;
    private ImageView img_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_scan);

        back= (ImageView) findViewById(R.id.back);
        img_code= (ImageView) findViewById(R.id.img_code);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        String phone= SharedPreferencesUtil.getInfo(MyScanActivity.this,"phone");
        bitmap= EncodingUtils.createQRCode(phone,500,500,null);

        img_code.setImageBitmap(bitmap);


    }

}
