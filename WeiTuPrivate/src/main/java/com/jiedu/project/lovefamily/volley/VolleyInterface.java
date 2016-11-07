package com.jiedu.project.lovefamily.volley;

/**
 * Created by naruto on 2016/2/25.
 */



public abstract class VolleyInterface {

    /*public Context mContext;
    public static Listener<String> listener;
    public static ErrorListener errorListener;

    public VolleyInterface(Context context, Listener<String> listener, ErrorListener errorListener) {
        // TODO Auto-generated constructor stub
        this.mContext=context;
        this.errorListener=errorListener;
        this.listener=listener;
    }

    public abstract void onMySuccess(String result);
    public abstract void onMyError(VolleyError error);
    public ErrorListener errorListener(){
        errorListener=new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onMyError(error);
            }
        };

        return errorListener;
    }

    public Listener<String> loadingListener(){
        listener=new Listener<String>() {
            @Override
            public void onResponse(String response) {
                onMySuccess(response);
            }
        };
        return listener;
    }*/

}
