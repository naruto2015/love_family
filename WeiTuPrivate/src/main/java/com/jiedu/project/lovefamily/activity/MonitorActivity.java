package com.jiedu.project.lovefamily.activity;

import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.fragment.CodeFragment;

public class MonitorActivity extends FragmentActivity {

   FragmentTransaction transaction;
    FragmentManager fragmentManager;
    CodeFragment codeFragment;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT){
            Window window=getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0).setFitsSystemWindows(true);

        }
        codeFragment=new CodeFragment();
        fragmentManager=getSupportFragmentManager();
        transaction=fragmentManager.beginTransaction();
        transaction.add(R.id.monitor_list,codeFragment);
        transaction.commit();

    }
}
