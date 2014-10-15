package cn.gaily.pub.util;

/**
 * <p>Title: DataSourceInfo</P>
 * <p>Description: 简单的新旧数据库信息</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-9-10
 */
public class DataSourceInfo{
	public String oName;
	public String oUserName;
	public String oPassword;
	public String oIp;
	public String nName;
	public String nUserName;
	public String nPassword;
	public String nIp;
	public String getoName() {
		return oName;
	}
	public void setoName(String oName) {
		this.oName = oName;
	}
	public String getoUserName() {
		return oUserName;
	}
	public void setoUserName(String oUserName) {
		this.oUserName = oUserName;
	}
	public String getoPassword() {
		return oPassword;
	}
	public void setoPassword(String oPassword) {
		this.oPassword = oPassword;
	}
	public String getoIp() {
		return oIp;
	}
	public void setoIp(String oIp) {
		this.oIp = oIp;
	}
	public String getnName() {
		return nName;
	}
	public void setnName(String nName) {
		this.nName = nName;
	}
	public String getnUserName() {
		return nUserName;
	}
	public void setnUserName(String nUserName) {
		this.nUserName = nUserName;
	}
	public String getnPassword() {
		return nPassword;
	}
	public void setnPassword(String nPassword) {
		this.nPassword = nPassword;
	}
	public String getnIp() {
		return nIp;
	}
	public void setnIp(String nIp) {
		this.nIp = nIp;
	}
}
