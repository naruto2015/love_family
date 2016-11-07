package com.jiedu.project.lovefamily.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.mode.User;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by naruto on 2016/5/16.
 */
public class HomeListViewAdapter extends BaseAdapter {

    private ArrayList<User> list;
    private Context context;
    private ImageLoader loader;
    private LinearLayout hideOptions;
    DisplayImageOptions options;

    public HomeListViewAdapter(Context context,ArrayList<User> list){
        this.list=list;
        this.context=context;
//        Log.e("0015","HomeListViewAdapter---==========>list.size()"+list.size());
       /* ImageLoaderConfiguration configuration=new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 1) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(context)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs()
                .build();*/

        options=new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                //.cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.head_peron)
                .showImageOnFail(R.drawable.head_peron)
                .build();

        loader=ImageLoader.getInstance();
    }


    @Override
    public int getCount() {
//        Log.e("0015","getCount--->list.size()"+list.size());
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        HomeViewHolder homeViewHolder=null;
        if(convertView==null){
            homeViewHolder=new HomeViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.home_listview_item,null);
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
//        Log.e("0015","list.get(i).getUserName().equals(\"添加成员\")--->list.size()"+list.size());
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
//            Log.e("0015","homeViewHolder--->list.size()"+list.size());
            if(TextUtils.isEmpty(list.get(i).getPhotoUrl())){
                loader.displayImage("drawable://" + R.drawable.head_peron,homeViewHolder.user_head);
//                Log.e("0015","loader.displayImage--if--->list.size()"+list.size());
            }else{
                //loader.displayImage(list.get(i).getPhotoUrl(),homeViewHolder.user_head);
               loader.displayImage(list.get(i).getPhotoUrl(),homeViewHolder.user_head,options);
//                Log.e("0015","loader.displayImage--else--->list.size()"+list.size());
            }
           /* loader.displayImage(list.get(i).getPhotoUrl(),homeViewHolder.user_head);
            loader.displayImage(list.get(i).getPhotoUrl(),homeViewHolder.user_head,R.drawable.default_head);*/
            homeViewHolder.user_name.setText(list.get(i).getUserName());
            homeViewHolder.status.setText(list.get(i).getUserStatue());
//            Log.e("0015","HomeListViewAdapter--->list.size()"+list.size());
            setTextColor(list.get(i).getStatueNum(),homeViewHolder.status);
        }



        return convertView;
    }

    private void setTextColor(String st,TextView userStatue) {
//        if("1".equals(st)){
//            userStatue.setTextColor(Color.GRAY);
//        }else
        if("4".equals(st)){
            userStatue.setTextColor(context.getResources().getColor(R.color.online));
        }
        else {
            userStatue.setTextColor(Color.GRAY);
        }
    }

    class HomeViewHolder{
        CircleImageView user_head;
        TextView user_name,hide_dingwei,hide_shipin,hide_dianhua,hide_duanxin,hide_shezhi;
        TextView status;
        LinearLayout hideOptions;
    }

}
