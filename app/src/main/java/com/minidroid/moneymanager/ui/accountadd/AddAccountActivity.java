package com.minidroid.moneymanager.ui.accountadd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.Account;
import com.minidroid.moneymanager.permission.PermissionManagerActivity;
import com.minidroid.moneymanager.ui.input.InputActivity;
import com.minidroid.moneymanager.ui.login.LoginActivity;
import com.minidroid.moneymanager.utils.AppUtils;
import com.minidroid.moneymanager.utils.LogUtils;
import com.minidroid.moneymanager.utils.UserFactory;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomToast;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


/**
 * 添加账户界面
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class AddAccountActivity extends PermissionManagerActivity {
    private EditText accountadd_et_name;
    private TextView accountadd_tv_count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setTranslucentStatus(AddAccountActivity.this);
        setContentView(R.layout.activity_add_account);
        initView();

    }

    /**
     * 初始化界面
     */
    private void initView() {
        accountadd_et_name = (EditText) findViewById(R.id.accountadd_et_name);
        accountadd_tv_count = (TextView) findViewById(R.id.accountadd_tv_count);
    }

    public void onClick(View v) {
        if (v == null) return;
        switch (v.getId()) {
            case R.id.baseactivity_ib_return:
                finish();
                break;
            case R.id.accountadd_ll_count:
                Intent intent = new Intent(this, InputActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.baseactivity_ib_ok:
                if (accountadd_et_name.getText().toString().length() > 1 && !accountadd_tv_count.getText().toString().equals("00.00")) {
                    addCount();
                } else if (accountadd_tv_count.getText().toString().equals("00.00")) {
                    CustomToast.showToast(AddAccountActivity.this, "金额不能为0", Toast.LENGTH_SHORT);
                    return;
                } else {
                    CustomToast.showToast(AddAccountActivity.this, "账户长度不能小于2", Toast.LENGTH_SHORT);
                    return;
                }
                break;
            default:
                break;
        }
    }

    /**
     * 将新增账户添加至数据库
     */
    private void addCount() {
        //保存到Bmob
        if (UserFactory.currentLoginUser != null) {
            Account account = new Account();
            account.setAccountName(accountadd_et_name.getText().toString());
            account.setNumber(Double.valueOf(accountadd_tv_count.getText().toString()));
            account.setUser(UserFactory.currentLoginUser);
            account.save(new SaveListener<String>() {
                @Override
                public void done(String objectId, BmobException ex) {
                    if (ex == null) {
                        LogUtils.i("Bmob账户保存成功,objectId:" + objectId);
                        CustomToast.showToast(AddAccountActivity.this, "添加成功", Toast.LENGTH_SHORT);
                    } else {
                        LogUtils.i("Bmob账户保存失败:" + ex.getMessage());
                    }
                    finish();
                }
            });
        } else {
            LogUtils.i("当前没有登录");
            CustomToast.showToast(this, "当前没有登录", Toast.LENGTH_LONG);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        //保存到本地数据库
        /*if (SqliteManager.getInstance(this).isExistInTable("account", "accountname=?", new
        String[]{accountadd_et_name.getText().toString()})) {
            CustomToast.showToast(this, "该账户已存在", Toast.LENGTH_SHORT);
        } else {
            ContentValues values = new ContentValues();
            values.put("accountname", accountadd_et_name.getText().toString());
            values.put("money", accountadd_tv_count.getText().toString());
            SqliteManager.getInstance(this).insertItem("account", values);
            CustomToast.showToast(this, "添加成功", Toast.LENGTH_SHORT);
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            accountadd_tv_count.setText(data.getStringExtra("update_money"));
        }
    }
}
