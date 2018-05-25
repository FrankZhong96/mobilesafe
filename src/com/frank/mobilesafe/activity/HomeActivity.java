package com.frank.mobilesafe.activity;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

import com.frank.mobilesafe.R;
import com.frank.mobilesafe.utils.ConstantValue;
import com.frank.mobilesafe.utils.Md5Util;
import com.frank.mobilesafe.utils.SpUtil;
import com.frank.mobilesafe.utils.ToastUtil;
import com.frank.mobilesafe.activity.AppManagerActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends Activity {
	private GridView gv_home;
	private int[] mDrawables;
	private String[] mTitleStrs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		// 实例化广告条
		AdView adView = new AdView(this, AdSize.FIT_SCREEN);

		// 获取要嵌入广告条的布局
		LinearLayout adLayout=(LinearLayout)findViewById(R.id.adLayout);

		// 将广告条加入到布局中
		adLayout.addView(adView);
		// 初始化UI
		initUI();
		// 初始化数据
		initData();
		
	}

	private void initData() {
		mTitleStrs = new String[] { "手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计",
				"手机杀毒", "缓存清理", "高级工具", "设置中心" };
		mDrawables = new int[] { R.drawable.home_safe,
				R.drawable.home_callmsgsafe, R.drawable.home_apps,
				R.drawable.home_taskmanager, R.drawable.home_netmanager,
				R.drawable.home_trojan, R.drawable.home_sysoptimize,
				R.drawable.home_tools, R.drawable.home_settings };
		// 设置数据适配器
		gv_home.setAdapter(new MyAdapter());
		// 设置点击事件
		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:// 手机防盗
					showDialog();
					break;
				case 1:// 黑名单
					startActivity(new Intent(getApplicationContext(), BlackNumberActivity.class));
					break;
				case 2:
					//跳转到通信卫士模块
					startActivity(new Intent(getApplicationContext(), AppManagerActivity.class));
					break;
				case 3:
					//进程管理模块
					startActivity(new Intent(getApplicationContext(), ProcessManagerActivity.class));
					break;
				case 4:
					//流量统计模块
					startActivity(new Intent(getApplicationContext(), TrafficActivity.class));
					break;
				case 5:
					//杀毒模块
					startActivity(new Intent(getApplicationContext(), AnitVirusActivity.class));
					break;
				case 6:// 缓存清理
					startActivity(new Intent(getApplicationContext(), BasecacheClearActivity.class));
					break;
				case 7:// 高级工具
					startActivity(new Intent(getApplicationContext(), AToolActivity.class));
					break;
				case 8:// 第九个，设置中心
					Intent intent = new Intent(getApplicationContext(),
							SettingActivity.class);
					startActivity(intent);
					break;
				default:
					break;
				}
			}
		});
	}

	/**
	 * 开启对话框
	 */
	private void showDialog() {
		// 判断本地是否储存密码(SP)
		String psd = SpUtil.getString(this, ConstantValue.MOBILE_SAFE_PSD, "");
		if (TextUtils.isEmpty(psd)) {
			// 初始化设置密码对话框
			showSetPsdDialog();
		} else {
			// 确认密码对话框
			showConfirmPsdDialog();
		}
	}

	/**
	 * 确认密码对话框
	 */
	private void showConfirmPsdDialog() {
		// 因为需要自己定义对话框，所以要调用dialog.setView(view);
		// view是由自己编写的xml转换成view对象 xml——》view
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		final View view = View.inflate(this, R.layout.dialog_confirm_psd, null);
		// 让对话框显示自己定义的界面
		// dialog.setView(view);
		// 为了兼容低版本安卓 设置无内边距 低版本安卓系统是默认有2个内边距
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancle = (Button) view.findViewById(R.id.bt_cancle);
		// 确认按钮
		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText et_confirm_psd = (EditText) view
						.findViewById(R.id.et_confirm_psd);
				String confirmPsd = et_confirm_psd.getText().toString();
				// 判断密码是否不为空
				if (!TextUtils.isEmpty(confirmPsd)) {
					// 读取储存的密码
					String psd = SpUtil.getString(getApplicationContext(),
							ConstantValue.MOBILE_SAFE_PSD, "");
					if (psd.equals(Md5Util.encoder(confirmPsd))) {
						// 进入手机防盗模块
						Intent intent = new Intent(getApplicationContext(),
								SetupOverActivity.class);
						startActivity(intent);
						// 跳转到新的界面得隐藏对话框
						dialog.dismiss();
					} else {
						ToastUtil.show(getApplicationContext(), "密码不一致", 0);
					}

				} else {
					// 提示用户，密码不能为空
					ToastUtil.show(getApplicationContext(), "密码不能为空", 0);
				}
			}
		});
		// 取消按钮
		bt_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();// 关闭对话框
			}
		});
	}

	/**
	 * 初始化设置密码对话框
	 */
	private void showSetPsdDialog() {
		// 因为需要自己定义对话框，所以要调用dialog.setView(view);
		// view是由自己编写的xml转换成view对象 xml——》view
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		final View view = View.inflate(this, R.layout.dialog_set_psd, null);
		// 让对话框显示自己定义的界面
		// dialog.setView(view);
		// 为了兼容低版本安卓 设置无内边距 低版本安卓系统是默认有2个内边距
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancle = (Button) view.findViewById(R.id.bt_cancle);
		// 确认按钮
		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText et_set_psd = (EditText) view
						.findViewById(R.id.et_set_psd);
				EditText et_confirm_psd = (EditText) view
						.findViewById(R.id.et_confirm_psd);
				String psd = et_set_psd.getText().toString();
				String confirmPsd = et_confirm_psd.getText().toString();
				// 判断密码是否不为空
				if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirmPsd)) {
					if (psd.equals(confirmPsd)) {
						// 进入手机防盗模块
						Intent intent = new Intent(getApplicationContext(),
								SetupOverActivity.class);
						startActivity(intent);
						// 跳转到新的界面得隐藏对话框
						dialog.dismiss();
						// 保存密码
						SpUtil.putString(getApplicationContext(),
								ConstantValue.MOBILE_SAFE_PSD,
								Md5Util.encoder(psd));
					} else {
						ToastUtil.show(getApplicationContext(), "密码不一致", 0);
					}

				} else {
					// 提示用户，密码不能为空
					ToastUtil.show(getApplicationContext(), "密码不能为空", 0);
				}
			}
		});
		// 取消按钮
		bt_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();// 关闭对话框
			}
		});
	}

	private void initUI() {
		gv_home = (GridView) findViewById(R.id.gv_home);

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// 返回条目的总数 文字组数 = 图片张数
			return mTitleStrs.length;
		}

		@Override
		public Object getItem(int position) {
			return mTitleStrs[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(),
					R.layout.gridview_item, null);
			ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
			TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
			tv_title.setText(mTitleStrs[position]);
			iv_icon.setBackgroundResource(mDrawables[position]);
			return view;
		}

	}
}
