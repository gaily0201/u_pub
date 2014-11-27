package cn.gaily.pub.script;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.gaily.pub.util.CommonUtils;

/**
 * <p>Title: ScriptExporter</P>
 * <p>Description: 脚本抽取工具</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-11-27
 */
public class ScriptExporter {

	private static String encoding  = "UTF-8";
	
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
		 */
		
		
		
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
