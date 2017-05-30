package com.minidroid.moneymanager.ui.centerperson;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
import com.luck.picture.lib.model.PictureConfig;
import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.User;
import com.minidroid.moneymanager.permission.PermissionListener;
import com.minidroid.moneymanager.permission.PermissionManagerActivity;
import com.minidroid.moneymanager.ui.login.LoginActivity;
import com.minidroid.moneymanager.ui.personalupdate.UpdatePersonInfoActivity;
import com.minidroid.moneymanager.utils.AppUtils;
import com.minidroid.moneymanager.utils.FormatUtils;
import com.minidroid.moneymanager.utils.LogUtils;
import com.minidroid.moneymanager.view.swipemenulistview.custom.AnimDownloadProgressButton;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomImageView;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomToast;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.entity.LocalMedia;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * 个人中心界面
 * Created by minidroid on 2017/5/6.
 * Email:460821714@qq.com
 */
public class PersonCenterActivity extends PermissionManagerActivity implements View
        .OnClickListener {
    //请求修改手机号的标识符
    private static final int UPDATE_PHONE = 1;
    //请求修改邮箱的标识符
    private static final int UPDATE_EMAIL = 2;
    //请求修改QQ账号的标识符
    private static final int UPDATE_QQ = 3;
    //请求修改微信账号的标识符
    private static final int UPDATE_WEIXIN = 4;
    //返回
    private RelativeLayout base_rl_back;
    //业务标题
    private TextView base_tv_title;
    //当前登录用户
    private User loginUser;
    //用户昵称
    private TextView personal_tv_nikename;
    //用户头像
    private CustomImageView personal_customiv_headview;
    //性别、生日、手机、邮箱、qq账号、微信账号布局
    private RelativeLayout personal_rl_gender, personal_rl_birthday, personal_rl_phone,
            personal_rl_email, personal_rl_qq, personal_rl_weixin;
    //性别、生日、手机、邮箱、qq账号、微信账号
    private TextView personal_tv_gender, personal_tv_birthday, personal_tv_phone,
            personal_tv_email, personal_tv_qq, personal_tv_weinxin;
    //同步按钮
    private AnimDownloadProgressButton mAnimDownloadProgressButton;
    //同步时间
    private TextView personal_tv_synchronization_time;
    //上一次同步时间
    private String mLastSynchronizationTime;
    //当前同步进度
    private int mProgress = 0;
    //更新同步进度的标识符
    private static final int REFRESH_PROGRESS = 0x10;
    //退出登录按钮
    private Button personal_btn_logout;
    //数据实时同步
    BmobRealTimeData tra = null;
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_PROGRESS:
                    if (mProgress < 40) {
                        mProgress += (new Random().nextInt(3) + 1);
                        mAnimDownloadProgressButton.setState(AnimDownloadProgressButton
                                .DOWNLOADING);
                        mAnimDownloadProgressButton.setProgressText("同步中", mProgress);
                        // 随机800ms以内刷新一次
                        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS,
                                new Random().nextInt(800));
                        mAnimDownloadProgressButton.setProgress(mProgress);
                    } else if (mProgress < 100) {
                        mProgress += (new Random().nextInt(3) + 4);
                        mAnimDownloadProgressButton.setState(AnimDownloadProgressButton
                                .DOWNLOADING);
                        mAnimDownloadProgressButton.setProgressText("同步中", mProgress);
                        // 随机1200ms以内刷新一次
                        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS,
                                new Random().nextInt(1200));
                        mAnimDownloadProgressButton.setProgress(mProgress);
                    } else {
                        mAnimDownloadProgressButton.setState(AnimDownloadProgressButton.INSTALLING);
                        mAnimDownloadProgressButton.setCurrentText("同步完成");
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                mAnimDownloadProgressButton.setState(AnimDownloadProgressButton
                                        .NORMAL);
                                mAnimDownloadProgressButton.setCurrentText("云同步");
                                CustomToast.showToast(PersonCenterActivity.this, "同步完成", Toast
                                        .LENGTH_SHORT);
                                updateSynchronizationTime();
                            }
                        }, 2000);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    //选择图片完成回调
    private PictureConfig.OnSelectResultCallback mResultCallback = new PictureConfig
            .OnSelectResultCallback() {
        //多选回调
        @Override
        public void onSelectSuccess(List<LocalMedia> list) {
            LogUtils.i("多选回调====" + list.size());
            personal_customiv_headview.setImageBitmap(getLocalBitmap(list.get(0).getPath()));
            uploadFile(new BmobFile(new File(list.get(0).getPath())));
        }

        //单选回调
        @Override
        public void onSelectSuccess(LocalMedia localMedia) {
            LogUtils.i("单选回调...");
        }
    };

    /**
     * 上传文件到Bmob
     *
     * @param file
     */
    private void uploadFile(final BmobFile file) {
        file.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    LogUtils.i("上传文件成功:" + file.getFileUrl());
                    User newUser = new User();
                    newUser.setHead(file.getFileUrl());
                    updateUserInfo(newUser);
                } else {
                    LogUtils.i("上传文件失败:" + e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setTranslucentStatus(this);
        setContentView(R.layout.activity_person_center);
        initView();
    }

    /**
     * 更新当前登录用户信息
     *
     * @param newUser
     */
    private void updateUserInfo(User newUser) {
        newUser.update(loginUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                LogUtils.i("更新用户信息成功...");
            }
        });
    }

    /**
     * 获取操作SD卡的权限
     */
    private void requestPermissions() {
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest
                .permission.WRITE_EXTERNAL_STORAGE};
        PermissionManagerActivity.requestPermissions(permissions, new PermissionListener() {
            @Override
            public void onGranted() {
                //打开选择图片
                PictureConfig.getInstance().openPhoto(PersonCenterActivity.this, mResultCallback);
            }

            @Override
            public void onDenied(List<String> deniedPermissionList) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*loginUser = BmobUser.getCurrentUser(User.class);
        if (loginUser != null) {
            personal_tv_nikename.setText(loginUser.getUsername());
            //请求图片
            isRunning = true;
            mHandlerThread.run();
        }*/
    }

    /**
     * 获取本地图片
     *
     * @param path
     * @return
     */
    private Bitmap getLocalBitmap(String path) {
        Bitmap image = null;
        File imageFile = new File(path);
        if (imageFile.exists()) {
            image = BitmapFactory.decodeFile(path);
        }
        return image;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        tra = new BmobRealTimeData();
        //返回
        base_rl_back = (RelativeLayout) findViewById(R.id.base_rl_back);
        //业务标题
        base_tv_title = (TextView) findViewById(R.id.base_tv_title);
        base_tv_title.setText("个人中心");
        //用户昵称
        personal_tv_nikename = (TextView) findViewById(R.id.personal_tv_nikename);
        //用户头像
        personal_customiv_headview = (CustomImageView) findViewById(R.id
                .personal_customiv_headview);
        //同步按钮
        mAnimDownloadProgressButton = (AnimDownloadProgressButton) findViewById(R.id
                .personal_dpb_cloud_synchronization);
        mAnimDownloadProgressButton.setCurrentText("云 同 步");
        //同步时间
        personal_tv_synchronization_time = (TextView) findViewById(R.id
                .personal_tv_synchronization_time);
        //性别布局
        personal_rl_gender = (RelativeLayout) findViewById(R.id.personal_rl_gender);
        //生日布局
        personal_rl_birthday = (RelativeLayout) findViewById(R.id.personal_rl_birthday);
        //手机号码布局
        personal_rl_phone = (RelativeLayout) findViewById(R.id.personal_rl_phone);
        //邮箱布局
        personal_rl_email = (RelativeLayout) findViewById(R.id.personal_rl_email);
        //qq账号布局
        personal_rl_qq = (RelativeLayout) findViewById(R.id.personal_rl_qq);
        //微信账号布局
        personal_rl_weixin = (RelativeLayout) findViewById(R.id.personal_rl_weixin);
        //性别
        personal_tv_gender = (TextView) findViewById(R.id.personal_tv_gender);
        //生日
        personal_tv_birthday = (TextView) findViewById(R.id.personal_tv_birthday);
        //手机号
        personal_tv_phone = (TextView) findViewById(R.id.personal_tv_phone);
        //邮箱
        personal_tv_email = (TextView) findViewById(R.id.personal_tv_email);
        //QQ账号
        personal_tv_qq = (TextView) findViewById(R.id.personal_tv_qq);
        //微信账号
        personal_tv_weinxin = (TextView) findViewById(R.id.personal_tv_weinxin);
        //退出登录按钮
        personal_btn_logout = (Button) findViewById(R.id.personal_btn_logout);

        //添加监听器
        base_rl_back.setOnClickListener(this);
        personal_customiv_headview.setOnClickListener(this);
        mAnimDownloadProgressButton.setOnClickListener(this);
        personal_rl_gender.setOnClickListener(this);
        personal_rl_birthday.setOnClickListener(this);
        personal_rl_phone.setOnClickListener(this);
        personal_rl_email.setOnClickListener(this);
        personal_rl_qq.setOnClickListener(this);
        personal_rl_weixin.setOnClickListener(this);
        personal_btn_logout.setOnClickListener(this);

        loginUser = BmobUser.getCurrentUser(User.class);
        if (loginUser != null) {
            //请求图片
            LogUtils.i("头像地址:" + loginUser.getHead());
            if (null != loginUser.getHead()) {
                Picasso.with(this).load(loginUser.getHead()).into(personal_customiv_headview);
            }
            personal_tv_nikename.setText(loginUser.getUsername());
            personal_tv_gender.setText(loginUser.getGender());
            personal_tv_birthday.setText(loginUser.getBirthday());
            personal_tv_phone.setText(loginUser.getMobilePhoneNumber());
            personal_tv_email.setText(loginUser.getEmail());
            personal_tv_qq.setText(loginUser.getQq());
            personal_tv_weinxin.setText(loginUser.getWeixin());
            String synchronizationTime = loginUser.getSynchronizationTime();
            LogUtils.i("上次同步时间:" + synchronizationTime);
            if (null == synchronizationTime) {
                personal_tv_synchronization_time.setText("点击按钮试试同步");
            } else {
                personal_tv_synchronization_time.setText("上次同步:" + loginUser
                        .getSynchronizationTime());
            }
        }

        //TODO 数据实时同步
        tra.start(new ValueEventListener() {
            @Override
            public void onConnectCompleted(Exception e) {
                LogUtils.i("数据实时同步连接:" + tra.isConnected());
            }

            @Override
            public void onDataChange(JSONObject jsonObject) {
                LogUtils.i("同步数据:" + jsonObject.toString());
            }
        });
        if (tra.isConnected()) {
            //监听表的更新、删除
            tra.subTableUpdate("_User");
            tra.subTableDelete("_User");
            tra.subTableUpdate("Account");
            tra.subTableDelete("Account");
            tra.subTableUpdate("InOut");
            tra.subTableDelete("InOut");
        }
    }

    /**
     * 监听事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v == null) return;
        switch (v.getId()) {
            case R.id.base_rl_back:                 //返回
                finish();
                overridePendingTransition(R.anim.in_load, R.anim.out_load);
                break;
            case R.id.personal_customiv_headview:  //切换头像
                requestPermissions();
                break;
            case R.id.personal_dpb_cloud_synchronization:   //云同步
                mProgress = 0;
                Message msg = mHandler.obtainMessage();
                msg.what = REFRESH_PROGRESS;
                mHandler.sendMessage(msg);
                break;
            case R.id.personal_rl_gender:           //填写性别
                showSingleChoiceDialog();
                break;
            case R.id.personal_rl_birthday:        //填写生日
                showDatePickDialog();
                break;
            case R.id.personal_rl_phone:           //填写手机号码
                Intent phoneIntent = new Intent(this, UpdatePersonInfoActivity.class);
                phoneIntent.putExtra("flag", UPDATE_PHONE);
                startActivityForResult(phoneIntent, UPDATE_PHONE);
                break;
            case R.id.personal_rl_email:          //填写邮箱
                Intent emailIntent = new Intent(this, UpdatePersonInfoActivity.class);
                emailIntent.putExtra("flag", UPDATE_EMAIL);
                startActivityForResult(emailIntent, UPDATE_EMAIL);
                break;
            case R.id.personal_rl_qq:             //填写qq账号
                Intent qqIntent = new Intent(this, UpdatePersonInfoActivity.class);
                qqIntent.putExtra("flag", UPDATE_QQ);
                startActivityForResult(qqIntent, UPDATE_QQ);
                break;
            case R.id.personal_rl_weixin:        //填写微信账号
                Intent weixinIntent = new Intent(this, UpdatePersonInfoActivity.class);
                weixinIntent.putExtra("flag", UPDATE_WEIXIN);
                startActivityForResult(weixinIntent, UPDATE_WEIXIN);
                break;
            case R.id.personal_btn_logout:      //退出登录
                logout();
                break;
            default:
                break;
        }
    }

    /**
     * 退出登录
     */
    private void logout() {
        BmobUser.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case UPDATE_PHONE:
                if (data != null) {
                    personal_tv_phone.setText(data.getStringExtra("info"));
                }
                break;
            case UPDATE_EMAIL:
                if (data != null) {
                    personal_tv_email.setText(data.getStringExtra("info"));
                }
                break;
            case UPDATE_QQ:
                if (data != null) {
                    personal_tv_qq.setText(data.getStringExtra("info"));
                }
                break;
            case UPDATE_WEIXIN:
                if (data != null) {
                    personal_tv_weinxin.setText(data.getStringExtra("info"));
                }
                break;
            default:
                break;
        }
    }

    /**
     * 选择日期
     */
    private void showDatePickDialog() {
        DatePickDialog dialog = new DatePickDialog(this);
        //设置上下年分限制
        dialog.setYearLimt(30);
        //设置标题
        dialog.setTitle("选择日期");
        //设置类型
        dialog.setType(DateType.TYPE_YMD);
        //设置消息体的显示格式，日期格式
        dialog.setMessageFormat("yyyy-MM-dd");
        //设置选择回调
        dialog.setOnChangeLisener(null);
        //设置点击确定按钮回调
        dialog.setOnSureLisener(new OnSureLisener() {
            @Override
            public void onSure(Date date) {
                String birthday = formatDate(date);
                personal_tv_birthday.setText(birthday);
                User newUser = new User();
                newUser.setBirthday(birthday);
                updateUserInfo(newUser);
            }
        });
        dialog.show();
    }

    /**
     * 格式化日期
     *
     * @param date
     * @return
     */
    private String formatDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(cal.YEAR) + "/" + (cal.get(cal.MONTH) + 1) + "/" + cal.get(cal.DATE);
    }

    /**
     * 选择性别单选框
     */
    private void showSingleChoiceDialog() {
        final String[] items = {"男", "女"};
        int index = 0;
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(personal_tv_gender.getText())) {
                index = i;
            }
        }
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("性别")
                .setIcon(R.drawable.gender)
                .setCancelable(true)
                .setSingleChoiceItems(items, index, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        personal_tv_gender.setText(items[which]);
                        User newUser = new User();
                        newUser.setGender(items[which]);
                        updateUserInfo(newUser);
                    }
                })
                .create();
        dialog.show();
    }

    /**
     * 更新同步时间
     *
     * @return
     */
    public void updateSynchronizationTime() {
        //获取服务器时间
        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long time, BmobException ex) {
                if (ex == null) {
                    LogUtils.i("获取服务器时间成功:" + mLastSynchronizationTime);
                    mLastSynchronizationTime = FormatUtils.formatTime(time);
                    personal_tv_synchronization_time.setText("上次同步:" +
                            mLastSynchronizationTime);
                    User newUser = new User();
                    newUser.setSynchronizationTime(mLastSynchronizationTime);
                    updateUserInfo(newUser);
                } else {
                    LogUtils.i("获取服务器时间失败" + ex.getMessage());
                }
            }
        });
    }
}
