package cn.gaily.pub.trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.gaily.pub.util.CommonUtils;
import cn.gaily.simplejdbc.SimpleDSMgr;
import cn.gaily.simplejdbc.SimpleJdbc;
import cn.gaily.simplejdbc.SimpleSession;

/**
 * <p>Title: ETLSynTask</P>
 * <p>Description: ԭʼ����ͬ������</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-11-22
 */
public class ETLSynTask {

	public final String tempTabName = "ETL_PK_TEMP";
	
	private Map<String,String> tabPkMap = new HashMap<String,String>();
	
	private List<String> pkValues = new ArrayList<String>();
	
	/**
	 * <p>�������ƣ�doExecute</p>
	 * <p>����������ִ������</p>
	 * @param tableName
	 * @author xiaoh
	 * @since  2014-11-22
	 * <p> history 2014-11-22 xiaoh  ����   <p>
	 */
	public void doExecute(SimpleDSMgr src, SimpleDSMgr dest, String tableName){
		/**
		 * 1. ������ʱ��
		 * 2. ���������ݲ�����ʱ��
		 * 3. ��Ŀ�����ݿ�ͬ����������ʱ�������Ƚϣ������ڵ���������ʱ��ɾ��
		 * 4. ��ѯԴ�����ݣ�����Ŀ���
		 */
		
		buildTempTab(src);
		
		insertTemp(src,tableName);
		
		synTempPk(src, dest, tableName);
		
		AbstractETLTask task = DefaultETLTask.getInstance();
		task.execute(src, dest, tableName);  //TODO �޸�statusΪ1;�޸Ĳ�ѯ��ʱ��ETL_ǰ׺ ...
		
		dropTemTab(src);
	}
	
	
	
	
	/**
	 * <p>�������ƣ�dropTemTab</p>
	 * <p>����������ɾ����ʱ��</p>
	 * @param src
	 * @author xiaoh
	 * @since  2014-11-22
	 * <p> history 2014-11-22 xiaoh  ����   <p>
	 */
	private void dropTemTab(SimpleDSMgr src) {
		String sql ="DROP TABLE "+ tempTabName;
		SimpleSession.executeSql(src, sql);
	}





	/**
	 * <p>�������ƣ�synTempPk</p>
	 * <p>������������Ŀ�����ݿ�ͬ����������ʱ�������Ƚϣ������ڵ���������ʱ��ɾ��</p>
	 * @param src
	 * @param dest
	 * @param tableName
	 * @author xiaoh
	 * @since  2014-11-22
	 * <p> history 2014-11-22 xiaoh  ����   <p>
	 */
	private void synTempPk(SimpleDSMgr src, SimpleDSMgr dest, String tableName) {
		String sql = "DELETE FROM "+tempTabName+" WHERE PKVALUE =?";
		String pkName = getPkName(src, tableName);
		String qsql = "SELECT "+pkName+" FROM "+tableName;
		
		if(pkValues.isEmpty()){
			return;
		}
		Connection srcConn = src.getConnection();
		Connection destConn = dest.getConnection();
		Statement st = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String value = null;
		try {
			srcConn.setAutoCommit(false);
			pst = srcConn.prepareStatement(sql);
			st = destConn.createStatement();
			rs = st.executeQuery(qsql);
			while(rs.next()){
				value = rs.getString(1);
				if(CommonUtils.isEmpty(value)){
					continue;
				}
				if(!pkValues.contains(value)){
					continue;
				}
				pst.setString(1, value);
				pst.addBatch();
				pst.clearParameters();
				pkValues.remove(value);
			}
			pst.executeBatch();
			srcConn.commit();
			pst.clearBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			SimpleJdbc.release(null, pst, null);
			SimpleJdbc.release(null, st, rs);
			src.release(srcConn);
			dest.release(destConn);
		}
	}






	/**
	 * <p>�������ƣ�insertTemp</p>
	 * <p>�������������������ݲ�����ʱ��</p>
	 * @author xiaoh
	 * @since  2014-11-22
	 * <p> history 2014-11-22 xiaoh  ����   <p>
	 */
	private void insertTemp(SimpleDSMgr src, String tableName){
		pkValues.clear();
		String pkName = getPkName(src, tableName);
		if(CommonUtils.isEmpty(pkName)){
			return;
		}
		String insql = "INSERT INTO "+tempTabName+"(TABNAME,PKNAME,PKVALUE) VALUES(?,?,?)";
		String qusql = "SELECT "+pkName + " FROM "+tableName;
		
		Connection conn = src.getConnection();
		Connection iconn = src.getConnection();
		Statement st = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		String value = null;
		
		int count = SimpleSession.getRecordCount(src, tableName);
		if(count<=0){
			return;
		}
		
		try {
			iconn.setAutoCommit(false);
			pst = iconn.prepareStatement(insql);
			st = conn.createStatement();
			rs = st.executeQuery(qusql);
			int i = 0;
			while(rs.next()){
				i++;
				value = rs.getString(1);
				if(CommonUtils.isEmpty(value)){
					continue;
				}
				pkValues.add(value);
				pst.setString(1, tableName);
				pst.setString(2, pkName);
				pst.setString(3, value);
				pst.addBatch();
				pst.clearParameters();
				if(i/800==0||i==count){
					pst.executeBatch();
					pst.clearBatch();
				}
			}
			iconn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			SimpleJdbc.release(null, st, rs);
			SimpleJdbc.release(null, pst, null);
			src.release(conn);
		}
	}
	
	
	/**
	 * <p>�������ƣ�getPkName</p>
	 * <p>������������ȡ����</p>
	 * @param src
	 * @param tableName
	 * @return
	 * @author xiaoh
	 * @since  2014-11-22
	 * <p> history 2014-11-22 xiaoh  ����   <p>
	 */
	private String getPkName(SimpleDSMgr src, String tableName) {
		String pkName = tabPkMap.get(tableName);
		if(CommonUtils.isNotEmpty(pkName)){
			return pkName;
		}
		String sql = " SELECT A.COLUMN_NAME INTO PKNAME FROM USER_CONS_COLUMNS A, USER_CONSTRAINTS B" +
					 " WHERE A.CONSTRAINT_NAME = B.CONSTRAINT_NAME" +
			         " AND B.CONSTRAINT_TYPE = 'P'  AND A.TABLE_NAME ='"+tableName+"'";
		
		Connection conn = src.getConnection();
		Statement st = null;
		ResultSet rs = null;
		String value = null;
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
			src.release(conn);
		}
		tabPkMap.put(tableName, pkName);
		
		return value;
	}

	/**
	 * <p>�������ƣ�buildTempTab</p>
	 * <p>����������������ʱ������������������</p>
	 * @author xiaoh
	 * @param  ds
	 * @throws SQLException 
	 * @since  2014-11-22
	 * <p> history 2014-11-22 xiaoh  ����   <p>
	 */
	private void buildTempTab(SimpleDSMgr ds){
		boolean has = SimpleSession.checkHasTable(ds, tempTabName);
		String dsql = "DROP TABLE "+tempTabName;
		String sql = "CREATE TABLE "+tempTabName+"(PKVALUE VARCHAR2(50), TABLENAME VARCHAR2(30), PKNAME VARCHAR2(50))";
		String pkvsql = "CREATE INDEX IDX_V ON "+tempTabName+"(PKVALUE)";
		String tabsql = "CREATE INDEX IDX_T ON "+tempTabName+"(TABNAME)";
		String pknsql = "CREATE INDEX IDX_N ON "+tempTabName+"(PKNAME)";
		Connection conn = ds.getConnection();
		Statement st = null;
		try {
			conn.setAutoCommit(false);
			st  = conn.createStatement();
			if(has){
				st.executeUpdate(dsql);
				conn.commit();
			}
			st.executeUpdate(sql);
			st.executeUpdate(pkvsql);
			st.executeUpdate(tabsql);
			st.executeUpdate(pknsql);
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally{
			SimpleJdbc.release(null, st, null);
			ds.release(conn);
		}
	}

}
