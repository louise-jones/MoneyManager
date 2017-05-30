package com.minidroid.moneymanager.ui.detailmonth;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.minidroid.moneymanager.R;
import com.minidroid.moneymanager.bean.MsgMonth;
import com.minidroid.moneymanager.bean.MsgYear;
import com.minidroid.moneymanager.utils.FormatUtils;

import java.util.ArrayList;

/**
 * 月和年统计饼状图显示界面
 * Created by minidroid on 2017/4/24 10:14.
 * Email:460821714@qq.com
 */
public class MonthAndYearDetailPieChartFragment extends Fragment {
    private MsgMonth mMsgMonth = null;
    private MsgYear mMsgYear = null;
    //饼状图
    private PieChart fragment_monthdetail_piechart_account, fragment_monthdetail_piechart_class;

    private ArrayList<PieEntry> mEntries;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LinearLayout.inflate(getActivity(), R.layout.fragment_monthandyeardetail_piechart_list, null);
        if (getArguments().getSerializable("msgMonth") != null) {
            mMsgMonth = (MsgMonth) getArguments().getSerializable("msgMonth");
        }
        if (getArguments().getSerializable("msgYear") != null) {
            mMsgYear = (MsgYear) getArguments().getSerializable("msgYear");
        }
        initFragmentView(view);
        return view;
    }

    /**
     * 初始化界面
     *
     * @param view
     */
    private void initFragmentView(View view) {
        fragment_monthdetail_piechart_account = (PieChart) view.findViewById(R.id.fragment_monthdetail_piechart_account);
        fragment_monthdetail_piechart_class = (PieChart) view.findViewById(R.id.fragment_monthdetail_piechart_class);
        initPieChart(fragment_monthdetail_piechart_account, "账户");
        initPieChart(fragment_monthdetail_piechart_class, "分类");
    }

    private void initPieChart(PieChart pieChart, String centerText) {
        pieChart.setUsePercentValues(true);//显示成百分比
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawCenterText(true);  //饼状图中间可以添加文字
        pieChart.setCenterText(centerText);//饼状图中间的文字

        pieChart.setDrawHoleEnabled(true);     //设置是否显示中间圆
        pieChart.setHoleColor(Color.WHITE);    //设置中间圆的颜色

        pieChart.setTransparentCircleColor(Color.WHITE);// 设置透明圈的颜色。
        pieChart.setTransparentCircleAlpha(110);//设置透明圈的透明度（0-255）。

        pieChart.setHoleRadius(30f);   //设置中心圆孔半径占整个饼状图半径的百分比（100f 是最大=整个图表的半径），默认的50％的百分比（即50f）。
        pieChart.setTransparentCircleRadius(34f);//设置中心透明圈半径占整个饼状图半径的百分比，默认是 55％ 的半径 -> 大于默认是 50％ 的中心圆孔半径。

        pieChart.setRotationAngle(180f);//设置饼状图的旋转角度。默认是270f 。

        // 触摸旋转
        pieChart.setRotationEnabled(true);// 可以手动旋转
        pieChart.setHighlightPerTapEnabled(true);

        //初始化数据
        initData(centerText);

        //设置数据
        setData(pieChart, mEntries, centerText);

        pieChart.animateY(2000, Easing.EasingOption.EaseOutBack);//设置动画

        Legend l = pieChart.getLegend();//设置比例图
        l.setEnabled(false);    //禁用图例
    }

    //设置数据
    private void setData(PieChart pieChart, ArrayList<PieEntry> entries, String centerText) {
        PieDataSet dataSet = null;
        if ("账户".equals(centerText)) {
            dataSet = new PieDataSet(entries, "账户月收支统计");
        } else if ("分类".equals(centerText)) {
            dataSet = new PieDataSet(entries, "分类月收支统计");
        }
        dataSet.setSliceSpace(3f);// 设置被排除在饼图切片，默认之间的空间：0° - >没有空间，最大的45岁，最小0（无空格）。
        dataSet.setSelectionShift(5f);//选中饼状图时，向外扩张的大小.

        //数据和颜色
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);    //设置饼状图其中各个块上的百分比颜色
        pieChart.setData(data);
        pieChart.highlightValues(null);    //高亮显示值，高亮显示的点击的位置在数据集中的值。 设置null或空数组则撤消所有高亮。
        //刷新
        pieChart.invalidate();
    }

    //初始化数据
    private void initData(String centerText) {
        if ("账户".equals(centerText)) {
            if (mMsgMonth != null) {
                mEntries = new ArrayList<PieEntry>();
                int accountNumber = mMsgMonth.getCountMsgList().size();
                for (int index = 0; index < accountNumber; index++) {
                    String formatMoney = FormatUtils.format2Decimal(Math.abs(mMsgMonth.getCountMsgList().get(index).money));
                    String countName = mMsgMonth.getCountMsgList().get(index).countName;
                    mEntries.add(new PieEntry(Float.valueOf(formatMoney) * 10, countName));
                }
            } else if (mMsgYear != null) {
                mEntries = new ArrayList<PieEntry>();
                int accountNumber = mMsgYear.getCountMsgList().size();
                for (int index = 0; index < accountNumber; index++) {
                    String formatMoney = FormatUtils.format2Decimal(Math.abs(mMsgYear.getCountMsgList().get(index).money));
                    String countName = mMsgYear.getCountMsgList().get(index).countName;
                    mEntries.add(new PieEntry(Float.valueOf(formatMoney) * 10, countName));
                }
            }
        } else if ("分类".equals(centerText)) {
            if (mMsgMonth != null) {
                mEntries = new ArrayList<PieEntry>();
                int classNumber = mMsgMonth.getClassMsgList().size();
                for (int index = 0; index < classNumber; index++) {
                    String formatMoney = FormatUtils.format2Decimal(Math.abs(mMsgMonth.getClassMsgList().get(index).money));
                    String className = mMsgMonth.getClassMsgList().get(index).className;
                    mEntries.add(new PieEntry(Float.valueOf(formatMoney) * 10, className));
                }
            } else if (mMsgYear != null) {
                mEntries = new ArrayList<PieEntry>();
                int classNumber = mMsgYear.getClassMsgList().size();
                for (int index = 0; index < classNumber; index++) {
                    String formatMoney = FormatUtils.format2Decimal(Math.abs(mMsgYear.getClassMsgList().get(index).money));
                    String className = mMsgYear.getClassMsgList().get(index).className;
                    mEntries.add(new PieEntry(Float.valueOf(formatMoney) * 10, className));
                }
            }
        }
    }
}
