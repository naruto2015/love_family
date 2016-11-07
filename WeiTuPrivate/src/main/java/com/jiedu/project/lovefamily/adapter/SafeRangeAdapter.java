package com.jiedu.project.lovefamily.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.message.MessageInfoUtil;
import com.jiedu.project.lovefamily.mode.SafeRange;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/24.
 */
public class SafeRangeAdapter extends BaseAdapter{
    Context context;
    ArrayList<SafeRange>list;
    Handler handler;
    public SafeRangeAdapter(Context context,ArrayList<SafeRange>list,Handler handler){
        this.context=context;
        this.list=list;
        this.handler=handler;
    }

    @Override
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final int index=position;
        Mode mode=null;
        if(convertView==null){
            mode=new Mode();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_invitation_code,null);
            mode.title= (TextView) convertView.findViewById(R.id.areaname);
            mode.delete=(TextView)convertView.findViewById(R.id.delete);
            mode.areadetail=(TextView)convertView.findViewById(R.id.area_detail);
            mode.fujin=(TextView)convertView.findViewById(R.id.fujin);
            convertView.setTag(mode);
        }else{
            mode= (Mode) convertView.getTag();
        }
        mode.areadetail.setText(list.get(position).address);
        mode.title.setText(list.get(position).title);
        mode.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Message message=handler.obtainMessage();
                message.what=MessageInfoUtil.TO_SET_SAFE_RANGE_ACTIVITY;
                message.arg1=index;
                message.sendToTarget();
            }
        });
        mode.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
                alertDialog.setTitle("提示")
                           .setMessage("是否删除")
                           .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {

                                   Message message=handler.obtainMessage();
                                   message.what= MessageInfoUtil.DELETE;
                                   message.arg1=index;
                                   message.sendToTarget();
                               }
                           })
                        .setNegativeButton("取消",null).create().show();


            }
        });
        return convertView;
    }
    class Mode{
        TextView title;
        TextView delete;
        TextView areadetail;
        TextView fujin;
    }
}
