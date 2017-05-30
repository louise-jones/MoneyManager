package com.minidroid.moneymanager.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.User;
import com.minidroid.moneymanager.db.SqliteManager;
import com.minidroid.moneymanager.permission.PermissionManagerActivity;
import com.minidroid.moneymanager.ui.main.MainActivity;
import com.minidroid.moneymanager.ui.register.RegisterActivity;
import com.minidroid.moneymanager.utils.AppUtils;
import com.minidroid.moneymanager.utils.Constants;
import com.minidroid.moneymanager.utils.LogUtils;
import com.minidroid.moneymanager.utils.MD5Utils;
import com.minidroid.moneymanager.utils.NetUtils;
import com.minidroid.moneymanager.utils.UserFactory;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomProgressDialog;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomToast;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static cn.bmob.v3.BmobUser.getCurrentUser;

/**
 * 登录界面
 * Created by minidroid on 2017/5/4.
 * Email:460821714@qq.com
 */
public class LoginActivity extends PermissionManagerActivity implements View.OnClickListener, View
        .OnFocusChangeListener {
    private long lastPressTime;
    private TextView base_tv_title;
    //注册、找回密码
    private EditText loginactivity_et_username, loginactivity_et_password;
    //登录按钮
    private Button loginactivity_btn_login;
    //注册、找回密码
    private TextView loginactivity_tv_register, loginactivity_tv_find_password;
    //微信、QQ、新浪微博登录
    private ImageButton loginactivity_ib_weixin, loginactivity_ib_qq, loginactivity_ib_sina;
    //登录用户名、密码
    private String mUserName, mPassword;

    private static Tencent mTencent;
    //自定义加载框
    private CustomProgressDialog mProgressDialog;
    //授权登录监听器
    private IUiListener loginListener;
    //获取用户信息监听器
    private IUiListener userInfoListener;
    //qq用户信息
    private UserInfo mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setTranslucentStatus(this);
        setContentView(R.layout.activity_login);
        initView();
        initData();
    }

    private void initData() {
        if (mTencent == null) {
            //传入参数APPID和全局Context上下文
            mTencent = Tencent.createInstance(Constants.QQ_APP_ID, getApplicationContext());
        }
        loginListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                if (o != null) {
                    JSONObject jsonObject = (JSONObject) o;
                    try {
                        final String openId = jsonObject.getString(com.tencent.connect.common
                                .Constants
                                .PARAM_OPEN_ID);
                        final String accessToken = jsonObject.getString(com.tencent.connect.common
                                .Constants.PARAM_ACCESS_TOKEN);
                        final String expiresIn = jsonObject.getString(com.tencent.connect.common
                                .Constants
                                .PARAM_EXPIRES_IN);
                        mTencent.setOpenId(openId);
                        mTencent.setAccessToken(accessToken, expiresIn);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(UiError uiError) {
                LogUtils.i("QQ授权出错...");
                mProgressDialog.dismiss();
                CustomToast.showToast(LoginActivity.this, "QQ授权出错:" + uiError.errorCode + "--" +
                        uiError.errorDetail, Toast.LENGTH_SHORT);
            }

            @Override
            public void onCancel() {
                LogUtils.i("取消QQ授权...");
                mProgressDialog.dismiss();
                CustomToast.showToast(LoginActivity.this, "取消QQ授权", Toast.LENGTH_SHORT);
            }
        };
        userInfoListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                LogUtils.i("QQ信息:" + o.toString());
                JSONObject qqObject = (JSONObject) o;
                try {
                    String nickName = qqObject.getString("nickname");
                    String headPath = qqObject.getString("figureurl_qq_2");
                    User user = new User();
                    user.setUsername(nickName);
                    user.setHead(headPath);
                    BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth(BmobUser
                            .BmobThirdUserAuth.SNS_TYPE_QQ, mTencent.getAccessToken(), mTencent
                            .getExpiresIn() + "", mTencent.getOpenId());
                    loginWithAuth(authInfo, user);
                } catch (JSONException e) {
                    LogUtils.e(e.getMessage());
                }
            }

            @Override
            public void onError(UiError uiError) {
                mProgressDialog.dismiss();
                CustomToast.showToast(LoginActivity.this, "第三方登录失败", Toast
                        .LENGTH_SHORT);
                LogUtils.i("获取QQ信息出错:" + uiError.errorCode + "--" +
                        uiError.errorDetail);
            }

            @Override
            public void onCancel() {
                mProgressDialog.dismiss();
                CustomToast.showToast(LoginActivity.this, "第三方登录失败", Toast
                        .LENGTH_SHORT);
                LogUtils.i("取消获取QQ信息");
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取这个缓存的用户对象来进行登录
        User mUser = getCurrentUser(User.class);
        if (mUser != null) {
            UserFactory.currentLoginUser = mUser;
            LogUtils.i("当前登录用户名:" + mUser.getUsername());
            loginactivity_et_username.setText(mUser.getUsername());
            openActivity(MainActivity.class);
        } else {
            Intent intent = getIntent();
            if (intent != null) {
                loginactivity_et_username.setText(intent.getStringExtra("phone"));
            }
        }
    }

    /**
     * 初始化界面
     */
    private void initView() {
        //业务标题
        base_tv_title = (TextView) findViewById(R.id.base_tv_title);
        base_tv_title.setText("登录");
        //用户名、密码输入框
        loginactivity_et_username = (EditText) findViewById(R.id.loginactivity_et_username);
        loginactivity_et_password = (EditText) findViewById(R.id.loginactivity_et_password);
        //登录按钮
        loginactivity_btn_login = (Button) findViewById(R.id.loginactivity_btn_login);
        //注册、找回密码
        loginactivity_tv_register = (TextView) findViewById(R.id.loginactivity_tv_register);
        loginactivity_tv_find_password = (TextView) findViewById(R.id
                .loginactivity_tv_find_password);
        //微信、qq、新浪微博登录
        loginactivity_ib_weixin = (ImageButton) findViewById(R.id.loginactivity_ib_weixin);
        loginactivity_ib_qq = (ImageButton) findViewById(R.id.loginactivity_ib_qq);
        loginactivity_ib_sina = (ImageButton) findViewById(R.id.loginactivity_ib_sina);
        //添加监听器
        loginactivity_btn_login.setOnClickListener(this);
        loginactivity_tv_register.setOnClickListener(this);
        loginactivity_tv_find_password.setOnClickListener(this);
        loginactivity_et_username.setOnFocusChangeListener(this);
        loginactivity_et_password.setOnFocusChangeListener(this);
        loginactivity_ib_weixin.setOnClickListener(this);
        loginactivity_ib_qq.setOnClickListener(this);
        loginactivity_ib_sina.setOnClickListener(this);
        mProgressDialog = CustomProgressDialog.createDialog(this);
    }


    @Override
    public void onClick(View v) {
        if (v == null) return;
        switch (v.getId()) {
            case R.id.loginactivity_btn_login:          //登录
                login();
                break;
            case R.id.loginactivity_tv_register:        //注册
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_load, R.anim.out_load);
                break;
            case R.id.loginactivity_tv_find_password:   //找回密码
                break;
            case R.id.loginactivity_ib_weixin:           //微信账号登录
                break;
            case R.id.loginactivity_ib_qq:               //qq账号登录
                qqAuthorize();
                break;
            case R.id.loginactivity_ib_sina:            //新浪微博登录
                break;
            default:
                break;
        }
    }

    /**
     * 通过QQ账号进行授权并登录
     */
    private void qqAuthorize() {
        LogUtils.i("QQ授权中...");
        mProgressDialog.setMessage("加载中...");
        mProgressDialog.show();
        //注销登录(即清除缓存用户对象)
        mTencent.logout(this);
        /**通过这句代码，SDK实现了QQ的登录，这个方法有三个参数，第一个参数是context上下文，第二个参数SCOPO 是一个String类型的字符串，表示一些权限
         官方文档中的说明：应用需要获得哪些API的权限，由“，”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
         第三个参数，是一个事件监听器，IUiListener接口的实例，这里用的是该接口的实现类 */
        mTencent.login(this, "all", loginListener);
    }

    /**
     * 授权之后登录到Bmob(更新登录者的信息)
     *
     * @param authInfo
     */
    private void loginWithAuth(final BmobUser.BmobThirdUserAuth authInfo, final User u) {
        BmobUser.loginWithAuthData(authInfo, new LogInListener<JSONObject>() {
            @Override
            public void done(JSONObject userAuth, BmobException ex) {
                if (ex == null) {
                    u.update(BmobUser.getCurrentUser(User.class).getObjectId(), new
                            UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        LogUtils.i("更新用户成功");
                                    }
                                }
                            });
                    UserFactory.currentLoginUser = BmobUser.getCurrentUser(User.class);
                    mProgressDialog.dismiss();
                    LogUtils.i(authInfo.getSnsType() + "登录成功返回:" + userAuth);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("json", userAuth.toString());
                    intent.putExtra("from", authInfo.getSnsType());
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.in_load, R.anim.out_load);
                } else {
                    CustomToast.showToast(LoginActivity.this, "第三方登录失败：" + ex.getMessage(), Toast
                            .LENGTH_SHORT);
                }
            }
        });
    }

    /**
     * 在调用Login的Activity或者Fragment中重写onActivityResult方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_LOGIN) {
            if (resultCode == -1) {
                LogUtils.i("onActivityResult loginListener...");
                Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
                Tencent.handleResultData(data, loginListener);
                mUserInfo = new UserInfo(this, mTencent.getQQToken());
                mUserInfo.getUserInfo(userInfoListener);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 登录方法
     */
    private void login() {
        mUserName = loginactivity_et_username.getText().toString();
        mPassword = MD5Utils.string2MD5(loginactivity_et_password.getText().toString());
        if (!NetUtils.isConnected(this)) {
            CustomToast.showToast(this, "亲,网络不给力呀,已经切换到本地验证登录", Toast.LENGTH_SHORT);
            SqliteManager.QueryResult result = SqliteManager.getInstance(this).query("user",
                    "(username = ? and password = ?) | (phone = ? and password = ?) | (email = ? " +
                            "and password = ?)", new String[]{mUserName, mPassword, mUserName,
                            mPassword, mUserName, mPassword});
            if (result.cursor.getCount() != 0) {
                CustomToast.showToast(this, "欢迎回到理财小能手", Toast.LENGTH_SHORT);
                openActivity(MainActivity.class);
            } else {
                CustomToast.showToast(this, "账号或密码错误", Toast.LENGTH_SHORT);
            }
        } else if ("".equals(mUserName) || "".equals(mPassword)) {
            CustomToast.showToast(this, "账号或密码不能为空", Toast.LENGTH_SHORT);
        } else {
            mProgressDialog.setMessage("登录中...");
            mProgressDialog.show();
            User user = new User();
            user.setUsername(mUserName);
            user.setPassword(mPassword);
            user.login(new SaveListener<User>() {
                @Override
                public void done(User u, BmobException ex) {
                    mProgressDialog.dismiss();
                    if (ex == null) {
                        User loginUser = getCurrentUser(User.class);
                        UserFactory.currentLoginUser = loginUser;
                        LogUtils.i("当前登录用户信息：" + loginUser.getUsername() + ";" + loginUser
                                .getMobilePhoneNumber());
                        CustomToast.showToast(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT);
                        openActivity(MainActivity.class);
                    } else {
                        if (ex.getErrorCode() == 207) {
                            CustomToast.showToast(LoginActivity.this, "该手机号未注册", Toast
                                    .LENGTH_SHORT);
                        } else {
                            CustomToast.showToast(LoginActivity.this, "用户名或密码错误", Toast
                                    .LENGTH_SHORT);
                        }
                    }
                }
            });
        }
    }

    /**
     * 页面跳转
     */
    private void openActivity(Class target) {
        Intent intent = new Intent(this, target);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.in_load, R.anim.out_load);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText tempView = (EditText) v;
        if (!hasFocus) {// 失去焦点
            tempView.setHint(tempView.getTag().toString());
        } else {
            String hint = tempView.getHint().toString();
            tempView.setTag(hint);
            tempView.setHint("");
        }
    }

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
}
