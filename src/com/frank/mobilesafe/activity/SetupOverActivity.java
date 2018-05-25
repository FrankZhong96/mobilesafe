package com.frank.mobilesafe.activity;

import com.frank.mobilesafe.R;
import com.frank.mobilesafe.utils.ConstantValue;
import com.frank.mobilesafe.utils.SpUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SetupOverActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		boolean setup_over = SpUtil.getBoolean(this, ConstantValue.SETUP_OVER, false);
		if (setup_over) {
			//密码输入成功，并且四个导航界面设置完成-------》停留在设置完成功能列表界面
			setContentView(R.layout.activity_setup_over);
			initUI();
		}else {
			//密码输入成功，并且四个导航界面没有设置完成-------》跳转到导航界面1
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			//关闭当前界面
			finish();
		}
		
		
	}

	private void initUI() {
		TextView tv_phone = (TextView) findViewById(R.id.tv_phone);
		TextView tv_reset_setup = (TextView) findViewById(R.id.tv_reset_setup);
		//从sp里读取安全号码并显示安全号码
		String contact_phone = SpUtil.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
		tv_phone.setText(contact_phone);
		//为TextView设置点击事件
		tv_reset_setup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),Setup1Activity.class);
				startActivity(intent);
				finish();
			}
		});	
	}
}
