package com.minidroid.moneymanager.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 运行时权限管理封装类
 * Created by minidroid on 2017/4/23.
 * Email:460821714@qq.com
 */
public class PermissionManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    /**
     * 回调接口实例
     */
    private static PermissionListener mListener;

    /**
     * 运行时权限执行方法（static修饰的必要性--在非Activity类中也可以调用）
     *
     * @param permissions 需要授权的权限
     * @param listener    接口回调实例（运行时权限处理结果）
     */
    public static void requestPermissions(String[] permissions, PermissionListener listener) {

        // 获得栈顶Activity作为授权时的参数
        Activity topActivity = ActivityCollector.getTopActivity();
        if (topActivity == null) {
            Log.d("chan","top return");
            return;
        }

        mListener = listener;
        List<String> unGrantedPermissionsList = new ArrayList<String>(); // 未授权权限的集合

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(topActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                unGrantedPermissionsList.add(permission);
            }
        }
//        LogUtils.i("","unGrantedListSize===");
        Log.d("chan","unGrantedListSize==========="+unGrantedPermissionsList.size());


        // 授权业务
        if (!unGrantedPermissionsList.isEmpty()) {
            ActivityCompat.requestPermissions(topActivity, unGrantedPermissionsList.
                    toArray(new String[unGrantedPermissionsList.size()]), 1);
        } else { // 为空说明已全部授权，执行后续业务
            // TODO Something
            mListener.onGranted();
            Log.d("chan","=========1========");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("chan","=======aaaaaaaa=");

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("chan","======a==");
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    List<String> deniedPermissionList = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissionList.add(permission);
                        }
                    }
                    if (deniedPermissionList.isEmpty()) {
                        mListener.onGranted();
                        Log.d("chan","=========2========");
                    } else {
                        mListener.onDenied(deniedPermissionList);
                        Log.d("chan","=========3========");

                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        ActivityCollector.removeActivity(this);
        super.onDestroy();
    }
}
