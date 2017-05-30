package com.minidroid.moneymanager.ui.detailday;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.MsgDay;

import java.util.List;

/**
 * 日统计收支情况的适配器
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class DayAdapter extends BaseAdapter {
    private int mPosition = 0;
    private List<MsgDay> mData;
    private Context mContext;

    public DayAdapter(List<MsgDay> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
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
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LinearLayout.inflate(mContext, R.layout.lv_left_item, null);
            vh.tv_day = (TextView) convertView.findViewById(R.id.lv_item_day);
            vh.tv_year = (TextView) convertView.findViewById(R.id.lv_item_year);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        MsgDay msgDay = mData.get(position);
        if (position == 0 || mData.get(position - 1).getYear() != msgDay.getYear()) {
            vh.tv_year.setVisibility(View.VISIBLE);
            vh.tv_year.setText(msgDay.getYear() + "年");
        } else {
            vh.tv_year.setVisibility(View.GONE);
            vh.tv_year.setText("");
        }
        convertView.setBackgroundColor(mContext.getResources().getColor(R.color.view_background_light));
        if (mPosition == position) {
            /*TypedArray array = mContext.getTheme().obtainStyledAttributes(new int[]{
                    android.R.attr.colorBackground,
            });
            int backgroundColor = array.getColor(0, 0xFF00FF);*/
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.background_activity));
        }
        if (msgDay.getInout() == -1) {
            vh.tv_day.setTextColor(mContext.getResources().getColor(R.color.text_out_color));
        } else {
            vh.tv_day.setTextColor(mContext.getResources().getColor(R.color.text_in_color));
        }
        vh.tv_day.setText(msgDay.getMonth() + "月" + msgDay.getDay() + "日");
        return convertView;
    }

    class ViewHolder {
        TextView tv_year, tv_day;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }
}
