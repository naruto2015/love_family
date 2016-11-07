package com.jiedu.project.lovefamily.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.jiedu.project.lovefamily.BackHandledFragment;
import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

/**
 * Created by Administrator on 2016/10/24.
 */
public class MyScanFragment extends BackHandledFragment implements View.OnClickListener{
    private boolean hadIntercept;
    private ImageView back;
    Bitmap bitmap;
    private ImageView img_code;
    private  View inflate;
    public MyScanFragment(){

   }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.activity_my_scan, container, false);
        back= (ImageView)inflate.findViewById(R.id.scan_back);
        img_code= (ImageView)inflate.findViewById(R.id.img_code);
        String phone= SharedPreferencesUtil.getInfo(getContext(),"phone");
        bitmap= EncodingUtils.createQRCode(phone,500,500,null);
        back.setOnClickListener(this);
        img_code.setImageBitmap(bitmap);


        return inflate;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.scan_back:
                HomeFragment02 homeFragment02=new HomeFragment02();
                getFragmentManager().beginTransaction().replace(R.id.fragment_layout,homeFragment02).commit();
                break;
        }
    }
    @Override
    public boolean onBackPressed() {
        if(hadIntercept){
            return false;
        }else{
            Toast.makeText(getActivity(), "Click From MyFragment", Toast.LENGTH_SHORT).show();
            hadIntercept = true;
            return true;
        }
    }

}
