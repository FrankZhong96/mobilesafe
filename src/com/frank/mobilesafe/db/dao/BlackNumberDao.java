package com.frank.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.frank.mobilesafe.db.BlickNumberOpenHelper;
import com.frank.mobilesafe.db.bean.BlackNumberInfo;

public class BlackNumberDao {

	private BlickNumberOpenHelper blickNumberOpenHelper;

	//BlickNumberDao单列模式
	//1,私有化构造方法
	private BlackNumberDao(Context context){
		blickNumberOpenHelper = new BlickNumberOpenHelper(context);
	}
	//2,声明当前类的 对象
	private static BlackNumberDao blickNumberDao = null;
	//3,提供一个静态方法，如果当前类对象为空，创建一个新的
	public static BlackNumberDao getInstance(Context context){
		if (blickNumberDao == null) {
			blickNumberDao = new BlackNumberDao(context);
		}
		return blickNumberDao;
	}
	
	/**增加一条数据
	 * @param phone 电话号码
	 * @param mode 拦截模式 1：短信 2：电话 3：拦截所有（电话+短信）
	 */
	public void insert(String phone,String mode){
		SQLiteDatabase db = blickNumberOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("phone", phone);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		db.close();
	}
	
	/**删除数据库中电话号码
	 * @param phone  需要删除的电话号码
	 */
	public void delete(String phone){
		SQLiteDatabase db = blickNumberOpenHelper.getWritableDatabase();
		db.delete("blacknumber", "phone = ?", new String[]{phone});
		db.close();
	}
	
	/**根据电话号码去更新拦截模式
	 * @param phone 电话号码
	 * @param mode 拦截模式 1：短信 2：电话 3：拦截所有（电话+短信）
	 */
	public void update(String phone,String mode){
		SQLiteDatabase db = blickNumberOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		db.update("blacknumber", values, "phone = ?", new String[]{phone});
		db.close();
	}
	
	/**
	 * @return 返回所有的电话号码list集合
	 */
	public List<BlackNumberInfo> findAll(){
		SQLiteDatabase db = blickNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.query("blacknumber", new String[]{"phone","mode"}, null, null, null, null, "_id desc");
		List<BlackNumberInfo> blickNumberList = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo blickNumberInfo = new BlackNumberInfo();
			blickNumberInfo.phone = cursor.getString(0);
			blickNumberInfo.mode = cursor.getString(1);
			blickNumberList.add(blickNumberInfo);		
		}
		cursor.close();
		db.close();
		return blickNumberList;
	}
	
	/**
	 * 每次查询20条数据
	 * @param index	查询的索引值
	 */
	public List<BlackNumberInfo> find(int index){
		SQLiteDatabase db = blickNumberOpenHelper.getWritableDatabase();

		Cursor cursor = db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20;", new String[]{index+""});
		
		List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.phone = cursor.getString(0);
			blackNumberInfo.mode = cursor.getString(1);
			blackNumberList.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		
		return blackNumberList;
	}
	
	/**
	 * @return	数据库中数据的总条目个数,返回0代表没有数据或异常
	 */
	public int getCount(){
		SQLiteDatabase db = blickNumberOpenHelper.getWritableDatabase();
		int count = 0;
		Cursor cursor = db.rawQuery("select count(*) from blacknumber;", null);
		if(cursor.moveToNext()){
			count = cursor.getInt(0);
		}
		
		cursor.close();
		db.close();
		return count;
	}

	/**
	 * @param Phone 作为查询条件的号码
	 * @return 返回改号码在黑名单的拦截模式		1：短信	2：电话	3：所有	0：没有
	 */
	public int getMode(String Phone) {
		SQLiteDatabase db = blickNumberOpenHelper.getWritableDatabase();
		int mode = 0;
		Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "phone = ?", new String[]{Phone},null, null, null);
		while(cursor.moveToNext()){
			mode = cursor.getInt(0);		
		}
		cursor.close();
		db.close();
		
		return mode;
	}
}
