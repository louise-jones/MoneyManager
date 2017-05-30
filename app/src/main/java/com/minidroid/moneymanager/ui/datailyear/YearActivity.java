package com.minidroid.moneymanager.ui.datailyear;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.MsgYear;
import com.minidroid.moneymanager.ui.detailmonth.MonthAndYearDetailPieChartFragment;
import com.minidroid.moneymanager.utils.FormatUtils;
import com.minidroid.moneymanager.utils.LogUtils;

import java.util.Collections;
import java.util.Comparator;


/**
 * 年统计收支情况界面
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class YearActivity extends YearBaseActivity implements YearBaseActivity
        .OnQueryMsgYearListListener {
    private ListView detail_lv;
    private ViewPager detail_vp;
    private YearAdapter detail_lvAdapter;

    private ImageButton dayactivity_ib_list;

    //是否是饼状图显示方式
    private boolean isPieChart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setListener();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        //隐藏删除、排序\更多按钮
        ((ImageButton) findViewById(R.id.dayactivity_ib_delete)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.dayactivity_ib_order)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.dayactivity_ib_more)).setVisibility(View.GONE);
        dayactivity_ib_list = (ImageButton) findViewById(R.id.dayactivity_ib_list);
        detail_lv = (ListView) findViewById(R.id.detail_lv);
        detail_vp = (ViewPager) findViewById(R.id.detail_vp);
    }

    @Override
    public void onQueryMsgYearResult() {
        LogUtils.i("onQueryMsgYearResult()...");
        //按年份排序
        Collections.sort(mMsgYearList, mOrderYear);
        detail_vp.setAdapter(detail_vpAdapter);
        detail_lvAdapter = new YearAdapter(this, mMsgYearList);
        detail_lv.setAdapter(detail_lvAdapter);
    }
    /**
     * 设置监听器
     */
    private void setListener() {
        setOnQueryMsgYearListListener(this);
        dayactivity_ib_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                detail_lvAdapter.setPosition(position);
                detail_lvAdapter.notifyDataSetChanged();
                detail_vp.setCurrentItem(position);
            }
        });

        detail_vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                detail_lvAdapter.setPosition(position);
                detail_lvAdapter.notifyDataSetChanged();
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
            return mMsgYearList == null ? 0 : mMsgYearList.size();
        }

        // 返回值就是当前位置显示的Fragment
        @Override
        public Fragment getItem(int position) {
            MonthAndYearDetailPieChartFragment fragment = new MonthAndYearDetailPieChartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("msgYear", mMsgYearList.get(position));
            fragment.setArguments(bundle);
            return fragment;
        }
    };
    //正常文字显示的
    private PagerAdapter detail_vpAdapter = new PagerAdapter() {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = LayoutInflater.from(YearActivity.this).inflate(R.layout.fragment_monthdetail_list, null);
            ListView lv = (ListView) v.findViewById(R.id.fragment_monthdetail_lv);
            TextView tv = (TextView) v.findViewById(R.id.fragment_monthdetail_tv);
            tv.setText(mMsgYearList.get(position).getYear() + "年");
            container.addView(v);
            lv.setAdapter(new ListViewAdapter(position));
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return mMsgYearList.size();
        }
    };

    class ListViewAdapter extends BaseAdapter {
        private int Iposition;

        public ListViewAdapter(int position) {
            this.Iposition = position;
        }

        @Override
        public int getCount() {
            return mMsgYearList == null ? 0 : mMsgYearList.get(Iposition).getCountMsgList().size() + mMsgYearList.get(Iposition).getClassMsgList().size() + 3;
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
                convertView = LayoutInflater.from(YearActivity.this).inflate(R.layout.item_month_vp_list, null);
                vh.tv_title = (TextView) convertView.findViewById(R.id.item_vp_tv_title);
                vh.tv_left = (TextView) convertView.findViewById(R.id.item_vp_tv_left);
                vh.tv_right = (TextView) convertView.findViewById(R.id.item_vp_tv_right);
                vh.ll_title = (LinearLayout) convertView.findViewById(R.id.item_vp_ll_title);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            MsgYear msg = mMsgYearList.get(Iposition);
            LogUtils.i("MonthActivity", mMsgYearList.size() + ":" + msg.getCountMsgList().size() + ":" + msg.getClassMsgList().size());
            if (position == 0) {
                vh.ll_title.setVisibility(View.VISIBLE);
                vh.tv_title.setText("总收支");
                vh.tv_left.setText("合计");
                FormatUtils.setText(YearActivity.this, vh.tv_right, msg.getTotalin() - msg.getTotalout());
            } else if (position == 1) {
                vh.ll_title.setVisibility(View.GONE);
                vh.tv_left.setText("总收入");
                FormatUtils.setText(YearActivity.this, vh.tv_right, msg.getTotalin());
            } else if (position == 2) {
                vh.ll_title.setVisibility(View.GONE);
                vh.tv_left.setText("总支出");
                FormatUtils.setText(YearActivity.this, vh.tv_right, -1 * msg.getTotalout());
            } else if (position == 3) {
                vh.ll_title.setVisibility(View.VISIBLE);
                vh.tv_title.setText("账户收支");
            } else if (position == msg.getCountMsgList().size() + 3) {
                vh.ll_title.setVisibility(View.VISIBLE);
                vh.tv_title.setText("分类收支");
            } else {
                vh.ll_title.setVisibility(View.GONE);
            }
            if (position > 2 && position < msg.getCountMsgList().size() + 3) {  //账户收支
                vh.tv_left.setText(msg.getCountMsgList().get(position - 3).countName);
                double money = msg.getCountMsgList().get(position - 3).money;
                FormatUtils.setText(YearActivity.this, vh.tv_right, money);
            } else if (position > msg.getCountMsgList().size() + 2 &&
                    position < msg.getCountMsgList().size() + msg.getClassMsgList().size() + 3) {   //分类收支
                vh.tv_left.setText(msg.getClassMsgList().get(position - msg.getCountMsgList().size() - 3).className);
                double money = msg.getClassMsgList().get(position - msg.getCountMsgList().size() - 3).money;
                FormatUtils.setText(YearActivity.this, vh.tv_right, money);
            }
            return convertView;
        }
    }

    class ViewHolder {
        TextView tv_title, tv_left, tv_right;
        LinearLayout ll_title;
    }

    private Comparator<MsgYear> mOrderYear = new Comparator<MsgYear>() {
        @Override
        public int compare(MsgYear lhs, MsgYear rhs) {
            //大于0的就是指compare方法第一个参数要放在第二个参数的前面
            // 小于0就是指第一个参数要放在第二个参数后面
            if (lhs.getYear() > rhs.getYear()) {
                return -1;
            } else {
                return 1;
            }
        }
    };
}