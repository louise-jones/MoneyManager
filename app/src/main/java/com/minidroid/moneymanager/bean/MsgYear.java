package com.minidroid.moneymanager.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 年统计收支情况类
 * Created by minidroid on 2017/4/20 18:20.
 * csdn:http://blog.csdn.net/qq_22063697
 */
public class MsgYear implements Serializable{
    private double totalin, totalout;
    private int year;
    private List<ClassMsg> classMsgList;
    private List<CountMsg> countMsgList;

    public MsgYear() {
        classMsgList = new ArrayList<>();
        countMsgList = new ArrayList<>();
    }
    //按类别分类
    public class ClassMsg implements Serializable{
        public String className;
        public double money;
    }
    //按账户分类
    public class CountMsg implements Serializable{
        public String countName;
        public double money;
    }

    public double getTotalin() {
        return totalin;
    }

    public void setTotalin(double totalin) {
        this.totalin = totalin;
    }

    public double getTotalout() {
        return totalout;
    }

    public void setTotalout(double totalout) {
        this.totalout = totalout;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<ClassMsg> getClassMsgList() {
        return classMsgList;
    }

    public void setClassMsgList(List<ClassMsg> classMsgList) {
        this.classMsgList = classMsgList;
    }

    public List<CountMsg> getCountMsgList() {
        return countMsgList;
    }

    public void setCountMsgList(List<CountMsg> countMsgList) {
        this.countMsgList = countMsgList;
    }
}
