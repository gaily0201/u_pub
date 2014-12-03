package cn.gaily.pub.script;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;

import cn.gaily.pub.util.CommonUtils;
import cn.gaily.simplejdbc.SimpleDSMgr;
import cn.gaily.simplejdbc.SimpleJdbc;
import cn.gaily.simplejdbc.SimpleSession;


/**
 * <p>Title: ScriptExporter</P>
 * <p>Description: 脚本抽取工具</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-11-27
 */
public class ScriptExporter {

	private SimpleDSMgr mgr = null;
	
	private static String encoding  = "GBK";
	
	private String mgrTable = "XFL_TABSTATUS";
	
	private int batchSize = 1024;
	
	Map<String, Map<String,String>> colNameTypeMapCache = new HashMap<String, Map<String,String>>();
	
	
	/**
	 * <p>方法名称：exportAll</p>
	 * <p>方法描述：导出数据库所有表</p>
	 * @param path 保存路径
	 * @author xiaoh
	 * @since  2014-12-3
	 * <p> history 2014-12-3 xiaoh  创建   <p>
	 */
	public void exportAll(String path){
		if(mgr==null){
			mgr = getDataSource();
		}
		List<String> allTabs = getAllUserTabs(mgr);
		for(String tab: allTabs){
			export(path+File.separator+tab.toLowerCase()+".sql", true, tab);
		}
	}
	
	
	
	


	/**
	 * <p>方法名称：export</p>
	 * <p>方法描述：导出变动临时表数据</p>
	 * @param filePath
	 * @author xiaoh
	 * @since  2014-12-3
	 * <p> history 2014-12-3 xiaoh  创建   <p>
	 */
	public void export(String filePath){
		export(filePath, false, null);
	}
	
	/**
	 * <p>方法名称：export</p>
	 * <p>方法描述：根据触发器保存的临时表数据 导出sql补丁脚本</p>
	 * @param filePath  文件路径
	 * @param isOriginal 是否直接表导出
	 * @param tableNames 直接表导出表名
	 * @return
	 * @author xiaoh
	 * @since  2014-11-27
	 * <p> history 2014-11-27 xiaoh  创建   <p>
	 */
	public void export(String filePath, boolean isOriginal, String tableName){
		/**
		 * 1. 获取所有配置的同步表;
		 * 2. 分别获取每个同步表对应临时表中的数据;
		 * 3. 对获取的数据逐条进行构建sql;
		 * 4. 将sql写入文件
		 * 5. 删除临时表数据
		 */
		if(mgr==null||mgr.conns.size()<=0){
			mgr = getDataSource();
		}
		
		List<String> tables = null;
		if(CommonUtils.isEmpty(tableName)){
			tables = getAllTables(mgr);
		}else{
			tables = new ArrayList<String>();
			tables.add(tableName);
		}
		
		ArrayBlockingQueue<Map<String,Object>> 	valueList 	= new ArrayBlockingQueue<Map<String,Object>>(batchSize, false);
		
		int valueCount = 0;
		int round = 0;
		for(String tab: tables){
			if(isOriginal){
				valueCount = SimpleSession.getRecordCount(mgr, tab.trim().toUpperCase());
			}else{
				valueCount = SimpleSession.getRecordCount(mgr, "XFL_"+tab.trim().toUpperCase());
			}
			round = valueCount/batchSize+1;
			for(int i=0;i<round;i++){
				valueList = getValues(mgr, tab, i, isOriginal);
				if(valueList.isEmpty()){
					continue;
				}
				writeFile(valueList, tab, filePath, isOriginal);
			}
		}
		mgr.realRelease();
	}
	
	
	
	private void writeFile(ArrayBlockingQueue<Map<String, Object>> valueList, String tableName, String filePath, boolean isOriginal) {
		if(valueList.isEmpty()||CommonUtils.isEmpty(tableName)){
			return ;
		}
		Map<String, Object> valueMap = null;
		String sql = null;
		try{
			while(!valueList.isEmpty()){
				valueMap = (Map<String, Object>) valueList.poll();
				if(isOriginal){
					sql = buildSql(valueMap, colNameTypeMapCache.get(tableName.trim().toUpperCase()), tableName, isOriginal);
				}else{
					sql = buildSql(valueMap, colNameTypeMapCache.get("XFL_"+tableName.trim().toUpperCase()), tableName, isOriginal);
				}
				System.out.println(sql);
				write(filePath, sql, true); //写sql
			}
			write(filePath, "commit;", true); //写commit
		}catch(IOException e){
			e.printStackTrace();
		}
	}



	private String buildSql(Map<String, Object> valueMap, Map<String, String> colNameTypeMap, String tableName, boolean isOriginal) {
		String sql = null;
		int type = 1;
		if(!isOriginal){
			type = Integer.valueOf(String.valueOf(valueMap.get("ETLSTATUS")));  //增删改，状态
		}
		switch(type){
			case 1:
				sql = buildInSql(valueMap,colNameTypeMap,tableName);
				break;
			case 2:
				sql = buildUpSql(valueMap,colNameTypeMap, tableName,isOriginal);
				break;
			case 3:
				sql = buildDeSql(valueMap,colNameTypeMap, tableName,isOriginal);
				break;
			default:
				break;
		}
		return sql;
	}



	private String buildDeSql(Map<String, Object> valueMap, Map<String, String> colNameTypeMap, String tableName, boolean isOriginal) {
		String pkColumn = null;
		if(isOriginal){
			pkColumn = SimpleSession.getPkName(mgr, tableName);
		}else{
			pkColumn = String.valueOf(valueMap.get("ETLPKNAME"));
		}
		String pkValue = String.valueOf(valueMap.get(pkColumn));
		StringBuilder sb = new StringBuilder("DELETE FROM").append(tableName).append(" WHERE ").append(pkColumn);
		sb.append("='").append(pkValue).append("';");
		return sb.toString();
	}



	private String buildUpSql(Map<String, Object> valueMap, Map<String, String> colNameTypeMap, String tableName, boolean isOriginal) {
		String pkColumn = null;
		if(isOriginal){
			pkColumn = SimpleSession.getPkName(mgr, tableName);
		}else{
			pkColumn = String.valueOf(valueMap.get("ETLPKNAME"));
		}
		String pkValue = String.valueOf(valueMap.get(pkColumn));
		StringBuilder sb = new StringBuilder("UPDATE ");
		sb.append(tableName).append(" SET ");
		Entry<String,Object> entry  = null;
		
		String colName = null;
		Object colValue = null;
		String colType = null;
		for(Iterator it=valueMap.entrySet().iterator();it.hasNext();){
			entry = (Entry<String, Object>) it.next();
			colName = entry.getKey();
			if("ETLTS".equalsIgnoreCase(colName)||"ETLSTATUS".equalsIgnoreCase(colName)||"ETLPKNAME".equalsIgnoreCase(colName)){
				continue;
			}
			colValue = entry.getValue();
			colType = colNameTypeMap.get(colName);
			if("VARCHAR2".equals(colType)||"NVARCHAR2".equals(colType)||"CHAR".equals(colType)){
				if("NULL".equalsIgnoreCase(String.valueOf(colValue))){
					sb.append(colName).append("=").append(colValue).append(",");
				}else{
					sb.append(colName).append("='").append(colValue).append("',");
				}
			}else if("FLOAT".equals(colType)||"NUMBER".equals(colType)){
				sb.append(colName).append("=").append(colValue).append(",");
			}else{
				continue;
			}
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(" WHERE ").append(pkColumn).append("='").append(pkValue).append("';");
		return sb.toString();
	}



	private String buildInSql(Map<String, Object> valueMap, Map<String, String> colNameTypeMap, String tableName) {
		StringBuilder csb = new StringBuilder("INSERT INTO ").append(tableName).append("(");
		StringBuilder vsb = new StringBuilder(") VALUES(");
		
		Entry<String,Object> entry  = null;
		String colName = null;
		Object colValue = null;
		String colType = null;
		for(Iterator it=valueMap.entrySet().iterator();it.hasNext();){
			entry = (Entry<String, Object>) it.next();
			colName = entry.getKey();
			if("ETLTS".equalsIgnoreCase(colName)||"ETLSTATUS".equalsIgnoreCase(colName)||"ETLPKNAME".equalsIgnoreCase(colName)){
				continue;
			}
			colValue = entry.getValue();
			colType = colNameTypeMap.get(colName);
			if("VARCHAR2".equals(colType)||"NVARCHAR2".equals(colType)||"CHAR".equals(colType)){
				if("NULL".equalsIgnoreCase(String.valueOf(colValue))){
					continue;
				}else{
					vsb.append("'").append(colValue).append("',");
				}
			}else if("FLOAT".equals(colType)||"NUMBER".equals(colType)){
				vsb.append(colValue).append(",");
			}else{
				continue;
			}
			csb.append(colName+",");
		}
		
		csb.deleteCharAt(csb.length()-1);
		vsb.deleteCharAt(vsb.length()-1);
		vsb.append(");");
		csb.append(vsb);
		return csb.toString();
	}



	private ArrayBlockingQueue<Map<String, Object>> getValues(SimpleDSMgr srcMgr, String tableName, int round, boolean isOriginal) {
		if(CommonUtils.isEmpty(tableName)||srcMgr==null){
			return null;
		}
		Map<String, Object> valueMap = null;
		String pkName = null;
		Connection srcConn = srcMgr.getConnection();
		
		ArrayBlockingQueue<Map<String,Object>> 	valueList 	= new ArrayBlockingQueue<Map<String,Object>>(batchSize, false);
		
		StringBuilder querySrcSb = new StringBuilder("SELECT ");
		Map<String,String> colNameTypeMap  = null;
		if(isOriginal){
			colNameTypeMap = getTabCols(srcMgr, tableName);
		}else{
			colNameTypeMap = getTabCols(srcMgr, "XFL_"+tableName);
		}
		if(colNameTypeMap.isEmpty()){
			return null;
		}
		for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
			querySrcSb.append("result.").append(((Entry<String,String>)it.next()).getKey()).append(",");
		}
		pkName = SimpleSession.getPkName(srcMgr, tableName.toUpperCase());
		querySrcSb.deleteCharAt(querySrcSb.length()-1);
		if(isOriginal){
			querySrcSb.append(" FROM (SELECT T.*,ROWNUM RN FROM (SELECT * FROM ").append(tableName).append(" A");
			querySrcSb.append(") T").append(" WHERE ROWNUM<=").append((round+1)*batchSize).append(") RESULT WHERE RN>").append(round*batchSize);
		}else{
			querySrcSb.append(" FROM (SELECT T.*,ROWNUM RN FROM (SELECT * FROM XFL_").append(tableName).append(" A ORDER BY A.ETLTS DESC");
			querySrcSb.append(") T").append(" WHERE ROWNUM<=").append((round+1)*batchSize).append(") RESULT WHERE RN>").append(round*batchSize);
		}
		Statement st = null;
		ResultSet rs = null;
		try {
			st  = srcConn.createStatement();
			rs 	= st.executeQuery(querySrcSb.toString());
			String colName = null;
			Object value = null;
			while(rs.next()){
				valueMap= new HashMap<String,Object>();
				for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
					colName = ((Entry<String,String>)it.next()).getKey();
					value = rs.getObject(colName);
					valueMap.put(colName,value);
				}
				valueList.put(valueMap);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(tableName+"查询数据出错"+e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			SimpleJdbc.release(null, st, rs);
			srcMgr.release(srcConn);
		}
		
		return valueList;
	}



	/**
	 * <p>方法名称：getAllTables</p>
	 * <p>方法描述：获取所有需到处的表</p>
	 * @param mgr
	 * @return
	 * @author xiaoh
	 * @since  2014-11-27
	 * <p> history 2014-11-27 xiaoh  创建   <p>
	 */
	private List<String> getAllTables(SimpleDSMgr mgr) {
		if(mgr==null){
			return null;
		}
		String sql = "SELECT TABLENAME FROM "+mgrTable+" WHERE STATUS='2'";  //类型2为监控启用
		List<String> list = new ArrayList<String>();
		Connection conn = mgr.getConnection();
		Statement st = null;
		ResultSet rs = null;
		String value = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while(rs.next()){
				value = rs.getString(1);
				if(CommonUtils.isNotEmpty(value)){
					list.add(value);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			SimpleJdbc.release(null, st, rs);
			mgr.release(conn);
		}
		return list;
	}


	private List<String> getAllUserTabs(SimpleDSMgr mgr) {
		if(mgr==null){
			return null;
		}
		String sql = "SELECT TABLE_NAME FROM USER_TABLES"; 
		List<String> list = new ArrayList<String>();
		Connection conn = mgr.getConnection();
		Statement st = null;
		ResultSet rs = null;
		String value = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while(rs.next()){
				value = rs.getString(1);
				if(CommonUtils.isNotEmpty(value)){
					list.add(value);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			SimpleJdbc.release(null, st, rs);
			mgr.release(conn);
		}
		return list;
	}
	

	/**
	 * <p>方法名称：getDataSource</p>
	 * <p>方法描述：初始化数据源信息</p>
	 * @return
	 * @author xiaoh
	 * @since  2014-11-27
	 * <p> history 2014-11-27 xiaoh  创建   <p>
	 */
	private SimpleDSMgr getDataSource() {
		SimpleDSMgr mgr = new SimpleDSMgr();
		mgr.setDbName("orcl");
		mgr.setIp("127.0.0.1");
		mgr.setUserName("uap63");
		mgr.setPassword("1");
		mgr.setPort("1521");
		mgr.setInitSize(2);
		mgr.init();
		return mgr;
	}


	
	public Map<String,String> getTabCols(SimpleDSMgr mgr, String tableName){
		
		if(mgr==null||CommonUtils.isEmpty(tableName)){
			throw new RuntimeException(tableName+"获取表列参数出错");
		}
		
		Connection conn = mgr.getConnection();
		tableName = tableName.toUpperCase().trim();
		Map<String,String> result = colNameTypeMapCache.get(tableName.trim().toUpperCase());
		if(result!=null&&result.size()>0){
			return result;
		}
		
		result = new HashMap<String,String>();

		String sql = "SELECT COLUMN_NAME,DATA_TYPE FROM USER_TAB_COLS WHERE TABLE_NAME=?";
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, tableName);
			rs = pst.executeQuery();
			String colName = null;
			String colType = null;
			while(rs.next()){
				colName = rs.getString(1);
				colType = rs.getString(2);
				if(CommonUtils.isNotEmpty(colName)){
					result.put(colName, colType);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			SimpleJdbc.release(null, pst, rs);
			mgr.release(conn);
		}
		colNameTypeMapCache.put(tableName.trim().toUpperCase(), result);
		return result;
	}


	public static void write(String filePath, String data, boolean append) throws IOException{
		if(CommonUtils.isEmpty(filePath)||CommonUtils.isEmpty(data)){
			return;
		}
		data = data+"\r\n";
		File file = new File(filePath);
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		if(!file.exists()){
			file.createNewFile();
		}
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), encoding));
		writer.write(data);
		writer.flush();
		writer.close();
	}
	
	
	public void setMgr(SimpleDSMgr mgr) {
		this.mgr = mgr;
	}
}
