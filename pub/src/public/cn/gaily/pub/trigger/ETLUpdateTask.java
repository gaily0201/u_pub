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

import cn.gaily.pub.util.CommonUtils;
import cn.gaily.simplejdbc.SimpleDSMgr;
import cn.gaily.simplejdbc.SimpleJdbc;

/**
 * <p>Title: ETLUpdateTask</P>
 * <p>Description: ���ݽ�����������</p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-29
 */
public class ETLUpdateTask extends AbstractETLTask{

	
	public static ETLUpdateTask instance = null;
	
	private ETLUpdateTask(){}
	
	public static ETLUpdateTask getInstance(){
		if(instance ==null){
			instance = new ETLUpdateTask();
		}
		return instance;
	}
	
	@Override
	public void doBatch(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr,
						String tableName, String pkName,
						ArrayBlockingQueue<Map<String, Object>> valueList,
						Map<String, String> colNameTypeMap, Boolean canBatch) {
		
		PreparedStatement ipst = null;
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ").append(tableName).append(" SET ");
		String colName = null;
		String colType = null;
		Entry<String,String> entry =  null;
		
		int n=0;
		int hasc = 0;  //��¼λ��
		for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
			entry = (Entry<String, String>) it.next();
			colName = entry.getKey();
			if(!"ETLSTATUS".equalsIgnoreCase(colName)&&!"ETLPKNAME".equalsIgnoreCase(colName)&&!"ETLTS".equalsIgnoreCase(colName)){
				sb.append(colName).append("=?,");
				colIndexMap.put(colName, ++n);
				hasc++;
			}
		}
		hasc++;
		sb.deleteCharAt(sb.length()-1);
		
		sb.append(" WHERE ").append(pkName).append("=?");
		System.out.println(sb);
		List<String> insertPks = new ArrayList<String>(); 
		
		Connection targetConn = tarMgr.getConnection();
		Connection srcConn = srcMgr.getConnection();
		try {
			targetConn.setAutoCommit(false);
			srcConn.setAutoCommit(false);
			ipst = targetConn.prepareStatement(sb.toString());
			ipst.clearParameters();
			Map<String, Object> valueMap = null;
			String pkValue = null;
			while(!valueList.isEmpty()){
				valueMap = valueList.poll();
				pkValue = (String) valueMap.get(pkName);  //TODO pk����Ϊint������������
				if(CommonUtils.isEmpty(pkValue)){
					throw new RuntimeException("��������δ��ȡ������");
				}
				for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
					entry = (Entry<String, String>) it.next();
					colName = entry.getKey();
					colType = entry.getValue();
					Object value = valueMap.get(colName);
					if(colName.equals(pkName)){
						insertPks.add((String)value);   //TODO ���������ֶ����ݲ���String����
						if(value==null){  //fix ORA-01407: �޷����� ("UAP63_TEST"."CRPAS_GAJ_AJXX_SACW_B"."PK_GAJ_AJXX_H") Ϊ NULL
							ipst.clearParameters(); 
							break;
						}
					}
					
					List<String> ignoreCols = Arrays.asList(new String[]{"ETLSTATUS","ETLPKNAME","ETLTS"});
					ipst = setValues(ipst, colName, colType, value, ignoreCols);  //������ֵ
				}
				ipst.setString(hasc, pkValue); //TODO pk����Ϊint������������
				ipst.addBatch();
				ipst.clearParameters();
			}
			ipst.executeBatch();
			System.out.println("UPDATE " + batchSize +" record");
			srcConn = delTemp(pkName, insertPks, tablePrefix+tableName, srcConn, UPDATE, true); //ɾ�������β�����ʱ������
			targetConn.commit();
			srcConn.commit();
		}catch (SQLException e) {
			try {
				targetConn.rollback();
				srcConn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new RuntimeException("�������ݿ�ع����ݳ���"+e1);
			}
			e.printStackTrace();
			throw new RuntimeException("�������ݿ��쳣"+e);
		}finally{
			SimpleJdbc.release(null, ipst, null);
			tarMgr.release(targetConn);
			srcMgr.release(srcConn);
		}
	}
	
	
	@Override
	public void doexecute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName, String pkName, Map<String,Object> valueMap, Map<String,String> colNameTypeMap) {
		if(srcMgr==null||tarMgr==null||CommonUtils.isEmpty(tableName)){
			throw new RuntimeException("�������ݿ������������");
		}

		//1��ͣ��Ŀ��ⴥ����
//		enableTrigger(tarMgr, tableName, DISABLE);
		
		//2��ִ��Ŀ������ݿ����
		doUpdate(srcMgr, tarMgr, tableName,pkName, valueMap, colNameTypeMap);
		
		//4������Ŀ��ⴥ����
//		enableTrigger(tarMgr, tableName, ENABLE);
	}

	/**
	 * <p>�������ƣ�doUpdate</p>
	 * <p>����������</p>
	 * @param tarMgr
	 * @param tableName
	 * @param pkName
	 * @param valueMap
	 * @param colNameTypeMap
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  ����   <p>
	 */
	private void doUpdate(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName, String pkName, Map<String, Object> valueMap, Map<String, String> colNameTypeMap) {
		PreparedStatement ipst = null;
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ").append(tableName).append(" SET ");
		String colName = null;
		String colType = null;
		Entry<String,String> entry =  null;
		
		int n=0;
		for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
			entry = (Entry<String, String>) it.next();
			colName = entry.getKey();
			if(!"ETLSTATUS".equalsIgnoreCase(colName)&&!"ETLPKNAME".equalsIgnoreCase(colName)&&!"ETLTS".equalsIgnoreCase(colName)){
				sb.append(colName).append("=?,");
				colIndexMap.put(colName, ++n);
			}
		}
		sb.deleteCharAt(sb.length()-1);
		String pkValue = (String) valueMap.get(pkName);  //TODO pk����Ϊint
		if(CommonUtils.isEmpty(pkValue)){
			throw new RuntimeException("��������δ��ȡ������");
		}
		sb.append(" WHERE ").append(pkName).append("='").append(pkValue).append("'");
		System.out.println(sb);
		List<String> insertPks = new ArrayList<String>();
		insertPks.add(pkValue);
		Connection targetConn = tarMgr.getConnection();
		Connection srcConn = srcMgr.getConnection();
		try {
			targetConn.setAutoCommit(false);
			srcConn.setAutoCommit(false);
			ipst = targetConn.prepareStatement(sb.toString());
			ipst.clearParameters();
			
			for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
				entry = (Entry<String, String>) it.next();
				colName = entry.getKey();
				colType = entry.getValue();
				Object value = valueMap.get(colName);
				List<String> ignoreCols = Arrays.asList(new String[]{"ETLSTATUS","ETLPKNAME","ETLTS"});
				ipst =setValues(ipst, colName, colType, value, ignoreCols);  //������ֵ
			}
			ipst.execute();
			System.out.println("UPDATE 1 record");
			srcConn = delTemp(pkName, insertPks, tablePrefix+tableName, srcConn, UPDATE, false); //ɾ�������β�����ʱ������
			targetConn.commit();
			srcConn.commit();
		} catch (SQLException e) {
			try {
				targetConn.rollback();
				srcConn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new RuntimeException("�������ݿ�ع����ݳ���"+e1);
			}
			e.printStackTrace();
			throw new RuntimeException("�������ݿ��쳣");
		}finally{
			SimpleJdbc.release(null, ipst, null);
			tarMgr.release(targetConn);
			srcMgr.release(srcConn);
		}
	}

	
}
