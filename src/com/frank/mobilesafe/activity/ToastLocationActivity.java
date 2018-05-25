package com.frank.mobilesafe.activity;

import com.frank.mobilesafe.R;
import com.frank.mobilesafe.utils.ConstantValue;
import com.frank.mobilesafe.utils.SpUtil;
import com.frank.mobilesafe.utils.ToastUtil;
import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class ToastLocationActivity extends Activity {

	private Button bt_top;
	private Button bt_bottom;
	private ImageView iv_drag;
	private int startX;
	private int startY;
	private WindowManager mWM;
	private int mScreenHeight;
	private int mScreenWidth;
	private long startTime = 0;
	private long[] mHits = new long[2];// 数组的长度决定多少击

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toast_location);

		initUI();
	}

	private void initUI() {
		iv_drag = (ImageView) findViewById(R.id.iv_drag);
		bt_top = (Button) findViewById(R.id.bt_top);
		bt_bottom = (Button) findViewById(R.id.bt_bottom);

		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
		mScreenHeight = mWM.getDefaultDisplay().getHeight();
		mScreenWidth = mWM.getDefaultDisplay().getWidth();

		int locationX = SpUtil.getInt(getApplicationContext(),
				ConstantValue.LOCATION_X, 0);
		int locationY = SpUtil.getInt(getApplicationContext(),
				ConstantValue.LOCATION_Y, 0);
		// 左上角坐标作用在iv_drag上
		// iv_drag在相对布局中,所以其所在位置的规则需要由相对布局提供

		// 指定宽高都为WRAP_CONTENT
		LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		// 将左上角的坐标作用在iv_drag对应规则参数上
		layoutParams.leftMargin = locationX;
		layoutParams.topMargin = locationY;

		if (layoutParams.topMargin > mScreenHeight / 2) {
			// 底部的描述隐藏
			bt_bottom.setVisibility(View.INVISIBLE);
			// 顶部的描述显示
			bt_top.setVisibility(View.VISIBLE);
		} else {
			// 底部的描述显示
			bt_bottom.setVisibility(View.VISIBLE);
			// 顶部的描述隐藏
			bt_top.setVisibility(View.INVISIBLE);
		}
		// 将以上规则作用在iv_drag上
		iv_drag.setLayoutParams(layoutParams);

		iv_drag.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);

				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (500 >= (SystemClock.uptimeMillis() - mHits[0])) {
					// 让图片在屏幕中居中
					iv_drag.layout(mScreenWidth / 2 - iv_drag.getWidth() / 2,
							mScreenHeight / 2 - iv_drag.getHeight() / 2,
							mScreenWidth / 2 + iv_drag.getWidth() / 2,
							mScreenHeight / 2 + iv_drag.getHeight() / 2);
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.LOCATION_X, iv_drag.getLeft());
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.LOCATION_Y, iv_drag.getTop());
				}
			}
		});

		// 监听某一个控件的拖拽过程(按下(1),移动(多次),抬起(1))
		iv_drag.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:// 按下
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:// 移动
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();

					int disX = moveX - startX;
					int disY = moveY - startY;
					// 当前控件所在屏幕的（左，上）角的位置
					int left = iv_drag.getLeft() + disX;// 左侧坐标
					int top = iv_drag.getTop() + disY;// 顶端坐标
					int right = iv_drag.getRight() + disX;// 右侧坐标
					int bottom = iv_drag.getBottom() + disY;// 底部坐标
					// 容错处理(iv_drag不能拖拽出手机屏幕)
					// 左边缘不能超出屏幕
					if (left < 0) {
						return true;
					}

					// 右边边缘不能超出屏幕
					if (right > mScreenWidth) {
						return true;
					}

					// 上边缘不能超出屏幕可现实区域
					if (top < 0) {
						return true;
					}

					// 下边缘(屏幕的高度-22 = 底边缘显示最大值)减22是减去通知栏的高度
					if (bottom > mScreenHeight - 22) {
						return true;
					}

					// 在移动过程中,如果吐司图片高度超过一半屏幕高度,则让描述文字底部隐藏,顶部显示,反之同上
					if (top > mScreenHeight / 2) {
						// 底部的描述隐藏
						bt_bottom.setVisibility(View.INVISIBLE);
						// 顶部的描述显示
						bt_top.setVisibility(View.VISIBLE);
					} else {
						// 底部的描述显示
						bt_bottom.setVisibility(View.VISIBLE);
						// 顶部的描述隐藏
						bt_top.setVisibility(View.INVISIBLE);
					}

					// 告知移动的控件，按计算出来的坐标去做展示
					iv_drag.layout(left, top, right, bottom);
					// 重置一次坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:// 抬起
					// 储存控件位置
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.LOCATION_X, iv_drag.getLeft());
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.LOCATION_Y, iv_drag.getTop());
					break;
				default:
					break;
				}

				// 在当前的情况下返回false不响应事件，返回true才会响应事件
				// 既要响应点击事件，又要响应拖拽事件，则返回结果需要修改为false
				return false;
			}
		});
	}
}
