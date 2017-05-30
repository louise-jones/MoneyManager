package com.minidroid.moneymanager.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.Account;
import com.minidroid.moneymanager.utils.FormatUtils;

import java.util.List;

/**
 * 账户列表适配器
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class AccountAdapter extends BaseAdapter {
    private List<Account> mData;
    private Context mContext;

    public AccountAdapter(List<Account> data, Context mContext) {
        this.mData = data;
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
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_swap, null);
            vh.tv_count = (TextView) convertView.findViewById(R.id.swap_item_count);
            vh.tv_number = (TextView) convertView.findViewById(R.id.swap_item_number);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tv_count.setText(mData.get(position).getAccountName());
        vh.tv_number.setText(FormatUtils.format2d(mData.get(position).getNumber()));
        return convertView;
    }

    class ViewHolder {
        TextView tv_count, tv_number;
    }
}
