package com.jiedu.project.lovefamily.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.adapter.InvitationCodeAdapter;
import com.jiedu.project.lovefamily.json.JsonHelp;
import com.jiedu.project.lovefamily.message.MessageInfoUtil;
import com.jiedu.project.lovefamily.mode.InvitationCode;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/21.
 * 邀请码fragment
 */
public class CodeFragment extends Fragment implements View.OnClickListener {
    View view;
    EditText invitation_code;
    Button submit;
    RequestHelp requestHelp;
    JsonHelp jsonHelp;
    ImageView back;
    TextView no_data;
    PullToRefreshListView code_listview;
    ArrayList<InvitationCode>list=new ArrayList<InvitationCode>();
    InvitationCodeAdapter adapter;
    int index;
    private TextView tv_user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_code,null);
        initUtil();
        initView();
        startGetMonitorList();
        return view;
    }
    void initUtil(){
        requestHelp=new RequestHelp();
        jsonHelp=new JsonHelp();

    }
    void initView(){
        invitation_code= (EditText) view.findViewById(R.id.invitation_code);
        submit= (Button) view.findViewById(R.id.submit);
        back=(ImageView)view.findViewById(R.id.back);
        no_data=(TextView)view.findViewById(R.id.no_data);
        code_listview=(PullToRefreshListView)view.findViewById(R.id.code_listview);
        submit.setOnClickListener(this);
        back.setOnClickListener(this);

        tv_user= (TextView) view.findViewById(R.id.tv_user);

        code_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                startGetMonitorList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.submit:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        Log.e("0011","邀请码id="+SharedPreferencesUtil.getInfo(getActivity(), "customerId"));
//                        Log.e("0011", "邀请码=" + invitation_code.getText().toString());
                        String inviteCode=invitation_code.getText().toString();
                        String monitoredId=SharedPreferencesUtil.getInfo(getActivity(), "customerId");
                              String result=  requestHelp.submitInvitationCode(inviteCode, monitoredId);
                        jsonHelp.dealSubmitInvitationCode(result,handler);
                    }
                }).start();
                break;
            case R.id.back:
                HomeFragment02 homeFragment02=new HomeFragment02();
                getFragmentManager().beginTransaction().replace(R.id.fragment_layout,homeFragment02).commit();
                break;
        }
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MessageInfoUtil.SUBMIT_INVITATION_CODE:
                    //刷新列表
                    startGetMonitorList();
                    break;
                case MessageInfoUtil.SUBMIT_INVITATION_CODE_FAIL:
                    Toast.makeText(getActivity(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    break;
                case MessageInfoUtil.GET_MONITOR_LIST:
                    code_listview.onRefreshComplete();
                    adapter=new InvitationCodeAdapter(getActivity(),list,handler);
                    code_listview.setAdapter(adapter);
                    if(list.size()==0){
                        no_data.setVisibility(View.VISIBLE);
                        tv_user.setVisibility(View.GONE);

                    }else{
                        no_data.setVisibility(View.GONE);
                        tv_user.setVisibility(View.VISIBLE);
                    }
                    break;
                case MessageInfoUtil.GET_MONITOR_LIST_FAIL:
                    code_listview.onRefreshComplete();
                    break;
                case MessageInfoUtil.DELETE_MONITOR:
                    //刷新列表
                    startGetMonitorList();
                    break;
                case MessageInfoUtil.DELETE_MONITOR_FAIL:
                    Toast.makeText(getActivity(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    break;
                case MessageInfoUtil.DELETE:
                    index=msg.arg1;
//                    Log.e("0011","点击删除"+index);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                           String result= requestHelp.deleteMonitor(list.get(index).customerId,SharedPreferencesUtil.getInfo(getActivity(),"customerId"));
                            jsonHelp.dealDeleteMonitor(result,handler);
                        }
                    }).start();

                    break;
            }

        }
    };

//获取监控人列表
    private void startGetMonitorList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("0011","请求监控人列表参数"+SharedPreferencesUtil.getInfo(getActivity(), "customerId"));
              String result=  requestHelp.getMonitorList(SharedPreferencesUtil.getInfo(getActivity(), "customerId"));
               if(TextUtils.isEmpty(result)){
                  handler.sendEmptyMessage(MessageInfoUtil.GET_MONITOR_LIST_FAIL);
               }else{
                   jsonHelp.dealGetMonitorList(result, handler, list);
                   Log.e("0011", "获取监控人列表" + result + "\t列表长度" + list.size());
               }

            }
        }).start();
    }
}
