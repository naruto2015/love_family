package com.jiedu.project.lovefamily.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.adapter.PayStyleAdapter;
import com.jiedu.project.lovefamily.adapter.ServiceTypeAdapter;
import com.jiedu.project.lovefamily.json.JsonHelp;
import com.jiedu.project.lovefamily.message.MessageInfoUtil;
import com.jiedu.project.lovefamily.mode.MyPayType;
import com.jiedu.project.lovefamily.mode.PayStyleMode;
import com.jiedu.project.lovefamily.mode.ServiceMode;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;
import com.jiedu.project.lovefamily.utils.CustomDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PayActivity extends BaseActivity implements View.OnClickListener {

   Button submitPay;
    ImageView back;
    ListView style_choose;
    ListView pay_choose;

    TextView pay_num;
    RequestHelp requestHelp;
    JsonHelp jsonHelp;
    private ArrayList<PayStyleMode>list=new ArrayList<>();
    PayStyleAdapter payStyleAdapter;
    ServiceTypeAdapter serviceTypeAdapter;


    private List<MyPayType> myPayTypeList;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        requestHelp=new RequestHelp();
        jsonHelp=new JsonHelp();
        initView();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String result= requestHelp.requestPost(RequestHelp.GET_PAY_INFO,new  HashMap<String, Object>());
               jsonHelp.drealPayInfo(result,list,handler);

  //              myPayTypeList=MyPayType.getMyPayTypeList(result);

                Log.e("0011","支付界面请求结果"+result);
            }
        }).start();
    }
void initView(){
    submitPay=(Button)findViewById(R.id.submit_pay);
    back=(ImageView)findViewById(R.id.back);
    style_choose= (ListView) findViewById(R.id.style_choose);
    pay_choose=(ListView)findViewById(R.id.pay_choose);
    pay_num= (TextView) findViewById(R.id.pay_num);
    submitPay.setOnClickListener(this);
    back.setOnClickListener(this);
}
    HashMap map;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_pay:

                if(SharedPreferencesUtil.getInfo(PayActivity.this,"ctccPhone").equals("非电信号码")){
                    //Toast.makeText(PayActivity.this,"非电信号码不能话费支付!",Toast.LENGTH_SHORT).show();

                    for(int i=0;i<list.size();i++){
                        if(list.get(i).getName().equals("话费代收")){
                            Intent intent=new Intent(PayActivity.this,PhoneCollectionActivity.class);
                            intent.putExtra("busiTypeId",list.get(i).getList().get(0).getId());
                            intent.putExtra("typeId",list.get(i).getId());
                            intent.putExtra("busiTypeCode",list.get(i).getList().get(0).getCode());

                            startActivity(intent);

                        }
                    }


                    return;
                }
                for(int i=0;i<list.size();i++){
                    if(list.get(i).getIsChecked()){
                        if(list.get(i).getName().equals("话费代收")){
                            map=new HashMap<String,String>();
                            map.put("customerId", SharedPreferencesUtil.getInfo(PayActivity.this,"customerId"));
                            map.put("phone", SharedPreferencesUtil.getInfo(PayActivity.this,"phone"));
                            map.put("busiTypeId", list.get(i).getList().get(0).getId());
                            map.put("typeId",list.get(i).getId());
                            map.put("busiTypeCode", list.get(i).getList().get(0).getCode());

                            CustomDialog cd=new CustomDialog(this);
                            cd.setTitle("温馨提示");
                            cd.setMessage("是否确认订购？");
                            cd.setPositiveButton("订购",new View.OnClickListener(){

                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(PayActivity.this, "订购成功", Toast.LENGTH_SHORT).show();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String result=requestHelp.requestPost(RequestHelp.GET_MY_PAY,map);
                                            Log.i("TAG",result+"");
                                            payhandler.sendEmptyMessage(2);

                                        }
                                    }).start();
                                }
                            });

                            cd.setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            });
                        }

                        if(list.get(i).getName().equals("微信")){
                          payhandler.sendEmptyMessage(1);
                        }

                        if(list.get(i).getName().equals("支付宝")){
                            payhandler.sendEmptyMessage(1);
                        }
                        break;
                    }

                }
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private Handler payhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Toast.makeText(PayActivity.this,"暂无此业务！",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    setResult(1001);
                    finish();
                    break;
            }



        }
    };


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch ( msg.what){
                case MessageInfoUtil.GET_PAY_INFO:

                    list.get(0).setIsChecked(true);
                    payStyleAdapter=new PayStyleAdapter(PayActivity.this,list,handler);
                    pay_choose.setAdapter(payStyleAdapter);
                    setListNoCheck(list.get(0).getList());
                    list.get(0).getList().get(0).setIsCheck(true);
                    serviceTypeAdapter=new ServiceTypeAdapter(PayActivity.this,list.get(0).getList(),handler);
                    style_choose.setAdapter(serviceTypeAdapter);
                    pay_num.setText(list.get(0).getList().get(0).getBusiExpense());
                    break;
                case MessageInfoUtil.SET_SERVICE_DATA:
                    setListNoCheck();
                    list.get(msg.arg1).setIsChecked(true);
                    payStyleAdapter.notifyDataSetChanged();
                    setListNoCheck(list.get(msg.arg1).getList());
                    list.get(msg.arg1).getList().get(0).setIsCheck(true);
                    serviceTypeAdapter=new ServiceTypeAdapter(PayActivity.this,list.get(msg.arg1).getList(),handler);
                    style_choose.setAdapter(serviceTypeAdapter);
                    pay_num.setText(list.get(msg.arg1).getList().get(0).getBusiExpense());
                    break;
                case MessageInfoUtil.SET_PAY_NUM:
                    setListNoCheck( serviceTypeAdapter.getList());
                    serviceTypeAdapter.getList().get(msg.arg1).setIsCheck(true);
                    serviceTypeAdapter.notifyDataSetChanged();
                    pay_num.setText(msg.obj.toString());
                    break;
            }

        }
    };


    private void setListNoCheck(){
        for (int i=0;i<list.size();i++){
            list.get(i).setIsChecked(false);
        }
    }
    private void setListNoCheck(ArrayList<ServiceMode> list){
        for (int i=0;i<list.size();i++){
            list.get(i).setIsCheck(false);
        }
    }
}
