package com.jiedu.project.lovefamily.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;
import com.jiedu.project.lovefamily.utils.MySelfDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PayContentActivity extends BaseActivity {


    private TextView tv_paycontent;
    private TextView cancle_pay;
    private RequestHelp requestHelp=new RequestHelp();
    private ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_content);


        tv_paycontent= (TextView) findViewById(R.id.tv_paycontent);
        cancle_pay= (TextView) findViewById(R.id.cancle_pay);
        tv_paycontent.setText(SharedPreferencesUtil.getInfo(PayContentActivity.this,"orderMsg"));
        back= (ImageView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cancle_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HashMap map=new HashMap<String, String>();
                        map.put("customerId", SharedPreferencesUtil.getInfo(PayContentActivity.this,"customerId"));
                        map.put("phone", SharedPreferencesUtil.getInfo(PayContentActivity.this,"phone"));
                        String result=requestHelp.requestPost(RequestHelp.CANCEL_PAY,map);
                        Log.i("TAG",result+"");
                        if(result!=null){
                            try {
                                JSONObject jsonObject=new JSONObject(result);
                                String ok=jsonObject.optString("ok");
                                if(ok.equals("true")){
                                    handler.sendEmptyMessage(1);
//                                    setResult(1001);
//                                    finish();
                                }else{
                                    handler.sendEmptyMessage(2);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                }).start();

            }
        });




    }
    private MySelfDialog mySelfDialog;

    android.os.Handler handler=new android.os.Handler(){
       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           switch (msg.what){
               case 1:
                   //Toast.makeText(getApplicationContext(),"退订成功",Toast.LENGTH_SHORT).show();
                  /* mySelfDialog=new MySelfDialog(PayContentActivity.this);
                   mySelfDialog.setPositiveButton(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                            mySelfDialog.dismiss();
                            setResult(1001);
                            finish();
                       }
                   });*/
                   AlertDialog.Builder builder=new AlertDialog.Builder(PayContentActivity.this);
                   builder.setMessage("支付成功");
                   builder.setTitle("提示");
                   builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           dialogInterface.dismiss();
                           setResult(1001);
                           finish();
                       }
                   });
                   builder.create().show();


                   break;
               case 2:
                   Toast.makeText(getApplicationContext(),"退订失败",Toast.LENGTH_LONG).show();
                   break;
           }

       }
   };
}
