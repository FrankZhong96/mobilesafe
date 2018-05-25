package com.frank.mobilesafe.receive;

import com.frank.mobilesafe.dao.ProcessInfoProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KillProcessReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//杀死进程
		ProcessInfoProvider.KillAll(context);

	}

}
