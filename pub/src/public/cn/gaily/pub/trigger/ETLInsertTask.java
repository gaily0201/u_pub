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
 * <p>Description: 数据交换插入任务</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
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
			throw new RuntimeException("新增数据库操作参数出错");
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
		List<String> insertPks = new ArrayList<String>(); //保存插入的数据的pk值,便于删除使用
		
		List<String> tarPkList = null;
		if(tarPks.get(tableName.toUpperCase())!=null&&tarPks.get(tableName.toUpperCase()).size()>0&&sameTab){
			tarPkList = tarPks.get(tableName.toUpperCase());
		}else{
			tarPkList = queryHasPks(tarMgr,tableName,pkName); //获取目标库中已经存在的pk，避免数据重复插入
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
				
				insertPks.add(pkValue);   //TODO 可能主键字段数据不是String类型
				
				if(tarPkList.contains(pkValue)){
					continue;  //目标库中已经存在Pk值，不插入
				}
				tarPkList.add(pkValue);
				tarPks.put(tableName.toUpperCase(), tarPkList);
				for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
					entry = (Entry<String, String>) it.next();
					colName = entry.getKey();
					colType = entry.getValue();
					Object value = valueMap.get(colName);
					List<String> ignoreCols = Arrays.asList(new String[]{"ETLSTATUS","ETLPKNAME","ETLTS"});
					pst =setValues(pst, colName, colType, value, ignoreCols);  //设置列值
				}
				pst.addBatch();
			}
			int[] ss = pst.executeBatch();
			count += ss.length;
			System.out.println("insert "+ batchSize +" record");
			srcConn = delTemp(pkName, insertPks, tablePrefix+tableName, srcConn, NEW, true); //删除出本次操作临时表数据
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
				throw new RuntimeException("回滚数据异常"+e);
			}
			e.printStackTrace();
			throw new RuntimeException("插入数据库异常"+e);
		}finally{
			SimpleJdbc.release(null, pst, null);
			tarMgr.release(targetConn);
			srcMgr.release(srcConn);
		}
		return count;
	}
	

	/**
	 * <p>方法名称：queryHasPks</p>
	 * <p>方法描述：查询目标库中存在的pk数据</p>
	 * @param tarMgr  目标数据源
	 * @param pkName  pk字段名
	 * @param tableName  表名
	 * @return
	 * @author xiaoh
	 * @since  2014-11-19
	 * <p> history 2014-11-19 xiaoh  创建   <p>
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
	 * <p>方法名称：dealAdd</p>
	 * <p>方法描述：处理新增数据</p>
	 * @param tableName 表名
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  创建   <p>
	 */
	@Override
	public void doexecute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName, String pkName, Map<String,Object> valueMap, Map<String,String> colNameTypeMap){
		
		if(srcMgr==null||tarMgr==null||CommonUtils.isEmpty(tableName)){
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
			tarPkList = queryHasPks(tarMgr,tableName,pkName); //获取目标库中已经存在的pk，避免数据重复插入
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
				pst =setValues(pst, colName, colType, value, ignoreCols);  //设置列值
			}
			pst.execute();
			System.out.println("insert 1 record");
			srcConn = delTemp(pkName, insertPks, tablePrefix+tableName, srcConn, NEW, false); //删除出本次操作临时表数据
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
