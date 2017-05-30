package com.minidroid.moneymanager.ui.detailaccount;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.MsgDay;
import com.minidroid.moneymanager.utils.FormatUtils;

import java.util.List;

/**
 * 账户收入支出信息列表适配器
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class AccountDetailAdapter extends BaseAdapter {
    private List<MsgDay> mData;
    private Context mContext;

    public AccountDetailAdapter(List<MsgDay> mData, Context mContext) {
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
            convertView = LinearLayout.inflate(mContext, R.layout.item_account_detail, null);
            vh.iv_image = (ImageView) convertView.findViewById(R.id.item_account_detail_img);
            vh.tv_day = (TextView) convertView.findViewById(R.id.item_account_detail_day);
            vh.tv_class = (TextView) convertView.findViewById(R.id.item_account_detail_class);
            vh.tv_money = (TextView) convertView.findViewById(R.id.item_account_detail_money);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        MsgDay msgDay = mData.get(position);
        vh.iv_image.setImageResource(msgDay.getResourceId());
        vh.tv_day.setText(msgDay.getYear() + "年" + msgDay.getMonth() + "月" + msgDay.getDay() + "日");
        vh.tv_class.setText(msgDay.getClasses());
        FormatUtils.setText(mContext, vh.tv_money, msgDay.getInout() * msgDay.getMoney());
        return convertView;
    }

    class ViewHolder {
        ImageView iv_image;
        TextView tv_day, tv_class, tv_money;
    }
}
