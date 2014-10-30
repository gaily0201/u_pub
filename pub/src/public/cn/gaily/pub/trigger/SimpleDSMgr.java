package cn.gaily.pub.trigger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.gaily.pub.util.JdbcUtils;

/**
 * <p>Title: SimpleDSMgr</P>
 * <p>Description: 简单的数据源管理器</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-28
 */
public class SimpleDSMgr{

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
	
	private String dbName 	= null;
	private String ip 		= null;
	private String userName	= null;
	private String password  = null;
	private String port 		= null;
	
	
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
		if(conns.isEmpty()){
			init();
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
			conn = JdbcUtils.getConnection(userName, password, ip, dbName, port);
			conns.add(conn);
//			try {
//				conn.setAutoCommit(false);
//			} catch (SQLException e) {
//				e.printStackTrace();
//				throw new RuntimeException("设置手动提交失败");
//			}
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
	
}
