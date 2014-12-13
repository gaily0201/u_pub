package cn.gaily.simplejdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cn.gaily.pub.util.CommonUtils;

/**
 * <p>Title: SimpleChecker</P>
 * <p>Description: �����</p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-31
 */
public class SimpleSession {

	
	public static String getPkName(SimpleDSMgr mgr,String tableName){
		if(CommonUtils.isEmpty(tableName)){
			throw new RuntimeException("��ȡ["+tableName+"]����ʱ���������Ч");
		}

		String sql = " SELECT A.COLUMN_NAME FROM USER_CONS_COLUMNS A, USER_CONSTRAINTS B" +
					 " WHERE A.CONSTRAINT_NAME = B.CONSTRAINT_NAME" +
			         " AND B.CONSTRAINT_TYPE = 'P'  AND A.TABLE_NAME ='"+tableName+"'";
		
		Statement st = null;
		ResultSet rs = null;
		String value = null;
		Connection conn = mgr.getConnection();
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while(rs.next()){
				value = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			SimpleJdbc.release(null, st, rs);
			mgr.release(conn);
		}
		return value;
	}
	
	

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
			throw new RuntimeException(tableName+"У����Ƿ����ʱ��������Ӳ�����");
		}
		String sql = "SELECT COUNT(1) FROM USER_TABLES WHERE TABLE_NAME=?";
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = mgr.getConnection();
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, tableName.toUpperCase().trim());
			rs = pst.executeQuery();
			if(rs.next() && 1<=rs.getInt(1)){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(tableName+"У����Ƿ����ʱ��ѯ���ݳ���");
		}finally{
			SimpleJdbc.release(null, pst, rs);
			mgr.release(conn);
		}
		return false;
	}
	
	public static boolean checkHasTable(Connection conn, String tableName){
		if(conn==null){
			throw new RuntimeException(tableName+"У����Ƿ����ʱ��������Ӳ�����");
		}
		String sql = "SELECT COUNT(1) FROM USER_TABLES WHERE TABLE_NAME=?";
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, tableName.toUpperCase().trim());
			rs = pst.executeQuery();
			if(rs.next() && 1<=rs.getInt(1)){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(tableName+"У����Ƿ����ʱ��ѯ���ݳ���");
		}finally{
			SimpleJdbc.release(null, pst, rs);
		}
		return false;
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
			throw new RuntimeException(tableName+"У�����Ƿ����ʱ��������Ӳ�����");
		}
		String sql = "SELECT COUNT(1) FROM USER_TAB_COLS  WHERE TABLE_NAME=? AND COLUMN_NAME=? ";
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = mgr.getConnection();
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
			throw new RuntimeException(tableName+"У�����Ƿ��ʱ��ѯ���ݳ���");
		}finally{
			SimpleJdbc.release(null, pst, rs);
			mgr.release(conn);
		}
		return false;
	}
	
	
	public static int getRecordCount(SimpleDSMgr mgr, String tableName){
		if(mgr==null||mgr.conns.size()<=0){
			throw new RuntimeException(tableName+"��ȡ��¼��ʱ��������Ӳ�����");
		}
		Connection conn = mgr.getConnection();
		String sql = "SELECT COUNT(1) FROM "+tableName;
		Statement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.createStatement();
			rs = pst.executeQuery(sql);
			if(rs.next() && 0<=rs.getInt(1)){
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(tableName+"��ȡ��¼��ʱ��ѯ���ݳ���"+e);
		}finally{
			SimpleJdbc.release(null, pst, rs);
			mgr.release(conn);
		}
		return 0;
	}
	
	
	/**
	 * <p>�������ƣ�executeSql</p>
	 * <p>����������ִ��sql</p>
	 * @param mgr
	 * @param sql
	 * @author xiaoh
	 * @since  2014-11-22
	 * <p> history 2014-11-22 xiaoh  ����   <p>
	 */
	public static void executeSql(SimpleDSMgr mgr, String sql) {
		if(mgr==null||CommonUtils.isEmpty(sql)||mgr.conns.size()<=0){
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
