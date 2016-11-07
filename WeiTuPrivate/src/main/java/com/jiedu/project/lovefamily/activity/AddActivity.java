package com.jiedu.project.lovefamily.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.activitydailog.PhoneStyleActivity;
import com.jiedu.project.lovefamily.activitydailog.UploadMyHeadDialogActivity;
import com.jiedu.project.lovefamily.file.BitmapUtil;
import com.jiedu.project.lovefamily.file.FileHelp;
import com.jiedu.project.lovefamily.json.JsonHelp;
import com.jiedu.project.lovefamily.message.MessageInfoUtil;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.popupwindow.AddressDialogUtil;
import com.jiedu.project.lovefamily.popupwindow.PayPopupwindowUtil;
import com.jiedu.project.lovefamily.popupwindow.PhoneStyleUtil;
import com.jiedu.project.lovefamily.popupwindow.PhotoPopupwindowUtil;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;
import com.jiedu.project.lovefamily.utils.MyAddSyleDialog;
import com.xys.libzxing.zxing.activity.CaptureActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddActivity extends BaseActivity implements View.OnClickListener {

    ImageView submit;
    Button payKind;
    Button phoneKind;
    ImageView back;
    CircleImageView photo;
//    Button province;
//    Button city;
//    Button area;
    AddressDialogUtil dialogUtil;
    PayPopupwindowUtil payPopupwindowUtil;
    PhoneStyleUtil phoneStyleUtil;
    PhotoPopupwindowUtil photoPopupwindowUtil;
    private Bitmap bitmap;

    EditText phone;
    EditText name;
    RequestHelp requestHelp;
    JsonHelp jsonHelp;

    private ImageView add_style;
    MyAddSyleDialog myAddSyleDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        init();
        initFile();

    }

    private void init() {
        dialogUtil = new AddressDialogUtil();
        jsonHelp = new JsonHelp();
        payPopupwindowUtil = new PayPopupwindowUtil();

        photoPopupwindowUtil = new PhotoPopupwindowUtil();
        requestHelp = new RequestHelp();
        phoneStyleUtil = new PhoneStyleUtil();
        submit = (ImageView) findViewById(R.id.add_submit);
        payKind = (Button) findViewById(R.id.pay_style);
        phoneKind = (Button) findViewById(R.id.phone_style);
        back = (ImageView) findViewById(R.id.add_back);
//        province = (Button) findViewById(R.id.province);
//        city = (Button) findViewById(R.id.city);
//        area = (Button) findViewById(R.id.area);
        photo = (CircleImageView) findViewById(R.id.user_photo);
        phone = (EditText) findViewById(R.id.add_edit);
        name = (EditText) findViewById(R.id.add_name_edit);
        add_style= (ImageView) findViewById(R.id.add_style);

        add_style.setOnClickListener(this);
        submit.setOnClickListener(this);
//        取消终端类型
//        phoneKind.setOnClickListener(this);
        phoneKind.setText("安卓");

        payKind.setOnClickListener(this);
        back.setOnClickListener(this);
//        province.setOnClickListener(this);
//        city.setOnClickListener(this);
//        area.setOnClickListener(this);
        photo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_submit:
                //submit.setClickable(false);

                if (!TextUtils.isEmpty(phone.getText().toString())) {

//                    if(phoneKind.getText().toString().trim().equals("终端类型")){
//
//                        Toast.makeText(AddActivity.this,"请选择终端类型!",Toast.LENGTH_LONG).show();
//                        break;
//                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String result="";
                            try{

//                            String RequestURL= RequestHelp.ADD_MEMBER + "?myphone=" + SharedPreferencesUtil.getInfo(AddActivity.this, "phone") + "&phone=" +  phone.getText().toString().trim() + "&nickName=" + URLEncoder.encode(name.getText().toString(), "UTF-8") + "&type=" + getType(phoneKind.getText().toString());
                                String RequestURL= RequestHelp.ADD_MEMBER + "?myphone=" + SharedPreferencesUtil.getInfo(AddActivity.this, "phone") + "&phone=" +  phone.getText().toString().trim() + "&nickName=" + URLEncoder.encode(name.getText().toString(), "UTF-8") + "&type=" + getType(phoneKind.getText().toString());
                                if(/*FileHelp.getFile()!=null*/headImgOriFileName!=null && headImgOriFileName!=""){
                                    //result =   requestHelp.uploadFile(FileHelp.getFile(),RequestURL);

                                    result = requestHelp.uploadFile(new File(headImgOriFileName), RequestURL);
//                               Log.e("0011","上传头像路径"+RequestURL);
                                }else{
                                    result = requestHelp.addMember(SharedPreferencesUtil.getInfo(AddActivity.this, "phone"), phone.getText().toString().trim(), name.getText().toString(), getType(phoneKind.getText().toString()));
                                }
                            }catch (Exception e){

                            };

                            if(result!=null){
                                JSONObject jsonObject=null;
                                try {
                                    jsonObject=new JSONObject(result);
                                    String ok=jsonObject.optString("ok");
                                    Log.e("TAG","-------"+ok);
                                    Message msg=Message.obtain();
                                    msg.what=3838;
                                    msg.obj=jsonObject.optString("msg");
                                    if(ok.trim().equals("true")){
                                        flag=1;
                                    }else{
                                        flag=0;
                                    }
                                    handler.sendMessage(msg);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //上传成功后删除图片
                                FileHelp.deteleFile(FileHelp.getFile());
                                //jsonHelp.dealAddMember(result, handler);
                            }

                        }
                    }).start();


                }else{
                    //submit.setClickable(true);
                    Toast.makeText(AddActivity.this,getString(R.string.null_phone),Toast.LENGTH_LONG).show();
                }


                break;
            case R.id.add_back:
                finish();
                break;
            case R.id.pay_style:
                payPopupwindowUtil.showPopupwindow(payKind, this);
                break;
            case R.id.phone_style:
                //phoneStyleUtil.showPopupwindow(phoneKind, this);
                startActivityForResult(new Intent(AddActivity.this, PhoneStyleActivity.class),1);
                break;
//            case R.id.province:
//            case R.id.city:
//            case R.id.area:
//                dialogUtil.showDialog(this, province, city, area);
//                break;
            case R.id.user_photo:
                //photoPopupwindowUtil.showPopupwindow(photo, this, handler);
                startActivityForResult(new Intent(AddActivity.this, UploadMyHeadDialogActivity.class),1);
                break;
            case R.id.add_style:
                myAddSyleDialog=new MyAddSyleDialog(AddActivity.this);
                myAddSyleDialog.setCancleOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myAddSyleDialog.dismiss();
                    }
                });

                myAddSyleDialog.setTongxunluOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         /*
                      跳转到电话本页面
                         */
                        myAddSyleDialog.dismiss();
                        Intent intent=new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent,1001);

                    }
                });

                myAddSyleDialog.setScanOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myAddSyleDialog.dismiss();
                        scan();
                    }
                });

                break;
        }
    }


    //开启二维码扫描
    public void scan(){
        startActivityForResult(new Intent(AddActivity.this, CaptureActivity.class),1002);

    }


    private AlertDialog alertDialog=null;
    private int flag=0;
    private static int REQUEST_LIBRARY_PHOTO = 1;
    private static int REQUEST_TAKE_PHOTO = 2;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PhotoPopupwindowUtil.CHOOSE_PHOTO:
                    //选择图片
                  /*  Intent intentChoose = new Intent(Intent.ACTION_PICK);// 打开相册
                    intentChoose.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image*//*");
                    intentChoose.putExtra("output", Uri.fromFile(tempFile));
                    startActivityForResult(intentChoose, PhotoPopupwindowUtil.RESULT_CHOOSE_PHOTO);*/
                    ((Activity) AddActivity.this).startActivityForResult(new Intent(
                                    "android.intent.action.PICK",
                                    MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                            AddActivity.REQUEST_LIBRARY_PHOTO);

                    break;
                case PhotoPopupwindowUtil.TAKE_PHOTO:
                    //拍照

                 /*   Intent intentTakePhoto = new Intent("android.media.action.IMAGE_CAPTURE");
                    intentTakePhoto.putExtra("output", Uri.fromFile(tempFile));

                    startActivityForResult(intentTakePhoto, PhotoPopupwindowUtil.RESULT_TAKE_PHOTO);*/
                    Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    ((Activity) AddActivity.this).startActivityForResult(intent2,
                            AddActivity.REQUEST_TAKE_PHOTO);
                    break;
                case MessageInfoUtil.ADD_MEMBER:
                  /*  setResult(2);
                    finish();*/
                    break;
                case MessageInfoUtil.ADD_MEMBER_FAIL:
                 /*   submit.setClickable(true);
                    Toast.makeText(AddActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();*/
                    break;
                case 3838:
                    String mes;
                    submit.setClickable(true);
                    mes= (String) msg.obj;
                    if(flag==1){
                        //表示添加成功
                        // //"已向对方发送邀请信息，对方安装完成后添加";
                    }else{
                        //mes="添加失败";
                    }
                    alertDialog=new AlertDialog.Builder(AddActivity.this)
                            .setTitle("温馨提示")
                            .setMessage(mes)
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    alertDialog.dismiss();
                                    if(flag==1){
                                        setResult(1002);
                                        finish();
                                    }
                                }
                            })
                            .show();

                    break;

            }

        }
    };

    //查询本机电话号码
    private String getContactNumber(Cursor cursor) {
        int numberCount=cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
        String number="";
        if(numberCount>0){
            int contactId=cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phoneCursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+contactId,null,null);
            phoneCursor.moveToFirst();
            number=phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneCursor.close();
        }
        cursor.close();
        return number;
    }


    private Bitmap headImg = null;
    private String headImgOriFileName;
    Random random=new Random();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==101){
            phoneKind.setText(data.getStringExtra("myphone"));
        }

        if(resultCode==102){
            ((Activity) AddActivity.this).startActivityForResult(new Intent(
                            "android.intent.action.PICK",
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                    AddActivity.REQUEST_LIBRARY_PHOTO);
        }

        if(resultCode==103){
            Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            ((Activity) AddActivity.this).startActivityForResult(intent2,
                    AddActivity.REQUEST_TAKE_PHOTO);
        }



        if(requestCode==1001){
            {
                if(resultCode==RESULT_OK){
                /*
                获取电话本联系人相关信息
                 */
                    Uri contactUri=data.getData();
                    Cursor cursor= getContentResolver().query(contactUri, null, null, null, null);
                    cursor.moveToFirst();
                    String contactName=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String number=getContactNumber(cursor);
                    String num=number.replace(" ","");
                    phone.setText(num);
                    if(contactName!=null){
                        name.setText(contactName);
                    }

                }
            }
        }

        if(requestCode==1002){
            if(resultCode==RESULT_OK){
                Bundle bundle=data.getExtras();
                String result=bundle.getString("result");
                phone.setText(result+"");
            }
        }


      /*  switch(requestCode){
            case PhotoPopupwindowUtil.RESULT_CHOOSE_PHOTO:

                //选择图片结果回调
                Log.e("0011", "选择图片结果");
                if(data!=null){
                    cropPhoto(data.getData());
                }

                break;
            case PhotoPopupwindowUtil.RESULT_CROP:
                Log.e("0011","裁剪图片结果返回");
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                     bitmap = BitmapFactory.decodeFile(fileName, options);
                    Log.e("0011","生成图片");
                    if (bitmap != null) {
                        Log.d("1", "aaaa");
                        photo.setImageBitmap(bitmap);
                        FileHelp.saveBitmapFile(bitmap);
//                   setPicToView(bitmap);
                        //saveBitmap(tempFile,bitmap);
                    }else{
                        Log.e("0011","裁剪得到的图片为空");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("0011","设置图片异常");
                }


                break;
            case PhotoPopupwindowUtil.RESULT_TAKE_PHOTO:
                Log.e("0011", "拍照结果");
                if(null!=tempFile&&tempFile.length()!=0){
                    cropPhoto(Uri.fromFile(tempFile));
                }
                break;

        }*/

        if (resultCode == RESULT_OK) {
            Uri mImageCaptureUri;
            if (requestCode == 2 || requestCode == 1)// 原图
            {
                if (headImg != null)
                    headImg.recycle();
                mImageCaptureUri = data.getData();
                if (mImageCaptureUri != null) {
                    try {
                        headImg = MediaStore.Images.Media.getBitmap(
                                this.getContentResolver(), mImageCaptureUri);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        //Log.e(TAG, e.getMessage());
                    }
                } else {
                    Bundle extras = data.getExtras();
                    headImg = extras.getParcelable("data");
                    mImageCaptureUri = Uri.parse(MediaStore.Images.Media
                            .insertImage(getContentResolver(), headImg, null,
                                    null));
                }
                Intent intent = new Intent();
                intent.setAction("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");// mUri是已经选择的图片Uri
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);// 裁剪框比例
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 120);// 输出图片大小
                intent.putExtra("outputY", 120);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, 200);
            } else if (requestCode == 200)// 剪裁完成的图
            {
                headImgOriFileName = Environment.getExternalStorageDirectory()+File.separator+"dqysh"+random.nextInt(10000) + ".jpg";
                mImageCaptureUri = data.getData();
                if (mImageCaptureUri != null) {
                    try {
                        String[] proj = { MediaStore.Images.Media.DATA };
                        Cursor actualimagecursor = managedQuery(mImageCaptureUri,proj,null,null,null);
                        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        actualimagecursor.moveToFirst();
                        String img_path = actualimagecursor.getString(actual_image_column_index);
//			    	File file = new File(img_path);
                        BitmapUtil.compress2(AddActivity.this,img_path);
                        headImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(),mImageCaptureUri);
//    			headImg = MediaStore.Images.Media.getBitmap(
//    				this.getContentResolver(), mImageCaptureUri);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        //Log.e(TAG, e.getMessage());
                    }
                } else {
                    headImg = data.getExtras().getParcelable("data");
                }
                String fileName=savePicToSdcard(headImg, headImgOriFileName);
                photo.setImageBitmap(headImg);

            }

        }


    }

    public static String savePicToSdcard(Bitmap bitmap, String fileName) {
        if (bitmap == null||bitmap.isRecycled()) {
            return "";
        } else {
            // File destFile = new
            // File(Environment.getExternalStorageDirectory()+"/dqysh",fileName);
            File destFile = new File(fileName);
            OutputStream os = null;
            try {
                os = new FileOutputStream(destFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);// 100代表不压缩
                os.flush();
                os.close();
            } catch (IOException e) {

                fileName = "";
            }
        }
        return fileName;
    }




    /**
     * 裁剪图片
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        Log.d("2", "aaaa");
        Intent intent = new Intent("com.android.camera.action.CROP");
        //自定义裁剪大小
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("output", Uri.fromFile(tempFile));
        //设置裁剪大小
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", (int)getResources().getDimension(R.dimen.x113));
        intent.putExtra("outputY", (int)getResources().getDimension(R.dimen.x113));
        startActivityForResult(intent, PhotoPopupwindowUtil.RESULT_CROP);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=bitmap&&!bitmap.isRecycled()){
            bitmap.recycle();
            bitmap=null;
        }
    }

    private String fileName = "";
    private File tempFile;


    private void initFile() {
        if(fileName.equals("")) {
            // 判断sdcard上的空间

            if (Environment.MEDIA_MOUNTED.equals(Environment
                    .getExternalStorageState())){
                String DIR =  Environment.getExternalStorageDirectory() + "/perso";
                // 目录不存在就创建
                File dirPath = new File(DIR);
                if (!dirPath.exists()) {
                    dirPath.mkdirs();
                }

                fileName=DIR + "/" + "hdimg.jpg";
                //Toast.makeText(this, fileName, 0).show();
                Log.d("3", fileName);
                try {
                    tempFile = File.createTempFile("head",".jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{

            }



        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && payPopupwindowUtil.isPopupWindowShowing()) {
            payPopupwindowUtil.closePopupwindow();
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && phoneStyleUtil.isPopupWindowShowing()) {
            phoneStyleUtil.closePopupwindow();
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && photoPopupwindowUtil.isPopupWindowShowing()) {
            photoPopupwindowUtil.closePopupwindow();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private String getType(String string) {
        if (string.indexOf("苹果") >= 0) {
            return "1";
        } else if (string.indexOf("安卓") >= 0) {
            return "2";
        } else if (string.indexOf("智能") >= 0) {
            return "3";
        } else {
            return "";
        }

    }



}