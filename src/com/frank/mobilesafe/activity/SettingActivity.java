package com.frank.mobilesafe.activity;

import com.frank.mobilesafe.R;
import com.frank.mobilesafe.service.AddressService;
import com.frank.mobilesafe.service.AppLockService;
import com.frank.mobilesafe.service.BlackNumberService;
import com.frank.mobilesafe.utils.ConstantValue;
import com.frank.mobilesafe.utils.ServiceUtil;
import com.frank.mobilesafe.utils.SpUtil;
import com.frank.mobilesafe.utils.ToastUtil;
import com.frank.mobilesafe.view.SettingClickView;
import com.frank.mobilesafe.view.SettingItemView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {

	private String[] mToastStrleDes;
	private int mToast_style;
	private SettingClickView scv_toast_style;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.frank.mobilesafe.R.layout.activity_setting);
		initUpdate();
		initAddress();
		initToastStyle();
		initLocation();
		initBlacknumber();
		initAppLock();
	}

	/**
	 * 程序锁设置
	 */
	private void initAppLock() {
		final SettingItemView siv_app_lock = (SettingItemView) findViewById(R.id.siv_app_lock);
		//获取服务运行状态
		boolean isRunning = ServiceUtil.isRunning(this, "com.frank.mobilesafe.service.AppLockService");
		//用服务状态显示CheckBox
		siv_app_lock.setCheck(isRunning);
		
		siv_app_lock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean isCheck = siv_app_lock.isCheck();
				siv_app_lock.setCheck(!isCheck);
				if (!isCheck) {
					//开启服务
					startService(new Intent(getApplicationContext(), AppLockService.class));
				}else {
					//关闭服务
					stopService(new Intent(getApplicationContext(), AppLockService.class));
				}
				
			}
		});
		
	}

	/**
	 * 黑名单服务开启关闭设置
	 */
	private void initBlacknumber() {
		final SettingItemView siv_blacknumber = (SettingItemView) findViewById(R.id.siv_blacknumber);
		//获取服务运行状态
		boolean isRunning = ServiceUtil.isRunning(this, "com.frank.mobilesafe.service.BlackNumberService");
		//用服务状态显示CheckBox
		siv_blacknumber.setCheck(isRunning);
		
		siv_blacknumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean isCheck = siv_blacknumber.isCheck();
				siv_blacknumber.setCheck(!isCheck);
				if (!isCheck) {
					//开启服务
					startService(new Intent(getApplicationContext(), BlackNumberService.class));
				}else {
					//关闭服务
					stopService(new Intent(getApplicationContext(), BlackNumberService.class));
				}
				
			}
		});
	}

	/**
	 * 双击居中view所在屏幕位置的处理方法
	 */
	private void initLocation() {
		SettingClickView scv_toast_location = (SettingClickView) findViewById(R.id.scv_toast_location);
		scv_toast_location.setTitle("归属地提示框的位置");
		scv_toast_location.setDes("设置归属地提示框的位置");
		scv_toast_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), ToastLocationActivity.class));
			}
		});
	}

	/**
	 * 来电归属地显示主题风格
	 */
	private void initToastStyle() {
		scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);
		scv_toast_style.setTitle("电话归属地样式选择");
		mToastStrleDes = new String[]{"透明","橙色","蓝色","灰色","绿色",};
		mToast_style = SpUtil.getInt(this, ConstantValue.TOAST_STYLE, 0);
		//通过索引，获取字符串数组的文字，显示给描述内容控件
		scv_toast_style.setDes(mToastStrleDes[mToast_style]);		
		scv_toast_style.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//弹出对话框				
				showToastStyleDialog();
			}
		});
	}

	/**
	 * 吐司风格选择对话框
	 */
	protected void showToastStyleDialog() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);//设置图标
		builder.setTitle("选择归属地样式");
		//弹出选择条目对话框时,需要读取上次存储的索引值,作为默认选中条目
		mToast_style = SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
		//选择单个条目事件的监听
		//1:string类型的数组描述颜色文字数组
		//2:弹出对话框的时候的选中条目索引值
		//3:点击某一条目后触发的点击事件
		builder.setSingleChoiceItems(mToastStrleDes, mToast_style, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {//which选中的索引值
				//保存选中的索引值
				SpUtil.putInt(getApplicationContext(), ConstantValue.TOAST_STYLE, which);
				//关闭对话框
				dialog.dismiss();
				//显示选中色值的文字
				scv_toast_style.setDes(mToastStrleDes[which]);
			}
		});
		
		//消极按钮
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//关闭对话框
				dialog.dismiss();			
			}
		});
		//最后一定得show一下
		builder.show();		
	}

	/**
	 * 电话归属地查询
	 */
	private void initAddress() {
		final SettingItemView siv_address = (SettingItemView) findViewById(R.id.siv_address);	
		//对服务是否开启状态作显示
		boolean running = ServiceUtil.isRunning(this, "com.frank.mobilesafe.service.AddressService");
		siv_address.setCheck(running);
		
		siv_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean isCheck = siv_address.isCheck();
				siv_address.setCheck(!isCheck);
				if (!isCheck) {
					ToastUtil.show(getApplicationContext(), "开启服务", 0);
					//开启服务
					startService(new Intent(getApplicationContext(), AddressService.class));
				}else {
					ToastUtil.show(getApplicationContext(), "关闭服务", 0);
					//关闭服务
					stopService(new Intent(getApplicationContext(), AddressService.class));
				}
			}
		});
	}

	/**
	 * 版本更新开关
	 */
	private void initUpdate() {
		final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);
		// 获取已有的开关状态，用作显示
		boolean open_update = SpUtil.getBoolean(this,
				ConstantValue.OPEN_UPDATE, false);
		// 是否选中，根据上一次储存的结果而定
		siv_update.setCheck(open_update);

		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				boolean isCheck = siv_update.isCheck();
				// 如果之前是选中的，点击过后，变成未选中
				// 如果之前是未选中的，点击过后，变成选中
				siv_update.setCheck(!isCheck);
				// 将反转后的状态储存到相应的sp中
				SpUtil.putBoolean(getApplicationContext(),
						ConstantValue.OPEN_UPDATE, !isCheck);
			}
		});
	}

}
