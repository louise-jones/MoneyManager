package com.minidroid.moneymanager.view.swipemenulistview.custom;

/**
 * Created by minidroid on 2017/4/23.
 * Email:460821714@qq.com
 */
public interface ButtonController {
    int getPressedColor(int color);

    int getLighterColor(int color);

    int getDarkerColor(int color);

    boolean enablePress();

    boolean enableGradient();


}

