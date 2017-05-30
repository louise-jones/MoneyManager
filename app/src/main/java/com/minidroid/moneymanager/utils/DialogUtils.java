package com.minidroid.moneymanager.utils;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.minidroid.moneymanager.R;


/**
 * 对话框工具类
 *
 * @author minidroid
 * @date 2017/4/21
 */
public class DialogUtils {
    public static void show(Context context, String message, final DialogCallBack callBack) {

        //创建AlertDialog 的Builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("温馨提示")//设置对话框标题
                .setCancelable(false)   //设置不可取消
                .setIcon(context.getResources().getDrawable(R.drawable.icon_1))//设置对话框的图标
                .setMessage(message)//设置对话框的内容
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //点击确定按钮要做的操作
                        callBack.doListener();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击取消按钮要做的操作
                        dialog.dismiss();
                    }
                });
        builder.create().show();//创建AlertDialog对象
    }

    public interface DialogCallBack {
        void doListener();
    }
}
