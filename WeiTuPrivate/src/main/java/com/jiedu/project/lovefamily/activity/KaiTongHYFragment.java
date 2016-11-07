package com.jiedu.project.lovefamily.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.fragment.HomeFragment02;

/**
 * Created by Administrator on 2016/10/27.
 */
public class  KaiTongHYFragment extends Fragment implements View.OnClickListener{
    private View inflate;
    private ImageView huiyuan;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.kaitonghuiyuan, container, false);
        huiyuan= (ImageView) inflate.findViewById(R.id.huiyuan);
        huiyuan.setOnClickListener(this);
        return inflate;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.huiyuan:
                HomeFragment02 homeFragment02=new HomeFragment02();
                getFragmentManager().beginTransaction().replace(R.id.fragment_layout,homeFragment02).commit();
                break;
            default:
                break;
        }
    }
}
