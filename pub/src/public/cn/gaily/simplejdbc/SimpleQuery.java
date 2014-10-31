package cn.gaily.simplejdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * <p>Title: Query</P>
 * <p>Description: ��ѯ</p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-31
 */
public class SimpleQuery {

	private SimpleDSMgr ds = null;
	
	private Connection conn = null;
	
	public SimpleQuery(){
	}
	
	public SimpleQuery(SimpleDSMgr ds){
		this.ds = ds;
	}
	public SimpleQuery(Connection conn){
		this.conn = conn;
	}
	
	
	/**
	 * <p>�������ƣ�query</p>
	 * <p>�����������򵥵Ĳ�ѯ����</p>
	 * @param sql			��ѯsql
	 * @param processor		�����������
	 * @return
	 * @author xiaoh
	 * @since  2014-10-31
	 * <p> history 2014-10-31 xiaoh  ����   <p>
	 */
	public Object query(String sql, IResultSetProcessor processor){
		
		Statement st = null;
		ResultSet rs = null;
		
		if(ds==null&&conn==null){
			throw new RuntimeException("û�л������");
		}
		if(ds!=null&&ds.conns.size()>0){
			conn = ds.getConnection();
		}
		Object obj = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			obj = processor.process(rs);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("��ѯ���ݿ����"+e);
		} finally{
			if(ds!=null){
				ds.release(conn);
				SimpleJdbc.release(null, st, rs);
			}else{
				SimpleJdbc.release(conn, st, rs);
			}
		}
		
		return obj;
	}
	
	public Object query(String sql,SQLParameter parameter, IResultSetProcessor processor){
		
		return null;
	}
	
	
	
	public SimpleDSMgr getDs() {
		return ds;
	}
	public void setDs(SimpleDSMgr ds) {
		this.ds = ds;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public Connection getConn() {
		return conn;
	}
	
}
