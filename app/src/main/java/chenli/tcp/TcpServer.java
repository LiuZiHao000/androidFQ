package chenli.tcp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.os.Message;
import chenli.tools.Tools;

import com.example.androidfq.MainActivity;

public class TcpServer {
	MainActivity mainA;

	public TcpServer(Activity mainA) {
		this.mainA = (MainActivity) mainA;
	}

	public void start() {
		server s = new server();
		s.start();
	}

	class server extends Thread {

		public void run() {
			try {
				creatServer();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void creatServer() throws Exception {
		ServerSocket ss = new ServerSocket(2222);
		Socket s = new Socket();
		s = ss.accept();
		// 服务器只是短时间用来传送文件 只用一次 用来接收文件
		// ObjectInputStream is=new ObjectInputStream(s.getInputStream());
		// MessageDao msg= (MessageDao)is.readObject();
		// File f=new File(Tools.savePath+"/"+msg.getSendUser());
		// if(!f.exists()){
		// f.getParentFile().mkdirs();
		// f.createNewFile();
		// }
		// OutputStream os=new FileOutputStream(f);
		// os.write((byte[])msg.getBody());
		// is.close();
		// os.flush();
		// os.close();
		// s.close();
		File file = new File(Tools.savePath + "/" + Tools.fileName);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		BufferedInputStream is = new BufferedInputStream(s.getInputStream()); // 读进
		BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));// 写出
		Thread.sleep(1000);
		int n = 1;
		long part = Tools.fileSize / Tools.byteSize;// 分成几段
		long surplus = Tools.fileSize % Tools.byteSize;// 最后剩余多少段
		byte[] data = new byte[Tools.byteSize];// 每次读取的字节数
		int len= -1;
		while ((len=is.read(data) )!= -1) {
			os.write(data,0,len);
			Tools.sendProgress+=len;//进度
		}
		Tools.sendProgress=-1;
		is.close();
		os.flush();
		os.close();
		s.close();
		tiShi("接收文成：" + Tools.fileName);

	}

	// 显示mainActivity提示消息 比如又让你上线
	public void tiShi(String str) {
		Message m = new Message();
		m.what = mainA.SHOW;
		m.obj = str;
		mainA.handler.sendMessage(m);
	}
}
