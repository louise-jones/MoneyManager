package com.minidroid.moneymanager.ui.detailday;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.Account;
import com.minidroid.moneymanager.bean.InOut;
import com.minidroid.moneymanager.bean.MsgDay;
import com.minidroid.moneymanager.permission.PermissionManagerActivity;
import com.minidroid.moneymanager.utils.AppUtils;
import com.minidroid.moneymanager.utils.LogUtils;
import com.minidroid.moneymanager.utils.UserFactory;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomProgressDialog;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomToast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 日统计收支情况的基类
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class DayBaseActivity extends PermissionManagerActivity {
    protected List<MsgDay> mMsgDayList;
    private OnQueryMsgDayListListener onQueryMsgDayListListener;

    public OnQueryMsgDayListListener getOnQueryMsgDayListListener() {
        return onQueryMsgDayListListener;
    }

    public void setOnQueryMsgDayListListener(OnQueryMsgDayListListener onQueryMsgDayListListener) {
        this.onQueryMsgDayListListener = onQueryMsgDayListListener;
    }
    private CustomProgressDialog mProgressDialog;
    private int currentUserAccountNum = 0;  //当前用户账户数
    private int counter = 0;    //计数器
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x1) {
                if (counter == currentUserAccountNum) {
                    mProgressDialog.dismiss();
                    counter = 0;
                    onQueryMsgDayListListener.onQueryMsgDayResult();
                    if (mMsgDayList.size() == 0) {
                        CustomToast.showToast(DayBaseActivity.this, "还没有记过账",
                                Toast.LENGTH_SHORT);
                    }
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setTranslucentStatus(DayBaseActivity.this);
        setContentView(R.layout.activity_detail);
        mProgressDialog = CustomProgressDialog.createDialog(this);
        mProgressDialog.setMessage("加载中...");
        initParentData();

    }

    public interface OnQueryMsgDayListListener {
        public void onQueryMsgDayResult();
    }
    /**
     * 初始化数据
     */
    protected void initParentData() {
        mMsgDayList = new ArrayList<>();

        BmobQuery<Account> query = new BmobQuery<>();
        query.addWhereEqualTo("user", new BmobPointer(UserFactory.currentLoginUser));
        query.findObjects(new FindListener<Account>() {
            @Override
            public void done(List<Account> list, BmobException e) {
                if (e == null) {
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
                                        LogUtils.i("查询收支表成功:" + list.size());
                                        if (list.size() > 0) {
                                            for (int i = 0; i < list.size(); i++) {
                                                MsgDay msgDay = new MsgDay();
                                                msgDay.setObjectId(list.get(i).getObjectId());
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
                                                mMsgDayList.add(msgDay);
                                            }
                                            LogUtils.i("query inout success...");
                                        }
                                        counter++;
                                        Message msg = mHandler.obtainMessage();
                                        msg.what = 0x1;
                                        mHandler.sendMessage(msg);
                                        /*onQueryMsgDayListListener.onQueryMsgDayResult();
                                        if (mMsgDayList.size() == 0) {
                                            CustomToast.showToast(DayBaseActivity.this, "还没有记过账",
                                                    Toast.LENGTH_SHORT);
                                        }*/
                                    } else {
                                        LogUtils.i("查询收支表失败:" + e.getMessage());
                                    }
                                }
                            });
                        }
                    }
                    /*onQueryMsgDayListListener.onQueryMsgDayResult();
                    if (mMsgDayList.size() == 0) {
                        CustomToast.showToast(DayBaseActivity.this, "还没有记过账",
                                Toast.LENGTH_SHORT);
                    }*/
                } else {
                    LogUtils.i(e.getMessage());
                }
            }
        });

        /*SqliteManager.QueryResult result = SqliteManager.getInstance(this).query("inout", null,
         null);
        while (result.cursor.moveToNext()) {
            MsgDay msgDay = new MsgDay(
                    result.cursor
                    *//*result.cursor.getInt(result.cursor.getColumnIndex("day")),
                    result.cursor.getInt(result.cursor.getColumnIndex("month")),
                    result.cursor.getInt(result.cursor.getColumnIndex("year")),
                    result.cursor.getInt(result.cursor.getColumnIndex("week")),
                    result.cursor.getInt(result.cursor.getColumnIndex("inout")),
                    result.cursor.getLong(result.cursor.getColumnIndex("time")),
                    result.cursor.getFloat(result.cursor.getColumnIndex("money")),
                    result.cursor.getString(result.cursor.getColumnIndex("class")),
                    result.cursor.getString(result.cursor.getColumnIndex("count")),
                    result.cursor.getString(result.cursor.getColumnIndex("other"))*//*
            );
            mMsgDayList.add(msgDay);
        }*/
        ((ImageButton) findViewById(R.id.dayactivity_ib_return)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
