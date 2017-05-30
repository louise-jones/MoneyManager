package com.minidroid.moneymanager.ui.accounttransfer;

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
import com.minidroid.moneymanager.permission.PermissionManagerActivity;
import com.minidroid.moneymanager.ui.accountselect.SelectAccountActivity;
import com.minidroid.moneymanager.ui.input.InputActivity;
import com.minidroid.moneymanager.utils.AppUtils;
import com.minidroid.moneymanager.utils.LogUtils;
import com.minidroid.moneymanager.utils.UserFactory;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 转账业务界面
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class TransferAccountsActivity extends PermissionManagerActivity {
    private int mDay, mMonth, mYear, mWeek;
    private LinearLayout transfer_account_ll_money, transfer_account_ll_in, transfer_account_ll_out, transfer_account_ll_time;
    private TextView transfer_account_tv_money, transfer_account_tv_in, transfer_account_tv_out, transfer_account_tv_time;
    private EditText transfer_account_et_note;
    private ImageButton baseactivity_ib_ok, baseactivity_ib_return;
    private Calendar mCalendar;
    private Account inAccount;
    private Account outAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setTranslucentStatus(TransferAccountsActivity.this);
        setContentView(R.layout.activity_transfer_account);
        initView();
        initData();

    }

    /**
     * 初始化界面
     */
    private void initView() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        //业务标题
        ((TextView) findViewById(R.id.baseactivity_tv_title)).setText("转账/存取款");
        //金额布局、支出账号布局、收入账户布局、时间
        transfer_account_ll_money = (LinearLayout) findViewById(R.id.transfer_account_ll_money);
        transfer_account_ll_in = (LinearLayout) findViewById(R.id.transfer_account_ll_in);
        transfer_account_ll_out = (LinearLayout) findViewById(R.id.transfer_account_ll_out);
        transfer_account_ll_time = (LinearLayout) findViewById(R.id.transfer_account_ll_time);
        //金额、支出账户、收入账户、时间
        transfer_account_tv_money = (TextView) findViewById(R.id.transfer_account_tv_money);
        transfer_account_tv_in = (TextView) findViewById(R.id.transfer_account_tv_in);
        transfer_account_tv_out = (TextView) findViewById(R.id.transfer_account_tv_out);
        transfer_account_tv_time = (TextView) findViewById(R.id.transfer_account_tv_time);
        //备注
        transfer_account_et_note = (EditText) findViewById(R.id.transfer_account_et_note);
        //保存按钮、返回按钮
        baseactivity_ib_ok = (ImageButton) findViewById(R.id.baseactivity_ib_ok);
        baseactivity_ib_return = (ImageButton) findViewById(R.id.baseactivity_ib_return);
        //添加监听器
        transfer_account_ll_money.setOnClickListener(mListener);
        transfer_account_ll_in.setOnClickListener(mListener);
        transfer_account_ll_out.setOnClickListener(mListener);
        transfer_account_ll_time.setOnClickListener(mListener);
        baseactivity_ib_ok.setOnClickListener(mListener);
        baseactivity_ib_return.setOnClickListener(mListener);
    }

    /**
     * 初始化转账时间（默认当前时间）
     */
    private void initData() {
        mCalendar = Calendar.getInstance(Locale.CHINA);//创建一个日历对象
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
        transfer_account_tv_time.setText(mYear + "年" + mMonth + "月" + mDay + "日");
    }

    //事件监听器
    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == null) return;
            switch (v.getId()) {
                case R.id.transfer_account_ll_money:
                    startActivityForResult(new Intent(TransferAccountsActivity.this, InputActivity.class), 0);
                    break;
                case R.id.transfer_account_ll_in:
                    startActivityForResult(new Intent(TransferAccountsActivity.this, SelectAccountActivity.class), 1);
                    break;
                case R.id.transfer_account_ll_out:
                    startActivityForResult(new Intent(TransferAccountsActivity.this, SelectAccountActivity.class), 2);
                    break;
                case R.id.transfer_account_ll_time:
                    new DatePickerDialog(TransferAccountsActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                            transfer_account_tv_time.setText(mYear + "年" + mMonth + "月" + mDay + "日");
                        }
                    },
                            mCalendar.get(Calendar.YEAR),
                            mCalendar.get(Calendar.MONTH),
                            mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    break;
                case R.id.baseactivity_ib_ok:
                    if ("00.00".equals(transfer_account_tv_money.getText().toString())) {
                        CustomToast.showToast(TransferAccountsActivity.this, "金额不能为0", Toast.LENGTH_SHORT);
                        return;
                    }
                    if (transfer_account_tv_in.getText().toString().length() == 0 || transfer_account_tv_out.getText().toString().length() == 0) {
                        CustomToast.showToast(TransferAccountsActivity.this, "账户不能为空", Toast.LENGTH_SHORT);
                        return;
                    }
                    if (transfer_account_tv_in.getText().toString().equals(transfer_account_tv_out.getText().toString())) {
                        CustomToast.showToast(TransferAccountsActivity.this, "账户不能相同", Toast.LENGTH_SHORT);
                        return;
                    }
                    if (Double.valueOf(transfer_account_tv_money.getText().toString()) >
                            outAccount.getNumber()) {
                        CustomToast.showToast(TransferAccountsActivity.this, "支出账户余额不足", Toast
                                .LENGTH_SHORT);
                    } else {
                        insertData();
                    }
                    break;
                case R.id.baseactivity_ib_return:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 将转账信息保存到数据库
     */
    private void insertData() {
        InOut inInOut = new InOut();
        inInOut.setYear(mYear);
        inInOut.setMonth(mMonth);
        inInOut.setDay(mDay);
        inInOut.setWeek(mWeek);
        inInOut.setMoney(Float.valueOf(transfer_account_tv_money.getText
                ().toString()));
        inInOut.setInOut(1);
        inInOut.setClazz("转账/存取款");
        inInOut.setAccount(inAccount);
        inInOut.setTime(System.currentTimeMillis());
        inInOut.setOther(transfer_account_et_note.getText().toString());
        inInOut.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    LogUtils.i("保存转账/存取成功");
                    Account newAccount = new Account();
                    newAccount.setNumber(Double.parseDouble
                            (transfer_account_tv_money.getText().toString
                                    ()) + inAccount.getNumber());
                    updateAccount(newAccount, inAccount);
                } else {
                    LogUtils.i("保存转账/存取失败:" + e.getMessage());
                }
            }
        });
        InOut inInOut2 = new InOut();
        inInOut2.setYear(mYear);
        inInOut2.setMonth(mMonth);
        inInOut2.setDay(mDay);
        inInOut2.setWeek(mWeek);
        inInOut2.setMoney(Float.valueOf(transfer_account_tv_money.getText
                ().toString()));
        inInOut2.setInOut(-1);
        inInOut2.setClazz("转账/存取款");
        inInOut2.setAccount(outAccount);
        inInOut2.setTime(System.currentTimeMillis());
        inInOut2.setOther(transfer_account_et_note.getText().toString());
        inInOut2.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    LogUtils.i("保存转账/存取成功");
                    Account newAccount = new Account();
                    newAccount.setNumber(-1 * Double.parseDouble
                            (transfer_account_tv_money.getText().toString
                                    ()) + outAccount.getNumber());
                    updateAccount(newAccount, outAccount);
                    CustomToast.showToast(TransferAccountsActivity.this,
                            "转账/存取款成功", Toast.LENGTH_SHORT);
                    finish();
                } else {
                    LogUtils.i("保存转账/存取失败:" + e.getMessage());
                }
            }
        });
       /* ContentValues valuesIn = new ContentValues();
        valuesIn.put("year", mYear);
        valuesIn.put("month", mMonth);
        valuesIn.put("day", mDay);
        valuesIn.put("week", mWeek);
        valuesIn.put("money", transfer_account_tv_money.getText().toString());
        valuesIn.put("inout", 1);
        valuesIn.put("class", "转账/存取款");
        valuesIn.put("count", transfer_account_tv_in.getText().toString());
        valuesIn.put("time", System.currentTimeMillis() + "");
        valuesIn.put("other", transfer_account_et_note.getText().toString());
        SqliteManager.getInstance(this).insertItem("inout", valuesIn);

        SqliteUtils.update(TransferAccountsActivity.this, transfer_account_tv_in.getText()
        .toString(),
                Double.parseDouble(transfer_account_tv_money.getText().toString()));
//
        ContentValues valuesOut = new ContentValues();
        valuesOut.put("year", mYear);
        valuesOut.put("month", mMonth);
        valuesOut.put("day", mDay);
        valuesOut.put("week", mWeek);
        valuesOut.put("money", transfer_account_tv_money.getText().toString());
        valuesOut.put("inout", -1);
        valuesOut.put("class", "转账/存取款");
        valuesOut.put("account", transfer_account_tv_out.getText().toString());
        valuesOut.put("time", System.currentTimeMillis() + "");
        valuesOut.put("other", transfer_account_et_note.getText().toString());
        SqliteManager.getInstance(this).insertItem("inout", valuesOut);

        SqliteUtils.update(TransferAccountsActivity.this, transfer_account_tv_out.getText()
        .toString(),
                -1 * Double.parseDouble(transfer_account_tv_money.getText().toString()));
        CustomToast.showToast(this, "添加成功", Toast.LENGTH_SHORT);*/
    }

    /**
     * 更新账户信息
     */
    private void updateAccount(final Account newAccount, Account oldAccount) {
        newAccount.update(oldAccount.getObjectId(), new UpdateListener() {
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    String update_money = data.getStringExtra("update_money");
                    if (update_money != null && update_money.length() != 0)
                        transfer_account_tv_money.setText(update_money);
                    break;
                case 1:
                    transfer_account_tv_in.setText(data.getStringExtra("msgaccount"));
                    BmobQuery<Account> query = new BmobQuery<>();
                    query.addWhereEqualTo("user", new BmobPointer(UserFactory.currentLoginUser));
                    query.addWhereEqualTo("accountName", data.getStringExtra("msgaccount"));
                    query.findObjects(new FindListener<Account>() {
                        @Override
                        public void done(List<Account> list, BmobException e) {
                            if (e == null) {
                                if (list.size() > 0) {
                                    inAccount = list.get(0);
                                }
                            } else {
                                LogUtils.i(e.getMessage());
                            }
                        }
                    });
                    break;
                case 2:
                    transfer_account_tv_out.setText(data.getStringExtra("msgaccount"));
                    BmobQuery<Account> query2 = new BmobQuery<>();
                    query2.addWhereEqualTo("user", new BmobPointer(UserFactory.currentLoginUser));
                    query2.addWhereEqualTo("accountName", data.getStringExtra("msgaccount"));
                    query2.findObjects(new FindListener<Account>() {
                        @Override
                        public void done(List<Account> list, BmobException e) {
                            if (e == null) {
                                if (list.size() > 0) {
                                    outAccount = list.get(0);
                                }
                            } else {
                                LogUtils.i(e.getMessage());
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
