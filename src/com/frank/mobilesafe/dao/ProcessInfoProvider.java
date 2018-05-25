package com.frank.mobilesafe.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.frank.mobilesafe.R;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Contacts.People;

public class ProcessInfoProvider{

	/**
	 * 获取进程总数
	 * @param context 上下文环境
	 * @return 进程总数
	 */
	public static int getProcessCount(Context context) {
		//获取ActivityManager
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取正在运行进程的集合
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		//返回进程总数
		return runningAppProcesses.size();
	}
	
	/**
	 * 获取可用内存值
	 * @param context 上下文环境
	 * @return 可用内存值   bytes
	 */
	public static long getAvailSpace(Context context) {
		//获取ActivityManager
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//构建储存可用内存的对象
		MemoryInfo memoryInfo = new MemoryInfo();
		//给MemoryInfo对象赋（可用内存）值
		am.getMemoryInfo(memoryInfo);
		//获取memoryInfo中可用内存的值
		return memoryInfo.availMem;
	}
	/**
	 * @param ctx	
	 * @return 返回总的内存数	单位为bytes 返回0说明异常
	 */
	public static long getTotalSpace(Context ctx){
		/*//1,获取activityManager
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2,构建存储可用内存的对象
		MemoryInfo memoryInfo = new MemoryInfo();
		//3,给memoryInfo对象赋(可用内存)值
		am.getMemoryInfo(memoryInfo);
		//4,获取memoryInfo中相应可用内存大小
		return memoryInfo.totalMem;*/
		
		//内存大小写入文件中,读取proc/meminfo文件,读取第一行,获取数字字符,转换成bytes返回
		FileReader fileReader  = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader= new FileReader("proc/meminfo");
			bufferedReader = new BufferedReader(fileReader);
			String lineOne = bufferedReader.readLine();
			//将字符串转换成字符的数组
			char[] charArray = lineOne.toCharArray();
			//循环遍历每一个字符,如果此字符的ASCII码在0到9的区域内,说明此字符有效
			StringBuffer stringBuffer = new StringBuffer();
			for (char c : charArray) {
				if(c>='0' && c<='9'){
					stringBuffer.append(c);
				}
			}
			return Long.parseLong(stringBuffer.toString())*1024;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(fileReader!=null && bufferedReader!=null){
					fileReader.close();
					bufferedReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}  
	
	/**
	 * @param context 上下文环境
	 * @return 当前正在运行应用相关信息
	 */
	public static List<ProcessInfo> getProcessInfo(Context context) {
		
		List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
		//获取ActivityManager管理者对象
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		//获取正在运行的应用集合
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		//循环遍历集合，获取相关正在运行应用的数据（名称，包名，图标，占用内存，是否为系统应用进程（状态机））
		for (RunningAppProcessInfo info : runningAppProcesses) {
			ProcessInfo processInfo = new ProcessInfo();
			//获取进程名称 （进程名称==包名）
			processInfo.setPackageName(info.processName);
			//获取进程占用 的内存大小(传递一个应用进程相对应的pid的数组)
			android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
			//返回数组中索引位置为0的对象，为当前应用进程内存信息的对象
			android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
			//获取已使用内存大小
			processInfo.setMemSize((long) (memoryInfo.getTotalPrivateDirty()*1024));
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.getPackageName(), 0);
				//获取应用名称
				processInfo.setName(applicationInfo.loadLabel(pm).toString());
				//获取应用图标
				processInfo.setIcon(applicationInfo.loadIcon(pm));
				//是否为系统进程  状态机
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
					processInfo.setSystem(true);
				}else {
					processInfo.setSystem(false);
				}
			} catch (NameNotFoundException e) {
				//应用程序找不到，包名对应的对象找不到
				//名称用包名，图标用安卓默认图标，一般为系统应用
				processInfo.setName(processInfo.getPackageName());
				processInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
				processInfo.setSystem(true);
				e.printStackTrace();
			}			
			processInfoList.add(processInfo);
		}
		return processInfoList;
	}
	
	/**
	 * 杀进程方法
	 * @param ctx	上下文环境
	 * @param processInfo	杀死进程所在的javabean的对象
	 */
	public static void killProcess(Context ctx,ProcessInfo processInfo) {
		//1,获取activityManager
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2,杀死指定包名进程(权限)
		am.killBackgroundProcesses(processInfo.packageName);
	}

	
	/**
	 * 清理所有进程
	 * @param context 上下文环境
	 */
	public static void KillAll(Context context) {
		//获取ActivityManager
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取正在运行进程的集合
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		//循环遍历所有进程并杀死
		for (RunningAppProcessInfo info : runningAppProcesses) {
			//除当前应用不要清理
			if (info.processName.equals(context.getPackageName())) {
				continue;
			}
			//杀死指定包名进程(权限)
			am.killBackgroundProcesses(info.processName);
		}
	}
	
}
