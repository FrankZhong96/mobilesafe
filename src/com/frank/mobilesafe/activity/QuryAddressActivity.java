package com.frank.mobilesafe.activity;

import com.frank.mobilesafe.R;
import com.frank.mobilesafe.dao.AddressDao;

import android.app.Activity;
import android.app.DownloadManager.Query;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QuryAddressActivity extends Activity {

	private EditText et_input_phone;
	private Button bt_qury_address;
	private TextView tv_address;
	private String mAddress;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			tv_address.setText(mAddress);
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quryaddress);
		initUI();
	}

	private void initUI() {
		et_input_phone = (EditText) findViewById(R.id.et_input_phone);
		bt_qury_address = (Button) findViewById(R.id.bt_qury_address);
		tv_address = (TextView) findViewById(R.id.tv_address);

		bt_qury_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String phone = et_input_phone.getText().toString();
				if (!TextUtils.isEmpty(phone)) {
					// 查询电话归属地 耗时操作开子线程
					query(phone);
				}else {
					//抖动效果
					Animation shake = AnimationUtils.loadAnimation(
							getApplicationContext(), R.anim.shake);
					//interpolator插补器,数学函数
					//自定义插补器
//					shake.setInterpolator(new Interpolator() {
//						//y = 2x+1
//						@Override
//						public float getInterpolation(float arg0) {
//							return 0;
//						}
//					});
					
//					Interpolator
//					CycleInterpolator
					et_input_phone.startAnimation(shake);
					//手机振动效果
					Vibrator vibrator =  (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(500);//振动500ms
					//规律振动 (pattern:振动规则(不振动的时间,振动时间,不振动的时间,振动时间,......) repeat:重复次数)
					//vibrator.vibrate(new long[]{2000,5000,2000,5000}, -1);
				}	
			}
		});
		
		//实时查询  （监听输入框文本变化）
		et_input_phone.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// 当文本发生改变的时候
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// 当文本发生改变之前时候
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// 当文本发生改变之后时候
				String phone = et_input_phone.getText().toString();
				query(phone);
				
			}
		});;
	}

	/**
	 * 查询电话归属地 耗时操作开子线程
	 * 
	 * @param phone
	 *            查询的电话号码
	 */
	public void query(final String phone) {
		new Thread() {
			public void run() {
				mAddress = AddressDao.getAddress(phone);
				mHandler.sendEmptyMessage(0);//发送空消息，告知完成查询操作
			};
		}.start();
	}
}
