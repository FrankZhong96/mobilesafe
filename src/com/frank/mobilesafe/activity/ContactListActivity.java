package com.frank.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.frank.mobilesafe.R;
import com.frank.mobilesafe.bean.ContactInfo;
import com.frank.mobilesafe.utils.ContactUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ContactListActivity extends Activity {

	private ListView lv_contact;
	private List<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
	private MyAdapter mAdapter;
	// private List<ContactInfo> allContacts;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 8,填充数据适配器
			mAdapter = new MyAdapter();
			lv_contact.setAdapter(mAdapter);
		};
	};
	private List<ContactInfo> allContacts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);

		initUI();
		/*
		 * allContacts = ContactUtil.getAllContacts(this); //填充数据适配器
		 * lv_contact.setAdapter(new MyAdapter1());
		 */
		initData();

	}

	/**
	 * 获取系统联系人数据方法
	 */
	private void initData() {
		// 因为读取系统联系人,可能是一个耗时操作,放置到子线程中处理
		new Thread() {
			public void run() {
				// 1,获取内容解析器对象
				ContentResolver contentResolver = getContentResolver();
				// 2,做查询系统联系人数据库表过程(读取联系人权限)
				Cursor cursor = contentResolver.query(Uri
						.parse("content://com.android.contacts/raw_contacts"),
						new String[] { "contact_id" }, null, null, null);
				contactList.clear();
				// 3,循环游标,直到没有数据为止
				while (cursor.moveToNext()) {
					String id = cursor.getString(0);
					// Log.i(tag, "id = "+id);
					// 4,根据用户唯一性id值,查询data表和mimetype表生成的视图,获取data以及mimetype字段
					Cursor indexCursor = contentResolver.query(
							Uri.parse("content://com.android.contacts/data"),
							new String[] { "data1", "mimetype" },
							"raw_contact_id = ?", new String[] { id }, null);
					// 5,循环获取每一个联系人的电话号码以及姓名,数据类型
					HashMap<String, String> hashMap = new HashMap<String, String>();
					while (indexCursor.moveToNext()) {
						String data = indexCursor.getString(0);
						String type = indexCursor.getString(1);

						// 6,区分类型去给hashMap填充数据
						if (type.equals("vnd.android.cursor.item/phone_v2")) {
							// 数据非空判断
							 if (!TextUtils.isEmpty(data)) {
							hashMap.put("phone", data);
							 }
						} else if (type.equals("vnd.android.cursor.item/name")) {
							 if (!TextUtils.isEmpty(data)) {
							hashMap.put("name", data);
							 }
						}
					}
					indexCursor.close();
					contactList.add(hashMap);
				}
				cursor.close();
				// 7,消息机制,发送一个空的消息,告知主线程可以去使用子线程已经填充好的数据集合
				mHandler.sendEmptyMessage(0);
			};
		}.start();

		// 工具类里的读取联系人方法只能读取部分联系人，不完整，可能是*没有放到子线程里*
		/*
		 * allContacts = ContactUtil.getAllContacts(getApplicationContext());
		 * lv_contact.setAdapter(new MyAdapter());
		 */
	}

	private void initUI() {
		lv_contact = (ListView) findViewById(R.id.lv_contact);
		lv_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HashMap<String, String> item = mAdapter.getItem(position);
				String phone = item.get("phone");
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(0, intent);
				finish();
			}
		});
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return contactList.size();
		}

		@Override
		public HashMap<String, String> getItem(int position) {
			return contactList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(),
					R.layout.listview_contact_item, null);

			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);

			tv_name.setText(getItem(position).get("name"));
			tv_phone.setText(getItem(position).get("phone"));

			return view;
		}

	}

	class MyAdapter1 extends BaseAdapter {

		@Override
		public int getCount() {
			return allContacts.size();
		}

		@Override
		public Object getItem(int position) {
			return allContacts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(),
					R.layout.listview_contact_item, null);

			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
			ContactInfo contactInfo = new ContactInfo();
			tv_name.setText(contactInfo.name);
			tv_phone.setText(contactInfo.phone);

			return view;
		}

	}
}
