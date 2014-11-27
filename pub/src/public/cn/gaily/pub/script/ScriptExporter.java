package cn.gaily.pub.script;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cn.gaily.pub.util.CommonUtils;
import cn.gaily.simplejdbc.SimpleDSMgr;
import cn.gaily.simplejdbc.SimpleJdbc;

/**
 * <p>Title: ScriptExporter</P>
 * <p>Description: 脚本抽取工具</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-11-27
 */
public class ScriptExporter {

	private static String encoding  = "UTF-8";
	
	private String mgrTable = "XFL_TABSTATUS";
	
	
	/**
	 * <p>方法名称：export</p>
	 * <p>方法描述：根据触发器保存的临时表数据 导出sql补丁脚本</p>
	 * @return
	 * @author xiaoh
	 * @since  2014-11-27
	 * <p> history 2014-11-27 xiaoh  创建   <p>
	 */
	public void export(){
		/**
		 * 1. 获取所有配置的同步表;
		 * 2. 分别获取每个同步表对应临时表中的数据;
		 * 3. 对获取的数据逐条进行构建sql;
		 * 4. 将sql写入文件
		 * 5. 删除临时表数据
		 */
		SimpleDSMgr mgr = getDataSource();
		
		List<String> tables = getAllTables(mgr);
		
		
		
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
		String sql = "SELECT TABLENAME FROM "+mgrTable; //TODO 启用配置表中的 类型
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
		
		return null;
	}




	public static void writeFile(String fileName, String data, boolean append) throws IOException{
		if(CommonUtils.isEmpty(fileName)||CommonUtils.isEmpty(data)){
			return;
		}
		data = data+"\r\n";
		File file = new File(fileName);
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		if(!file.exists()){
			file.createNewFile();
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, append));
		writer.write(data);
		writer.flush();
		writer.close();
	}
}
