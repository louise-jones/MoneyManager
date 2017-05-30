package com.minidroid.moneymanager.ui.record;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.Account;
import com.minidroid.moneymanager.bean.InOut;
import com.minidroid.moneymanager.ui.accountselect.SelectAccountActivity;
import com.minidroid.moneymanager.ui.classselect.SelectClassActivity;
import com.minidroid.moneymanager.ui.input.InputActivity;
import com.minidroid.moneymanager.ui.main.BaseActivity;
import com.minidroid.moneymanager.utils.AppUtils;
import com.minidroid.moneymanager.utils.LogUtils;
import com.minidroid.moneymanager.utils.UserFactory;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 添加收入/支出界面(即记录界面)
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class RecordActivity extends BaseActivity {
    private Calendar mCalendar;
    private LinearLayout recordin_ll_money, record_ll_class, record_ll_count, record_ll_time, recordin_ll_inorout;
    private TextView record_tv_money, record_tv_class, record_tv_count, record_tv_time, record_tv_inorout;
    private ImageButton baseactivity_ib_ok;
    private EditText record_et_note;
    private int mYear, mMonth, mDay, mWeek;
    private int resourceId = SelectClassActivity.class_rightimg[1][0];
    private int isOut = -1;     //类型（支出或者收入）
    private Account selectedAccount;    //被选择的账户
    private Long serverTime; //服务器时间
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppUtils.setTranslucentStatus(RecordActivity.this);
        setContentView(R.layout.activity_record);
        super.onCreate(savedInstanceState);
        initData();
        initView();
    }

    /**
     * 初始化添加时间（默认为当前时间）
     */
    private void initData() {
        mCalendar = Calendar.getInstance(Locale.CHINA);//创建一个日历对象
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        //业务标题
        ((TextView) findViewById(R.id.baseactivity_tv_title)).setText("添加收入/支出");
        //金额、收入/支出、分类、账户、时间布局
        recordin_ll_money = (LinearLayout) findViewById(R.id.recordin_ll_money);
        recordin_ll_inorout = (LinearLayout) findViewById(R.id.recordin_ll_inorout);
        record_ll_class = (LinearLayout) findViewById(R.id.record_ll_class);
        record_ll_count = (LinearLayout) findViewById(R.id.record_ll_count);
        record_ll_time = (LinearLayout) findViewById(R.id.record_ll_time);
        //金额、收入/支出、分类、账户、时间、备注
        record_tv_money = (TextView) findViewById(R.id.record_tv_money);
        record_tv_money.setTextColor(getResources().getColor(R.color.text_out_color));
        record_tv_inorout = (TextView) findViewById(R.id.record_tv_inorout);
        record_tv_class = (TextView) findViewById(R.id.record_tv_class);
        record_tv_count = (TextView) findViewById(R.id.record_tv_count);
        record_tv_time = (TextView) findViewById(R.id.record_tv_time);
        record_et_note = (EditText) findViewById(R.id.record_et_note);
        record_tv_time.setText(mYear + "年" + mMonth + "月" + mDay + "日");
        record_tv_inorout.setText("支出");
        //保存按钮
        baseactivity_ib_ok = (ImageButton) findViewById(R.id.baseactivity_ib_ok);
        //添加监听器
        record_ll_class.setOnClickListener(mListener);
        recordin_ll_money.setOnClickListener(mListener);
        record_ll_count.setOnClickListener(mListener);
        record_ll_time.setOnClickListener(mListener);
        recordin_ll_inorout.setOnClickListener(mListener);
        baseactivity_ib_ok.setOnClickListener(mListener);
    }

    //监听器
    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == null) return;
            Intent intent;
            switch (v.getId()) {
                case R.id.recordin_ll_money:
                    startActivityForResult(new Intent(RecordActivity.this, InputActivity.class), 0);
                    break;
                case R.id.record_ll_class:
                    intent = new Intent(RecordActivity.this, SelectClassActivity.class);
                    if (isOut == -1) {
//                        intent.putExtra("flag", 1);
                        startActivityForResult(intent, 1);
                    } else {
                        if ("工资收入".equals(record_tv_class.getText())) {
                            record_tv_class.setText("其他收入");
                            resourceId = SelectClassActivity.class_rightimg[SelectClassActivity.class_rightimg.length - 1][0];
                        } else {
                            record_tv_class.setText("工资收入");
                            resourceId = SelectClassActivity.class_rightimg[SelectClassActivity.class_rightimg.length - 1][0];
                        }
                    }
                    break;
                case R.id.record_ll_count:
                    intent = new Intent(RecordActivity.this, SelectAccountActivity.class);
                    startActivityForResult(intent, 2);
                    break;
                case R.id.record_ll_time:
                    new DatePickerDialog(RecordActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            //月份默认从0开始
                            mYear = year;
                            mMonth = monthOfYear + 1;
                            mDay = dayOfMonth;
                            Calendar c = Calendar.getInstance(Locale.CHINA);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                c.setTime(format.parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth));
                                mWeek = c.get(Calendar.DAY_OF_WEEK);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            record_tv_time.setText(mYear + "年" + mMonth + "月" + mDay + "日");
                        }
                    },
                            mCalendar.get(Calendar.YEAR),
                            mCalendar.get(Calendar.MONTH),
                            mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    break;
                case R.id.recordin_ll_inorout:
                    isOut = -1 * isOut;
                    if (isOut == -1) {
                        record_tv_inorout.setText("支出");
                        record_tv_class.setText("早午晚餐");
                        record_tv_money.setTextColor(getResources().getColor(R.color.text_out_color));
                    } else {
                        record_tv_inorout.setText("收入");
                        record_tv_class.setText("工资收入");
                        record_tv_money.setTextColor(getResources().getColor(R.color.text_in_color));
                    }
                    break;
                case R.id.baseactivity_ib_ok:
                    if (record_tv_count.getText().toString().length() == 0) {
                        CustomToast.showToast(RecordActivity.this, "请选择账户！", Toast.LENGTH_SHORT);
                        return;
                    }
                    if ("00.00".equals(record_tv_money.getText().toString())) {
                        CustomToast.showToast(RecordActivity.this, "金额不能为0", Toast.LENGTH_SHORT);
                        return;
                    } else if (isOut == -1 && !checkMoney(Double.valueOf(record_tv_money.getText()
                            .toString()))) {        //是支出类型且该支出账户余额不足时
                        return;
                    } else {
                        insertData();
                    }
//                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 校验账户余额是否足够
     */
    private boolean checkMoney(Double money) {
        boolean isEnough = true;
        if (money > selectedAccount.getNumber()) {
            isEnough = false;
            CustomToast.showToast(this, "当前账户余额不足", Toast.LENGTH_SHORT);
        }
        return isEnough;
    }

    /**
     * 将收入/支出信息保存到Bmob
     */
    private void insertData() {
        LogUtils.i("inserData()...");
        InOut inOut = new InOut();
        inOut.setYear(mYear);
        inOut.setMonth(mMonth);
        inOut.setDay(mDay);
        inOut.setWeek(mWeek);
        inOut.setResourceId(resourceId);
        inOut.setMoney(Float.valueOf(record_tv_money.getText().toString()));
        inOut.setInOut(isOut);
        inOut.setClazz(record_tv_class.getText().toString());
        inOut.setTime(getServerTime());
        inOut.setOther(record_et_note.getText().toString());
        inOut.setAccount(selectedAccount);
        inOut.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    LogUtils.i("保存收支情况成功");
                    CustomToast.showToast(RecordActivity.this, "添加成功", Toast.LENGTH_SHORT);
                    Account newAccount = new Account();
                    newAccount.setNumber(selectedAccount.getNumber() + isOut * Double.parseDouble
                            (record_tv_money.getText()
                                    .toString()));
                    updateAccount(newAccount);
                } else {
                    LogUtils.i("保存收支情况失败:" + e.getMessage());
                }
                finish();
            }
        });
//        ContentValues values = new ContentValues();
//        values.put("year", mYear);
//        values.put("month", mMonth);
//        values.put("day", mDay);
//        values.put("week", mWeek);
//        values.put("resourceid", resourceId);
//        values.put("money", record_tv_money.getText().toString());
//        values.put("inout", isOut);
//        values.put("class", record_tv_class.getText().toString());
//        values.put("account", record_tv_count.getText().toString());
//        values.put("time", System.currentTimeMillis() + "");
//        values.put("other", record_et_note.getText().toString());
//        SqliteManager.getInstance(this).insertItem("inout", values);
//
//        SqliteUtils.update(RecordActivity.this, record_tv_count.getText().toString(),
//                isOut * Double.parseDouble(record_tv_money.getText().toString()));
//        CustomToast.showToast(this, "添加成功", Toast.LENGTH_SHORT);
    }

    /**
     * 更新账户信息
     */
    private void updateAccount(final Account newAccount) {
        newAccount.update(selectedAccount.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    LogUtils.i("更新账户成功");
                } else {
                    LogUtils.i("更新账户失败:" + e.getMessage());
                }
            }
        });
    }

    /**
     * 获取服务器时间
     *
     * @return
     */
    private Long getServerTime() {
        serverTime = System.currentTimeMillis();
        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long time, BmobException e) {
                if (e == null) {
                    LogUtils.i("获取服务器时间成功");
                    serverTime = time;
                } else {
                    LogUtils.i("获取服务器时间失败:" + e.getMessage());
                }
            }
        });
        return serverTime;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    String updateMoney = data.getStringExtra("update_money");
                    if (updateMoney != null && updateMoney.length() != 0)
                        record_tv_money.setText(updateMoney);
                    break;
                case 1:
                    resourceId = Integer.valueOf(data.getStringExtra("msgresourceid"));
                    record_tv_class.setText(data.getStringExtra("msgclass"));
                    break;
                case 2:
                    record_tv_count.setText(data.getStringExtra("msgaccount"));
                    //从Bmob查询相应账户
                    BmobQuery<Account> query = new BmobQuery<>();
                    query.addWhereEqualTo("user", new BmobPointer(UserFactory.currentLoginUser));
                    query.setLimit(1).addWhereEqualTo("accountName", data.getStringExtra
                            ("msgaccount"))
                            .findObjects(new FindListener<Account>() {
                                @Override
                                public void done(List<Account> list, BmobException e) {
                                    if (e == null) {
                                        LogUtils.i("查询账户成功");
                                        if (list.size() > 0) {
                                            selectedAccount = list.get(0);
                                        }
                                    } else {
                                        LogUtils.i("查询账户失败:" + e.getMessage());
                                    }
                                }
                            });
                    break;
                default:
                    break;
            }
        }
    }
}