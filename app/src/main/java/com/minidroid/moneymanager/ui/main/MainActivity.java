package com.minidroid.moneymanager.ui.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.permission.ActivityCollector;
import com.minidroid.moneymanager.utils.AppUtils;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomToast;

/**
 * 主界面
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class MainActivity extends FragmentActivity implements MainFragment.IgoAccountFragment {
    private Fragment mMainFragment, mCountFragment, mMoreFragment;
    private long lastPressTime;
    private RadioButton main_rb_main, main_rb_count, main_rb_more;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        AppUtils.setTranslucentStatus(MainActivity.this);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        initView();
        setListener();
    }

    @Override
    protected void onDestroy() {
        ActivityCollector.removeActivity(this);
        super.onDestroy();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        main_rb_main = (RadioButton) findViewById(R.id.main_rb_main);
        main_rb_count = (RadioButton) findViewById(R.id.main_rb_count);
        main_rb_more = (RadioButton) findViewById(R.id.main_rb_more);
        setTabSelection(0);
    }


    /**
     * 添加监听器
     */
    private void setListener() {
        main_rb_main.setOnClickListener(mListener);
        main_rb_count.setOnClickListener(mListener);
        main_rb_more.setOnClickListener(mListener);
    }

    /**
     * 点击事件监听器
     */
    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == null) return;
            switch (v.getId()) {
                case R.id.main_rb_main:
                    setTabSelection(0);
                    break;
                case R.id.main_rb_count:
                    setTabSelection(1);
                    break;
                case R.id.main_rb_more:
                    setTabSelection(2);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 按两下退出应用
     */
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPressTime <= 2000) {
            finish();
        } else {
            CustomToast.showToast(this, "再按一次退出应用", Toast.LENGTH_SHORT);
            lastPressTime = currentTime;
        }
    }

    private void setTabSelection(int index) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (index) {
            case 0:
                if (mMainFragment == null) {
                    mMainFragment = new MainFragment();
                    transaction.add(R.id.mainactivity_fl_space, mMainFragment);
                } else {
                    transaction.show(mMainFragment);
                }
                main_rb_main.setChecked(true);
                break;
            case 1:
                if (mCountFragment == null) {
                    mCountFragment = new AccountFragment();
                    transaction.add(R.id.mainactivity_fl_space, mCountFragment);
                } else {
                    transaction.show(mCountFragment);
                }
                main_rb_count.setChecked(true);
                break;
            case 2:
                if (mMoreFragment == null) {
                    mMoreFragment = new MoreFragment();
                    transaction.add(R.id.mainactivity_fl_space, mMoreFragment);
                } else {
                    transaction.show(mMoreFragment);
                }
                main_rb_more.setChecked(true);
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mMainFragment != null) {
            transaction.hide(mMainFragment);
            main_rb_main.setChecked(false);
        }
        if (mCountFragment != null) {
            transaction.hide(mCountFragment);
            main_rb_count.setChecked(false);
        }
        if (mMoreFragment != null) {
            transaction.hide(mMoreFragment);
            main_rb_more.setChecked(false);
        }
    }

    @Override
    public void showAccountFragment() {
        setTabSelection(1);
    }
}
