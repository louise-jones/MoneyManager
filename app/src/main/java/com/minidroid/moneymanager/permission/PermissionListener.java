package com.minidroid.moneymanager.permission;

import java.util.List;

/**
 * 运行时权限封装回调接口
 * Created by minidroid on 2017/4/23.
 * Email:460821714@qq.com
 */
public interface PermissionListener {

    /**
     * 用户授权时的回调方法
     */
    public void onGranted();

    /**
     * 用户拒绝授权时的回调方法
     *
     * @param deniedPermissionList 被拒绝的权限
     */
    public void onDenied(List<String> deniedPermissionList);

}
