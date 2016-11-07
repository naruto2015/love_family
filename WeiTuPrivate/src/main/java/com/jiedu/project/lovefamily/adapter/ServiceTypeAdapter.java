package com.jiedu.project.lovefamily.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.message.MessageInfoUtil;
import com.jiedu.project.lovefamily.mode.ServiceMode;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/6.
 */
public class ServiceTypeAdapter extends BaseAdapter {
    ArrayList<ServiceMode> list;
    Context context;
    Handler handler;

    public ServiceTypeAdapter(Context context, ArrayList<ServiceMode> list, Handler handler) {
    this.context=context;
        this.list=list;
        this.handler=handler;
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
        final int index=position;
        Mode mode=null;
        if(view==null){
            mode=new Mode();
            view= LayoutInflater.from(context).inflate(R.layout.item_service_type_list,null);
            mode.name= (TextView) view.findViewById(R.id.service_name);
            mode.price= (TextView) view.findViewById(R.id.price);
            mode.layout= (LinearLayout) view.findViewById(R.id.layout_service);
            mode.check= (ImageView) view.findViewById(R.id.check);
            view.setTag(mode);
        }else {
            mode= (Mode) view.getTag();
        }
        mode.name.setText(list.get(position).getName());
        mode.price.setText(list.get(position).getBusiExpense());
        if(list.get(position).getIsCheck()){
            mode.check.setImageDrawable(context.getResources().getDrawable(R.drawable.pay_checked));
        }else{
            mode.check.setImageDrawable(context.getResources().getDrawable(R.drawable.pay_no_checked));
        }


        mode.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message=handler.obtainMessage();
                message.what= MessageInfoUtil.SET_PAY_NUM;
                message.arg1=index;
                message.obj=list.get(index).getBusiExpense();
                message.sendToTarget();
            }
        });
        return view;
    }
    public  ArrayList<ServiceMode> getList(){
        return list;
    }
    class Mode{
        TextView name;
        TextView price;
        LinearLayout layout;
        ImageView check;
    }
}
