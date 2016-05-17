package chenli.udp;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Activity;
import chenli.dao.MessageDao;
import chenli.dao.MsgType;
import chenli.dao.User;
import chenli.tools.Tools;

public class UdpSend {
	Activity mainA = null;

	public UdpSend(Activity mainA) {
		this.mainA = mainA;
	}

	public UdpSend() {
	}

	public void sendMsg(MessageDao msg) {// 启动线程发送
		Send send = new Send(msg);
		send.start();
	}

	class Send extends Thread {
		MessageDao msg = null;

		Send(MessageDao msg) {
			this.msg = msg;
		}

		public void run() {
			SendMsg(msg);
			return;
		}
	}

	// 发消息
	private void SendMsg(MessageDao msg) {
		try {
			// byte[] data = this.messageLoad(msg);
			byte[] data = Tools.toByteArray(msg);
			DatagramSocket ds = new DatagramSocket(2426);

			DatagramPacket packet1 = new DatagramPacket(data, data.length,
					InetAddress.getByName(msg.getReceiveUser()), 2425);
			packet1.setData(data);
			ds.send(packet1);
			ds.close();
		} catch (Exception e) {
		}
	}

	// 上线
	public void upline() {// 版本号：数据包id：发送人昵称：消息类型：消息体
		// 组装上线msg
		MessageDao msg = new MessageDao(Tools.getTimel() + "", Tools.getName(),
				MsgType.ONLINE + "", Tools.getBroadCastIP());
		sendMsg(msg);// 发送广播通知上线
	}

	// 下线
	public void downline() {// 版本号：数据包id：发送人昵称：消息类型：消息体

		// 组装上线msg
		MessageDao msg = new MessageDao(Tools.getTimel() + "", Tools.getName(),
				MsgType.DOWNLINE + "", Tools.getBroadCastIP());
		sendMsg(msg);// 发送广播通知下线
	}

	// 询问是否接收文件 **
	public void fileXunWen(User user, String path) {
		// 组装上线msg
		MessageDao msg = new MessageDao(Tools.getTimel() + "", Tools.getName(),
				MsgType.FileTishi + "", user.getIp(),
				(new File(path)).getName()+Tools.sign+(new File(path)).length());
		sendMsg(msg);//
	}

	// 回复 接收
	public void fileJie(String ip) {
		// 组装上线msg
		MessageDao msg = new MessageDao(Tools.getTimel() + "", Tools.getName(),
				MsgType.FILEJIE + "", ip);
		sendMsg(msg);// 发送广播通知下线
	}

	// 回复 拒绝
	public void fileJu(String ip) {
		// 组装上线msg
		MessageDao msg = new MessageDao(Tools.getTimel() + "", Tools.getName(),
				MsgType.FILEJU + "", ip);
		sendMsg(msg);// 发送广播通知下线
	}

	// 回复 拒绝
	public void fileOver(String ip) {
		// 组装上线msg
		MessageDao msg = new MessageDao(Tools.getTimel() + "", Tools.getName(),
				MsgType.FILEOVER + "", ip);
		sendMsg(msg);// 发送广播通知下线
	}
}
