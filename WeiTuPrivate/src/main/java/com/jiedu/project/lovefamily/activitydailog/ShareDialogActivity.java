package com.jiedu.project.lovefamily.activitydailog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.activity.BaseActivity;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

public class ShareDialogActivity extends BaseActivity implements View.OnClickListener {


    private ImageView weixin,qq,weixinZong,qqZong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_share_dialog);

        weixin= (ImageView) findViewById(R.id.weixin);
        qq= (ImageView) findViewById(R.id.qq);
        qqZong= (ImageView) findViewById(R.id.qqZong);
        weixinZong= (ImageView) findViewById(R.id.weixinZong);

        weixin.setOnClickListener(this);
        qqZong.setOnClickListener(this);
        qq.setOnClickListener(this);
        weixinZong.setOnClickListener(this);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }


    @Override
    public void onClick(View v) {
        UMImage umImage=new UMImage(ShareDialogActivity.this, R.drawable.logo);
        switch (v.getId()) {
            case R.id.weixin:
                new ShareAction(ShareDialogActivity.this)
                        .setPlatform(SHARE_MEDIA.WEIXIN)
                            /*.setCallback(umShareListener)*/
                        .withText("您的家人邀请您安装爱家在线，快来看看吧")
                        .withTitle("爱家在线")
                        .withTargetUrl(RequestHelp.HTTP_HEAD + RequestHelp.HTTP_ACTION + "/wttp/toIntroduce.do")
                        .withMedia(umImage)
                        .share();
                break;
            case R.id.weixinZong:
                new ShareAction(ShareDialogActivity.this)
                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                            /*.setCallback(umShareListener)*/
                        .withText("您的家人邀请您安装爱家在线，快来看看吧")
                        .withTitle("爱家在线")
                        .withTargetUrl(RequestHelp.HTTP_HEAD + RequestHelp.HTTP_ACTION + "/wttp/toIntroduce.do")
                        .withMedia(umImage)
                        .share();
                break;
            case R.id.qq:
                new ShareAction(ShareDialogActivity.this)
                        .setPlatform(SHARE_MEDIA.QQ)
                            /*.setCallback(umShareListener)*/
                        .withText("您的家人邀请您安装爱家在线，快来看看吧")
                        .withTitle("爱家在线")
                        .withTargetUrl(RequestHelp.HTTP_HEAD + RequestHelp.HTTP_ACTION + "/wttp/toIntroduce.do")
                        .withMedia(umImage)
                        .share();
                break;
            case R.id.qqZong:
                new ShareAction(ShareDialogActivity.this)
                        .setPlatform(SHARE_MEDIA.QZONE)
                            /*.setCallback(umShareListener)*/
                        .withText("您的家人邀请您安装爱家在线，快来看看吧")
                        .withTitle("爱家在线")
                        .withTargetUrl(RequestHelp.HTTP_HEAD + RequestHelp.HTTP_ACTION + "/wttp/toIntroduce.do")
                        .withMedia(umImage)
                        .share();
                break;

        }

        finish();
    }

}
