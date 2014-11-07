package cn.gaily.pub.trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;

import cn.gaily.pub.util.CommonUtil;
import cn.gaily.simplejdbc.SimpleDSMgr;
import cn.gaily.simplejdbc.SimpleJdbc;


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
	
	
	
	private ETLInsertTask(){}
	
	public static ETLInsertTask getInstance(){
		if(instance ==null){
			instance = new ETLInsertTask();
		}
		return instance;
	}
	
	
	
	@Override
	public void doBatch(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr,
						String tableName, String pkName,
						ArrayBlockingQueue<Map<String, Object>> valueList,
						Map<String, String> colNameTypeMap, Boolean canBatch) {
		
		if(srcMgr==null||tarMgr==null||CommonUtil.isEmpty(tableName)){
			throw new RuntimeException("�������ݿ������������");
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

		List<String> insertPks = new ArrayList<String>(); //�����������ݵ�pkֵ,����ɾ��ʹ��
		
		PreparedStatement pst = null;
		Connection targetConn = tarMgr.getConnection();
		Connection srcConn = srcMgr.getConnection();
		try {
			targetConn.setAutoCommit(false);
			srcConn.setAutoCommit(false);
			pst = targetConn.prepareStatement(insertSql);
			Map<String, Object> valueMap = null;
			while(!valueList.isEmpty()){
				valueMap = valueList.poll();
				for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
					entry = (Entry<String, String>) it.next();
					colName = entry.getKey();
					colType = entry.getValue();
					Object value = valueMap.get(colName);
					if(colName.equals(pkName)){
						insertPks.add((String)value);   //TODO ���������ֶ����ݲ���String����
					}
					List<String> ignoreCols = Arrays.asList(new String[]{"ETLSTATUS","ETLPKNAME","ETLTS"});
					pst =setValues(pst, colName, colType, value, ignoreCols);  //������ֵ
				}
				pst.addBatch();
				pst.clearParameters();
			}
			pst.executeBatch();
			
			srcConn = delTemp(pkName, insertPks, tablePrefix+tableName, srcConn, NEW); //ɾ�������β�����ʱ������
			
			targetConn.commit();
			srcConn.commit();
			
		}catch (SQLException e) {
			try {
				targetConn.rollback();
				srcConn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new RuntimeException("�ع������쳣"+e);
			}
			e.printStackTrace();
			throw new RuntimeException("�������ݿ��쳣"+e);
		}finally{
			SimpleJdbc.release(null, pst, null);
			tarMgr.release(targetConn);
			tarMgr.release(srcConn);
		}
		
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
		doAddInsert(srcMgr, tarMgr, tableName, valueMap,pkName, colNameTypeMap);
		
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
	private void doAddInsert(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName, Map<String,Object> valueMap,String pkName, Map<String,String> colNameTypeMap) {
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
		
		PreparedStatement pst = null;
		Connection targetConn = tarMgr.getConnection();
		Connection srcConn = srcMgr.getConnection();
		String pkValue = null;
		List<String> insertPks = new ArrayList<String>();
		try {
//			if(pst==null){
//			}
			targetConn.setAutoCommit(false);
			srcConn.setAutoCommit(false);
			pst = targetConn.prepareStatement(insertSql);
			pst.clearParameters();
			
			for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
				entry = (Entry<String, String>) it.next();
				colName = entry.getKey();
				colType = entry.getValue();
				Object value = valueMap.get(colName);
				if(colName.equals(pkName)){
					pkValue = (String) value;
					insertPks.add(pkValue);
				}
				List<String> ignoreCols = Arrays.asList(new String[]{"ETLSTATUS","ETLPKNAME","ETLTS"});
				pst =setValues(pst, colName, colType, value, ignoreCols);  //������ֵ
			}
			pst.execute();
			srcConn = delTemp(pkName, insertPks, tablePrefix+tableName, srcConn, NEW); //ɾ�������β�����ʱ������
			targetConn.commit();
			srcConn.commit();
		} catch (SQLException e) {
			try {
				targetConn.rollback();
				srcConn.commit();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new RuntimeException("�ع������쳣"+e);
			}
			e.printStackTrace();
			throw new RuntimeException("�������ݿ��쳣"+e);
		}finally{
			SimpleJdbc.release(null, pst, null);
			tarMgr.release(targetConn);
			srcMgr.release(srcConn);
		}
	}

}
