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
import java.util.concurrent.ArrayBlockingQueue;

import cn.gaily.pub.util.CommonUtil;
import cn.gaily.simplejdbc.SimpleDSMgr;
import cn.gaily.simplejdbc.SimpleJdbc;


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
	 * ���������ʶ 1
	 */
	public final int NEW = 1;
	/**
	 * ���±�ʶ 2
	 */
	public final int UPDATE = 2;
	/**
	 * ɾ����ʶ 3
	 */
	public final int DELETE = 3;
	
	/**
	 * ����
	 */
	public final int ENABLE = 1;
	/**
	 * ͣ��
	 */
	public final int DISABLE = 0;
	
	/**
	 * ������ѯ��С
	 */
	public final int batchSize = 800;
	
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
	 * ������ֵ���У�Map<String,Object> key:����, value:��ֵ
	 */
	public ArrayBlockingQueue<Map<String,Object>> 	valueList 	= new ArrayBlockingQueue<Map<String,Object>>(batchSize,true);
	/**
	 * ������pkֵ���Ա���ɾ����
	 */
	public List<String> 			 	pkValues 		= new ArrayList<String>();
	/**
	 * ���������Ͷ�Ӧ��ϵ������������
	 */
	public Map<String,Map<String,String>> colNameTypeCache	= new HashMap<String,Map<String,String>>();
	
	/**
	 * ���������Ͷ�Ӧ��ϵ
	 */
	public Map<String,String> colNameTypeMap = new HashMap<String,String>();
	
	/**
	 * �к�����ӳ��map,ֵ������ֵ
	 */
	public Map<String,Integer> 	 colIndexMap 	= new HashMap<String,Integer>();
	
	
	public void clear(){
		valueList.clear();
		pkValues.clear();
		colIndexMap.clear();
		colNameTypeMap.clear();
		
	}
	
	/**
	 * <p>�������ƣ�execute</p>
	 * <p>����������ִ������Ԥ��</p>
	 * @param srcMgr
	 * @param tarMgr
	 * @param tableName
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  ����   <p>
	 */
	public String execute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName){
		if(srcMgr==null||tarMgr==null||CommonUtil.isEmpty(tableName)){
			throw new RuntimeException("ִ��ǰ����Ԥ�ò�������");
		}
		
		clear();
		
		enableTrigger(tarMgr, tableName, 0);  //ͣ�ô����� //TODO �������⣺��ִ���ڼ䣬���ݿ��ܶ�ʧ
		
		String pkName = null;
		Boolean canBatch =  null;
		String status = null;
		int round = 0;
		while(true){
			System.out.println(++round);
			Map<String,Object> recvMap = queryTempData(tableName, srcMgr, batchSize, 0); //����ÿ�ű��pk��Ψһ��
			canBatch = (Boolean) recvMap.get("canBatch");
			pkName = (String) recvMap.get("pkName");
			status = (String) recvMap.get("status");
			
			if(canBatch==null||CommonUtil.isEmpty(pkName)||CommonUtil.isEmpty(status)){
				break;
			}
			
			AbstractETLTask task = null;
			if(canBatch){ //TODO ��BLOBʱ�򣬸ĳɲ���batch
				int type = Integer.valueOf(status);
				switch(type){
				case NEW:
					task = ETLInsertTask.getInstance();
					break;
				case UPDATE:
					task = ETLUpdateTask.getInstance();
					break;
				case DELETE:
					task = ETLDeleteTask.getInstance();
					break;
				default:
					throw new RuntimeException("����");
				}
				task.doBatch(srcMgr, tarMgr, tableName, pkName,  valueList, colNameTypeMap, canBatch);
			}else{
				Map<String,Object> map = null;
				int i=0;
				for(Iterator it= valueList.iterator();it.hasNext();){
					map = (Map<String, Object>) valueList.poll();
					if(map==null){
						break;
					}
					status = (String) map.get("ETLSTATUS");
					if(CommonUtil.isEmpty(status)){
						continue;
					}
					
					i++;
					
					int type = Integer.valueOf(status);
					switch(type){
					case NEW:
						task = ETLInsertTask.getInstance();
						System.out.println("insert: "+(round-1)+i); //TODO ������
						break;
					case UPDATE:
						task = ETLUpdateTask.getInstance();
						System.out.println("update: "+(round-1)+i); //TODO ������
						break;
					case DELETE:
						task = ETLDeleteTask.getInstance();
						System.out.println("delete: "+(round-1)+i); //TODO ������
						break;
					default:
						throw new RuntimeException("����");
					}
					
					task.doexecute(srcMgr, tarMgr, tableName, pkName,  map, colNameTypeMap);
				}
			}
		}
		
		enableTrigger(tarMgr, tableName, 1);  //�ָ�������
		
//		delTempData(srcMgr, tableName, null);
		
		//ͳһ����
//		for(int i=0;i<srcMgr.conns.size();i++){
//			try {
//				srcMgr.conns.get(i).commit();
//				tarMgr.conns.get(i).commit();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
		
		return pkName;
	}
	
	/**
	 * <p>�������ƣ�doBatch</p>
	 * <p>����������ִ������</p>
	 * @param srcMgr
	 * @param tarMgr
	 * @param tableName
	 * @param pkName
	 * @param valueList
	 * @param colNameTypeMap
	 * @param canBatch
	 * @author xiaoh
	 * @since  2014-11-5
	 * <p> history 2014-11-5 xiaoh  ����   <p>
	 */
	public abstract void doBatch(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr,
								 String tableName, String pkName,
								 ArrayBlockingQueue<Map<String, Object>> valueList,
								 Map<String, String> colNameTypeMap, Boolean canBatch);

	/**
	 * <p>�������ƣ�doexecute</p>
	 * <p>����������ִ��DDL����</p>
	 * @param srcMgr		Դ���ӳ�
	 * @param tarMgr		Ŀ�����ӳ�
	 * @param tableName		����
	 * @param pkName		Ψһ��ʶ����
	 * @param valueMap		ֵMAP
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  ����   <p>
	 */
	public abstract void doexecute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName, String pkName, Map<String,Object> valueMap, Map<String,String> colNameTypeMap);
	
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
		if(ignoreCols!=null&&ignoreCols.contains(colName)){
			return ipst;
		}
		try{
			if(value==null){
				 ipst.setNull(colIndexMap.get(colName), 0);
			 }else if("VARCHAR2".equals(colType)||"NVARCHAR2".equals(colType)||"CHAR".equals(colType)){
				ipst.setString(colIndexMap.get(colName), (String)value);
			}else if("DATE".equals(colType)){
				Date date = null;
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(CommonUtil.DATE_FORMATER_YYYY_MM_DD_TIME);
					String v = String.valueOf(value).substring(0,19);
					date = new Date(sdf.parse(v).getTime());  //TODO ��ʱ��
				} catch (ParseException e) {
					e.printStackTrace();
					throw new RuntimeException("����ת������"+e);
				}
				ipst.setDate(colIndexMap.get(colName), date);
			}else if("FLOAT".equals(colType)){
				ipst.setFloat(colIndexMap.get(colName), Float.valueOf((String) value));
			}
			 else if("NUMBER".equals(colType)){
				 if(((String)value).contains(".")){
					 ipst.setDouble(colIndexMap.get(colName), Double.valueOf((String) value));
				 }else{
					 ipst.setInt(colIndexMap.get(colName), Integer.valueOf((String)value));
				 }
				 
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
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  ����   <p>
	 */@Deprecated
	public void delTempData(SimpleDSMgr srcMgr, String tableName, String pkName) {//TODO ������գ��ڲ�����ʱ�򣬱��е����ݿ����޸�
		if(srcMgr==null||CommonUtil.isEmpty(tableName)){
			return ;
		}
		Connection srcConn = srcMgr.getConnection();
		Statement delSt = null;
		StringBuilder delSb = new StringBuilder("DELETE FROM ");
		delSb.append(tablePrefix).append(tableName);
		try {
			delSt = srcConn.createStatement();
			delSt.execute(delSb.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("ɾ�����ݳ���");
		}finally{
			SimpleJdbc.release(null, delSt, null);
			srcMgr.release(srcConn);
		}
	}

	


	/**
	 * <p>�������ƣ�queryTempData</p>
	 * <p>������������ѯ��ʱ���е�����</p>
	 * @param tableName
	 * @param srcMgr
	 * @return canBatch �Ƿ�������
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  ����   <p>
	 */
	public Map<String,Object> queryTempData(String tableName,SimpleDSMgr srcMgr, int batchsize, int round) {
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		
		Boolean canBatch = true;
		
		valueList.clear();
//		pkValues.clear();
		
		colNameTypeMap = colNameTypeCache.get(tableName);
		if(colNameTypeMap==null||colNameTypeMap.isEmpty()){
			colNameTypeMap = getTabCols(srcMgr, tableName);
			colNameTypeCache.put(tableName, colNameTypeMap);
		}
		if(CommonUtil.isEmpty(tableName)||srcMgr==null){
			return null;
		}
		Map<String, Object> valueMap = null;
		String pkName = null;
		Connection srcConn = srcMgr.getConnection();
		
		StringBuilder querySrcSb = new StringBuilder("SELECT ");

		for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
			querySrcSb.append("").append(((Entry<String,String>)it.next()).getKey()).append(",");
		}
		querySrcSb.deleteCharAt(querySrcSb.length()-1);
//		querySrcSb.append(" FROM( SELECT T.*, ROWNUM RN FROM( SELECT * FROM ").append(tablePrefix).append(tableName);
//		querySrcSb.append(" ORDER BY ETLTS ASC) T WHERE ROWNUM<=").append(batchsize*(round+1)).append(") RESULT");
//		querySrcSb.append(" WHERE RN>=").append(batchsize*round);
//		querySrcSb.append(" ORDER BY RESULT.ETLTS ASC");
		querySrcSb.append(" FROM ").append(tablePrefix).append(tableName).append(" WHERE ROWNUM<=").append(batchSize);
		querySrcSb.append(" ORDER BY ETLTS ASC");
		
		Statement st = null;
		ResultSet rs = null;
		String status = null;
		try {
			st  = srcConn.createStatement();
			rs 	= st.executeQuery(querySrcSb.toString());
			String colName = null;
			String value = null;
			while(rs.next()){
				valueMap= new HashMap<String,Object>();
				for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
					colName = ((Entry<String,String>)it.next()).getKey();
					value = rs.getString(colName);
					if("ETLSTATUS".equals(colName)){
						if(status==null){
							status=value;
						}else if(!status.equals(value)){
							canBatch = false;
							status=value;
						}else{
							status = value;
						}
					}
					if(CommonUtil.isEmpty(pkName)&&"ETLPKNAME".equals(colName)){
						pkName = value;
					}
					valueMap.put(colName,value);
				}
				valueList.put(valueMap);
			}
			//��ȡpkֵ
//			Map<String,String> map = null;
//			for(Iterator it= valueList.iterator();it.hasNext();){
//				map = (Map<String,String>)it.next();
//				if(CommonUtil.isEmpty(pkName)){
//					pkName = (String)map.get("ETLPKNAME");
//				}
//				pkValues.add(map.get(pkName));
//			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("��ѯ���ݳ���"+e);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException("������ֵ����"+e);
		} finally{
			SimpleJdbc.release(null, st, rs);
			srcMgr.release(srcConn);
		}
		
		returnMap.put("canBatch", canBatch);
		returnMap.put("status", status);
		returnMap.put("pkName", pkName);
		
		return returnMap;
	}
	
	
	/**
	 * <p>�������ƣ�getTabCols</p>
	 * <p>������������ȡ���������</p>
	 * @param mgr		    ����
	 * @param tableName	    ����
	 * @return Map<String,String>  key:�ֶ���, value:�ֶ����ݿ�����
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  ����   <p>
	 */
	public Map<String,String> getTabCols(SimpleDSMgr mgr, String tableName){
		
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
				if("BLOB".equalsIgnoreCase(colType)||"CLOB".equalsIgnoreCase(colType)){
					//TODO no batch
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			SimpleJdbc.release(null, pst, rs);
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
	 * @param type 			1������; 0��ͣ��
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
		
		
		String sql = null;
		String sql1 = null;
		if(type==0){
			sql = "ALTER TABLE "+tableName+" DISABLE ALL TRIGGERS";
			sql1 = "UPDATE XFL_TABSTATUS SET STATUS=0 WHERE TABLENAME='"+tableName+"'";
		}else if(type==1){
			sql = "ALTER TABLE "+tableName+" ENABLE ALL TRIGGERS";
			sql1 = "UPDATE XFL_TABSTATUS SET STATUS=1 WHERE TABLENAME='"+tableName+"'";
		}
		Statement st = null;
		try {
			st = conn.createStatement();
			st.execute(sql);
			st.execute(sql1);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("ͣ�����ô���������"+e);
		}finally{
			SimpleJdbc.release(null, st, null);
			mgr.release(conn);
		}
		
//		String sql = "UPDATE "+mgrTriggerTabName+" A SET A.STATUS=? WHERE TABLENAME=?";
//		
//		PreparedStatement pst = null;
//		try {
//			pst = conn.prepareStatement(sql);
//			pst.setString(1, String.valueOf(type));
//			pst.setString(2, tableName.toUpperCase().trim());
//			pst.execute();
//		} catch (SQLException e) {
//			e.printStackTrace();
//			throw new RuntimeException("ͣ�����ô���������"+e);
//		} finally{
//			JdbcUtils.release(null, pst, null);
//			mgr.release(conn);
//		}
	}
	
	/**
	 * <p>�������ƣ�delTemp</p>
	 * <p>����������ɾ����ʱ������</p>
	 * @param pkName		�����ֶ�
	 * @param insertPks		pkֵ
	 * @param tableName		����
	 * @author xiaoh
	 * @since  2014-11-7
	 * <p> history 2014-11-7 xiaoh  ����   <p>
	 */
	protected Connection delTemp(String pkName, List<String> insertPks, String tableName, Connection conn, int type, boolean batch) {
		if(CommonUtil.isEmpty(pkName)||CommonUtil.isEmpty(tableName)||conn==null||(type!=1&&type!=2&&type!=3)){
			throw new RuntimeException("ɾ����ʱ�����ݴ����������Ϊ��");
		}
		if(insertPks.size()<=0){
			return conn;
		}
		
		int delSize = batchSize;
		
		StringBuilder sb = null;
		String value = null;
		Statement st = null;
		
		int round = insertPks.size()/delSize + 1;
		
		int deleteCount = 0;
		try {
			st = conn.createStatement();
			
			for(int i=0;i<round;i++){
				sb = new StringBuilder("DELETE FROM ");
				sb.append(tableName).append(" WHERE ").append(pkName);
				sb.append(" IN('");
				for(int j=0;j<delSize&&insertPks.size()>0;j++){
					value = insertPks.remove(0);
					if(CommonUtil.isEmpty(value)){
						continue;
					}else{
						sb.append(value).append("','");
					}
				}
				
				sb.deleteCharAt(sb.length()-1);
				sb.deleteCharAt(sb.length()-1);
				sb.append(")");
				if(!sb.toString().contains("(")){
					return conn;
				}
				sb.append(" AND ETLSTATUS='").append(String.valueOf(type)).append("'");
				if(batch){
					sb.append(" AND ROWNUM<=").append(batchSize);
				}else{
					sb.append(" AND ROWNUM<=").append(1);  //fix 141109�޶���Χɾ��
				}
				System.out.println(sb);
				int cc = st.executeUpdate(sb.toString());
				System.out.println("delete "+cc+ " record in table:"+ tableName);
				deleteCount+=cc;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("ɾ����ʱ�������쳣��"+e);
		} finally{
			SimpleJdbc.release(null, st, null);
		}
		return conn;
	}
	
	
}
