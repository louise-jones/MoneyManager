package com.minidroid.moneymanager.ui.personalupdate;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.User;
import com.minidroid.moneymanager.permission.PermissionManagerActivity;
import com.minidroid.moneymanager.utils.AppUtils;
import com.minidroid.moneymanager.utils.LogUtils;
import com.minidroid.moneymanager.utils.PhoneValidUtils;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomToast;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import me.james.biuedittext.BiuEditText;

/**
 * 修改个人信息界面
 * Created by minidroid on 2017/5/8.
 * Email:460821714@qq.com
 */
public class UpdatePersonInfoActivity extends PermissionManagerActivity implements View
        .OnClickListener {
    //返回
    private RelativeLayout base_rl_back;
    //业务标题
    private TextView base_tv_title;
    //提示信息
    private TextView update_tv_info;
    //输入框
    private BiuEditText update_et_info;
    //确定按钮
    private Button update_btn_sure;
    //修改的信息
    private String info;
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setTranslucentStatus(this);
        setContentView(R.layout.activity_update_person_info);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        //返回
        base_rl_back = (RelativeLayout) findViewById(R.id.base_rl_back);
        //业务标题
        base_tv_title = (TextView) findViewById(R.id.base_tv_title);
        //提示信息
        update_tv_info = (TextView) findViewById(R.id.update_tv_info);
        //输入框
        update_et_info = (BiuEditText) findViewById(R.id.update_et_info);
        //确定按钮
        update_btn_sure = (Button) findViewById(R.id.update_btn_sure);
        //添加监听器
        base_rl_back.setOnClickListener(this);
        update_btn_sure.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent sourceIntent = getIntent();
        if (sourceIntent != null) {
            flag = sourceIntent.getIntExtra("flag", 0);
            switch (flag) {
                case 1:
                    base_tv_title.setText("修改手机号");
                    update_tv_info.setText("手机号:");
                    setBoundsDrawables(R.drawable.update_iv_phone, update_tv_info);
                    update_et_info.setHint("请输入手机号");
                    break;
                case 2:
                    base_tv_title.setText("修改邮箱");
                    update_tv_info.setText("邮箱:");
                    setBoundsDrawables(R.drawable.update_iv_email, update_tv_info);
                    update_et_info.setHint("请输入邮箱");
                    break;
                case 3:
                    base_tv_title.setText("修改QQ账号");
                    update_tv_info.setText("QQ账号:");
                    setBoundsDrawables(R.drawable.update_iv_qq, update_tv_info);
                    update_et_info.setHint("请输入QQ账号");
                    break;
                case 4:
                    base_tv_title.setText("修改微信账号");
                    update_tv_info.setText("微信账号:");
                    setBoundsDrawables(R.drawable.update_iv_weixin, update_tv_info);
                    update_et_info.setHint("请输入微信账号");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 为TextView设置左边图片
     */
    private void setBoundsDrawables(int resId, TextView view) {
        Drawable drawable = ContextCompat.getDrawable(this, resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        view.setCompoundDrawables(drawable, null, null, null);
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;
        info = update_et_info.getText().toString();
        switch (v.getId()) {
            case R.id.base_rl_back:                     //返回
                finish();
                overridePendingTransition(R.anim.in_load, R.anim.out_load);
                break;
            case R.id.update_btn_sure:           //确定按钮
                motifyWhichAttribute();
                break;
            default:
                break;
        }
    }

    /**
     * 更新哪个属性
     */
    private void motifyWhichAttribute() {
        switch (flag) {
            case 1:                 //更新手机号
                motityPhone();
                break;
            case 2:                //更新邮箱
                motifyEmail();
                break;
            case 3:               //更新QQ账号
                motifyQQ();
                break;
            case 4:              //更新微信账号
                motifyWeixin();
                break;
            default:
                break;
        }
    }

    /**
     * 更新微信账号
     */
    private void motifyWeixin() {
        if (!"".equals(info)) {
            User newUser = new User();
            newUser.setWeixin(info);
            updateUserInfo(newUser);
        } else {
            CustomToast.showToast(this, "微信账号不能为空", Toast.LENGTH_SHORT);
        }
    }

    /**
     * 更新QQ账号
     */
    private void motifyQQ() {
        if (!"".equals(info)) {
            User newUser = new User();
            newUser.setQq(info);
            updateUserInfo(newUser);
        } else {
            CustomToast.showToast(this, "QQ账号不能为空", Toast.LENGTH_SHORT);
        }
    }

    /**
     * 更新邮箱
     */
    private void motifyEmail() {
        if (!"".equals(info)) {
            User newUser = new User();
            newUser.setEmail(info);
            updateUserInfo(newUser);
        } else {
            CustomToast.showToast(this, "邮箱不能为空", Toast.LENGTH_SHORT);
        }
    }

    /**
     * 更新手机号
     */
    private void motityPhone() {
        if (PhoneValidUtils.isPhoneNumberValid(info)) {
            User newUser = new User();
            newUser.setMobilePhoneNumber(info);
            updateUserInfo(newUser);
        } else {
            CustomToast.showToast(this, "无效的手机号", Toast.LENGTH_SHORT);
        }
    }

    /**
     * 更新当前登录用户信息
     *
     * @param newUser
     */
    private void updateUserInfo(User newUser) {
        newUser.update(BmobUser.getCurrentUser(User.class).getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                LogUtils.i("更新用户信息成功");
                Intent intent = new Intent();
                intent.putExtra("info", info);
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.in_load, R.anim.out_load);
            }
        });
    }
}
