package com.jiedu.project.lovefamily.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;

import java.util.HashMap;
import java.util.Map;

public class MessageContentActivity extends BaseActivity {

    private ImageView back;
    private TextView message_content;
    String messageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_content);
        initView();
        initData();

    }

    private void initData() {
        final RequestHelp requestHelp=new RequestHelp();

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map=new HashMap<String,String>();
                map.put("messageId",messageId);
                String json=requestHelp.requestPost(RequestHelp.READED,map);
                Log.e("Tag",json);
            }
        }).start();
    }

    private void initView() {

        String msg=getIntent().getStringExtra("content");
        messageId=getIntent().getStringExtra("messageId");
        back= (ImageView) findViewById(R.id.back);
        message_content= (TextView) findViewById(R.id.message_content);
        message_content.setText("  "+msg);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setResult(1);
                finish();
            }
        });
    }
}
