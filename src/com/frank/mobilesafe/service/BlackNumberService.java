package com.frank.mobilesafe.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.frank.mobilesafe.db.dao.BlackNumberDao;

public class BlackNumberService extends Service {

	private BlackNumberDao mDao;
	private InnerSmsReceiver mInnerSmsReceiver;
	private MyContentObserver mContentObserver;
	private MyPhoneStateListener mPhoneStateListener;
	private TelephonyManager mTM;

	@Override
	public void onCreate() {
		mDao = BlackNumberDao.getInstance(getApplicationContext());	
		//拦截短信
		//短信在接受的时候,广播发送,监听广播接受者,拦截短信(有序)
		//将广播的优先级级别提高到最高 (1000)
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(1000);
		
		mInnerSmsReceiver = new InnerSmsReceiver();
		registerReceiver(mInnerSmsReceiver, intentFilter);//注册广播接收者
		
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mPhoneStateListener = new MyPhoneStateListener();
		mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		super.onCreate();
	}
	
	class MyPhoneStateListener extends PhoneStateListener{
		//3,手动重写,电话状态发生改变会触发的方法
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				//挂断电话 	aidl文件中去了
//				mTM.endCall();
				endCall(incomingNumber);
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}
	
	public void endCall(String phone) {
		int mode = mDao.getMode(phone);
		
		if(mode == 2 || mode == 3){
//			ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
			//ServiceManager此类android对开发者隐藏,所以不能去直接调用其方法,需要反射调用
			try {
				//1,获取ServiceManager字节码文件
				Class<?> clazz = Class.forName("android.os.ServiceManager");
				//2,获取方法
				Method method = clazz.getMethod("getService", String.class);
				//3,反射调用此方法
				IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
				//4,调用获取aidl文件对象方法
				ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
				//5,调用在aidl中隐藏的endCall方法
				iTelephony.endCall();
				
				//删除通话记录
				//通过内容观察者检测是否有数据库插入操作
				mContentObserver = new MyContentObserver(new Handler(),phone);
				getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, mContentObserver);
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	class MyContentObserver extends ContentObserver{

		private String phone;
		public MyContentObserver(Handler handler,String phone) {
			super(handler);
			this.phone = phone;
		}
		//数据库存在操作
		@Override
		public void onChange(boolean selfChange) {
			//检测到数据库有插入新的通话记录，删除相对应的通话记录(权限READ_CALL_LOG和WRITE_CALL_LOG)
			getContentResolver().delete(Uri.parse("content://call_log/calls"), "number = ?", new String[]{phone});
			super.onChange(selfChange);
		}
		
	}
	
	class InnerSmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//获取发送短信的电话号码，如果该号码在黑名单中，且拦截模式为	1：短信  	3：所有 	就拦截（终止广播继续发送）
			//获取短信
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			//循环遍历短信
			for (Object object : objects) {
				android.telephony.SmsMessage sms = android.telephony.SmsMessage.createFromPdu((byte[])object);
				
				String originatingAddress = sms.getOriginatingAddress();//获取发送号码
				String messageBody = sms.getMessageBody();//获取短信内容
				int mode = mDao.getMode(originatingAddress);
				if (mode==1&&mode==3) {
					//拦截短信(android 4.4版本以上失效	短信数据库,删除)
					abortBroadcast();//终止广播
				}
			}
		
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		//注销广播
		if(mInnerSmsReceiver!=null){
			unregisterReceiver(mInnerSmsReceiver);
		}
		//注销内容观察者
		if (mContentObserver != null) {
			getContentResolver().unregisterContentObserver(mContentObserver);
		}
		//取消对电话状态的监听
		if (mPhoneStateListener != null) {
			mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		super.onDestroy();
	}
}
