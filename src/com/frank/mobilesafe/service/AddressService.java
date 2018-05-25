package com.frank.mobilesafe.service;

import com.frank.mobilesafe.R;
import com.frank.mobilesafe.dao.AddressDao;
import com.frank.mobilesafe.utils.ConstantValue;
import com.frank.mobilesafe.utils.SpUtil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class AddressService extends Service {

	private TelephonyManager tmManager;
	private MyPhoneStateListener mPhoneStateListener;
	public String tag = "AddressService";
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private WindowManager mWm;
	private View toastView;
	private TextView tv_toast;
	private String mAddress;
	private int mScreenHeight;
	private int mScreenWidth;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			tv_toast.setText(mAddress);
		};
	};
	private InterOutCallReceiver mInterOutCallReceiver;	
	
	@Override
	public void onCreate() {

		tmManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mWm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		//获取屏幕的宽度和高度
		mScreenHeight = mWm.getDefaultDisplay().getHeight();
		mScreenWidth = mWm.getDefaultDisplay().getWidth();
		mPhoneStateListener = new MyPhoneStateListener();
		tmManager.listen(mPhoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);
		//监听拨打电话的广播过滤条件（权限）
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		mInterOutCallReceiver = new InterOutCallReceiver();
		//注册
		registerReceiver(mInterOutCallReceiver, intentFilter);
		
		super.onCreate();
	}
	
	class InterOutCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//接收到此广播后，需要显示自定义吐司，显示播出归属地的号码
			//获取播出电话号码的字符串
			String phone = getResultData();
			MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener();
			myPhoneStateListener.showToast(phone);
			
		}
		
	}

	class MyPhoneStateListener extends PhoneStateListener {
		private int[] mDrawableIds;

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:// 空闲状态
				// 空闲状态:没有活动,挂断电话（移除吐司）
				Log.i(tag, "空闲状态");
				if (mWm != null && toastView != null) {
					mWm.removeView(toastView);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 摘机状态
				// 摘机状态:至少有一个电话，该活动或是拨打或是通话
				Log.i(tag, "摘机状态");
				break;
			case TelephonyManager.CALL_STATE_RINGING:// 响铃状态
				// 响铃状态（展示吐司）
				Log.i(tag, "响铃状态");
				showToast(incomingNumber);
				break;

			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}

		public void showToast(String incomingNumber) {
			final WindowManager.LayoutParams params = mParams;
			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
			params.width = WindowManager.LayoutParams.WRAP_CONTENT;
			params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE 默认能够被触摸
					| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

			params.format = PixelFormat.TRANSLUCENT;
			// 在响铃到时候显示吐司，和电话类型一样
			params.type = WindowManager.LayoutParams.TYPE_PHONE;
			params.setTitle("Toast");
			// 指定吐司位置 (指定在左上角)
			params.gravity = Gravity.LEFT + Gravity.TOP;
			toastView = View.inflate(getApplicationContext(),
					R.layout.toast_view, null);
			tv_toast = (TextView) toastView.findViewById(R.id.tv_toast);
			
			//设置自定义吐司的拖拽事件
			toastView.setOnTouchListener(new OnTouchListener() {
				
				

				private int startX;
				private int startY;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:// 按下
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						break;
					case MotionEvent.ACTION_MOVE:// 移动
						int moveX = (int) event.getRawX();
						int moveY = (int) event.getRawY();

						int disX = moveX - startX;
						int disY = moveY - startY;

						params.x = params.x + disX;
						params.y = params.y + disY;
						//告知窗体吐司需要按照手势的移动去做位置更新
						mWm.updateViewLayout(toastView, params);
						
						// 容错处理(iv_drag不能拖拽出手机屏幕)
						if (params.x<0) {
							params.x = 0;
						}
						if (params.y<0) {
							params.y = 0;
						}
						if (params.x>mScreenWidth-toastView.getWidth()) {
							params.x = mScreenWidth-toastView.getWidth();
						}
						//减22是减去通知栏的高度
						if (params.y>mScreenHeight-toastView.getHeight()-22) {
							params.y = mScreenHeight-toastView.getHeight()-22;
						}
						
						// 重置一次坐标
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						break;
					case MotionEvent.ACTION_UP:// 抬起
						// 储存控件位置
						SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, params.x);
						SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, params.y);
						break;
					}
					// 在当前的情况下返回false不响应事件，返回true才会响应事件
					// 既要响应点击事件，又要响应拖拽事件，则返回结果需要修改为false
					return true;
				}
			});
			
			//从sp中获取存储吐司的位置（x，y）坐标
			//params.x为吐司的x坐标
			params.x = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
			//params.y为吐司的y坐标
			params.y = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);
			
			//从sp中获取颜色文字的索引值，匹配图片，用作显示
			mDrawableIds = new int[]{R.drawable.call_locate_white,
					R.drawable.call_locate_orange,
					R.drawable.call_locate_blue,
					R.drawable.call_locate_gray,
					R.drawable.call_locate_green};
			int toast_style = SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
			tv_toast.setBackgroundResource(mDrawableIds[toast_style]);
			
			// 将自定义吐司显示toastView(需加权限SYSTEM_ALERT_WINDOW)
			mWm.addView(toastView, params);
			// 查询归属地数据库
			query(incomingNumber);
		}
	}

	@Override
	public void onDestroy() {
		// 取消电话状态监听(开启服务的时候监听电话对象)
		if (tmManager != null && mPhoneStateListener != null) {
			tmManager.listen(mPhoneStateListener,
					PhoneStateListener.LISTEN_NONE);
		}
		if (mInterOutCallReceiver!=null) {
			//去电广播接收者的注销
			unregisterReceiver(mInterOutCallReceiver);
		}
		super.onDestroy();
	}
	public void query(final String incomingNumber) {
		new Thread() {
			public void run() {
				mAddress = AddressDao.getAddress(incomingNumber);
				mHandler.sendEmptyMessage(0);//告知查询完成,让其去更新UI
			};
		}.start();

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
