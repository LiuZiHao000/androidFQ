package chenli.dao;

import java.io.Serializable;

import chenli.tools.Tools;

public class MessageDao implements Serializable {
	// 版本号: :数据包号：（用户名）a: （类型）6291457: （类容）陈立 :ip
	private final String edition = "WIFIFQ1.0";// 版本号
	private String packetID;// 数据包号
	private String sendUser;// 发送人 用户名
	private String sendUserIp;// 发送人ip
	private String receiveUser;// 接收人 ip
	private String MsgType;// 消息类型
	private Object body;// 主体

	public String getSendUserIp() {
		return sendUserIp;
	}

	public void setSendUserIp(String sendUserIp) {
		this.sendUserIp = sendUserIp;
	}

	public String getEdition() {
		return edition;
	}

	public String getReceiveUser() {
		return receiveUser;
	}

	public void setReceiveUser(String receiveUser) {
		this.receiveUser = receiveUser;
	}







	public MessageDao(String packetID, String sendUser, String msgType,
					  String receiveUser) {
		super();
		this.packetID = packetID;
		this.sendUser = sendUser;
		MsgType = msgType;
		this.receiveUser = receiveUser;
	}

	public MessageDao(String packetID, String sendUser, String msgType,
					  String receiveUser, Object body) {
		super();
		this.packetID = packetID;
		this.sendUser = sendUser;
		MsgType = msgType;
		this.receiveUser = receiveUser;
		this.body = body;
	}

	public String getPacketID() {
		return packetID;
	}

	public void setPacketID(String packetID) {
		this.packetID = packetID;
	}

	public String getSendUser() {
		return sendUser;
	}

	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

}
