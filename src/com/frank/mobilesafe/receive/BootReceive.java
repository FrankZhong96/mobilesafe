package com.frank.mobilesafe.receive;

import com.frank.mobilesafe.utils.ConstantValue;
import com.frank.mobilesafe.utils.SpUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;

public class BootReceive extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//一旦监听的开机广播，就得判断SIM卡是否切换  
		//获取sp保存的SIM卡序列号
		String spSim_Number = SpUtil.getString(context, ConstantValue.SIM_NUMBER, "");
		//获取当前SIM卡的序列号
		TelephonyManager manager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
		String simSerialNumber = manager.getSimSerialNumber();
		//对比两个SIM卡序列号		
		if (!spSim_Number.equals(simSerialNumber)) {
			//不一致，则发送短信给指定号码
			String phone = SpUtil.getString(context, ConstantValue.CONTACT_PHONE, "");
			android.telephony.SmsManager sms = android.telephony.SmsManager.getDefault();
			sms.sendTextMessage(phone, "null", "SIM Change!!", null, null);
		}
	}

}
