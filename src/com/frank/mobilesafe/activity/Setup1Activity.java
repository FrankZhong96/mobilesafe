package com.frank.mobilesafe.activity;

import com.frank.mobilesafe.R;
import android.content.Intent;
import android.os.Bundle;

public class Setup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}

	@Override
	public void showNextPage() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		// 平移动画
		overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
	}

	@Override
	public void showPrePage() {

	}
}
