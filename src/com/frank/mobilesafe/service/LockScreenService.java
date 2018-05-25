package com.frank.mobilesafe.service;

import com.frank.mobilesafe.dao.ProcessInfoProvider;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class LockScreenService extends Service {

	private IntentFilter intentFilter;
	private InnerReceiver innerReceiver;

	@Override
	public void onCreate() {
		intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver, intentFilter);
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		if (innerReceiver!=null) {
			unregisterReceiver(innerReceiver);
		}
		super.onDestroy();
	}
	
	class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//清理所有进程
			ProcessInfoProvider.KillAll(context);		
		}
	}

}
