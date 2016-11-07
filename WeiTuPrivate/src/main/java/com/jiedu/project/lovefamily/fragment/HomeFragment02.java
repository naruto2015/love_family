package com.jiedu.project.lovefamily.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.activity.AddActivity;
import com.jiedu.project.lovefamily.activity.HomeActivity;
import com.jiedu.project.lovefamily.activity.LocationActivity;
import com.jiedu.project.lovefamily.activity.MessageActivity;
import com.jiedu.project.lovefamily.activity.MessageClassifyActivity;
import com.jiedu.project.lovefamily.activity.MonitorRequestDialog;
import com.jiedu.project.lovefamily.activity.PayActivity;
import com.jiedu.project.lovefamily.activity.PayContentActivity;
import com.jiedu.project.lovefamily.activity.VedioActivity;
import com.jiedu.project.lovefamily.activitydailog.HomeFourDialogActivity;
import com.jiedu.project.lovefamily.activitydailog.ShareDialogActivity;
import com.jiedu.project.lovefamily.adapter.HomeGridViewAdapter;
import com.jiedu.project.lovefamily.adapter.HomeListViewAdapter;
import com.jiedu.project.lovefamily.adapter.HomeptrAdapter;
import com.jiedu.project.lovefamily.json.JsonHelp;
import com.jiedu.project.lovefamily.message.MessageInfoUtil;
import com.jiedu.project.lovefamily.mode.PositionBean;
import com.jiedu.project.lovefamily.mode.User;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.popupwindow.HomePopupwindowUtil;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;
import com.jiedu.project.lovefamily.sqlite.FriendDao;
import com.jiedu.project.lovefamily.sqlite.FriendDaoImpl;
import com.jiedu.project.lovefamily.stringutil.IntentString;
import com.jiedu.project.lovefamily.utils.CustomDialog;
import com.jiedu.project.lovefamily.utils.HomeTopDailog;
import com.jiedu.project.lovefamily.utils.NetUtil;
import com.jiedu.project.lovefamily.utils.ShareUmengDialog;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/3/17.
 */
public class HomeFragment02 extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    View view;
    private PullToRefreshGridView gridView;
    private HomeGridViewAdapter adapter;
    private ArrayList<User> list = new ArrayList<User>();
    public HomePopupwindowUtil util;
    private ImageView share;
    private ImageView message_top;
    RequestHelp requestHelp;
    JsonHelp jsonHelp;
    int index;
    User user;

    private TextView del,bianji,safeEdit,myLocation;

    private PullToRefreshExpandableListView pullToRefreshListView;
    private Button ibhomejia;
    private ImageView dingwei_iv,user_photo;
    private HomeListViewAdapter homeListViewAdapter;
    private HomeptrAdapter homeptrAdapter;
    FriendDao friendDao;
    //private ImageView shipintonghua;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home02, null);
//         // 加载脚布局
//        View footView=inflater.inflate(R.layout.home_listview_foot,null);
        friendDao=new FriendDaoImpl(getActivity());
        init();



        IntentFilter intentFilterIsUp=new IntentFilter();
        intentFilterIsUp.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilterIsUp.addAction("com.update.family.member");
        getActivity().registerReceiver(receiver,intentFilterIsUp);

        if(NetUtil.checkNet(getActivity())){
            startGetMemberListThread();
        }else{

            list.clear();
            int size=list.size();
            //list=friendDao.getAll();
            list.addAll(friendDao.getAll());
            //initData();

            pullToRefreshListView.onRefreshComplete();
            homeListViewAdapter.notifyDataSetChanged();
            Toast.makeText(getActivity(),"网络断开！",Toast.LENGTH_SHORT).show();
        }

        return view;
    }



    BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String flag=intent.getStringExtra("flag");
            if(flag.equals("update")){
                startGetMemberListThread();
            }
        }
    };


    public boolean buy(){
        String isExpired=SharedPreferencesUtil.getInfo(getActivity(),"isExpired");
        if(isExpired.equals(0+"")){

        }else{
            final CustomDialog cd=new CustomDialog(getActivity());
            cd.setTitle("温馨提示");
            cd.setMessage("你的免费试用已到期!");
            cd.setPositiveButton("订购", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     cd.dismiss();
                    if(SharedPreferencesUtil.getInfo(getActivity(),"orderMsg")==null || SharedPreferencesUtil.getInfo(getActivity(),"orderMsg")==""){
                        startActivityForResult(new Intent(getActivity(), PayActivity.class),1001);
                    }else{
                        startActivityForResult(new Intent(getActivity(), PayContentActivity.class),1001);
                    }
                }
            });
            cd.setNegativeButton("取消", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cd.dismiss();
                }
            });
            return true;
//            return false;
        }

        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    private void init() {
       /* this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);*/
        //gridView = (PullToRefreshGridView) view.findViewById(R.id.home_grid);
        pullToRefreshListView= (PullToRefreshExpandableListView) view.findViewById(R.id.home_listview);
        user_photo= (ImageView) view.findViewById(R.id.user_photo);

   //     shipintonghua=(ImageView)view.findViewById(R.id.shipintonghua);
//        // 加载脚布局
//        View footView = View.inflate(getActivity(), R.layout.home_listview_foot,null);
            refreshAdd();
//        //添加成员按钮
//        ibhomejia=(ImageView) view.findViewById(R.id.ib_home_jia);
//        Log.e("0015","list.size()"+list.size());
//        if (list.size()==0){
//            ibhomejia.setVisibility(View.VISIBLE);
//        }else {
//            ibhomejia.setVisibility(View.GONE);
//        }

//        shipintonghua.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getContext(),VedioActivity.class));
//            }
//        });
        ibhomejia.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(buy()) {

                        }else{
                            startActivityForResult(new Intent(getActivity(), AddActivity.class), 2);
                        }
                    }
                }
        );
//        pullToRefreshListView.addView(footView,list.size());

       // share = (ImageView) view.findViewById(R.id.share);
        message_top= (ImageView) view.findViewById(R.id.message_top);
        requestHelp = new RequestHelp();
        jsonHelp = new JsonHelp();

        util = new HomePopupwindowUtil();
        //adapter = new HomeGridViewAdapter(this.getActivity(), list);
        homeListViewAdapter=new HomeListViewAdapter(getActivity(),list);
        homeptrAdapter=new HomeptrAdapter(getActivity(),list);

      /*  gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);*/
//        pullToRefreshListView.setAdapter(homeListViewAdapter);


        pullToRefreshListView.getRefreshableView().setAdapter(homeptrAdapter);
        pullToRefreshListView.getRefreshableView().setGroupIndicator(null);//去掉默认展开小图标
        pullToRefreshListView.setOnItemClickListener(this);

        user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.openDrawer();
            }
        });

        message_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),MessageClassifyActivity.class));
            }
        });
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ExpandableListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
                refreshAdd();//控制显示按钮
                if(NetUtil.checkNet(getActivity())){
                    startGetMemberListThread();
                }else{
                    list.clear();
                    int size=list.size();
                    //list=friendDao.getAll();
                    list.addAll(friendDao.getAll());
                    refreshAdd();//控制显示按钮
                    //initData();
                     pullToRefreshListView.onRefreshComplete();
                    homeListViewAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(),"网络断开！",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {

            }
        });

//        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                refreshAdd();//控制显示按钮
//                if(NetUtil.checkNet(getActivity())){
//                    startGetMemberListThread();
//                }else{
//                    list.clear();
//                    int size=list.size();
//                    //list=friendDao.getAll();
//                    list.addAll(friendDao.getAll());
//                    refreshAdd();//控制显示按钮
//                    //initData();
//                     pullToRefreshListView.onRefreshComplete();
//                    homeListViewAdapter.notifyDataSetChanged();
//                    Toast.makeText(getActivity(),"网络断开！",Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//
//            }
//        });
       /* gridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                startGetMemberListThread();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {

            }
        });*/

        myView=LayoutInflater.from(getActivity()).inflate(R.layout.personalpop_item,null);
        del= (TextView) myView.findViewById(R.id.tv_del);
        safeEdit= (TextView) myView.findViewById(R.id.tv_safe);
        myLocation= (TextView) myView.findViewById(R.id.tv_dingwei);
        bianji= (TextView) myView.findViewById(R.id.tv_bianji);

       /* del.setOnClickListener(new View.OnClickListener() {
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
        });*/

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        if(buy()){

        }else if(NetUtil.checkNet(getActivity())){
            if (position-1 < list.size()) {
                //util.showPopupwindow(pullToRefreshListView, this.getActivity(),  handler, position-1, list.get(position-1));
                Intent intent=new Intent();
                intent.setClass(getActivity(), HomeFourDialogActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("user",list.get(position-1));
                intent.putExtras(bundle);
                startActivityForResult(intent,1);
                //myPopwindow();

            } else {
                startActivityForResult(new Intent(this.getActivity(), AddActivity.class), 2);
            }
        }else{
            Toast.makeText(getActivity(),"网络断开！",Toast.LENGTH_SHORT).show();
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


    private PositionBean positionBean;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case MessageInfoUtil.DELETE:
                    index=msg.arg2;
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    builder.setTitle("提示")
                            .setMessage("是否删除?")
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String result= requestHelp.deleteMonitor(SharedPreferencesUtil.getInfo(getActivity(),"customerId"),list.get(index).getMonitoredId());
                                            jsonHelp.dealDeleteMonitor(result,handler);
                                            refreshAdd();
                                        }
                                    }).start();
                                }
                            })
                            .setNegativeButton("取消",null)
                            .show();

                    break;
                case MessageInfoUtil.MEMBER:
//                  Log.e("0011","查询数o据得到结果刷新adapter");
                    //gridView.onRefreshComplete();
                    pullToRefreshListView.onRefreshComplete();
                    friendDao.deleteAll();
                    friendDao.addAll(list);
                    //friendDao.getAll();
                    //initData();
                    //adapter.notifyDataSetChanged();
                    homeListViewAdapter.notifyDataSetChanged();
                    break;
                case MessageInfoUtil.GET_LOCATION:
//                    Log.e("0011","获取位置信息");
                    positionBean=new PositionBean();
                    //LatLng latLng = (LatLng) msg.obj;
                    positionBean= (PositionBean) msg.obj;
                    Intent intent = new Intent(getActivity(), LocationActivity.class);
                    intent.putExtra("lat", positionBean.latLng.latitude);
                    intent.putExtra("lon", positionBean.latLng.longitude);
                    intent.putExtra(IntentString.NICK_NAME,list.get(msg.arg2).getUserName());
                    intent.putExtra(IntentString.PHONO_URL,list.get(msg.arg2).getPhotoUrl());
                    intent.putExtra("uploadTime",positionBean.uploadTime);
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
                case 2001:
                    list.clear();
                    String result= (String) msg.obj;
                    jsonHelp.dealMemberList(result, getActivity(), handler, list);

                    break;
                case 2002:
                    String result2= (String) msg.obj;
                    jsonHelp.dealGetMonitorRequestList(result2, handler);

                    refreshAdd();

                    break;
                case 2004:

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
        //if(list.indexOf(user)<00){
            list.add(user);
        //}
        //homeListViewAdapter.notifyDataSetChanged();

    }


    private ShareUmengDialog shareUmengDialog;

    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            homeTopDailog.dismiss();
            switch (view.getId()){
                case R.id.home_share:
                 /*   shareUmengDialog=new ShareUmengDialog(getActivity(),itemsOnClick);
                    //显示窗口
                    shareUmengDialog.showAtLocation(getActivity().findViewById(R.id.share), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                   */
                    startActivity(new Intent(getActivity(), ShareDialogActivity.class));
                    break;
                case R.id.home_jia:
                    if(buy()) {

                      }else{
                        startActivityForResult(new Intent(getActivity(), AddActivity.class), 2);
                          }
                    break;
                case R.id.home_msg:

                    startActivity(new Intent(getActivity(), MessageActivity.class));
                    break;
            }
        }
    };

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

    HomeTopDailog homeTopDailog;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share:

//                if(buy()) {
//
//                }else{
                    homeTopDailog = new HomeTopDailog(getActivity(), listener);
                    homeTopDailog.showAsDropDown(share);
//                }
                break;
        }
    }

    public void startGetMemberListThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                String result = requestHelp.getMember(SharedPreferencesUtil.getInfo(getActivity(), "customerId"));

                String resultMontior= requestHelp.requestPost(RequestHelp.GET_MONITOR_REQUEST+SharedPreferencesUtil.getInfo(getActivity(),"customerId"),new HashMap<String, Object>());

                Log.e("0011","查询成员"+result);

                Log.e("0011", "请求结果监控请求" + resultMontior);

                    Message myMsg=Message.obtain();
                    myMsg.what=2001;
                    myMsg.obj=result;
                    handler.sendMessage(myMsg);

                    Message myMsg2=Message.obtain();
                    myMsg2.what=2002;
                    myMsg2.obj=resultMontior;
                    handler.sendMessage(myMsg2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void refreshAdd(){
        //添加成员按钮
        ibhomejia=(Button) view.findViewById(R.id.home_jia);
        dingwei_iv= (ImageView) view.findViewById(R.id.dingwei_iv);
        Log.e("0015","list.size()"+list.size());
        if (list.size()==0){
            ibhomejia.setVisibility(View.VISIBLE);
            dingwei_iv.setVisibility(View.VISIBLE);
        }else {
            dingwei_iv.setVisibility(View.GONE);
        }
    }

}
