package cn.gaily.pub.trigger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import cn.gaily.pub.util.CommonUtil;
import cn.gaily.pub.util.JdbcUtils;

/**
 * <p>Title: TriggerGenerator</P>
 * <p>Description: ������������-���ڴ�������ʹ�õĴ�����</p>
 * <p> </p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-27
 */
public class TriggerGenerator {

	public static final int NEW = 1;
	public static final int UPDATE = 2;
	public static final int DELETE = 3;

	private Connection conn = null;
	
	
	
	/**
	 * <p>�������ƣ�generate</p>
	 * <p>��������������������  ����������, ���ý�����, </p>
	 * @param tableName
	 * @param type
	 * @param fullBuild
	 * @author xiaoh
	 * @since  2014-10-27
	 * <p> history 2014-10-27 xiaoh  ����   <p>
	 */
	public void generate(String tableName, Integer type, boolean fullBuild){
		
		checkTempExist(tableName); 	//1��������ʱ���Ƿ���ڣ������ڴ���
		
		enableTable(tableName); 	//2���������ݱ�����
		String sql = null;
		if(fullBuild){				//3������������
			sql = buildTriggerSql(tableName, NEW);
			execSql(sql);
			sql = buildTriggerSql(tableName, UPDATE);
			execSql(sql);
			sql = buildTriggerSql(tableName, DELETE);
			execSql(sql);
		}else{
			sql = buildTriggerSql(tableName, type); 
			execSql(sql);
		}
		System.out.println("build Sussesful!");
	}
	
	/**
	 * <p>�������ƣ�execSql</p>
	 * <p>����������ִ�д����������ű�����</p>
	 * @param sql
	 * @author xiaoh
	 * @since  2014-10-27
	 * <p> history 2014-10-27 xiaoh  ����   <p>
	 */
	private void execSql(String sql){
		if(conn==null){
			throw new RuntimeException("���������Դ");
		}
		Statement st = null;
		try {
			st = conn.createStatement();
			st.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("ִ�д�������������: "+e);
		}finally{
			JdbcUtils.release(conn, st, null);
		}
	}
	
	/**
	 * <p>�������ƣ�checkTempExist</p>
	 * <p>����������������Ƿ����,�����ڴ���</p>
	 * @param tableName
	 * @return
	 * @author xiaoh
	 * @since  2014-10-27
	 * <p> history 2014-10-27 xiaoh  ����   <p>
	 */
	private boolean checkTempExist(String tableName){
		if(conn==null){
			throw new RuntimeException("���������Դ");
		}
		if(CommonUtil.isEmpty(tableName)){
			throw new RuntimeException("�봫����Ч�ı���");
		}
		String sql = "SELECT COUNT(1) FROM USER_TABLES WHERE TABLE_NAME=?";
		Statement st = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, "XFL_"+tableName.toUpperCase().trim());
			rs = pst.executeQuery();
			if(rs.next()&&"1".equals(rs.getString(1))){
				return true;
			}else{
				st = conn.createStatement();
				st.executeUpdate("CREATE TABLE XFL_"+tableName+" AS SELECT * FROM "+tableName+" WHERE 1=0");
				st.executeUpdate("ALTER TABLE XFL_"+tableName+" ADD ETLSTATUS CHAR(1)");
				st.executeUpdate("ALTER TABLE XFL_"+tableName+" ADD ETLPKNAME VARCHAR2(50)");
				st.executeUpdate("ALTER TABLE XFL_"+tableName+" ADD ETLTS VARCHAR2(100)");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.release(null, pst, rs);
		}
		
		
		return false;
	}
	
	/**
	 * <p>�������ƣ�enableTable</p>
	 * <p>�������������ñ�,��XFL_TABSTATUS�в���һ������</p>
	 * @param tableName
	 * @author xiaoh
	 * @since  2014-10-27
	 * <p> history 2014-10-27 xiaoh  ����   <p>
	 */
	private void enableTable(String tableName){
		if(conn==null){
			throw new RuntimeException("���������Դ");
		}
		
		if(CommonUtil.isEmpty(tableName)){
			throw new RuntimeException("����ı�����Ч"); //TODO ��ʵ����������ݱ��иñ��Ƿ����
		}
		String dsql = "DELETE XFL_TABSTATUS WHERE TABLENAME =?";
		String sql = "INSERT INTO XFL_TABSTATUS (TABLENAME, STATUS) VALUES (?, '1')";
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(dsql);
			st.setString(1, tableName.toUpperCase().trim());
			st.executeUpdate();
			
			st.clearParameters();
			st = conn.prepareStatement(sql);
			st.setString(1, tableName.toUpperCase().trim());
			st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JdbcUtils.release(null, st, null);
		}
	}
	
	/**
	 * <p>�������ƣ�buildTriggerSql</p>
	 * <p>��������������������������sql</p>
	 * @param tableName
	 * @param type	     1:����, 2:����, 3:ɾ��
	 * @author xiaoh
	 * @since  2014-10-27
	 * <p> history 2014-10-27 xiaoh  ����   <p>
	 */
	private String buildTriggerSql(String tableName, int type){
		
		if(type!=NEW&&type!=UPDATE&&type!=DELETE){
			throw new RuntimeException("�����������Ч��Ч");
		}
		tableName = tableName.toUpperCase();
		StringBuilder sb  = new StringBuilder("CREATE OR REPLACE TRIGGER ");
		sb.append(tableName).append("_").append(type).append(" ");
		switch(type){
			case NEW: 
				sb.append(" AFTER INSERT ON ") ;
				break;
			case UPDATE: 
				sb.append(" BEFORE UPDATE ON ");
				break;
			case DELETE: 
				sb.append(" AFTER DELETE ON ");
				break;
		}
		sb.append(tableName).append(" FOR EACH ROW DECLARE ACTION NUMBER; PKNAME VARCHAR2(50);");
		sb.append("BEGIN SELECT STATUS INTO ACTION  FROM XFL_TABSTATUS WHERE TABLENAME='");
		sb.append(tableName).append("';");  //status
		sb.append(" SELECT A.COLUMN_NAME INTO PKNAME FROM USER_CONS_COLUMNS A, USER_CONSTRAINTS B");
		sb.append(" WHERE A.CONSTRAINT_NAME = B.CONSTRAINT_NAME");
		sb.append(" AND B.CONSTRAINT_TYPE = 'P'  AND A.TABLE_NAME ='");
		sb.append(tableName).append("';"); //pkname
		sb.append("IF(ACTION=1) THEN INSERT INTO XFL_").append(tableName);
		Map<Integer,String> map = getTableCols(tableName, 1);
		sb.append(map.get(1));
		sb.append("ETLSTATUS, ETLPKNAME, ETLTS) VALUES "); //--ETLSTATUS ����״̬
		switch(type){
		case NEW: 
			sb.append(map.get(2)).append("1,");
			break;
		case UPDATE: 
			sb.append(map.get(2)).append("2,");
			break;
		case DELETE: 
			sb.append(map.get(3)).append("3,");
			break;
		}
		sb.append("NVL(PKNAME,''), TO_CHAR(current_timestamp,'YYYYMMDDHH24MISSXFF'));");
		sb.append("END IF;");
		sb.append("EXCEPTION WHEN OTHERS THEN  NULL; END;");
		return sb.toString();
	}

	/**
	 * <p>�������ƣ�getTableCols</p>
	 * <p>������������ȡsql��׼</p>
	 * @param tableName
	 * @param type  1:��׼, 2::NEW, 3::OLD
	 * @return
	 * @author xiaoh
	 * @since  2014-10-27
	 * <p> history 2014-10-27 xiaoh  ����   <p>
	 */
	private Map<Integer, String> getTableCols(String tableName, int type) {
		if(conn==null){
			throw new RuntimeException("���������Դ");
		}
		Map<Integer,String> map = new HashMap<Integer,String>();
		StringBuilder sb = new StringBuilder(" (");
		StringBuilder nsb = new StringBuilder(" (");
		StringBuilder osb = new StringBuilder(" (");
		String sql = "SELECT COLUMN_NAME FROM USER_TAB_COLS WHERE TABLE_NAME=?";
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, tableName.toUpperCase().trim());
			rs = pst.executeQuery();
			String value = null;
			while(rs.next()){
				value = rs.getString(1).trim().toUpperCase();
				sb.append(value).append(",");
				nsb.append(":NEW.").append(value).append(",");
				osb.append(":OLD.").append(value).append(",");
			}
			map.put(1, sb.toString());
			map.put(2, nsb.toString());
			map.put(3, osb.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("��ѯ���ݳ���:"+e);
		}finally{
			JdbcUtils.release(null, pst, rs);
		}
		return map;
	}
	
	public Connection getConn() {
		return conn;
	}
	
	public void setconn(Connection conn) {
		this.conn = conn;
	}
}











