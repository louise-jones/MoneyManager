package com.minidroid.moneymanager.bean;

import java.io.Serializable;

/**
 * 版本类
 * Created by minidroid on 2017/4/20 18:35.
 * csdn:http://blog.csdn.net/qq_22063697
 */
public class Version implements Serializable {
    public int versionCode = 0;

    public Version(int versionCode) {
        this.versionCode = versionCode;
    }
}
