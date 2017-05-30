package com.minidroid.moneymanager.ui.detailaccount;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.Account;
import com.minidroid.moneymanager.bean.InOut;
import com.minidroid.moneymanager.bean.MsgDay;
import com.minidroid.moneymanager.permission.PermissionManagerActivity;
import com.minidroid.moneymanager.utils.AppUtils;
import com.minidroid.moneymanager.utils.FormatUtils;
import com.minidroid.moneymanager.utils.LogUtils;
import com.minidroid.moneymanager.utils.OrderUtils;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomToast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 账户详情界面
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class AccountDetailActivity extends PermissionManagerActivity {
    private ListView accountdetail_lv_detail;
    private TextView activity_account_detail_tvin, activity_account_detail_tvout;
    private List<MsgDay> mData;
    private String mAccount;
    private String mAccountObjectId;
    private boolean isOrderByDay = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setTranslucentStatus(AccountDetailActivity.this);
        setContentView(R.layout.activity_account_detail);
        initView();
        initData();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        mAccount = getIntent().getStringExtra("accountname");
        mAccountObjectId = getIntent().getStringExtra("accountObjectId");
        accountdetail_lv_detail = (ListView) findViewById(R.id.accountdetail_lv_detail);
        ((TextView) findViewById(R.id.baseactivity_tv_title)).setText(mAccount + "账单");
        ((ImageButton) findViewById(R.id.baseactivity_ib_ok)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.baseactivity_ib_order)).setVisibility(View.VISIBLE);
        activity_account_detail_tvin = (TextView) findViewById(R.id.activity_account_detail_tvin);
        activity_account_detail_tvout = (TextView) findViewById(R.id.activity_account_detail_tvout);
    }

    /**
     * 初始化账户收入支出信息
     */
    private void initData() {
        mData = new ArrayList<>();
        //从Bmob上查询该账户收支情况
        BmobQuery<InOut> query = new BmobQuery<>();
        Account selectedAccount = new Account();
        selectedAccount.setObjectId(mAccountObjectId);
        query.addWhereEqualTo("account", new BmobPointer(selectedAccount))
                .findObjects(new FindListener<InOut>() {
                    @Override
                    public void done(List<InOut> list, BmobException e) {
                        if (e == null) {
                            LogUtils.i("查询" + mAccount + "账户收支表成功:" +
                                    list.size());
                            for (int i = 0; i < list.size(); i++) {
                                MsgDay msgDay = new MsgDay();
                                msgDay.setYear(list.get(i).getYear());
                                msgDay.setMonth(list.get(i).getMonth());
                                msgDay.setDay(list.get(i).getDay());
                                msgDay.setWeek(list.get(i).getWeek());
                                msgDay.setMoney(list.get(i).getMoney());
                                msgDay.setInout(list.get(i).getInOut());
                                msgDay.setClasses(list.get(i).getClazz());
                                msgDay.setTime(list.get(i).getTime());
                                msgDay.setResourceId(list.get(i).getResourceId());
                                msgDay.setOther(list.get(i).getOther());
                                msgDay.setAccount(list.get(i).getAccount());
                                mData.add(msgDay);
                            }
                            //各订单按时间排序
                            OrderUtils.orderByDay(mData);
                            if (mData.size() == 0) {
                                CustomToast.showToast(AccountDetailActivity.this,
                                        "暂无该账户订单", Toast.LENGTH_SHORT);
                            }
                            showData();
                            AccountDetailAdapter adapter = new AccountDetailAdapter
                                    (mData, AccountDetailActivity.this);
                            accountdetail_lv_detail.setAdapter(adapter);
                        } else {
                            LogUtils.i("查询" + mAccount + "账户收支表失败:" +
                                    e.getMessage());
                        }
                    }
                });
        /*SqliteManager.QueryResult result = SqliteManager.getInstance(this).query
                ("inout", "account=?", new String[]{mAccount});
        while (result.cursor.moveToNext()) {
            MsgDay msgDay = new MsgDay(result.cursor);
            mData.add(msgDay);
        }
        //各订单按时间排序
        OrderUtils.orderByDay(mData);
        if (mData.size() == 0) {
            CustomToast.showToast(AccountDetailActivity.this, "暂无该账户订单", Toast.LENGTH_SHORT);
        }
        showData();
        AccountDetailAdapter adapter = new AccountDetailAdapter(mData, this);
        accountdetail_lv_detail.setAdapter(adapter);*/
    }

    /**
     * 设置显示的数据(总收入、总支出)
     */
    private void showData() {
        //从Bmob上查询该账户的所有收支情况
        BmobQuery<InOut> query = new BmobQuery<>();
        Account selectedAccount = new Account();
        selectedAccount.setObjectId(mAccountObjectId);
        query.addWhereEqualTo("account", new BmobPointer(selectedAccount));
        query.findObjects(new FindListener<InOut>() {
            @Override
            public void done(List<InOut> list, BmobException e) {
                if (e == null) {
                    LogUtils.i("查询该账户收支情况成功:" + list.size());
                    float totalin = 0, totalout = 0;
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            float money = list.get(i).getMoney();
                            float inout = list.get(i).getInOut();
                            if (inout == 1) totalin += money;
                            if (inout == -1) totalout += money;
                        }
                    }
                    activity_account_detail_tvin.setText(FormatUtils.format2d(totalin));
                    activity_account_detail_tvout.setText(FormatUtils.format2d(totalout));
                } else {
                    LogUtils.i("查询该账户收支情况失败:" + e.getMessage());
                }
            }
        });
        /*SqliteManager.QueryResult result = SqliteManager.getInstance(this).query("inout", null,
         null *//*"day=?,month=?,year=?",
                new String[]{mainactivity_tv_day.getText().toString(), mainactivity_tv_month
                .getText().toString(), mainactivity_tv_year.getText().toString()}*//*);
        if (result.cursor == null) return;
        float totalin = 0, totalout = 0;
        while (result.cursor.moveToNext()) {
            float money = result.cursor.getFloat(result.cursor.getColumnIndex("money"));
            float inout = result.cursor.getFloat(result.cursor.getColumnIndex("inout"));
            if (inout == 1) totalin += money;
            if (inout == -1) totalout += money;
        }
        activity_account_detail_tvin.setText(FormatUtils.format2d(totalin));
        activity_account_detail_tvout.setText(FormatUtils.format2d(totalout));*/
    }

    /**
     * 点击事件
     *
     * @param v
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.baseactivity_ib_return:
                finish();
                break;
            case R.id.baseactivity_ib_order:
                isOrderByDay = !isOrderByDay;
                if (isOrderByDay) {
                    OrderUtils.orderByMoney(mData);
                    CustomToast.showToast(AccountDetailActivity.this, "按金额排序", Toast.LENGTH_SHORT);
                } else {
                    OrderUtils.orderByDay(mData);
                    CustomToast.showToast(AccountDetailActivity.this, "按时间排序", Toast.LENGTH_SHORT);
                }
                break;
        }
    }
}
