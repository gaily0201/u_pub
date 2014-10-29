package cn.gaily.pub.trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import cn.gaily.pub.util.CommonUtil;
import cn.gaily.pub.util.JdbcUtils;

/**
 * <p>Title: ETLInsertTask</P>
 * <p>Description: 数据交换插入任务</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-29
 */
public class ETLInsertTask extends AbstractETLTask{

	/**
	 * <p>方法名称：dealAdd</p>
	 * <p>方法描述：处理新增数据</p>
	 * @param tableName 表名
	 * @author xiaoh
	 * @since  2014-10-28
	 * <p> history 2014-10-28 xiaoh  创建   <p>
	 */
	public void dealAdd(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName){
		clear(); 	//清空数据
		
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
	 * <p>方法名称：doAddInsert</p>
	 * <p>方法描述：执行目标库数据库插入</p>
	 * @param tarMgr
	 * @param tableName
	 * @author xiaoh
	 * @since  2014-10-29
	 * <p> history 2014-10-29 xiaoh  创建   <p>
	 */
	private void doAddInsert(SimpleDSMgr tarMgr, String tableName) {
		if(tarMgr==null||CommonUtil.isEmpty(tableName)){
			return ;
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
					List<String> ignoreCols = Arrays.asList(new String[]{"ETLSTATUS","ETLPKNAME","ETLTS"});
					ipst =setValues(ipst, colName, colType, value, ignoreCols);  //设置列值
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

	
	@Override
	public void dealUpdate(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName) {
	}

	@Override
	public void dealDel(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName) {
	}
}
