package cn.gaily.simplejdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cn.gaily.pub.util.CommonUtil;

/**
 * <p>Title: SimpleChecker</P>
 * <p>Description: 表检验</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-31
 */
public class SimpleSession {

	/**
	 * <p>方法名称：checkTableExist</p>
	 * <p>方法描述：检验表是否存在</p>
	 * @param tableName
	 * @return
	 * @author xiaoh
	 * @since  2014-10-31
	 * <p> history 2014-10-31 xiaoh  创建   <p>
	 */
	public static boolean checkHasTable(SimpleDSMgr mgr, String tableName){
		if(mgr==null||mgr.conns.size()<=0){
			throw new RuntimeException("校验表是否存在时传入的连接不存在");
		}
		return checkHasTable(mgr.getConnection(), tableName);
	}
	
	/**
	 * <p>方法名称：checkHasColumn</p>
	 * <p>方法描述：校验是否存在列</p>
	 * @param mgr
	 * @param tableName
	 * @param columnName
	 * @return
	 * @author xiaoh
	 * @since  2014-10-31
	 * <p> history 2014-10-31 xiaoh  创建   <p>
	 */
	public static boolean checkHasColumn(SimpleDSMgr mgr, String tableName,String columnName){
		if(mgr==null||mgr.conns.size()<=0){
			throw new RuntimeException("校验列是否存在时传入的连接不存在");
		}
		return checkHasColumn(mgr.getConnection(), tableName, columnName);
	}
	
	/**
	 * <p>方法名称：checkHasColumn</p>
	 * <p>方法描述：校验是否存在列</p>
	 * @param mgr
	 * @param tableName
	 * @param columnName
	 * @return
	 * @author xiaoh
	 * @since  2014-10-31
	 * <p> history 2014-10-31 xiaoh  创建   <p>
	 */
	public static boolean checkHasColumn(Connection conn, String tableName,String columnName){
		if(CommonUtil.isEmpty(tableName)||CommonUtil.isEmpty(columnName)){
			throw new RuntimeException("校验列是否存时传入参数无效");
		}
		String sql = "SELECT COUNT(1) FROM USER_TAB_COLS  WHERE TABLE_NAME=? AND COLUMN_NAME=? ";
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, tableName.toUpperCase().trim());
			pst.setString(2, columnName.toUpperCase().trim());
			rs = pst.executeQuery();
			if(rs.next() && 1==rs.getInt(1)){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("校验列是否存时查询数据出错");
		}finally{
			SimpleJdbc.release(conn, pst, rs);
		}
		
		
		return false;
	}
	
	public static int getRecordCount(SimpleDSMgr mgr, String tableName){
		if(mgr==null||mgr.conns.size()<=0){
			throw new RuntimeException("校验表是否存在时传入的连接不存在");
		}
		return getRecordCount(mgr.getConnection(), tableName);
	}
	
	public static int getRecordCount(Connection conn,String tableName){
		if(CommonUtil.isEmpty(tableName)){
			throw new RuntimeException("校验表是否存在时传入表名无效");
		}
		String sql = "SELECT COUNT(1) FROM "+tableName;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, tableName.toUpperCase().trim());
			rs = pst.executeQuery();
			if(rs.next() && 1==rs.getInt(1)){
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("校验表是否存在时查询数据出错");
		}finally{
			SimpleJdbc.release(conn, pst, rs);
		}
		return 0;
	}
	
	/**
	 * <p>方法名称：checkHasTable</p>
	 * <p>方法描述：检验表是否存在</p>
	 * @param conn
	 * @param tableName
	 * @return
	 * @author xiaoh
	 * @since  2014-10-31
	 * <p> history 2014-10-31 xiaoh  创建   <p>
	 */
	public static boolean checkHasTable(Connection conn,String tableName){
		if(CommonUtil.isEmpty(tableName)){
			throw new RuntimeException("校验表是否存在时传入表名无效");
		}
		String sql = "SELECT COUNT(1) FROM USER_TABLES WHERE TABLENAME=?";
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, tableName.toUpperCase().trim());
			rs = pst.executeQuery();
			if(rs.next() && 1==rs.getInt(1)){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("校验表是否存在时查询数据出错");
		}finally{
			SimpleJdbc.release(conn, pst, rs);
		}
		return false;
	}
	
	/**
	 * <p>方法名称：executeSql</p>
	 * <p>方法描述：执行sql</p>
	 * @param mgr
	 * @param sql
	 * @author xiaoh
	 * @since  2014-11-22
	 * <p> history 2014-11-22 xiaoh  创建   <p>
	 */
	public static void executeSql(SimpleDSMgr mgr, String sql) {
		if(mgr==null||CommonUtil.isEmpty(sql)||mgr.conns.size()<=0){
			return;
		}
		
		Connection conn = mgr.getConnection();
		Statement st = null;
		try {
			st = conn.createStatement();
			st.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			SimpleJdbc.release(null, st, null);
			mgr.release(conn);
		}
	}
	
	
}
