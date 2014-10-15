package cn.gaily.pub.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



/**
 * <p>Title: ColCommentBuilder</P>
 * <p>Description: ��������ע��</p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-8-6
 */
public class ColCommentBuilder {
	
	/**
	 * <p>�������ƣ�build</p>
	 * <p>�������������ݱ����������������</p>
	 * @param tableName
	 * @author xiaoh
	 * @throws SQLException 
	 * @since  2014-8-6
	 * <p> history 2014-8-6 xiaoh  ����   <p>
	 */
	public static void build(String tableName) throws SQLException{
		Connection conn = JdbcUtils.getDefaultConnection();
		Statement st = conn.createStatement();
		String sql  = "SELECT COLUMN_NAME,COMMENTS FROM USER_COL_COMMENTS WHERE TABLE_NAME='"+tableName.toUpperCase()+"'";
		
		ResultSet rs = st.executeQuery(sql);
		int i = 0;
		while(rs.next()){
			System.out.println("/**");
			System.out.println("* "+rs.getString(2));
			System.out.println("*/");
			System.out.println("public static final String "+rs.getString(1)+"=\""+rs.getString(1).toLowerCase()+"\";");
			System.out.println();
			i++;
		}
		System.out.println(i);
	}
	
}
