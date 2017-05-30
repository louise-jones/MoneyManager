package com.minidroid.moneymanager.ui.about;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.permission.PermissionListener;
import com.minidroid.moneymanager.permission.PermissionManagerActivity;
import com.minidroid.moneymanager.ui.invest.InvestActivity;
import com.minidroid.moneymanager.utils.AppUtils;
import com.minidroid.moneymanager.utils.LogUtils;

import java.util.List;


/**
 * 关于我们界面
 * Created by minidroid on 2017/4/23.
 * Email:460821714@qq.com
 */
public class AboutActivity extends PermissionManagerActivity implements View.OnClickListener {

    private ImageButton baseactivity_ib_return;
    private TextView baseactivity_tv_title;
    private TextView aboutactivity_about_tv_contact, aboutactivity_about_tv_recommend, aboutactivity_about_tv_service;

    /**
     * 需要的权限数组
     */
    private String[] permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setTranslucentStatus(AboutActivity.this);
        setContentView(R.layout.activity_about);
        initView();
        setListener();
    }

    /**
     * 设置监听器
     */
    private void setListener() {
        baseactivity_ib_return.setOnClickListener(this);
        aboutactivity_about_tv_contact.setOnClickListener(this);
        aboutactivity_about_tv_recommend.setOnClickListener(this);
        aboutactivity_about_tv_service.setOnClickListener(this);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        // 权限数组初始化
        permissions = new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS};

        ((ImageButton)findViewById(R.id.baseactivity_ib_ok)).setVisibility(TextView.GONE);
        baseactivity_ib_return = (ImageButton) findViewById(R.id.baseactivity_ib_return);
        baseactivity_tv_title = (TextView) findViewById(R.id.baseactivity_tv_title);
        aboutactivity_about_tv_contact = (TextView) findViewById(R.id.aboutactivity_about_tv_contact);
        aboutactivity_about_tv_recommend = (TextView) findViewById(R.id.aboutactivity_about_tv_recommend);
        aboutactivity_about_tv_service = (TextView) findViewById(R.id.aboutactivity_about_tv_service);

        baseactivity_tv_title.setText("关于我们");
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;
        switch (v.getId()) {
            case R.id.baseactivity_ib_return:           //返回
                finish();
                break;
            case R.id.aboutactivity_about_tv_contact:  //联系我们
                contactWithQQ();
                break;
            case R.id.aboutactivity_about_tv_recommend://理财推荐
                Intent intent = new Intent(this, InvestActivity.class);
                startActivity(intent);
                break;
            case R.id.aboutactivity_about_tv_service:   //联系客服
                requestPermissions();
                break;
            default:
                break;
        }
    }

    /**
     * 申请打电话的权限
     */
    private void requestPermissions() {
        requestPermissions(permissions, new PermissionListener() {
            @Override
            public void onGranted() {   // 全部授权成功回调
                LogUtils.i("所有权限都已授权");
                call();
            }

            @Override
            public void onDenied(List<String> deniedPermissionList) {   // 部分或全部未授权回调
                StringBuilder sb = new StringBuilder();
                for (String permission : deniedPermissionList) {
                    sb.append(permission);
                }
                LogUtils.i("未授权的权限：" + sb.toString());
            }
        });
    }

    /**
     * 打电话业务逻辑
     */
    private void call() {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:10086"));
            startActivity(intent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * 弹出QQ聊天窗口
     */
    private void contactWithQQ() {
        String url="mqqwpa://im/chat?chat_type=wpa&uin=460821714";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
