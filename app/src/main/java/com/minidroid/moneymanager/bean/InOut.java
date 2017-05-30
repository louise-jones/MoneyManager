package com.minidroid.moneymanager.bean;

import cn.bmob.v3.BmobObject;

/**
 * 收支类
 * Created by minidroid on 2017/5/17 01:38.
 * Email:460821714@qq.com
 */
public class InOut extends BmobObject {
    private Integer year;               //年
    private Integer month;             //月
    private Integer day;              //日
    private Integer week;             //星期
    private Integer resourceId;       //图片
    private Float money;              //金额
    private Integer inOut;              //类型（收入或支出）
    private String clazz;               //分类
    private Long time;                //时间
    private String other;               //备注
    private Account account;            //使用的账户

    public InOut() {
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Float getMoney() {
        return money;
    }

    public void setMoney(Float money) {
        this.money = money;
    }

    public Integer getInOut() {
        return inOut;
    }

    public void setInOut(Integer inOut) {
        this.inOut = inOut;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "InOut{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", week=" + week +
                ", resourceId=" + resourceId +
                ", money='" + money + '\'' +
                ", inOut='" + inOut + '\'' +
                ", clazz='" + clazz + '\'' +
                ", time='" + time + '\'' +
                ", other='" + other + '\'' +
                ", account=" + account +
                '}';
    }
}
