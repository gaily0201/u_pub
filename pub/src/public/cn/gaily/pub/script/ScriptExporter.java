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
 * <p>Description: �ű���ȡ����</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-11-27
 */
public class ScriptExporter {

	private static String encoding  = "UTF-8";
	
	private String mgrTable = "XFL_TABSTATUS";
	
	
	/**
	 * <p>�������ƣ�export</p>
	 * <p>�������������ݴ������������ʱ������ ����sql�����ű�</p>
	 * @return
	 * @author xiaoh
	 * @since  2014-11-27
	 * <p> history 2014-11-27 xiaoh  ����   <p>
	 */
	public void export(){
		/**
		 * 1. ��ȡ�������õ�ͬ����;
		 * 2. �ֱ��ȡÿ��ͬ�����Ӧ��ʱ���е�����;
		 * 3. �Ի�ȡ�������������й���sql;
		 * 4. ��sqlд���ļ�
		 * 5. ɾ����ʱ������
		 */
		SimpleDSMgr mgr = getDataSource();
		
		List<String> tables = getAllTables(mgr);
		
		
		
	}
	
	
	
	/**
	 * <p>�������ƣ�getAllTables</p>
	 * <p>������������ȡ�����赽���ı�</p>
	 * @param mgr
	 * @return
	 * @author xiaoh
	 * @since  2014-11-27
	 * <p> history 2014-11-27 xiaoh  ����   <p>
	 */
	private List<String> getAllTables(SimpleDSMgr mgr) {
		if(mgr==null){
			return null;
		}
		String sql = "SELECT TABLENAME FROM "+mgrTable; //TODO �������ñ��е� ����
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
	 * <p>�������ƣ�getDataSource</p>
	 * <p>������������ʼ������Դ��Ϣ</p>
	 * @return
	 * @author xiaoh
	 * @since  2014-11-27
	 * <p> history 2014-11-27 xiaoh  ����   <p>
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
