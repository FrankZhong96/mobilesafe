package com.frank.mobilesafe.activity;

import com.frank.mobilesafe.R;
import com.frank.mobilesafe.service.LockScreenService;
import com.frank.mobilesafe.utils.ConstantValue;
import com.frank.mobilesafe.utils.ServiceUtil;
import com.frank.mobilesafe.utils.SpUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ProcessSettingActivity extends Activity{

	private CheckBox cb_show_system,cb_lock_clear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);
		
		initSystemShow();
		initLockScreenClear();
	}

	private void initLockScreenClear() {
		cb_lock_clear = (CheckBox) findViewById(R.id.cb_lock_clear);
		//根据锁屏服务是否开启作回显
		boolean isRunning = ServiceUtil.isRunning(getApplicationContext(), "com.frank.mobilesafe.service.LockScreenService");
		cb_lock_clear.setChecked(isRunning);
		if (isRunning) {
			cb_lock_clear.setText("锁屏清理已开启");
		}else {
			cb_lock_clear.setText("锁屏清理已关闭");
		}
		cb_lock_clear.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//isChecked为是否选中状态
				if (isChecked) {
					cb_lock_clear.setText("锁屏清理已开启");
					//开启服务
					startService(new Intent(getApplicationContext(), LockScreenService.class));
				}else {
					cb_lock_clear.setText("锁屏清理已关闭");
					//关闭服务
					stopService(new Intent(getApplicationContext(), LockScreenService.class));
				}
			}
		});		
	}

	private void initSystemShow() {
		cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
		//回显
		boolean show_system = SpUtil.getBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, false);
		cb_show_system.setChecked(show_system);
		
		if (show_system) {
			cb_show_system.setText("隐藏系统进程");
		}else {
			cb_show_system.setText("显示系统进程");
		}
		
		cb_show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//isChecked为是否选中状态
				if (isChecked) {
					cb_show_system.setText("隐藏系统进程");
				}else {
					cb_show_system.setText("显示系统进程");
				}
				//储存状态
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, isChecked);
			}
		});		
	}
}
