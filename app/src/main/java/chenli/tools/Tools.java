package chenli.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import com.example.androidfq.FileChoose;
import com.example.androidfq.MainActivity;

public class Tools {
	static final String CFGPATH = "/mnt/sdcard/wifiFQ/config";
	public static final String savePath = "/mnt/sdcard/wifiFQ/Files";
	public static String fileName = null;// 发送的文件名称
	public static long fileSize=0;// 文件大小
	public static int byteSize = 1024*5;// 每次读写文件的字节数
	public static String sign = ":";
	public static String startPath = FileChoose.ADDRESS;
	public static Map<String, String> MsgEx = new HashMap<String, String>();
	public static double sendProgress = -1;// 每次读写文件的字节数s
	public static float fontSize = 0;// chat msgShow的字体大小
	public static float chatFirst = 0;// chat msgShow的字体大小


	// 获得本机ip
	public static String getLocalHostIp() {
		String ipaddress = "";
		try {
			Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces();
			// 遍历所用的网络接口
			while (en.hasMoreElements()) {
				NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
				Enumeration<InetAddress> inet = nif.getInetAddresses();
				// 遍历每一个接口绑定的所有ip
				while (inet.hasMoreElements()) {
					InetAddress ip = inet.nextElement();
					if (!ip.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(ip
							.getHostAddress())) {
						return ipaddress = ip.getHostAddress();
					}
				}

			}
		} catch (SocketException e) {
			Log.e("feige", "获取本地ip地址失败");
			e.printStackTrace();
		}
		return ipaddress;

	}

	// 得到广播ip
	public static String getBroadCastIP() {
		String ip = getLocalHostIp().substring(0,
				getLocalHostIp().lastIndexOf(".") + 1)
				+ "255";
		return ip;
	}

	// 获得机器信息
	public static String getMachineInfo() {
		// String manufacturer = null;
		// String model = null;
		// String device = null;
		// int version = 3;
		// try {
		//
		// Class<android.os.Build.VERSION> build_version_class =
		// android.os.Build.VERSION.class;
		// // 取得 android 版本
		// java.lang.reflect.Field field = build_version_class
		// .getField("SDK_INT");
		// version = (Integer) field.get(new android.os.Build.VERSION());
		//
		// Class<android.os.Build> build_class = android.os.Build.class;
		// // 取得牌子
		// java.lang.reflect.Field manu_field = build_class
		// .getField("MANUFACTURER");
		// manufacturer = (String) manu_field.get(new android.os.Build());
		// // 取得型號
		// java.lang.reflect.Field field2 = build_class.getField("MODEL");
		// model = (String) field2.get(new android.os.Build());
		// // 模組號碼
		// java.lang.reflect.Field device_field = build_class
		// .getField("DEVICE");
		// device = (String) device_field.get(new android.os.Build());
		// // Tools.debug("Build.DEVICE:" + Build.DEVICE);
		// // Tools.debug("Build.ID:" + Build.ID);
		// // Tools.debug("Build.DISPLAY:" + Build.DISPLAY);
		// // Tools.debug("Build.PRODUCT:" + Build.PRODUCT);
		// // Tools.debug("Build.BOARD:" + Build.BOARD);
		// // Tools.debug("Build.BRAND:" + Build.BRAND);
		// // Tools.debug("Build.MODEL:" + );
		// String xinxi = "android " + "牌子:" + manufacturer + " 型號:" + model
		// + " SDK版本:" + version + " 模組號碼:" + device;
		// } catch (Exception e) {
		// }

		return Build.MODEL;
	}

	/**
	 * 对象转数组
	 *
	 * @param obj
	 * @return
	 */
	public static byte[] toByteArray(Object obj) {
		byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			bytes = bos.toByteArray();
			oos.close();
			bos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return bytes;
	}

	/**
	 * 数组转对象
	 *
	 * @param bytes
	 * @return
	 */
	public static Object toObject(byte[] bytes) {
		Object obj = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			obj = ois.readObject();
			ois.close();
			bis.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return obj;
	}

	// 改昵称
	public static void changeName(String name,MainActivity mainA) {
		Writer wb = null;
		File file = new File(CFGPATH + "/config.data");
		try {
			wb = new OutputStreamWriter(new FileOutputStream(file));

			wb.write(name + "\r\n");
			wb.close();
			//刷新列表
			mainA.SAList.get(0).put("name", name+"(自己)");
			mainA.userList.get(0).setName(name);
			Message m = new Message();
			m.what = mainA.LIST_FLUSH;
			mainA.handler.sendMessage(m);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 取昵称
	public static String getName() {
		File dir = new File(CFGPATH);
		File file = new File(CFGPATH + "/config.data");
		Writer wb = null;
		BufferedReader rb = null;
		String name = null;
		// 是否插入sd卡
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		try {
			if (!sdCardExist) {// 如果没有插入sd卡
				return Tools.getMachineInfo() + ("");
			}
			// 如果文件不存在 即为第一次使用
			if (!dir.exists()) {
				dir.mkdirs();
				// 昵称默认为机器型号
				wb = new OutputStreamWriter(new FileOutputStream(file));
				wb.write(Tools.getMachineInfo() + "\r\n");
				wb.close();
			}
			// 读取昵称
			rb = new BufferedReader(new InputStreamReader(new FileInputStream(
					file)));
			name = rb.readLine();
			rb.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return name;
	}

	public static long getTimel() {
		return (new Date()).getTime();
	}

	// 聊天记录记录
	public static void chatRecord(String ip, String msg) {
		msg = MsgEx.get(ip) + msg;
		MsgEx.put(ip, msg);
	}

	// 去聊天记录
	public static String getChatRecord(String ip) {
		if (MsgEx == null || MsgEx.isEmpty()) {// 如果为空的话
			return "";
		}
		return MsgEx.get(ip);
	}

	// 时间转换
	public static String getChangeTime(long timel) {
		SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sfd.format(timel);
	}

	// 获得初始目录
	public static String getStartPath(String startPath) {
		if (startPath == null) {
			return FileChoose.ADDRESS;
		} else {
			File file = new File(startPath);
			if (file.isFile()) {
				return file.getParent();
			} else {
				return startPath;
			}
		}
	}


	/**
	 * 打开文件
	 * @param file
	 */
	public static void openFile(Activity a,File file){

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);
		//获取文件file的MIME类型
		String type = getMIMEType(file);
		//设置intent的data和Type属性。
		intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
		//跳转
		a.startActivity(intent);

	}

	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 * @param file
	 */
	private static String getMIMEType(File file) {

		String type="*/*";
		String fName = file.getName();
		//获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if(dotIndex < 0){
			return type;
		}
	    /* 获取文件的后缀名*/
		String end=fName.substring(dotIndex,fName.length()).toLowerCase();
		if(end=="")return type;
		//在MIME和文件类型的匹配表中找到对应的MIME类型。
		for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
			if(end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}







	//	 MIME_MapTable是所有文件的后缀名所对应的MIME类型的一个String数组：
//
//	Java代码  <span style="white-space: pre;">    </span>
	private static final String[][] MIME_MapTable={
			//{后缀名，MIME类型}
			{".3gp",    "video/3gpp"},
			{".apk",    "application/vnd.android.package-archive"},
			{".asf",    "video/x-ms-asf"},
			{".avi",    "video/x-msvideo"},
			{".bin",    "application/octet-stream"},
			{".bmp",    "image/bmp"},
			{".c",  "text/plain"},
			{".class",  "application/octet-stream"},
			{".conf",   "text/plain"},
			{".cpp",    "text/plain"},
			{".doc",    "application/msword"},
			{".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
			{".xls",    "application/vnd.ms-excel"},
			{".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
			{".exe",    "application/octet-stream"},
			{".gif",    "image/gif"},
			{".gtar",   "application/x-gtar"},
			{".gz", "application/x-gzip"},
			{".h",  "text/plain"},
			{".htm",    "text/html"},
			{".html",   "text/html"},
			{".jar",    "application/java-archive"},
			{".java",   "text/plain"},
			{".jpeg",   "image/jpeg"},
			{".jpg",    "image/jpeg"},
			{".js", "application/x-javascript"},
			{".log",    "text/plain"},
			{".m3u",    "audio/x-mpegurl"},
			{".m4a",    "audio/mp4a-latm"},
			{".m4b",    "audio/mp4a-latm"},
			{".m4p",    "audio/mp4a-latm"},
			{".m4u",    "video/vnd.mpegurl"},
			{".m4v",    "video/x-m4v"},
			{".mov",    "video/quicktime"},
			{".mp2",    "audio/x-mpeg"},
			{".mp3",    "audio/x-mpeg"},
			{".mp4",    "video/mp4"},
			{".mpc",    "application/vnd.mpohun.certificate"},
			{".mpe",    "video/mpeg"},
			{".mpeg",   "video/mpeg"},
			{".mpg",    "video/mpeg"},
			{".mpg4",   "video/mp4"},
			{".mpga",   "audio/mpeg"},
			{".msg",    "application/vnd.ms-outlook"},
			{".ogg",    "audio/ogg"},
			{".pdf",    "application/pdf"},
			{".png",    "image/png"},
			{".pps",    "application/vnd.ms-powerpoint"},
			{".ppt",    "application/vnd.ms-powerpoint"},
			{".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
			{".prop",   "text/plain"},
			{".rc", "text/plain"},
			{".rmvb",   "audio/x-pn-realaudio"},
			{".rtf",    "application/rtf"},
			{".sh", "text/plain"},
			{".tar",    "application/x-tar"},
			{".tgz",    "application/x-compressed"},
			{".txt",    "text/plain"},
			{".wav",    "audio/x-wav"},
			{".wma",    "audio/x-ms-wma"},
			{".wmv",    "audio/x-ms-wmv"},
			{".wps",    "application/vnd.ms-works"},
			{".xml",    "text/plain"},
			{".z",  "application/x-compress"},
			{".zip",    "application/x-zip-compressed"},
			{"",        "*/*"}
	};
}
