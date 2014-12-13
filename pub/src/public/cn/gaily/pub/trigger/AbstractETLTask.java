package cn.gaily.pub.trigger;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
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

import cn.gaily.pub.util.CommonUtils;
import cn.gaily.simplejdbc.SimpleDSMgr;
import cn.gaily.simplejdbc.SimpleJdbc;
import cn.gaily.simplejdbc.SimpleSession;


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
	 * Ĭ�������ύ��С
	 */
	private int DEFAULT_BATCHSIZE =500;

	/**
	 * ������ѯ��С
	 */
	public int batchSize = DEFAULT_BATCHSIZE;
	
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
	
	private boolean isSyn = false;
	
	public void clear(){
		valueList.clear();
		pkValues.clear();
		colIndexMap.clear();
		colNameTypeMap.clear();
		
	}
	
	public Map<String,String> execute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName){
		return execute(srcMgr, tarMgr, tableName,  false);
	}
	
	/**
	 * <p>�������ƣ�execute</p>
	 * <p>����������ִ������Ԥ��</p>
	 * @param srcMgr
	 * @param tarMgr
	 * @param tableName
	 * @param isSyn 	�Ƿ�����ʷ����ͬ������
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  ����   <p>
	 */
	public Map<String,String> execute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName, boolean isSyn){
		if(srcMgr==null||tarMgr==null||CommonUtils.isEmpty(tableName)){
			throw new RuntimeException(tableName+"ִ��ǰ����Ԥ�ò�������");
		}
		
		setIsSyn(isSyn);
		
		clear();
		
		tarMgr = enableTrigger(tarMgr, tableName, DISABLE);  //ͣ�ô����� //TODO �������⣺��ִ���ڼ䣬���ݿ��ܶ�ʧ
		
		String pkName = null;
		Boolean canBatch =  null;
		String status = null;
		int round = 0;
		
		int add = 0;
		int update = 0;
		int delete = 0;
		boolean cbreak = false;
		int roundi = 0;
		int m = 0;
		while(true){
			
			long start = System.currentTimeMillis();
			
			System.out.println(++round);
			Map<String,Object> recvMap = null;
			if(isSyn){
				recvMap = queryTempData(tableName, srcMgr, roundi++, true);  //ͬ����ʷ����
				status = "1";
			}else{
				recvMap = queryTempData(tablePrefix+tableName, srcMgr, 0, false);
				status = (String) recvMap.get("status");
			}
			canBatch = (Boolean) recvMap.get("canBatch");
			pkName = (String) recvMap.get("pkName");
			
			if(canBatch==null||CommonUtils.isEmpty(status)){
				break;
			}
			if((Boolean)recvMap.get("break")){
				break;
			}
			AbstractETLTask task = null;
			if(canBatch){ //TODO ��BLOBʱ�򣬸ĳɲ���batch
				int type = Integer.valueOf(status);
				switch(type){
				case NEW:
					task = ETLInsertTask.getInstance();
					int i =  task.doBatch(srcMgr, tarMgr, tableName, pkName,  valueList, colNameTypeMap, canBatch);
					add +=i;
//					if(i==0&&isSyn==true){
//						m++;
//						if(m>=5){
//							cbreak = true;
//						}
//					}else{
//						cbreak = false;
//					}
					break;
				case UPDATE:
					task = ETLUpdateTask.getInstance();
					update += task.doBatch(srcMgr, tarMgr, tableName, pkName,  valueList, colNameTypeMap, canBatch);
					break;
				case DELETE:
					task = ETLDeleteTask.getInstance();
					delete += task.doBatch(srcMgr, tarMgr, tableName, pkName,  valueList, colNameTypeMap, canBatch);
					break;
				default:
					throw new RuntimeException(tableName+"����");
				}
				
			}else{
				Map<String,Object> map = null;
				int i=0;
				for(Iterator it= valueList.iterator();it.hasNext();){
					map = (Map<String, Object>) valueList.poll();
					if(map==null){
						break;
					}
					status = (String) map.get("ETLSTATUS");
					if(CommonUtils.isEmpty(status)){
						continue;
					}
					
					i++;
					
					int type = Integer.valueOf(status);
					switch(type){
					case NEW:
						task = ETLInsertTask.getInstance();
						task.doexecute(srcMgr, tarMgr, tableName, pkName,  map, colNameTypeMap);
						add++;
						break;
					case UPDATE:
						task = ETLUpdateTask.getInstance();
						task.doexecute(srcMgr, tarMgr, tableName, pkName,  map, colNameTypeMap);
						update++;
						break;
					case DELETE:
						task = ETLDeleteTask.getInstance();
						task.doexecute(srcMgr, tarMgr, tableName, pkName,  map, colNameTypeMap);
						delete++;
						break;
					default:
						throw new RuntimeException(tableName+"����");
					}
					
				}
			}
			
			long end = System.currentTimeMillis();
			System.out.println("costs: " +(end-start) +"ms");
		}
		
		enableTrigger(tarMgr, tableName, ENABLE);  //�ָ�������
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("add", String.valueOf(add));
		map.put("update", String.valueOf(update));
		map.put("delete", String.valueOf(delete));
		
		return map;
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
	public abstract int doBatch(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr,
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
		ByteArrayInputStream bis = null;
		try{
			if(value==null){
				 ipst.setNull(colIndexMap.get(colName), 0);
			 }else if("VARCHAR2".equals(colType)||"NVARCHAR2".equals(colType)||"CHAR".equals(colType)){
				ipst.setString(colIndexMap.get(colName), (String)value);
			}else if("DATE".equals(colType)){
				Date date = null;
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(CommonUtils.DATE_FORMATER_YYYY_MM_DD_TIME);
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
				 if((String.valueOf(value)).contains(".")){
					 ipst.setDouble(colIndexMap.get(colName), Double.valueOf((String.valueOf(value))));
				 }else{
					 ipst.setBigDecimal(colIndexMap.get(colName), new BigDecimal(String.valueOf(value)));
				 }
				 
			}else if("CLOB".equals(colType)){
				ipst.setClob(colIndexMap.get(colName), ((Clob)value).getCharacterStream(), ((Clob)value).length());
			}else if("BLOB".equals(colType)){
				Blob b = (Blob) value;
				bis = new ByteArrayInputStream(b.getBytes(1L, (int)b.length()));
				ipst.setBinaryStream(colIndexMap.get(colName), bis, bis.available());
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
		if(srcMgr==null||CommonUtils.isEmpty(tableName)){
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
	 * @param isSyn	�Ƿ�Ϊͬ����ʷ����
	 * @return canBatch �Ƿ�������
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  ����   <p>
	 */
	public Map<String,Object> queryTempData(String tableName,SimpleDSMgr srcMgr, int round, boolean isSyn) {
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		
		Boolean canBatch = true;
		
		valueList.clear();
//		pkValues.clear();
		
		colNameTypeMap = colNameTypeCache.get(tableName);
		if(colNameTypeMap==null||colNameTypeMap.isEmpty()){
			colNameTypeMap = getTabCols(srcMgr, tableName, isSyn);
			colNameTypeCache.put(tableName, colNameTypeMap);
		}
		if(CommonUtils.isEmpty(tableName)||srcMgr==null){
			return null;
		}
		Map<String, Object> valueMap = null;
		String pkName = null;
		Connection srcConn = srcMgr.getConnection();
		
		StringBuilder querySrcSb = new StringBuilder("SELECT ");
		if(colNameTypeMap.isEmpty()){
			return null;
		}
		if(!isSyn){
			for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
				querySrcSb.append("").append(((Entry<String,String>)it.next()).getKey()).append(",");
			}
			querySrcSb.deleteCharAt(querySrcSb.length()-1);
			querySrcSb.append(" FROM ").append(tableName).append(" WHERE ROWNUM<=").append(batchSize);
			querySrcSb.append(" ORDER BY ETLTS ASC");
		}else{
			for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
				querySrcSb.append("result.").append(((Entry<String,String>)it.next()).getKey()).append(",");
			}
			pkName = SimpleSession.getPkName(srcMgr, tableName.toUpperCase());
			querySrcSb.deleteCharAt(querySrcSb.length()-1);
			querySrcSb.append(" FROM (SELECT T.*,ROWNUM RN FROM (SELECT * FROM ").append(tableName).append(" A, ETL_PK_TEMP B WHERE B.TABLENAME='");
			querySrcSb.append(tableName.toUpperCase()).append("' AND A.").append(pkName).append("=B.PKVALUE) T");
			querySrcSb.append(" WHERE ROWNUM<=").append((round+1)*batchSize).append(") RESULT WHERE RN>").append(round*batchSize);
		}
		Statement st = null;
		ResultSet rs = null;
		String status = null;
		boolean canBreak = true;
		try {
			st  = srcConn.createStatement();
			rs 	= st.executeQuery(querySrcSb.toString());
			String colName = null;
			Object value = null;
			while(rs.next()){
				canBreak = false;
				valueMap= new HashMap<String,Object>();
				for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
					colName = ((Entry<String,String>)it.next()).getKey();
					value = rs.getObject(colName);
					if("ETLSTATUS".equals(colName)){
						if(status==null){
							status=(String) value;
						}else if(!status.equals(value)){
							canBatch = false;
							status=(String) value;
						}else{
							status = (String) value;
						}
					}
					if(CommonUtils.isEmpty(pkName)&&"ETLPKNAME".equals(colName)){
						pkName = (String) value;
					}
					if(CommonUtils.isEmpty(pkName)&&isSyn){
						pkName = SimpleSession.getPkName(srcMgr, tableName.toUpperCase());
					}

					valueMap.put(colName,value);
				}
				valueList.put(valueMap);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(tableName+"��ѯ���ݳ���"+e);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(tableName+"������ֵ����"+e);
		} finally{
			SimpleJdbc.release(null, st, rs);
			srcMgr.release(srcConn);
		}
		returnMap.put("canBatch", canBatch);
		returnMap.put("status", status);
		returnMap.put("pkName", pkName);
		returnMap.put("break", canBreak);
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
	public Map<String,String> getTabCols(SimpleDSMgr mgr, String tableName, boolean isSyn){
		
		if(mgr==null||CommonUtils.isEmpty(tableName)){
			throw new RuntimeException(tableName+"��ȡ���в�������");
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
			pst.setString(1, tableName);
			rs = pst.executeQuery();
			String colName = null;
			String colType = null;
			while(rs.next()){
				colName = rs.getString(1);
				colType = rs.getString(2);
				if(CommonUtils.isNotEmpty(colName)){
					result.put(colName, colType);
				}
				if("BLOB".equalsIgnoreCase(colType)||"CLOB".equalsIgnoreCase(colType)){
					//TODO set batch size to 50
					this.setBatchSize(50);
				}else{
					this.setBatchSize(DEFAULT_BATCHSIZE);
				}
			}
			if(!isSyn){
				result.put("ETLTS", "VARCHAR2");
				result.put("ETLSTATUS", "CHAR");
				result.put("ETLPKNAME", "VARCHAR2");
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
	 * @param type 			1������; 0��ͣ��; 2�����ü��; 3��ͣ�ü��
	 * @param needRelese	�Ƿ���Ҫ�ͷ�
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  ����   <p>
	 */
	public SimpleDSMgr enableTrigger(SimpleDSMgr mgr, String tableName, int type){
		
		if(mgr==null||CommonUtils.isEmpty(tableName)||(type!=0&&type!=1&&type!=2&&type!=3)){
			throw new RuntimeException(tableName+"ͣ���ô�������������");
		}
		Connection conn = mgr.getConnection();
		
		ensureMgrTable(mgr,"XFL_TABSTATUS");
		
		String sql = null;
		String tsql1 = "ALTER TRIGGER XFL_"+tableName+"_1 DISABLE";
		String tsql2 = "ALTER TRIGGER XFL_"+tableName+"_2 DISABLE";
		String tsql3 = "ALTER TRIGGER XFL_"+tableName+"_3 DISABLE";
		
		String tsql11 = "ALTER TRIGGER XFL_"+tableName+"_1 ENABLE";
		String tsql22 = "ALTER TRIGGER XFL_"+tableName+"_2 ENABLE";
		String tsql33 = "ALTER TRIGGER XFL_"+tableName+"_3 ENABLE";
		
		
		Statement st = null;
		try {
			conn.setAutoCommit(false);
			st = conn.createStatement();
			if(type==0){
				sql = "UPDATE XFL_TABSTATUS SET STATUS=0 WHERE TABLENAME='"+tableName+"'";
				st.executeUpdate(sql);
				st.executeUpdate(tsql1);
				st.executeUpdate(tsql2);
				st.executeUpdate(tsql3);
			}else if(type==1){
				sql = "UPDATE XFL_TABSTATUS SET STATUS=1 WHERE TABLENAME='"+tableName+"'";
				st.executeUpdate(sql);
				st.executeUpdate(tsql11);
				st.executeUpdate(tsql22);
				st.executeUpdate(tsql33);
			}else if(type==2){
				sql = "UPDATE XFL_TABSTATUS SET STATUS=2 WHERE TABLENAME='"+tableName+"'";
				st.executeUpdate(sql);
				st.executeUpdate(tsql11);
				st.executeUpdate(tsql22);
				st.executeUpdate(tsql33);
			}else if(type==3){
				sql = "UPDATE XFL_TABSTATUS SET STATUS=3 WHERE TABLENAME='"+tableName+"'";
				st.executeUpdate(sql);
				st.executeUpdate(tsql1);
				st.executeUpdate(tsql2);
				st.executeUpdate(tsql3);
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
//			throw new RuntimeException("ͣ�����ô���������"+e);
		}finally{
			SimpleJdbc.release(null, st, null);
			mgr.release(conn);
		}
		return mgr;
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
		if(CommonUtils.isEmpty(pkName)||CommonUtils.isEmpty(tableName)||conn==null||(type!=1&&type!=2&&type!=3)){
			throw new RuntimeException(tableName+"ɾ����ʱ�����ݴ����������Ϊ��");
		}
		
		if(isSyn){   		//Ϊͬ��������ִ�и÷���
			return conn;
		}
		if(insertPks.size()<=0){
			return conn;
		}
		
		int delSize = 500;
		
		if(delSize>batchSize){
			delSize = batchSize;
		}
		
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
					if(CommonUtils.isEmpty(value)){
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
					sb.append(" AND ROWNUM<=").append(delSize);
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
			throw new RuntimeException(tableName+"ɾ����ʱ�������쳣��"+e);
		} finally{
			SimpleJdbc.release(null, st, null);
		}
		return conn;
	}
	
	private boolean ensureMgrTable(SimpleDSMgr mgr , String tableName){
		if(mgr==null){
			throw new RuntimeException(tableName+"���������Դ");
		}
		Connection conn = mgr.getConnection();
		if(conn==null){
			return false;
		}
		if(CommonUtils.isEmpty(tableName)){
			throw new RuntimeException(tableName+"�봫����Ч�ı���");
		}
		String sql = "SELECT COUNT(1) FROM USER_TABLES WHERE TABLE_NAME=?";
		Statement st = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, tableName.toUpperCase().trim());
			rs = pst.executeQuery();
			st = conn.createStatement();
			if(rs.next()&&"1".equals(rs.getString(1))){
				return true;  //fix ��ʱ�����,���ܺ�������,��ɾ��ֱ�ӷ���
			}
			st.executeUpdate("CREATE TABLE "+tableName+"(TABLENAME VARCHAR2(30),STATUS CHAR(1))");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			SimpleJdbc.release(null, pst, rs);
			mgr.release(conn);
		}
		return false;
	}
	
	
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public void setIsSyn(boolean isSyn) {
		this.isSyn = isSyn;
	}
	
	
}
