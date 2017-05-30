package com.minidroid.moneymanager.ui.input;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.permission.PermissionManagerActivity;


/**
 * 修改账户余额界面
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class InputActivity extends PermissionManagerActivity {
    private TextView inputactivity_tv_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppUtils.setTranslucentStatus(InputActivity.this);
        setContentView(R.layout.activity_input);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        inputactivity_tv_show = (TextView) findViewById(R.id.inputactivity_tv_show);
    }

    public void keyClick(View v) {
        if (v == null) return;
        switch (v.getId()) {
            case R.id.key_x:
                inputactivity_tv_show.setText(null);
                break;
            case R.id.key_ok:
                Intent intent = getIntent();
                intent.putExtra("update_money", inputactivity_tv_show.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
        if (inputactivity_tv_show.getText().toString().length() == 8) return;
        if (inputactivity_tv_show.getText().toString().contains(".")) {
            if (!inputactivity_tv_show.getText().toString().endsWith(".")) {
                String[] array = inputactivity_tv_show.getText().toString().split("\\.");
                if (array[1] != null)
                    if (array[1].length() == 2) return;
            }
        } else {
            if (inputactivity_tv_show.getText().toString() != null && inputactivity_tv_show.getText().toString().length() != 0 && Double.parseDouble(inputactivity_tv_show.getText().toString()) > 100000) {
                return;
            }
        }
        switch (v.getId()) {
            case R.id.key_0:
                if (inputactivity_tv_show.getText().toString().length() != 0) {
                    inputactivity_tv_show.append("0");
                }
                break;
            case R.id.key_1:
                inputactivity_tv_show.append("1");
                break;
            case R.id.key_2:
                inputactivity_tv_show.append("2");
                break;
            case R.id.key_3:
                inputactivity_tv_show.append("3");
                break;
            case R.id.key_4:
                inputactivity_tv_show.append("4");
                break;
            case R.id.key_5:
                inputactivity_tv_show.append("5");
                break;
            case R.id.key_6:
                inputactivity_tv_show.append("6");
                break;
            case R.id.key_7:
                inputactivity_tv_show.append("7");
                break;
            case R.id.key_8:
                inputactivity_tv_show.append("8");
                break;
            case R.id.key_9:
                inputactivity_tv_show.append("9");
                break;
            case R.id.key_point:
                if (inputactivity_tv_show.getText().toString().length() != 0)
                    if (!inputactivity_tv_show.getText().toString().contains("."))
                        inputactivity_tv_show.append(".");
                break;
            default:
                break;
        }
    }
}
