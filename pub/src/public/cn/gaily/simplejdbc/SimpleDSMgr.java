package cn.gaily.simplejdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Title: SimpleDSMgr</P>
 * <p>Description: 简单的数据源管理器</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-28
 */
public class SimpleDSMgr implements Serializable{
	private static final long serialVersionUID = 1949752490522906986L;

	/**
	 * 默认初始化连接池大小
	 */
	public int defaultSize = 5;
	
	/**
	 * 初始化大小
	 */
	public int initSize = 0;
	
	/**
	 * 缓存连接List
	 */
	public List<Connection> conns = new ArrayList<Connection>();
	
	private String dbName 	 = null;
	private String ip 		 = null;
	private String userName	 = null;
	private String password  = null;
	private String port 	 = null;
	
	
	/**
	 * <p>方法名称：release</p>
	 * <p>方法描述：释放连接</p>
	 * @param conn
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  创建   <p>
	 */
	public void release(Connection conn){
		if(conn!=null){
			conns.add(conn);
		}
	}
	
	
	/**
	 * <p>方法名称：getConnection</p>
	 * <p>方法描述：获取链接</p>
	 * @return Connection
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  创建   <p>
	 */
	public Connection getConnection(){
		int i = 0;
		while(conns.isEmpty()){
			init();
			i++;
			if(i==10){
				return null;
			}
		}
		return conns.remove(0);
	}
	
	/**
	 * <p>方法名称：init</p>
	 * <p>方法描述：初始化连接</p>
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  创建   <p>
	 */
	public void init(){
		if(conns.isEmpty()&&initSize==0){
			initSize = defaultSize;
		}
		Connection conn = null;
		for(int i=0;i<initSize;i++){
			conn = SimpleJdbc.getConnection(userName, password, ip, dbName, port);
			if(conn!=null){
				conns.add(conn);
			}
		}
	}
	
	public void realRelease(){
		if(conns.size()>0){
			for(Connection c:conns){
				SimpleJdbc.release(c, null, null);
			}
		}
	}
	
	/**
	 * <p>方法名称：initDB</p>
	 * <p>方法描述：初始化数据库信息</p>
	 * @param dbName	数据库名
	 * @param ip		IP
	 * @param userName	用户名
	 * @param password	密码
	 * @param port		端口
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  创建   <p>
	 */
	public void initDB(String dbName,String ip,String userName, String password,String port){
		this.dbName 	= dbName;
		this.ip 		= ip;
		this.userName	= userName;
		this.password 	= password;
		this.port 		= port;
	}
	

	public void setInitSize(int initSize) {
		this.initSize = initSize;
	}
	public String getDbName() {
		return dbName;
	}
	public String getIp() {
		return ip;
	}
	public String getUserName() {
		return userName;
	}
	public String getPassword() {
		return password;
	}
	public String getPort() {
		return port;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setPort(String port) {
		this.port = port;
	}
}
