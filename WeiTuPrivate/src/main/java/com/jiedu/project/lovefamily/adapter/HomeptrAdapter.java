package com.jiedu.project.lovefamily.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.activity.EditActivity;
import com.jiedu.project.lovefamily.activity.HomeActivity;
import com.jiedu.project.lovefamily.activity.LocationActivity;
import com.jiedu.project.lovefamily.activity.PersonalFragment;
import com.jiedu.project.lovefamily.activity.VedioActivity;
import com.jiedu.project.lovefamily.application.MyApplication;
import com.jiedu.project.lovefamily.mode.PositionBean;
import com.jiedu.project.lovefamily.mode.User;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;
import com.jiedu.project.lovefamily.stringutil.IntentString;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/10/18.
 */
public class HomeptrAdapter extends BaseExpandableListAdapter {
    private ProgressDialog progressDialog;
    private Activity mActivity;
    RequestHelp help;
    HomeViewHolder homeViewHolder=null;
    //群组名
    private ArrayList<User> list;
    //群组下的子项
    private ArrayList<ArrayList<Child>> childs;
    private Context mContext;
    private ImageLoader loader;
    private LinearLayout hideOptions;
    DisplayImageOptions options;
    private int i;
    public HomeptrAdapter(Context context,ArrayList<User> list){
       this.mContext=context;
        this.list=list;
        help=new RequestHelp();
        childs = new ArrayList<ArrayList<Child>>();
        options=new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                //.cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.head_peron)
                .showImageOnFail(R.drawable.head_peron)
                .build();
        loader= ImageLoader.getInstance();
        for (int i = 0; i < list.size(); i++) {
            ArrayList<Child> values = new ArrayList<>();
            Child child = new Child();
            values.add(child);
            childs.add(values);
        }
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return list.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return childs.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int i, boolean b, View view, ViewGroup viewGroup) {
        View convertView=null;

        if(convertView==null){
            homeViewHolder=new HomeViewHolder();
             convertView= LayoutInflater.from(mContext).inflate(R.layout.item_group,null);
            homeViewHolder.hideOptions= (LinearLayout) convertView.findViewById(R.id.hideOptions);
            homeViewHolder.user_head= (CircleImageView) convertView.findViewById(R.id.user_head);
            homeViewHolder.user_name= (TextView) convertView.findViewById(R.id.user_name);
            homeViewHolder.hide_dingwei= (TextView) convertView.findViewById(R.id.hide_dinewei);
            homeViewHolder.hide_shipin= (TextView) convertView.findViewById(R.id.hide_shipin);
            homeViewHolder.hide_dianhua= (TextView) convertView.findViewById(R.id.hide_dianhua);
            homeViewHolder.hide_duanxin= (TextView) convertView.findViewById(R.id.hide_duanxin);
            homeViewHolder.hide_shezhi= (TextView) convertView.findViewById(R.id.hide_shezhi);
            homeViewHolder.status= (TextView) convertView.findViewById(R.id.status);

            convertView.setTag(homeViewHolder);

        }else{
            homeViewHolder= (HomeViewHolder) convertView.getTag();
        }
        if(list.get(i).getUserName().equals("添加成员")){
            homeViewHolder.user_name.setVisibility(View.GONE);
            homeViewHolder.user_head.setVisibility(View.GONE);
            homeViewHolder.status.setVisibility(View.GONE);
            homeViewHolder.hideOptions.setVisibility(View.GONE);
            convertView.findViewById(R.id.add_relay).setVisibility(View.VISIBLE);
        }else{

            homeViewHolder.user_name.setVisibility(View.VISIBLE);
            homeViewHolder.user_head.setVisibility(View.VISIBLE);
            homeViewHolder.status.setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.add_relay).setVisibility(View.GONE);
            if(TextUtils.isEmpty(list.get(i).getPhotoUrl())){
                loader.displayImage("drawable://" + R.drawable.head_peron,homeViewHolder.user_head);
            }else{
                loader.displayImage(list.get(i).getPhotoUrl(),homeViewHolder.user_head,options);
            }
            homeViewHolder.user_name.setText(list.get(i).getUserName());
            homeViewHolder.status.setText(list.get(i).getUserStatue());
            setTextColor(list.get(i).getStatueNum(),homeViewHolder.status);
        }
        return convertView;
    }

    private void setTextColor(String st,TextView userStatue) {
//        if("1".equals(st)){
//            userStatue.setTextColor(Color.GRAY);
//        }else
        if("4".equals(st)){
            userStatue.setTextColor(mContext.getResources().getColor(R.color.online));
        }
        else {
            userStatue.setTextColor(Color.GRAY);
        }
    }

    @Override
    public View getChildView(final int i3, int i4, boolean b, View view, ViewGroup viewGroup) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_child,null);
        TextView hide_dingwei= (TextView) v.findViewById(R.id.hide_dinewei);
        TextView hide_shipin= (TextView) v.findViewById(R.id.hide_shipin);
        final TextView hide_dianhua= (TextView) v.findViewById(R.id.hide_dianhua);
        TextView hide_shezhi= (TextView) v.findViewById(R.id.hide_shezhi);
        TextView hide_duanxin= (TextView) v.findViewById(R.id.hide_duanxin);


        hide_dingwei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog=new ProgressDialog(mContext);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("正在获取实时位置.....");
                //progressDialog.setCancelable(false);
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String result=help.getLocation(list.get(i3).getMonitoredId());

                        if(result!=null){
                            Message msg=Message.obtain();
                            msg.what=i3;
                            msg.obj=result;
                            handler.sendMessage(msg);
                        }

                    }
                }).start();


            }
        });
        hide_shipin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, VedioActivity.class);
                intent.putExtra("touser",list.get(i3).getPhone());
                intent.putExtra("fromuser",SharedPreferencesUtil.getInfo(mContext, "phone"));
                mContext.startActivity(intent);
            }
        });
        hide_dianhua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+list.get(i3).getPhone()));
                mContext.startActivity(intent);
            }
        });
        hide_duanxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:"+list.get(i3).getPhone()));
                mContext.startActivity(intent);
            }
        });
        hide_shezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, EditActivity.class);
                intent.putExtra(IntentString.INTENT_TITLE,list.get(i3).getUserName());
                intent.putExtra(IntentString.CUSTOMER_ID,list.get(i3).getMonitoredId());
                intent.putExtra(IntentString.NICK_NAME,list.get(i3).getUserName());
                intent.putExtra(IntentString.BRITHDAY,list.get(i3).getBirthday());
                intent.putExtra(IntentString.SEX,list.get(i3).getSex());
                intent.putExtra(IntentString.ADDRESS,list.get(i3).getAddress());
                intent.putExtra(IntentString.PHONO_URL,list.get(i3).getPhotoUrl());
                intent.putExtra(IntentString.PHONE,list.get(i3).getPhone());
                mContext.startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }


    class HomeViewHolder{
        CircleImageView user_head;
        TextView user_name,hide_dingwei,hide_shipin,hide_dianhua,hide_duanxin,hide_shezhi;
        TextView status;
        LinearLayout hideOptions;
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
//                      Log.e("0011","定位信息"+json);
                        if (!TextUtils.isEmpty(jsonObject.optString("data"))) {
                            JSONObject dataObject = new JSONObject(jsonObject.optString("data"));
                            LatLng latLng = new LatLng(dataObject.optDouble("latitude"), dataObject.optDouble("longitude"));
                            String uploadTime=dataObject.optString("uploadTime");

                            Intent intent = new Intent(mContext, LocationActivity.class);
                            intent.putExtra("user",list.get(msg.what));
                            intent.putExtra("lat", latLng.latitude);
                            intent.putExtra("lon", latLng.longitude);
                            intent.putExtra(IntentString.NICK_NAME, list.get(msg.what).getUserName());
                            intent.putExtra(IntentString.PHONO_URL, list.get(msg.what).getPhotoUrl());
                            intent.putExtra(IntentString.ADDRESS, list.get(msg.what).getAddress());
                          //  intent.putExtra(IntentString.ADDRESS, list.get(msg.what).getAddress());
                            intent.putExtra("uploadTime",list.get(msg.what).uploadTime);
                            mContext.startActivity(intent);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

        }
    };


}
