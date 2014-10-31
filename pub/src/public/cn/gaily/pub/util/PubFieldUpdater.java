package cn.gaily.pub.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.gaily.simplejdbc.SimpleJdbc;

/**
 * <p>Title: PubFieldUpdater</P>
 * <p>Description: 通用的字段更新类, 用于模糊匹配字段值, 更新数据</p>
 * <p>
 * 		例：
			PubFieldUpdater updater = new PubFieldUpdater();
			updater.buildIndex(new String[]{"pk_ywcz_b","v_zfczbm","v_zfczmc"},"crpas_ywcz_b", true);
			Map<String,String> map = updater.getQueryInfo("crpas_baobu_h",new String[]{"pk_baobu_h","v_zay"});
			updater.buildUpdateMap(map, "v_zfczmc", "v_zfczbm");
			updater.updateToDB("crpas_baobu_h", "pk_baobu_h", "c_aybm");
		主要过程分为：
			1. 构建索引
			2. 获取待进行索引的数据(其中参数存在问题，数组中第一个必须为主键,具体很多暂待优化)
			3. 索引新数据构建待更新数据结构
			4. 更新数据
 * </p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-8-28
 */
public class PubFieldUpdater {


	public static Connection conn = null;
	public static Statement st = null;
	public static ResultSet rs = null;
	public static final String db_username = "UAP63";
	public static final String db_password = "1";
	public static final String db_ip = "192.168.1.100";
	public static final String db_name = "ORCL";
	
	private static String queryColSql = "SELECT column_name FROM user_tab_cols WHERE TABLE_name=?";
	private static List<String> colList = new ArrayList<String>();
	
	private DataSourceInfo ds = null;
	/**
	 * 保存查询值, key:pk, value:queryField
	 */
	private static Map<String,String> queryMap = new HashMap<String, String>();
	private static Map<String,String> updateMap = new HashMap<String, String>();
	private static String querySql = null;
	
	private static List<String> resultList = new ArrayList<String>();
	
	
	
	static {
		initData();
	}
	
	
	public static void initData(){
		colList.clear();
		queryMap.clear();
		updateMap.clear();
		resultList.clear();
		querySql = null;
	}
	
	
	public boolean updateToDB(String tableName, String pkField, String updateField){
		DataSourceInfo ds = new  DataSourceInfo();
		ds.setnUserName(db_username);
		ds.setnPassword(db_password);
		ds.setnIp(db_ip);
		ds.setnName(db_name);
		return updateToDB(tableName, pkField, updateField, ds);
	}
	
	/**
	 * <p>方法名称：updateToDB</p>
	 * <p>方法描述：更新数据到数据库</p>
	 * @param tableName		表名
	 * @param pkField		主键字段
	 * @param updateField	要更新的字段
	 * @author xiaoh
	 * @since  2014-9-4
	 * <p> history 2014-9-4 xiaoh  创建   <p>
	 */
	public boolean updateToDB(String tableName, String pkField, String updateField, DataSourceInfo ds){
		if(CommonUtil.isEmpty(tableName)||CommonUtil.isEmpty(updateField)){
			return false;
		}
		PreparedStatement pst = null;
		StringBuilder sb = new StringBuilder("UPDATE ").append(tableName);
		sb.append(" SET ").append(updateField).append("=? WHERE ").append(pkField).append("=?");
		String sql = sb.toString();
		
		conn = SimpleJdbc.getConnection(ds.getnUserName(), ds.getnPassword(),ds.getnIp(),ds.getnName());
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			if(updateMap!=null&&!updateMap.isEmpty()){
				Iterator<Entry<String, String>> it = updateMap.entrySet().iterator();
				String key = null;
				String value = null;
				int i = 1;
				while(it.hasNext()){
					i++;
					Entry<String,String> entry = it.next();
					key = entry.getKey().trim();
					value =entry.getValue().trim();
					pst.setString(1, value);
					pst.setString(2, key);
					pst.addBatch();
					if(i%500!=0&&it.hasNext()){
						continue;
					}
					pst.executeBatch();
					conn.commit();
					pst.clearBatch();
				}
				System.out.println(i);
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			SimpleJdbc.release(conn, pst, rs);
		}
		
		return true;
	}
	
	
	public Map<String, String> getQueryInfo(String tableName,String[] fields){
		DataSourceInfo ds = new  DataSourceInfo();
		ds.setnUserName(db_username);
		ds.setnPassword(db_password);
		ds.setnIp(db_ip);
		ds.setnName(db_name);
		return getQueryInfo(tableName, fields, ds);
	}
	/**
	 * <p>方法名称：getQueryInfo</p>
	 * <p>方法描述：获得查询的map,供查询和更新数据</p>
	 * @param tableName	表名
	 * @param fields	字段名(pk, queryField即拿去跟索引匹配的字段)
	 * @return
	 * @author xiaoh
	 * @since  2014-8-28
	 * <p> history 2014-8-28 xiaoh  创建   <p>
	 */
	public Map<String, String> getQueryInfo(String tableName,String[] fields, DataSourceInfo ds){
		return getQueryInfo(tableName, fields, null, ds);
	}
	
	/**
	 * <p>方法名称：getQueryInfo</p>
	 * <p>方法描述：获得查询的map,供查询和更新数据</p>
	 * @param tableName	表名
	 * @param fields	字段名(pk, queryField即拿去跟索引匹配的字段)
	 * @return
	 * @author xiaoh
	 * @since  2014-8-28
	 * <p> history 2014-8-28 xiaoh  创建   <p>
	 */
	public Map<String, String> getQueryInfo(String tableName,String[] fields,String wherePart, DataSourceInfo ds){
		if(CommonUtil.isEmpty(tableName)||fields==null||fields.length<=0){
			return null;
		}
		conn = SimpleJdbc.getConnection(ds.getnUserName(), ds.getnPassword(),ds.getnIp(),ds.getnName());
		buildQuerySql(tableName, fields, wherePart);
		try {
			if(CommonUtil.isEmpty(querySql)){
				throw new RuntimeException("查询语句有错，请修改！");
			}
			st = conn.createStatement();
			rs = st.executeQuery(querySql);
			String key = null;
			String value=null;
			while(rs.next()){
				key = rs.getString(fields[0]);
				value = rs.getString(fields[1]);
				if(CommonUtil.isEmpty(key)||CommonUtil.isEmpty(value)){
					continue;
				}
				queryMap.put(key, value);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			SimpleJdbc.release(conn, st, rs);
		}
		
		return queryMap;
	}
	
	private String buildQuerySql(String tableName, String[] fields, String wherePart) {
		StringBuilder sql = new StringBuilder("SELECT ");
		if(fields!=null && fields.length>0 && CommonUtil.isNotEmpty(tableName)){
			getColList(conn, tableName);
			for(int i=0;i<fields.length;i++){
				if(!colList.contains(fields[i].trim().toUpperCase())){
					throw new RuntimeException("没有找到该字段！【"+fields[i]+"】");
				}
			}
		}
		//build sql
		for(int i=0;i<fields.length;i++){
			sql.append(" "+fields[i]+",");
		}
		sql.delete(sql.length()-1, sql.length());
		sql.append(" FROM "+tableName).append(" ");
		if(CommonUtil.isNotEmpty(wherePart)){
			sql.append(wherePart);
		}
		querySql = sql.toString().toUpperCase();
		return querySql;
	}
	
	private void getColList(Connection conn, String tableName) {
		if(conn!=null){
			PreparedStatement pst = null;
			try {
				pst = conn.prepareStatement(queryColSql);
				pst.setString(1, tableName.trim().toUpperCase());
				rs = pst.executeQuery();
				String name = "";
				while(rs.next()){
					name = rs.getString(1);
					if(CommonUtil.isNotEmpty(name)){
						colList.add(name.trim().toUpperCase());
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally{
				rs = null;
				SimpleJdbc.release(null, pst, rs);
			}
			
		}
	}

	/**
	 * <p>方法名称：update</p>
	 * <p>方法描述：更新方法</p>
	 * @param fields  		构建索引, 参照的表的字段名
	 * @param tableName		构建索引, 查询的表名
	 * @param rebuild		构建索引, 是否重建索引
	 * @param queryField	查询索引, 检索字段
	 * @param resultField	查询索引, 检索返回结果值
	 * @author xiaoh
	 * @since  2014-8-28
	 * <p> history 2014-8-28 xiaoh  创建   <p>
	 */
	public boolean update(String[] fields, String tableName, boolean rebuild,String updateTbName,String queryField, String[] resultField){
		//构建索引
		buildIndex(fields, tableName, rebuild);
		Map<String,String> map = getQueryInfo(tableName, fields);
		
		buildUpdateMap(map, queryField, resultField[0]);
		return updateToDB(updateTbName, resultField[0], queryField);
	}
	
	
	/**
	 * <p>方法名称：buildUpdateMap</p>
	 * <p>方法描述：构建数据库更新的map</p>
	 * @param map			为主键和待模糊匹配的值的map
	 * @param queryField	查询字段名(索引中存在的)
	 * @param resultField	结果字段名(索引中存在的)
	 * @return
	 * @author xiaoh
	 * @since  2014-9-4
	 * <p> history 2014-9-4 xiaoh  创建   <p>
	 */
	public Map<String, String> buildUpdateMap(Map<String, String> map, String queryField, String resultField){
		if(map.isEmpty()){
			throw new RuntimeException("构建查询出错！");
		}
		
		Iterator<Entry<String, String>> it = map.entrySet().iterator();
		String key = null;
		String value = null;
		String result = null;
		Entry<String,String> entry = null;
		while(it.hasNext()){
			entry = it.next();
			key = entry.getKey();
			value =entry.getValue();
			if(CommonUtil.isNotEmpty(value)){
				result = queryOnly(value, queryField, resultField);
				if(CommonUtil.isNotEmpty(result)){
					updateMap.put(key, result);
				}
			}
		}
		return updateMap;
	}
	
	
	
	private String queryOnly(String keyword, String queryField, String resultField){
		if(resultList!=null){
			resultList.clear();
			resultList= null;
		}
		resultList = queryIndex(keyword, queryField, new String[]{resultField});
		if(resultList!=null&&resultList.size()==1){
			return resultList.get(0);
		}
		return "";
	}
	
	private List<String> queryIndex(String keyword, String queryField, String[] resultField){
		return FieldUpdater.queryByKeyword(keyword, queryField, resultField);
	}
	
	public boolean buildIndex(String[] fields, String tableName, boolean rebuild){
		try {
			FieldUpdater.buildLuceneIndex(fields, tableName, rebuild, null);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean buildIndex(String[] fields, String tableName, boolean rebuild, DataSourceInfo ds){
		try {
			FieldUpdater.buildLuceneIndex(fields, tableName, rebuild, null, ds);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean buildIndex(String[] fields, String tableName, boolean rebuild, String wherePart, DataSourceInfo ds){
		try {
			return FieldUpdater.buildLuceneIndex(fields, tableName, rebuild, wherePart, ds);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public DataSourceInfo getDs() {
		return ds;
	}
	public void setDs(DataSourceInfo ds) {
		this.ds = ds;
	}
	
}
