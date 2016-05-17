package com.example.androidfq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ZoomControls;
import chenli.dao.MessageDao;
import chenli.dao.MsgType;
import chenli.dao.User;
import chenli.tools.Tools;
import chenli.udp.UdpSend;

public class ChatA extends Activity {

	private static final ChatA chata = new ChatA();

	// private ChatA(){}
	public static ChatA getThis() {
		return chata;
	}

	public static final int LIST_FLUSH = 1;
	public static final int SHOW = 0;
	public static TextView showMsg;
	private Button sendBut;
	private TextView userName;
	private EditText editMsg;
	public static User u = null;
	UdpSend msgSend = new UdpSend();// 发送文件对象
	ScrollView scrollView;
	ZoomControls zoomC;
	public static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SHOW:
					showMsg.setText((String) msg.obj);
					// Toast.makeText(ChatA.this, (String) msg.obj,
					// Toast.LENGTH_SHORT)
					// .show();
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
		super.setContentView(R.layout.chat_main);
		Intent it = super.getIntent();
		u = (User) it.getSerializableExtra("User");
		init();
	}

	public void init() {
		scrollView = (ScrollView) super.findViewById(R.id.scrollView);
		zoomC = (ZoomControls) super.findViewById(R.id.zoomC);
		showMsg = (TextView) super.findViewById(R.id.showMsg);
		if (Tools.fontSize != 0) {// 设置初始字体大小
			showMsg.setTextSize(Tools.fontSize);
		}

		sendBut = (Button) super.findViewById(R.id.sendBut);
		userName = (TextView) super.findViewById(R.id.userName);
		editMsg = (EditText) super.findViewById(R.id.editMsg);
		showMsg.setText(Tools.getChatRecord(u.getIp()));// 显示聊天记录
		userName.setText(u.getName());
		sendBut.setOnClickListener(new OnClickListenerll());
		// scrollView.setOnTouchListener(new OnTouchListener());
		zoomC.setOnZoomInClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				showMsg.setTextSize(showMsg.getTextSize() + 2);
				Tools.fontSize=showMsg.getTextSize();
			}
		});// 设置放大监听
		zoomC.setOnZoomOutClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				showMsg.setTextSize(showMsg.getTextSize() - 2);
				Tools.fontSize=showMsg.getTextSize();
			}
		});
	}

//	class OnTouchListener implements android.view.View.OnTouchListener {//双指缩放功能
//		private int mode = 0;
//		float oldDist;
//		float textSize = 0;
//
//		public boolean onTouch(View v, MotionEvent event) {
//
//			if (textSize == 0) {
//				textSize = showMsg.getTextSize();
//			}
//			switch (event.getAction() & MotionEvent.ACTION_MASK) {
//			case MotionEvent.ACTION_DOWN:
//				mode = 1;
//				break;
//			case MotionEvent.ACTION_UP:
//				mode = 0;
//				break;
//			case MotionEvent.ACTION_POINTER_UP:
//				mode -= 1;
//				break;
//			case MotionEvent.ACTION_POINTER_DOWN:
//				oldDist = spacing(event);
//				mode += 1;
//				break;
//
//			case MotionEvent.ACTION_MOVE:
//				if (mode >= 2) {
//					float newDist = spacing(event);
//					if (newDist > oldDist + 1) {
//						zoom(newDist / oldDist);
//						oldDist = newDist;
//					}
//					if (newDist < oldDist - 1) {
//						zoom(newDist / oldDist);
//						oldDist = newDist;
//					}
//				}
//				break;
//			}
//
//			return true;
//		}
//
//		private void zoom(float f) {
//			showMsg.setTextSize(textSize *= f);
//		}
//
//		private float spacing(MotionEvent event) {
//			float x = event.getX(0) - event.getX(1);
//			float y = event.getY(0) - event.getY(1);
//			return FloatMath.sqrt(x * x + y * y);
//		}
//
//	}

	// 发送按钮监听
	class OnClickListenerll implements OnClickListener {

		public void onClick(View v) {
			String body = editMsg.getText().toString();
			editMsg.setText("");
			// 发送信息
			MessageDao msg = new MessageDao(Tools.getTimel() + "",
					Tools.getName(), MsgType.Msg + "", u.getIp(), body);
			msgSend.sendMsg(msg);
			// 刷新 消息框
			body = Tools.getName() + "(" + u.getIp() + ")  "
					+ Tools.getChangeTime(Tools.getTimel()) + "\r\n  " + body
					+ "\r\n";
			Tools.chatRecord(u.getIp(), body);
			showMsg.append(body);
		}
	}

	// 会掉的时候注意初始化！

	public void onBackPressed() {
		u = null;
		this.finish();

	}

}
