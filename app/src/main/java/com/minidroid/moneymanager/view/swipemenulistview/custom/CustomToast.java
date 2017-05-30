package com.minidroid.moneymanager.view.swipemenulistview.custom;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * 自定义吐司
 * Created by minidroid on 2017/4/21 12:54.
 * Email:460821714@qq.com
 */
public class CustomToast {
    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mToast.cancel();
            //mToast隐藏后，将其置为null
            mToast = null;
        }
    };

    public static void showToast(Context context, String text, int duration) {
        mHandler.removeCallbacks(mRunnable);
        if (mToast != null) {
            mToast.setText(text);
        } else {
            //只有mToast==null时才重新创建，否则只需更改提示文字
            mToast = Toast.makeText(context, text, duration);
        }
        //异步，延迟1.8秒隐藏mToast
        mHandler.postDelayed(mRunnable, 1800);
        mToast.show();
    }

    public static void showToast(Context context, int resId, int duration) {
        showToast(context, context.getString(resId), duration);
    }
}

