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
 * <p>Description: ͨ�õ��ֶθ�����, ����ģ��ƥ���ֶ�ֵ, ��������</p>
 * <p>
 * 		����
			PubFieldUpdater updater = new PubFieldUpdater();
			updater.buildIndex(new String[]{"pk_ywcz_b","v_zfczbm","v_zfczmc"},"crpas_ywcz_b", true);
			Map<String,String> map = updater.getQueryInfo("crpas_baobu_h",new String[]{"pk_baobu_h","v_zay"});
			updater.buildUpdateMap(map, "v_zfczmc", "v_zfczbm");
			updater.updateToDB("crpas_baobu_h", "pk_baobu_h", "c_aybm");
		��Ҫ���̷�Ϊ��
			1. ��������
			2. ��ȡ����������������(���в����������⣬�����е�һ������Ϊ����,����ܶ��ݴ��Ż�)
			3. ���������ݹ������������ݽṹ
			4. ��������
 * </p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
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
	 * �����ѯֵ, key:pk, value:queryField
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
	 * <p>�������ƣ�updateToDB</p>
	 * <p>�����������������ݵ����ݿ�</p>
	 * @param tableName		����
	 * @param pkField		�����ֶ�
	 * @param updateField	Ҫ���µ��ֶ�
	 * @author xiaoh
	 * @since  2014-9-4
	 * <p> history 2014-9-4 xiaoh  ����   <p>
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
	 * <p>�������ƣ�getQueryInfo</p>
	 * <p>������������ò�ѯ��map,����ѯ�͸�������</p>
	 * @param tableName	����
	 * @param fields	�ֶ���(pk, queryField����ȥ������ƥ����ֶ�)
	 * @return
	 * @author xiaoh
	 * @since  2014-8-28
	 * <p> history 2014-8-28 xiaoh  ����   <p>
	 */
	public Map<String, String> getQueryInfo(String tableName,String[] fields, DataSourceInfo ds){
		return getQueryInfo(tableName, fields, null, ds);
	}
	
	/**
	 * <p>�������ƣ�getQueryInfo</p>
	 * <p>������������ò�ѯ��map,����ѯ�͸�������</p>
	 * @param tableName	����
	 * @param fields	�ֶ���(pk, queryField����ȥ������ƥ����ֶ�)
	 * @return
	 * @author xiaoh
	 * @since  2014-8-28
	 * <p> history 2014-8-28 xiaoh  ����   <p>
	 */
	public Map<String, String> getQueryInfo(String tableName,String[] fields,String wherePart, DataSourceInfo ds){
		if(CommonUtil.isEmpty(tableName)||fields==null||fields.length<=0){
			return null;
		}
		conn = SimpleJdbc.getConnection(ds.getnUserName(), ds.getnPassword(),ds.getnIp(),ds.getnName());
		buildQuerySql(tableName, fields, wherePart);
		try {
			if(CommonUtil.isEmpty(querySql)){
				throw new RuntimeException("��ѯ����д����޸ģ�");
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
					throw new RuntimeException("û���ҵ����ֶΣ���"+fields[i]+"��");
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
	 * <p>�������ƣ�update</p>
	 * <p>�������������·���</p>
	 * @param fields  		��������, ���յı���ֶ���
	 * @param tableName		��������, ��ѯ�ı���
	 * @param rebuild		��������, �Ƿ��ؽ�����
	 * @param queryField	��ѯ����, �����ֶ�
	 * @param resultField	��ѯ����, �������ؽ��ֵ
	 * @author xiaoh
	 * @since  2014-8-28
	 * <p> history 2014-8-28 xiaoh  ����   <p>
	 */
	public boolean update(String[] fields, String tableName, boolean rebuild,String updateTbName,String queryField, String[] resultField){
		//��������
		buildIndex(fields, tableName, rebuild);
		Map<String,String> map = getQueryInfo(tableName, fields);
		
		buildUpdateMap(map, queryField, resultField[0]);
		return updateToDB(updateTbName, resultField[0], queryField);
	}
	
	
	/**
	 * <p>�������ƣ�buildUpdateMap</p>
	 * <p>�����������������ݿ���µ�map</p>
	 * @param map			Ϊ�����ʹ�ģ��ƥ���ֵ��map
	 * @param queryField	��ѯ�ֶ���(�����д��ڵ�)
	 * @param resultField	����ֶ���(�����д��ڵ�)
	 * @return
	 * @author xiaoh
	 * @since  2014-9-4
	 * <p> history 2014-9-4 xiaoh  ����   <p>
	 */
	public Map<String, String> buildUpdateMap(Map<String, String> map, String queryField, String resultField){
		if(map.isEmpty()){
			throw new RuntimeException("������ѯ����");
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
