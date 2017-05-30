package com.minidroid.moneymanager.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.Account;
import com.minidroid.moneymanager.ui.accountadd.AddAccountActivity;
import com.minidroid.moneymanager.ui.accounttransfer.TransferAccountsActivity;
import com.minidroid.moneymanager.ui.detailaccount.AccountDetailActivity;
import com.minidroid.moneymanager.ui.input.InputActivity;
import com.minidroid.moneymanager.ui.login.LoginActivity;
import com.minidroid.moneymanager.utils.DialogUtils;
import com.minidroid.moneymanager.utils.FormatUtils;
import com.minidroid.moneymanager.utils.LogUtils;
import com.minidroid.moneymanager.utils.UserFactory;
import com.minidroid.moneymanager.view.swipemenulistview.SwipeMenu;
import com.minidroid.moneymanager.view.swipemenulistview.SwipeMenuCreator;
import com.minidroid.moneymanager.view.swipemenulistview.SwipeMenuItem;
import com.minidroid.moneymanager.view.swipemenulistview.SwipeMenuListView;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomToast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 账户Fragment
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class AccountFragment extends Fragment {
    private TextView fragment_count_tv_total;
    private SwipeMenuListView count_lv_count;
    private TextView count_tv_add, count_tv_change;
    private AccountAdapter mAccountAdapter;
    private List<Account> mAccountList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_account, null);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initData();
        }
    }

    /**
     * 初始化账户信息
     */
    private void initData() {
//        double total = 0;
        mAccountList = new ArrayList<>();
        //从Bmob上加载当前登录用户所有账户
        if (UserFactory.currentLoginUser != null) {
            BmobQuery<Account> query = new BmobQuery<>();
            query.addWhereEqualTo("user", UserFactory.currentLoginUser);
            query.findObjects(new FindListener<Account>() {
                @Override
                public void done(List<Account> list, BmobException e) {
                    if (e == null) {
                        mAccountList = list;
                        LogUtils.i("查询当前登录用户的账户成功,账户个数:" + list.size());
                        if (list.size() == 0) {
                            CustomToast.showToast(getActivity(), "未添加账户，点击添加！", Toast.LENGTH_SHORT);
                        }
                        double total = 0;
                        for (int i = 0; i < list.size(); i++) {
                            total += list.get(i).getNumber();
                        }
                        fragment_count_tv_total.setText(FormatUtils.format2d(total));

                        mAccountAdapter = new AccountAdapter(list, getActivity());
                        count_lv_count.setAdapter(mAccountAdapter);
                        intItemMenu();
                    } else {
                        LogUtils.i("查询当前登录用户的账户失败:" + e.getMessage());
                    }
                }
            });
        } else {
            LogUtils.i("当前没有登录");
            CustomToast.showToast(getActivity(), "当前没有登录", Toast.LENGTH_LONG);
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
        /*SqliteManager.QueryResult result = SqliteManager.getInstance(getActivity()).query
        ("account", null, null);
        while (result.cursor.moveToNext()) {
            Account count = new Account();
            count.setAccountName(result.cursor.getString(result.cursor.getColumnIndex("accountname")));
            count.setNumber(result.cursor.getDouble(result.cursor.getColumnIndex("money")));
            total += result.cursor.getDouble(result.cursor.getColumnIndex("money"));
            mAccountList.add(count);
        }
        result.cursor.close();
        result.db.close();
        fragment_count_tv_total.setText(FormatUtils.format2d(total));
        if (mAccountList.size() == 0) {
            CustomToast.showToast(getActivity(), "未添加账户，点击添加！", Toast.LENGTH_SHORT);
        }
        mAccountAdapter = new AccountAdapter(mAccountList, getActivity());
        count_lv_count.setAdapter(mAccountAdapter);
        intItemMenu();*/
    }

    /**
     * 初始化布局
     *
     * @param view
     */
    private void initView(View view) {
        //账户列表
        count_lv_count = (SwipeMenuListView) view.findViewById(R.id.count_lv_count);
        //添加账户按钮
        count_tv_add = (TextView) view.findViewById(R.id.count_tv_add);
        //转账/存取款按钮
        count_tv_change = (TextView) view.findViewById(R.id.count_tv_change);
        //总额
        fragment_count_tv_total = (TextView) view.findViewById(R.id.fragment_count_tv_total);
        //添加监听器
        count_tv_add.setOnClickListener(mAccountListener);
        count_tv_change.setOnClickListener(mAccountListener);
    }

    /**
     * 事件监听器
     */
    private View.OnClickListener mAccountListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == null) return;
            Intent intent = null;
            switch (v.getId()) {
                case R.id.count_tv_add:     //添加账户
                    intent = new Intent(AccountFragment.this.getActivity(), AddAccountActivity.class);
                    break;
                case R.id.count_tv_change:  //转账业务
                    intent = new Intent(AccountFragment.this.getActivity(), TransferAccountsActivity.class);
                    break;
                default:
                    break;
            }
            startActivity(intent);
        }
    };

    /**
     * 初始化账户子布局
     */
    private void intItemMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                //修改按钮
                SwipeMenuItem updateItem = new SwipeMenuItem(getActivity().getApplicationContext());
                updateItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                updateItem.setWidth(dp2px(80));
                //openItem.setTitle("Open");
                updateItem.setTitleSize(18);
                //openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(updateItem);
                updateItem.setIcon(R.drawable.ic_gai);
                //删除按钮
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(dp2px(80));
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);
            }
        };
        count_lv_count.setMenuCreator(creator);
        //账户子布局的点击事件
        count_lv_count.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                final int i = position;
                switch (index) {
                    case 0:
                        DialogUtils.show(getActivity(), "确定要修改该账户么！", new DialogUtils.DialogCallBack() {
                            @Override
                            public void doListener() {
                                Intent intent = new Intent(AccountFragment.this.getActivity(), InputActivity.class);
                                startActivityForResult(intent, i);
                            }
                        });
                        break;
                    case 1:
                        DialogUtils.show(getActivity(), "确定要删除该账户么！", new DialogUtils.DialogCallBack() {
                            @Override
                            public void doListener() {
                                //将Bmob上的该账户删除
                                Account deleteAccount = new Account();
                                deleteAccount.setObjectId(mAccountList.get(i).getObjectId());
                                deleteAccount.delete(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            LogUtils.i("删除账户成功");
                                            initData();
                                        } else {
                                            LogUtils.i("删除账户失败");
                                        }
                                    }
                                });
//                                SqliteManager.getInstance(getActivity()).delteItem
//                                        ("account", "accountname=?", new String[]{mAccountList.get(i).getAccountName()});
//                                initData();
                            }
                        });
                        break;
                }
                return false;
            }
        });
        //账户的点击事件
        count_lv_count.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), AccountDetailActivity.class);
                intent.putExtra("accountObjectId", mAccountList.get(position).getObjectId());
                intent.putExtra("accountname", mAccountList.get(position).getAccountName());
                startActivity(intent);
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (resultCode == getActivity().RESULT_OK) {
            ContentValues values = new ContentValues();
            values.put("money", data.getStringExtra("update_money"));
            SqliteManager.getInstance(getActivity()).updateItem
                    ("account", "accountname=?", new String[]{mAccountList.get(requestCode).getAccountName() + ""}, values);
        }*/
        //将修改的账户更新到Bmob
        if (resultCode == getActivity().RESULT_OK) {
            Account updateAccount = new Account();
            updateAccount.setNumber(Double.valueOf(data.getStringExtra("update_money")));
            updateAccount.update(mAccountList.get(requestCode).getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        LogUtils.i("更新账户信息成功");
                    } else {
                        LogUtils.i("更新账户信息失败:" + e.getMessage());
                    }
                }
            });
        }
    }
}
