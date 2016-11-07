package com.jiedu.project.lovefamily.mode;

import com.linkage.gson.Gson;
import com.linkage.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by naruto on 2016/4/15.
 */
public class MyPayType {

    public String name;
    public String id;
    public lstbusiType lstbusiType;


    public static List<MyPayType> getMyPayTypeList(String json){
        List<MyPayType> myPayTypeList=new ArrayList<MyPayType>();
        Gson gson=new Gson();
        try {
            JSONObject jsonObject=new JSONObject(json);
            String json2=jsonObject.optString("data");
            myPayTypeList=gson.fromJson(json2,new TypeToken<List<MyPayType>>(){}.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return myPayTypeList;

    }
    
}
