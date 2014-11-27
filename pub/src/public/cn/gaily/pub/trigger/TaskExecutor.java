package cn.gaily.pub.trigger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.jdbc.framework.JdbcPersistenceManager;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.exception.DbException;
import cn.gaily.pub.util.CommonUtils;
import cn.gaily.simplejdbc.SimpleDSMgr;
import cn.gaily.simplejdbc.SimpleJdbc;
import cn.gaily.simplejdbc.SimpleSession;

/**
 * <p>Title: TaskExecutor</P>
 * <p>Description: 任务执行器</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-11-4
 */
public class TaskExecutor {

	public SimpleDSMgr remote = null;
	public SimpleDSMgr local = null;
	
	Connection conn = null;
	
	public void execute(){
		execute(remote, local);
	}
	
//	ISynMaintain service = NCLocator.getInstance().lookup(ISynMaintain.class);
	
	/**
	 * <p>方法名称：execute</p>
	 * <p>方法描述：任务执行方法入口</p>
	 * @param srcMgr
	 * @param destMgr
	 * @author xiaoh
	 * @since  2014-11-5
	 * <p> history 2014-11-5 xiaoh  创建   <p>
	 */
	public boolean execute(SimpleDSMgr srcMgr, SimpleDSMgr destMgr){
		List<String> srcTableNames = getAllTables(srcMgr);
		List<String> destTableNames = getAllTables(destMgr);
		
		AbstractETLTask task = DefaultETLTask.getInstance();
		Map<String,String> value1 = null;
		Map<String,String> value2 = null;
		int recordCount = 0;
		for(String tableName: srcTableNames){
			String time1 = CommonUtils.getCurrentDate(CommonUtils.DATE_FORMATER_YYYY_MM_DD_TIME);
			recordCount = SimpleSession.getRecordCount(srcMgr, "XFL_"+tableName);
			if(recordCount>0){
				value1 = task.execute(srcMgr, destMgr, tableName);
				doLog(srcMgr,destMgr, tableName, value1, time1);
			}
			
			String time2 = CommonUtils.getCurrentDate(CommonUtils.DATE_FORMATER_YYYY_MM_DD_TIME);
			recordCount = SimpleSession.getRecordCount(destMgr, "XFL_"+tableName);
			if(recordCount>0){
				value2 = task.execute(destMgr, srcMgr, tableName);
				doLog(destMgr, srcMgr, tableName, value2, time2);
			}
			destTableNames.remove(tableName);
		}
		
		if(destTableNames.size()>0){
			for(String tableName:destTableNames){
				String time1 = CommonUtils.getCurrentDate(CommonUtils.DATE_FORMATER_YYYY_MM_DD_TIME);
				recordCount = SimpleSession.getRecordCount(destMgr, "XFL_"+tableName);
				if(recordCount>0){
					value1 = task.execute(destMgr, srcMgr, tableName);
					doLog(srcMgr,destMgr, tableName, value1, time1);
				}
			}
		}
		return true;
	}

	
	/**
	 * <p>方法名称：executeSyn</p>
	 * <p>方法描述：历史数据同步</p>
	 * @param srcMgr
	 * @param destMgr
	 * @return
	 * @author xiaoh
	 * @since  2014-11-23
	 * <p> history 2014-11-23 xiaoh  创建   <p>
	 */
	public boolean executeSyn(SimpleDSMgr srcMgr, SimpleDSMgr destMgr){
		List<String> srcTableNames = getAllTables(srcMgr);
		List<String> destTableNames = getAllTables(destMgr);
		
		ETLSynTask task = new ETLSynTask();
		Map<String,String> value1 = null;
		Map<String,String> value2 = null;
		for(String tableName: srcTableNames){
			String time1 = CommonUtils.getCurrentDate(CommonUtils.DATE_FORMATER_YYYY_MM_DD_TIME);
			value1 = task.doExecute(destMgr, srcMgr, tableName);
			doLog(destMgr,srcMgr, tableName, value1, time1, true);
			String time2 = CommonUtils.getCurrentDate(CommonUtils.DATE_FORMATER_YYYY_MM_DD_TIME);
			value2 = task.doExecute(srcMgr, destMgr, tableName);
			doLog(srcMgr,destMgr, tableName, value2, time2, true);
			destTableNames.remove(tableName);
		}
		
		if(destTableNames.size()>0){
			for(String tableName:destTableNames){
				String time1 = CommonUtils.getCurrentDate(CommonUtils.DATE_FORMATER_YYYY_MM_DD_TIME);
				value1 = task.doExecute(destMgr, srcMgr, tableName);
				doLog(srcMgr,destMgr, tableName, value1, time1, true);
			}
		}
		return true;
	}
	
	
	private void doLog(SimpleDSMgr srcMgr, SimpleDSMgr destMgr, String tableName, Map<String, String> value1,
			String startTtime) {
		doLog(srcMgr, destMgr, tableName, value1, startTtime, false);
	}
	
	private void doLog(SimpleDSMgr srcMgr, SimpleDSMgr destMgr, String tableName, Map<String, String> value1,
								String startTtime, boolean isSyn) {
		
		int add = 0;
		int update = 0;
		int delete = 0;
		
		String endtime = CommonUtils.getCurrentDate(CommonUtils.DATE_FORMATER_YYYY_MM_DD_TIME); 
		
		if(value1!=null){
			add = Integer.valueOf(value1.get("add"));
			update = Integer.valueOf(value1.get("update"));
			delete = Integer.valueOf(value1.get("delete"));
		}else{
			add = 0;
		}
		
		ETLLogVO vo = null;
//		if(isSyn){
//			vo = new ETLLogVO();
//			vo.setSrcdbname(srcMgr.getDbName()).setSrcip(srcMgr.getIp()).setSrcport(srcMgr.getPort()).setSrcusername(srcMgr.getUserName());
//			vo.setDestdbname(destMgr.getDbName()).setDestip(destMgr.getIp()).setDestport(destMgr.getPort()).setDestusername(destMgr.getUserName());
//			vo.setTABLENAME(tableName);
//			vo.setType("数据同步");
//			vo.setResult(add+"");
//			vo.setStarttime(startTtime);
//			vo.setEndtime(endtime);
//			log(srcMgr, vo);
//		}
//		if(add!=0){
//			vo = new ETLLogVO();
//			vo.setSrcdbname(srcMgr.getDbName()).setSrcip(srcMgr.getIp()).setSrcport(srcMgr.getPort()).setSrcusername(srcMgr.getUserName());
//			vo.setDestdbname(destMgr.getDbName()).setDestip(destMgr.getIp()).setDestport(destMgr.getPort()).setDestusername(destMgr.getUserName());
//			vo.setTABLENAME(tableName);
//			vo.setType("新增");
//			vo.setResult(add+"");
//			vo.setStarttime(startTtime);
//			vo.setEndtime(endtime);
//			log(srcMgr, vo);
//		}
//		if(update!=0){
//			vo = new ETLLogVO();
//			vo.setSrcdbname(srcMgr.getDbName()).setSrcip(srcMgr.getIp()).setSrcport(srcMgr.getPort()).setSrcusername(srcMgr.getUserName());
//			vo.setDestdbname(destMgr.getDbName()).setDestip(destMgr.getIp()).setDestport(destMgr.getPort()).setDestusername(destMgr.getUserName());
//			vo.setTABLENAME(tableName);
//			vo.setType("更新");
//			vo.setStarttime(startTtime);
//			vo.setEndtime(endtime);
//			vo.setResult(update+"");
//			log(srcMgr, vo);
//		}
//		if(delete!=0){
//			vo = new ETLLogVO();
//			vo.setSrcdbname(srcMgr.getDbName()).setSrcip(srcMgr.getIp()).setSrcport(srcMgr.getPort()).setSrcusername(srcMgr.getUserName());
//			vo.setDestdbname(destMgr.getDbName()).setDestip(destMgr.getIp()).setDestport(destMgr.getPort()).setDestusername(destMgr.getUserName());
//			vo.setTABLENAME(tableName);
//			vo.setType("删除");
//			vo.setResult(delete+"");
//			vo.setStarttime(startTtime);
//			vo.setEndtime(endtime);
//			log(srcMgr, vo);
//		}

	}


	
	
	/**
	 * <p>方法名称：getAllTables</p>
	 * <p>方法描述：获取触发器管理的所有状态为启用的表名称</p>
	 * @param mgr
	 * @return
	 * @author xiaoh
	 * @since  2014-11-4
	 * <p> history 2014-11-4 xiaoh  创建   <p>
	 */
	private List<String> getAllTables(SimpleDSMgr mgr){
		List<String> tableNames = new ArrayList<String>();
		
		String sql ="SELECT TABLENAME FROM XFL_TABSTATUS WHERE STATUS='1'";
		Connection conn = mgr.getConnection();
		Statement st = null;
		ResultSet rs = null;
		String value = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while(rs.next()){
				value = rs.getString(1);
				tableNames.add(value);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("查询触发器表名出错！"+e);
		} finally{
			SimpleJdbc.release(null, st, rs);
			mgr.release(conn);
		}
		
		return tableNames;
	}
	
	
	
	private void log(SimpleDSMgr mgr, ETLLogVO vo) {
		StringBuilder sql = new StringBuilder("INSERT INTO ETL_SYNLOG(");
		StringBuilder value = new StringBuilder(" VALUES('");
		if(CommonUtils.isNotEmpty(vo.getSRCDBNAME())){
			sql.append("SRCDBNAME").append(",");
			value.append(vo.getSRCDBNAME()).append("','");
		}
		if(CommonUtils.isNotEmpty(vo.getSRCIP())){
			sql.append("SRCIP").append(",");
			value.append(vo.getSRCIP()).append("','");
		}
		if(CommonUtils.isNotEmpty(vo.getSRCPORT())){
			sql.append("SRCPORT").append(",");
			value.append(vo.getSRCPORT()).append("','");
		}
		if(CommonUtils.isNotEmpty(vo.getSRCUSERNAME())){
			sql.append("SRCUSERNAME").append(",");
			value.append(vo.getSRCUSERNAME()).append("','");
		}
		if(CommonUtils.isNotEmpty(vo.getDESTDBNAME())){
			sql.append("DESTDBNAME").append(",");
			value.append(vo.getDESTDBNAME()).append("','");
		}
		if(CommonUtils.isNotEmpty(vo.getDESTIP())){
			sql.append("DESTIP").append(",");
			value.append(vo.getDESTIP()).append("','");
		}
		if(CommonUtils.isNotEmpty(vo.getDESTPORT())){
			sql.append("DESTPORT").append(",");
			value.append(vo.getDESTPORT()).append("','");
		}
		if(CommonUtils.isNotEmpty(vo.getDESTUSERNAME())){
			sql.append("DESTUSERNAME").append(",");
			value.append(vo.getDESTUSERNAME()).append("','");
		}
		
		if(CommonUtils.isNotEmpty(vo.getTABLENAME())){
			sql.append("TABLENAME").append(",");
			value.append(vo.getTABLENAME()).append("','");
		}
		
		if(CommonUtils.isNotEmpty(vo.getTYPE())){
			sql.append("TYPE").append(",");
			value.append(vo.getTYPE()).append("','");
		}
		
		if(CommonUtils.isNotEmpty(vo.getSTARTTIME())){
			sql.append("STARTTIME").append(",");
			value.append(vo.getSTARTTIME()).append("','");
		}
		
		if(CommonUtils.isNotEmpty(vo.getENDTIME())){
			sql.append("ENDTIME").append(",");
			value.append(vo.getENDTIME()).append("','");
		}
		
		if(CommonUtils.isNotEmpty(vo.getRESULT())){
			sql.append("RESULT").append(",");
			value.append(vo.getRESULT()).append("','");
		}
		sql.deleteCharAt(sql.length()-1);
		value.deleteCharAt(value.length()-1);
		value.deleteCharAt(value.length()-1);
		sql.append(") ").append(value).append(")");
		
		Statement st = null;
		JdbcPersistenceManager m = null; 
		try {
			if(conn==null){
				m  =(JdbcPersistenceManager) PersistenceManager.getInstance();
				conn = m.getConnection();
			}
			conn.setAutoCommit(false);
			st = conn.createStatement();
			st.executeUpdate(sql.toString());
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (DbException e) {
			e.printStackTrace();
		} finally{
			SimpleJdbc.release(conn, st, null);
		}
	}
	
	
	
	
	
	public void setRemote(SimpleDSMgr remote) {
		this.remote = remote;
	}
	public  SimpleDSMgr getRemote() {
		return remote;
	}
	public SimpleDSMgr getLocal() {
		return local;
	}
	public void setLocal(SimpleDSMgr local) {
		this.local = local;
	}
	
}
