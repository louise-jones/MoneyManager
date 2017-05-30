package com.minidroid.moneymanager.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.permission.PermissionManagerActivity;


/**
 * 基础Activity
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class BaseActivity extends PermissionManagerActivity {
    protected ImageButton baseactivity_ib_return;
    protected TextView baseactivity_tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParentView();
        setParentListener();
    }

    protected void initParentView() {
        baseactivity_ib_return = (ImageButton) findViewById(R.id.baseactivity_ib_return);
        baseactivity_tv_title = (TextView) findViewById(R.id.baseactivity_tv_title);
    }

    protected void setParentListener() {
        baseactivity_ib_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
