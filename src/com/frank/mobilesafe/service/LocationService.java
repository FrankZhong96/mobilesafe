package com.frank.mobilesafe.service;

import com.frank.mobilesafe.utils.ConstantValue;
import com.frank.mobilesafe.utils.SpUtil;
import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;


public class LocationService extends Service {
	private MyLocationListener mLocationListener;
	//第一次开启服务的调用的方法
	@Override
	public void onCreate() {
		//对当前手机的所在位置进行监听(LocationListener)
		//1,位置的管理者对象
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		//2,提供一个最优方式返回经纬度
		Criteria criteria = new Criteria();
		criteria.setCostAllowed(true);//允许花费,网络定位
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//获取Gps位置的精确度
		String bestProvider = lm.getBestProvider(criteria, true);
		//3,通过最优的方式对经纬度进行一个监听,一旦变化就在回调方法中打印出来
		mLocationListener = new MyLocationListener();
		lm.requestLocationUpdates(bestProvider, 0, 0, mLocationListener);
		
		super.onCreate();
	}
	
	class MyLocationListener implements LocationListener{
		@Override
		public void onLocationChanged(Location location) {
			//当位置发生改变的时候调用方法
			//4,在获取到了经纬度后,将此地址发送给指定电话号码
			double latitude = location.getLatitude();//纬度
			double longitude = location.getLongitude();//经度
			
			SmsManager sm = SmsManager.getDefault();
			String phone = SpUtil.getString(getApplicationContext(),ConstantValue.CONTACT_PHONE, "");
			sm.sendTextMessage(phone, null,"location:"+longitude+","+latitude, null, null);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	//多次调用
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		if(mLocationListener!=null){
			mLocationListener = null;
		}
		super.onDestroy();
	}
}