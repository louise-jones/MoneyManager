package com.minidroid.moneymanager.ui.main;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.InOut;
import com.minidroid.moneymanager.bean.MsgDay;
import com.minidroid.moneymanager.permission.PermissionListener;
import com.minidroid.moneymanager.permission.PermissionManagerActivity;
import com.minidroid.moneymanager.ui.about.AboutActivity;
import com.minidroid.moneymanager.ui.centerperson.PersonCenterActivity;
import com.minidroid.moneymanager.utils.AppUtils;
import com.minidroid.moneymanager.utils.LogUtils;
import com.minidroid.moneymanager.utils.SDUtils;
import com.minidroid.moneymanager.view.swipemenulistview.custom.CustomToast;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 更多Fragment
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class MoreFragment extends Fragment {
    private TextView fragment_more_tv_person_center, fragment_more_tv_clear,
            fragment_more_tv_pull, fragment_more_tv_version, fragment_more_tv_us;
    private ProgressDialog mProgressDialog;

    /**
     * 需要的权限数组
     */
    private String[] permissions;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_more, null);
        initView(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化布局
     *
     * @param view
     */
    private void initView(View view) {
        // 权限数组初始化
        permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        String version = AppUtils.getVersion(getActivity());
        mProgressDialog = new ProgressDialog(getActivity());
        //个人中心、清除下载、导出Excel、版本更新、关于我们
        fragment_more_tv_person_center = (TextView) view.findViewById(R.id
                .fragment_more_tv_person_center);
        fragment_more_tv_clear = (TextView) view.findViewById(R.id.fragment_more_tv_clear);
        fragment_more_tv_pull = (TextView) view.findViewById(R.id.fragment_more_tv_pull);
        fragment_more_tv_version = (TextView) view.findViewById(R.id.fragment_more_tv_version);
        fragment_more_tv_us = (TextView) view.findViewById(R.id.fragment_more_tv_us);
        fragment_more_tv_version.setText("版本更新" + "(" + version + ")");
        //设置监听器
        fragment_more_tv_person_center.setOnClickListener(mListener);
        fragment_more_tv_clear.setOnClickListener(mListener);
        fragment_more_tv_pull.setOnClickListener(mListener);
        fragment_more_tv_version.setOnClickListener(mListener);
        fragment_more_tv_us.setOnClickListener(mListener);
    }

    //监听器
    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == null) return;
            switch (v.getId()) {
                case R.id.fragment_more_tv_person_center:
                    Intent personIntent = new Intent(getActivity(), PersonCenterActivity.class);
                    startActivity(personIntent);
                    getActivity().overridePendingTransition(R.anim.in_load, R.anim.out_load);
                    break;
                case R.id.fragment_more_tv_clear:
                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        //  /storage/emulated/0/appcache/imgcache/
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                                + File.separator + "appX.DownloadApp.hiyo";
                        File file = new File(path);
                        file.mkdirs();
                        deleFile(file);
                        CustomToast.showToast(getActivity(), "清除完成", Toast.LENGTH_SHORT);
                    }
                    break;
                case R.id.fragment_more_tv_version:
                    mProgressDialog.show();
                    //获取新版本
                    BDAutoUpdateSDK.uiUpdateAction(getActivity(), new MyUICheckUpdateCallback());
                    break;
                case R.id.fragment_more_tv_pull:
                    requestPermissions();
                    break;
                case R.id.fragment_more_tv_us:
                    Intent aboutIntent = new Intent(getActivity(), AboutActivity.class);
                    startActivity(aboutIntent);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 申请读写SD卡的权限
     */
    private void requestPermissions() {
        PermissionManagerActivity.requestPermissions(permissions, new PermissionListener() {
            @Override
            public void onGranted() {
                initData();
            }

            @Override
            public void onDenied(List<String> deniedPermissionList) {

            }
        });
    }
    private class MyUICheckUpdateCallback implements UICheckUpdateCallback {
        @Override
        public void onCheckComplete() {
            mProgressDialog.dismiss();
            CustomToast.showToast(getActivity(), "已是最新版本！", Toast.LENGTH_SHORT);
        }
    }

    /**
     * 递归删除下载
     *
     * @param file
     */
    private void deleFile(File file) {
        if (file.isDirectory()) {
            File f[] = file.listFiles();
            for (int i = 0; i < f.length; i++) {
                if (f[i].isDirectory()) {
                    deleFile(f[i]);
                } else {
                    f[i].delete();
                }
            }
        }
        file.delete();
    }

    /**
     * 初始化导出的Excel的数据格式
     */
    private void initData() {
        BmobQuery<InOut> query = new BmobQuery<>();
        query.findObjects(new FindListener<InOut>() {
            @Override
            public void done(List<InOut> list, BmobException e) {
                if (e == null) {
                    LogUtils.i("查询收支表成功:" + list.size());
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            SDUtils.delete("理财小能手.csv");
                            SDUtils.save("理财小能手.csv", "年," + "月," + "日," + "星期," + "收支," + "记录时间," +
                                    "" + "金额," + "分类," + "账户," + "备注");
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
                            SDUtils.save("理财小能手.csv", msgDay.toString());
                            CustomToast.showToast(getActivity(), "成功保存在理财小能手文件夹", Toast.LENGTH_SHORT);
                        }
                    } else {
                        CustomToast.showToast(getActivity(), "暂无收支记录", Toast.LENGTH_SHORT);
                    }
                } else {
                    LogUtils.i("查询收支表失败:" + e.getMessage());
                }
            }
        });
        /*SqliteManager.QueryResult result = SqliteManager.getInstance(getActivity()).query("inout", null, null);
        if (result.cursor == null) return;
        SDUtils.delete("理财小能手.csv");
        SDUtils.save("理财小能手.csv", "年," + "月," + "日," + "星期," + "收支," + "记录时间," + "金额," + "分类," + "账户," + "备注");
        while (result.cursor.moveToNext()) {
            MsgDay msgDay = new MsgDay(result.cursor);
            SDUtils.save("理财小能手.csv", msgDay.toString());
        }
        CustomToast.showToast(getActivity(), "成功保存在理财小能手文件夹", Toast.LENGTH_SHORT);*/
    }

}