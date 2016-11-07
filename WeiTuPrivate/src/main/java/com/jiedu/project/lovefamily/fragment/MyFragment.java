package com.jiedu.project.lovefamily.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.activity.AboutVersionActivity;
import com.jiedu.project.lovefamily.activity.EditActivity;
import com.jiedu.project.lovefamily.activity.LoginActivity;
import com.jiedu.project.lovefamily.activity.MessageActivity;
import com.jiedu.project.lovefamily.activity.MonitorActivity;
import com.jiedu.project.lovefamily.activity.MyScanActivity;
import com.jiedu.project.lovefamily.activity.PayActivity;
import com.jiedu.project.lovefamily.activity.PayContentActivity;
import com.jiedu.project.lovefamily.activity.SitePlaceActivity;
import com.jiedu.project.lovefamily.json.JsonHelp;
import com.jiedu.project.lovefamily.message.MessageInfoUtil;
import com.jiedu.project.lovefamily.mode.User;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.service.Service1;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;
import com.jiedu.project.lovefamily.sqlite.UserDao;
import com.jiedu.project.lovefamily.sqlite.UserDaoImpl;
import com.jiedu.project.lovefamily.stringutil.IntentString;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/3/21.
 */
public class MyFragment extends Fragment implements View.OnClickListener {
    View view;
    //ImageView go;
    TextView go;
    TextView pay;
    Button cancellation;
    TextView phone;
    CircleImageView head_img;
    TextView name;
    TextView monitor;
    ImageLoader loader;
    RequestHelp requestHelp;
    JsonHelp jsonHelp;

    private TextView tv_userState;
    private TextView site_place;
    private TextView mycode;
    private TextView about,center_message;
    private CircleImageView mc;
    private RelativeLayout msg_center_relay;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my, null);
        initview();
        //initData();
        loadData();

        return view;
    }

    private void loadData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map=new HashMap<String,String>();
                map.put("customerId",SharedPreferencesUtil.getInfo(getActivity(),"customerId"));
                String json=requestHelp.requestPost(RequestHelp.QUERY_MESSAGE_COUNT,map);
                if(json!=null){
                    Message msg=Message.obtain();
                    msg.obj=json;
                    msg.what=1;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    public void gg(String orderMsg){
        tv_userState.setText(orderMsg);
    }

    private void initview() {
        requestHelp=new RequestHelp();
        jsonHelp=new JsonHelp();
        loader = ImageLoader.getInstance().getInstance();

        //go = (ImageView) view.findViewById(R.id.go);
        go = (TextView) view.findViewById(R.id.go);
        pay = (TextView) view.findViewById(R.id.pay);
        head_img = (CircleImageView) view.findViewById(R.id.head_img);
        name = (TextView) view.findViewById(R.id.name);
        monitor = (TextView) view.findViewById(R.id.monitor_list);
        cancellation = (Button) view.findViewById(R.id.cancellation);
        phone = (TextView) view.findViewById(R.id.phone);
        tv_userState= (TextView) view.findViewById(R.id.tv_userState);
        //site_place= (TextView) view.findViewById(R.id.site_place);
        mycode= (TextView) view.findViewById(R.id.mycode);
        about= (TextView) view.findViewById(R.id.about);
        mc= (CircleImageView) view.findViewById(R.id.mc);
        msg_center_relay= (RelativeLayout) view.findViewById(R.id.msg_center_relay);
        center_message= (TextView) view.findViewById(R.id.center_message);

        about.setOnClickListener(this);
        msg_center_relay.setOnClickListener(this);
        center_message.setOnClickListener(this);

        tv_userState.setText(SharedPreferencesUtil.getInfo(getActivity(),"useStatus"));

        //site_place.setOnClickListener(this);
        go.setOnClickListener(this);
        pay.setOnClickListener(this);
        cancellation.setOnClickListener(this);
        monitor.setOnClickListener(this);
        mycode.setOnClickListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("USER_STATUS");
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        getActivity().registerReceiver(receiver,intentFilter);
    }

    void initData() {

        phone.setText(SharedPreferencesUtil.getInfo(getActivity(), "phone"));
        if (TextUtils.isEmpty(SharedPreferencesUtil.getInfo(getActivity(), "photoUrl"))) {
//            Log.e("0011","设置默认的头像");
            loader.displayImage("drawable://" + R.drawable.default_head, head_img);
        } else {
//            Log.e("0011","设置修改的头像");
            loader.displayImage(SharedPreferencesUtil.getInfo(getActivity(), "photoUrl"), head_img);
        }

        if (TextUtils.isEmpty(SharedPreferencesUtil.getInfo(getActivity(), "nickName"))) {
            name.setText("user" + SharedPreferencesUtil.getInfo(getActivity(), "customerId"));
        } else {
            name.setText(SharedPreferencesUtil.getInfo(getActivity(), "nickName"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        initData();

    }

    BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("USER_STATUS")){
                tv_userState.setText(intent.getStringExtra("useStatus"));
                name.setText(intent.getStringExtra("name"));
                loader.displayImage(intent.getStringExtra("phoneUrl"), head_img);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go:
                Intent intent = new Intent(this.getActivity(), EditActivity.class);
                intent.putExtra(IntentString.INTENT_TITLE, SharedPreferencesUtil.getInfo(getActivity(), "nickName"));
                intent.putExtra(IntentString.CUSTOMER_ID, SharedPreferencesUtil.getInfo(getActivity(), "customerId"));
                intent.putExtra(IntentString.NICK_NAME, SharedPreferencesUtil.getInfo(getActivity(), "nickName"));
                intent.putExtra(IntentString.BRITHDAY,SharedPreferencesUtil.getInfo(getActivity(),"birthday"));
                intent.putExtra(IntentString.SEX,SharedPreferencesUtil.getInfo(getActivity(), "sex"));
                intent.putExtra(IntentString.ADDRESS,SharedPreferencesUtil.getInfo(getActivity(), "address"));
                intent.putExtra(IntentString.PHONO_URL,SharedPreferencesUtil.getInfo(getActivity(), "photoUrl"));
                intent.putExtra(IntentString.PHONE,SharedPreferencesUtil.getInfo(getActivity(), "phone"));
//                startActivity(intent);
                startActivityForResult(intent,2);
                break;
            case R.id.pay:

                 if(SharedPreferencesUtil.getInfo(getActivity(),"orderMsg")==null || SharedPreferencesUtil.getInfo(getActivity(),"orderMsg")==""){
                     startActivityForResult(new Intent(this.getActivity(), PayActivity.class),1001);
                 }else{
                    startActivityForResult(new Intent(this.getActivity(), PayContentActivity.class),1001);
                }

                break;
            case R.id.cancellation:
                SharedPreferencesUtil.saveUserLoginInfo(getActivity(), "", "", "", "",  "","", "","","", "", 0,"","");
                startActivity(new Intent(this.getActivity(), LoginActivity.class));
                UserDao userDao=new UserDaoImpl(getActivity());
                userDao.deleteAllUserInfo();
                getActivity().finish();
                break;
            case R.id.monitor_list:
                startActivity(new Intent(this.getActivity(), MonitorActivity.class));
                break;
            /*case R.id.site_place:

                startActivityForResult(new Intent(this.getActivity(), SitePlaceActivity.class),111);
                break;*/
            case R.id.msg_center_relay:
                startActivity(new Intent(getActivity(), MessageActivity.class));
                break;
            case R.id.center_message:
                startActivity(new Intent(getActivity(), MessageActivity.class));
                break;
            case R.id.mycode:

                startActivity(new Intent(getActivity(), MyScanActivity.class));
                break;
            case R.id.about:
               /* AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setTitle("提示")
                        .setMessage("此功能暂未开放")
                        .setPositiveButton("确定",null)
                        .show();*/
                startActivity(new Intent(getActivity(), AboutVersionActivity.class));

                break;
        }
    }


    public void startUserInfoThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String,String> hashMap= SharedPreferencesUtil.getUserLoginInfo(getActivity());
//              Log.e("0011","自动登录手机号=="+hashMap.get("phone")+"验证码=="+hashMap.get("verification"));
                String result=  requestHelp.loginIn(hashMap.get("phone"),hashMap.get("verification"));
                if(jsonHelp.dealLoginJson(result,getActivity(),hashMap.get("verification"))){
                    handler.sendEmptyMessage(MessageInfoUtil.LOGIN);
                }
            }
        }).start();
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    try {
                        JSONObject jsonObject=new JSONObject(msg.obj.toString());
                        int message_count=jsonObject.optInt("mc");
                        if(message_count!=0){
                            mc.setVisibility(View.VISIBLE);
                        }else{
                            mc.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    initData();
                    break;
            }

        }
    };
}
