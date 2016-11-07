package com.jiedu.project.lovefamily.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.net.RequestHelp;

/**
 * Created by Administrator on 2016/3/30.
 */
public class HelpFragment extends Fragment  implements View.OnClickListener{
    View view;
    WebView webView;
    private ImageView user_photo;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_help,null);
        user_photo= (ImageView) view.findViewById(R.id.user_photo);
        webView= (WebView) view.findViewById(R.id.web);
        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(RequestHelp.WEB_HELP_URL);
        user_photo.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.user_photo:
                HomeFragment02 homeFragment02=new HomeFragment02();
                getFragmentManager().beginTransaction().replace(R.id.fragment_layout,homeFragment02).commit();
                break;
            default:
                break;
        }
    }
}
