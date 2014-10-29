package cn.gaily.pub.trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import cn.gaily.pub.util.CommonUtil;
import cn.gaily.pub.util.JdbcUtils;

/**
 * <p>Title: ETLInsertTask</P>
 * <p>Description: ���ݽ�����������</p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-29
 */
public class ETLInsertTask extends AbstractETLTask{

	/**
	 * <p>�������ƣ�dealAdd</p>
	 * <p>����������������������</p>
	 * @param tableName ����
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  ����   <p>
	 */
	public void dealAdd(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName){
		clear(); 	//�������
		
		if(srcMgr==null||tarMgr==null||CommonUtil.isEmpty(tableName)){
			throw new RuntimeException("�������ݿ������������");
		}
		colNameTypeMap = getTabCols(srcMgr, tableName, false);
		String pkName = null;
		//1����ѯԴ����ʱ����������
		pkName = queryTempData(tableName, srcMgr, 1);
		
		//2��ͣ��Ŀ��ⴥ����
		enableTrigger(tarMgr, tableName, 0);
		
		//3��ִ��Ŀ������ݿ����
		doAddInsert(tarMgr, tableName);
		
		//4������Ŀ��ⴥ����
		enableTrigger(tarMgr, tableName, 1);
		
		//5��ɾ��Դ����ʱ����������
		delTempData(srcMgr, tableName, pkName, 1);
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
	private void doAddInsert(SimpleDSMgr tarMgr, String tableName) {
		if(tarMgr==null||CommonUtil.isEmpty(tableName)){
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
		PreparedStatement ipst = null;
		try {
			targetConn.setAutoCommit(false);
			ipst = targetConn.prepareStatement(insertSql);
			for(int i=0;i<valueList.size();i++){
				for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
					entry = (Entry<String, String>) it.next();
					colName = entry.getKey();
					colType = entry.getValue();
					Object value = valueList.get(i).get(colName);
					List<String> ignoreCols = Arrays.asList(new String[]{"ETLSTATUS","ETLPKNAME","ETLTS"});
					ipst =setValues(ipst, colName, colType, value, ignoreCols);  //������ֵ
				}
				ipst.addBatch();
			}
			ipst.executeBatch();
			targetConn.commit();
			ipst.clearBatch();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("�������ݿ��쳣");
		}finally{
			JdbcUtils.release(null, ipst, null);
			tarMgr.release(targetConn);
		}
	}

	
	@Override
	public void dealUpdate(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName) {
	}

	@Override
	public void dealDel(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName) {
	}
}
