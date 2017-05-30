package com.minidroid.moneymanager.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号校验工具类
 *
 * @author minidroid
 * @date 2017/5/5
 */
public class PhoneValidUtils {

    // 判断电话号码是否有效
    public static boolean isPhoneNumberValid(String phoneNumber) {

        boolean isValid = false;
        if (!TextUtils.isEmpty(phoneNumber)) {
            String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] " +
                    "{1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-?" +
                    " " +
                    "\\d{7,8}-(\\d{1,4})$))";
            // CharSequence的值是可读可写序列，而String的值是只读序列
            CharSequence inputStr = phoneNumber;
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches()) {
                isValid = true;
            }
        }
        return isValid;
    }

}
