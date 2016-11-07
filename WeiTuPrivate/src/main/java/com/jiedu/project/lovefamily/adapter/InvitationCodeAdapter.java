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
import com.jiedu.project.lovefamily.mode.InvitationCode;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/24.
 * 邀请码适配器
 */
public class InvitationCodeAdapter extends BaseAdapter{
   ArrayList<InvitationCode>list;
    Context context;
    Handler handler;
    public InvitationCodeAdapter(Context context,ArrayList<InvitationCode>list,Handler handler){
        this.context=context;
        this.handler=handler;
        this.list=list;
//        Log.e("0011","监控人表长度"+list.size());
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final int index=position;
        Mode mode=null;
        if(convertView==null){
            mode=new Mode();
            convertView= LayoutInflater.from(context).inflate(R.layout.sharerelationship_item,null);
            mode.phone= (TextView) convertView.findViewById(R.id.phone);
            mode.delete= (TextView) convertView.findViewById(R.id.delete);
            convertView.setTag(mode);
        }else{
            mode= (Mode) convertView.getTag();
        }
        mode.phone.setText(list.get(position).phone);
        mode.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("提示")
                        .setMessage("是否删除?")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Message message=handler.obtainMessage();
                                message.what= MessageInfoUtil.DELETE;
                                message.arg1=index;
                                message.sendToTarget();
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();

            }
        });
        return convertView;
    }
    class Mode{
        TextView phone;
        TextView delete;
    }
}
