package com.minidroid.moneymanager.ui.detailmonth;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.MsgMonth;

import java.util.Collections;
import java.util.Comparator;

/**
 * 月统计收支情况界面
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class MonthActivity extends MonthBaseActivity implements MonthBaseActivity
        .OnQueryMsgMonthListListener {
    private ListView detail_lv;
    private MonthAdapter mAdapter;
    private ViewPager detail_vp;

    private ImageButton dayactivity_ib_list;

    //是否是饼状图显示方式
    private boolean isPieChart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setListener();

    }

    @Override
    public void onQueryMsgMonthResult() {
        //按月份排序
        Collections.sort(mMsgMonthList, mOrderMonth);
        mAdapter = new MonthAdapter(MonthActivity.this, mMsgMonthList);
        detail_lv.setAdapter(mAdapter);
        detail_vp.setAdapter(detail_vpAdapter);
    }
    /**
     * 初始化界面
     */
    private void initView() {
        //隐藏删除、排序、更多按钮
        ((ImageButton) findViewById(R.id.dayactivity_ib_delete)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.dayactivity_ib_order)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.dayactivity_ib_more)).setVisibility(View.GONE);
        dayactivity_ib_list = (ImageButton) findViewById(R.id.dayactivity_ib_list);
        //左边ListView，右边ViewPager
        detail_lv = (ListView) findViewById(R.id.detail_lv);
        detail_vp = (ViewPager) findViewById(R.id.detail_vp);
    }

    /**
     * 设置监听器
     */
    private void setListener() {
        setOnQueryMsgMonthListListener(this);
        dayactivity_ib_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //切换显示方式
                detail_vp.removeAllViews();
                isPieChart = !isPieChart;
                if (isPieChart) {       //饼状图显示
                    dayactivity_ib_list.setImageResource(R.drawable.icon_picture_normal);
                    detail_vp.setAdapter(detail_vp_piechart_Adapter);
                } else {                //正常文字显示
                    dayactivity_ib_list.setImageResource(R.drawable.icon_list_normal);
                    detail_vp.setAdapter(detail_vpAdapter);
                }
            }
        });
        detail_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setPosition(position);
                mAdapter.notifyDataSetChanged();
                detail_vp.setCurrentItem(position);
            }
        });

        detail_vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mAdapter.setPosition(position);
                mAdapter.notifyDataSetChanged();
                detail_lv.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    //图表显示的适配器
    private FragmentStatePagerAdapter detail_vp_piechart_Adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
        // 展示的page个数
        @Override
        public int getCount() {
            return mMsgMonthList == null ? 0 : mMsgMonthList.size();
        }

        // 返回值就是当前位置显示的Fragment
        @Override
        public Fragment getItem(int position) {
            MonthAndYearDetailPieChartFragment fragment = new MonthAndYearDetailPieChartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("msgMonth", mMsgMonthList.get(position));
            fragment.setArguments(bundle);
            return fragment;
        }
    };
    //正常显示的适配器
    private FragmentStatePagerAdapter detail_vpAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
        // 展示的page个数
        @Override
        public int getCount() {
            return mMsgMonthList == null ? 0 : mMsgMonthList.size();
        }

        // 返回值就是当前位置显示的Fragment
        @Override
        public Fragment getItem(int position) {
            MonthDetailFragment fragment = new MonthDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("msgMonth", mMsgMonthList.get(position));
            fragment.setArguments(bundle);
            return fragment;
        }
    };
    private Comparator<MsgMonth> mOrderMonth = new Comparator<MsgMonth>() {
        @Override
        public int compare(MsgMonth lhs, MsgMonth rhs) {
            if (lhs.getYear() > rhs.getYear()) {
                return -1;
            } else if (lhs.getYear() < rhs.getYear()) {
                return 1;
            } else {
                if (lhs.getMonth() > rhs.getMonth()) {
                    return -1;
                } else if (lhs.getMonth() < rhs.getMonth()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    };
}