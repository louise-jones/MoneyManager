package com.minidroid.moneymanager.ui.load;

import android.app.Application;
import android.content.Context;

import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;
import com.minidroid.moneymanager.utils.Constants;

import java.io.File;

import cn.bmob.v3.Bmob;

/**
 * Created by minidroid on 2017/4/20 18:30.
 * csdn:http://blog.csdn.net/qq_22063697
 */
public class MyApplication extends Application {
    public String mConfigDir;
    public static Context mContext;

    @Override
    public void onCreate() {
        //初始化Bmob
        Bmob.initialize(this, Constants.BMOB_APPLICATION_ID);
        //初始化选择图片
        FunctionOptions options = new FunctionOptions.Builder()
                .setType(FunctionConfig.TYPE_IMAGE)         //图片or视频:图片
                .setCompress(true)                          //是否压缩：是
                .setMaxSelectNum(1)
                .setMinSelectNum(1)
                .create();
        PictureConfig.getInstance().init(options);
        //创建配置目录的文件夹
        mConfigDir = this.getFilesDir() + "/config";
        makeConfigDir();
        mContext = this;
        super.onCreate();
    }

    /**
     * 创建配置目录的文件夹
     */
    private void makeConfigDir() {
        File file = new File(mConfigDir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
