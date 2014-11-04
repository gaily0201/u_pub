package cn.gaily.pub.trigger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cn.gaily.simplejdbc.SimpleDSMgr;
import cn.gaily.simplejdbc.SimpleJdbc;

/**
 * <p>Title: TaskExecutor</P>
 * <p>Description: 任务执行器</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-11-4
 */
public class TaskExecutor {

	
	public void execute(SimpleDSMgr srcMgr, SimpleDSMgr destMgr){
		List<String> srcTableNames = getAllTables(srcMgr);
		List<String> destTableNames = getAllTables(destMgr);
		
		AbstractETLTask task = DefaultETLTask.getInstance();
		for(String tableName: srcTableNames){
			task.execute(srcMgr, destMgr, tableName);
			if(destTableNames.contains(tableName)){
				task.execute(destMgr, srcMgr, tableName);
				destTableNames.remove(tableName);
			}
		}
		
		if(!destTableNames.isEmpty()){
			for(String tableName: destTableNames){
				task.execute(destMgr, srcMgr, tableName);
			}
		}
		
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
	public List<String> getAllTables(SimpleDSMgr mgr){
		List<String> tableNames = new ArrayList<String>();
		
		String sql ="SELECT TABLENAME FROM XFL_TABSTATUS WHERE STATUS=1";
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
			mgr.release(conn);
			SimpleJdbc.release(null, st, rs);
		}
		
		return tableNames;
	}
}
