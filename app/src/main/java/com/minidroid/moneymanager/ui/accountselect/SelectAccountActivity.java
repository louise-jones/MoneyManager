package com.minidroid.moneymanager.ui.accountselect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.Account;
import com.minidroid.moneymanager.permission.PermissionManagerActivity;
import com.minidroid.moneymanager.utils.AppUtils;
import com.minidroid.moneymanager.utils.UserFactory;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomToast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.minidroid.moneymanager.R.id.baseactivity_ib_return;

/**
 * 选择账户界面
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class SelectAccountActivity extends PermissionManagerActivity {
    private ListView lv_count;
    private ArrayAdapter mCountAdapter;
    private List<String> mCountList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setTranslucentStatus(SelectAccountActivity.this);
        setContentView(R.layout.activity_class_out);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        findViewById(R.id.baseactivity_ib_ok).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.baseactivity_tv_title)).setText("选择账户");
        lv_count = (ListView) findViewById(R.id.class_lv_left);
        mCountList = new ArrayList<>();
        //从Bmob上查询账户
        BmobQuery<Account> query = new BmobQuery<>();
        query.addWhereEqualTo("user", new BmobPointer(UserFactory.currentLoginUser));
        query.findObjects(new FindListener<Account>() {
            @Override
            public void done(List<Account> list, BmobException e) {
                if (list.size() == 0) {
                    CustomToast.showToast(SelectAccountActivity.this, "请先添加账户！", Toast
                            .LENGTH_SHORT);
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        mCountList.add(list.get(i).getAccountName());
                    }
                }

                mCountAdapter = new ArrayAdapter(SelectAccountActivity.this, R.layout.item_tv,
                        mCountList);
                lv_count.setAdapter(mCountAdapter);

                lv_count.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long
                            id) {
                        Intent intent = getIntent();
                        intent.putExtra("msgaccount", mCountList.get(position));
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        });
//        SqliteManager.QueryResult result = SqliteManager.getInstance(this).query("account",
// null, null);
//        while (result.cursor.moveToNext()) {
//            mCountList.add(result.cursor.getString(result.cursor.getColumnIndex("accountname")));
//        }
//        result.cursor.close();
//        result.db.close();
    }

    public void onClick(View v) {
        if (v == null) return;
        switch (v.getId()) {
            case baseactivity_ib_return:
                finish();
                break;
            default:
                break;
        }
    }
}
