package com.frank.mobilesafe.activity;

import com.frank.mobilesafe.R;
import com.frank.mobilesafe.utils.ConstantValue;
import com.frank.mobilesafe.utils.SpUtil;
import com.frank.mobilesafe.utils.ToastUtil;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupActivity {

	private CheckBox cb_box;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);

		initUI();
	}

	private void initUI() {
		cb_box = (CheckBox) findViewById(R.id.cb_box);
		// 读取sp里的是否开启防盗为CheckBox 和 文字作回显
		boolean open_security = SpUtil.getBoolean(this,
				ConstantValue.OPEN_SECURITY, false);
		cb_box.setChecked(open_security);
		if (open_security) {
			cb_box.setText("防盗保护已开启");
		} else {
			cb_box.setText("防盗保护已关闭");
		}
		cb_box.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// isChecked代表点击后的CheckBox的状态,点击后储存状态
				SpUtil.putBoolean(getApplicationContext(),
						ConstantValue.OPEN_SECURITY, isChecked);
				// 根据开关状态显示文字
				if (isChecked) {
					cb_box.setText("防盗保护已开启");
				} else {
					cb_box.setText("防盗保护已关闭");
				}
			}
		});
	}

	@Override
	public void showNextPage() {
		boolean open_security = SpUtil.getBoolean(this,
				ConstantValue.OPEN_SECURITY, false);
		if (open_security) {
			Intent intent = new Intent(this, SetupOverActivity.class);
			startActivity(intent);
			finish();
			// 平移动画
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
			SpUtil.putBoolean(this, ConstantValue.SETUP_OVER, true);
		} else {
			ToastUtil.show(this, "请开启手机防盗保护", 0);
		}
	}

	@Override
	public void showPrePage() {
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		// 平移动画
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}
}
