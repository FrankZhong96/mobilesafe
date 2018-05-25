package com.frank.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends Activity {

	private GestureDetector gestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 3,创建手势管理的对象,用作管理在onTouchEvent(event)传递过来的手势动作
		gestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					// 4,重写手势识别器，包含按下点和抬起点在移动过程中的方法
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						// e1 起始点 e2 抬起点
						if (e1.getX() - e2.getX() > 0) {
							// 下一页，由右向左滑
							showNextPage();
						}
						if (e1.getX() - e2.getX() < 0) {
							// 上一页，由左向右滑
							showPrePage();
						}
						return super.onFling(e1, e2, velocityX, velocityY);
					}
				});
	}

	// 监听屏幕上响应的事件类型(按下(1次),移动(多次),抬起(1次))
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 2,通过手势识别器去识别不同事件的类型
		gestureDetector.onTouchEvent(event);// 将activity中的手机移动操作，交由手势识别器处理
		return super.onTouchEvent(event);
	}

	// 下一页的抽象方法,由子类决定具体跳转到那个界面
	public abstract void showNextPage();

	// 下一页的抽象方法,由子类决定具体跳转到那个界面
	public abstract void showPrePage();

	// 点击下一页按钮的时候,根据子类的showNextPage方法做相应跳转
	public void nextPage(View view) {
		showNextPage();
	}

	// 点击上一页按钮的时候,根据子类的showPrePage方法做相应跳转
	public void backPage(View v) {
		showPrePage();
	}

}
