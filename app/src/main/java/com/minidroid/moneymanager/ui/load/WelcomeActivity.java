package com.minidroid.moneymanager.ui.load;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.Version;
import com.minidroid.moneymanager.permission.ActivityCollector;
import com.minidroid.moneymanager.ui.login.LoginActivity;
import com.minidroid.moneymanager.utils.AppUtils;
import com.minidroid.moneymanager.utils.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 引导界面
 * Created by minidroid on 2017/4/22.
 * Email:460821714@qq.com
 */
public class WelcomeActivity extends FragmentActivity implements OnPageChangeListener, OnClickListener {//使用Fragment进行逻辑的切换，所以必须继承FragmentActivity
    private ViewPager VP;
    private LinearLayout linear, linear2;
    private ImageView iv2;
    private int[] layoutId = {
            R.layout.welcome_one,
            R.layout.welcome_two,
            R.layout.welcome_three
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setTranslucentStatus(WelcomeActivity.this);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_welcome);
        initView();
    }

    @Override
    protected void onDestroy() {
        ActivityCollector.removeActivity(this);
        super.onDestroy();
    }

    /*初始化整个界面*/
    public void initView() {
        VP = (ViewPager) findViewById(R.id.vp);
        linear = (LinearLayout) findViewById(R.id.linear);
        linear2 = (LinearLayout) findViewById(R.id.linear2);
        linear2.setOnClickListener(this);
        initCircleView();//调用初始化小圆点的界面
        MyAdapter adapter = new MyAdapter(getSupportFragmentManager());
        VP.setAdapter(adapter);
        //setPageTransformer页面滑动监听器，第一个参数是个Boolean类型，第二个则是PageTransfromer接口
        VP.setPageTransformer(true, new MyTransformerPager());
        VP.setOnPageChangeListener(this);
    }

    /*初始化下面小圆点的方法*/
    public void initCircleView() {
        for (int i = 0; i < layoutId.length; i++) {
            ImageView iv = new ImageView(this);
            if (i == 0) {
                iv.setImageResource(R.drawable.circle_two);
            } else {
                iv.setImageResource(R.drawable.circle);
            }
            iv.setPadding(12, 3, 12, 3);
            linear.addView(iv);
        }
    }

    /*自定义ViewPage适配器*/
    class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {//返回每个Fragment。就相当于ListView返回每一个View对象item
            Fragment f = new MyViewPagerFragment();//
            //f.setArguments(position);//返回是哪个页面的布局id
            Bundle b = new Bundle();
            b.putInt("layoutId", layoutId[position]);
            f.setArguments(b);
            return f;
        }

        @Override
        public int getCount() {//首先会调用这个方法

            return 3;
        }

    }

    /*重写页面切换监听器的方法*/
    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int position) {
        linear.removeAllViews();
        setcurrentSelector(position);

    }

    /*页面滑动时小圆点切换的效果实现*/
    private void setcurrentSelector(int position) {
        if (position == 2) {
            iv2 = new ImageView(this);
            iv2.setImageResource(R.drawable.tiyan);
            linear2.addView(iv2);
            linear.removeAllViews();
        } else {
            initCircleView();//如果不是最后的页面则重新需要初始化小圆点
            linear2.removeAllViews();//并把立即体验的按钮给删除掉
        }
        for (int i = 0; i < linear.getChildCount(); i++) {
            ImageView child = (ImageView) linear.getChildAt(i);
            if (i == position) {
                child.setImageResource(R.drawable.circle_two);
            } else {
                child.setImageResource(R.drawable.circle);
            }
        }
    }

    /*点击事件的方法*/
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        writeReadFlag();
        finish();
        overridePendingTransition(R.anim.in_load, R.anim.out_load);
    }

    /**
     * 将版本号写入配置文件
     */
    private void writeReadFlag() {
        File file = null;
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            file = new File(this.getFilesDir() + File.separator + "config" + File.separator + LoadingActivity.CONFIG_NAME);
            if (!file.exists()) file.createNewFile();
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            int code = AppUtils.getVersionCode(this);
            Version version = new Version(code);
            oos.writeObject(version);
            LogUtils.i("WelcomeActivity", "write");
            oos.flush();
        } catch (FileNotFoundException e) {
            LogUtils.i("WelcomeActivity", "error1");
            e.printStackTrace();
        } catch (IOException e) {
            LogUtils.i("WelcomeActivity", "error2");
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
