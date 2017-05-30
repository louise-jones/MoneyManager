package com.minidroid.moneymanager.utils;


import com.minidroid.moneymanager.bean.MsgDay;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 收入支出工具类
 *
 * @author minidroid
 * @date 2017/4/21
 */
public class OrderUtils {
    /**
     * 各收入支出情况按时间从早到晚排序
     * @param list
     */
    public static void orderByDay(List list) {
        Comparator<MsgDay> orderByDay = new Comparator<MsgDay>() {
            @Override
            public int compare(MsgDay lhs, MsgDay rhs) {
                if (lhs.getYear() > rhs.getYear()) {
                    return -1;
                } else if (lhs.getYear() < rhs.getYear()) {
                    return 1;
                } else {
                    if (lhs.getMonth() > rhs.getMonth()) {
                        return -1;
                    } else if (lhs.getMonth() < rhs.getMonth()) {
                        return 1;
                    } else {
                        if (lhs.getDay() > rhs.getDay()) {
                            return -1;
                        } else if (lhs.getDay() < rhs.getDay()) {
                            return 1;
                        } else {
                            if (lhs.getTime() > rhs.getTime()) {
                                return -1;
                            } else if (lhs.getTime() < rhs.getTime()) {
                                return 1;
                            }
                        }
                    }
                }
                return 0;
            }
        };
        Collections.sort(list, orderByDay);
    }

    /**
     * 各收入支出情况按金额从大到小排序
     * @param list
     */
    public static void orderByMoney(List list) {
        Comparator<MsgDay> orderByMoney = new Comparator<MsgDay>() {
            @Override
            public int compare(MsgDay lhs, MsgDay rhs) {
                if (lhs.getMoney() > rhs.getMoney()) {
                    return -1;
                } else if (lhs.getMoney() < rhs.getMoney()) {
                    return 1;
                }
                return 0;
            }
        };
        Collections.sort(list, orderByMoney);
    }
}
