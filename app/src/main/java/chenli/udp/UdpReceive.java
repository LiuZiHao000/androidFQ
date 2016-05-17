package chenli.udp;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

import android.os.Message;
import chenli.dao.MessageDao;
import chenli.dao.MsgType;
import chenli.dao.User;
import chenli.tcp.TcpClient;
import chenli.tools.Tools;

import com.example.androidfq.ChatA;
import com.example.androidfq.FileChoose;
import com.example.androidfq.MainActivity;

public class UdpReceive {

	private MainActivity mainA;
	UdpSend msgSend = new UdpSend();

	public UdpReceive(MainActivity mainA) {
		this.mainA = mainA;
	}

	public void Star() {
		FReceive fr = new FReceive();
		// Toast.makeText(mainA, "线程已打开", Toast.LENGTH_SHORT).show();

		fr.start();
		// test();
	}

	// 建udp接收端
	class FReceive extends Thread {

		public void run() {
			try {
				// msgSend.upline();// 发送上线消息！
				// msgSend.upline();// 发送上线消息！
				while (true) {

					fType(receiveMsg());// 接受消息 开始分类
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public MessageDao receiveMsg() throws Exception {

		DatagramSocket ds = new DatagramSocket(2425);
		byte[] data1 = new byte[1024 * 4];
		DatagramPacket dp = new DatagramPacket(data1, data1.length);
		dp.setData(data1);
		ds.receive(dp);

		// Message m = new Message();
		// m.what = mainA.SHOW;
		// m.obj = " 消息来了！";
		// mainA.handler.sendMessage(m);

		byte[] data2 = new byte[dp.getLength()];
		System.arraycopy(data1, 0, data2, 0, data2.length);// 得到接收的数据
		MessageDao msg = (MessageDao) Tools.toObject(data2);
		String ip = dp.getAddress() + "";
		if (ip.charAt(0) == '/') {
			ip = ip.substring(1);
		}
		msg.setSendUserIp(ip);
		ds.close();

		return msg;
	}

	// 0 1 2 3 4 5
	// 按消息类型处理
	// 1_lbt4_36#128#000000000000#1676#0#0:1351617184:a:A-PC:"+MsgType.ONLINE+":蛋疼！！！！"
	public void fType(MessageDao msg) {
		switch (Integer.parseInt(msg.getMsgType())) {
			case MsgType.ONLINE:// 上线
				upline(msg);
				break;
			case MsgType.SELFONLINE:// 我在线
				upline(msg);
				break;
			case MsgType.DOWNLINE: // 下线
				downline(msg);
				break;
			case MsgType.Msg: // 来文字消息

				// mainA.jinDu();
				doMsg(msg);
				break;
			case MsgType.FileTishi: // 文件接收提示
				doFile(msg);
				break;
			case MsgType.FILEJIE: // 对方确定接收文件
				sendFile(msg);// 发送文件
				break;
			case MsgType.FILEJU: // 对方拒绝接收文件
				this.doFileJu(msg);// 提示
				break;
		}
	}

	// 上线处理 //版本号：数据包号:用户名:命令字：昵称：ip
	public void upline(MessageDao msg) {
		// 看看列表有无此人
		// ----有 --判断名字有无更改
		// ----无--添加在线列表

		if (!judgeUser(msg)) {// 如果不存在
			tiShi(msg.getSendUser() + " 上线···");
			addUser(msg);// 添加此人
		}
		// 发送消息对方 告知我在线
		if (Integer.parseInt(msg.getMsgType()) == MsgType.SELFONLINE) {// 如果收到的是通知对方在线的消息
			// 则不需再向外发
			return;
		}
		SendSelfOline(msg);
	}

	// 添加user
	public void addUser(MessageDao msg) {
		User user = new User(msg.getSendUser(), msg.getSendUserIp(), 0);
		mainA.userList.add(user);// 在线列表加人
		// 为其创建聊天记录
		Tools.MsgEx.put(msg.getSendUserIp(), "");
		// listView加人
		online(user);
		// 刷新列表
		flushListView();
	}

	// //发送消息对方 告知我在线
	public void SendSelfOline(MessageDao msg) {
		UdpSend us = new UdpSend();
		msg.setSendUser(Tools.getName());
		msg.setMsgType(MsgType.SELFONLINE + "");// 我在线
		msg.setReceiveUser(msg.getSendUserIp());
		msg.setPacketID(Tools.getTimel() + "");
		us.sendMsg(msg);
	}

	// 判断是否有此人 更新
	public boolean judgeUser(MessageDao msg) {// false 表示不存在
		for (int i = 0; i < mainA.userList.size(); i++) {
			if (mainA.userList.get(i).getIp().equals(msg.getSendUserIp())) {
				// 如果存在 改名字
				if (!mainA.userList.get(i).getName().equals(msg.getSendUser())) {
					mainA.userList.get(i).setName(msg.getSendUser());// 该在线列表的名字
					mainA.SAList.get(i).put("name", msg.getSendUser());// 改listview
					// 的显示
					flushListView();
				}

				return true;
			}
		}
		return false;
	}

	// simpleAdapter（listview）加人
	public void online(User user) {
		Map map = new HashMap<String, String>();
		map.put("name", user.getName());
		map.put("ip", user.getIp());
		map.put("msgPoint", "0");
		mainA.SAList.add(map);
	}

	// 通知刷新列表
	public void flushListView() {
		Message m = new Message();
		m.what = mainA.LIST_FLUSH;
		mainA.handler.sendMessage(m);
	}

	// 下线 删除 更新列表
	public void downline(MessageDao msg) {
		tiShi(msg.getSendUser() + " 下线···");

		for (int i = 0; i < mainA.userList.size(); i++) {
			if (mainA.userList.get(i).getIp().equals(msg.getSendUserIp())) { // 如果存在这个人
				// 则删除
				mainA.userList.remove(i);
				mainA.SAList.remove(i);
				// 如果存在 改名字
				flushListView();
			}
		}
	}

	// 来消息处理
	public void doMsg(MessageDao msg) {
		tiShi(msg.getSendUser() + " 发来消息！");
		// 提示要消息
		String point = null;
		if (!judgeUser(msg)) {// 如果列表无此人
			this.addUser(msg);// 列表添加此人
		}
		String body = msg.getSendUser() + "（" + msg.getSendUserIp() + "） "
				+ Tools.getChangeTime(Long.parseLong(msg.getPacketID()))
				+ "\r\n  " + (String) msg.getBody() + "\r\n";
		// 加入到聊天记录
		Tools.MsgEx.put(msg.getSendUserIp(),
				Tools.MsgEx.get(msg.getSendUserIp()) + body);

		if (Tools.chatFirst!=0 && ChatA.u != null &&
				ChatA.u.getIp().equals(msg.getSendUserIp())) {// 如果正在和此人聊天
			Message m = new Message();
			m.what = ChatA.getThis().SHOW;
			m.obj = Tools.MsgEx.get(msg.getSendUserIp());
			// mainA.handler.sendMessage(m);
			ChatA.getThis().handler.sendMessage(m);// 刷新msgshow
		} else {// 如果没有和此人聊天则
			// 更新来信条数
			for (int i = 0; i < mainA.SAList.size(); i++) {
				if (mainA.SAList.get(i).get("ip").equals(msg.getSendUserIp())) { // 遍历
					point = mainA.SAList.get(i).get("msgPoint");

					point = (Integer.parseInt(point) + 1) + "";
					//tiShi(msg.getSendUser() + " 消息条数： " + point);
					mainA.SAList.get(i).put("msgPoint", point);
					flushListView();
				}

			}
		}
	}

	// 文件接收提示处理
	public void doFile(MessageDao msg) {
		// this.tiShi("接受到文件接收提示！··");
		// Message m = new Message();
		// m.what = mainA.FILE_TISHI;
		// m.obj=msg.getBody();
		// mainA.handler.sendMessage(m);
		mainA.ip = msg.getSendUserIp();
		String[] fileInfo = ((String) msg.getBody()).split(Tools.sign);

		Tools.fileName = fileInfo[0];// 记录下文件名称
		Tools.fileSize = Long.parseLong(fileInfo[1].trim());// 文件大小
		// Tools.changeName(Tools.fileSize+"", mainA);
		Message m = new Message();
		m.what = mainA.FILE_TISHI;
		m.obj = msg.getBody();
		mainA.handler.sendMessage(m);
		// super.onBackPressed();

	}

	// 建立tcp客户端发送文件
	public void sendFile(MessageDao msg) {
		String path = mainA.choosePath;
		this.tiShi("正在发送文件:" + new File(path).getName());
		Tools.sendProgress=0;
		TcpClient tc = new TcpClient(msg, path);
		tc.start();
		Message m1 = new Message();
		m1.what = mainA.FILE_JINDU;
		m1.obj="发送文件"+Tools.sign+"正在发送："+new File(path).getName()+Tools.sign+ (new File(path).length());
		mainA.handler.sendMessage(m1);
		fileProgress();//启动进度条线程
		// //this.tiShi("发送完成！文件:" + new File(path).getName());
	}

	// 显示mainActivity提示消息 比如又让你上线
	public void tiShi(String str) {
		Message m = new Message();
		m.what = mainA.SHOW;
		m.obj = str;
		mainA.handler.sendMessage(m);
	}

	// 拒绝文件传输处理
	public void doFileJu(MessageDao msg) {
		this.tiShi(msg.getSendUser() + "拒绝接收文件");
	}

	public void fileProgress() {
		new Thread() {
			public void run() {

				while (Tools.sendProgress != -1) {

					// Tools.sendProgress++;
					Message m = new Message();
					m.what = mainA.PROGRESS_FLUSH;
					mainA.handler.sendMessage(m);
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// 关闭进度条
				Message m1 = new Message();
				m1.what = mainA.PROGRESS_COL;
				mainA.handler.sendMessage(m1);
			}
		}.start();
	}
}
