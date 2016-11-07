package com.jiedu.project.lovefamily.volley;

/**
 * Created by naruto on 2016/2/25.
 */




import java.util.Map;

public class VolleyRequest {


    /*public static StringRequest stringRequest;
    public static Context context;


    public static void RequestGet(Context mContext,String url,String tag,
                                  VolleyInterface vif){

        MyApplication.getHttpQueues().cancelAll(tag);
        stringRequest=new StringRequest(Method.GET, url, vif.loadingListener(),vif.errorListener());
        stringRequest.setTag(tag);
        MyApplication.getHttpQueues().add(stringRequest);
        MyApplication.getHttpQueues().start();
    }



    public static void RequestPost(Context mContext,String url,String tag,
                                   final Map<String, String> params,VolleyInterface vif){

        MyApplication.getHttpQueues().cancelAll(tag);
        stringRequest=new StringRequest(Method.GET, url, vif.loadingListener(),vif.errorListener()){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // TODO Auto-generated method stub
                return params;
            }
        };
        stringRequest.setTag(tag);
        MyApplication.getHttpQueues().add(stringRequest);
        MyApplication.getHttpQueues().start();

    }*/


}

