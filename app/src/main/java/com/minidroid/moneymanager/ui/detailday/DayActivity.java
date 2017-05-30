package com.minidroid.moneymanager.ui.detailday;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.Account;
import com.minidroid.moneymanager.bean.InOut;
import com.minidroid.moneymanager.utils.DialogUtils;
import com.minidroid.moneymanager.utils.LogUtils;
import com.minidroid.moneymanager.utils.OrderUtils;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomToast;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


/**
 * 日统计收支情况界面
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class DayActivity extends DayBaseActivity implements DayBaseActivity
        .OnQueryMsgDayListListener {
    private boolean isOrderDay = true;
    private ListView detail_lv;
    private ViewPager detail_vp;
    private DayAdapter mAdapter;
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
        ((ImageButton)findViewById(R.id.dayactivity_ib_more)).setVisibility(View.GONE);
        ((ImageButton)findViewById(R.id.dayactivity_ib_list)).setVisibility(View.GONE);
        findViewById(R.id.dayactivity_ib_more).setVisibility(View.GONE);
        detail_vp = (ViewPager) findViewById(R.id.detail_vp);
        detail_lv = (ListView) findViewById(R.id.detail_lv);
    }

    /**
     * 设置监听器
     */
    private void setListener() {
        setOnQueryMsgDayListListener(this);
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

    private FragmentStatePagerAdapter detail_vpAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
        // 展示的page个数
        @Override
        public int getCount() {
            return mMsgDayList.size();
        }

        // 返回值就是当前位置显示的Fragment
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new DayDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("msg", mMsgDayList.get(position));
            fragment.setArguments(bundle);
            return fragment;
        }
    };

    /**
     * 点击事件
     * @param v
     */
    public void onClick(View v) {
        if (v == null) return;
        switch (v.getId()) {
            case R.id.dayactivity_ib_delete:
                if (mMsgDayList.size() == 0) return;
                DialogUtils.show(DayActivity.this, "确定要删除该条记录么！", new DialogUtils.DialogCallBack() {
                    @Override
                    public void doListener() {
                        delete();
                    }
                });
                break;
            case R.id.dayactivity_ib_order:
                isOrderDay = !isOrderDay;
                if (isOrderDay) {
                    OrderUtils.orderByDay(mMsgDayList);
                    updateList("按时间排序");
                } else {
                    OrderUtils.orderByMoney(mMsgDayList);
                    updateList("按金额排序");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 删除该条收支记录
     */
    private void delete() {
//        LogUtils.i("DayActivity", "==========" + mMsgDayList.get(detail_vp.getCurrentItem())
//                .getMoney());
        Account updateAccount = new Account();
        updateAccount.setNumber(-1 * mMsgDayList.get(detail_vp.getCurrentItem()).getInout() *
                mMsgDayList.get(detail_vp.getCurrentItem()).getMoney() + mMsgDayList.get
                (detail_vp.getCurrentItem()).getAccount().getNumber());
        updateAccount.update(mMsgDayList.get(detail_vp.getCurrentItem()).getAccount().getObjectId
                (), new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    LogUtils.i(mMsgDayList.get(detail_vp.getCurrentItem()).getObjectId() + "");
                    InOut deleteInOut = new InOut();
                    deleteInOut.setObjectId(mMsgDayList.get(detail_vp.getCurrentItem())
                            .getObjectId());
                    deleteInOut.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                LogUtils.i("删除该条收支记录成功");
                                initParentData();
                                initView();
                            } else {
                                LogUtils.i("删除该条收支记录失败:" + e.getMessage());
                            }
                        }
                    });
                    /*LogUtils.i(mMsgDayList.get(detail_vp.getCurrentItem()).getAccount()
                            .getAccountName() + "更新成功");
                    BmobQuery<InOut> query = new BmobQuery<>();
                    query.setLimit(1)
                            .addWhereEqualTo("time", mMsgDayList.get(detail_vp.getCurrentItem())
                                    .getTime());
                    query.findObjects(new FindListener<InOut>() {
                        @Override
                        public void done(List<InOut> list, BmobException e) {
                            if (e == null) {
                                LogUtils.i("查询该删除的收支记录成功");
                                if (list.size() > 0) {
                                    InOut deleteInOut = new InOut();
                                    deleteInOut.setObjectId(list.get(0).getObjectId());
                                    deleteInOut.delete(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                LogUtils.i("删除该条收支记录成功");
                                                initParentData();
                                                initView();
                                            } else {
                                                LogUtils.i("删除该条收支记录失败:" + e.getMessage());
                                            }
                                        }
                                    });
                                }
                            } else {
                                LogUtils.i("查询该删除的收支记录失败...");
                            }
                        }
                    });*/
                } else {
                    LogUtils.i(mMsgDayList.get(detail_vp.getCurrentItem()).getAccount()
                            .getAccountName() + "更新失败:" + e.getMessage());
                }
            }
        });
// (detail_vp.getCurrentItem()).getMoney());
//        SqliteUtils.update(DayActivity.this, mMsgDayList.get(detail_vp.getCurrentItem())
// .getAccount(),
//                -1 * mMsgDayList.get(detail_vp.getCurrentItem()).getInout() * mMsgDayList.get
// (detail_vp.getCurrentItem()).getMoney());
//        SqliteManager.getInstance(this).delteItem("inout", "time=?",
//                new String[]{mMsgDayList.get(detail_vp.getCurrentItem()).getTime() + ""});

//        initParentData();
//        initView();
    }

    /**
     * 重新排序
     * @param msg
     */
    private void updateList(String msg) {
        detail_vp.setCurrentItem(0);
        mAdapter.setPosition(0);
        mAdapter.notifyDataSetChanged();
        detail_vpAdapter.notifyDataSetChanged();
        detail_vp.setAdapter(detail_vpAdapter);
        detail_lv.setSelection(0);
        CustomToast.showToast(DayActivity.this, msg, Toast.LENGTH_SHORT);
    }

    @Override
    public void onQueryMsgDayResult() {
        LogUtils.i("收支记录数:" + mMsgDayList.size());
        OrderUtils.orderByDay(mMsgDayList);
        mAdapter = new DayAdapter(mMsgDayList, this);
        detail_lv.setAdapter(mAdapter);
        detail_vp.setAdapter(detail_vpAdapter);
    }
}
