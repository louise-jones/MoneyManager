package com.minidroid.moneymanager.ui.load;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.Version;
import com.minidroid.moneymanager.db.SqliteManager;
import com.minidroid.moneymanager.permission.PermissionListener;
import com.minidroid.moneymanager.permission.PermissionManagerActivity;
import com.minidroid.moneymanager.ui.login.LoginActivity;
import com.minidroid.moneymanager.utils.AppUtils;
import com.minidroid.moneymanager.utils.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 加载界面
 * Created by minidroid on 2017/4/22.
 * Email:460821714@qq.com
 */
public class LoadingActivity extends PermissionManagerActivity {
    public static final String CONFIG_NAME = "config.sys";

    private ImageView iv_icon;
    private RelativeLayout rl_background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setTranslucentStatus(this);
        getPermission();

//        initView();
    }

    /**
     * 获取操作SD卡的权限
     */
    private void getPermission() {
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        PermissionManagerActivity.requestPermissions(permissions, new PermissionListener() {
            @Override
            public void onGranted() {
                setContentView(R.layout.activity_loading);
                initView();
            }

            @Override
            public void onDenied(List<String> deniedPermissionList) {
                finish();
            }
        });
    }

    /**
     * 初始化时间
     */
    private void initTime() {
        if (!SqliteManager.getInstance(this).isExistInTable("time", "time=?", new String[]{"firsttime"})) {
            ContentValues values = new ContentValues();
            values.put("time", "firsttime");
            values.put("value", System.currentTimeMillis());
            SqliteManager.getInstance(this).insertItem("time", values);
        }

        /*if (!SqliteManager.getInstance(this).isExistInTable("time", "time=?", new String[]{"bdtime"})) {
            ContentValues values = new ContentValues();
            values.put("time", "bdtime");
            values.put("value", 0);
            SqliteManager.getInstance(this).insertItem("time", values);
        }*/
    }
    /*初始化视图*/
    private void initView() {
        initTime();
        rl_background = (RelativeLayout) findViewById(R.id.rl_background);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        final Animation icon_animation = AnimationUtils.loadAnimation(this, R.anim.icon_animation);
        icon_animation.setFillAfter(true);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.loading_animation);
        animation.setFillAfter(true);//设置动画停留在最后一帧，不会自动回到原来的状态，记住只能在java设置，在XML中设置是不起作用的
        rl_background.startAnimation(animation);
        iv_icon.startAnimation(icon_animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //切换页面overridePendingTransition第一个参数是进入屏幕的动画的资源id，第二个则是退出屏幕的动画资源id
                openActivity();
                finish();
                overridePendingTransition(R.anim.in_load, R.anim.out_load);
            }

            private void openActivity() {
                Intent intent = new Intent();
                if (isNewVersion()) {   //有更新
                    intent.setClass(LoadingActivity.this, WelcomeActivity.class);
                } else {
                    intent.setClass(LoadingActivity.this, LoginActivity.class);
                }
                startActivity(intent);
            }
        });

        TextView tv_t = (TextView) findViewById(R.id.load_time_t);
        TextView tv_b = (TextView) findViewById(R.id.load_time_b);
        String[] weeks = new String[]{"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance(Locale.CHINA);//创建一个日历对象
        tv_t.setText(calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月"
                + calendar.get(Calendar.DAY_OF_MONTH) + "日" + weeks[calendar.get(Calendar.DAY_OF_WEEK) - 1]);

        SqliteManager.QueryResult result = SqliteManager.getInstance(this).query("time", "time=?", new String[]{"firsttime"});
        if (result.cursor.getCount() != 0) {
            result.cursor.moveToFirst();
            long start = result.cursor.getLong(result.cursor.getColumnIndex("value"));
            long end = System.currentTimeMillis();
            int d = getCount(new Date(start), new Date(end));
            tv_b.setText("你的记账第" + d + "天");
        }
    }

    /**
     * 统计记账的天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    private int getCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
       /* toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);*/

        return 1 + (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     * 判断版本是否更新（通过配置文件中的版本号比较）
     *
     * @return
     */
    public boolean isNewVersion() {
        File file = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            file = new File(this.getFilesDir() + File.separator + "config" + File.separator + CONFIG_NAME);
            if (!file.exists()) return true;
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            Version v = (Version) ois.readObject();
            LogUtils.i("LoadingActivity", v.versionCode + ":" + AppUtils.getVersionCode(this));
            if (v.versionCode < AppUtils.getVersionCode(this)) {
                return true;
            }
        } catch (FileNotFoundException e) {
            LogUtils.i("LoadingActivity", "error1");
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            LogUtils.i("LoadingActivity", "error2");
            e.printStackTrace();
        } catch (IOException e) {
            LogUtils.i("LoadingActivity", "error3");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            LogUtils.i("LoadingActivity", "error4");
            e.printStackTrace();
        }
        return false;
    }
}
