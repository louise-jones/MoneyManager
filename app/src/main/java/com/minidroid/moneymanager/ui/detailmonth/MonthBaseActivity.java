package com.minidroid.moneymanager.ui.detailmonth;

import android.os.Bundle;

import com.minidroid.moneymanager.bean.MsgDay;
import com.minidroid.moneymanager.bean.MsgMonth;
import com.minidroid.moneymanager.ui.detailday.DayBaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 月统计收支情况的基类
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class MonthBaseActivity extends DayBaseActivity implements DayBaseActivity.OnQueryMsgDayListListener{

    protected List<MsgMonth> mMsgMonthList;
    private OnQueryMsgMonthListListener onQueryMsgMonthListListener;

    public OnQueryMsgMonthListListener getOnQueryMsgMonthListListener() {
        return onQueryMsgMonthListListener;
    }

    public void setOnQueryMsgMonthListListener(OnQueryMsgMonthListListener
                                                       onQueryMsgMonthListListener) {
        this.onQueryMsgMonthListListener = onQueryMsgMonthListListener;
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOnQueryMsgDayListListener(this);
//        initData();
    }

    public interface OnQueryMsgMonthListListener {
        public void onQueryMsgMonthResult();
    }
    /**
     * 初始化数据
     */
    private void initData() {
        mMsgMonthList = new ArrayList<>();
        for (MsgDay msgDay : mMsgDayList) {
            double money;
            if (msgDay.getInout() == -1) {
                money = -1 * msgDay.getMoney();
            } else {
                money = msgDay.getMoney();
            }
            boolean isExist = false;
            for (MsgMonth msgMonth : mMsgMonthList) {
                //同年同月
                if (msgDay.getYear() == msgMonth.getYear() && msgDay.getMonth() == msgMonth.getMonth()) {
                    if (msgDay.getInout() == -1) {
                        msgMonth.setTotalout(msgMonth.getTotalout() + msgDay.getMoney());
                    } else {
                        msgMonth.setTotalin(msgMonth.getTotalin() + msgDay.getMoney());
                    }
                    boolean isExistCount = false;
                    boolean isExistClass = false;
                    for (MsgMonth.CountMsg countMsg : msgMonth.getCountMsgList()) {
                        if (msgDay.getAccount().getAccountName().equals(countMsg.countName)) {
                            //进来了说明是同一账户
                            countMsg.money += money;
                            isExistCount = true;
                        }
                    }
                    for (MsgMonth.ClassMsg classMsg : msgMonth.getClassMsgList()) {
                        if (msgDay.getClasses().equals(classMsg.className)) {
                            //进来了说明是同一类别
                            classMsg.money += money;
                            isExistClass = true;
                        }
                    }
                    //isExistCount为false的话说明还没有统计这一账户收支
                    if (!isExistCount) {
                        MsgMonth.CountMsg countMsg = msgMonth.new CountMsg();
                        countMsg.money = money;
                        countMsg.countName = msgDay.getAccount().getAccountName();
                        msgMonth.getCountMsgList().add(countMsg);
                    }
                    //isExistClass为false的话说明还没有统计这一类别收支
                    if (!isExistClass) {
                        MsgMonth.ClassMsg classMsg = msgMonth.new ClassMsg();
                        classMsg.money = money;
                        classMsg.className = msgDay.getClasses();
                        msgMonth.getClassMsgList().add(classMsg);
                    }
                    isExist = true;
                    break;
                }
            }
            //isExist为false的话说明还没有统计这个月收支
            if (!isExist) {
                MsgMonth msgMonth = new MsgMonth();
                msgMonth.setYear(msgDay.getYear());
                msgMonth.setMonth(msgDay.getMonth());
                if (msgDay.getInout() == -1) {
                    msgMonth.setTotalout(msgDay.getMoney());
                } else {
                    msgMonth.setTotalin(msgDay.getMoney());
                }
                //按账户统计
                MsgMonth.CountMsg countMsg = msgMonth.new CountMsg();
                countMsg.money = money;
                countMsg.countName = msgDay.getAccount().getAccountName();
                msgMonth.getCountMsgList().add(countMsg);
                //按类别统计
                MsgMonth.ClassMsg classMsg = msgMonth.new ClassMsg();
                classMsg.money = money;
                classMsg.className = msgDay.getClasses();
                msgMonth.getClassMsgList().add(classMsg);

                mMsgMonthList.add(msgMonth);
            }
        }
    }

    @Override
    public void onQueryMsgDayResult() {
        initData();
        onQueryMsgMonthListListener.onQueryMsgMonthResult();
    }
}