package pic_e.ncafeverify.data;

import java.io.Serializable;

public class VerifyData implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String naverId;
	private long time;
	
	public VerifyData(String naverId, long time) {
		this.naverId = naverId;
		this.time = time;
	}
	
	public String getNaverId() {
		return this.naverId;
	}
	
	public long getTime() {
		return this.time;
	}
}
