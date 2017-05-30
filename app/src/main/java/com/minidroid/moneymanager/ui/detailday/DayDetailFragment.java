package com.minidroid.moneymanager.ui.detailday;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.MsgDay;
import com.minidroid.moneymanager.utils.FormatUtils;


/**
 * 日统计收支情况详情Fragment
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class DayDetailFragment extends Fragment {
    private String[] weeks = new String[]{"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星六"};
    private MsgDay msgDay;
    private ImageView dayactivity_tv_img;
    private TextView dayactivity_tv_money, dayactivity_tv_class, dayactivity_tv_count, dayactivity_tv_time, dayactivity_tv_week, dayactivity_tv_other;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_daydetail, null);
        msgDay = (MsgDay) getArguments().getSerializable("msg");
        initFragmentView(view);
        return view;
    }

    /**
     * 初始化界面
     *
     * @param view
     */
    private void initFragmentView(View view) {
        //图片、金额、分类、账户、时间、星期、备注
        dayactivity_tv_img = (ImageView) view.findViewById(R.id.dayactivity_tv_img);
        dayactivity_tv_money = (TextView) view.findViewById(R.id.dayactivity_tv_money);
        dayactivity_tv_class = (TextView) view.findViewById(R.id.dayactivity_tv_class);
        dayactivity_tv_count = (TextView) view.findViewById(R.id.dayactivity_tv_count);
        dayactivity_tv_time = (TextView) view.findViewById(R.id.dayactivity_tv_time);
        dayactivity_tv_week = (TextView) view.findViewById(R.id.dayactivity_tv_week);
        dayactivity_tv_other = (TextView) view.findViewById(R.id.dayactivity_tv_other);

        dayactivity_tv_img.setImageResource(msgDay.getResourceId());
        dayactivity_tv_money.setText(FormatUtils.format2d(msgDay.getMoney()));
        dayactivity_tv_class.setText(msgDay.getClasses() + "");
        dayactivity_tv_count.setText(msgDay.getAccount().getAccountName() + "");
        dayactivity_tv_time.setText(msgDay.getYear() + "年" + msgDay.getMonth() + "月" + msgDay.getDay() + "日");
        dayactivity_tv_week.setText(weeks[msgDay.getWeek() - 1]);
        dayactivity_tv_other.setText(msgDay.getOther() == null ? "" : msgDay.getOther());
        if (msgDay.getInout() == -1) {
            dayactivity_tv_money.setTextColor(getResources().getColor(R.color.text_out_color));
        } else {
            dayactivity_tv_money.setTextColor(getResources().getColor(R.color.text_in_color));
        }
    }
}
