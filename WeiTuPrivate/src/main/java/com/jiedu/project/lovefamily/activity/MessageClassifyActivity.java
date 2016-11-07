package com.jiedu.project.lovefamily.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.net.RequestHelp;

/**
 * Created by Administrator on 2016/10/19.
 */
public class MessageClassifyActivity extends Activity implements View.OnClickListener{
    private RelativeLayout rl_xtxx,rl_cyxx;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messageclassify);
        rl_xtxx= (RelativeLayout) findViewById(R.id.rl_xtxx);
        rl_cyxx= (RelativeLayout) findViewById(R.id.rl_cyxx);
        back= (ImageView) findViewById(R.id.back);
        rl_xtxx.setOnClickListener(this);
        rl_cyxx.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_xtxx:
                break;
            case  R.id.rl_cyxx:
                startActivity(new Intent(MessageClassifyActivity.this,MessageActivity.class));
                break;
            case R.id.back:
                finish();
                break;
        }
    }
}
