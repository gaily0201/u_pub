package cn.gaily.simplejdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import cn.gaily.pub.util.CommonUtil;

/**
 * <p>Title: SimpleChecker</P>
 * <p>Description: �����</p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-31
 */
public class SimpleChecker {

	/**
	 * <p>�������ƣ�checkTableExist</p>
	 * <p>����������������Ƿ����</p>
	 * @param tableName
	 * @return
	 * @author xiaoh
	 * @since  2014-10-31
	 * <p> history 2014-10-31 xiaoh  ����   <p>
	 */
	public static boolean checkHasTable(SimpleDSMgr mgr, String tableName){
		if(mgr==null||mgr.conns.size()<=0){
			throw new RuntimeException("У����Ƿ����ʱ��������Ӳ�����");
		}
		return checkHasTable(mgr.getConnection(), tableName);
	}
	
	/**
	 * <p>�������ƣ�checkHasColumn</p>
	 * <p>����������У���Ƿ������</p>
	 * @param mgr
	 * @param tableName
	 * @param columnName
	 * @return
	 * @author xiaoh
	 * @since  2014-10-31
	 * <p> history 2014-10-31 xiaoh  ����   <p>
	 */
	public static boolean checkHasColumn(SimpleDSMgr mgr, String tableName,String columnName){
		if(mgr==null||mgr.conns.size()<=0){
			throw new RuntimeException("У�����Ƿ����ʱ��������Ӳ�����");
		}
		return checkHasColumn(mgr.getConnection(), tableName, columnName);
	}
	
	/**
	 * <p>�������ƣ�checkHasColumn</p>
	 * <p>����������У���Ƿ������</p>
	 * @param mgr
	 * @param tableName
	 * @param columnName
	 * @return
	 * @author xiaoh
	 * @since  2014-10-31
	 * <p> history 2014-10-31 xiaoh  ����   <p>
	 */
	public static boolean checkHasColumn(Connection conn, String tableName,String columnName){
		if(CommonUtil.isEmpty(tableName)||CommonUtil.isEmpty(columnName)){
			throw new RuntimeException("У�����Ƿ��ʱ���������Ч");
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
			throw new RuntimeException("У�����Ƿ��ʱ��ѯ���ݳ���");
		}finally{
			SimpleJdbc.release(conn, pst, rs);
		}
		
		
		return false;
	}
	
	
	/**
	 * <p>�������ƣ�checkHasTable</p>
	 * <p>����������������Ƿ����</p>
	 * @param conn
	 * @param tableName
	 * @return
	 * @author xiaoh
	 * @since  2014-10-31
	 * <p> history 2014-10-31 xiaoh  ����   <p>
	 */
	public static boolean checkHasTable(Connection conn,String tableName){
		if(CommonUtil.isEmpty(tableName)){
			throw new RuntimeException("У����Ƿ����ʱ���������Ч");
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
			throw new RuntimeException("У����Ƿ����ʱ��ѯ���ݳ���");
		}finally{
			SimpleJdbc.release(conn, pst, rs);
		}
		return false;
	}
	
	
}
