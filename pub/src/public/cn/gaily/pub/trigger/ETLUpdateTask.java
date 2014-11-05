package cn.gaily.pub.trigger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import cn.gaily.pub.util.CommonUtil;
import cn.gaily.simplejdbc.SimpleJdbc;
import cn.gaily.simplejdbc.SimpleDSMgr;

/**
 * <p>Title: ETLUpdateTask</P>
 * <p>Description: 数据交换更新任务</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-29
 */
public class ETLUpdateTask extends AbstractETLTask{

	
	public static ETLUpdateTask instance = null;
	
	public PreparedStatement ipst = null;
	
	private ETLUpdateTask(){}
	
	public static ETLUpdateTask getInstance(){
		if(instance ==null){
			instance = new ETLUpdateTask();
		}
		return instance;
	}
	
	@Override
	public void doexecute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName, String pkName, Map<String,Object> valueMap, Map<String,String> colNameTypeMap) {
		if(srcMgr==null||tarMgr==null||CommonUtil.isEmpty(tableName)){
			throw new RuntimeException("更新数据库操作参数出错");
		}

		//1、停用目标库触发器
//		enableTrigger(tarMgr, tableName, DISABLE);
		
		//2、执行目标库数据库更新
		doUpdate(tarMgr, tableName,pkName, valueMap, colNameTypeMap);
		
		//4、启用目标库触发器
//		enableTrigger(tarMgr, tableName, ENABLE);
	}

	/**
	 * <p>方法名称：doUpdate</p>
	 * <p>方法描述：</p>
	 * @param tarMgr
	 * @param tableName
	 * @param pkName
	 * @param valueMap
	 * @param colNameTypeMap
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  创建   <p>
	 */
	private void doUpdate(SimpleDSMgr tarMgr, String tableName, String pkName, Map<String, Object> valueMap, Map<String, String> colNameTypeMap) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ").append(tableName).append(" SET ");
		String colName = null;
		String colType = null;
		Entry<String,String> entry =  null;
		
		int n=0;
		for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
			entry = (Entry<String, String>) it.next();
			colName = entry.getKey();
			if(!"ETLSTATUS".equalsIgnoreCase(colName)&&!"ETLPKNAME".equalsIgnoreCase(colName)&&!"ETLTS".equalsIgnoreCase(colName)){
				sb.append(colName).append("=?,");
				colIndexMap.put(colName, ++n);
			}
		}
		sb.deleteCharAt(sb.length()-1);
		String pkValue = (String) valueMap.get(pkName);  //TODO pk可能为int
		if(CommonUtil.isEmpty(pkValue)){
			throw new RuntimeException("更新数据未获取到主键");
		}
		sb.append(" WHERE ").append(pkName).append("='").append(pkValue).append("'");
		
		Connection targetConn = tarMgr.getConnection();
		try {
			ipst = targetConn.prepareStatement(sb.toString());
			ipst.clearParameters();
			
			for(Iterator it=colNameTypeMap.entrySet().iterator();it.hasNext();){
				entry = (Entry<String, String>) it.next();
				colName = entry.getKey();
				colType = entry.getValue();
				Object value = valueMap.get(colName);
				List<String> ignoreCols = Arrays.asList(new String[]{"ETLSTATUS","ETLPKNAME","ETLTS"});
				ipst =setValues(ipst, colName, colType, value, ignoreCols);  //设置列值
			}
			ipst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("更新数据库异常");
		}finally{
			SimpleJdbc.release(null, ipst, null);
			tarMgr.release(targetConn);
		}
	}
	
	
}
