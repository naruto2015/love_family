package com.jiedu.project.lovefamily.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.adapter.MessageAdapter;
import com.jiedu.project.lovefamily.application.MyApplication;
import com.jiedu.project.lovefamily.mode.MessageModel;
import com.jiedu.project.lovefamily.net.RequestHelp;

import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;
import com.jiedu.project.lovefamily.volley.VolleyInterface;
import com.jiedu.project.lovefamily.volley.VolleyRequest;
import com.linkage.gson.JsonArray;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MessageActivity extends Activity implements View.OnClickListener{
    private LinearLayout mViewPopupwidow;
    private TextView deleteall;
    private TextView isreaded;
    private ImageView back;
    private PullToRefreshListView listView;
    private MessageAdapter adapter;
    private List<MessageModel> list;
    int page = 1;
    private PopupWindow mPopupWindow;
    private RequestHelp help;
    private int  checkNum;//选中个数
    private TextView msg_del_all;//消息全部删除
    private static Boolean isMsgDelAll=true;//默认全选删除，点中之后取消
    private LinearLayout linear;
    private String edittype="notedit";
    private TextView chooseall;
    public boolean isChoosed= false;
    private  static String messageId1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initView();
        //loadData();
        //volley_Post();
        // volley_get();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

   /* private void volley_get() {
        String cus=SharedPreferencesUtil.getInfo(MessageActivity.this,"customerId");
        String url=RequestHelp.GET_MESSAGE+"?page="+page+"&pagesize=10&"+"customerId="+cus;

        //String url="http://192.168.0.152:8080/wttp/messages/query.do?page=1&pagesize=10&customerId=4";

        StringRequest request=new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listView.onRefreshComplete();
                        Message msg=Message.obtain();
                        msg.obj=response;
                        msg.what=2;
                        handler.sendMessage(msg);
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                listView.onRefreshComplete();
            }

        });
        //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
        request.setTag("abcGet");
        MyApplication.getHttpQueues().add(request);

    }*/


    private void loadData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap<String, String>();
              /*  map.put("page",page+"");
                map.put("pagesize",10+"");
                map.put("customerId", SharedPreferencesUtil.getInfo(MessageActivity.this,"customerId")+"");
                String cus=SharedPreferencesUtil.getInfo(MessageActivity.this,"customerId");*/
                String cus = SharedPreferencesUtil.getInfo(MessageActivity.this, "customerId");
                String url = RequestHelp.GET_MESSAGE + "?page=" + page + "&pagesize=10&" + "customerId=" + cus;
                String json = help.requestPost(url, map);
                Log.e("0012", "json" + json);
                ++page;

                if (json != null) {
                    Message msg = Message.obtain();
                    msg.obj = json;
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    listView.onRefreshComplete();
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        JSONArray jsonArray = new JSONArray(jsonObject.optString("list"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            MessageModel messageModel = new MessageModel();
                            JSONObject monitorJson = jsonArray.getJSONObject(i);
                            messageModel.messageTitle = monitorJson.optString("messageTitle");
                            messageModel.messageContent = monitorJson.optString("messageContent");
                            messageModel.isReaded = monitorJson.optString("isReaded");
                            messageModel.messageId = monitorJson.optString("messageId");
                            //获取消息时间
                            messageModel.msgTime=monitorJson.optString("sendTime");
                            list.add(messageModel);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    HashMap map = new HashMap<String, String>();
                    String url = RequestHelp.Del_MESSAGE + messageId1;
                    Log.e("1102", "------------->" + url);
                    String json = help.requestPost(url, map);
                    Log.e("1102", "------------->" + json);
                    break;
            }
        }
    };




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.msg_del_all:
                if (isMsgDelAll){
                    msg_del_all.setText("取消");
                    mViewPopupwidow.setVisibility(View.VISIBLE);
                    chooseall.setVisibility(View.VISIBLE);
                    back.setVisibility(View.GONE);
                    edittype="edit";
                    adapter = new MessageAdapter(MessageActivity.this, list,listView,edittype);
                    listView.setAdapter(adapter);
                }else {
                    msg_del_all.setText("编辑");
                    mViewPopupwidow.setVisibility(View.GONE);
                    chooseall.setVisibility(View.GONE);
                    back.setVisibility(View.VISIBLE);
                    edittype="notedit";
                    adapter = new MessageAdapter(MessageActivity.this, list,listView,edittype);
                    listView.setAdapter(adapter);
                }
                adapter.notifyDataSetChanged();
                isMsgDelAll=!isMsgDelAll;


                break;
            case R.id.delete_all:
                Map<Integer, Boolean> map = adapter.getCheckMap();
                int count = adapter.getCount();
                for (int i = 0; i < count; i++) {
                    final int position = i - (count - adapter.getCount());
                    if (map.get(i) != null && map.get(i)) {
                        MessageModel item = (MessageModel) adapter.getItem(position);
                        if (item.isCanRemove()) {
                            adapter.getCheckMap().remove(i);
                            adapter.remove(position);
                               messageId1 = item.messageId;
                                Message msg = Message.obtain();
                                msg.obj = messageId1;
                                msg.what=3;
                                handler.sendMessage(msg);
                        } else {
                            map.put(position, false);
                        }

                    }
                    adapter.notifyDataSetInvalidated();
                }
                break;
            case  R.id.israded:
                int count1 = adapter.getCount();
                for (int i = 0; i < count1; i++) {
                    final int position1 = i - (count1 - adapter.getCount());
                    adapter.SetIsreaded(position1);
                }


//                adapter = new MessageAdapter(MessageActivity.this, list,listView,edittype);
//                listView.setAdapter(adapter);

                break;

            case R.id.choose_all:
                  if(chooseall.getText().equals("全选")){
                      adapter.configCheckMap(true);
                      adapter.notifyDataSetChanged();
                      chooseall.setText("全不选");
//                      edittype="chooseallcheckbox";
//                      adapter = new MessageAdapter(MessageActivity.this, list,listView,edittype);
//                      listView.setAdapter(adapter);
                  }else{
                      adapter.configCheckMap(false);
                      adapter.notifyDataSetChanged();
                      chooseall.setText("全选");
//                      edittype="unchooseallcheckbox";
//                      adapter = new MessageAdapter(MessageActivity.this, list,listView,edittype);
//                      listView.setAdapter(adapter);
                  }

                break;
            case R.id.back:
                finish();
                break;
        }
    }


    private void initView() {
        mViewPopupwidow= (LinearLayout) findViewById(R.id.popwindow);
//        mViewPopupwidow = ((LayoutInflater) getBaseContext()
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
//                R.layout.pop_sure, null);
        deleteall= (TextView) mViewPopupwidow.findViewById(R.id.delete_all);
        isreaded= (TextView) mViewPopupwidow.findViewById(R.id.israded);
        help = new RequestHelp();
        list = new ArrayList<MessageModel>();
        back = (ImageView) findViewById(R.id.back);
        listView = (PullToRefreshListView) findViewById(R.id.listview);
        chooseall= (TextView) findViewById(R.id.choose_all);
        chooseall.setOnClickListener(this);
        deleteall.setOnClickListener(this);
        isreaded.setOnClickListener(this);
        back.setOnClickListener(this);

        msg_del_all= (TextView) findViewById(R.id.msg_del_all);
//        linear= (LinearLayout) findViewById(R.id.linear);

        adapter = new MessageAdapter(MessageActivity.this, list,listView,edittype);
        listView.setAdapter(adapter);
        msg_del_all.setOnClickListener(this);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                Intent intent = new Intent(MessageActivity.this, MessageContentActivity.class);
//                list.get(i - 1).isReaded = 0 + "";
//                adapter.notifyDataSetChanged();
//                intent.putExtra("content", list.get(i - 1).messageContent);
//                intent.putExtra("messageId", list.get(i - 1).messageId);
//                startActivity(intent);

            }
        });
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                list.clear();
                //volley_get();
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                ++page;
                //volley_get();
                loadData();
            }
        });

        //  initData();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
        return super.onTouchEvent(event);
    }

    private void RefreshUpData(String currentItem) {
        final String item = currentItem;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap<String, String>();
                String url = RequestHelp.Del_MESSAGE + item;
                Log.e("1102", "------------->" + url);
                String json = help.requestPost(url, map);
                Log.e("1102", "------------->" + json);
            }
        }).start();
    }
}
