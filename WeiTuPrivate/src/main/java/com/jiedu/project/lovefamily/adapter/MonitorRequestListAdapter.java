package com.jiedu.project.lovefamily.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.locationutil.BaiDuSimpleLocationHelp;
import com.jiedu.project.lovefamily.message.MessageInfoUtil;
import com.jiedu.project.lovefamily.mode.Monitor;
import com.jiedu.project.lovefamily.net.RequestHelp;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/3/31.
 */
public class MonitorRequestListAdapter extends BaseAdapter{

    Context context;
    ArrayList<Monitor>list;
    RequestHelp requestHelp;
    Handler handler;
    BaiDuSimpleLocationHelp baiDuSimpleLocationHelp;
    public MonitorRequestListAdapter(Context context,ArrayList<Monitor>list,Handler handler){
         this.context=context;
         this.list=list;
        this.handler=handler;
        baiDuSimpleLocationHelp=new BaiDuSimpleLocationHelp();
        requestHelp=new RequestHelp();

    }
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final  int index=position;
        Mode mode=null;
        if(view==null){
            mode=new Mode();
            view= LayoutInflater.from(context).inflate(R.layout.itme_monitor_request,null);
            mode.phone= (TextView) view.findViewById(R.id.name);
            mode.submit=(Button)view.findViewById(R.id.submit);
            mode.cancel=(Button)view.findViewById(R.id.cancel);
            view.setTag(mode);
        }else{
            mode= (Mode) view.getTag();
        }
        mode.phone.setText(list.get(position).getPhone());
        mode.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baiDuSimpleLocationHelp.startGetLocation(context,handler);
            new Thread(new Runnable() {
                @Override
                public void run() {
                  String result  =requestHelp.requestPost(RequestHelp.CONFIRM_MONITOR_REQUEST + list.get(index).getId(), new HashMap<String, Object>());
//                    Log.e("0011","同意监控请求结果===="+result);

                    Message message=handler.obtainMessage();
                    message.what= MessageInfoUtil.DELETE;
                    message.arg1=index;
//                    message.obj=result;
                    message.sendToTarget();
                }
            }).start();
            }
        });
        mode.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String result  =requestHelp.requestPost(RequestHelp.REFUSE_MONITOR_REQUEST + list.get(index).getId(), new HashMap<String, Object>());
//                        Log.e("0011","拒绝监控请求结果===="+result);
                        Message message=handler.obtainMessage();
                        message.what= MessageInfoUtil.DELETE;
                        message.arg1=index;
                        message.obj=result;
                        message.sendToTarget();
                    }
                }).start();
            }
        });

        return view;
    }
    class Mode{
        TextView phone;
        Button submit;
        Button cancel;
    }
}
