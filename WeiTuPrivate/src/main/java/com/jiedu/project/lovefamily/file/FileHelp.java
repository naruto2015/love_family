package com.jiedu.project.lovefamily.file;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Administrator on 2016/3/28.
 */
public class FileHelp {
    private  static final String FILE_NAME="head";
    private static  final String SUFFIX=".jpg";
//    private static final String FILE_PATH= Environment.getDataDirectory().getPath();
    private static final String FILE_PATH= "/sdcard/";
    public static void saveBitmapFile(Bitmap bitmap) {

        try {  File file=new File(FILE_PATH+FILE_NAME+SUFFIX);
            if(!file.exists()){
                    file.createNewFile();
            }

            OutputStream bos = new FileOutputStream(file);
           boolean b= bitmap.compress(Bitmap.CompressFormat.JPEG,100, bos);
//            Log.e("0011","图片保存结果"+b);
            bos.flush();
            bos.close();

        } catch (IOException e) {
            e.printStackTrace();
//            Log.e("0011", "图片保存失败", e);
        }
//        saveBitmap2file(bitmap,FILE_NAME+SUFFIX);
    }
    static boolean saveBitmap2file(Bitmap bmp,String filename){
        Bitmap.CompressFormat format= Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream("/sdcard/" + filename);
        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
//            Log.e("0011", "图片保存失败", e);
        }

        return bmp.compress(format, quality, stream);
    }

    public static File getFile(){
     File file=   new File(FILE_PATH+FILE_NAME+SUFFIX);
        if(!file.exists()){
//            Log.e("0011","文件为空");
          return  null;
        }else {
            return file;
        }

    }
    public static void deteleFile(File file){
        if(null!=file&&file.exists()){
            file.delete();
        }
    }
}
