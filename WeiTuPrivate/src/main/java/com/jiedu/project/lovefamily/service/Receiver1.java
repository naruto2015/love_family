package com.jiedu.project.lovefamily.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/4/5.
 */
public class Receiver1 extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            Intent startServiceIntent = new Intent(context, Service1.class);
            startServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(startServiceIntent);
            //Toast.makeText(context,"开机广播",Toast.LENGTH_SHORT).show();

        }
       if(Intent.ACTION_USER_PRESENT.equals(intent.getAction())){

            Intent startServiceIntent = new Intent(context, Service1.class);
            //startServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(startServiceIntent);
            //Toast.makeText(context,"解锁广播",Toast.LENGTH_SHORT).show();

        }
    }
}
