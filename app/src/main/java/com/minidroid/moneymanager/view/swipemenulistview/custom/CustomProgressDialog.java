package com.minidroid.moneymanager.view.swipemenulistview.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.minidroid.moneymanager.R;

/**
 * 自定义进度弹框
 * Created by minidroid on 2017/5/6 11:29.
 * Email:460821714@qq.com
 */
public class CustomProgressDialog extends Dialog {
    private Context context = null;
    private static CustomProgressDialog mCustomProgressDialog = null;

    public CustomProgressDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public CustomProgressDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected CustomProgressDialog(@NonNull Context context, boolean cancelable, @Nullable
            OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static CustomProgressDialog createDialog(Context context) {
        mCustomProgressDialog = new CustomProgressDialog(context,
                R.style.CustomProgressDialog);
        mCustomProgressDialog.setContentView(R.layout.custom_progress_dialog);
        mCustomProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        mCustomProgressDialog.setCancelable(false);
        mCustomProgressDialog.setCanceledOnTouchOutside(false);
        return mCustomProgressDialog;
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (mCustomProgressDialog == null) {
            return;
        }
        ImageView imageView = (ImageView) mCustomProgressDialog
                .findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView
                .getBackground();
        animationDrawable.start();
    }

    /**
     * 设置标题
     *
     * @param strTitle
     * @return
     */
    public CustomProgressDialog setTitile(String strTitle) {
        return mCustomProgressDialog;
    }

    /**
     * 提示内容
     *
     * @param strMessage
     * @return
     */
    public CustomProgressDialog setMessage(String strMessage) {
        TextView tvMsg = (TextView) mCustomProgressDialog
                .findViewById(R.id.id_tv_loadingmsg);
        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }
        return mCustomProgressDialog;
    }
}
