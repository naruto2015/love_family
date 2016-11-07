package com.jiedu.project.lovefamily.file;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;

public class ImageHelper {
	public byte[] createThumbnail(byte[] bytes, String sMaxImageWidth,
			String orientation, boolean tiny) {
		// creates a thumbnail and returns the bytes
		int finalHeight = 0;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		int width = opts.outWidth;
		int finalWidth = 500; // default to this if there's a problem
		// Change dimensions of thumbnail
		if (tiny) {
			finalWidth = 150;
		}
		byte[] finalBytes;
		if (sMaxImageWidth.equals("Original Size")) {
			finalBytes = bytes;
		} else {
			finalWidth = Integer.parseInt(sMaxImageWidth);
			if (finalWidth > width) {
				// don't resize
				finalBytes = bytes;
			} else {
				int sample = 0;
				float fWidth = width;
				sample = Double.valueOf(Math.ceil(fWidth / 1200)).intValue();
				if (sample == 3) {
					sample = 4;
				} else if (sample > 4 && sample < 8) {
					sample = 8;
				}
				opts.inSampleSize = sample;
				opts.inJustDecodeBounds = false;
				try {
					bm = BitmapFactory
							.decodeByteArray(bytes, 0, bytes.length, opts);
				} catch (OutOfMemoryError e) {
					// out of memory
					return null;
				}
				float percentage = (float) finalWidth / bm.getWidth();
				float proportionateHeight = bm.getHeight() * percentage;
				finalHeight = (int) Math.rint(proportionateHeight);
				float scaleWidth = ((float) finalWidth) / bm.getWidth();
				float scaleHeight = ((float) finalHeight) / bm.getHeight();
				float scaleBy = Math.min(scaleWidth, scaleHeight);
				// Create a matrix for the manipulation
				Matrix matrix = new Matrix();
				// Resize the bitmap
				matrix.postScale(scaleBy, scaleBy);
				if ((orientation != null)
						&& (orientation.equals("90")
								|| orientation.equals("180") || orientation
									.equals("270"))) {
					matrix.postRotate(Integer.valueOf(orientation));
				}
				Bitmap resized = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), matrix, true);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				resized.compress(Bitmap.CompressFormat.JPEG, 85, baos);
				bm.recycle(); // free up memory
				resized.recycle();
				finalBytes = baos.toByteArray();
			}
		}
		return finalBytes;
	}

    /**
     * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处：
     * 1.使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     * 2.缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
     *
     * @param imagePath
     *            图像的路径
     * @param width
     *            指定输出图像的宽度
     * @param height
     *            指定输出图像的高度
     * @return 生成的缩略图
     */
    public static Bitmap getImageThumbnail(String imagePath, int width,
                                           int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth > beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        options.inPurgeable = true;// 同时设置才会有效
        options.inInputShareable = true;//当系统内存不够时候图片自动被回收
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);

        Bitmap newBitmap = null;

        if (bitmap != null) {
            String orientation = getExifOrientation1(imagePath, "0");

            Matrix matrix = new Matrix();
            matrix.postRotate(Float.valueOf(orientation));

            newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, false);
        }

        return newBitmap;

    }

    public static String getExifOrientation1(String path, String orientation) {
        Method exif_getAttribute;
        Constructor<ExifInterface> exif_construct;
        String exifOrientation = "";

        int sdk_int = 0;
        try {
            sdk_int = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (Exception e1) {
            sdk_int = 3; // assume they are on cupcake
        }
        if (sdk_int >= 5) {
            try {
                exif_construct = ExifInterface.class
                        .getConstructor(new Class[] { String.class });
                Object exif = exif_construct.newInstance(path);
                exif_getAttribute = ExifInterface.class
                        .getMethod("getAttribute", new Class[] { String.class });
                try {
                    exifOrientation = (String) exif_getAttribute.invoke(exif,
                            ExifInterface.TAG_ORIENTATION);
                    if (exifOrientation != null) {
                        if (exifOrientation.equals("1")) {
                            orientation = "0";
                        } else if (exifOrientation.equals("3")) {
                            orientation = "180";
                        } else if (exifOrientation.equals("6")) {
                            orientation = "90";
                        } else if (exifOrientation.equals("8")) {
                            orientation = "270";
                        }
                    } else {
                        orientation = "0";
                    }
                } catch (InvocationTargetException ite) {
					/* unpack original exception when possible */
                    orientation = "0";
                } catch (IllegalAccessException ie) {
                    System.err.println("unexpected " + ie);
                    orientation = "0";
                }
				/* success, this is a newer device */
            } catch (NoSuchMethodException nsme) {
                orientation = "0";
            } catch (IllegalArgumentException e) {
                orientation = "0";
            } catch (InstantiationException e) {
                orientation = "0";
            } catch (IllegalAccessException e) {
                orientation = "0";
            } catch (InvocationTargetException e) {
                orientation = "0";
            }

        }
        return orientation;
    }
	
	public HashMap<String, Object> getImageBytesForPath(String filePath,
			Context ctx) {
		Uri curStream = null;
		String[] projection;
		HashMap<String, Object> mediaData = new HashMap<String, Object>();
		String title = "", orientation = "";
		byte[] bytes;
		if (filePath != null) {
			if (!filePath.contains("content://"))
				curStream = Uri.parse("content://media" + filePath);
			else
				curStream = Uri.parse(filePath);
		}
		if (curStream != null) {
			if (filePath.contains("video")) {
				int videoID = Integer.parseInt(curStream.getLastPathSegment());
				projection = new String[] { Video.Thumbnails._ID,
						Video.Thumbnails.DATA };
				ContentResolver crThumb = ctx.getContentResolver();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				Bitmap videoBitmap = Video.Thumbnails.getThumbnail(
						crThumb, videoID,
						Video.Thumbnails.MINI_KIND, options);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				try {
					videoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
					bytes = stream.toByteArray();
					title = "Video";
					videoBitmap = null;
				} catch (Exception e) {
					return null;
				}
			} else {
				projection = new String[] { Images.Thumbnails._ID,
						Images.Thumbnails.DATA, Images.Media.ORIENTATION };
				String path = "";
				Cursor cur = ctx.getContentResolver().query(curStream,
						projection, null, null, null);
				File jpeg = null;
				if (cur != null) {
					String thumbData = "";
					if (cur.moveToFirst()) {
						int dataColumn, orientationColumn;
						dataColumn = cur.getColumnIndex(Images.Media.DATA);
						thumbData = cur.getString(dataColumn);
						orientationColumn = cur
								.getColumnIndex(Images.Media.ORIENTATION);
						orientation = cur.getString(orientationColumn);
					}
					
					if (thumbData == null) { 
					 	return null;
					}
					
					jpeg = new File(thumbData);
					path = thumbData;
				} else {
					path = filePath.toString().replace("file://", "");
					jpeg = new File(path);
				}
				title = jpeg.getName();
				try {
					bytes = new byte[(int) jpeg.length()];
				} catch (Exception e) {
					return null;
				} catch (OutOfMemoryError e) {
					return null;
				}
				DataInputStream in = null;
				try {
					in = new DataInputStream(new FileInputStream(jpeg));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return null;
				}
				try {
					in.readFully(bytes);
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
				title = jpeg.getName();
				if (orientation == "") {
					orientation = getExifOrientation1(path, orientation);
				}
			}
			mediaData.put("bytes", bytes);
			mediaData.put("title", title);
			mediaData.put("orientation", orientation);
			return mediaData;
		} else {
			return null;
		}
	}
}
