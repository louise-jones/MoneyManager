package com.minidroid.moneymanager.ui.register;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.User;
import com.minidroid.moneymanager.db.SqliteManager;
import com.minidroid.moneymanager.permission.PermissionListener;
import com.minidroid.moneymanager.permission.PermissionManagerActivity;
import com.minidroid.moneymanager.ui.login.LoginActivity;
import com.minidroid.moneymanager.utils.AppUtils;
import com.minidroid.moneymanager.utils.LogUtils;
import com.minidroid.moneymanager.utils.MD5Utils;
import com.minidroid.moneymanager.utils.NetUtils;
import com.minidroid.moneymanager.utils.PhoneValidUtils;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomProgressDialog;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomToast;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 注册界面
 * Created by minidroid on 2017/5/5.
 * Email:460821714@qq.com
 */
public class RegisterActivity extends PermissionManagerActivity implements View.OnClickListener {
    //返回布局
    private RelativeLayout base_rl_back;
    //业务标题文本框
    private TextView base_tv_title;
    //手机号、密码、验证码输入框
    private EditText registeractivity_et_phone, registeractivity_et_password,
            registeractivity_et_checknum;
    //获取验证码、下一步
    private Button registeractivity_btn_getchecknum, registeractivity_btn_next_step;

    private String mPhone, mPassword, mChecknum;
    //验证码计时器
    private TimerCount mTimerCount;
    private CustomProgressDialog mProgressDialog;
    /**
     * 需要的权限数组
     */
    private String[] permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setTranslucentStatus(this);
        setContentView(R.layout.activity_register);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        // 权限数组初始化
        permissions = new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS};

        //计时器，一共60秒，每秒计时
        mTimerCount = new TimerCount(60000, 1000);
        //返回布局
        base_rl_back = (RelativeLayout) findViewById(R.id.base_rl_back);
        base_rl_back.setVisibility(View.VISIBLE);
        //业务标题
        base_tv_title = (TextView) findViewById(R.id.base_tv_title);
        base_tv_title.setText("注册");
        //手机号、密码、验证码输入框
        registeractivity_et_phone = (EditText) findViewById(R.id.registeractivity_et_phone);
        registeractivity_et_password = (EditText) findViewById(R.id.registeractivity_et_password);
        registeractivity_et_checknum = (EditText) findViewById(R.id.registeractivity_et_checknum);
        //获取验证码、下一步
        registeractivity_btn_getchecknum = (Button) findViewById(R.id
                .registeractivity_btn_getchecknum);
        registeractivity_btn_next_step = (Button) findViewById(R.id.registeractivity_btn_next_step);
        //添加监听器
        base_rl_back.setOnClickListener(this);
        registeractivity_btn_getchecknum.setOnClickListener(this);
        registeractivity_btn_next_step.setOnClickListener(this);
        mProgressDialog = CustomProgressDialog.createDialog(this);
        mProgressDialog.setMessage("注册中...");
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;
        mPhone = registeractivity_et_phone.getText().toString();
        mPassword = registeractivity_et_password.getText().toString();
        mChecknum = registeractivity_et_checknum.getText().toString();
        switch (v.getId()) {
            case R.id.base_rl_back:                                 //返回
                finish();
                overridePendingTransition(R.anim.in_load, R.anim.out_load);
                break;
            case R.id.registeractivity_btn_getchecknum:          //获取验证码
                requestPermissions();
                break;
            case R.id.registeractivity_btn_next_step:           //下一步
                register();
                break;
            default:
                break;
        }
    }

    /**
     * 获取短信相关权限
     */
    private void requestPermissions() {
        requestPermissions(permissions, new PermissionListener() {
            @Override
            public void onGranted() {
                LogUtils.i("phone===" + mPhone);
                LogUtils.i("password===" + mPassword);
                //获取验证码
                getCheckNum();
            }

            @Override
            public void onDenied(List<String> deniedPermissionList) {
                for (String permission : deniedPermissionList) {
                    LogUtils.i(permission);
                }
            }
        });
    }

    /**
     * 获取短信验证码
     */
    private void getCheckNum() {
        if ("".equals(mPhone)) {
            CustomToast.showToast(this, "手机号不能为空", Toast.LENGTH_SHORT);
        } else if (!PhoneValidUtils.isPhoneNumberValid(mPhone)) {
            CustomToast.showToast(this, "请输入正确的手机号码", Toast.LENGTH_SHORT);
        } else {
            CustomToast.showToast(this, "正在获取验证码" + mPhone, Toast.LENGTH_SHORT);
            mTimerCount.start();
            BmobSMS.requestSMSCode(mPhone, "理财小能手", new QueryListener<Integer>() {
                @Override
                public void done(Integer smsId, BmobException ex) {
                    if (ex == null) {
                        //验证码发送成功
                        LogUtils.i("bmob", "短信id：" + smsId);//用于查询本次短信发送详情
                    } else {
                        CustomToast.showToast(RegisterActivity.this, "验证码发送失败", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    /**
     * 验证码倒计时内部类
     */
    private class TimerCount extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and
         *                          {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimerCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //在计时过程中按钮设置为不可点击
            registeractivity_btn_getchecknum.setClickable(false);
            //背景设置为灰色
            registeractivity_btn_getchecknum.setBackgroundResource(R.drawable.button_shape_timer);
            //并且将倒计时的秒数显示在Button
            registeractivity_btn_getchecknum.setText(millisUntilFinished / 1000 + "秒重新获取");
        }

        @Override
        public void onFinish() {            //计时器时间到，此时按钮回原来可点的状态
            registeractivity_btn_getchecknum.setClickable(true);
            registeractivity_btn_getchecknum.setBackgroundResource(R.drawable.button_shape);//背景设置回来
            registeractivity_btn_getchecknum.setText("重新验证");
        }
    }

    /**
     * 注册方法
     */
    private void register() {
        if (!NetUtils.isConnected(this)) {
            CustomToast.showToast(this, "没有网络!", Toast.LENGTH_SHORT);
        } else if (!PhoneValidUtils.isPhoneNumberValid(mPhone)) {
            CustomToast.showToast(this, "请输入正确的手机号码", Toast.LENGTH_SHORT);
        } else if ("".equals(mPassword) || !pswFilter(mPassword)) {
            CustomToast.showToast(this, "密码长度6-20 和 字符串只能为字母、数字和下划线", Toast.LENGTH_SHORT);
        } else if ("".equals(mChecknum)) {
            CustomToast.showToast(this, "验证码不能为空", Toast.LENGTH_SHORT);
        } else {
            mProgressDialog.show();
//            checkSmsCode(mPhone, mChecknum);
            signUp();
        }
    }

    /**
     * 不验证验证码注册（供测试）
     */
    private void signUp() {
        mPassword = MD5Utils.string2MD5(mPassword);
        User user = new User();
        user.setMobilePhoneNumber(mPhone);
        user.setUsername(mPhone);
        user.setPassword(mPassword);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User u, BmobException ex) {
                if (ex == null) {
                    mProgressDialog.dismiss();
                    LogUtils.i(u.getUsername() + ";" + u.getMobilePhoneNumber());
                    //将用户信息保存到本地
                    ContentValues values = new ContentValues();
                    values.put("phone", mPhone);
                    values.put("username", mPhone);
                    values.put("password", mPassword);
                    SqliteManager.getInstance(RegisterActivity.this).insertItem
                            ("user", values);

                    CustomToast.showToast(RegisterActivity.this, "注册成功", Toast
                            .LENGTH_SHORT);
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity
                            .class);
                    intent.putExtra("phone", mPhone);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.in_load, R.anim.out_load);
                } else {
                    LogUtils.e(ex.getMessage());
                    if (ex.getErrorCode() == 202) {
                        CustomToast.showToast(RegisterActivity.this, "该手机号已被注册", Toast
                                .LENGTH_SHORT);
                    } else {
                        CustomToast.showToast(RegisterActivity.this, "注册失败", Toast
                                .LENGTH_SHORT);
                    }
                }
            }
        });
    }

    /**
     * 校验验证码
     *
     * @param phone
     * @param checknum
     */
    private void checkSmsCode(String phone, String checknum) {
        mPassword = MD5Utils.string2MD5(mPassword);
        //短信验证码验证，验证通过即可完成注册
        BmobSMS.verifySmsCode(phone, checknum, new UpdateListener() {
            @Override
            public void done(BmobException ex) {    //短信验证码已验证成功
                if (ex == null) {
                    LogUtils.i("bmob", "验证通过");
                    // 开始提交注册信息
                    User user = new User();
                    user.setMobilePhoneNumber(mPhone);
                    user.setUsername(mPhone);
                    user.setPassword(mPassword);
                    user.signUp(new SaveListener<User>() {
                        @Override
                        public void done(User u, BmobException ex) {
                            if (ex == null) {
                                mProgressDialog.dismiss();
                                LogUtils.i(u.getUsername() + ";" + u.getMobilePhoneNumber());
                                //将用户信息保存到本地
                                ContentValues values = new ContentValues();
                                values.put("phone", mPhone);
                                values.put("username", mPhone);
                                values.put("password", MD5Utils.string2MD5(mPassword));
                                SqliteManager.getInstance(RegisterActivity.this).insertItem
                                        ("user", values);

                                CustomToast.showToast(RegisterActivity.this, "注册成功", Toast
                                        .LENGTH_SHORT);
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity
                                        .class);
                                intent.putExtra("phone", mPhone);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.in_load, R.anim.out_load);
                            } else {
                                LogUtils.e(ex.getMessage());
                                if (ex.getErrorCode() == 202) {
                                    CustomToast.showToast(RegisterActivity.this, "该手机号已被注册", Toast
                                            .LENGTH_SHORT);
                                } else {
                                    CustomToast.showToast(RegisterActivity.this, "注册失败", Toast
                                            .LENGTH_SHORT);
                                }
                            }
                        }
                    });
                } else {
                    LogUtils.i("bmob", "验证失败");
                    CustomToast.showToast(RegisterActivity.this, "验证失败", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    /**
     * 使用正则表达式来验证密码的格式
     *
     * @param s
     * @return
     */
    private boolean pswFilter(CharSequence s) {
        if (TextUtils.isEmpty(s)) {
            return false;
        }
        String regex = "^[0-9a-zA-z_]{6,20}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
}
