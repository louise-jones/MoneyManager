package com.minidroid.moneymanager.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * SD卡工具类
 *
 * @author minidroid
 * @date 2017/4/21
 */
public class SDUtils {
    //是否挂载
    private static boolean isMounted() {
        return Environment.MEDIA_MOUNTED.equals(
                Environment.getExternalStorageState());
    }

    //获取到根路径
    private static String getRootPath() {
        if (isMounted()) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "理财小能手";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        }
        return null;
    }

    public static void delete(String fileName) {
        //获取到根路径
        String rootPath = getRootPath();
        //判断是否为null , 为null ， 就无法存储，返回false
        if (TextUtils.isEmpty(rootPath)) {
            return;
        }
        //文件存储的路径
        File file = new File(rootPath, fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean save(String fileName, String data) {
        //获取到根路径
        String rootPath = getRootPath();
        //判断是否为null , 为null ， 就无法存储，返回false
        if (TextUtils.isEmpty(rootPath)) {
            return false;
        }
        //文件存储的路径
        File file = new File(rootPath, fileName);
        try {
            //使用文件输入流
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bos = new BufferedWriter(fw);
            //保存在流里面
            bos.write(data);
            bos.newLine();
            bos.flush();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
