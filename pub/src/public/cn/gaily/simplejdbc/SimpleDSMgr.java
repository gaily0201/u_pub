package cn.gaily.simplejdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Title: SimpleDSMgr</P>
 * <p>Description: �򵥵�����Դ������</p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-28
 */
public class SimpleDSMgr implements Serializable{
	private static final long serialVersionUID = 1949752490522906986L;

	/**
	 * Ĭ�ϳ�ʼ�����ӳش�С
	 */
	public int defaultSize = 5;
	
	/**
	 * ��ʼ����С
	 */
	public int initSize = 0;
	
	/**
	 * ��������List
	 */
	public List<Connection> conns = new ArrayList<Connection>();
	
	private String dbName 	 = null;
	private String ip 		 = null;
	private String userName	 = null;
	private String password  = null;
	private String port 	 = null;
	
	
	/**
	 * <p>�������ƣ�release</p>
	 * <p>�����������ͷ�����</p>
	 * @param conn
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  ����   <p>
	 */
	public void release(Connection conn){
		if(conn!=null){
			conns.add(conn);
		}
	}
	
	
	/**
	 * <p>�������ƣ�getConnection</p>
	 * <p>������������ȡ����</p>
	 * @return Connection
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  ����   <p>
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
		Connection conn = conns.remove(0);
		boolean isValid = false;
		try {
			isValid = ensureOpen(conn);
			if(isValid){
				return conn;
			}else{
				return getConnection();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * <p>�������ƣ�ensureOpen</p>
	 * <p>����������ȷ���õ������ӿ���</p>
	 * @param conn
	 * @return
	 * @throws SQLException
	 * @author xiaoh
	 * @since  2014-12-1
	 * <p> history 2014-12-1 xiaoh  ����   <p>
	 */
	private boolean ensureOpen(Connection conn) throws SQLException {
		if(conn==null){
			return false;
		}
		if(conn.isClosed()){
			conn.close();
			conn = null;
			return false;
		}
		return true;
	}


	/**
	 * <p>�������ƣ�init</p>
	 * <p>������������ʼ������</p>
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  ����   <p>
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
			for(int i=0;i<conns.size();i++){
				SimpleJdbc.release(conns.get(i), null, null);
			}
		}
	}
	
	/**
	 * <p>�������ƣ�initDB</p>
	 * <p>������������ʼ�����ݿ���Ϣ</p>
	 * @param dbName	���ݿ���
	 * @param ip		IP
	 * @param userName	�û���
	 * @param password	����
	 * @param port		�˿�
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  ����   <p>
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
