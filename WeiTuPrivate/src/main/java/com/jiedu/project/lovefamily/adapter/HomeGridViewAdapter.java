package com.jiedu.project.lovefamily.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.mode.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/2.
 */
 public class HomeGridViewAdapter extends BaseAdapter{
    private ArrayList<User>list;
    private  Context context;
    private ImageLoader loader;
    public HomeGridViewAdapter(Context contextt,ArrayList<User>list){
        this.list=list;
        this.context=contextt;
        loader=ImageLoader.getInstance().getInstance();
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
        ViewMode mode=null;
        if(null==convertView){
            mode=new ViewMode();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_home_gridview,null);
            mode.photo=(ImageView)convertView.findViewById(R.id.photo);
            mode.userName=(TextView)convertView.findViewById(R.id.name);
            mode.userStatue=(TextView)convertView.findViewById(R.id.statue);
            convertView.setTag(mode);
        }else{
            mode= (ViewMode) convertView.getTag();
        }
            if(TextUtils.isEmpty(list.get(position).getPhotoUrl())){
                loader.displayImage("drawable://" + R.drawable.head_peron,mode.photo);
            }else{
                loader.displayImage(list.get(position).getPhotoUrl(),mode.photo);
            }


        mode.userName.setText(list.get(position).getUserName());
        mode.userStatue.setText(list.get(position).getUserStatue());
        setTextColor(list.get(position).getStatueNum(),mode.userStatue);
        return convertView;
    }

    private void setTextColor(String st,TextView userStatue) {
//        if("1".equals(st)){
//            userStatue.setTextColor(Color.GRAY);
//        }else
        if("4".equals(st)){
            userStatue.setTextColor(Color.GREEN);
        }
        else {
            userStatue.setTextColor(Color.GRAY);
        }
    }

    class ViewMode{
        private ImageView photo;
        private TextView userName;
        private TextView userStatue;
    }
}
