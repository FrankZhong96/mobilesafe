package com.frank.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.frank.mobilesafe.db.AppLockOpenHelper;
import com.frank.mobilesafe.db.BlickNumberOpenHelper;
import com.frank.mobilesafe.db.bean.BlackNumberInfo;

public class AppLockDao {

	private AppLockOpenHelper appLockOpenHelper;

	//BlickNumberDao单列模式
	//1,私有化构造方法
	private AppLockDao(Context context){
		appLockOpenHelper = new AppLockOpenHelper(context);
	}
	//2,声明当前类的 对象
	private static AppLockDao appLockDao = null;
	//3,提供一个静态方法，如果当前类对象为空，创建一个新的
	public static AppLockDao getInstance(Context context){
		if (appLockDao == null) {
			appLockDao = new AppLockDao(context);
		}
		return appLockDao;
	}
	
	/**增加一条数据
	 * @param packagename 应用包名
	 */
	public void insert(String packagename){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packagename",packagename);
		db.insert("applock", null, values);
		db.close();
	}
	
	/**删除数据库中应用
	 * @param packagename 应用包名
	 */
	public void delete(String packagename){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		db.delete("applock", "packagename = ?", new String[]{packagename});
		db.close();
	}
	
	/**
	 * @return 返回所有的应用list集合
	 */
	public List<String> findAll(){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		Cursor cursor = db.query("applock", new String[]{"packagename"}, null, null, null, null, null, null);
		List<String> lockpackageList = new ArrayList<String>();
		while(cursor.moveToNext()){
			lockpackageList.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return lockpackageList;
	}
}
