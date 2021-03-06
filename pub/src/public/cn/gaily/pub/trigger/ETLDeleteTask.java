package cn.gaily.pub.trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import cn.gaily.pub.util.CommonUtils;
import cn.gaily.simplejdbc.SimpleDSMgr;
import cn.gaily.simplejdbc.SimpleJdbc;


/**
 * <p>Title: ETLDeleteTask</P>
 * <p>Description: 数据交换删除任务</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
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
	public int doBatch(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr,
						String tableName, String pkName,
						ArrayBlockingQueue<Map<String, Object>> valueList,
						Map<String, String> colNameTypeMap, Boolean canBatch) {
		
		if(tarMgr==null||CommonUtils.isEmpty(tableName)||CommonUtils.isEmpty(pkName)){
			return 0;
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
		System.out.println(delSb);
		int count = 0;
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
			
			count = pst.executeUpdate();
			System.out.println("DELETE" + count +" record");
			srcConn = delTemp(pkName, insertPks, tablePrefix+tableName, srcConn, DELETE, true);
			
			tarConn.commit();
			srcConn.commit();
			
		} catch (SQLException e) {
			try {
				tarConn.rollback();
				srcConn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new RuntimeException(tableName+"更新数据库回滚数据出错"+e1);
				
			}
			e.printStackTrace();
			throw new RuntimeException(tableName+"删除数据出错"+e);
		} finally{
			SimpleJdbc.release(null, pst, null);
			tarMgr.release(tarConn);
			srcMgr.release(srcConn);
		}
		
		return count;
		
	}
	
	@Override
	public void doexecute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName,String pkName, Map<String,Object> valueMap, Map<String,String> colNameTypeMap) {
		
		//1.停用目标数据源触发器
//		enableTrigger(tarMgr, tableName, DISABLE);
		
		String pkValue = (String) valueMap.get(pkName); //TODO pkvalue可能是int
		if(CommonUtils.isEmpty(pkValue)){
			return;
		}
		//2.删除目标表中的数据
		doDelete(srcMgr, tarMgr, tableName, pkName, pkValue, colNameTypeMap);
		
		//3.启用目标数据源触发器
//		enableTrigger(tarMgr, tableName, ENABLE);
		
	}

	
	/**
	 * <p>方法名称：doDelete</p>
	 * <p>方法描述：执行删除操作</p>
	 * @param tarMgr
	 * @param tableName
	 * @param pkName
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  创建   <p>
	 */
	public void doDelete(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName, String pkName, String pkValue, Map<String,String> colNameTypeMap) {
		
		if(tarMgr==null||CommonUtils.isEmpty(tableName)||CommonUtils.isEmpty(pkName)){
			return ;
		}
		
		Connection tarConn = tarMgr.getConnection();
		Connection srcConn = srcMgr.getConnection();
		StringBuilder delSb = new StringBuilder("DELETE FROM ");
		delSb.append(tableName);
		delSb.append(" WHERE ").append(pkName).append("=?");
		colIndexMap.put(pkName, 1);
		System.out.println(delSb);
		List<String> insertPks = new ArrayList<String>(); 
		insertPks.add(pkValue);
		try {
			tarConn.setAutoCommit(false);
			srcConn.setAutoCommit(false);
			pst = tarConn.prepareStatement(delSb.toString());
			pst.clearParameters();
			
			String colType =colNameTypeMap.get(pkName);
			if(CommonUtils.isEmpty(colType)){
				throw new RuntimeException(tableName+"获取主键字段类型出错");
			}
			pst = setValues(pst, pkName, colType, pkValue, null);
			int i= pst.executeUpdate();
			System.out.println("DELETE" + i +" record");
			srcConn = delTemp(pkName, insertPks, tablePrefix+tableName, srcConn,DELETE, false);
			
			tarConn.commit();
			srcConn.commit();
			
		} catch (SQLException e) {
			try {
				tarConn.rollback();
				srcConn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new RuntimeException(tableName+"更新数据库回滚数据出错"+e1);
				
			}
			e.printStackTrace();
			throw new RuntimeException(tableName+"删除数据出错"+e);
		}finally{
			SimpleJdbc.release(null, pst, null);
			tarMgr.release(tarConn);
			srcMgr.release(srcConn);
		}
	}

	
	
}
