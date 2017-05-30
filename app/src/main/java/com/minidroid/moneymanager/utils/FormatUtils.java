package com.minidroid.moneymanager.utils;

import android.content.Context;
import android.widget.TextView;

import com.minidroid.moneymanager.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 格式化工具类
 *
 * @author minidroid
 * @date 2017/4/21
 */
public class FormatUtils {
    public static String format2d(double d) {
        //#表示没有则为空，0表示如果没有则该位补0
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(d);
    }

    public static String format2Time(Long currentTime) {
        SimpleDateFormat time = new SimpleDateFormat(/*"yyyy-MM-dd*/" hh:mm:ss");
        Date date = new Date(currentTime);
        return time.format(date);
    }

    public static String formatTime(Long currentTime) {
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(currentTime * 1000L);
        return time.format(date);
    }
    /**
     * 格式化成保留一位小数的字符串
     * @param number
     * @return
     */
    public static String format2Decimal(double number) {
        DecimalFormat df = new DecimalFormat(".0");//保留一位小数
        return df.format(number);
    }
    public static void setText(Context context, TextView tv, double values) {
        if (values > 0) {
            tv.setTextColor(context.getResources().getColor(R.color.text_in_color));
        } else {
            tv.setTextColor(context.getResources().getColor(R.color.text_out_color));
        }
        tv.setText(FormatUtils.format2d(values));
    }
}

