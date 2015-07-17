package com.gztech.loadingani;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.TextView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Utilities {
    public static int dpToPixels(Context context, int dpValues){
        Resources r = context.getResources(); // 取得手機資源
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, //轉換dp值
                dpValues, //dp值
                r.getDisplayMetrics());
        return (int) px;
    }
    public static int spToPixels(Context context, int spValues){
        Resources r = context.getResources(); // 取得手機資源
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, //轉換sp值
                spValues, //dp值
                r.getDisplayMetrics());
        return (int) px;
    }

    public static File compressImage(Bitmap image) {
        if(image!=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
                options -= 10;//每次都减少10
            }

            FileOutputStream fos = null;
            try {
                String tmpFolder = Environment.getExternalStorageDirectory()+"/_CheHaoLa";
                String tmpFilePath = tmpFolder+"/temp";
                File folder = new File(tmpFolder);
                if(!folder.exists()){
                    folder.mkdir();
                }
                File file = new File(tmpFilePath);
                if(!file.exists()){
                    file.createNewFile();
                }
                fos = new FileOutputStream(file);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new File(Environment.getExternalStorageDirectory()+"/_CheHaoLa/temp");
        }
        return null;
    }
    public static Bitmap loadBitmap(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        newOpts.inJustDecodeBounds = false;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;
    }

    public static int getAppVersionCode(Context context) {
        int versioncode = -1;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versioncode = pi.versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versioncode;
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    public static boolean mobileValid(String mobile) {
        if(mobile.length()>10){
            return true;
        }else{
            return false;
        }
    }

    public static boolean pwdValid(String pwd){
        Pattern pattern = Pattern.compile("[0-9a-zA-Z_]{6,16}");
        Matcher matcher = pattern.matcher(pwd);
        return matcher.matches();
    }
}
