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
 * <p>Description: 触发器创建类-用于创建交换使用的触发器</p>
 * <p> </p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
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
	 * <p>方法名称：generate</p>
	 * <p>方法描述：构建触发器  创建交换表, 启用交换表, </p>
	 * @param tableName
	 * @param type
	 * @param fullBuild
	 * @author xiaoh
	 * @since  2014-10-27
	 * <p> history 2014-10-27 xiaoh  创建   <p>
	 */
	public void generate(String tableName, Integer type, boolean fullBuild){
		
		checkTempExist(tableName); 	//1、检验临时表是否存在，不存在创建
		
		enableTable(tableName); 	//2、启用数据表触发器
		String sql = null;
		if(fullBuild){				//3、创建触发器
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
	 * <p>方法名称：execSql</p>
	 * <p>方法描述：执行创建触发器脚本方法</p>
	 * @param sql
	 * @author xiaoh
	 * @since  2014-10-27
	 * <p> history 2014-10-27 xiaoh  创建   <p>
	 */
	private void execSql(String sql){
		if(conn==null){
			throw new RuntimeException("请添加数据源");
		}
		Statement st = null;
		try {
			st = conn.createStatement();
			st.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("执行创建触发器出错: "+e);
		}finally{
			JdbcUtils.release(conn, st, null);
		}
	}
	
	/**
	 * <p>方法名称：checkTempExist</p>
	 * <p>方法描述：检验表是否存在,不存在创建</p>
	 * @param tableName
	 * @return
	 * @author xiaoh
	 * @since  2014-10-27
	 * <p> history 2014-10-27 xiaoh  创建   <p>
	 */
	private boolean checkTempExist(String tableName){
		if(conn==null){
			throw new RuntimeException("请添加数据源");
		}
		if(CommonUtil.isEmpty(tableName)){
			throw new RuntimeException("请传入有效的表名");
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
	 * <p>方法名称：enableTable</p>
	 * <p>方法描述：启用表,向XFL_TABSTATUS中插入一条数据</p>
	 * @param tableName
	 * @author xiaoh
	 * @since  2014-10-27
	 * <p> history 2014-10-27 xiaoh  创建   <p>
	 */
	private void enableTable(String tableName){
		if(conn==null){
			throw new RuntimeException("请添加数据源");
		}
		
		if(CommonUtil.isEmpty(tableName)){
			throw new RuntimeException("传入的表名无效"); //TODO 真实还需检验数据表中该表是否存在
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
	 * <p>方法名称：buildTriggerSql</p>
	 * <p>方法描述：构建创建触发器的sql</p>
	 * @param tableName
	 * @param type	     1:新增, 2:更新, 3:删除
	 * @author xiaoh
	 * @since  2014-10-27
	 * <p> history 2014-10-27 xiaoh  创建   <p>
	 */
	private String buildTriggerSql(String tableName, int type){
		
		if(type!=NEW&&type!=UPDATE&&type!=DELETE){
			throw new RuntimeException("传入的类型无效无效");
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
		sb.append("ETLSTATUS, ETLPKNAME, ETLTS) VALUES "); //--ETLSTATUS 数据状态
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
	 * <p>方法名称：getTableCols</p>
	 * <p>方法描述：获取sql标准</p>
	 * @param tableName
	 * @param type  1:标准, 2::NEW, 3::OLD
	 * @return
	 * @author xiaoh
	 * @since  2014-10-27
	 * <p> history 2014-10-27 xiaoh  创建   <p>
	 */
	private Map<Integer, String> getTableCols(String tableName, int type) {
		if(conn==null){
			throw new RuntimeException("请添加数据源");
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
			throw new RuntimeException("查询数据出错:"+e);
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











