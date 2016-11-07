package com.jiedu.project.lovefamily.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jiedu.project.lovefamily.BackHandledFragment;
import com.jiedu.project.lovefamily.BackHandledInterface;
import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.fragment.CodeFragment;
import com.jiedu.project.lovefamily.fragment.HelpFragment;
import com.jiedu.project.lovefamily.fragment.HomeFragment02;
import com.jiedu.project.lovefamily.fragment.MyFragment;
import com.jiedu.project.lovefamily.fragment.MyScanFragment;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.popupwindow.SharePopupwindowUtil;
import com.jiedu.project.lovefamily.service.Service1;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;
import com.jiedu.project.lovefamily.sqlite.UserDao;
import com.jiedu.project.lovefamily.sqlite.UserDaoImpl;
import com.jiedu.project.lovefamily.stringutil.IntentString;
import com.jiedu.project.lovefamily.utils.CustomDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends FragmentActivity  implements BackHandledInterface {
    private BackHandledFragment mBackHandedFragment;
    private boolean hadIntercept;
    ImageLoader loader;
    SharePopupwindowUtil sharePopupwindowUtil;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    HomeFragment02 homeFragment02;
    HelpFragment helpFragment;
    private ListView lv_navigation;
    private ImageView left_touxiang;
    private TextView left_name,left_data;
    private int[] imgIds = new int[] { R.mipmap.saoyisao, R.mipmap.fenxiang, R.mipmap.vip, R.mipmap.bangzhu,R.mipmap.guanyu,R.mipmap.hdzx };
    private String[] title = new String[] {"我的二维码","共享关系", "开通会员", "使用帮助","关于我们","活动中心" };
    private RelativeLayout presonal;
    private Fragment mContent;
    private Context context;
    private  static DrawerLayout mdrawerlayout;
    private LinearLayout drawerview;
    private Button cancellation;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context=this;
        Intent intent=new Intent(this, Service1.class);
        startService(intent);
       // Notification notification=new Notification(R.drawable.logo,getText(R.string.notification),System.currentTimeMillis());

        //判断式，确定装置的版本是 4.4 (KitKat) 以上来进行容错
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT){
            Window window=getWindow();
            // Translucent status bar，设定状态列 (Status Bar)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0).setFitsSystemWindows(true);

        }

        if(savedInstanceState!=null){
            mContent = getSupportFragmentManager().getFragment(
                    savedInstanceState, "mContent");
        }
        mContent=new HomeFragment02();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,mContent).commit();

        initView();


            //版本检测
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map=new HashMap<String,String>();
                map.put("vn",getVersionName(HomeActivity.this)+"");
                String json=requestHelp.requestPost(RequestHelp.GET_LAST_VERSION,map);
                Log.e("TAG",json);
                Message msg= Message.obtain();
                msg.obj=json;
                msg.what=2004;
                handler.sendMessage(msg);
            }
        }).start();
    }





    public boolean buy(){
        String isExpired=SharedPreferencesUtil.getInfo(context,"isExpired");
        if(isExpired.equals(0+"")){

        }else{
            final CustomDialog cd=new CustomDialog(context);
            cd.setTitle("温馨提示");
            cd.setMessage("你的免费试用已到期!");
            cd.setPositiveButton("订购", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cd.dismiss();
                    if(SharedPreferencesUtil.getInfo(context,"orderMsg")==null || SharedPreferencesUtil.getInfo(context,"orderMsg")==""){
                        startActivityForResult(new Intent(context, PayActivity.class),1001);
                    }else{
                        startActivityForResult(new Intent(context, PayContentActivity.class),1001);
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



    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case 2004:
                    String json=msg.obj.toString();
                    try {
                        JSONObject jsonObject=new JSONObject(json);
                        String ok=jsonObject.optString("ok");
                        final String version=jsonObject.optString("msg");
                        if(ok.equals("true")){
                            AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
                            builder.setTitle("版本更新")
                                    .setMessage("是否更新最新版本")
                                    .setCancelable(false)
                                    .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Uri uri=Uri.parse(version);
                                            Intent intent1=new Intent(Intent.ACTION_VIEW,uri);
                                            startActivity(intent1);
                                        }
                                    })
                                    .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            /*SharedPreferencesUtil.saveUserLoginInfo(getActivity(), "", "", "", "",  "","", "","","", "", 0,"","");
                                            startActivity(new Intent(getActivity(), LoginActivity.class));*/
                                            HomeActivity.this.finish();
                                        }
                                    })
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(HomeActivity.this,Service1.class));
    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }


    void initView(){
        cancellation= (Button) findViewById(R.id.cancellation);
        loader = ImageLoader.getInstance().getInstance();
        drawerview= (LinearLayout) findViewById(R.id.drawerView);
        mdrawerlayout= (DrawerLayout) findViewById(R.id.mdrawerlayout);
        lv_navigation= (ListView)findViewById(R.id.lv_navigation);
        left_touxiang= (ImageView) findViewById(R.id.civ);
        left_name= (TextView) findViewById(R.id.username);
        left_data= (TextView) findViewById(R.id.date);
        presonal= (RelativeLayout) findViewById(R.id.presonal);
        sharePopupwindowUtil=new SharePopupwindowUtil();
        homeFragment02=new HomeFragment02();
        List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < imgIds.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("imgId", imgIds[i]);
            map.put("mingcheng", title[i]);
            list1.add(map);
            SimpleAdapter adapter = new SimpleAdapter(context, list1, R.layout.lv_navigation_item, new String[]{"imgId", "mingcheng"},
                    new int[]{R.id.iv_left_item, R.id.left_item_tv});
            lv_navigation.setAdapter(adapter);
            lv_navigation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selectItem(i);
                }
            });
            loadpersonalData();
            cancellation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferencesUtil.saveUserLoginInfo(HomeActivity.this, "", "", "", "",  "","", "","","", "", 0,"","");
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    UserDao userDao=new UserDaoImpl(HomeActivity.this);
                    userDao.deleteAllUserInfo();
                    finish();
                }
            });
            presonal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  PersonalFragment personalFragment = new PersonalFragment();
                    Intent intent=new Intent(HomeActivity.this,EditActivity.class);
                    Bundle person = new Bundle();
                    person.putString(IntentString.INTENT_TITLE, SharedPreferencesUtil.getInfo(HomeActivity.this, "nickTitle"));
                    person.putString(IntentString.CUSTOMER_ID, SharedPreferencesUtil.getInfo(HomeActivity.this, "customerId"));
                    person.putString(IntentString.NICK_NAME, SharedPreferencesUtil.getInfo(HomeActivity.this, "nickName"));
                    person.putString(IntentString.BRITHDAY, SharedPreferencesUtil.getInfo(HomeActivity.this, "birthday"));
                    person.putString(IntentString.SEX, SharedPreferencesUtil.getInfo(HomeActivity.this, "sex"));
                    person.putString(IntentString.ADDRESS, SharedPreferencesUtil.getInfo(HomeActivity.this, "address"));
                    person.putString(IntentString.PHONO_URL, SharedPreferencesUtil.getInfo(HomeActivity.this, "photoUrl"));
                    person.putString(IntentString.PHONE, SharedPreferencesUtil.getInfo(HomeActivity.this, "phone"));
                    intent.putExtras(person);
                    startActivity(intent);
//                    personalFragment.setArguments(person);
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, personalFragment).commit();
                    mdrawerlayout.closeDrawer(drawerview);
                }
            });


        }}

    private void loadpersonalData() {
        if (TextUtils.isEmpty(SharedPreferencesUtil.getInfo(HomeActivity.this, "photoUrl"))) {
//            Log.e("0011","设置默认的头像");
            loader.displayImage("drawable://" + R.drawable.default_head, left_touxiang);
        } else {
//            Log.e("0011","设置修改的头像");
            loader.displayImage(SharedPreferencesUtil.getInfo(HomeActivity.this, "photoUrl"), left_touxiang);
        }

        if (TextUtils.isEmpty(SharedPreferencesUtil.getInfo(HomeActivity.this, "nickName"))) {
            left_name.setText("user" + SharedPreferencesUtil.getInfo(HomeActivity.this, "customerId"));
        } else {
            left_name.setText(SharedPreferencesUtil.getInfo(HomeActivity.this, "nickName"));
        }
    }

    @Override
    protected void onResume() {


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HashMap map=new HashMap<String,String>();
//                map.put("customerId",SharedPreferencesUtil.getInfo(HomeActivity.this,"customerId"));
//                String json=requestHelp.requestPost(RequestHelp.GET_USER_INFO,map);
//                Log.i("TAG",json);
//                if(json!=null){
//                    try {
//                        JSONObject jsonObject = new JSONObject(json);
//                        JSONObject data = new JSONObject(jsonObject.optString("data"));
//                        SharedPreferencesUtil.saveUserLoginInfo(HomeActivity.this, data.optString("customerId"), data.optString("phone"),data.optString("nickName") ,data.optString("sex"),"",data.optString("birthday").replace("00:00:00",""),data.optString("address"),data.optString("inviteCode"), "", data.optString("needUploadPostion"), data.optInt("frequency"),data.optString("useStatus"),data.optString("orderMsg"));
//
////                        Intent intent=new Intent();
////                        intent.setAction("Constant.HOME.ACTION");
////                        intent.addCategory(Intent.CATEGORY_DEFAULT);
////                        intent.putExtra("")
//                        MyFragment myFragment=new MyFragment();
//                        myFragment.gg(data.optString("orderMsg"));
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }).start();

        super.onResume();
    }

    RequestHelp requestHelp=new RequestHelp();
    HashMap<String,String> hashMap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(resultCode==2){
//                Log.e("0011","编辑完成，刷新数据");18512509356
               // homeFragment02.startGetMemberListThread();
            }else if(resultCode==3){
                //myFragment.startUserInfoThread();
//                Log.e("0011","编辑个人数据完成，刷新数据");
            }
        if(resultCode==1002){
            Intent intent2=new Intent();
            intent2.addCategory(Intent.CATEGORY_DEFAULT);
            intent2.setAction("com.update.family.member");
            intent2.putExtra("flag","update");
            sendBroadcast(intent2);
        }

        if(resultCode==1001){
            hashMap= SharedPreferencesUtil.getUserLoginInfo(this);
            new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map=new HashMap<String,String>();
                map.put("customerId",SharedPreferencesUtil.getInfo(HomeActivity.this,"customerId"));
                String json=requestHelp.requestPost(RequestHelp.GET_USER_INFO,map);
                Log.i("TAG",json);
                if(json!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONObject data = new JSONObject(jsonObject.optString("data"));
                        String phoneUrl="";
                        if(!TextUtils.isEmpty(data.optString("portrait"))){
                            phoneUrl=RequestHelp.HTTP_HEAD + RequestHelp.HTTP_ACTION + "/wttp_portrait/" + data.optString("portrait");
                        }
                        String verification=hashMap.get("verification");
                       // SharedPreferencesUtil.saveUserLoginInfo(HomeActivity.this, data.optString("customerId"), data.optString("phone"),data.optString("nickName") ,data.optString("sex"),phoneUrl,data.optString("birthday").replace("00:00:00",""),data.optString("address"),data.optString("inviteCode"), verification, data.optString("needUploadPostion"), data.optInt("frequency"),data.optString("useStatus"),data.optString("orderMsg"));
                        Intent intent2=new Intent();
                        intent2.setAction("receiverFrequency");
                        intent2.addCategory(Intent.CATEGORY_DEFAULT);
                        intent2.putExtra("frequency", data.optInt("frequency"));
                        sendBroadcast(intent2);

                        SharedPreferencesUtil.saveUserLoginInfo2(HomeActivity.this, data.optString("customerId"), data.optString("phone"),
                                data.optString("nickName") ,data.optString("sex"),phoneUrl,data.optString("birthday").replace("00:00:00",""),
                                data.optString("address"),data.optString("inviteCode"), verification, data.optString("needUploadPostion"),
                                data.optInt("frequency"),data.optString("useStatus"),data.optString("orderMsg"),data.optString("isUploadLocation"),data.optString("ctccPhone"),data.optString("isExpired"));

                        Intent intent=new Intent();
                        intent.setAction("USER_STATUS");
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.putExtra("useStatus",data.optString("useStatus"));
                        intent.putExtra("name",data.optString("nickName"));
                        intent.putExtra("phoneUrl",phoneUrl);
                        sendBroadcast(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
        }
    }
//    对正在运行的服务进行遍历
    public static boolean isServiceWorked(Context context, String serviceName) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }



    private void selectItem(int position) {
        switch (position){
            case 0:
                Fragment MyScanFragment=new MyScanFragment();
                Bundle args = new Bundle();
                args.putInt("position", position);
                MyScanFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,MyScanFragment).commit();
                lv_navigation.setItemChecked(position, true);
                setTitle(title[position]);
                 mdrawerlayout.closeDrawer(drawerview);

                break;
            case 1:
                CodeFragment codeFragment = new CodeFragment();
                Bundle args1 = new Bundle();
                args1.putInt("position", position);
                codeFragment.setArguments(args1);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,codeFragment).commit();
                lv_navigation.setItemChecked(position, true);
                setTitle(title[position]);
                mdrawerlayout.closeDrawer(drawerview);

                break;
            case 2:
                KaiTongHYFragment kaiTongHYFragment = new KaiTongHYFragment();
                Bundle args2 = new Bundle();
                args2.putInt("position", position);
                kaiTongHYFragment.setArguments(args2);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,kaiTongHYFragment).commit();
                lv_navigation.setItemChecked(position, true);
                mdrawerlayout.closeDrawer(drawerview);

                break;
            case 3:
                HelpFragment helpFragment = new HelpFragment();
                Bundle args3 = new Bundle();
                args3.putInt("position", position);
                helpFragment.setArguments(args3);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,helpFragment).commit();
                lv_navigation.setItemChecked(position, true);
                mdrawerlayout.closeDrawer(drawerview);

                break;
            case 4:
                AboutFragment aboutFragment = new AboutFragment();
                Bundle args4 = new Bundle();
                args4.putInt("position", position);
                aboutFragment.setArguments(args4);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,aboutFragment).commit();
                lv_navigation.setItemChecked(position, true);
                mdrawerlayout.closeDrawer(drawerview);
                break;

        }

    }


    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;

    }

    public static void openDrawer() {
        mdrawerlayout.openDrawer(Gravity.LEFT);
    }


    private long now;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mdrawerlayout.isDrawerOpen(findViewById(R.id.drawerView))){
            mdrawerlayout.closeDrawers();
        }else{
            if(System.currentTimeMillis()-now>800){
                Toast.makeText(getApplicationContext(),"再按一次退出",Toast.LENGTH_SHORT).show();
                now= System.currentTimeMillis();
            }else{
                finish();
            }
        }
        return true;

    }


}
