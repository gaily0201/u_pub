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
 * <p>Description: 数据交换插入任务</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
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
			throw new RuntimeException("新增数据库操作参数出错");
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

		List<String> insertPks = new ArrayList<String>(); //保存插入的数据的pk值,便于删除使用
		
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
						insertPks.add((String)value);   //TODO 可能主键字段数据不是String类型
					}
					List<String> ignoreCols = Arrays.asList(new String[]{"ETLSTATUS","ETLPKNAME","ETLTS"});
					pst =setValues(pst, colName, colType, value, ignoreCols);  //设置列值
				}
				pst.addBatch();
				pst.clearParameters();
			}
			pst.executeBatch();
			
			srcConn = delTemp(pkName, insertPks, tablePrefix+tableName, srcConn, NEW); //删除出本次操作临时表数据
			
			targetConn.commit();
			srcConn.commit();
			
		}catch (SQLException e) {
			try {
				targetConn.rollback();
				srcConn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new RuntimeException("回滚数据异常"+e);
			}
			e.printStackTrace();
			throw new RuntimeException("插入数据库异常"+e);
		}finally{
			SimpleJdbc.release(null, pst, null);
			tarMgr.release(targetConn);
			tarMgr.release(srcConn);
		}
		
	}
	

	/**
	 * <p>方法名称：dealAdd</p>
	 * <p>方法描述：处理新增数据</p>
	 * @param tableName 表名
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  创建   <p>
	 */
	@Override
	public void doexecute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName, String pkName, Map<String,Object> valueMap, Map<String,String> colNameTypeMap){
		
		if(srcMgr==null||tarMgr==null||CommonUtil.isEmpty(tableName)){
			throw new RuntimeException("新增数据库操作参数出错");
		}

		//1、停用目标库触发器
//		enableTrigger(tarMgr, tableName, DISABLE);
		
		//2、执行目标库数据库插入
		doAddInsert(srcMgr, tarMgr, tableName, valueMap,pkName, colNameTypeMap);
		
		//3、启用目标库触发器
//		enableTrigger(tarMgr, tableName, ENABLE);
	}
	
	/**
	 * <p>方法名称：doAddInsert</p>
	 * <p>方法描述：执行目标库数据库插入</p>
	 * @param tarMgr
	 * @param tableName
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  创建   <p>
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
				pst =setValues(pst, colName, colType, value, ignoreCols);  //设置列值
			}
			pst.execute();
			srcConn = delTemp(pkName, insertPks, tablePrefix+tableName, srcConn, NEW); //删除出本次操作临时表数据
			targetConn.commit();
			srcConn.commit();
		} catch (SQLException e) {
			try {
				targetConn.rollback();
				srcConn.commit();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new RuntimeException("回滚数据异常"+e);
			}
			e.printStackTrace();
			throw new RuntimeException("插入数据库异常"+e);
		}finally{
			SimpleJdbc.release(null, pst, null);
			tarMgr.release(targetConn);
			srcMgr.release(srcConn);
		}
	}

}
