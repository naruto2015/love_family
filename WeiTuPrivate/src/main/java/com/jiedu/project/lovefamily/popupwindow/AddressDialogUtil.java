package com.jiedu.project.lovefamily.popupwindow;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.mode.CityData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

/**
 * Created by Administrator on 2016/3/3.
 */
public class AddressDialogUtil implements OnWheelChangedListener, View.OnClickListener {
    private Dialog dialog;
    private Context context;
    private View view;
    private Button cancel;
    private Button submit;
    /**
     * 把全国的省市区的信息以json的格式保存，解析完成后赋值为null
     */
    private JSONObject mJsonObj;
    /**
     * 省的WheelView控件
     */
    private WheelView mProvince;
    /**
     * 市的WheelView控件
     */
    private WheelView mCity;
    /**
     * 区的WheelView控件
     */
    private WheelView mArea;

    /**
     * 所有省
     */
    private String[] mProvinceDatas;
    /**
     * key - 省 value - 市s
     */
    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /**
     * key - 市 values - 区s
     */
    private Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();

    /**
     * 当前省的名称
     */
    private String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    private String mCurrentCityName;
    /**
     * 当前区的名称
     */
    private String mCurrentAreaName ="";

    private Button province;
    private Button city;
    private  Button area;

    public void showDialog(Context context, Button province, Button city, Button area){
        this.context=context;
        this.province=province;
        this.city=city;
        this.area=area;
        if (null==dialog){
            dialog=new Dialog(context,R.style.NoFrameDialog);
            view= LayoutInflater.from(context).inflate(R.layout.dialog_city_choose,null);
            Window win = dialog.getWindow();
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.x=0;
            params.y=400;
            win.setAttributes(params);

            dialog.setContentView(view);
        }
        try {
            mJsonObj=new JSONObject(CityData.cityDataString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mProvince = (WheelView) view.findViewById(R.id.id_province);
        mCity = (WheelView) view.findViewById(R.id.id_city);
        mArea = (WheelView) view.findViewById(R.id.id_area);
        cancel=(Button)view.findViewById(R.id.cancel);
        submit=(Button)view.findViewById(R.id.submit);
        cancel.setOnClickListener(this);
        submit.setOnClickListener(this);
        initDatas();

        mProvince.setViewAdapter(new ArrayWheelAdapter<String>(context, mProvinceDatas));
        // 添加change事件
        mProvince.addChangingListener(this);
        // 添加change事件
        mCity.addChangingListener(this);
        // 添加change事件
        mArea.addChangingListener(this);

        mProvince.setVisibleItems(5);
        mCity.setVisibleItems(5);
        mArea.setVisibleItems(5);
        updateCities();
        updateAreas();
        dialog.show();;

    }
    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas()
    {
        int pCurrent = mCity.getCurrentItem();
        try {
            mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
            String[] areas = mAreaDatasMap.get(mCurrentCityName);

            if (areas == null)
            {
                areas = new String[] { "" };
            }
            mArea.setViewAdapter(new ArrayWheelAdapter<String>(context, areas));
            mCurrentAreaName=areas[0];
            mArea.setCurrentItem(0);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities()
    {
        int pCurrent = mProvince.getCurrentItem();
        try {
            mCurrentProviceName = mProvinceDatas[pCurrent];
            String[] cities = mCitisDatasMap.get(mCurrentProviceName);
            if (cities == null)
            {
                cities = new String[] { "" };
            }
            mCity.setViewAdapter(new ArrayWheelAdapter<String>(context, cities));
            mCurrentCityName=cities[0];
            mCity.setCurrentItem(0);
            updateAreas();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    /**
     * 解析整个Json对象，完成后释放Json对象的内存
     */
    private void initDatas()
    {
        try
        {
            JSONArray jsonArray = mJsonObj.getJSONArray("citylist");
            mProvinceDatas = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonP = jsonArray.getJSONObject(i);// 每个省的json对象
                String province = jsonP.getString("p");// 省名字

                mProvinceDatas[i] = province;

                JSONArray jsonCs = null;
                try
                {
                    /**
                     * Throws JSONException if the mapping doesn't exist or is
                     * not a JSONArray.
                     */
                    jsonCs = jsonP.getJSONArray("c");
                } catch (Exception e1)
                {
                    continue;
                }
                String[] mCitiesDatas = new String[jsonCs.length()];
                for (int j = 0; j < jsonCs.length(); j++)
                {
                    JSONObject jsonCity = jsonCs.getJSONObject(j);
                    String city = jsonCity.getString("n");// 市名字
                    mCitiesDatas[j] = city;
                    JSONArray jsonAreas = null;
                    try
                    {
                        /**
                         * Throws JSONException if the mapping doesn't exist or
                         * is not a JSONArray.
                         */
                        jsonAreas = jsonCity.getJSONArray("a");
                    } catch (Exception e)
                    {
                        continue;
                    }

                    String[] mAreasDatas = new String[jsonAreas.length()];// 当前市的所有区
                    for (int k = 0; k < jsonAreas.length(); k++)
                    {
                        String area = jsonAreas.getJSONObject(k).getString("s");// 区域的名称
                        mAreasDatas[k] = area;
                    }
                    mAreaDatasMap.put(city, mAreasDatas);
                }

                mCitisDatasMap.put(province, mCitiesDatas);
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        mJsonObj = null;
    }


    /**
     * change事件的处理
     */
    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue)
    {
        if (wheel == mProvince)
        {
            updateCities();
        } else if (wheel == mCity)
        {
            updateAreas();

        } else if (wheel == mArea)
        {
            mCurrentAreaName = mAreaDatasMap.get(mCurrentCityName)[newValue];
        }
    }

    public void showChoose()
    {
        if(!TextUtils.isEmpty(mCurrentProviceName)){
     province.setText(mCurrentProviceName);
        }else{
            province.setText("");
        }
        if(!TextUtils.isEmpty(mCurrentCityName)){
        city.setText(mCurrentCityName);
        }else {
            city.setText("");
        }
        if(!TextUtils.isEmpty(mCurrentAreaName)){
area.setText(mCurrentAreaName);
        }else{
            area.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        dialog.dismiss();
switch(v.getId()){
    case R.id.cancel:

        break;
    case R.id.submit:

        showChoose();
        break;
}
    }

}
