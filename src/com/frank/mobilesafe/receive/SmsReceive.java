package com.frank.mobilesafe.receive;

import com.frank.mobilesafe.service.LocationService;
import com.frank.mobilesafe.utils.ConstantValue;
import com.frank.mobilesafe.utils.SpUtil;
import com.frank.mobilesafe.utils.ToastUtil;
import com.frank.mobilesafe.R;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

public class SmsReceive extends BroadcastReceiver {

	private ComponentName mDeviceAdminSample;
	private DevicePolicyManager mDPM;
	@Override
	public void onReceive(Context context, Intent intent) {
		//判断是否开启了防盗保护
		boolean open_security = SpUtil.getBoolean(context, ConstantValue.OPEN_SECURITY, false);
		if (open_security) {
			//获取短信
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			//循环遍历短信
			for (Object object : objects) {
				android.telephony.SmsMessage sms = android.telephony.SmsMessage.createFromPdu((byte[])object);
				@SuppressWarnings("unused")
				String originatingAddress = sms.getOriginatingAddress();//获取发送号码
				String messageBody = sms.getMessageBody();//获取短信内容
				ToastUtil.show(context, "读取短信成功", 0);
				//判断是否包含播放音乐的指令
				if (messageBody.contains("#*alarm*#")) {
					//播放音乐
					MediaPlayer mediaPlayer = MediaPlayer.create(context,R.raw.ylzs);
					mediaPlayer.setLooping(true);//无限循环播放
					ToastUtil.show(context, "无限循环播放", 0);
					mediaPlayer.start();
				}
				
				//判断是否包含获取位置的指令
				if (messageBody.contains("#*location*#")) {
					//开启获取位置的服务
					context.startService(new Intent(context,LocationService.class));
				}
				mDeviceAdminSample = new ComponentName(context, AdminReceiver.class);
				mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				//mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
				//判断是否包含清除数据的指令
				if (messageBody.contains("#*wipedata*#")) {
					if(mDPM.isAdminActive(mDeviceAdminSample)){
						mDPM.wipeData(0);
//						mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
					}else{
						ToastUtil.show(context, "请先激活", 0);					
					}
				}
				//判断是否包含锁屏的指令
				if (messageBody.contains("#*lockscreen*#")) {
					if(mDPM.isAdminActive(mDeviceAdminSample)){
						mDPM.lockNow();
						mDPM.resetPassword("123", 0);
					}else{
						ToastUtil.show(context, "请先激活", 0);					
					}
				}
			}
		}
	}

}
