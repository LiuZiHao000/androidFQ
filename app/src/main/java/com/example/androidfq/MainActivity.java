package com.example.androidfq;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import chenli.dao.User;
import chenli.tcp.TcpServer;
import chenli.tools.Tools;
import chenli.udp.UdpReceive;
import chenli.udp.UdpSend;

public class MainActivity extends Activity {

	public static final int LIST_FLUSH = 1;
	public static final int FILE_TISHI = 2;
	public static final int FILE_JINDU = 3;
	public static final int PROGRESS_FLUSH = 4;
	public static final int PROGRESS_COL = 5;
	public static final int SHOW = 0;
	ProgressDialog proDia = null;
	public List<User> userList = new ArrayList<User>();
	public List<Map<String, String>> SAList = null;
	SimpleAdapter sa = null;
	UdpSend msgSend = new UdpSend(this);// 发送文件对象
	User fileToUser = null;
	public String choosePath = null;// 选中的文件
	public String ip = null;// 存放发来文件的人的ip
	public int nowUser = 1;
	public double fileSize;// 暂存文件大小
	public TextView state;// 显示状态 传送文件显示
	public String stateStr;
	private TextView about;
	private TextView aboutTxt;
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case LIST_FLUSH:
					sa.notifyDataSetChanged();
					break;
				// MainActivity.this.listView.setAdapter((SimpleAdapter)msg.obj);break;
				case SHOW:
					Toast.makeText(MainActivity.this, (String) msg.obj,
							Toast.LENGTH_SHORT).show();
					break;
				case FILE_TISHI:
					showFileTiShi((String) msg.obj);

					break;
				case FILE_JINDU:

					String[] pi = ((String) msg.obj).split(Tools.sign);
					state.setVisibility(0);
					fileSize = Double.parseDouble(pi[2]);
					stateStr = pi[0] + "  " + pi[1] + " "
							+ FileChoose.getFormatSize(fileSize);
					proDia.setTitle(pi[0]);// 设置标题
					proDia.setMessage(pi[1] + " 大小："
							+ FileChoose.getFormatSize(fileSize));// 设置显示信息
					// proDia.setMax(Integer.parseInt(pi[2]));//设置最大进度指

					proDia.onStart();
					proDia.show();

					break;
				case PROGRESS_FLUSH:
					int i = (int) ((Tools.sendProgress / (fileSize)) * 100);
					state.setText(stateStr + "  " + i + "% 点击查看进度");
					proDia.setProgress(i);
					// proDia.setProgress((int)Tools.sendProgress);
					// jinDu();

					break;
				case PROGRESS_COL:// 关闭进度条
					state.setVisibility(8);
					proDia.dismiss();
					// jinDu();

					break;
				default:
					return;
			}
		}
	};
	public ListView listView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.init();
	}

	@Override
	// 创建菜单
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		// menu.add(Menu.NONE, Menu.FIRST + 1, 5, "删除").setIcon(
		//
		// android.R.drawable.ic_menu_delete);

		// setIcon()方法为菜单设置图标，这里使用的是系统自带的图标，同学们留意一下,以

		// android.R开头的资源是系统提供的，我们自己提供的资源是以R开头的

		// menu.add(Menu.NONE, Menu.FIRST + 2, 2, "保存").setIcon(
		//
		// android.R.drawable.ic_menu_edit);
		//
		// menu.add(Menu.NONE, Menu.FIRST + 3, 6, "帮助").setIcon(
		//
		// android.R.drawable.ic_menu_help);

		menu.add(Menu.NONE, Menu.FIRST + 4, 1, "设置").setIcon(

				android.R.drawable.ic_menu_manage);

		// menu.add(Menu.NONE, Menu.FIRST + 5, 4, "详细").setIcon(
		//
		// android.R.drawable.ic_menu_info_details);
		//
		// menu.add(Menu.NONE, Menu.FIRST + 6, 3, "发送").setIcon(
		//
		// android.R.drawable.ic_menu_send);

		return true;

	}

	// 菜单事件
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

			case Menu.FIRST + 1:

				Toast.makeText(this, "删除菜单被点击了", Toast.LENGTH_LONG).show();

				break;

			case Menu.FIRST + 2:

				Toast.makeText(this, "保存菜单被点击了", Toast.LENGTH_LONG).show();

				break;

			case Menu.FIRST + 3:

				Toast.makeText(this, "帮助菜单被点击了", Toast.LENGTH_LONG).show();

				break;

			case Menu.FIRST + 4:
				changeName();
				// Toast.makeText(this, "添加菜单被点击了", Toast.LENGTH_LONG).show();

				break;

			case Menu.FIRST + 5:

				Toast.makeText(this, "详细菜单被点击了", Toast.LENGTH_LONG).show();

				break;

			case Menu.FIRST + 6:

				Toast.makeText(this, "发送菜单被点击了", Toast.LENGTH_LONG).show();

				break;

		}

		return false;

	}

	// 初始化
	public void init() {
		about = (TextView) super.findViewById(R.id.about);
		aboutTxt = (TextView) super.findViewById(R.id.aboutTxt);
		state = (TextView) super.findViewById(R.id.state);
		state.setVisibility(8);// 隐藏状态栏

		listView = (ListView) super.findViewById(R.id.listView);
		// 注册菜单
		super.registerForContextMenu(this.listView);
		// 列表项 添加长按监听
		listView.setOnItemLongClickListener(new OnItemLongClickListenerList());
		listView.setOnItemClickListener(new OnItemClickListenerList());
		// 初始化列表 显示自己
		SAList = new ArrayList<Map<String, String>>();
		Map map = new HashMap<String, String>();
		map.put("name", Tools.getName() + "（自己）");
		map.put("ip", Tools.getLocalHostIp());
		map.put("msgPoint", "0");
		userList.add(new User(Tools.getName(), Tools.getLocalHostIp()));
		SAList.add(map);
		sa = new SimpleAdapter(this, SAList, R.layout.online_main,
				new String[] { "name", "ip", "msgPoint" }, new int[] {
				R.id.name, R.id.ip, R.id.msgPoint });
		MainActivity.this.listView.setAdapter(sa);
		msgSend.upline();// 发送上线消息！
		// 开启接收端 时时更新在线列表
		UdpReceive ur = new UdpReceive(this);
		ur.Star();
		Toast.makeText(MainActivity.this, Tools.getBroadCastIP(),
				Toast.LENGTH_SHORT).show();
		if (Tools.sendProgress != -1) {
			state.setVisibility(0);
		}
		proDia = new ProgressDialog(this);
		proDia.setTitle("文件发送");// 设置标题
		proDia.setMessage("文件");// 设置显示信息
		proDia.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 水平进度条
		proDia.setMax(100);// 设置最大进度指
		proDia.setProgress(10);// 开始点
		proDia.setButton("后台处理", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				proDia.dismiss();// 关闭对话框
			}
		});
		state.setOnClickListener(new OnClickListener() {// 显示进度条

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				proDia.show();
			}
		});
		// proDia.onStart();
		about.setOnClickListener(new OnClickListener() {// 打开关于窗体

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				about();

			}
		});
	}

	// 创建菜单
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("操作");
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "发送文件");
		if (nowUser == 0) {
			menu.add(Menu.NONE, Menu.FIRST + 2, 2, "修改昵称");
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) { // 选中菜单时 询问是否接受
			case Menu.FIRST + 1:
				// 跳转至文件选择器
				this.toFileChoose();
				break;
			case Menu.FIRST + 2:
				changeName();
				break;
		}

		// 通知 对方 欲创建tcpsocket连接

		return true;
	}

	// 修改昵称提示窗口
	public void changeName() {
		LayoutInflater factory = LayoutInflater.from(MainActivity.this);
		View myView = factory.inflate(R.layout.changename_win, null);// 将布局文件转换成view
		final EditText changeName = (EditText) myView
				.findViewById(R.id.changeName);
		changeName.setHint(Tools.getName());
		Dialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setIcon(R.drawable.head)
				.setTitle("昵称修改")
				.setView(myView)
				.setPositiveButton("修改", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Tools.changeName(changeName.getText().toString(),
								MainActivity.this);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).create();
		dialog.show();
	}

	// 跳转文件选择器
	public void toFileChoose() {
		Intent it = new Intent(MainActivity.this, FileChoose.class);
		MainActivity.this.startActivityForResult(it, 1);
	}

	// 取得选择的路径 发送信息提示接收文件
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
			case RESULT_OK:
				choosePath = data.getStringExtra("path");

				// Tools.changeName((new File(choosePath)).length()+"",
				// MainActivity.this);
//			Toast.makeText(MainActivity.this, "取得文件路径：" + choosePath,
//					Toast.LENGTH_LONG).show();
				// 发送msg询问对方是否接受消息choosePath
				msgSend.fileXunWen(fileToUser, choosePath);
		}
	}

	@Override
	public void onContextMenuClosed(Menu menu) {
		// TODO Auto-generated method stub
		super.onContextMenuClosed(menu);

	}

	// 列表 点击跳转聊天
	class OnItemClickListenerList implements OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
			// 打开聊天窗体
			Intent it = new Intent(MainActivity.this, ChatA.class);
			it.putExtra("User", userList.get(arg2));//
			MainActivity.this.startActivityForResult(it, 1);
			// 当前人的聊天提示信息清零
			msgPointZ(userList.get(arg2).getIp());
			// Toast.makeText(MainActivity.this,
			// "userlist:" + userList.get(arg2).getName(),
			// Toast.LENGTH_LONG).show();
		}

	}

	// 打开聊天窗 消息清零
	public void msgPointZ(String ip) {
		Tools.chatFirst=1;
		String point = null;
		for (int i = 0; i < SAList.size(); i++) {
			if (SAList.get(i).get("ip").equals(ip)) { // 遍历
				SAList.get(i).put("msgPoint", "0");
				Message m = new Message();
				m.what = LIST_FLUSH;
				handler.sendMessage(m);
			}
		}
	}

	// 列表长按监听
	class OnItemLongClickListenerList implements OnItemLongClickListener {

		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
									   int arg2, long arg3) {
			fileToUser = userList.get(arg2);// 存储即将发送文件给谁
			nowUser = arg2;// 存储当前列表 项 判断是否为自己
			return false;
		}

	}

	@Override
	public void onBackPressed() {

		new AlertDialog.Builder(this).setTitle("确认退出吗？")

				.setIcon(android.R.drawable.ic_dialog_info)

				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						// 发送下线msg
						msgSend.downline();
						// 点击“确认”后的操作

						MainActivity.this.finish();
						System.exit(0);

					}

				})

				.setNegativeButton("返回", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						// 点击“返回”后的操作,这里不设置没有任何操作

					}

				}).show();

		// super.onBackPressed();

	}

	// 文件接收提示！
	public void showFileTiShi(String str) {
		new AlertDialog.Builder(MainActivity.this)
				.setTitle("是否接收文件：" + str.split(Tools.sign)[0] +" 大小："+FileChoose.getFormatSize(Double.parseDouble(str.split(Tools.sign)[1])))

				.setIcon(android.R.drawable.ic_dialog_info)

				.setPositiveButton("接受", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						// 接收文件 返回提示接受 建立tcp 服务器 接收文件
						TcpServer ts = new TcpServer(MainActivity.this);
						ts.start();
						Tools.sendProgress = 0;
						Message m1 = new Message();
						m1.what = FILE_JINDU;
						m1.obj = "接收文件" + Tools.sign + "正在接收：" + Tools.fileName
								+ Tools.sign + Tools.fileSize;
						handler.sendMessage(m1);
						fileProgress();// 启动进度条线程
						// 发送消息 让对方开始发送文件
						msgSend.fileJie(ip);
						return;
					}

				})

				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						// 不接受 返回提示不接受
						msgSend.fileJu(ip);
						return;
					}

				}).show();
	}

	public void fileProgress() {
		new Thread() {
			public void run() {

				while (Tools.sendProgress != -1) {

					// Tools.sendProgress++;
					Message m = new Message();
					m.what = PROGRESS_FLUSH;
					handler.sendMessage(m);
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// 关闭进度条
				Message m1 = new Message();
				m1.what = PROGRESS_COL;
				handler.sendMessage(m1);
			}
		}.start();
	}

	// 修改昵称提示窗口
	public void about() {
		LayoutInflater factory = LayoutInflater.from(MainActivity.this);
		// aboutTxt =(TextView)super.findViewById(R.id.aboutTxt);
		// aboutTxt.setText("本程序为个人完成测试版，可检测同一wifi下登陆本程序的设备。也可以进入设备设置本机为wifi热点构建局域网."
		// +
		// "\r\n程序基本功能为个人聊天和文件传输，文件传输测试平均速度为1.5M,可以方便传输文件，因为是局域网通讯，所以不消耗流量!后续提供新功能，\r\n"
		// +
		// "这是一款可以伴随你进步的软件，有好的建议和想法请" +
		// "\r\nemil：185817196@qq.com" +
		// "\r\n让作者帮你把你的想法变成可能！");
		View myView = factory.inflate(R.layout.about_win, null);// 将布局文件转换成view
		Dialog dialog = new AlertDialog.Builder(MainActivity.this)
				// .setIcon(R.drawable.head)
				.setTitle("关于").setView(myView)
				.setPositiveButton("关闭", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						return;
					}
				}).create();
		dialog.show();
	}
}
