package com.frank.mobilesafe.activity;

import com.frank.mobilesafe.R;
import com.frank.mobilesafe.utils.ConstantValue;
import com.frank.mobilesafe.utils.SpUtil;
import com.frank.mobilesafe.utils.ToastUtil;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Setup3Activity extends BaseSetupActivity {

	private EditText et_phone_number;
	private Button bt_selsect_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);

		initUI();
	}

	private void initUI() {
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		bt_selsect_number = (Button) findViewById(R.id.bt_selsect_number);
		// 获取联系人电话号码回显过程
		String phone = SpUtil.getString(this, ConstantValue.CONTACT_PHONE, "");
		et_phone_number.setText(phone);
		bt_selsect_number.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						ContactListActivity.class);
				startActivityForResult(intent, 0);
			}
		});

	}

	/* 
	 * 等待联系人界面返回的数据结果
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data!=null){
			String phone = data.getStringExtra("phone");
			phone = phone.replace(" ", "").replace("-", "").trim();
			et_phone_number.setText(phone);			
			//将回传的数据做本地sp存储
			SpUtil.putString(this,ConstantValue.CONTACT_PHONE,phone);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}		
	
	@Override
	public void showNextPage() {
		// 点击按钮以后,需要获取输入框中的联系人,再做下一页操作
		String phone = et_phone_number.getText().toString();

		// 在sp存储了相关联系人以后才可以跳转到下一个界面
		// String contact_phone = SpUtil.getString(getApplicationContext(),
		// ConstantValue.CONTACT_PHONE, "");
		if (!TextUtils.isEmpty(phone)) {
			Intent intent = new Intent(getApplicationContext(),
					Setup4Activity.class);
			startActivity(intent);

			finish();
			// 平移动画
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
			// 如果现在是输入电话号码,则需要去保存
			SpUtil.putString(getApplicationContext(),
					ConstantValue.CONTACT_PHONE, phone);
		} else {
			ToastUtil.show(this, "请输入电话号码", 0);
		}

	}

	@Override
	public void showPrePage() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		// 平移动画
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);

	}
}
