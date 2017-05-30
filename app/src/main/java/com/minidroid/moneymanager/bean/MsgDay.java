package com.minidroid.moneymanager.bean;

import com.minidroid.moneymanager.utils.FormatUtils;

import java.io.Serializable;

/**
 * 日统计收支情况类
 * Created by minidroid on 2017/4/20 18:20.
 * csdn:http://blog.csdn.net/qq_22063697
 */
public class MsgDay implements Serializable {
    private String objectId;
    private int year, month, day, week, inout;
    private long time;
    private int resourceId;
    private double money;
    private String classes, other;
    private Account account;
    private String[] weeks = new String[]{"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    public MsgDay() {
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    /* public MsgDay(Cursor cursor) {
             this.year = cursor.getInt(cursor.getColumnIndex("year"));
             this.month = cursor.getInt(cursor.getColumnIndex("month"));
             this.day = cursor.getInt(cursor.getColumnIndex("day"));
             this.week = cursor.getInt(cursor.getColumnIndex("week"));
             this.inout = cursor.getInt(cursor.getColumnIndex("inout"));
             this.classes = cursor.getString(cursor.getColumnIndex("class"));
             this.account = cursor.getString(cursor.getColumnIndex("account"));
             this.time = cursor.getLong(cursor.getColumnIndex("time"));
             this.resourceId = cursor.getInt(cursor.getColumnIndex("resourceid"));
             this.money = cursor.getDouble(cursor.getColumnIndex("money"));
             this.other = cursor.getString(cursor.getColumnIndex("other"));
         }
     */
    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getInout() {
        return inout;
    }

    public void setInout(int inout) {
        this.inout = inout;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    @Override
    public String toString() {
        return year +
                "," + month +
                "," + day +
                "," + weeks[week - 1] +
                "," + (inout == 1 ? "收入" : "支出") +
                "," + FormatUtils.format2Time(time) +
                "," + money +
                "," + classes +
                "," + account.getAccountName() +
                ", " + other;
    }
/*
    private String simpleTime(Long lo) {
        SimpleDateFormat time = new SimpleDateFormat(*//*"yyyy-MM-dd*//*" hh:mm:ss");
        Date date = new Date(lo);
        return time.format(date);
    }*/
}
