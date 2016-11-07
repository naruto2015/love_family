package com.jiedu.project.lovefamily.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.fragment.MyFragment;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/10/17.
 */
public class LeftSlidingMenu extends Fragment  implements View.OnClickListener{

    private View mView;
    private LinearLayout l1,l2,l3,l4,l5,l6;
    private RelativeLayout presonal;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (mView == null)
        {
            initView(inflater, container);
        }
        return mView;
    }

    private void initView(LayoutInflater inflater, ViewGroup container)
    {
        mView = inflater.inflate(R.layout.left_menu, container, false);
        presonal= (RelativeLayout) mView.findViewById(R.id.presonal);
         l1= (LinearLayout) mView.findViewById(R.id.l1);
         l2= (LinearLayout) mView.findViewById(R.id.l1);
         l3= (LinearLayout) mView.findViewById(R.id.l1);
         l4= (LinearLayout) mView.findViewById(R.id.l1);
         l5= (LinearLayout) mView.findViewById(R.id.l1);
         l6= (LinearLayout) mView.findViewById(R.id.l1);
        l1.setOnClickListener(this);
        l2.setOnClickListener(this);
        l3.setOnClickListener(this);
        l4.setOnClickListener(this);
        l5.setOnClickListener(this);
        l6.setOnClickListener(this);
        presonal.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.presonal:
                //startActivity(new Intent(mContext,PersonalActivity.class));
//                HomeActivity activity=new HomeActivity();
//                activity.replaceFragment(new MyFragment());
                break;
            case R.id.l1:
                break;
            case R.id.l2:
                break;
            case R.id.l3:
                break;
            case R.id.l4:
                break;
            case R.id.l5:
                break;
            case R.id.l6:
                break;
        }
    }
}
