package com.frank.mobilesafe.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.youmi.android.AdManager;

import org.json.JSONException;
import org.json.JSONObject;
import com.frank.mobilesafe.R;
import com.frank.mobilesafe.utils.ConstantValue;
import com.frank.mobilesafe.utils.SpUtil;
import com.frank.mobilesafe.utils.StreamUtil;
import com.frank.mobilesafe.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashActivity extends Activity {

	protected static final String tag = "SplashActivity";

	/**
	 * 应用更新状态码
	 */
	protected static final int UPDATE_VERSION = 100;

	/**
	 * 进入主界面状态码
	 */
	protected static final int ENTER_HOME = 101;
	/**
	 * URL请求出错状态码
	 */
	protected static final int URL_ERROR = 102;

	/**
	 * IO出错状态码
	 */
	protected static final int IO_ERROR = 103;

	/**
	 * JSON出错状态码
	 */
	protected static final int JSON_ERROR = 104;
	private TextView tv_version;
	private String mDownloadUrl;
	private String mVersionDes;
	private int mLocalVersionCode;
	private Handler mHandle = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_VERSION:
				// 跳出更新提示框
				showUpdateDialog();
				break;
			case ENTER_HOME:
				// 进入主界面
				enterHome();
				break;
			case URL_ERROR:
				// URL请求出错
				ToastUtil.show(SplashActivity.this, "URL请求出错", 0);
				enterHome();// 不过怎么出错都要进入主界面
				break;
			case IO_ERROR:
				// IO出错
				ToastUtil.show(getApplicationContext(), "IO操作出错", 0);
				enterHome();
				break;
			case JSON_ERROR:
				// JSON出错
				ToastUtil.show(getApplicationContext(), "JSON请求出错", 0);
				enterHome();
				break;

			default:
				break;
			}
		};
	};

	private RelativeLayout rl_root;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除掉当前activity头title
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		AdManager.getInstance(this).init("c2b048e66e21a939", "8c10a94059fd6fa8", false);
		// 初始化UI
		initUI();
		// 初始化数据
		initData();
		// 初始化动画
		initAnimation();
		//初始化数据库
		initDB();
		if(!SpUtil.getBoolean(this, ConstantValue.HAS_SHORTCUT, false)){
        	//生成快捷方式
            initShortCut();
        }
	}

	/**
	 * 生成快捷方式
	 */
	private void initShortCut() {
		//1,给intent维护图标,名称
		Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		//维护图标
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, 
				BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		//名称
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士");
		//2,点击快捷方式后跳转到的activity
		//2.1维护开启的意图对象
		Intent shortCutIntent = new Intent("android.intent.action.HOME");
		shortCutIntent.addCategory("android.intent.category.DEFAULT");
		
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
		//3,发送广播
		sendBroadcast(intent);
		//4,告知sp已经生成快捷方式
		SpUtil.putBoolean(this, ConstantValue.HAS_SHORTCUT, true);
	}
	
	private void initDB() {
		//归属地数据库拷贝过程    目录：date/date/工程包名/files/address.db
		initCopyDb("address.db");
		//常用号码数据库拷贝
		initCopyDb("commonnum.db");
		//手机病毒数据库拷贝
		initCopyDb("antivirus.db");
	}

	/**
	 * 初始化数据库，将assets目录下的数据库拷贝到date/date/工程包名/files目录下
	 * @param dbName 数据库名称
	 */
	private void initCopyDb(String dbName) {
		
		FileOutputStream fos = null;
		InputStream stream = null;
		//获应用程序文件的目录的路径
		File filesDir = getFilesDir();
		//在filesDir文件夹下创建dbName文件
		File file = new File(filesDir, dbName);
		//判断是否存在file  存在就不用拷贝了直接返回
		if (file.exists()) {
			return;
		}
		//输入流读取第三方资产目录的文件
		try {
			stream = getAssets().open(dbName);
			//读取内容写入到指定文件中
			fos = new FileOutputStream(file);
			//每次读取内容的大小
			byte[] bytes = new byte[1024];
			int temp = -1;
			while ((temp = stream.read(bytes))!= -1) {
				fos.write(bytes,0,temp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (stream != null&&fos!=null) {
				try {
					stream.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		}
	}

	// 初始化动画
	private void initAnimation() {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(1000);// 设置动画执行时间
		// 最后得启动动画
		rl_root.startAnimation(alphaAnimation);
	}

	// 跳出更新对话框
	protected void showUpdateDialog() {
		// 对话框,是依赖于activity存在的
		Builder builder = new AlertDialog.Builder(this);
		// 设置左上角图标
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("版本更新");
		// 设置描述内容
		builder.setMessage(mVersionDes);

		// 积极按钮,立即更新
		builder.setPositiveButton("立即更新",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 下载apk,apk链接地址,downloadUrl
						downloadApk();
					}
				});

		builder.setNegativeButton("稍后再说",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 取消对话框,进入主界面
						enterHome();
					}
				});

		// 点击取消事件监听
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// 即使用户点击取消,也需要让其进入应用程序主界面
				enterHome();
				dialog.dismiss();
			}
		});
		// 最后得show一下
		builder.show();
	}

	protected void downloadApk() {
		// 下载前先判断sd卡状态 是否挂载上
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// 获取sd卡路径
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + File.separator + "mobilesaafe.apk";
			HttpUtils httpUtils = new HttpUtils();
			httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {

				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					// 下载成功
					Log.i(tag, "下载成功");
					File result = arg0.result;
					// 提示用户安装应用
					installApk(result);
				}

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// 下载失败
					Log.i(tag, "下载失败");
				}

				@Override
				public void onStart() {
					// 刚刚开始下载
					Log.i(tag, "开始下载");
					super.onStart();
				}

				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					// 下载中
					Log.i(tag, "总大小：" + total);
					Log.i(tag, "已下载：" + current);
					super.onLoading(total, current, isUploading);
				}
			});
		}

	}

	// 安装应用
	protected void installApk(File file) {
		// 系统应用界面,源码,安装apk入口
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		/*
		 * //文件作为数据源 intent.setData(Uri.fromFile(file)); //设置安装的类型
		 * intent.setType("application/vnd.android.package-archive");
		 */
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		// startActivity(intent);
		startActivityForResult(intent, 0);

	}

	// 开启一个activity后,返回结果调用的方法enterHome();//即使用户下载好apk后点击不更新也得确保用户进入应用程序主界面
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 即使用户下载好apk后点击不更新也得确保用户进入应用程序主界面
		enterHome();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 进入应用程序主界面
	 */
	protected void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		// 启动进入主界面前销毁当前activity
		finish();
	}

	private void initData() {
		// 获取应用版本名称
		tv_version.setText(getVersionName());
		mLocalVersionCode = getVersionCode();
		// 通过用户选择的是否开启更新来确定是否更新
		if (SpUtil.getBoolean(getApplicationContext(),
				ConstantValue.OPEN_UPDATE, false)) {
			// 判断是否有新版本
			checkVersionCode();
		} else {
			// 直接进入主界面 主线程最好不用sleep 7s就程序就死了
			// 在发送消息后的1s后执行ENTER_HOME状态码指向的消息
			mHandle.sendEmptyMessageDelayed(ENTER_HOME, 1000);
		}

	}

	/**
	 * 和云端版本号做对比 判断是否有新版本
	 */
	private void checkVersionCode() {
		new Thread() {
			public void run() {
				Message msg = Message.obtain();
				// 获取当前时间
				long startTime = System.currentTimeMillis();
				try {
					URL url = new URL("http://192.168.1.109/api/update.json");
					// 开启链接
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					// 设置请求超时时间
					connection.setConnectTimeout(2000);
					// 设置请求方式 默认是GET方式
					connection.setRequestMethod("GET");
					// 设置读取超时时间
					connection.setReadTimeout(2000);
					// 获取响应码并做出判断
					int code = connection.getResponseCode();
					if (code == 200) {
						InputStream in = connection.getInputStream();
						String json = StreamUtil.streamToString(in);
						Log.i(tag, json);
						// 解析json数据
						JSONObject jsonObject = new JSONObject(json);
						mDownloadUrl = jsonObject.getString("downloadUrl");
						String versionCode = jsonObject
								.getString("versionCode");
						mVersionDes = jsonObject.getString("versionDes");
						// String versionName =
						// jsonObject.getString("versionName");
						// 云端服务器版本号大于本地版本 提示用户更新
						if (Integer.parseInt(versionCode) > mLocalVersionCode) {
							// 提示用户更新，跳转到更新对话框
							msg.what = UPDATE_VERSION;
						} else {
							// 进入主界面
							msg.what = ENTER_HOME;
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					msg.what = URL_ERROR;
				} catch (IOException e) {
					msg.what = IO_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what = JSON_ERROR;
					e.printStackTrace();
				} finally {
					// 网络请求时间超过1s则不做处理 小于1s强制睡眠满1s
					long endTime = System.currentTimeMillis();
					if ((endTime - startTime) < 1000) {
						try {
							Thread.sleep(1000 - (endTime - startTime));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					mHandle.sendMessage(msg);
				}
			};
		}.start();

	}

	/**
	 * 获取应用版本号
	 * 
	 * @return 返回版本号 0 异常
	 */
	private int getVersionCode() {
		// 包管理者对象PackageManager
		PackageManager pm = getPackageManager();
		try {// 从包的管理者对象中，获取指定包的基本信息（版本号，版本名称）0代表获取基本信息
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			// 获取版本号
			return packageInfo.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取应用版本名称 返回null代表异常
	 */
	private String getVersionName() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			// 获取版本名称
			return packageInfo.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void initUI() {
		tv_version = (TextView) findViewById(R.id.tv_version);
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
	}
}
