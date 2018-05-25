package com.frank.mobilesafe.dao;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;

public class ProcessInfo {

	private String Name;//应用名称
	private Drawable icon;//应用图标
	private Long memSize;//应用已占用的内存数
	private boolean isCheck;//应用是否被选中
	private boolean isSystem;//是否为系统应用
	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public Long getMemSize() {
		return memSize;
	}
	public void setMemSize(Long memSize) {
		this.memSize = memSize;
	}
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	public boolean isSystem() {
		return isSystem;
	}
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String packageName;//如果进程没有名称则将该应用的包名为名称
}
