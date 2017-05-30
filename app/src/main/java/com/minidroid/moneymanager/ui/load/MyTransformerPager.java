package com.minidroid.moneymanager.ui.load;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;
import android.view.ViewGroup;

import com.minidroid.moneymanager.R;

public class MyTransformerPager implements PageTransformer {
	/**
	 * @author minidroid
	 * position参数就是页面左边的位置
	 * 当我们的ViewPager滑动的时候，每一个页面都会回调这个方法
	 * view：某个页面对应的视图，就是布局XML
	 * */
	@Override
	public void transformPage(View view, float position) {
		//渐变效果判断区间（-1，1）
		if (position<1&&position>-1) {
			ViewGroup r1=(ViewGroup) view.findViewById(R.id.rl);//此处获取的id值，三个Fragment的布局id都必须一样为rl,否则会报错误
			//视差加速效果，让里面的子控件都给一个加速偏移量
			for (int i = 0; i < r1.getChildCount(); i++) {
				float factor =(float) (Math.random()*2);//给出一个随机数使得每个控件的加速度大小不一致，才会看出效果
				View child=r1.getChildAt(i);
				//这里的position变化区间是-1~0~1,所以当向左边（X轴负方向）加速滑动的时候，此时需要一个负方向的加速度，position正好为负数
				//向右加速滑动的时候有正向加速度，然后此时的position为正的

				//这样会出现一个抖动的效果，因为子控件每次进入for循环都会随机的加速一次，每次都会调用factor而每次的factor都是不一样的，所以会有时候快有时候慢
				//这时候就需要这样处理，第一次调用后，第二次还是用这个值，那如何用view绑定一个随机数呢？？
				if (child.getTag()==null) {//如果是第一次调用，就设置随机数
					child.setTag(factor);
				}else{//如果不是第一次调用，就不需要再次调用，直接取第一次调用的获取到的随机数
					factor=(Float) child.getTag();
				}
				//1 往正方向加速效果
				child.setTranslationX(position*factor*child.getWidth());//child.getWidth()根据子控件的宽度大小，宽度大的加速度就越大
				//2 往反方向减速效果
				// child.setTranslationX(-position*factor*child.getWidth());//
				//缩放效果

			}
			//3 缩放效果1
			//缩放范围0~1,为什么要用1-Math.abs(position)绝对值呢？
			//因为分析可知当position由0到1的时候，此时屏幕正显示的是position为0，但是整个页面已全部显示，此时的Scale为1
			//当向左边滑动的时候，position由0到1，Scale却是由1到0，所以用1-绝对值，正好可以对应Scale的变化
			/*		r1.setScaleX(1-Math.abs(position));
			r1.setScaleY(1-Math.abs(position));*/
			//4 缩放效果2,先缩放缩放到小于0.8的时候就直接取最大值0.8固定的了，效果就是缩到一定的大小就不会缩放
			r1.setScaleX(Math.max(0.8f, 1-Math.abs(position)));
			r1.setScaleY(Math.max(0.8f, 1-Math.abs(position)));
			//5 3D翻外转效
			//绕着y轴旋转
			r1.setPivotX(position<0f?r1.getWidth():0f);//设置旋转中心点
			r1.setPivotY(r1.getHeight()*0.5f);
			r1.setRotationY(position*60);//如果这里的值越大，两个角度越小，像锥形

			//6 3D内翻转
		/*	r1.setPivotX(position<0f?r1.getWidth():0f);//设置旋转中心点
			r1.setPivotY(r1.getHeight()*0.5f);
			r1.setRotationY(-position*60);//如果这里的值越大，两个角度越小，像锥形
*/
		/*	// 7 绕着自身正中心旋转
			r1.setPivotX(r1.getHeight()*0.5f);//设置旋转中心点
			r1.setPivotY(r1.getHeight()*0.5f);
			r1.setRotationY(-position*60);//如果这里的值越大，两个角度越小，像锥形
*/			}
	}
}
