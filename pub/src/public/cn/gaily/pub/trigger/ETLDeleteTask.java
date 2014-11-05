package cn.gaily.pub.trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.jtds.jdbc.ColInfo;

import cn.gaily.pub.util.CommonUtil;
import cn.gaily.simplejdbc.SimpleJdbc;
import cn.gaily.simplejdbc.SimpleDSMgr;

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
	public void doexecute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName,String pkName, Map<String,Object> valueMap, Map<String,String> colNameTypeMap) {
		
		//1.ͣ��Ŀ������Դ������
//		enableTrigger(tarMgr, tableName, DISABLE);
		
		String pkValue = (String) valueMap.get(pkName); //TODO pkvalue������int
		if(CommonUtil.isEmpty(pkValue)){
			return;
		}
		//2.ɾ��Ŀ����е�����
		doDelete(tarMgr, tableName, pkName, pkValue, colNameTypeMap);
		
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
	public void doDelete(SimpleDSMgr tarMgr, String tableName, String pkName, String pkValue, Map<String,String> colNameTypeMap) {
		
		if(tarMgr==null||CommonUtil.isEmpty(tableName)||CommonUtil.isEmpty(pkName)){
			return ;
		}
		
		Connection srcConn = tarMgr.getConnection();
		StringBuilder delSb = new StringBuilder("DELETE FROM ");
		delSb.append(tableName);
		delSb.append(" WHERE ").append(pkName).append("=?");
		colIndexMap.put(pkName, 1);
		try {
			pst = srcConn.prepareStatement(delSb.toString());
			pst.clearParameters();
			
			String colType =colNameTypeMap.get(pkName);
			if(CommonUtil.isEmpty(colType)){
				throw new RuntimeException("��ȡ�����ֶ����ͳ���");
			}
			pst = setValues(pst, pkName, colType, pkValue, null);
			int i= pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("ɾ�����ݳ���");
		}finally{
			SimpleJdbc.release(null, pst, null);
			tarMgr.release(srcConn);
		}
	}
	
	
}
