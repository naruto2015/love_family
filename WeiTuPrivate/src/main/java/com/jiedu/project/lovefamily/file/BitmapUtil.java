package com.jiedu.project.lovefamily.file;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
public class BitmapUtil {
    public static final int BITMAP_SIZE_HIGHT = 480;
    public static final int BITMAP_SIZE_MID = 320;
    public static final int BITMAP_SIZE_LOW = 240;
    public static final int BITMAP_SIZE_AVATAR = 120;
    public static final int BITMAP_QUALITY = 60;

    public static void compress(File file, int size, int quality)
            throws IOException {
        // 压缩大小
        FileInputStream stream = new FileInputStream(file);
        // 获取这个图片的宽和高
        Bitmap bitmap = ImageHelper.getImageThumbnail(file.getAbsolutePath(), 400, 600);
        stream.close();
        // 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false
        stream = new FileInputStream(file);
        stream.close();
        // 删除文件
        file.delete();
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream outstream = new FileOutputStream(file);
        if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outstream)) {
            outstream.flush();
            outstream.close();
        }
    }

    public static void compress2(Activity context, String imgPath)
            throws IOException {
        ImageHelper ih = new ImageHelper();
        Display display = context.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();// 720
        int height = display.getHeight();// 1280
        if (width > height)
            width = height;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        File jpeg = new File(imgPath);
        byte[] bytes = new byte[(int) jpeg.length()];
        DataInputStream in = null;
        try {
            in = new DataInputStream(new FileInputStream(jpeg));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            in.readFully(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
        float conversionFactor = 1.0f;
        if (opts.outWidth > opts.outHeight)
            conversionFactor = 1.0f;
        String orientation = "";
        orientation = ih.getExifOrientation1(imgPath, orientation);
        byte[] finalBytes = ih.createThumbnail(bytes,
                String.valueOf((int) (width * conversionFactor)), orientation,
                true);
        if (jpeg.delete()) {
            if (jpeg.createNewFile()) {
                FileOutputStream outstream = new FileOutputStream(jpeg);
                outstream.write(finalBytes, 0, finalBytes.length);
                outstream.flush();
                outstream.close();
            }
        }
    }


    /**
     * Android图片缩放
     *
     * @param drawable
     * @param w
     * @param h
     * @return
     */
    public static Drawable zoomDrawable(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable); // drawable转换成bitmap
        Matrix matrix = new Matrix(); // 创建操作图片用的Matrix对象
        if (width > 480) {
            float scaleWidth = ((float) 480 / width); // 计算缩放比例
            // float scaleHeight = ((float) h / height);
            matrix.postScale(scaleWidth, scaleWidth); // 设置缩放比例
        }
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true); // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        return new BitmapDrawable(newbmp); // 把bitmap转换成drawable并返回
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {// drawable
        int width = drawable.getIntrinsicWidth(); // 取drawable的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565; // 取drawable的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config); // 建立对应bitmap
        Canvas canvas = new Canvas(bitmap); // 建立对应bitmap的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas); // 把drawable内容画到画布中
        return bitmap;
    }
}
