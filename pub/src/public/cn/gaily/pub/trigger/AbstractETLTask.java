package cn.gaily.pub.trigger;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.gaily.pub.util.CommonUtil;
import cn.gaily.pub.util.JdbcUtils;

/**
 * <p>Title: TriggerBaseTask</P>
 * <p>Description: ͬ�����ݿ�����ɾ����ִ�л�����</p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-28
 */
public abstract class AbstractETLTask {

	/**
	 * �����������
	 */
	protected String mgrTriggerTabName = "XFL_TABSTATUS";
	
	/**
	 * ��ʱ��ǰ׺
	 */
	protected String tablePrefix = "XFL_";
	
	/**
	 * �����ֶ���map��key:������д,value:�ֶ���,�ֶ�����map
	 */
	public Map<String,Map<String,String>> tabColMap = new HashMap<String,Map<String,String>>();
	
	/**
	 * ������ֵ��Map<String,Object> key:����, value:��ֵ
	 */
	public List<Map<String,Object>> 	valueList 		= new ArrayList<Map<String,Object>>();
	/**
	 * ������pkֵ���Ա���ɾ����
	 */
	public List<String> 			 	pkValues 		= new ArrayList<String>();
	/**
	 * ���������Ͷ�Ӧ��ϵ
	 */
	public Map<String,String> 		 	colNameTypeMap	= new HashMap<String,String>();
	/**
	 * �к�����ӳ��map,ֵ������ֵ
	 */
	public Map<String,Integer> 	 	 	colIndexMap 	= new HashMap<String,Integer>();
	
	
	public void clear(){
		valueList.clear();
		pkValues.clear();
		colNameTypeMap.clear();
		colIndexMap.clear();
		
	}
	
	/**
	 * <p>�������ƣ�dealAdd</p>
	 * <p>����������������������</p>
	 * @param tableName ����
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  ����   <p>
	 */
	public abstract void dealAdd(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName);
	
	/**
	 * <p>�������ƣ�dealUpdate</p>
	 * <p>���������������������</p>
	 * @param tableName ����
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  ����   <p>
	 */
	public abstract void dealUpdate(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName);
	
	/**
	 * <p>�������ƣ�dealDel</p>
	 * <p>��������������ɾ������</p>
	 * @param tableName ����
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  ����   <p>
	 */
	public abstract void dealDel(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName);

	
	
	
	/**
	 * <p>�������ƣ�setValues</p>
	 * <p>����������������ֵ</p>
	 * @param ipst		statement
	 * @param colName	�ֶ���
	 * @param colType	�ֶ�����
	 * @param value		�ֶ�ֵ
	 * @param ignoreCols	�����ֶ�
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  ����   <p>
	 */
	public PreparedStatement setValues(PreparedStatement ipst, String colName, String colType,Object value, List<String> ignoreCols) {
		if(ignoreCols.contains(colName)){
			return ipst;
		}
		try{
			if("VARCHAR2".equals(colType)||"NVARCHAR2".equals(colType)||"CHAR".equals(colType)){
				ipst.setString(colIndexMap.get(colName), (String)value);
			}else if("DATE".equals(colType)){
				Date date = null;
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(CommonUtil.DATE_FORMATER_YYYY_MM_DD_TIME);
					String v = String.valueOf(value).substring(0,19);
					date = new Date(sdf.parse(v).getTime());  //TODO
				} catch (ParseException e) {
					e.printStackTrace();
					throw new RuntimeException("����ת������"+e);
				}
				ipst.setDate(colIndexMap.get(colName), date);
			}else if("FLOAT".equals(colType)){
				ipst.setFloat(colIndexMap.get(colName), Float.valueOf((String) value));
			}
			 else if("NUMBER".equals(colType)){
				ipst.setDouble(colIndexMap.get(colName), Double.valueOf((String) value));
			}else if("CLOB".equals(colType)){
				ipst.setClob(colIndexMap.get(colName), ((Clob)value).getCharacterStream(), ((Clob)value).length());
			}else if("BLOB".equals(colType)){
				Blob b = (Blob) value;
				ipst.setBlob(colIndexMap.get(colName), b.getBinaryStream(), b.length());
			}
	//		else if("TIMESTAMP(6)".equals(colType)){
	//			String timevalue = ((String) value).substring(0, 10);
	//			ipst.setTime(colIndexMap.get(colName), Time.valueOf(timevalue));
	//		}
		}catch(SQLException e){
			e.printStackTrace();
			throw new RuntimeException("����preaparedStatementֵ����"+e);
		}
		return ipst;
	}
	
	/**
	 * <p>�������ƣ�delTempData</p>
	 * <p>����������ɾ��Դ����ʱ������</p>
	 * @param srcMgr
	 * @param tableName
	 * @param pkName		�����ֶ�����
	 * @param type			1������  2���޸�  3��ɾ��	
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  ����   <p>
	 */
	public void delTempData(SimpleDSMgr srcMgr, String tableName, String pkName, int type) {
		if(srcMgr==null||CommonUtil.isEmpty(tableName)||CommonUtil.isEmpty(pkName)){
			return ;
		}
		Connection srcConn = srcMgr.getConnection();
		Statement delSt = null;
		StringBuilder delSb = new StringBuilder("DELETE FROM ");
		delSb.append(tablePrefix).append(tableName);
		delSb.append(" WHERE ").append(pkName).append(" IN(");
		for(int i=0;i<pkValues.size();i++){
			delSb.append("'").append(pkValues.get(i)).append("',");
		}
		delSb.deleteCharAt(delSb.length()-1);
		delSb.append(") AND ETLSTATUS=").append(type);
		try {
			delSt = srcConn.createStatement();
			delSt.execute(delSb.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("ɾ�����ݳ���");
		}finally{
			JdbcUtils.release(null, delSt, null);
			srcMgr.release(srcConn);
		}
	}

	


	/**
	 * <p>�������ƣ�queryTempData</p>
	 * <p>������������ѯ���ݣ����������ֶ�</p>
	 * @param tableName
	 * @param srcMgr
	 * @param type		1������  2���޸�  3��ɾ��
	 * @return
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  ����   <p>
	 */
	public String queryTempData(String tableName,SimpleDSMgr srcMgr, int type) {
		
		if(CommonUtil.isEmpty(tableName)||srcMgr==null||(type!=1&&type!=2&&type!=3)){
			return null;
		}
		Map<String, Object> valueMap = null;
		String pkName = null;
		Connection srcConn = srcMgr.getConnection();
		
		StringBuilder querySrcSb = new StringBuilder("SELECT ");

		for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
			querySrcSb.append(((Entry<String,String>)it.next()).getKey()).append(",");
		}
		querySrcSb.deleteCharAt(querySrcSb.length()-1);
		querySrcSb.append(" FROM ").append(tablePrefix).append(tableName);
		querySrcSb.append(" WHERE ETLSTATUS=").append(type);
		querySrcSb.append(" ORDER BY ETLTS ASC");
		
		Statement st = null;
		ResultSet rs = null;
		try {
			st  = srcConn.createStatement();
			rs 	= st.executeQuery(querySrcSb.toString());
			String colName = null;
			while(rs.next()){
				valueMap= new HashMap<String,Object>();
				for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
					colName = ((Entry<String,String>)it.next()).getKey();
					valueMap.put(colName,rs.getString(colName));
				}
				valueList.add(valueMap);
			}
			//��ȡpkֵ
			for(int i=0;i<valueList.size();i++){
				pkName = (String) valueList.get(i).get("ETLPKNAME");
				pkValues.add((String) valueList.get(i).get(pkName));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("��ѯ���ݳ���"+e);
		} finally{
			JdbcUtils.release(null, st, rs);
			srcMgr.release(srcConn);
		}
		return pkName;
	}
	
	
	/**
	 * <p>�������ƣ�getTabCols</p>
	 * <p>������������ȡ���������</p>
	 * @param mgr		    ����
	 * @param tableName	    ����
	 * @param needRelease �Ƿ���Ҫ�ͷ�����
	 * @return Map<String,String>  key:�ֶ���, value:�ֶ����ݿ�����
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  ����   <p>
	 */
	public Map<String,String> getTabCols(SimpleDSMgr mgr, String tableName, boolean needRelease){
		
		if(mgr==null||CommonUtil.isEmpty(tableName)){
			throw new RuntimeException("��ȡ���в�������");
		}
		Connection conn = mgr.getConnection();
		tableName = tableName.toUpperCase().trim();
		Map<String,String> result = tabColMap.get(tableName);
		if(result!=null&&result.size()>0){
			return result;
		}
		
		result = new HashMap<String,String>();

		String sql = "SELECT COLUMN_NAME,DATA_TYPE FROM USER_TAB_COLS WHERE TABLE_NAME=?";
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, tablePrefix+tableName);
			rs = pst.executeQuery();
			String colName = null;
			String colType = null;
			while(rs.next()){
				colName = rs.getString(1);
				colType = rs.getString(2);
				if(CommonUtil.isNotEmpty(colName)){
					result.put(colName, colType);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JdbcUtils.release(null, pst, rs);
			mgr.release(conn);
		}
		tabColMap.put(tableName, result);
		return result;
	}
	
	/**
	 * <p>�������ƣ�enableTrigger</p>
	 * <p>����������ͣ�����ô�����</p>
	 * @param mgr 			����Դ
	 * @param tableName		����
	 * @param type 			1������; 0������
	 * @param needRelese	�Ƿ���Ҫ�ͷ�
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  ����   <p>
	 */
	public void enableTrigger(SimpleDSMgr mgr, String tableName, int type){
		
		if(mgr==null||CommonUtil.isEmpty(tableName)||(type!=0&&type!=1)){
			throw new RuntimeException("ͣ���ô�������������");
		}
		Connection conn = mgr.getConnection();
		String sql = "UPDATE "+mgrTriggerTabName+" A SET A.STATUS=? WHERE TABLENAME=?";
		
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, String.valueOf(type));
			pst.setString(2, tableName.toUpperCase().trim());
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("ͣ�����ô���������"+e);
		} finally{
			JdbcUtils.release(null, pst, null);
			mgr.release(conn);
		}
	}
}
