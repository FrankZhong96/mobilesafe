package com.frank.mobilesafe.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AddressDao {
	// 指定访问数据库的路径
	public static String path = "data/data/com.frank.mobilesafe/files/address.db";
	private static String tag = "AddressDao";
	private static String mAddress = "未知号码";

	
	public static String getAddress(String phone) {
		mAddress = "未知号码";
		// 开启数据库连接
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		// 正则表达式 表示号码
		String regex = "^1[3-8]\\d{9}";
		if (phone.matches(regex)) {
			phone = phone.substring(0, 7);
			// 数据库查询
			Cursor cursor = db.query("data1", new String[] { "outkey" },
					"id = ?", new String[] { phone }, null, null, null);
			if (cursor.moveToNext()) {
				String outkey = cursor.getString(0);
				Log.i(tag, "outkey:" + outkey);
				Cursor indexCursor = db.query("data2",
						new String[] { "location" }, "id = ?",
						new String[] { outkey }, null, null, null);
				if (indexCursor.moveToNext()) {
					mAddress = indexCursor.getString(0);
					Log.i(tag, "address:" + mAddress);
				}				
			}else {
				mAddress = "未知号码";
			}
		} else {
			int length = phone.length();
			switch (length) {
			case 3:// 110 120 119 114
				mAddress = "报警电话";
				break;
			case 4:// 5556
				mAddress = "模拟器";
				break;
			case 5:// 10086 10010 99555
				mAddress = "服务电话";
				break;
			case 8://
				mAddress = "本地电话";
				break;
			case 11://（3+8）区号+座机号码（外地）
				String area = phone.substring(1, 3);
				Cursor cursor = db.query("data2", new String[]{"location"}, "area = ?", new String[]{area}, null, null, null);
				if (cursor.moveToNext()) {
					mAddress = cursor.getString(0);
				}else {
					mAddress = "未知电话";
				}
				break;
			case 12://（4+8）区号（0797（江西赣州））+座机号码（外地）
				String area1 = phone.substring(1, 4);
				Cursor cursor1 = db.query("data2", new String[]{"location"}, "area = ?", new String[]{area1}, null, null, null);
				if (cursor1.moveToNext()) {
					mAddress = cursor1.getString(0);
				}else {
					mAddress = "未知电话";
				}
				break;
			}
		}
		return mAddress;
	}

}
