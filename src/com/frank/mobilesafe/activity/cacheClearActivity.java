package com.frank.mobilesafe.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import com.frank.mobilesafe.R;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class cacheClearActivity extends Activity {
	protected static final int UPDATE_CACHE_APP = 10;
	protected static final int CHECK_CACHE_APP = 11;
	protected static final int CHECK_FINISH = 12;
	protected static final int CLEAR_CACHE = 13;
	private Button bt_clear;
	private ProgressBar pb_bar;
	private TextView tv_name;
	private LinearLayout ll_add_text;
	private PackageManager mPm;
	private int mIndex = 0;

	private Handler mHandler = new Handler(){
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_CACHE_APP:
				//在线性布局中添加有缓存应用条目
				View view = View.inflate(getApplicationContext(), R.layout.linearlayout_cache_item, null);
				
				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				TextView tv_item_name = (TextView) view.findViewById(R.id.tv_name);
				TextView tv_memory_info = (TextView)view.findViewById(R.id.tv_memory_info);
				ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				
				final CacheInfo cacheInfo = (CacheInfo) msg.obj;
				iv_icon.setBackgroundDrawable(cacheInfo.icon);
				tv_item_name.setText(cacheInfo.name);
				tv_memory_info.setText(Formatter.formatFileSize(getApplicationContext(), cacheInfo.cacheSize));
				
				ll_add_text.addView(view, 0);
				iv_delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//清除单个选中应用的缓存内容(PackageMananger)
						
						/* 以下代码如果要执行成功则需要系统应用才可以去使用的权限
						 * android.permission.DELETE_CACHE_FILES
						 * try {
							Class<?> clazz = Class.forName("android.content.pm.PackageManager");
							//2.获取调用方法对象
							Method method = clazz.getMethod("deleteApplicationCacheFiles", String.class,IPackageDataObserver.class);
							//3.获取对象调用方法
							method.invoke(mPm, cacheInfo.packagename,new IPackageDataObserver.Stub() {
								@Override
								public void onRemoveCompleted(String packageName, boolean succeeded)
										throws RemoteException {
									//删除此应用缓存后,调用的方法,子线程中
									Log.i(tag, "onRemoveCompleted.....");
								}
							});
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
						//源码开发课程(源码(handler机制,AsyncTask(异步请求,手机启动流程)源码))
						//通过查看系统日志,获取开启清理缓存activity中action和data
						Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
						intent.setData(Uri.parse("package:"+cacheInfo.packagename));
						startActivity(intent);
					}
				});
				break;
			case CHECK_CACHE_APP:
				tv_name.setText((String)msg.obj);
				break;
			case CHECK_FINISH:
				tv_name.setText("扫描完成");
				break;
			case CLEAR_CACHE:
				//从线性布局中移除所有的条目
				ll_add_text.removeAllViews();
				break;
					
			}
		};
	};
	
	@Override	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_clear);
		initUI();
		initData();
	}

	/**
	 * 遍历手机所有的应用，获取手机应用的缓存信息
	 */
	private void initData() {
		new Thread(){

			public void run() {
				mPm = getPackageManager();
				// 获取安装在手机上的所有应用
				List<PackageInfo> installedPackages = mPm.getInstalledPackages(0);
				// 给进度条设置最大值
				pb_bar.setMax(installedPackages.size());
				// 遍历所有应用，获取应用缓存应用信息（应用名称，图标，缓存大小，包名）
				for (PackageInfo packageInfo : installedPackages) {
					// 获取应用包名，包名作为获取缓存信息的重要条件
					String packageName = packageInfo.packageName;
					getPackageCache(packageName);
					try {
						Thread.sleep(50+new Random().nextInt(50));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mIndex++;
					//更新进度条
					pb_bar.setProgress(mIndex);
					//每循环一次就将检测应用的名称发送给主线程显示
					Message msg = Message.obtain();
					msg.what = CHECK_CACHE_APP;
					String name = null;
					//获取应用名称
					try {
						name = mPm.getApplicationInfo(packageName, 0).loadLabel(mPm).toString();
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					msg.obj = name;
					mHandler.sendMessage(msg);
				}
				//扫描完成
				Message msg = Message.obtain();
				msg.what = CHECK_FINISH;
				mHandler.sendMessage(msg);
			};
		}.start();	
	}

	class CacheInfo{
		public String name;
		public Drawable icon;
		public String packagename;
		public long cacheSize;
	}
	
	/**
	 * 通过包名获取包名指定应用的缓存信息
	 * @param packageName	应用包名      
	 */
	private void getPackageCache(String packageName) {

		// 创建了一个IPackageStatsObserver.Stub子类的对象,并且实现了onGetStatsCompleted方法
		IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {

			public void onGetStatsCompleted(PackageStats stats,
					boolean succeeded) {
				//获取指定包名应用缓存大小
				long cacheSize = stats.cacheSize;
				//判断缓存大小是否大于0
				if (cacheSize>0) {
					//告知主线程更新UI
					Message msg = Message.obtain();
					msg.what = UPDATE_CACHE_APP;
					CacheInfo cacheInfo = null;
					try {
						//维护有缓存应用的javabean
						cacheInfo = new CacheInfo();
						cacheInfo.cacheSize = cacheSize;
						cacheInfo.packagename = stats.packageName;
						cacheInfo.name = mPm.getApplicationInfo(stats.packageName, 0).loadLabel(mPm).toString();
						cacheInfo.icon = mPm.getApplicationInfo(stats.packageName, 0).loadIcon(mPm);
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					msg.obj = cacheInfo;
					mHandler.sendMessage(msg);
				}

			}
		};

		// 通过反射调用方法
		// 需要权限:android.permission.DELETE_CACHE_FILES.
		// 获取指定类的字节码文件
		try {
			Class<?> clazz = Class.forName("android.content.pm.PackageManager");
			//获取调用方法对象
			Method method = clazz.getMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
			//获取对象调用方法
			method.invoke(mPm, packageName,mStatsObserver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initUI() {
		bt_clear = (Button) findViewById(R.id.bt_clear);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		tv_name = (TextView) findViewById(R.id.tv_name);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
		bt_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//1.获取指定类的字节码文件
				try {
					Class<?> clazz = Class.forName("android.content.pm.PackageManager");
					//2.获取调用方法对象
					Method method = clazz.getMethod("freeStorageAndNotify", long.class,IPackageDataObserver.class);
					//3.获取对象调用方法
					method.invoke(mPm, Long.MAX_VALUE,new IPackageDataObserver.Stub() {
						@Override
						public void onRemoveCompleted(String packageName, boolean succeeded)
								throws RemoteException {
							//清除缓存完成后调用的方法(考虑权限)
							Message msg = Message.obtain();
							msg.what = CLEAR_CACHE;
							mHandler.sendMessage(msg);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
