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
 * <p>Description: 数据交换更新任务</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
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
		int hasc = 0;  //记录位置
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
				pkValue = (String) valueMap.get(pkName);  //TODO pk可能为int或者其他类型
				if(CommonUtils.isEmpty(pkValue)){
					throw new RuntimeException("更新数据未获取到主键");
				}
				for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
					entry = (Entry<String, String>) it.next();
					colName = entry.getKey();
					colType = entry.getValue();
					Object value = valueMap.get(colName);
					if(colName.equals(pkName)){
						insertPks.add((String)value);   //TODO 可能主键字段数据不是String类型
						if(value==null){  //fix ORA-01407: 无法更新 ("UAP63_TEST"."CRPAS_GAJ_AJXX_SACW_B"."PK_GAJ_AJXX_H") 为 NULL
							ipst.clearParameters(); 
							break;
						}
					}
					
					List<String> ignoreCols = Arrays.asList(new String[]{"ETLSTATUS","ETLPKNAME","ETLTS"});
					ipst = setValues(ipst, colName, colType, value, ignoreCols);  //设置列值
				}
				ipst.setString(hasc, pkValue); //TODO pk可能为int或者其他类型
				ipst.addBatch();
				ipst.clearParameters();
			}
			ipst.executeBatch();
			System.out.println("UPDATE " + batchSize +" record");
			srcConn = delTemp(pkName, insertPks, tablePrefix+tableName, srcConn, UPDATE, true); //删除出本次操作临时表数据
			targetConn.commit();
			srcConn.commit();
		}catch (SQLException e) {
			try {
				targetConn.rollback();
				srcConn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new RuntimeException("更新数据库回滚数据出错"+e1);
			}
			e.printStackTrace();
			throw new RuntimeException("更新数据库异常"+e);
		}finally{
			SimpleJdbc.release(null, ipst, null);
			tarMgr.release(targetConn);
			srcMgr.release(srcConn);
		}
	}
	
	
	@Override
	public void doexecute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName, String pkName, Map<String,Object> valueMap, Map<String,String> colNameTypeMap) {
		if(srcMgr==null||tarMgr==null||CommonUtils.isEmpty(tableName)){
			throw new RuntimeException("更新数据库操作参数出错");
		}

		//1、停用目标库触发器
//		enableTrigger(tarMgr, tableName, DISABLE);
		
		//2、执行目标库数据库更新
		doUpdate(srcMgr, tarMgr, tableName,pkName, valueMap, colNameTypeMap);
		
		//4、启用目标库触发器
//		enableTrigger(tarMgr, tableName, ENABLE);
	}

	/**
	 * <p>方法名称：doUpdate</p>
	 * <p>方法描述：</p>
	 * @param tarMgr
	 * @param tableName
	 * @param pkName
	 * @param valueMap
	 * @param colNameTypeMap
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  创建   <p>
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
		String pkValue = (String) valueMap.get(pkName);  //TODO pk可能为int
		if(CommonUtils.isEmpty(pkValue)){
			throw new RuntimeException("更新数据未获取到主键");
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
				ipst =setValues(ipst, colName, colType, value, ignoreCols);  //设置列值
			}
			ipst.execute();
			System.out.println("UPDATE 1 record");
			srcConn = delTemp(pkName, insertPks, tablePrefix+tableName, srcConn, UPDATE, false); //删除出本次操作临时表数据
			targetConn.commit();
			srcConn.commit();
		} catch (SQLException e) {
			try {
				targetConn.rollback();
				srcConn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new RuntimeException("更新数据库回滚数据出错"+e1);
			}
			e.printStackTrace();
			throw new RuntimeException("更新数据库异常");
		}finally{
			SimpleJdbc.release(null, ipst, null);
			tarMgr.release(targetConn);
			srcMgr.release(srcConn);
		}
	}

	
}
