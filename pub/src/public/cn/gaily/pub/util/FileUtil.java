package cn.gaily.pub.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

public class FileUtil {

	static Connection conn = null;
	static PreparedStatement pst = null;
	static ResultSet rs = null;
	static InputStream in = null;
	static FileInputStream fis = null;
	static FileOutputStream fos = null;
	static String fileSuffix = ".jpg";
	
	
	public static void saveBlobFile(File path, int pageSize, int round) throws IOException{
		String sql = "SELECT result.FILES,result.FILENAME,result.SCANAJBH FROM (SELECT T.*, ROWNUM RN FROM (SELECT * FROM SCANTABLE) T WHERE ROWNUM <= "+(pageSize*(round+1))+") RESULT WHERE RN > "+pageSize*round;
//		String sql = "SELECT FILES,FILENAME,SCANAJBH FROM SCANTABLE";
		if(!path.exists()){
			path.mkdirs();
		}
		if(conn==null){
//			conn = JdbcUtils.getConnection("szgjf", "1", "192.168.1.100", "orcl");
		}
		try {
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			Blob blob = null;
			String fileName = null;
			String ajbh = null;
			File filePath = null;
			File savePath = null;
			int len = 0;
			byte[] buf = new byte[1024]; 
			while(rs.next()){
				blob = rs.getBlob(1);
				fileName = rs.getString(2);
				ajbh = rs.getString(3);
				filePath = new File(path+File.separator+ajbh);
				if(!filePath.exists()){
					filePath.mkdir();
				}
				savePath = new File(filePath+File.separator+fileName+fileSuffix);
				if(savePath.exists()){
					continue;
				}
				fos = new FileOutputStream(savePath);
				if(blob!=null){
					in = blob.getBinaryStream();
					while((len=in.read(buf))!=-1){
						fos.write(buf, 0, len);
					}
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
