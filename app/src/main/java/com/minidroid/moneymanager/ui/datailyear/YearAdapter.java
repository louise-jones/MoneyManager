package com.minidroid.moneymanager.ui.datailyear;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.MsgYear;

import java.util.List;

/**
 * 年统计收支情况的适配器
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class YearAdapter extends BaseAdapter {
    private int mPosition = 0;
    private List<MsgYear> mData;
    private Context mContext;

    public YearAdapter(Context mContext, List<MsgYear> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            convertView = LinearLayout.inflate(mContext, R.layout.lv_left_item, null);
            textView = (TextView) convertView.findViewById(R.id.lv_item_day);
            convertView.setTag(textView);
        } else {
            textView = (TextView) convertView.getTag();
        }
        MsgYear msgMonth = mData.get(position);

        convertView.setBackgroundColor(mContext.getResources().getColor(R.color.view_background_light));
        if (mPosition == position) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.background_activity));
        }
        if (msgMonth.getTotalin() - msgMonth.getTotalout() < 0) {
            textView.setTextColor(mContext.getResources().getColor(R.color.text_out_color));
        } else {
            textView.setTextColor(mContext.getResources().getColor(R.color.text_in_color));
        }
        textView.setText(msgMonth.getYear() + "年");
        return convertView;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }
}
