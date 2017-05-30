package com.minidroid.moneymanager.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.Account;
import com.minidroid.moneymanager.bean.InOut;
import com.minidroid.moneymanager.db.SqliteManager;
import com.minidroid.moneymanager.ui.datailyear.YearActivity;
import com.minidroid.moneymanager.ui.detailday.DayActivity;
import com.minidroid.moneymanager.ui.detailmonth.MonthActivity;
import com.minidroid.moneymanager.ui.invest.InvestActivity;
import com.minidroid.moneymanager.ui.record.RecordActivity;
import com.minidroid.moneymanager.utils.FormatUtils;
import com.minidroid.moneymanager.utils.LogUtils;
import com.minidroid.moneymanager.utils.UserFactory;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomProgressDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 首页Fragment
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class MainFragment extends Fragment {

    private IgoAccountFragment mIgoAccountFragment;
    private View mView;
    //余额布局
    private LinearLayout mainactivity_ll_remainder;
    //记账总天数
    private TextView mainactivity_tv_totalday;
    //收入总额、支出总额、收支总计、余额
    private TextView mainactivity_tv_totalin, mainactivity_tv_totalout, mainactivity_tv_total, mainactivity_tv_remainder;
    //“添加收入/支出”、理财
    private TextView mainactivity_tv_add, mainactivity_tv_invest;
    //按日、月、年统计的显示文字
    private TextView mainactivity_tv_day, mainactivity_tv_month, mainactivity_tv_year;
    //今日收入、支出、总计
    private TextView mainactivity_tv_dayin, mainactivity_tv_dayout, mainactivity_tv_daytotal;
    //月收入、支出、总计
    private TextView mainactivity_tv_monthin, mainactivity_tv_monthout, mainactivity_tv_monthtotal;
    //年收入、支出、总计
    private TextView mainactivity_tv_yearin, mainactivity_tv_yearout, mainactivity_tv_yeartotal;
    //今日、月、年收支布局
    private LinearLayout main_ll_itemday, main_ll_itemmonth, main_ll_itemyear;
    //余额总额
    double reminderMoney = 0.0;
    private CustomProgressDialog mProgressDialog;
    float totalin = 0, totalout = 0, yearin = 0, yearout = 0, monthin = 0,
            monthout = 0, dayin = 0, dayout = 0;
    private int currentUserAccountNum = 0;  //当前用户账户数
    private int counter = 0;    //计数器
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x1) {
                if (counter == currentUserAccountNum) {
                    mProgressDialog.dismiss();
                    counter = 0;
                    mainactivity_tv_dayin.setText((FormatUtils.format2d(dayin)));
                    mainactivity_tv_dayout.setText(FormatUtils.format2d(dayout));
                    mainactivity_tv_daytotal.setText(FormatUtils.format2d(dayin - dayout));
                    if (dayin > dayout) {
                        mainactivity_tv_daytotal.setTextColor(getResources().getColor(R.color
                                .text_in_color));
                    } else {
                        mainactivity_tv_daytotal.setTextColor(getResources().getColor(R.color
                                .text_out_color));
                    }

                    mainactivity_tv_monthin.setText(FormatUtils.format2d(monthin));
                    mainactivity_tv_monthout.setText(FormatUtils.format2d(monthout));
                    mainactivity_tv_monthtotal.setText(FormatUtils.format2d(monthin - monthout));
                    if (monthin > monthout) {
                        mainactivity_tv_monthtotal.setTextColor(getResources().getColor(R.color
                                .text_in_color));
                    } else {
                        mainactivity_tv_monthtotal.setTextColor(getResources().getColor(R.color
                                .text_out_color));
                    }

                    mainactivity_tv_yearin.setText(FormatUtils.format2d(yearin));
                    mainactivity_tv_yearout.setText(FormatUtils.format2d(yearout));
                    mainactivity_tv_yeartotal.setText(FormatUtils.format2d(yearin - yearout));
                    if (monthin > monthout) {
                        mainactivity_tv_yeartotal.setTextColor(getResources().getColor(R.color
                                .text_in_color));
                    } else {
                        mainactivity_tv_yeartotal.setTextColor(getResources().getColor(R.color
                                .text_out_color));
                    }

                    mainactivity_tv_totalin.setText(FormatUtils.format2d(totalin));
                    mainactivity_tv_totalout.setText(FormatUtils.format2d(totalout));
                    mainactivity_tv_total.setText(FormatUtils.format2d(totalin - totalout));
                    showReminder();
                    if (totalin > totalout) {
                        mainactivity_tv_total.setTextColor(getResources().getColor(R.color
                                .text_in_color));
                    } else {
                        mainactivity_tv_total.setTextColor(getResources().getColor(R.color
                                .text_out_color));
                    }
                    totalin = 0;
                    totalout = 0;
                    yearin = 0;
                    yearout = 0;
                    monthin = 0;
                    monthout = 0;
                    dayin = 0;
                    dayout = 0;
                }
            }
        }
    };
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main, null);
        mView = view;
        initView(view);
        setData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setShowData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setShowData();
        }
    }
    /**
     * 初始化组件及设置监听器
     *
     * @param view
     */
    private void initView(View view) {
        mProgressDialog = CustomProgressDialog.createDialog(getActivity());
        mProgressDialog.setMessage("加载中...");
        //余额布局
        mainactivity_ll_remainder = (LinearLayout) view.findViewById(R.id.mainactivity_ll_remainder);
        //记账总天数
        mainactivity_tv_totalday = (TextView) view.findViewById(R.id.mainactivity_tv_totalday);
        //收入总额、支出总额、收支总计、余额
        mainactivity_tv_totalin = (TextView) view.findViewById(R.id.mainactivity_tv_totalin);
        mainactivity_tv_totalout = (TextView) view.findViewById(R.id.mainactivity_tv_totalout);
        mainactivity_tv_total = (TextView) view.findViewById(R.id.mainactivity_tv_total);
        mainactivity_tv_remainder = (TextView) view.findViewById(R.id.mainactivity_tv_remainder);
        //“添加收入/支出”、理财
        mainactivity_tv_add = (TextView) view.findViewById(R.id.mainactivity_tv_add);
        mainactivity_tv_invest = (TextView) view.findViewById(R.id.mainactivity_tv_invest);
        //按日、月、年统计的显示文字
        mainactivity_tv_day = (TextView) view.findViewById(R.id.mainactivity_tv_day);
        mainactivity_tv_month = (TextView) view.findViewById(R.id.mainactivity_tv_month);
        mainactivity_tv_year = (TextView) view.findViewById(R.id.mainactivity_tv_year);
        //今日收入、支出、总计
        mainactivity_tv_dayin = (TextView) view.findViewById(R.id.mainactivity_tv_dayin);
        mainactivity_tv_dayout = (TextView) view.findViewById(R.id.mainactivity_tv_dayout);
        mainactivity_tv_daytotal = (TextView) view.findViewById(R.id.mainactivity_tv_daytotal);
        //月收入、支出、总计
        mainactivity_tv_monthin = (TextView) view.findViewById(R.id.mainactivity_tv_monthin);
        mainactivity_tv_monthout = (TextView) view.findViewById(R.id.mainactivity_tv_monthout);
        mainactivity_tv_monthtotal = (TextView) view.findViewById(R.id.mainactivity_tv_monthtotal);
        //年收入、支出、总计
        mainactivity_tv_yearin = (TextView) view.findViewById(R.id.mainactivity_tv_yearin);
        mainactivity_tv_yearout = (TextView) view.findViewById(R.id.mainactivity_tv_yearout);
        mainactivity_tv_yeartotal = (TextView) view.findViewById(R.id.mainactivity_tv_yeartotal);
        //今日、月、年收支布局
        main_ll_itemday = (LinearLayout) view.findViewById(R.id.main_ll_itemday);
        main_ll_itemmonth = (LinearLayout) view.findViewById(R.id.main_ll_itemmonth);
        main_ll_itemyear = (LinearLayout) view.findViewById(R.id.main_ll_itemyear);
        //为“余额”布局设置监听器
        mainactivity_ll_remainder.setOnClickListener(mListener);
        //为“添加收入/支出”、理财按钮设置监听器
        mainactivity_tv_add.setOnClickListener(mListener);
        mainactivity_tv_invest.setOnClickListener(mListener);
        //为今日、月、年收支布局设置监听器
        main_ll_itemday.setOnClickListener(mListener);
        main_ll_itemmonth.setOnClickListener(mListener);
        main_ll_itemyear.setOnClickListener(mListener);
    }

    /**
     * 设置显示记账第几天及年月日
     */
    private void setData() {
        SqliteManager.QueryResult result = SqliteManager.getInstance(getActivity()).query("time", "time=?", new String[]{"firsttime"});
        if (result.cursor.getCount() != 0) {
            result.cursor.moveToFirst();
            long start = result.cursor.getLong(result.cursor.getColumnIndex("value"));
            long end = System.currentTimeMillis();
            int d = getCount(new Date(start), new Date(end));
            mainactivity_tv_totalday.setText(d + "");
        }
        String[] weeks = new String[]{"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance(Locale.CHINA);//创建一个日历对象
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        int m = calendar.get(Calendar.MONTH) + 1;
        int y = calendar.get(Calendar.YEAR);
        mainactivity_tv_year.setText(y + "");
        mainactivity_tv_month.setText(m + "");
        mainactivity_tv_day.setText(d + "");
    }
    /**
     * 统计记账的天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    private int getCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
       /* toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);*/

        return 1 + (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    private void setShowData() {
        BmobQuery<Account> query = new BmobQuery<>();
        query.addWhereEqualTo("user", new BmobPointer(UserFactory.currentLoginUser));
        query.findObjects(new FindListener<Account>() {
            @Override
            public void done(List<Account> list, BmobException e) {
                if (e == null) {
                    LogUtils.i("请求账户成功...." + list.size());
                    mProgressDialog.show();
                    currentUserAccountNum = list.size();
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            //从Bmob上查询收支表,同时将关联的账户也查询出来
                            BmobQuery<InOut> query2 = new BmobQuery<>();
                            query2.include("account");
                            query2.addWhereEqualTo("account", new BmobPointer(list.get(i)));
                            query2.findObjects(new FindListener<InOut>() {
                                @Override
                                public void done(List<InOut> list, BmobException e) {
                                    if (e == null) {
                                        if (list.size() > 0) {
                                            for (int i = 0; i < list.size(); i++) {
                                                float money = list.get(i).getMoney();
                                                Integer inout = list.get(i).getInOut();
                                                if (inout == 1) totalin += money;
                                                if (inout == -1) totalout += money;
                                                if (list.get(i).getYear() == Integer.parseInt
                                                        (mainactivity_tv_year
                                                                .getText().toString())) {
                                                    if (inout == 1) yearin += money;
                                                    if (inout == -1) yearout += money;
                                                    if (list.get(i).getMonth() == Integer.parseInt
                                                            (mainactivity_tv_month.getText()
                                                                    .toString())) {
                                                        if (inout == 1) monthin += money;
                                                        if (inout == -1) monthout += money;
                                                        if (list.get(i).getDay() == Integer.parseInt
                                                                (mainactivity_tv_day.getText()
                                                                        .toString())) {
                                                            if (inout == 1) dayin += money;
                                                            if (inout == -1) dayout += money;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        counter++;
                                        Message msg = mHandler.obtainMessage();
                                        msg.what = 0x1;
                                        mHandler.sendMessage(msg);
                                    }
                                }
                            });
                        }
                    }
                    mProgressDialog.dismiss();
                }
            }
        });
    }
    /**
     * 设置显示的数据(供测试)
     */
    private void setShowData2() {
        //从Bmob上查询收支表
        BmobQuery<InOut> query = new BmobQuery<>();
        query.findObjects(new FindListener<InOut>() {
            @Override
            public void done(List<InOut> list, BmobException e) {
                if (e == null) {
                    LogUtils.i("查询收支表成功:" + list.size());
                    float totalin = 0, totalout = 0, yearin = 0, yearout = 0, monthin = 0,
                            monthout = 0, dayin = 0, dayout = 0;
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            float money = list.get(i).getMoney();
                            Integer inout = list.get(i).getInOut();
                            if (inout == 1) totalin += money;
                            if (inout == -1) totalout += money;
                            if (list.get(i).getYear() == Integer.parseInt(mainactivity_tv_year
                                    .getText().toString())) {
                                if (inout == 1) yearin += money;
                                if (inout == -1) yearout += money;
                                if (list.get(i).getMonth() == Integer.parseInt
                                        (mainactivity_tv_month.getText().toString())) {
                                    if (inout == 1) monthin += money;
                                    if (inout == -1) monthout += money;
                                    if (list.get(i).getDay() == Integer.parseInt
                                            (mainactivity_tv_day.getText().toString())) {
                                        if (inout == 1) dayin += money;
                                        if (inout == -1) dayout += money;
                                    }
                                }
                            }
                        }
                    }
                    mainactivity_tv_dayin.setText((FormatUtils.format2d(dayin)));
                    mainactivity_tv_dayout.setText(FormatUtils.format2d(dayout));
                    mainactivity_tv_daytotal.setText(FormatUtils.format2d(dayin - dayout));
                    if (dayin > dayout) {
                        mainactivity_tv_daytotal.setTextColor(getResources().getColor(R.color
                                .text_in_color));
                    } else {
                        mainactivity_tv_daytotal.setTextColor(getResources().getColor(R.color
                                .text_out_color));
                    }

                    mainactivity_tv_monthin.setText(FormatUtils.format2d(monthin));
                    mainactivity_tv_monthout.setText(FormatUtils.format2d(monthout));
                    mainactivity_tv_monthtotal.setText(FormatUtils.format2d(monthin - monthout));
                    if (monthin > monthout) {
                        mainactivity_tv_monthtotal.setTextColor(getResources().getColor(R.color
                                .text_in_color));
                    } else {
                        mainactivity_tv_monthtotal.setTextColor(getResources().getColor(R.color
                                .text_out_color));
                    }

                    mainactivity_tv_yearin.setText(FormatUtils.format2d(yearin));
                    mainactivity_tv_yearout.setText(FormatUtils.format2d(yearout));
                    mainactivity_tv_yeartotal.setText(FormatUtils.format2d(yearin - yearout));
                    if (monthin > monthout) {
                        mainactivity_tv_yeartotal.setTextColor(getResources().getColor(R.color
                                .text_in_color));
                    } else {
                        mainactivity_tv_yeartotal.setTextColor(getResources().getColor(R.color
                                .text_out_color));
                    }

                    mainactivity_tv_totalin.setText(FormatUtils.format2d(totalin));
                    mainactivity_tv_totalout.setText(FormatUtils.format2d(totalout));
                    mainactivity_tv_total.setText(FormatUtils.format2d(totalin - totalout));
                    showReminder();
                    if (totalin > totalout) {
                        mainactivity_tv_total.setTextColor(getResources().getColor(R.color
                                .text_in_color));
                    } else {
                        mainactivity_tv_total.setTextColor(getResources().getColor(R.color
                                .text_out_color));
                    }
                } else {
                    LogUtils.i("查询收支表失败:" + e.getMessage());
                }
            }
        });
        /*SqliteManager.QueryResult result = SqliteManager.getInstance(getActivity()).query
        ("inout", null, null *//*"day=?,month=?,year=?",
                new String[]{mainactivity_tv_day.getText().toString(), mainactivity_tv_month
                .getText().toString(), mainactivity_tv_year.getText().toString()}*//*);
        if (result.cursor == null) return;
        float totalin = 0, totalout = 0, yearin = 0, yearout = 0, monthin = 0, monthout = 0, dayin = 0, dayout = 0;
        while (result.cursor.moveToNext()) {
            float money = result.cursor.getFloat(result.cursor.getColumnIndex("money"));
            float inout = result.cursor.getFloat(result.cursor.getColumnIndex("inout"));
            if (inout == 1) totalin += money;
            if (inout == -1) totalout += money;
            if (result.cursor.getInt(result.cursor.getColumnIndex("year")) == Integer.parseInt(mainactivity_tv_year.getText().toString())) {
                if (inout == 1) yearin += money;
                if (inout == -1) yearout += money;
                if (result.cursor.getInt(result.cursor.getColumnIndex("month")) == Integer.parseInt(mainactivity_tv_month.getText().toString())) {
                    if (inout == 1) monthin += money;
                    if (inout == -1) monthout += money;
                    if (result.cursor.getInt(result.cursor.getColumnIndex("day")) == Integer.parseInt(mainactivity_tv_day.getText().toString())) {
                        if (inout == 1) dayin += money;
                        if (inout == -1) dayout += money;
                    }
                }
            }
        }
        mainactivity_tv_dayin.setText((FormatUtils.format2d(dayin)));
        mainactivity_tv_dayout.setText(FormatUtils.format2d(dayout));
        mainactivity_tv_daytotal.setText(FormatUtils.format2d(dayin - dayout));
        if (dayin > dayout) {
            mainactivity_tv_daytotal.setTextColor(getResources().getColor(R.color.text_in_color));
        } else {
            mainactivity_tv_daytotal.setTextColor(getResources().getColor(R.color.text_out_color));
        }

        mainactivity_tv_monthin.setText(FormatUtils.format2d(monthin));
        mainactivity_tv_monthout.setText(FormatUtils.format2d(monthout));
        mainactivity_tv_monthtotal.setText(FormatUtils.format2d(monthin - monthout));
        if (monthin > monthout) {
            mainactivity_tv_monthtotal.setTextColor(getResources().getColor(R.color.text_in_color));
        } else {
            mainactivity_tv_monthtotal.setTextColor(getResources().getColor(R.color.text_out_color));
        }

        mainactivity_tv_yearin.setText(FormatUtils.format2d(yearin));
        mainactivity_tv_yearout.setText(FormatUtils.format2d(yearout));
        mainactivity_tv_yeartotal.setText(FormatUtils.format2d(yearin - yearout));
        if (monthin > monthout) {
            mainactivity_tv_yeartotal.setTextColor(getResources().getColor(R.color.text_in_color));
        } else {
            mainactivity_tv_yeartotal.setTextColor(getResources().getColor(R.color.text_out_color));
        }

        mainactivity_tv_totalin.setText(FormatUtils.format2d(totalin));
        mainactivity_tv_totalout.setText(FormatUtils.format2d(totalout));
        mainactivity_tv_total.setText(FormatUtils.format2d(totalin - totalout));
        mainactivity_tv_remainder.setText(FormatUtils.format2d(getReminder()));
        if (totalin > totalout) {
            mainactivity_tv_total.setTextColor(getResources().getColor(R.color.text_in_color));
        } else {
            mainactivity_tv_total.setTextColor(getResources().getColor(R.color.text_out_color));
        }
        result.cursor.close();
        result.db.close();*/
    }

    /**
     * 获取所有账户余额总和
     *
     * @return
     */
    private void showReminder() {
        //从Bmob上查询余额总额
        reminderMoney = 0.0;
        BmobQuery<Account> query = new BmobQuery<>();
        query.addWhereEqualTo("user", new BmobPointer(UserFactory.currentLoginUser));
        query.findObjects(new FindListener<Account>() {
            @Override
            public void done(List<Account> list, BmobException e) {
                if (e == null) {
                    LogUtils.i("查询账户成功:" + list.size());
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            reminderMoney += list.get(i).getNumber();
                        }
                    }
                    mainactivity_tv_remainder.setText(FormatUtils.format2d(reminderMoney));
                } else {
                    LogUtils.i("查询账户失败:" + e.getMessage());
                }
            }
        });
        /*SqliteManager.QueryResult result = SqliteManager.getInstance(getActivity()).query
        ("account", null, null);
        if (result.cursor == null) return 0.0;
        float reminderMoney = 0;
        while (result.cursor.moveToNext()) {
            reminderMoney += result.cursor.getFloat(result.cursor.getColumnIndex("money"));
        }
        return reminderMoney;*/
    }

    //监听器
    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == null) return;
            Intent intent = null;
            switch (v.getId()) {
                case R.id.mainactivity_ll_remainder:
                    if (mIgoAccountFragment != null) {
                        mIgoAccountFragment.showAccountFragment();
                    }
                    break;
                case R.id.mainactivity_tv_add:
                    intent = new Intent(getActivity(), RecordActivity.class);
                    startActivity(intent);
                    break;
                case R.id.mainactivity_tv_invest:
                    intent = new Intent(getActivity(), InvestActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_ll_itemday:
                    intent = new Intent(getActivity(), DayActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_ll_itemmonth:
                    intent = new Intent(getActivity(), MonthActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_ll_itemyear:
                    intent = new Intent(getActivity(), YearActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
//            startActivity(intent);
        }
    };

    public interface IgoAccountFragment {
        void showAccountFragment();
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof IgoAccountFragment) {
            mIgoAccountFragment = (IgoAccountFragment) context;
        } else {
            throw new RuntimeException("context must implements IgoAccountFragment.");
        }
        super.onAttach(context);
    }
}
