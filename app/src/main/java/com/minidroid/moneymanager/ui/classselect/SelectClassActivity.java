package com.minidroid.moneymanager.ui.classselect;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.permission.PermissionManagerActivity;

import static com.minidroid.moneymanager.R.id.baseactivity_ib_return;

/**
 * 选择类别界面
 * Created by minidroid on 2017/4/21.
 * Email:460821714@qq.com
 */
public class SelectClassActivity extends PermissionManagerActivity {
    private ListView class_lv_left, class_lv_left_right;
    private LeftAdapter mMenuAdapter;//一级菜单适配器
    private int mPosition = -1;
    private String[] class_left = new String[]{"衣服饰品", "食品酒水", "居家物业", "行车交通", "交流通讯",
            "休闲娱乐", "学习进修", "人情往来", "医疗保健", "其他杂项"};
    private String[][] class_right = new String[][]{
            {"衣服裤子", "鞋帽包包", "化妆品"},
            {"早午晚餐", "烟酒茶", "水果零食"},
            {"日常用品", "水电煤气", "房租", "按揭还贷"},
            {"公共交通", "打车租车"},
            {"手机费", "上网费", "邮寄费"},
            {"运动健身", "腐败聚会", "休闲娱乐", "旅游度假"},
            {"书报杂志", "培训进修", "数码装备"},
            {"人情往来", "送礼请客", "孝敬长辈", "还人钱财"},
            {"药品费", "保健费", "美容费", "治疗费"},
            {"其他支出",}
    };
    public static int[][] class_rightimg = new int[][]{
            {R.drawable.icon_yfsp_yfkz, R.drawable.icon_yfsp_xmbb, R.drawable.icon_yfsp_hzsp},
            {R.drawable.splash_emotional_icon_egg, R.drawable.splash_emotional_icon_cup, R.drawable.splash_emotional_icon_icecream},
            {R.drawable.icon_jjwy_rcyp, R.drawable.icon_jjwy_sdmq, R.drawable.icon_jjwy_fz, R.drawable.icon_jrbx_ajhk},
            {R.drawable.icon_xcjt_ggjt, R.drawable.icon_xcjt_dczc},
            {R.drawable.icon_jltx_sjf, R.drawable.icon_jltx_swf, R.drawable.icon_jltx_yjf},
            {R.drawable.splash_emotional_icon_dumbbell, R.drawable.icon_xxyl_fbjh, R.drawable.splash_emotional_icon_gamehandle, R.drawable.splash_emotional_icon_trunk},
            {R.drawable.icon_xxjx_sbzz, R.drawable.splash_emotional_icon_bottle, R.drawable.splash_emotional_icon_camera},
            {R.drawable.icon_rqwl, R.drawable.icon_rqwl_csjz, R.drawable.icon_rqwl_xjjz, R.drawable.icon_rqwl_hrqc},
            {R.drawable.icon_ylbj_ypf, R.drawable.icon_ylbj_bjf, R.drawable.icon_ylbj_mrf, R.drawable.icon_ylbj_zlf},
            {R.drawable.icon_qtzx_qtzc},
            {R.drawable.go_add_income_btn_selected}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_out);
        initView();
        setListener();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        class_lv_left = (ListView) findViewById(R.id.class_lv_left);
        class_lv_left_right = (ListView) findViewById(R.id.class_lv_right);
        class_lv_left_right.setVisibility(View.VISIBLE);
        findViewById(R.id.baseactivity_ib_ok).setVisibility(View.GONE);
        mMenuAdapter = new LeftAdapter();
        class_lv_left.setAdapter(mMenuAdapter);
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

    /**
     * 给一级菜单和二级菜单设置监听器
     */
    private void setListener() {
        class_lv_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPosition = position;
                mMenuAdapter.setPosition(position);
                mMenuAdapter.notifyDataSetChanged();
                RightAdapter rightAdapter = new RightAdapter(class_right[position], class_rightimg[position]);
                class_lv_left_right.setAdapter(rightAdapter);
            }
        });
        class_lv_left_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getIntent();
                intent.putExtra("msgresourceid", class_rightimg[mPosition][position]+"");
                intent.putExtra("msgclass",/* class_left[mPosition] + ">" + */class_right[mPosition][position]);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

class LeftAdapter extends BaseAdapter {
    private int mPosition = -1;

    @Override
    public int getCount() {
        return class_left.length;
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
        TextView tv;
        if (convertView == null) {
            convertView = LayoutInflater.from(SelectClassActivity.this).inflate(R.layout.item_tv, null);
            tv = (TextView) convertView.findViewById(R.id.item_tv_tv);
            convertView.setTag(tv);
        } else {
            tv = (TextView) convertView.getTag();
        }
        tv.setText(class_left[position]);
        if (mPosition == position) {
            convertView.setBackgroundColor(SelectClassActivity.this.getResources().getColor(R.color.background_activity));
        } else {
            convertView.setBackgroundColor(getResources().getColor(R.color.item_down));
        }
        return convertView;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }
}

class RightAdapter extends BaseAdapter {
    private String[] data;
    private int[] resouce;

    public RightAdapter(String[] data, int[] resouce) {
        this.data = data;
        this.resouce = resouce;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.length;
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
        ViewHoler vh;
        if (convertView == null) {
            vh = new ViewHoler();
            convertView = LayoutInflater.from(SelectClassActivity.this).inflate(R.layout.lv_right_item, null);
            vh.img = (ImageView) convertView.findViewById(R.id.item_right_img);
            vh.tv = (TextView) convertView.findViewById(R.id.item_right_tv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHoler) convertView.getTag();
        }
        vh.tv.setText(data[position]);
        vh.img.setImageResource(resouce[position]);
        return convertView;
    }

    class ViewHoler {
        TextView tv;
        ImageView img;
    }
}
}
