package cn.gaily.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.exception.DbException;
import cn.gaily.itf.IETLService;
import cn.gaily.pub.impl.BaseServiceImpl;

/**
 * <p>Title: ETLServiceImpl</P>
 * <p>Description: ETL服务实现类</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-21
 */
public class ETLServiceImpl extends BaseServiceImpl implements IETLService {

	@Override
	public ArrayList<HashMap<String,String>> queryColumnInfo(String tableName,String dataSourceName) {
		String sql = "SELECT COLUMN_NAME,DATA_TYPE,DATA_LENGTH FROM USER_TAB_COLS WHERE TABLE_NAME=? ORDER BY COLUMN_NAME ASC";
		ArrayList<HashMap<String,String>> list = null;
		try {
			JdbcSession session = new JdbcSession(dataSourceName);
			Connection conn = session.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, tableName.toUpperCase());
			ResultSet rs = st.executeQuery();
			
			String columnName = null;
			String columnType = null;
			String colLength = null;
			list = new ArrayList<HashMap<String,String>>();
			HashMap<String,String> map = null;
			while(rs.next()){
				map = new HashMap<String,String>();
				columnName = rs.getString(1);
				columnType = rs.getString(2);
				colLength = rs.getString(3);
				map.put("name", columnName);
				map.put("type", columnType);
				map.put("length", colLength);
				list.add(map);
			}
			return list;
		} catch (DbException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
