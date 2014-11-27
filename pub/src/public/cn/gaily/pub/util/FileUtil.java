package cn.gaily.pub.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import nc.bs.logging.Logger;
import org.apache.commons.io.FileUtils;

import cn.gaily.simplejdbc.SimpleJdbc;

public class FileUtil {

	/**
	 * <p>方法名称：writeFile</p>
	 * <p>方法描述：写文件</p>
	 * @param fileName			文件名
	 * @param data				数据
	 * @param append			是否追加
	 * @throws IOException
	 * @author xiaoh
	 * @since  2014-11-27
	 * <p> history 2014-11-27 xiaoh  创建   <p>
	 */
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	static Connection conn = null;
	static PreparedStatement pst = null;
	static ResultSet rs = null;
	static InputStream in = null;
	static FileInputStream fis = null;
	static FileOutputStream fos = null;
	static String fileSuffix = ".jpg";
//	static String fileSuffix = ".doc";
	
	
	public static void saveBlobFile(File path,String tableName, int pageSize, int round) throws IOException{
//		String sql = "SELECT result.FILES,result.FILENAME,result.SCANAJBH,result.AJMC FROM (SELECT T.*, ROWNUM RN FROM (SELECT * FROM "+tableName+" A LEFT JOIN AJ_JBXX B ON A.SCANAJBH=B.AJBH) T WHERE ROWNUM <= "+(pageSize*(round+1))+") RESULT WHERE RN > "+pageSize*round;
		String sql = "SELECT A.FILES,A.FILENAME,A.SCANAJBH,B.AJMC FROM "+tableName+" A LEFT JOIN AJ_JBXX B ON A.SCANAJBH=B.AJBH"; //扫描件
//		String sql = "SELECT WSGS,WSBT FROM WSMANAGE"; //文书模板
//		String sql = "SELECT a.wsnr,a.wsbtmc,a.ajbh,b.ajmc from "+tableName+" a, aj_jbxx b WHERE a.ajbh=b.ajbh"; //公安文书
		/**
		 *  SELECT a.wsnr,a.wsbtmc,a.ajbh,b.ajmc from aj_flws_ga a, aj_jbxx b WHERE a.ajbh=b.ajbh;
			SELECT a.wsnr,a.wsbtmc,a.ajbh,b.ajmc from aj_flws_jcy a, aj_jbxx b WHERE a.ajbh=b.ajbh;
			SELECT a.wsnr,a.wsbtmc,a.ajbh,b.ajmc from aj_flws_sfj a, aj_jbxx b WHERE a.ajbh=b.ajbh;
			SELECT a.wsnr,a.wsbtmc,a.ajbh,b.ajmc from aj_flws_fy a, aj_jbxx b WHERE a.ajbh=b.ajbh;
			SELECT a.wsnr,a.wsbtmc,a.ajbh,b.ajmc from aj_flws_bak a, aj_jbxx b WHERE a.ajbh=b.ajbh;
		 */
		if(!path.exists()){
			path.mkdirs();
		}
		if(conn==null){
			conn = SimpleJdbc.getConnection("szgjf", "1", "192.168.1.100", "orcl");
		}
		try {
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			Blob blob = null;
			String fileName = null;
			String ajbh = null;
			String ajmc = null;
			File filePath = null;
			File savePath = null;
			int len = 0;
			byte[] buf = new byte[1024]; 
			String strPath = null;
			int i=0;
			while(rs.next()){
				blob = rs.getBlob(1);
				fileName = rs.getString(2);
				ajbh = rs.getString(3);
				ajmc = rs.getString(4);
				if(CommonUtils.isNotEmpty(ajmc)&&ajmc.contains("\"")){
					ajmc = ajmc.replace('\"', '_');
				}
				if(CommonUtils.isNotEmpty(ajmc)&&ajmc.contains("\\")){
					ajmc = ajmc.replace('\\', '_');
				}
				filePath = new File(path+File.separator+ajbh+"_"+ajmc);
//				filePath = path;
				if(!filePath.exists()){
					filePath.mkdirs();
				}
				strPath = filePath+File.separator+fileName+fileSuffix;
				savePath = new File(strPath);
				if(savePath.exists()){
					continue;
				}
				fos = new FileOutputStream(savePath);
				if(blob!=null){
					in = blob.getBinaryStream();
					while((len=in.read(buf))!=-1){
						fos.write(buf, 0, len);
					}
					System.out.println(tableName+": "+ i++);
					fos.flush();
					fos.close();
					in.close();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
//			JdbcUtils.release(conn, pst, rs	);
		}
	}
	
	
	/**
	 * <p>方法名称：changeCharcter</p>
	 * <p>方法描述：改变目录下指定类型的文件类型</p>
	 * @param filePath			file path
	 * @param fileTypes			filter file types,like java,jpg etc..
	 * @param sourceCharcter	source character, file character
	 * @param targetCharacter	target character
	 * @throws IOException
	 * @author xiaoh
	 * @since  2014-10-14
	 * <p> history 2014-10-14 xiaoh  创建   <p>
	 */
	public static void changeCharcter(String filePath,String[] fileTypes, String sourceCharcter,String targetCharacter) throws IOException{
		Collection<File> javaGbkFileCol =  FileUtils.listFiles(new File(filePath), fileTypes, true); 
		for (File javaGbkFile : javaGbkFileCol) { 
		      String newFilePath = filePath+javaGbkFile.getAbsolutePath().substring(filePath.length()); 
		      FileUtils.writeLines(new File(newFilePath), targetCharacter, FileUtils.readLines(javaGbkFile, sourceCharcter));        
		}
	}
	
	
	public static void deleteFile(File file){
		if(file.exists()){
			try {
				FileUtils.forceDelete(file);
			} catch (IOException e) {
				Logger.error(e);
			}
		}
	}
	
	/**
	 * <p>方法名称：executeCMD</p>
	 * <p>方法描述：	执行cmd命令</p>
	 * @param cmd	cmd命令
	 * @author xiaoh
	 * @since  2014-10-14
	 * <p> history 2014-10-14 xiaoh  创建   <p>
	 */
	public static void executeCMD(String cmd){
//		cmd.exe /c ipconfig /all
		BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            br = new BufferedReader(new InputStreamReader(p.getInputStream(),"GBK"));
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
	}
}
