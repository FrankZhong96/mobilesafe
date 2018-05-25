package com.frank.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtil {

	/**
	 * 判断service是否运行
	 * @param context 上下文环境
	 * @param serviceName service名称
	 * @return true 运行    false 没有运行
	 */
	public static boolean isRunning(Context context,String serviceName){
		//获取ActivityManager管理者对象
		ActivityManager mAm = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取手机正在运行的服务集合 数量(1000)个
		List<RunningServiceInfo> runningServices = mAm.getRunningServices(1000);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			//获取每一个正在运行的服务名称
			if (serviceName.equals(runningServiceInfo.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	
}
