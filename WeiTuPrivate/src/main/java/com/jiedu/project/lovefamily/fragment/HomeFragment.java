package com.jiedu.project.lovefamily.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.activity.AddActivity;
import com.jiedu.project.lovefamily.activity.LocationActivity;
import com.jiedu.project.lovefamily.activity.MonitorRequestDialog;
import com.jiedu.project.lovefamily.adapter.HomeGridViewAdapter;
import com.jiedu.project.lovefamily.json.JsonHelp;
import com.jiedu.project.lovefamily.message.MessageInfoUtil;
import com.jiedu.project.lovefamily.mode.User;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.popupwindow.HomePopupwindowUtil;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;
import com.jiedu.project.lovefamily.stringutil.IntentString;
import com.jiedu.project.lovefamily.utils.ShareUmengDialog;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/3/17.
 */
public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    View view;
    private PullToRefreshGridView gridView;
    private HomeGridViewAdapter adapter;
    private ArrayList<User> list = new ArrayList<User>();
    public HomePopupwindowUtil util;
    private ImageView share;
    RequestHelp requestHelp;
    JsonHelp jsonHelp;
    int index;
    User user;

    private TextView del,bianji,safeEdit,myLocation;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, null);
        init();
        return view;
    }

    private void init() {
        startGetMemberListThread();
        gridView = (PullToRefreshGridView) view.findViewById(R.id.home_grid);
        share = (ImageView) view.findViewById(R.id.share);
        requestHelp = new RequestHelp();
        jsonHelp = new JsonHelp();

        util = new HomePopupwindowUtil();
        adapter = new HomeGridViewAdapter(this.getActivity(), list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
        share.setOnClickListener(this);
        gridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                startGetMemberListThread();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {

            }
        });

        myView=LayoutInflater.from(getActivity()).inflate(R.layout.personalpop_item,null);
        del= (TextView) myView.findViewById(R.id.tv_del);
        safeEdit= (TextView) myView.findViewById(R.id.tv_safe);
        myLocation= (TextView) myView.findViewById(R.id.tv_dingwei);
        bianji= (TextView) myView.findViewById(R.id.tv_bianji);


        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        safeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bianji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < list.size() - 1) {
            util.showPopupwindow(gridView, this.getActivity(),  handler, position, list.get(position));
            //myPopwindow();
        } else {
            startActivityForResult(new Intent(this.getActivity(), AddActivity.class), 2);
        }
    }


    private PopupWindow popupWindow;
    private View myView;
    private void myPopwindow(){

        popupWindow=new PopupWindow(myView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);

    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case MessageInfoUtil.DELETE:
                    index=msg.arg2;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String result= requestHelp.deleteMonitor(SharedPreferencesUtil.getInfo(getActivity(),"customerId"),list.get(index).getMonitoredId());
                            jsonHelp.dealDeleteMonitor(result,handler);
                        }
                    }).start();
                    break;
                case MessageInfoUtil.MEMBER:
//                    Log.e("0011","查询数据得到结果刷新adapter");
                   gridView.onRefreshComplete();
                    initData();
                    adapter.notifyDataSetChanged();
                    break;
                case MessageInfoUtil.GET_LOCATION:
//                    Log.e("0011","获取位置信息");
                    LatLng latLng = (LatLng) msg.obj;
                    Intent intent = new Intent(getActivity(), LocationActivity.class);
                    intent.putExtra("lat", latLng.latitude);
                    intent.putExtra("lon", latLng.longitude);
                    intent.putExtra(IntentString.NICK_NAME,list.get(msg.arg2).getUserName());
                    intent.putExtra(IntentString.PHONO_URL,list.get(msg.arg2).getPhotoUrl());
                    intent.putExtra("uploadTime",list.get(msg.arg2).uploadTime);
                    getActivity().startActivity(intent);
                    break;


            }
            switch (msg.what){
                case MessageInfoUtil.DELETE_MONITOR:
                    //刷新列表
                    startGetMemberListThread();
                    break;
                case MessageInfoUtil.DELETE_MONITOR_FAIL:
                    Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case MessageInfoUtil.SHOW_TOAST:
                    Toast.makeText(getActivity(),"该用户没有位置信息",Toast.LENGTH_SHORT).show();
                    break;
                case MessageInfoUtil.SHOW_DIAAOLG:
                    Intent intent=new Intent(getActivity(), MonitorRequestDialog.class);
                    intent.putExtra(IntentString.JSON,msg.obj.toString());
                    startActivity(intent);
                    break;
            }

        }
    };

    void initData() {
//        Log.e("0011","添加按钮");
        user = new User();
        user.setPhotoUrl("drawable://" + R.drawable.add);
        user.setUserName("添加成员");
        user.setUserStatue("");
        if(list.indexOf(user)<00){
            list.add(user);
        }

    }


    private ShareUmengDialog shareUmengDialog;


    //为弹出窗口实现监听类
    private View.OnClickListener  itemsOnClick = new View.OnClickListener(){
        public void onClick(View v) {
            shareUmengDialog.dismiss();
            UMImage umImage=new UMImage(getActivity(), R.drawable.logo);
            switch (v.getId()) {
                case R.id.weixin:
                    new ShareAction(getActivity())
                            .setPlatform(SHARE_MEDIA.WEIXIN)
                            /*.setCallback(umShareListener)*/
                            .withText("您的家人邀请您安装爱家在线，快来看看吧")
                            .withTitle("爱家在线")
                            .withTargetUrl(RequestHelp.HTTP_HEAD + RequestHelp.HTTP_ACTION + "/wttp/toIntroduce.do")
                            .withMedia(umImage)
                            .share();
                    break;
                case R.id.weixinZong:
                    new ShareAction(getActivity())
                            .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                            /*.setCallback(umShareListener)*/
                            .withText("您的家人邀请您安装爱家在线，快来看看吧")
                            .withTitle("爱家在线")
                            .withTargetUrl(RequestHelp.HTTP_HEAD + RequestHelp.HTTP_ACTION + "/wttp/toIntroduce.do")
                            .withMedia(umImage)
                            .share();
                    break;
                case R.id.qq:
                    new ShareAction(getActivity())
                            .setPlatform(SHARE_MEDIA.QQ)
                            /*.setCallback(umShareListener)*/
                            .withText("您的家人邀请您安装爱家在线，快来看看吧")
                            .withTitle("爱家在线")
                            .withTargetUrl(RequestHelp.HTTP_HEAD + RequestHelp.HTTP_ACTION + "/wttp/toIntroduce.do")
                            .withMedia(umImage)
                            .share();
                    break;
                case R.id.qqZong:
                    new ShareAction(getActivity())
                            .setPlatform(SHARE_MEDIA.QZONE)
                            /*.setCallback(umShareListener)*/
                            .withText("您的家人邀请您安装爱家在线，快来看看吧")
                            .withTitle("爱家在线")
                            .withTargetUrl(RequestHelp.HTTP_HEAD + RequestHelp.HTTP_ACTION + "/wttp/toIntroduce.do")
                            .withMedia(umImage)
                            .share();
                    break;

            }

        }

    };
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share:
                //ShareUtil.share(getActivity());
                shareUmengDialog=new ShareUmengDialog(getActivity(),itemsOnClick);
                //显示窗口
                shareUmengDialog.showAtLocation(getActivity().findViewById(R.id.share), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                break;
        }
    }

    public void startGetMemberListThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);

                String result = requestHelp.getMember(SharedPreferencesUtil.getInfo(getActivity(), "customerId"));
//Log.e("0011","查询成员"+result);
                jsonHelp.dealMemberList(result, getActivity(), handler, list);

                String resultMontior= requestHelp.requestPost(RequestHelp.GET_MONITOR_REQUEST+SharedPreferencesUtil.getInfo(getActivity(),"customerId"),new HashMap<String, Object>());
                jsonHelp.dealGetMonitorRequestList(resultMontior, handler);
                Log.e("0011", "请求结果监控请求" + resultMontior);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }





}
