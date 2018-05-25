package com.frank.mobilesafe.view;

import com.frank.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.frank.mobilesafe";
	@SuppressWarnings("unused")
	private static final String tag = "SettingItemView";
	private CheckBox cb_box;
	private TextView tv_des;
	private String mDestitle;
	private String mDeson;
	private String mDesoff;

	public SettingItemView(Context context) {
		this(context, null);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// xml--->view 将设置界面的一个条目转换成view对象,直接添加到了当前SettingItemView对应的view中
		View.inflate(context, R.layout.setting_item_view, this);

		// 等同于以下两行代码
		/*
		 * View view = View.inflate(context, R.layout.setting_item_view, null);
		 * this.addView(view);
		 */

		// 自定义组合控件中的标题描述
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);
		cb_box = (CheckBox) findViewById(R.id.cb_box);

		// 获取自定义以及原生属性的操作 AttributeSet attrs对象中获取 Set代表一个集合
		initAttrs(attrs);
		// 获取布局文件中定义的字符串，赋值给自定义组合控件的标题
		tv_title.setText(mDestitle);
	}

	/**
	 * @param attrs
	 *            构造方法中维护好的属性集合 返回属性集合中自定义属性的属性值
	 */
	private void initAttrs(AttributeSet attrs) {
		mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
		mDeson = attrs.getAttributeValue(NAMESPACE, "deson");
		mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
	}

	/**
	 * @return 返回CheckBox状态
	 */
	public boolean isCheck() {
		return cb_box.isChecked();
	}

	/**
	 * @param isCheck
	 *          是否开启的变量
	 */
	public void setCheck(boolean isCheck) {
		// 当条目在选择过程中CheckBox的选中状态也跟随改变
		cb_box.setChecked(isCheck);
		if (isCheck) {
			tv_des.setText(mDeson);
		} else {
			tv_des.setText(mDesoff);
		}
	}

}
