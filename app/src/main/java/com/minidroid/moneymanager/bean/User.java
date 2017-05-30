package com.minidroid.moneymanager.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;

/**
 * 用户bean类
 * Created by minidroid on 2017/5/4 22:52.
 * Email:460821714@qq.com
 */
public class User extends BmobUser implements Serializable {

    private String head;      //头像
    private String gender;     //性别
    private String birthday;  //生日
    private String qq;        //qq账号
    private String weixin;   //微信账号
    private String synchronizationTime; //同步的时间

    public User() {
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getSynchronizationTime() {
        return synchronizationTime;
    }

    public void setSynchronizationTime(String synchronizationTime) {
        this.synchronizationTime = synchronizationTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "head='" + head + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                ", qq='" + qq + '\'' +
                ", weixin='" + weixin + '\'' +
                ", synchronizationTime='" + synchronizationTime + '\'' +
                '}';
    }
}
