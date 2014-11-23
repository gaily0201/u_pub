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
 * <p>Description: ����ִ����</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-11-4
 */
public class TaskExecutor {

	public SimpleDSMgr remote = null;
	public SimpleDSMgr local = null;
	
	
	public void execute(){
		execute(remote, local);
	}
	
	
	/**
	 * <p>�������ƣ�execute</p>
	 * <p>��������������ִ�з������</p>
	 * @param srcMgr
	 * @param destMgr
	 * @author xiaoh
	 * @since  2014-11-5
	 * <p> history 2014-11-5 xiaoh  ����   <p>
	 */
	public boolean execute(SimpleDSMgr srcMgr, SimpleDSMgr destMgr){
		List<String> srcTableNames = getAllTables(srcMgr);
		List<String> destTableNames = getAllTables(destMgr);
		
		AbstractETLTask task = DefaultETLTask.getInstance();
		for(String tableName: srcTableNames){
			task.execute(srcMgr, destMgr, tableName);
			task.execute(destMgr, srcMgr, tableName);
			destTableNames.remove(tableName);
		}
		
		if(destTableNames.size()>0){
			for(String tableName:destTableNames){
				task.execute(destMgr, srcMgr, tableName);
			}
		}
		return true;
	}

	/**
	 * <p>�������ƣ�executeSyn</p>
	 * <p>������������ʷ����ͬ��</p>
	 * @param srcMgr
	 * @param destMgr
	 * @return
	 * @author xiaoh
	 * @since  2014-11-23
	 * <p> history 2014-11-23 xiaoh  ����   <p>
	 */
	public boolean executeSyn(SimpleDSMgr srcMgr, SimpleDSMgr destMgr){
		List<String> srcTableNames = getAllTables(srcMgr);
		List<String> destTableNames = getAllTables(destMgr);
		
		ETLSynTask task = new ETLSynTask();
		for(String tableName: srcTableNames){
			task.doExecute(srcMgr, destMgr, tableName);
			task.doExecute(destMgr, srcMgr, tableName);
			destTableNames.remove(tableName);
		}
		
		if(destTableNames.size()>0){
			for(String tableName:destTableNames){
				task.doExecute(destMgr, srcMgr, tableName);
			}
		}
		return true;
	}
	
	/**
	 * <p>�������ƣ�getAllTables</p>
	 * <p>������������ȡ���������������״̬Ϊ���õı�����</p>
	 * @param mgr
	 * @return
	 * @author xiaoh
	 * @since  2014-11-4
	 * <p> history 2014-11-4 xiaoh  ����   <p>
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
			throw new RuntimeException("��ѯ��������������"+e);
		} finally{
			mgr.release(conn);
			SimpleJdbc.release(null, st, rs);
		}
		
		return tableNames;
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
