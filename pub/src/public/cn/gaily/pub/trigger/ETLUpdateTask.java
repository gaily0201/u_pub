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
 * <p>Title: ETLUpdateTask</P>
 * <p>Description: ���ݽ�����������</p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-29
 */
public class ETLUpdateTask extends AbstractETLTask{

	
	public static ETLUpdateTask instance = null;
	
	public PreparedStatement ipst = null;
	
	private ETLUpdateTask(){}
	
	public static ETLUpdateTask getInstance(){
		if(instance ==null){
			instance = new ETLUpdateTask();
		}
		return instance;
	}
	
	@Override
	public void doexecute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName, String pkName, Map<String,Object> valueMap, Map<String,String> colNameTypeMap) {
		if(srcMgr==null||tarMgr==null||CommonUtil.isEmpty(tableName)){
			throw new RuntimeException("�������ݿ������������");
		}

		//1��ͣ��Ŀ��ⴥ����
//		enableTrigger(tarMgr, tableName, DISABLE);
		
		//2��ִ��Ŀ������ݿ����
		doUpdate(tarMgr, tableName,pkName, valueMap, colNameTypeMap);
		
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
	private void doUpdate(SimpleDSMgr tarMgr, String tableName, String pkName, Map<String, Object> valueMap, Map<String, String> colNameTypeMap) {
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
		if(CommonUtil.isEmpty(pkValue)){
			throw new RuntimeException("��������δ��ȡ������");
		}
		sb.append(" WHERE ").append(pkName).append("='").append(pkValue).append("'");
		
		Connection targetConn = tarMgr.getConnection();
		try {
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
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("�������ݿ��쳣");
		}finally{
			SimpleJdbc.release(null, ipst, null);
			tarMgr.release(targetConn);
		}
	}
	
	
}
