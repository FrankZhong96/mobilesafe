package com.frank.mobilesafe.activity;

import java.util.List;
import com.frank.mobilesafe.R;
import com.frank.mobilesafe.db.bean.BlackNumberInfo;
import com.frank.mobilesafe.db.dao.BlackNumberDao;
import com.frank.mobilesafe.utils.ToastUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

//1,复用convertView
//2,对findViewById次数的优化,使用ViewHolder
//3,将ViewHolder定义成静态,不会去创建多个对象
//4,listView如果有多个条目的时候,可以做分页算法,每一次加载20条,逆序返回

public class BlackNumberActivity extends Activity {

	private Button bt_add;
	private ListView iv_blacknumber;
	private List<BlackNumberInfo> mBlackNumberList;
	private BlackNumberDao mDao;
	private MyAdapter myAdapter;
	private int mode = 1;
	private boolean mIsLoad = false;
	private int mCount;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			myAdapter = new MyAdapter();
			iv_blacknumber.setAdapter(myAdapter);
		};
	};

	class MyAdapter extends BaseAdapter {

		private ViewHolder viewHolder;

		@Override
		public int getCount() {
			return mBlackNumberList.size();
		}

		@Override
		public Object getItem(int position) {
			return mBlackNumberList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			/*
			 * View view = null; if (convertView == null) {//复用convertView优化
			 * view = View.inflate(getApplicationContext(),
			 * R.layout.listview_blacknumber, null); }else { view = convertView;
			 * }
			 */
			if (convertView == null) {// 复用convertView优化
				convertView = View.inflate(getApplicationContext(),
						R.layout.listview_blacknumber, null);
				viewHolder = new ViewHolder();
				viewHolder.tv_phone = (TextView) convertView
						.findViewById(R.id.tv_phone);
				viewHolder.tv_mode = (TextView) convertView
						.findViewById(R.id.tv_mode);
				viewHolder.iv_delete = (ImageView) convertView
						.findViewById(R.id.iv_delete);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			TextView tv_phone = (TextView) convertView
					.findViewById(R.id.tv_phone);
			TextView tv_mode = (TextView) convertView
					.findViewById(R.id.tv_mode);
			ImageView iv_delete = (ImageView) convertView
					.findViewById(R.id.iv_delete);

			tv_phone.setText(mBlackNumberList.get(position).phone);
			mode = Integer.parseInt(mBlackNumberList.get(position).mode);
			switch (mode) {
			case 1:
				tv_mode.setText("拦截短信");
				break;
			case 2:
				tv_mode.setText("拦截电话");
				break;
			case 3:
				tv_mode.setText("拦截所有");
				break;
			default:
				break;
			}

			iv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 删除数据库
					mDao.delete(mBlackNumberList.get(position).phone);
					// 删除集合里的数据
					mBlackNumberList.remove(position);
					// 通知数据适配器更新数据
					if (myAdapter != null) {
						myAdapter.notifyDataSetChanged();
					}
				}
			});

			return convertView;
		}
	}

	static class ViewHolder {
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_delete;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacknumber);

		initUI();
		initDate();

	}

	private void initDate() {
		// 获取数据库中所有的电话号码
		new Thread() {
			public void run() {
				mDao = BlackNumberDao.getInstance(getApplicationContext());			
				// 查询部分数据
				mBlackNumberList = mDao.find(0);
				mCount = mDao.getCount();
				mHandler.sendEmptyMessage(0);// 查询完成
			};
		}.start();

	}

	private void initUI() {
		bt_add = (Button) findViewById(R.id.bt_add);
		iv_blacknumber = (ListView) findViewById(R.id.iv_blacknumber);

		bt_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog();
			}
		});

		// 监听其滚动状态
		iv_blacknumber.setOnScrollListener(new OnScrollListener() {
			// 滚动过程中,状态发生改变调用方法()
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// OnScrollListener.SCROLL_STATE_FLING 飞速滚动
				// OnScrollListener.SCROLL_STATE_IDLE 空闲状态
				// OnScrollListener.SCROLL_STATE_TOUCH_SCROLL 拿手触摸着去滚动状态

				if (mBlackNumberList != null) {
					// 条件一:滚动到停止状态
					// 条件二:最后一个条目可见(最后一个条目的索引值>=数据适配器中集合的总条目个数-1)
					if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
							&& iv_blacknumber.getLastVisiblePosition() >= mBlackNumberList
									.size() - 1 && !mIsLoad) {
						/*
						 * mIsLoad防止重复加载的变量
						 * 如果当前正在加载mIsLoad就会为true,本次加载完毕后,再将mIsLoad改为false
						 * 如果下一次加载需要去做执行的时候
						 * ,会判断上诉mIsLoad变量,是否为false,如果为true,就需要等待上一次加载完成,将其值
						 * 改为false后再去加载
						 */

						// 如果条目总数大于集合大小的时,才可以去继续加载更多
						if (mCount > mBlackNumberList.size()) {
							// 加载下一页数据
							new Thread() {
								public void run() {
									// 1,获取操作黑名单数据库的对象
									mDao = BlackNumberDao
											.getInstance(getApplicationContext());
									// 2,查询部分数据
									List<BlackNumberInfo> moreData = mDao
											.find(mBlackNumberList.size());
									// 3,添加下一页数据的过程
									mBlackNumberList.addAll(moreData);
									// 4,通知数据适配器刷新
									mHandler.sendEmptyMessage(0);
								}
							}.start();
						}
					}
				}

			}

			// 滚动过程中调用方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}

	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(getApplicationContext(),
				R.layout.dialog_add_blacknumber, null);
		dialog.setView(view, 0, 0, 0, 0);

		final EditText et_input_phone = (EditText) view
				.findViewById(R.id.et_input_phone);
		RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_sms:
					mode = 1;
					break;
				case R.id.rb_phone:
					mode = 2;
					break;
				case R.id.rb_all:
					mode = 3;
					break;
				default:
					break;
				}
			}
		});

		// 确定
		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String phone = et_input_phone.getText().toString();
				if (!TextUtils.isEmpty(phone)) {// 如果输入的号码不为空
					// 插入数据库
					mDao.insert(phone, mode + "");
					// 让数据库和集合保持同步（1，数据库中数据重新读一遍2，手动向集合中添加一个对象（插入数据构建的对象））
					BlackNumberInfo blickNumberInfo = new BlackNumberInfo();
					blickNumberInfo.phone = phone;
					blickNumberInfo.mode = mode + "";
					// 将对象插入集合的最顶部
					mBlackNumberList.add(0, blickNumberInfo);
					// 通知数据适配器更新数据（数据改变了）
					if (myAdapter != null) {
						myAdapter.notifyDataSetChanged();
					}
					// 隐藏对话框
					dialog.dismiss();

				} else {
					ToastUtil.show(getApplicationContext(), "请输入拦截号码", 0);
				}

			}
		});

		// 取消
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
}
