package cn.gaily.pub.trigger;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
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

import cn.gaily.pub.util.CommonUtils;
import cn.gaily.simplejdbc.SimpleDSMgr;
import cn.gaily.simplejdbc.SimpleJdbc;
import cn.gaily.simplejdbc.SimpleSession;


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
	 * 默认批量提交大小
	 */
	private int DEFAULT_BATCHSIZE =500;

	/**
	 * 批量查询大小
	 */
	public int batchSize = DEFAULT_BATCHSIZE;
	
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
	
	private boolean isSyn = false;
	
	public void clear(){
		valueList.clear();
		pkValues.clear();
		colIndexMap.clear();
		colNameTypeMap.clear();
		
	}
	
	public Map<String,String> execute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName){
		return execute(srcMgr, tarMgr, tableName,  false);
	}
	
	/**
	 * <p>方法名称：execute</p>
	 * <p>方法描述：执行数据预置</p>
	 * @param srcMgr
	 * @param tarMgr
	 * @param tableName
	 * @param isSyn 	是否是历史数据同步任务
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  创建   <p>
	 */
	public Map<String,String> execute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName, boolean isSyn){
		if(srcMgr==null||tarMgr==null||CommonUtils.isEmpty(tableName)){
			throw new RuntimeException(tableName+"执行前数据预置参数出错");
		}
		
		setIsSyn(isSyn);
		
		clear();
		
		tarMgr = enableTrigger(tarMgr, tableName, DISABLE);  //停用触发器 //TODO 存在问题：在执行期间，数据可能丢失
		
		String pkName = null;
		Boolean canBatch =  null;
		String status = null;
		int round = 0;
		
		int add = 0;
		int update = 0;
		int delete = 0;
		boolean cbreak = false;
		int roundi = 0;
		int m = 0;
		while(true){
			
			long start = System.currentTimeMillis();
			
			System.out.println(++round);
			Map<String,Object> recvMap = null;
			if(isSyn){
				recvMap = queryTempData(tableName, srcMgr, roundi++, true);  //同步历史数据
				status = "1";
			}else{
				recvMap = queryTempData(tablePrefix+tableName, srcMgr, 0, false);
				status = (String) recvMap.get("status");
			}
			canBatch = (Boolean) recvMap.get("canBatch");
			pkName = (String) recvMap.get("pkName");
			
			if(canBatch==null||CommonUtils.isEmpty(status)){
				break;
			}
			if((Boolean)recvMap.get("break")){
				break;
			}
			AbstractETLTask task = null;
			if(canBatch){ //TODO 有BLOB时候，改成不用batch
				int type = Integer.valueOf(status);
				switch(type){
				case NEW:
					task = ETLInsertTask.getInstance();
					int i =  task.doBatch(srcMgr, tarMgr, tableName, pkName,  valueList, colNameTypeMap, canBatch);
					add +=i;
//					if(i==0&&isSyn==true){
//						m++;
//						if(m>=5){
//							cbreak = true;
//						}
//					}else{
//						cbreak = false;
//					}
					break;
				case UPDATE:
					task = ETLUpdateTask.getInstance();
					update += task.doBatch(srcMgr, tarMgr, tableName, pkName,  valueList, colNameTypeMap, canBatch);
					break;
				case DELETE:
					task = ETLDeleteTask.getInstance();
					delete += task.doBatch(srcMgr, tarMgr, tableName, pkName,  valueList, colNameTypeMap, canBatch);
					break;
				default:
					throw new RuntimeException(tableName+"出错");
				}
				
			}else{
				Map<String,Object> map = null;
				int i=0;
				for(Iterator it= valueList.iterator();it.hasNext();){
					map = (Map<String, Object>) valueList.poll();
					if(map==null){
						break;
					}
					status = (String) map.get("ETLSTATUS");
					if(CommonUtils.isEmpty(status)){
						continue;
					}
					
					i++;
					
					int type = Integer.valueOf(status);
					switch(type){
					case NEW:
						task = ETLInsertTask.getInstance();
						task.doexecute(srcMgr, tarMgr, tableName, pkName,  map, colNameTypeMap);
						add++;
						break;
					case UPDATE:
						task = ETLUpdateTask.getInstance();
						task.doexecute(srcMgr, tarMgr, tableName, pkName,  map, colNameTypeMap);
						update++;
						break;
					case DELETE:
						task = ETLDeleteTask.getInstance();
						task.doexecute(srcMgr, tarMgr, tableName, pkName,  map, colNameTypeMap);
						delete++;
						break;
					default:
						throw new RuntimeException(tableName+"出错");
					}
					
				}
			}
			
			long end = System.currentTimeMillis();
			System.out.println("costs: " +(end-start) +"ms");
		}
		
		enableTrigger(tarMgr, tableName, ENABLE);  //恢复触发器
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("add", String.valueOf(add));
		map.put("update", String.valueOf(update));
		map.put("delete", String.valueOf(delete));
		
		return map;
	}
	
	/**
	 * <p>方法名称：doBatch</p>
	 * <p>方法描述：执行批量</p>
	 * @param srcMgr
	 * @param tarMgr
	 * @param tableName
	 * @param pkName
	 * @param valueList
	 * @param colNameTypeMap
	 * @param canBatch
	 * @author xiaoh
	 * @since  2014-11-5
	 * <p> history 2014-11-5 xiaoh  创建   <p>
	 */
	public abstract int doBatch(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr,
								 String tableName, String pkName,
								 ArrayBlockingQueue<Map<String, Object>> valueList,
								 Map<String, String> colNameTypeMap, Boolean canBatch);

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
		ByteArrayInputStream bis = null;
		try{
			if(value==null){
				 ipst.setNull(colIndexMap.get(colName), 0);
			 }else if("VARCHAR2".equals(colType)||"NVARCHAR2".equals(colType)||"CHAR".equals(colType)){
				ipst.setString(colIndexMap.get(colName), (String)value);
			}else if("DATE".equals(colType)){
				Date date = null;
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(CommonUtils.DATE_FORMATER_YYYY_MM_DD_TIME);
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
				 if((String.valueOf(value)).contains(".")){
					 ipst.setDouble(colIndexMap.get(colName), Double.valueOf((String.valueOf(value))));
				 }else{
					 ipst.setBigDecimal(colIndexMap.get(colName), new BigDecimal(String.valueOf(value)));
				 }
				 
			}else if("CLOB".equals(colType)){
				ipst.setClob(colIndexMap.get(colName), ((Clob)value).getCharacterStream(), ((Clob)value).length());
			}else if("BLOB".equals(colType)){
				Blob b = (Blob) value;
				bis = new ByteArrayInputStream(b.getBytes(1L, (int)b.length()));
				ipst.setBinaryStream(colIndexMap.get(colName), bis, bis.available());
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
	 */@Deprecated
	public void delTempData(SimpleDSMgr srcMgr, String tableName, String pkName) {//TODO 不能清空，在操作的时候，表中的数据可能修改
		if(srcMgr==null||CommonUtils.isEmpty(tableName)){
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
	 * <p>方法描述：查询临时表中的数据</p>
	 * @param tableName
	 * @param srcMgr
	 * @param isSyn	是否为同步历史数据
	 * @return canBatch 是否能批量
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  创建   <p>
	 */
	public Map<String,Object> queryTempData(String tableName,SimpleDSMgr srcMgr, int round, boolean isSyn) {
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		
		Boolean canBatch = true;
		
		valueList.clear();
//		pkValues.clear();
		
		colNameTypeMap = colNameTypeCache.get(tableName);
		if(colNameTypeMap==null||colNameTypeMap.isEmpty()){
			colNameTypeMap = getTabCols(srcMgr, tableName, isSyn);
			colNameTypeCache.put(tableName, colNameTypeMap);
		}
		if(CommonUtils.isEmpty(tableName)||srcMgr==null){
			return null;
		}
		Map<String, Object> valueMap = null;
		String pkName = null;
		Connection srcConn = srcMgr.getConnection();
		
		StringBuilder querySrcSb = new StringBuilder("SELECT ");
		if(colNameTypeMap.isEmpty()){
			return null;
		}
		if(!isSyn){
			for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
				querySrcSb.append("").append(((Entry<String,String>)it.next()).getKey()).append(",");
			}
			querySrcSb.deleteCharAt(querySrcSb.length()-1);
			querySrcSb.append(" FROM ").append(tableName).append(" WHERE ROWNUM<=").append(batchSize);
			querySrcSb.append(" ORDER BY ETLTS ASC");
		}else{
			for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
				querySrcSb.append("result.").append(((Entry<String,String>)it.next()).getKey()).append(",");
			}
			pkName = SimpleSession.getPkName(srcMgr, tableName.toUpperCase());
			querySrcSb.deleteCharAt(querySrcSb.length()-1);
			querySrcSb.append(" FROM (SELECT T.*,ROWNUM RN FROM (SELECT * FROM ").append(tableName).append(" A, ETL_PK_TEMP B WHERE B.TABLENAME='");
			querySrcSb.append(tableName.toUpperCase()).append("' AND A.").append(pkName).append("=B.PKVALUE) T");
			querySrcSb.append(" WHERE ROWNUM<=").append((round+1)*batchSize).append(") RESULT WHERE RN>").append(round*batchSize);
		}
		Statement st = null;
		ResultSet rs = null;
		String status = null;
		boolean canBreak = true;
		try {
			st  = srcConn.createStatement();
			rs 	= st.executeQuery(querySrcSb.toString());
			String colName = null;
			Object value = null;
			while(rs.next()){
				canBreak = false;
				valueMap= new HashMap<String,Object>();
				for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
					colName = ((Entry<String,String>)it.next()).getKey();
					value = rs.getObject(colName);
					if("ETLSTATUS".equals(colName)){
						if(status==null){
							status=(String) value;
						}else if(!status.equals(value)){
							canBatch = false;
							status=(String) value;
						}else{
							status = (String) value;
						}
					}
					if(CommonUtils.isEmpty(pkName)&&"ETLPKNAME".equals(colName)){
						pkName = (String) value;
					}
					if(CommonUtils.isEmpty(pkName)&&isSyn){
						pkName = SimpleSession.getPkName(srcMgr, tableName.toUpperCase());
					}

					valueMap.put(colName,value);
				}
				valueList.put(valueMap);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(tableName+"查询数据出错"+e);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(tableName+"设置列值出错"+e);
		} finally{
			SimpleJdbc.release(null, st, rs);
			srcMgr.release(srcConn);
		}
		returnMap.put("canBatch", canBatch);
		returnMap.put("status", status);
		returnMap.put("pkName", pkName);
		returnMap.put("break", canBreak);
		return returnMap;
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
	public Map<String,String> getTabCols(SimpleDSMgr mgr, String tableName, boolean isSyn){
		
		if(mgr==null||CommonUtils.isEmpty(tableName)){
			throw new RuntimeException(tableName+"获取表列参数出错");
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
				if("BLOB".equalsIgnoreCase(colType)||"CLOB".equalsIgnoreCase(colType)){
					//TODO set batch size to 50
					this.setBatchSize(50);
				}else{
					this.setBatchSize(DEFAULT_BATCHSIZE);
				}
			}
			if(!isSyn){
				result.put("ETLTS", "VARCHAR2");
				result.put("ETLSTATUS", "CHAR");
				result.put("ETLPKNAME", "VARCHAR2");
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
	 * @param type 			1、启用; 0、停用; 2、启用监控; 3、停用监控
	 * @param needRelese	是否需要释放
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  创建   <p>
	 */
	public SimpleDSMgr enableTrigger(SimpleDSMgr mgr, String tableName, int type){
		
		if(mgr==null||CommonUtils.isEmpty(tableName)||(type!=0&&type!=1&&type!=2&&type!=3)){
			throw new RuntimeException(tableName+"停启用触发器参数出错");
		}
		Connection conn = mgr.getConnection();
		
		ensureMgrTable(mgr,"XFL_TABSTATUS");
		
		String sql = null;
		String tsql1 = "ALTER TRIGGER XFL_"+tableName+"_1 DISABLE";
		String tsql2 = "ALTER TRIGGER XFL_"+tableName+"_2 DISABLE";
		String tsql3 = "ALTER TRIGGER XFL_"+tableName+"_3 DISABLE";
		
		String tsql11 = "ALTER TRIGGER XFL_"+tableName+"_1 ENABLE";
		String tsql22 = "ALTER TRIGGER XFL_"+tableName+"_2 ENABLE";
		String tsql33 = "ALTER TRIGGER XFL_"+tableName+"_3 ENABLE";
		
		
		Statement st = null;
		try {
			conn.setAutoCommit(false);
			st = conn.createStatement();
			if(type==0){
				sql = "UPDATE XFL_TABSTATUS SET STATUS=0 WHERE TABLENAME='"+tableName+"'";
				st.executeUpdate(sql);
				st.executeUpdate(tsql1);
				st.executeUpdate(tsql2);
				st.executeUpdate(tsql3);
			}else if(type==1){
				sql = "UPDATE XFL_TABSTATUS SET STATUS=1 WHERE TABLENAME='"+tableName+"'";
				st.executeUpdate(sql);
				st.executeUpdate(tsql11);
				st.executeUpdate(tsql22);
				st.executeUpdate(tsql33);
			}else if(type==2){
				sql = "UPDATE XFL_TABSTATUS SET STATUS=2 WHERE TABLENAME='"+tableName+"'";
				st.executeUpdate(sql);
				st.executeUpdate(tsql11);
				st.executeUpdate(tsql22);
				st.executeUpdate(tsql33);
			}else if(type==3){
				sql = "UPDATE XFL_TABSTATUS SET STATUS=3 WHERE TABLENAME='"+tableName+"'";
				st.executeUpdate(sql);
				st.executeUpdate(tsql1);
				st.executeUpdate(tsql2);
				st.executeUpdate(tsql3);
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
//			throw new RuntimeException("停用启用触发器出错"+e);
		}finally{
			SimpleJdbc.release(null, st, null);
			mgr.release(conn);
		}
		return mgr;
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
	
	/**
	 * <p>方法名称：delTemp</p>
	 * <p>方法描述：删除临时表数据</p>
	 * @param pkName		主键字段
	 * @param insertPks		pk值
	 * @param tableName		表名
	 * @author xiaoh
	 * @since  2014-11-7
	 * <p> history 2014-11-7 xiaoh  创建   <p>
	 */
	protected Connection delTemp(String pkName, List<String> insertPks, String tableName, Connection conn, int type, boolean batch) {
		if(CommonUtils.isEmpty(pkName)||CommonUtils.isEmpty(tableName)||conn==null||(type!=1&&type!=2&&type!=3)){
			throw new RuntimeException(tableName+"删除临时表数据传入参数不能为空");
		}
		
		if(isSyn){   		//为同步，则不用执行该方法
			return conn;
		}
		if(insertPks.size()<=0){
			return conn;
		}
		
		int delSize = 500;
		
		if(delSize>batchSize){
			delSize = batchSize;
		}
		
		StringBuilder sb = null;
		String value = null;
		Statement st = null;
		
		int round = insertPks.size()/delSize + 1;
		
		int deleteCount = 0;
		try {
			st = conn.createStatement();
			
			for(int i=0;i<round;i++){
				sb = new StringBuilder("DELETE FROM ");
				sb.append(tableName).append(" WHERE ").append(pkName);
				sb.append(" IN('");
				for(int j=0;j<delSize&&insertPks.size()>0;j++){
					value = insertPks.remove(0);
					if(CommonUtils.isEmpty(value)){
						continue;
					}else{
						sb.append(value).append("','");
					}
				}
				
				sb.deleteCharAt(sb.length()-1);
				sb.deleteCharAt(sb.length()-1);
				sb.append(")");
				if(!sb.toString().contains("(")){
					return conn;
				}
				sb.append(" AND ETLSTATUS='").append(String.valueOf(type)).append("'");
				if(batch){
					sb.append(" AND ROWNUM<=").append(delSize);
				}else{
					sb.append(" AND ROWNUM<=").append(1);  //fix 141109限定范围删除
				}
				System.out.println(sb);
				int cc = st.executeUpdate(sb.toString());
				System.out.println("delete "+cc+ " record in table:"+ tableName);
				deleteCount+=cc;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(tableName+"删除临时表数据异常！"+e);
		} finally{
			SimpleJdbc.release(null, st, null);
		}
		return conn;
	}
	
	private boolean ensureMgrTable(SimpleDSMgr mgr , String tableName){
		if(mgr==null){
			throw new RuntimeException(tableName+"请添加数据源");
		}
		Connection conn = mgr.getConnection();
		if(conn==null){
			return false;
		}
		if(CommonUtils.isEmpty(tableName)){
			throw new RuntimeException(tableName+"请传入有效的表名");
		}
		String sql = "SELECT COUNT(1) FROM USER_TABLES WHERE TABLE_NAME=?";
		Statement st = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, tableName.toUpperCase().trim());
			rs = pst.executeQuery();
			st = conn.createStatement();
			if(rs.next()&&"1".equals(rs.getString(1))){
				return true;  //fix 临时表存在,可能含有数据,不删除直接返回
			}
			st.executeUpdate("CREATE TABLE "+tableName+"(TABLENAME VARCHAR2(30),STATUS CHAR(1))");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			SimpleJdbc.release(null, pst, rs);
			mgr.release(conn);
		}
		return false;
	}
	
	
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public void setIsSyn(boolean isSyn) {
		this.isSyn = isSyn;
	}
	
	
}
