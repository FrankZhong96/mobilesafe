package com.frank.mobilesafe.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class VirusDao {
	// 指定访问数据库的路径
	public static String path = "data/data/com.frank.mobilesafe/files/antivirus.db";
	
	/**
	 * 获取手机病毒数据库中应用md5值
	 * @return 手机病毒数据库中应用md5值的集合
	 */
	public static List<String> getVirusList() {
		//开启数据库，查询表中md5值
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null);
		List<String> virusList = new ArrayList<String>();
		while(cursor.moveToNext()){
			virusList.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return virusList;
	}
	
}
