package com.jiedu.project.lovefamily.share;

import android.app.Activity;
import android.content.Context;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

/**
 * Created by Administrator on 2016/3/17.
 */
public class ShareUtil {

    public static void share(Context context){

        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                {
                        SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                        SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
                };
        UMImage umImage=new UMImage(context, R.drawable.logo);
        new ShareAction((Activity) context).setDisplayList( displaylist )
                .withText("快使用位图通个人版来给亲朋好友定位吧！")
                .withTitle(context.getString(R.string.app_name))
                .withTargetUrl(RequestHelp.HTTP_HEAD + RequestHelp.HTTP_ACTION + "/wttp/toIntroduce.do")
                .withMedia(umImage)
                .open();

    }

}
