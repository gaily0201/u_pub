package cn.gaily.pub.trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import cn.gaily.pub.util.CommonUtil;
import cn.gaily.simplejdbc.SimpleDSMgr;
import cn.gaily.simplejdbc.SimpleJdbc;


/**
 * <p>Title: ETLDeleteTask</P>
 * <p>Description: ���ݽ���ɾ������</p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-29
 */
public class ETLDeleteTask extends AbstractETLTask{

	
	public static ETLDeleteTask instance = null;
	
	public PreparedStatement pst = null;
	
	private ETLDeleteTask(){}
	
	public static ETLDeleteTask getInstance(){
		if(instance ==null){
			instance = new ETLDeleteTask();
		}
		return instance;
	}
	
	@Override
	public void doBatch(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr,
						String tableName, String pkName,
						ArrayBlockingQueue<Map<String, Object>> valueList,
						Map<String, String> colNameTypeMap, Boolean canBatch) {
		
		if(tarMgr==null||CommonUtil.isEmpty(tableName)||CommonUtil.isEmpty(pkName)){
			return ;
		}
		Map<String,Object> valueMap = null;
		String pkValue = null;
		
		Connection tarConn = tarMgr.getConnection();
		Connection srcConn = srcMgr.getConnection();
		PreparedStatement pst = null;

		List<String> insertPks = new ArrayList<String>(); 
		
		StringBuilder delSb = new StringBuilder("DELETE FROM ");
		delSb.append(tableName).append(" WHERE ").append(pkName);
		delSb.append(" IN(");
		for(int i=0;i<valueList.size();i++){
			delSb.append("?,");
		}
		delSb.deleteCharAt(delSb.length()-1);
		delSb.append(")");
		
		try {
			tarConn.setAutoCommit(false);
			srcConn.setAutoCommit(false);
			pst = tarConn.prepareStatement(delSb.toString());
			int i = 1;
			while(valueList.size()>0){
				valueMap = valueList.poll();
				pkValue = (String) valueMap.get(pkName);
				insertPks.add(pkValue);
				pst.setString(i, pkValue);
				i++;
			}
			
			int count = pst.executeUpdate();
			srcConn = delTemp(pkName, insertPks, tablePrefix+tableName, srcConn, DELETE);
			
			tarConn.commit();
			srcConn.commit();
			
		} catch (SQLException e) {
			try {
				tarConn.rollback();
				srcConn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new RuntimeException("�������ݿ�ع����ݳ���"+e1);
				
			}
			e.printStackTrace();
			throw new RuntimeException("ɾ�����ݳ���"+e);
		} finally{
			SimpleJdbc.release(null, pst, null);
			tarMgr.release(tarConn);
			tarMgr.release(srcConn);
		}
		
		
		
	}
	
	@Override
	public void doexecute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName,String pkName, Map<String,Object> valueMap, Map<String,String> colNameTypeMap) {
		
		//1.ͣ��Ŀ������Դ������
//		enableTrigger(tarMgr, tableName, DISABLE);
		
		String pkValue = (String) valueMap.get(pkName); //TODO pkvalue������int
		if(CommonUtil.isEmpty(pkValue)){
			return;
		}
		//2.ɾ��Ŀ����е�����
		doDelete(srcMgr, tarMgr, tableName, pkName, pkValue, colNameTypeMap);
		
		//3.����Ŀ������Դ������
//		enableTrigger(tarMgr, tableName, ENABLE);
		
	}

	
	/**
	 * <p>�������ƣ�doDelete</p>
	 * <p>����������ִ��ɾ������</p>
	 * @param tarMgr
	 * @param tableName
	 * @param pkName
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  ����   <p>
	 */
	public void doDelete(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName, String pkName, String pkValue, Map<String,String> colNameTypeMap) {
		
		if(tarMgr==null||CommonUtil.isEmpty(tableName)||CommonUtil.isEmpty(pkName)){
			return ;
		}
		
		Connection tarConn = tarMgr.getConnection();
		Connection srcConn = srcMgr.getConnection();
		StringBuilder delSb = new StringBuilder("DELETE FROM ");
		delSb.append(tableName);
		delSb.append(" WHERE ").append(pkName).append("=?");
		colIndexMap.put(pkName, 1);
		
		List<String> insertPks = new ArrayList<String>(); 
		insertPks.add(pkValue);
		try {
			tarConn.setAutoCommit(false);
			srcConn.setAutoCommit(false);
			pst = tarConn.prepareStatement(delSb.toString());
			pst.clearParameters();
			
			String colType =colNameTypeMap.get(pkName);
			if(CommonUtil.isEmpty(colType)){
				throw new RuntimeException("��ȡ�����ֶ����ͳ���");
			}
			pst = setValues(pst, pkName, colType, pkValue, null);
			int i= pst.executeUpdate();
			srcConn = delTemp(pkName, insertPks, tablePrefix+tableName, srcConn,DELETE);
			
			tarConn.commit();
			srcConn.commit();
			
		} catch (SQLException e) {
			try {
				tarConn.rollback();
				srcConn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new RuntimeException("�������ݿ�ع����ݳ���"+e1);
				
			}
			e.printStackTrace();
			throw new RuntimeException("ɾ�����ݳ���"+e);
		}finally{
			SimpleJdbc.release(null, pst, null);
			tarMgr.release(tarConn);
			tarMgr.release(srcConn);
		}
	}

	
	
}
