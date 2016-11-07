package com.jiedu.project.lovefamily.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.message.MessageInfoUtil;
import com.jiedu.project.lovefamily.mode.PayStyleMode;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/6.
 */
public class PayStyleAdapter extends BaseAdapter {
    Context context;
    ArrayList<PayStyleMode>list;
    Handler handler;
    private ImageLoader loader;
    public PayStyleAdapter(Context context,ArrayList<PayStyleMode>list,Handler handler){
    this.context=context;
        this.list=list;
        this.handler=handler;
        loader=ImageLoader.getInstance().getInstance();
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
    public View getView(int position, View view, ViewGroup parent) {
        final  int index=position;
        Mode mode=null;
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.item_pay_type,null);
            mode=new Mode();
            mode.img= (ImageView) view.findViewById(R.id.img);
            mode.name= (TextView) view.findViewById(R.id.name);
            mode.check=(ImageView)view.findViewById(R.id.is_checked);
            view.setTag(mode);
        }else{
            mode= (Mode) view.getTag();
        }
        mode.name.setText(list.get(position).getName());
        loader.displayImage(list.get(position).getImgUrl(),mode.img);
        if(list.get(position).getIsChecked()){
            mode.check.setImageDrawable(context.getResources().getDrawable(R.drawable.pay_checked));
        }else{
            mode.check.setImageDrawable(context.getResources().getDrawable(R.drawable.pay_no_checked));
        }
        mode.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message=handler.obtainMessage();
                message.what= MessageInfoUtil.SET_SERVICE_DATA;
                message.arg1=index;
                message.sendToTarget();
            }
        });
        return view;
    }

    class Mode{
        ImageView img;
        TextView name;
        ImageView check;
    }
}
