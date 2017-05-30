package com.minidroid.moneymanager.ui.datailyear;

import android.os.Bundle;

import com.minidroid.moneymanager.bean.MsgDay;
import com.minidroid.moneymanager.bean.MsgYear;
import com.minidroid.moneymanager.ui.detailday.DayBaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 年统计收支情况的基类
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class YearBaseActivity extends DayBaseActivity implements DayBaseActivity.OnQueryMsgDayListListener{
    protected List<MsgYear> mMsgYearList;
    private OnQueryMsgYearListListener onQueryMsgYearListListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOnQueryMsgDayListListener(this);
//        initData();
    }

    public OnQueryMsgYearListListener getOnQueryMsgYearListListener() {
        return onQueryMsgYearListListener;
    }

    public void setOnQueryMsgYearListListener(OnQueryMsgYearListListener
                                                      onQueryMsgYearListListener) {
        this.onQueryMsgYearListListener = onQueryMsgYearListListener;
    }

    public interface OnQueryMsgYearListListener {
        public void onQueryMsgYearResult();
    }
    /**
     * 初始化数据
     */
    private void initData() {
        mMsgYearList = new ArrayList<>();
        for (MsgDay msgDay : mMsgDayList) {
            double money;
            if (msgDay.getInout() == -1) {
                money = -1 * msgDay.getMoney();
            } else {
                money = msgDay.getMoney();
            }
            boolean isExist = false;
            for (MsgYear msgYear : mMsgYearList) {
                if (msgDay.getYear() == msgYear.getYear()) {
                    if (msgDay.getInout() == -1) {
                        msgYear.setTotalout(msgYear.getTotalout() + msgDay.getMoney());
                    } else {
                        msgYear.setTotalin(msgYear.getTotalin() + msgDay.getMoney());
                    }
                    boolean isExistCount = false;
                    boolean isExistClass = false;
                    for (MsgYear.CountMsg countMsg : msgYear.getCountMsgList()) {
                        if (msgDay.getAccount().getAccountName().equals(countMsg.countName)) {
                            countMsg.money += money;
                            isExistCount = true;
                        }
                    }
                    for (MsgYear.ClassMsg classMsg : msgYear.getClassMsgList()) {
                        if (msgDay.getClasses().equals(classMsg.className)) {
                            classMsg.money += money;
                            isExistClass = true;
                        }
                    }
                    if (!isExistCount) {
                        MsgYear.CountMsg countMsg = msgYear.new CountMsg();
                        countMsg.money = money;
                        countMsg.countName = msgDay.getAccount().getAccountName();
                        msgYear.getCountMsgList().add(countMsg);
                    }
                    if (!isExistClass) {
                        MsgYear.ClassMsg classMsg = msgYear.new ClassMsg();
                        classMsg.money = money;
                        classMsg.className = msgDay.getClasses();
                        msgYear.getClassMsgList().add(classMsg);
                    }
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                MsgYear msgYear = new MsgYear();
                msgYear.setYear(msgDay.getYear());
                if (msgDay.getInout() == -1) {
                    msgYear.setTotalout(msgDay.getMoney());
                } else {
                    msgYear.setTotalin(msgDay.getMoney());
                }
                //按账户分类
                MsgYear.CountMsg countMsg = msgYear.new CountMsg();
                countMsg.money = money;
                countMsg.countName = msgDay.getAccount().getAccountName();
                msgYear.getCountMsgList().add(countMsg);
                //按类别分类
                MsgYear.ClassMsg classMsg = msgYear.new ClassMsg();
                classMsg.money = money;
                classMsg.className = msgDay.getClasses();
                msgYear.getClassMsgList().add(classMsg);

                mMsgYearList.add(msgYear);
            }
        }
    }

    @Override
    public void onQueryMsgDayResult() {
        initData();
        onQueryMsgYearListListener.onQueryMsgYearResult();
    }
}