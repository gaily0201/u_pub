package cn.gaily.pub.trigger;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;

import cn.gaily.pub.util.CommonUtil;
import cn.gaily.simplejdbc.SimpleDSMgr;
import cn.gaily.simplejdbc.SimpleJdbc;


/**
 * <p>Title: TriggerBaseTask</P>
 * <p>Description: 同步数据库增、删、改执行基础类</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-28
 */
public abstract class AbstractETLTask {

	/**
	 * 新增插入标识 1
	 */
	public final int NEW = 1;
	/**
	 * 更新标识 2
	 */
	public final int UPDATE = 2;
	/**
	 * 删除标识 3
	 */
	public final int DELETE = 3;
	
	/**
	 * 启用
	 */
	public final int ENABLE = 1;
	/**
	 * 停用
	 */
	public final int DISABLE = 0;
	
	/**
	 * 批量查询大小
	 */
	public final int batchSize = 2048;
	
	/**
	 * 触发器管理表
	 */
	protected String mgrTriggerTabName = "XFL_TABSTATUS";
	
	/**
	 * 临时表前缀
	 */
	protected String tablePrefix = "XFL_";
	
	/**
	 * 表名字段名map，key:表名大写,value:字段名,字段类型map
	 */
	public Map<String,Map<String,String>> tabColMap = new HashMap<String,Map<String,String>>();
	
	/**
	 * 保存列值队列，Map<String,Object> key:列名, value:列值
	 */
	public ArrayBlockingQueue<Map<String,Object>> 	valueList 	= new ArrayBlockingQueue<Map<String,Object>>(batchSize,true);
	/**
	 * 操作的pk值，以备回删数据
	 */
	public List<String> 			 	pkValues 		= new ArrayList<String>();
	/**
	 * 列名和类型对应关系（缓存起来）
	 */
	public Map<String,Map<String,String>> colNameTypeCache	= new HashMap<String,Map<String,String>>();
	
	/**
	 * 列名和类型对应关系
	 */
	public Map<String,String> colNameTypeMap = new HashMap<String,String>();
	
	/**
	 * 列和索引映射map,值插入设值
	 */
	public Map<String,Integer> 	 colIndexMap 	= new HashMap<String,Integer>();
	
	
	public void clear(){
		valueList.clear();
		colNameTypeMap.clear();
		pkValues.clear();
		colIndexMap.clear();
		
	}
	
	/**
	 * <p>方法名称：execute</p>
	 * <p>方法描述：执行数据预置</p>
	 * @param srcMgr
	 * @param tarMgr
	 * @param tableName
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  创建   <p>
	 */
	public String execute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName){
		if(srcMgr==null||tarMgr==null||CommonUtil.isEmpty(tableName)){
			throw new RuntimeException("执行前数据预置参数出错");
		}
		
		clear();
		
		enableTrigger(tarMgr, tableName, 0);  //停用触发器 //TODO 存在问题：在执行期间，数据可能丢失
		
		String onlyPkName = null;
		int round = 0;
		while(true){
			String pkName = queryTempData(tableName, srcMgr, batchSize, round++); //操作每张表的pk是唯一的
			onlyPkName = pkName;
			if(CommonUtil.isEmpty(pkName)){
				break;
			}
			Map<String,Object> map = null;
			String status = null;
			AbstractETLTask task = null;
			int i=0;
			for(Iterator it= valueList.iterator();it.hasNext();){
				map = (Map<String, Object>) valueList.poll();
				if(map==null){
					break;
				}
				status = (String) map.get("ETLSTATUS");
				if(CommonUtil.isEmpty(status)){
					continue;
				}
				
				i++;
				
				int type = Integer.valueOf(status);
				switch(type){
				case NEW:
					task = ETLInsertTask.getInstance();
					System.out.println("insert: "+(round-1)+i); //TODO 测试用
					break;
				case UPDATE:
					task = ETLUpdateTask.getInstance();
					System.out.println("update: "+(round-1)+i); //TODO 测试用
					break;
				case DELETE:
					task = ETLDeleteTask.getInstance();
					System.out.println("delete: "+(round-1)+i); //TODO 测试用
					break;
				default:
					throw new RuntimeException("出错");
				}
				
				task.doexecute(srcMgr, tarMgr, tableName, pkName,  map, colNameTypeMap);
			}
		}
		
		enableTrigger(tarMgr, tableName, 1);  //恢复触发器
		
		delTempData(srcMgr, tableName, onlyPkName);
		
		//统一事务
//		for(int i=0;i<srcMgr.conns.size();i++){
//			try {
//				srcMgr.conns.get(i).commit();
//				tarMgr.conns.get(i).commit();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
		
		return onlyPkName;
	}
	
	/**
	 * <p>方法名称：doexecute</p>
	 * <p>方法描述：执行DDL操作</p>
	 * @param srcMgr		源连接池
	 * @param tarMgr		目标连接池
	 * @param tableName		表名
	 * @param pkName		唯一标识名称
	 * @param valueMap		值MAP
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  创建   <p>
	 */
	public abstract void doexecute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName, String pkName, Map<String,Object> valueMap, Map<String,String> colNameTypeMap);
	
	/**
	 * <p>方法名称：setValues</p>
	 * <p>方法描述：设置列值</p>
	 * @param ipst		statement
	 * @param colName	字段名
	 * @param colType	字段类型
	 * @param value		字段值
	 * @param ignoreCols	过滤字段
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  创建   <p>
	 */
	public PreparedStatement setValues(PreparedStatement ipst, String colName, String colType,Object value, List<String> ignoreCols) {
		if(ignoreCols!=null&&ignoreCols.contains(colName)){
			return ipst;
		}
		try{
			if(value==null){
				 ipst.setNull(colIndexMap.get(colName), 0);
			 }else if("VARCHAR2".equals(colType)||"NVARCHAR2".equals(colType)||"CHAR".equals(colType)){
				ipst.setString(colIndexMap.get(colName), (String)value);
			}else if("DATE".equals(colType)){
				Date date = null;
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(CommonUtil.DATE_FORMATER_YYYY_MM_DD_TIME);
					String v = String.valueOf(value).substring(0,19);
					date = new Date(sdf.parse(v).getTime());  //TODO 丢时间
				} catch (ParseException e) {
					e.printStackTrace();
					throw new RuntimeException("日期转换出错"+e);
				}
				ipst.setDate(colIndexMap.get(colName), date);
			}else if("FLOAT".equals(colType)){
				ipst.setFloat(colIndexMap.get(colName), Float.valueOf((String) value));
			}
			 else if("NUMBER".equals(colType)){
				 if(((String)value).contains(".")){
					 ipst.setDouble(colIndexMap.get(colName), Double.valueOf((String) value));
				 }else{
					 ipst.setInt(colIndexMap.get(colName), Integer.valueOf((String)value));
				 }
				 
			}else if("CLOB".equals(colType)){
				ipst.setClob(colIndexMap.get(colName), ((Clob)value).getCharacterStream(), ((Clob)value).length());
			}else if("BLOB".equals(colType)){
				Blob b = (Blob) value;
				ipst.setBlob(colIndexMap.get(colName), b.getBinaryStream(), b.length());
			}
	//		else if("TIMESTAMP(6)".equals(colType)){
	//			String timevalue = ((String) value).substring(0, 10);
	//			ipst.setTime(colIndexMap.get(colName), Time.valueOf(timevalue));
	//		}
		}catch(SQLException e){
			e.printStackTrace();
			throw new RuntimeException("设置preaparedStatement值出错"+e);
		}
		return ipst;
	}
	
	/**
	 * <p>方法名称：delTempData</p>
	 * <p>方法描述：删除源库临时表数据</p>
	 * @param srcMgr
	 * @param tableName
	 * @param pkName		主键字段名称
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  创建   <p>
	 */
	public void delTempData(SimpleDSMgr srcMgr, String tableName, String pkName) {//TODO 不能清空，在操作的时候，表中的数据可能修改
		if(srcMgr==null||CommonUtil.isEmpty(tableName)){
			return ;
		}
		Connection srcConn = srcMgr.getConnection();
		Statement delSt = null;
		StringBuilder delSb = new StringBuilder("DELETE FROM ");
		delSb.append(tablePrefix).append(tableName);
		try {
			delSt = srcConn.createStatement();
			delSt.execute(delSb.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("删除数据出错");
		}finally{
			SimpleJdbc.release(null, delSt, null);
			srcMgr.release(srcConn);
		}
	}

	


	/**
	 * <p>方法名称：queryTempData</p>
	 * <p>方法描述：查询所有临时表中的数据</p>
	 * @param tableName
	 * @param srcMgr
	 * @return
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  创建   <p>
	 */
	public String queryTempData(String tableName,SimpleDSMgr srcMgr, int batchsize, int round) {
		
		valueList.clear();
		
		colNameTypeMap = colNameTypeCache.get(tableName);
		if(colNameTypeMap==null||colNameTypeMap.isEmpty()){
			colNameTypeMap = getTabCols(srcMgr, tableName);
		}
		if(CommonUtil.isEmpty(tableName)||srcMgr==null){
			return null;
		}
		Map<String, Object> valueMap = null;
		String pkName = null;
		Connection srcConn = srcMgr.getConnection();
		
		StringBuilder querySrcSb = new StringBuilder("SELECT ");

		for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
			querySrcSb.append("RESULT.").append(((Entry<String,String>)it.next()).getKey()).append(",");
		}
		querySrcSb.deleteCharAt(querySrcSb.length()-1);
		querySrcSb.append(" FROM( SELECT T.*, ROWNUM RN FROM( SELECT * FROM ").append(tablePrefix).append(tableName);
		querySrcSb.append(" ORDER BY ETLTS ASC) T WHERE ROWNUM<").append(batchsize*(round+1)).append(") RESULT");
		querySrcSb.append(" WHERE RN>").append(batchsize*round);
		querySrcSb.append(" ORDER BY RESULT.ETLTS ASC");
		
		Statement st = null;
		ResultSet rs = null;
		try {
			st  = srcConn.createStatement();
			rs 	= st.executeQuery(querySrcSb.toString());
			String colName = null;
			String value = null;
			while(rs.next()){
				valueMap= new HashMap<String,Object>();
				for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
					colName = ((Entry<String,String>)it.next()).getKey();
					value = rs.getString(colName);
					valueMap.put(colName,value);
				}
				valueList.put(valueMap);
			}
			//获取pk值
			Map<String,String> map = null;
			for(Iterator it= valueList.iterator();it.hasNext();){
				map = (Map<String,String>)it.next();
				pkName = (String)map.get("ETLPKNAME");
				pkValues.add(map.get(pkName));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("查询数据出错"+e);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException("设置列值出错"+e);
		} finally{
			SimpleJdbc.release(null, st, rs);
			srcMgr.release(srcConn);
		}
		return pkName;
	}
	
	
	/**
	 * <p>方法名称：getTabCols</p>
	 * <p>方法描述：获取表的所有列</p>
	 * @param mgr		    连接
	 * @param tableName	    表名
	 * @return Map<String,String>  key:字段名, value:字段数据库类型
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  创建   <p>
	 */
	public Map<String,String> getTabCols(SimpleDSMgr mgr, String tableName){
		
		if(mgr==null||CommonUtil.isEmpty(tableName)){
			throw new RuntimeException("获取表列参数出错");
		}
		Connection conn = mgr.getConnection();
		tableName = tableName.toUpperCase().trim();
		Map<String,String> result = tabColMap.get(tableName);
		if(result!=null&&result.size()>0){
			return result;
		}
		
		result = new HashMap<String,String>();

		String sql = "SELECT COLUMN_NAME,DATA_TYPE FROM USER_TAB_COLS WHERE TABLE_NAME=?";
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, tablePrefix+tableName);
			rs = pst.executeQuery();
			String colName = null;
			String colType = null;
			while(rs.next()){
				colName = rs.getString(1);
				colType = rs.getString(2);
				if(CommonUtil.isNotEmpty(colName)){
					result.put(colName, colType);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			SimpleJdbc.release(null, pst, rs);
			mgr.release(conn);
		}
		tabColMap.put(tableName, result);
		return result;
	}
	
	/**
	 * <p>方法名称：enableTrigger</p>
	 * <p>方法描述：停用启用触发器</p>
	 * @param mgr 			数据源
	 * @param tableName		表名
	 * @param type 			1、启用; 0、停用
	 * @param needRelese	是否需要释放
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  创建   <p>
	 */
	public void enableTrigger(SimpleDSMgr mgr, String tableName, int type){
		
		if(mgr==null||CommonUtil.isEmpty(tableName)||(type!=0&&type!=1)){
			throw new RuntimeException("停启用触发器参数出错");
		}
		Connection conn = mgr.getConnection();
		
		
		String sql = null;
		String sql1 = null;
		if(type==0){
			sql = "ALTER TABLE "+tableName+" DISABLE ALL TRIGGERS";
			sql1 = "UPDATE XFL_TABSTATUS SET STATUS=0 WHERE TABLENAME='"+tableName+"'";
		}else if(type==1){
			sql = "ALTER TABLE "+tableName+" ENABLE ALL TRIGGERS";
			sql1 = "UPDATE XFL_TABSTATUS SET STATUS=1 WHERE TABLENAME='"+tableName+"'";
		}
		Statement st = null;
		try {
			st = conn.createStatement();
			st.execute(sql);
			st.execute(sql1);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("停用启用触发器出错"+e);
		}finally{
			SimpleJdbc.release(null, st, null);
			mgr.release(conn);
		}
		
//		String sql = "UPDATE "+mgrTriggerTabName+" A SET A.STATUS=? WHERE TABLENAME=?";
//		
//		PreparedStatement pst = null;
//		try {
//			pst = conn.prepareStatement(sql);
//			pst.setString(1, String.valueOf(type));
//			pst.setString(2, tableName.toUpperCase().trim());
//			pst.execute();
//		} catch (SQLException e) {
//			e.printStackTrace();
//			throw new RuntimeException("停用启用触发器出错"+e);
//		} finally{
//			JdbcUtils.release(null, pst, null);
//			mgr.release(conn);
//		}
	}
}
