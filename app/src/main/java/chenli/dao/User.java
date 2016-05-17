package chenli.dao;

public class User implements java.io.Serializable {

	private String name="";

	private String ip;
	private int msgPoint = 0;

	public User(String name, String pCName, String ip) {
		super();
		this.name = name;

		this.ip = ip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getMsgPoint() {
		return msgPoint;
	}

	public void setMsgPoint(int msgPoint) {
		this.msgPoint = msgPoint;
	}

	public User(String name, String ip, int msgPoint) {
		super();
		this.name = name;
		this.ip = ip;
		this.msgPoint = msgPoint;
	}

	public User(String name, String ip) {
		super();
		this.name = name;
		this.ip = ip;
	}

}
