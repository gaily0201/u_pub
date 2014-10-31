package cn.gaily.pub.trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.gaily.pub.util.CommonUtil;
import cn.gaily.simplejdbc.SimpleJdbc;
import cn.gaily.simplejdbc.SimpleDSMgr;

/**
 * <p>Title: ETLInsertTask</P>
 * <p>Description: ���ݽ�����������</p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-29
 */
public class ETLInsertTask extends AbstractETLTask{

	
	public static ETLInsertTask instance = null;
	
	
	public PreparedStatement pst = null;
	
	private ETLInsertTask(){}
	
	public static ETLInsertTask getInstance(){
		if(instance ==null){
			instance = new ETLInsertTask();
		}
		return instance;
	}
	
	/**
	 * <p>�������ƣ�dealAdd</p>
	 * <p>����������������������</p>
	 * @param tableName ����
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  ����   <p>
	 */
	@Override
	public void doexecute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName, String pkName, Map<String,Object> valueMap, Map<String,String> colNameTypeMap){
		
		if(srcMgr==null||tarMgr==null||CommonUtil.isEmpty(tableName)){
			throw new RuntimeException("�������ݿ������������");
		}

		//1��ͣ��Ŀ��ⴥ����
//		enableTrigger(tarMgr, tableName, DISABLE);
		
		//2��ִ��Ŀ������ݿ����
		doAddInsert(tarMgr, tableName, valueMap, colNameTypeMap);
		
		//3������Ŀ��ⴥ����
//		enableTrigger(tarMgr, tableName, ENABLE);
	}
	
	/**
	 * <p>�������ƣ�doAddInsert</p>
	 * <p>����������ִ��Ŀ������ݿ����</p>
	 * @param tarMgr
	 * @param tableName
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  ����   <p>
	 */
	private void doAddInsert(SimpleDSMgr tarMgr, String tableName, Map<String,Object> valueMap, Map<String,String> colNameTypeMap) {
		if(tarMgr==null||CommonUtil.isEmpty(tableName)||valueMap.isEmpty()){
			return ;
		}
		StringBuilder tarSb = new StringBuilder();
		tarSb.append("INSERT INTO ").append(tableName).append("(");
		String colName = null;
		String colType = null;
		Entry<String,String> entry =  null;
		
		int n=0;
		for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
			entry = (Entry<String, String>) it.next();
			colName = entry.getKey();
			if(!"ETLSTATUS".equalsIgnoreCase(colName)&&!"ETLPKNAME".equalsIgnoreCase(colName)&&!"ETLTS".equalsIgnoreCase(colName)){
				tarSb.append(colName).append(",");
				colIndexMap.put(colName, ++n);
			}
		}
		tarSb.deleteCharAt(tarSb.length()-1);
		tarSb.append(") VALUES(");
		for(int i=0;i<colNameTypeMap.size()-3;i++){
			tarSb.append("?,");
		}
		tarSb.deleteCharAt(tarSb.length()-1);
		tarSb.append(") ");
		String insertSql = tarSb.toString();
		
		Connection targetConn = tarMgr.getConnection();
		try {
			if(pst==null){
				pst = targetConn.prepareStatement(insertSql);
			}
			pst.clearParameters();
			
			for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
				entry = (Entry<String, String>) it.next();
				colName = entry.getKey();
				colType = entry.getValue();
				Object value = valueMap.get(colName);
				List<String> ignoreCols = Arrays.asList(new String[]{"ETLSTATUS","ETLPKNAME","ETLTS"});
				pst =setValues(pst, colName, colType, value, ignoreCols);  //������ֵ
			}
			pst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("�������ݿ��쳣");
		}finally{
			SimpleJdbc.release(null, pst, null);
			tarMgr.release(targetConn);
		}
	}

}
