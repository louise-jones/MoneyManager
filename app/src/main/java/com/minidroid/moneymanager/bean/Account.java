package com.minidroid.moneymanager.bean;

import cn.bmob.v3.BmobObject;

/**
 * 账户类
 * Created by minidroid on 2017/4/20 18:20.
 * csdn:http://blog.csdn.net/qq_22063697
 */
public class Account extends BmobObject{
    private String accountName;  //账号
    private double number;     //金额
    private User user;         //所属用户

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public double getNumber() {
        return number;
    }

    public void setNumber(double number) {
        this.number = number;
    }
}