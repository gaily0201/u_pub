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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.gaily.pub.util.CommonUtil;
import cn.gaily.pub.util.JdbcUtils;

/**
 * <p>Title: TriggerBaseTask</P>
 * <p>Description: 同步数据库增、删、改执行基础类</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-28
 */
public class TriggerBaseTask {

	public int DSSIZE = 5;
	
//	private SimpleDSMgr remote = new SimpleDSMgr();

//	private SimpleDSMgr local = new SimpleDSMgr();
	
	/**
	 * 触发器管理表
	 */
	public String mgrTriggerTabName = "XFL_TABSTATUS";
	
	/**
	 * 临时表前缀
	 */
	public String tablePrefix = "XFL_";
	
	/**
	 * 表名字段名map，key:表名大写,value:字段名,字段类型map
	 */
	private Map<String,Map<String,String>> tabColMap = new HashMap<String,Map<String,String>>();
	
	/**
	 * 保存列值，Map<String,Object> key:列名, value:列值
	 */
	private List<Map<String,Object>> valueList 		= new ArrayList<Map<String,Object>>(); //值list,map中保存<字段名，字段值>
	/**
	 * 操作的pk值，以备回删数据
	 */
	private List<String> 			 pkValues 		= new ArrayList<String>();
	/**
	 * 列名和类型对应关系
	 */
	private Map<String,String> 		 colNameTypeMap	= new HashMap<String,String>();
	/**
	 * 列和索引映射map,值插入设值
	 */
	private Map<String,Integer> 	 colIndexMap 	= new HashMap<String,Integer>();
	
	
	
	private TriggerBaseTask(){
	}
	
	public TriggerBaseTask getInstance(){
		synchronized (this) {
			return new TriggerBaseTask();
		}
	}
	
	public void clear(){
		valueList.clear();
		pkValues.clear();
		colNameTypeMap.clear();
		colIndexMap.clear();
		
	}
	
	/**
	 * <p>方法名称：dealAdd</p>
	 * <p>方法描述：处理新增数据</p>
	 * @param tableName 表名
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  创建   <p>
	 */
	public void dealAdd(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName){
		clear();
		
		if(srcMgr==null||tarMgr==null||CommonUtil.isEmpty(tableName)){
			throw new RuntimeException("新增数据库操作参数出错");
		}
		colNameTypeMap = getTabCols(srcMgr, tableName, false);
		String pkName = null;
		//1、查询源库临时表新增数据
		pkName = queryTempData(tableName, srcMgr, 1);
		
		//2、停用目标库触发器
		enableTrigger(tarMgr, tableName, 0);
		
		//3、执行目标库数据库插入
		doAddInsert(tarMgr, tableName);
		
		//4、启用目标库触发器
		enableTrigger(tarMgr, tableName, 1);
		
		//5、删除源库临时表新增数据
		delTempData(srcMgr, tableName, pkName, 1);
	}

	/**
	 * 删除源库临时表数据
	 * @param srcMgr
	 * @param tableName
	 * @param pkName
	 * @param srcConn
	 */
	private void delTempData(SimpleDSMgr srcMgr, String tableName, String pkName, int type) {
		Connection srcConn = srcMgr.getConnection();
		Statement delSt = null;
		StringBuilder delSb = new StringBuilder("DELETE FROM ");
		delSb.append(tablePrefix).append(tableName);
		delSb.append(" WHERE ").append(pkName).append(" IN(");
		for(int i=0;i<pkValues.size();i++){
			delSb.append("'").append(pkValues.get(i)).append("',");
		}
		delSb.deleteCharAt(delSb.length()-1);
		delSb.append(") AND ETLSTATUS=").append(type);
		try {
			delSt = srcConn.createStatement();
			delSt.execute(delSb.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			JdbcUtils.release(null, delSt, null);
			srcMgr.release(srcConn);
		}
	}

	/**
	 * 执行目标库数据库插入
	 * @param tarMgr
	 * @param tableName
	 */
	private void doAddInsert(SimpleDSMgr tarMgr, String tableName) {
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
					if(!"ETLSTATUS".equalsIgnoreCase(colName)&&!"ETLPKNAME".equalsIgnoreCase(colName)&&!"ETLTS".equalsIgnoreCase(colName)){
						if("VARCHAR2".equals(colType)||"NVARCHAR2".equals(colType)||"CHAR".equals(colType)){
							ipst.setString(colIndexMap.get(colName), (String)value);
						}else if("DATE".equals(colType)){
							Date date = null;
							try {
								date = new Date(java.text.SimpleDateFormat.getInstance().parse(((String)value).substring(0, 19)).getTime());
							} catch (ParseException e) {
								e.printStackTrace();
							}
							ipst.setDate(colIndexMap.get(colName), date);
						}else if("FLOAT".equals(colType)){
							ipst.setFloat(colIndexMap.get(colName), Float.valueOf((String) value));
						}
//						else if("TIMESTAMP(6)".equals(colType)){
//							String timevalue = ((String) value).substring(0, 10);
//							ipst.setTime(colIndexMap.get(colName), Time.valueOf(timevalue));
//						}
						 else if("NUMBER".equals(colType)){
							ipst.setDouble(colIndexMap.get(colName), Double.valueOf((String) value));
						}else if("CLOB".equals(colType)){
							ipst.setClob(colIndexMap.get(colName), ((Clob)value).getCharacterStream(), ((Clob)value).length());
						}else if("BLOB".equals(colType)){
							Blob b = (Blob) value;
							ipst.setBlob(colIndexMap.get(colName), b.getBinaryStream(), b.length());
						}
					}
				}
				ipst.addBatch();
			}
			ipst.executeBatch();
			targetConn.commit();
			ipst.clearBatch();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("插入数据库异常");
		}finally{
			JdbcUtils.release(null, ipst, null);
			tarMgr.release(targetConn);
		}
	}


	/**
	 * 查询数据，返回主键字段
	 * @param tableName
	 * @param srcMgr
	 * @param type
	 * @return
	 */
	private String queryTempData(String tableName,SimpleDSMgr srcMgr, int type) {
		Map<String, Object> valueMap = null;
		String pkName = null;
		Connection srcConn = srcMgr.getConnection();
		
		StringBuilder querySrcSb = new StringBuilder("SELECT ");

		for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
			querySrcSb.append(((Entry<String,String>)it.next()).getKey()).append(",");
		}
		querySrcSb.deleteCharAt(querySrcSb.length()-1);
		querySrcSb.append(" FROM ").append(tablePrefix).append(tableName);
		querySrcSb.append(" WHERE ETLSTATUS=").append(type);
		querySrcSb.append(" ORDER BY ETLTS ASC");
		
		Statement st = null;
		ResultSet rs = null;
		try {
			st  = srcConn.createStatement();
			rs 	= st.executeQuery(querySrcSb.toString());
			String colName = null;
			while(rs.next()){
				valueMap= new HashMap<String,Object>();
				for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
					colName = ((Entry<String,String>)it.next()).getKey();
					valueMap.put(colName,rs.getString(colName));
				}
				valueList.add(valueMap);
			}
			//获取pk值
			for(int i=0;i<valueList.size();i++){
				pkName = (String) valueList.get(i).get("ETLPKNAME");
				pkValues.add((String) valueList.get(i).get(pkName));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("查询数据出错"+e);
		} finally{
			JdbcUtils.release(null, st, rs);
			srcMgr.release(srcConn);
		}
		return pkName;
	}
	
	/**
	 * <p>方法名称：dealUpdate</p>
	 * <p>方法描述：处理更新数据</p>
	 * @param tableName 表名
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  创建   <p>
	 */
	public void dealUpdate(String tableName){
		
	}
	
	/**
	 * <p>方法名称：dealDel</p>
	 * <p>方法描述：处理删除数据</p>
	 * @param tableName 表名
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  创建   <p>
	 */
	public void dealDel(String tableName){
		
	}
	
	/**
	 * <p>方法名称：getTabCols</p>
	 * <p>方法描述：获取表的所有列</p>
	 * @param mgr		    连接
	 * @param tableName	    表名
	 * @param needRelease 是否需要释放连接
	 * @return Map<String,String>  key:字段名, value:字段数据库类型
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  创建   <p>
	 */
	private Map<String,String> getTabCols(SimpleDSMgr mgr, String tableName, boolean needRelease){
		
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
			JdbcUtils.release(null, pst, rs);
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
	 * @param type 			1、启用; 0、启用
	 * @param needRelese	是否需要释放
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  创建   <p>
	 */
	private void enableTrigger(SimpleDSMgr mgr, String tableName, int type){
		
		if(mgr==null||CommonUtil.isEmpty(tableName)||(type!=0&&type!=1)){
			throw new RuntimeException("停启用触发器参数出错");
		}
		Connection conn = mgr.getConnection();
		String sql = "UPDATE "+mgrTriggerTabName+" A SET A.STATUS=? WHERE TABLENAME=?";
		
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, String.valueOf(type));
			pst.setString(2, tableName.toUpperCase().trim());
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("停用启用触发器出错"+e);
		} finally{
			JdbcUtils.release(null, pst, null);
			mgr.release(conn);
		}
	}
}
