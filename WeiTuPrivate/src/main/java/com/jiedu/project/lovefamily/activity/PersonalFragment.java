package com.jiedu.project.lovefamily.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.Text;
import com.jiedu.project.lovefamily.R;
import com.jiedu.project.lovefamily.activitydailog.UploadMyHeadDialogActivity;
import com.jiedu.project.lovefamily.file.BitmapUtil;
import com.jiedu.project.lovefamily.fragment.HomeFragment02;
import com.jiedu.project.lovefamily.json.JsonHelp;
import com.jiedu.project.lovefamily.message.MessageInfoUtil;
import com.jiedu.project.lovefamily.net.RequestHelp;
import com.jiedu.project.lovefamily.popupwindow.AddressDialogUtil;
import com.jiedu.project.lovefamily.popupwindow.EditDateChooseDialogUtil;
import com.jiedu.project.lovefamily.popupwindow.PhoneStyleUtil;
import com.jiedu.project.lovefamily.popupwindow.PhotoPopupwindowUtil;
import com.jiedu.project.lovefamily.sharedPreferences.SharedPreferencesUtil;
import com.jiedu.project.lovefamily.stringutil.IntentString;
import com.jiedu.project.lovefamily.utils.EditPhoneDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

/**
 * Created by Administrator on 2011/1/1.
 */
public class PersonalFragment extends Fragment implements View.OnClickListener{
    View view;
    ImageView back;
    AddressDialogUtil dialogUtil;
    TextView title;
    Intent intent;
    ImageView head;
    EditText name;
    EditText address;
    TextView birthday;
    TextView personal_phone;

    String namestr=" ";
    String birthstr=" ";
    //String sexstr=" ";
    String addressstr=" ";

    PhotoPopupwindowUtil photoPopupwindowUtil;
    PhoneStyleUtil phoneStyleUtil;
    Button phone_style;
    private Bitmap bitmap;
    RequestHelp requestHelp;
    private ImageLoader loader;
    EditDateChooseDialogUtil editDateChooseDialogUtil;
    private ImageView save_changes,personal_back;

    String sexStr = "0";
    JsonHelp jsonHelp;

    private static int REQUEST_LIBRARY_PHOTO = 1;
    private static int REQUEST_TAKE_PHOTO = 2;
    private String nickTitle,customerId,nickName,birthday1,sex,address1,photoUrl,phone1;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.personalinformation, null);
        initView();
        initData();
        return view;
    }

    private void initData() {
        Bundle arguments = getArguments();
        if (arguments!=null){
             nickTitle = arguments.getString("nickTitle");
             customerId = arguments.getString("customerId");
             nickName = arguments.getString("nickName");
             birthday1 = arguments.getString("birthday");
             sex = arguments.getString("sex");
             address1 = arguments.getString("address");
             photoUrl = arguments.getString("photoUrl");
             phone1 = arguments.getString("phone");
        }
    }

    void initView() {
        loader = ImageLoader.getInstance().getInstance();
        editDateChooseDialogUtil=new EditDateChooseDialogUtil();
        requestHelp = new RequestHelp();
        jsonHelp = new JsonHelp();
        personal_back= (ImageView) view.findViewById(R.id.personal_back);
        title = (TextView) view.findViewById(R.id.title_edit);
        head = (ImageView) view.findViewById(R.id.head);
//        personal_phone = (TextView) view.findViewById(R.id.personal_phone);
//        phone_style = (Button) view.findViewById(R.id.new_phone_style);
        name = (EditText) view.findViewById(R.id.myview_name_edit);
        address = (EditText) view.findViewById(R.id.address);
        birthday = (TextView) view.findViewById(R.id.myview_brithday_edit);
        save_changes= (ImageView) view.findViewById(R.id.save_changes);
        personal_phone.setText(phone1);
        name.setText(nickName);
        birthday.setText(birthday1);
        address.setText(address1);
        if (!TextUtils.isEmpty(photoUrl)){
            loader.displayImage(intent.getStringExtra(IntentString.PHONO_URL), head);
        }

        phoneStyleUtil = new PhoneStyleUtil();
        photoPopupwindowUtil = new PhotoPopupwindowUtil();
//        String titleStr = intent.getStringExtra(IntentString.INTENT_TITLE);
//
//        if (null != titleStr && !TextUtils.isEmpty(titleStr)) {
//            title.setText(titleStr);
//        }

        dialogUtil = new AddressDialogUtil();

        head.setOnClickListener(this);
        phone_style.setOnClickListener(this);;
        personal_back.setOnClickListener(this);;
        birthday.setOnClickListener(this);
        if(personal_phone.getText().toString().trim().equals(SharedPreferencesUtil.getInfo(getContext(),"phone"))){
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_changes:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String result = "";
                        if (/*FileHelp.getFile() != null*/headImgOriFileName!=null && headImgOriFileName!="") {
                            String url = null;
                            try {

                                url = RequestHelp.EDIT_USER_INFO + "?monitorId=" + SharedPreferencesUtil.getInfo(getContext(), "customerId") + "&monitoredId=" + intent.getStringExtra(IntentString.CUSTOMER_ID) + "&nickName=" + URLEncoder.encode(name.getText().toString(), "UTF-8") + "&birthday=" + birthday.getText().toString().trim() + "&sex=" + sexStr + "&address=" + URLEncoder.encode(address.getText().toString(), "UTF-8") + "&type=" + getType(phone_style.getText().toString());
                                //result = requestHelp.uploadFile(FileHelp.getFile(), url);
                                result = requestHelp.uploadFile(new File(headImgOriFileName), url);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {

                            namestr=name.getText().toString();
                            birthstr=birthday.getText().toString().trim();
                            addressstr=address.getText().toString();

                            result = requestHelp.editUserInfo(SharedPreferencesUtil.getInfo(getContext(), "customerId"), intent.getStringExtra(IntentString.CUSTOMER_ID), namestr, birthstr, sexStr, addressstr, getType(phone_style.getText().toString()));

                        }
                        //上传成功后删除图片
//                        FileHelp.deteleFile(new File(headImgOriFileName));
                        Log.e("0011", "修改信息结果" + result);
                        jsonHelp.dealEdit(result, handler);
                    }
                }).start();
                Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                break;

            case R.id.head:
                startActivityForResult(new Intent(getContext(), UploadMyHeadDialogActivity.class),1);
                break;
            case R.id.myview_brithday_edit:
                editDateChooseDialogUtil.showDialog(getContext(),birthday);

//            case R.id.phone_style:
//                phoneStyleUtil.showPopupwindow(phone_style, getActivity());
//                break;
            case R.id.personal_back:
                HomeFragment02 homeFragment02=new HomeFragment02();
                getFragmentManager().beginTransaction().replace(R.id.fragment_layout,homeFragment02).commit();
                break;

        }


    }

    public void sendMsg(String phone,String content){
        if(phone.trim()!=null){
            Uri uri = Uri.parse("smsto:"+phone);
            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
            it.putExtra("sms_body", content);
           getContext().startActivity(it);
        }else{
            Uri uri = Uri.parse("smsto:");
            Intent ii = new Intent(Intent.ACTION_SENDTO, uri);
            ii.putExtra("sms_body", content);
            getContext().startActivity(ii);
        }


    }

    private EditPhoneDialog editPhoneDialog=null;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PhotoPopupwindowUtil.CHOOSE_PHOTO:

                    //选择图片
//                    Intent intentChoose = new Intent(Intent.ACTION_PICK);// 打开相册
//                    intentChoose.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
//                    intentChoose.putExtra("output", Uri.fromFile(tempFile));
//                    startActivityForResult(intentChoose, PhotoPopupwindowUtil.RESULT_CHOOSE_PHOTO);

                    getActivity().startActivityForResult(new Intent(
                                    "android.intent.action.PICK",
                                    MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                            PersonalFragment.REQUEST_LIBRARY_PHOTO);

                    break;
                case PhotoPopupwindowUtil.TAKE_PHOTO:
                    //拍照

//                    Intent intentTakePhoto = new Intent("android.media.action.IMAGE_CAPTURE");
//                    intentTakePhoto.putExtra("output", Uri.fromFile(tempFile));
//
//                    startActivityForResult(intentTakePhoto, PhotoPopupwindowUtil.RESULT_TAKE_PHOTO);
                    Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    getActivity().startActivityForResult(intent2,
                            PersonalFragment.REQUEST_TAKE_PHOTO);
                    break;
                case MessageInfoUtil.EDIT:
                    if(intent.getStringExtra(IntentString.CUSTOMER_ID).equals(SharedPreferencesUtil.getInfo(getContext(),"customerId"))){
                        getActivity().setResult(1001);
                    }else{
                        getActivity().setResult(1002);
                    }

                    getActivity().finish();
                    break;
                case MessageInfoUtil.EDIT_FAIL:
                    Toast.makeText(getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };




    private Bitmap headImg = null;
    private String headImgOriFileName;
    Random random=new Random();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==102){
            getActivity().startActivityForResult(new Intent(
                            "android.intent.action.PICK",
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                    PersonalFragment.REQUEST_LIBRARY_PHOTO);
        }

        if(resultCode==103){
            Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            getActivity().startActivityForResult(intent2,
                    PersonalFragment.REQUEST_TAKE_PHOTO);
        }


        if (resultCode == getActivity().RESULT_OK) {
            Uri mImageCaptureUri;
            if (requestCode == 2 || requestCode == 1)// 原图
            {
                if (headImg != null)
                    headImg.recycle();
                mImageCaptureUri = data.getData();
                if (mImageCaptureUri != null) {
                    try {
                        headImg = MediaStore.Images.Media.getBitmap(
                                getActivity().getContentResolver(), mImageCaptureUri);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        //Log.e(TAG, e.getMessage());
                    }
                } else {
                    Bundle extras = data.getExtras();
                    headImg = extras.getParcelable("data");
                    mImageCaptureUri = Uri.parse(MediaStore.Images.Media
                            .insertImage(getActivity().getContentResolver(), headImg, null,
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
                        Cursor actualimagecursor = getActivity().managedQuery(mImageCaptureUri,proj,null,null,null);
                        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        actualimagecursor.moveToFirst();
                        String img_path = actualimagecursor.getString(actual_image_column_index);
//			    	File file = new File(img_path);
                        BitmapUtil.compress2(getActivity(),img_path);
                        headImg = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),mImageCaptureUri);
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
                head.setImageBitmap(headImg);

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
//                tempFile = new File(fileName);
                try {
                    tempFile=File.createTempFile("head",".jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Log.d("mainactici", "1212");
            }



        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != bitmap && !bitmap.isRecycled()) {

            bitmap.recycle();
            bitmap = null;
        }
    }


    private String getType(String string) {
        if (string.indexOf("苹果") >= 0) {
            return "1";
        } else if (string.indexOf("安卓") >= 0) {
            return "2";
        } else if (string.indexOf("智能") >= 0) {
            return "3";
        } else {
            return "2";
        }

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

}
