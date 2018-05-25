package com.frank.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	/**
	 * 打印吐司
	 * @param context  上下位环境
	 * @param text     打印文本内容
	 * @param duration 时长  0短时间  1长时间
	 */
	public static void show(Context context,String text,int duration) {
		Toast.makeText(context, text, duration).show();
	}
}
