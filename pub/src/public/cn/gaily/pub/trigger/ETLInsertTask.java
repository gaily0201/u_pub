package cn.gaily.pub.trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;

import cn.gaily.pub.util.CommonUtils;
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
	
	private Map<String,List<String>> tarPks = new HashMap<String,List<String>>();
	
	String pTab = null;
	
	boolean sameTab = true;
	
	private ETLInsertTask(){}
	
	public static ETLInsertTask getInstance(){
		if(instance ==null){
			instance = new ETLInsertTask();
		}
		return instance;
	}
	
	
	
	@Override
	public int doBatch(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr,
						String tableName, String pkName,
						ArrayBlockingQueue<Map<String, Object>> valueList,
						Map<String, String> colNameTypeMap, Boolean canBatch) {
		
		if(pTab==null){
			pTab  = tableName;
			sameTab = true;
		}
		if(!tableName.equalsIgnoreCase(pTab)){
			sameTab = false;
			pTab = null;
			if(tarPks.get(tableName.toUpperCase())!=null){
				tarPks.remove(tableName.toUpperCase());
			}
		}
		if(tableName.equalsIgnoreCase(pTab)){
			sameTab = true;
		}
		
		if(srcMgr==null||tarMgr==null||CommonUtils.isEmpty(tableName)){
			throw new RuntimeException("�������ݿ������������");
		}
		StringBuilder tarSb = new StringBuilder();
		tarSb.append("INSERT INTO ").append(tableName).append("(");
		String colName = null;
		String colType = null;
		Entry<String,String> entry =  null;
		boolean isSyn = true;
		int n=0;
		for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
			entry = (Entry<String, String>) it.next();
			colName = entry.getKey();
			if("ETLSTATUS".equalsIgnoreCase(colName)){
				isSyn = false;
			}
			if(!"ETLSTATUS".equalsIgnoreCase(colName)&&!"ETLPKNAME".equalsIgnoreCase(colName)&&!"ETLTS".equalsIgnoreCase(colName)){
				tarSb.append(colName).append(",");
				colIndexMap.put(colName, ++n);
			}
		}
		tarSb.deleteCharAt(tarSb.length()-1);
		tarSb.append(") VALUES(");
		if(isSyn){
			for(int i=0;i<colNameTypeMap.size();i++){
				tarSb.append("?,");
			}
		}else{
			for(int i=0;i<colNameTypeMap.size()-3;i++){
				tarSb.append("?,");
			}
		}
		tarSb.deleteCharAt(tarSb.length()-1);
		tarSb.append(") ");
		String insertSql = tarSb.toString();
		System.out.println(insertSql);
		List<String> insertPks = new ArrayList<String>(); //�����������ݵ�pkֵ,����ɾ��ʹ��
		
		List<String> tarPkList = null;
		if(tarPks.get(tableName.toUpperCase())!=null&&tarPks.get(tableName.toUpperCase()).size()>0&&sameTab){
			tarPkList = tarPks.get(tableName.toUpperCase());
		}else{
			tarPkList = queryHasPks(tarMgr,tableName,pkName); //��ȡĿ������Ѿ����ڵ�pk�����������ظ�����
			tarPks.put(tableName.toUpperCase(), tarPkList);
		}
		
		PreparedStatement pst = null;
		Connection targetConn = tarMgr.getConnection();
		Connection srcConn = srcMgr.getConnection();
		int count = 0;
		try {
			targetConn.setAutoCommit(false);
			srcConn.setAutoCommit(false);
			pst = targetConn.prepareStatement(insertSql);
			Map<String, Object> valueMap = null;
			String pkValue = null;
			while(!valueList.isEmpty()){
				valueMap = valueList.poll();
				pkValue = (String) valueMap.get(pkName);
				
				insertPks.add(pkValue);   //TODO ���������ֶ����ݲ���String����
				
				if(tarPkList.contains(pkValue)){
					continue;  //Ŀ������Ѿ�����Pkֵ��������
				}
				tarPkList.add(pkValue);
				tarPks.put(tableName.toUpperCase(), tarPkList);
				for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
					entry = (Entry<String, String>) it.next();
					colName = entry.getKey();
					colType = entry.getValue();
					Object value = valueMap.get(colName);
					List<String> ignoreCols = Arrays.asList(new String[]{"ETLSTATUS","ETLPKNAME","ETLTS"});
					pst =setValues(pst, colName, colType, value, ignoreCols);  //������ֵ
				}
				pst.addBatch();
			}
			int[] ss = pst.executeBatch();
			count += ss.length;
			System.out.println("insert "+ batchSize +" record");
			srcConn = delTemp(pkName, insertPks, tablePrefix+tableName, srcConn, NEW, true); //ɾ�������β�����ʱ������
			targetConn.commit();
			srcConn.commit();
			pst.clearParameters();
			pst.clearBatch();
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
			srcMgr.release(srcConn);
		}
		return count;
	}
	

	/**
	 * <p>�������ƣ�queryHasPks</p>
	 * <p>������������ѯĿ����д��ڵ�pk����</p>
	 * @param tarMgr  Ŀ������Դ
	 * @param pkName  pk�ֶ���
	 * @param tableName  ����
	 * @return
	 * @author xiaoh
	 * @since  2014-11-19
	 * <p> history 2014-11-19 xiaoh  ����   <p>
	 */
	private List<String> queryHasPks(SimpleDSMgr tarMgr,String tableName, String pkName) {
		Connection conn = tarMgr.getConnection();
		String sql = "SELECT "+pkName+" FROM " +tableName;
		Statement st = null;
		ResultSet rs = null;
		List<String> pks = new ArrayList<String>();
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			String pk = null;
			while(rs.next()){
				pk = rs.getString(1);
				if(CommonUtils.isNotEmpty(pk)){
					pks.add(pk);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			SimpleJdbc.release(null, st, rs);
			tarMgr.release(conn);
		}
		return pks;
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
		
		if(srcMgr==null||tarMgr==null||CommonUtils.isEmpty(tableName)){
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
		if(tarMgr==null||CommonUtils.isEmpty(tableName)||valueMap.isEmpty()){
			return ;
		}
		if(pTab==null){
			pTab  = tableName;
			sameTab = true;
		}
		if(!tableName.equalsIgnoreCase(pTab)){
			sameTab = false;
			pTab = null;
			if(tarPks.get(tableName.toUpperCase())!=null){
				tarPks.remove(tableName.toUpperCase());
			}
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
		System.out.println(insertSql);
		PreparedStatement pst = null;
		Connection targetConn = tarMgr.getConnection();
		Connection srcConn = srcMgr.getConnection();
		String pkValue = null;
		List<String> insertPks = new ArrayList<String>();
		
		List<String> tarPkList = null;
		if(tarPks.get(tableName.toUpperCase())!=null&&tarPks.get(tableName.toUpperCase()).size()>0&&sameTab){
			tarPkList = tarPks.get(tableName.toUpperCase());
		}else{
			tarPkList = queryHasPks(tarMgr,tableName,pkName); //��ȡĿ������Ѿ����ڵ�pk�����������ظ�����
			tarPks.put(tableName.toUpperCase(), tarPkList);
		}
		
		try {
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
					tarPkList.add(pkValue);
					tarPks.put(tableName.toUpperCase(), tarPkList);
					insertPks.add(pkValue);
				}
				List<String> ignoreCols = Arrays.asList(new String[]{"ETLSTATUS","ETLPKNAME","ETLTS"});
				pst =setValues(pst, colName, colType, value, ignoreCols);  //������ֵ
			}
			pst.execute();
			System.out.println("insert 1 record");
			srcConn = delTemp(pkName, insertPks, tablePrefix+tableName, srcConn, NEW, false); //ɾ�������β�����ʱ������
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
