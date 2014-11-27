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
 * <p>Description: �ű���ȡ����</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-11-27
 */
public class ScriptExporter {

	private static String encoding  = "UTF-8";
	
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
