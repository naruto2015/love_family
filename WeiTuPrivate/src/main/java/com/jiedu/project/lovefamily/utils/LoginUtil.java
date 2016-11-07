package com.jiedu.project.lovefamily.utils;



import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;

import com.jiedu.project.lovefamily.application.MyApplication;

public class LoginUtil {

	public static final String LOGIN_UID = "login_uid";
	public static final String LOGIN_PWD = "login_pwd";

	public static void initUidAndPwd(EditText uidEt, EditText pwdEt) {
		String saveUid = MyApplication.getInstance().getSharePrefValue(
				LoginUtil.LOGIN_UID, null);
		String savePwd = MyApplication.getInstance().getSharePrefValue(
				LoginUtil.LOGIN_PWD, null);
		if (saveUid == null) {
			return;
		} else {
			uidEt.setText(saveUid);
			pwdEt.setText(savePwd);
		}
	}


	public boolean checkUid(Context context, String uid) {
		if (null == uid || "".equals(uid)) {
			new AlertDialog.Builder(context).setTitle("请输入手机号")
					.setPositiveButton("确定", null).create().show();
			return false;
		} else if (!uid.matches("^1[3-9]\\d{9}$")) {
			new AlertDialog.Builder(context).setTitle("您输入的手机号格式有误")
					.setPositiveButton("确定", null).create().show();
			return false;
		}
		return true;
	}

	public boolean checkPassWd(Context context, String uid, String passwd) {
		if (null == passwd || "".equals(passwd)) {
			new AlertDialog.Builder(context).setTitle("请输入密码")
					.setPositiveButton("确定", null).create().show();
			return true;
		}
		if (!uid.substring(7).equals(passwd)) {
			new AlertDialog.Builder(context).setTitle("密码错误")
					.setPositiveButton("确定", null).create().show();
			return true;
		}
		return true;
	}
}
