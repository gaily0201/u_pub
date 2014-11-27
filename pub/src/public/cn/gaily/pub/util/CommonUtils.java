package cn.gaily.pub.util;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import nc.bs.logging.Logger;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * <p>Title: CommonUtil</P>
 * <p>Description: �����Ĺ�����</p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-9-14
 */
public class CommonUtils {
	
	/**
	 * UAP_����ת�������ļ�����λ��
	 */
	public static final String  UAP_ETL_DB_CONFIG_PATH = System.getProperty("user.home")+File.separator+"uap_ETL"+File.separator;
	/**
	 * WEB_����ת�������ļ�����λ��
	 */
	public static final String  WEB_ETL_DB_CONFIG_PATH = System.getProperty("user.home")+File.separator;
	
	public String ETL_DB_CONFIG_NAME = "etl_config.properties";
	
	public static final String 	DATE_FORMATER_YYYY      = "yyyy";
	public static final String 	DATE_FORMATER_YYYYMM    = "yyyyMM";
	public static final String 	DATE_FORMATER_YYYYMMDD  = "yyyyMMdd" ; 
	public static final String 	DATE_FORMATER_YYYY_MM_DD  = "yyyy-MM-dd" ; 
	public static final String 	DATE_FORMATER_YYYY_MM_DD_1  = "yyyy/MM/dd" ; 
	public static final String 	DATE_FORMATER_YYYY_MM_DD_TIME  = "yyyy-MM-dd HH:mm:ss" ; 
	public static final String 	DATE_FORMATER_YYYY_MM_DD_TIME_1  = "yyyy/MM/dd HH:mm:ss" ; 

	/**
   	* ���У��??19XX/20XX
   	*/
	public static final String DATE_PATTERN_YYYY       = "^(19|20)(\\d{2})$";
	/**
	 * ���ڸ�ʽ??yyyyMM, 100����??
	 */
	public static final String DATE_PATTERN_YYYYMM     = "^(19|20)\\d{2}(0[1-9]|1[012])$";
	/**
	 * ���ڸ�ʽ��yyyyMMdd, 100����??
	 */
	public static final String DATE_PATTERN_YYYYMMDD   = "^(19|20)\\d{2}((0[1-9])|1[012])((0[1-9])|(1[0-9])|(2[0-9])|30|31)$";
	
	/**
	 * ���ڸ�ʽ��yyyy-MM-dd, 100����??
	 */
	public static final String DATE_PATTERN_YYYY_MM_DD   = "^(19|20)\\d{2}-((0[1-9])|1[012])-((0[1-9])|(1[0-9])|(2[0-9])|30|31)$";
	/**
	 * ���ڸ�ʽ��yyyy/MM/dd, 100����??
	 */
	public static final String DATE_PATTERN_YYYY_MM_DD_1   = "^(19|20)\\d{2}/((0[1-9])|1[012])/((0[1-9])|(1[0-9])|(2[0-9])|30|31)$";
	
	/**
	 * ���ڸ�ʽ��yyyy-MM-dd HH:mm:ss, 100����??
	 */
	public static final String DATE_PATTERN_YYYY_MM_DD_TIME   = "^(19|20)\\d{2}-((0[1-9])|1[012])-((0[1-9])|(1[0-9])|(2[0-9])|30|31) (0[1-9]|1[0-9]|2[0123]):([0-5][1-9]|60):([0-5][0-9]|60)$";
	
	/**
	 * ���ڸ�ʽ��yyyy/MM/dd HH:mm:ss, 100����??
	 */
	public static final String DATE_PATTERN_YYYY_MM_DD_TIME_1   = "^(19|20)\\d{2}/((0[1-9])|1[012])/((0[1-9])|(1[0-9])|(2[0-9])|30|31) (0[1-9]|1[0-9]|2[0123]):([0-5][1-9]|60):([0-5][0-9]|60)$";
	
	/**
	 * ����
	 */
	public static final String  NUMBER_INT_PATTERN= "^-?\\d+$";
	/**
	 * ��������
	 */
	public static final String NUMBER_NORMAL_FLOAT_PATTERN = "^(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*))$";
	
	/**
	 * ��������??
	 */
	public static final String NUMBER_NEGATIVE_FLOAT_PATTERN= "^((-\\d+(\\.\\d+)?)|(0+(\\.0+)?))$";
	
	/**
	 * �����ļ���
	 */
	public static InputStream in = null;
	
	/**
	 * �����ļ���ϸ·��
	 */
	public String propFilepath = UAP_ETL_DB_CONFIG_PATH + ETL_DB_CONFIG_NAME;
	
	/**
	 * <p>�������ƣ�isValidFloat</p>
	 * <p>����������У���Ƿ�Ϊ�Ϸ�����??/p>
	 * @param number ����
	 * @return
	 * @author xiaoh
	 * @since  2014-9-28
	 * <p> history 2014-9-28 xiaoh  ����   <p>
	 */
	public static boolean isValidFloat(String number){
		if(isEmpty(number)){
			return false;
		}
		if(number.matches(NUMBER_NORMAL_FLOAT_PATTERN)){
			return true;
		}else if(number.matches(NUMBER_NEGATIVE_FLOAT_PATTERN)){
			return true;
		}
		return false;
	}
	
	/**
	 * <p>�������ƣ�isValidInt</p>
	 * <p>�����������Ƿ�Ϊ�Ϸ���INT����</p>
	 * @param number ����
	 * @return
	 * @author xiaoh
	 * @since  2014-9-28
	 * <p> history 2014-9-28 xiaoh  ����   <p>
	 */
	public static boolean isValidInt(String number){
		if(isEmpty(number)){
			return true;
		}
		if(number.matches(NUMBER_INT_PATTERN)){
			return true;
		}
		return false;
	}
	
	/**
	 * <p>�������ƣ�isValidDate</p>
	 * <p>�����������жϸ������ַ�����ʽ�������Ƿ���ϸ��������ڸ�ʽ�ַ���</p>
	 * @param date �ַ������͵����ڣ���20120212
	 * @param formater ���ڸ�ʽ�ַ����� PubUtil.DATE_FORMATER_YYYYMM/PubUtil.DATE_FORMATER_YYYYMMDD
	 * @return �������ͣ��ҷ���ָ���ĸ�ʽ�򷵻�true,���򷵻�false
	 * @author xiaoh
	 * @since 2014-9-14
	 */
	public static boolean isValidDate(String date,String formater) {
	    if(date != null && formater != null){
	    	if(DATE_FORMATER_YYYY_MM_DD.equals(formater)){
	    		return date.matches(DATE_PATTERN_YYYY_MM_DD);
	    	}else if(DATE_FORMATER_YYYY_MM_DD_TIME.equals(formater)){
	    		return date.matches(DATE_PATTERN_YYYY_MM_DD_TIME);
	    	}else if(DATE_FORMATER_YYYYMM.equals(formater)){
	    		return date.matches(DATE_PATTERN_YYYYMM);
	    	}else if(DATE_FORMATER_YYYYMMDD.equals(formater)){
	    		return date.matches(DATE_PATTERN_YYYYMMDD);
	    	}else if(DATE_FORMATER_YYYY.equals(formater)){
	    		return date.matches(DATE_PATTERN_YYYY);
	    	}
	    }else if(date!=null && formater==null){
	    	if(date.matches(DATE_PATTERN_YYYY_MM_DD)){
	    		return true;
	    	}else if(date.matches(DATE_PATTERN_YYYY_MM_DD_1)){
	    		return true;
	    	}else if(date.matches(DATE_PATTERN_YYYY_MM_DD_TIME)){
	    		return true;
	    	}else if(date.matches(DATE_PATTERN_YYYY_MM_DD_TIME_1)){
	    		return true;
	    	}else if(date.matches(DATE_PATTERN_YYYYMM)){
	    		return true;
	    	}else if(date.matches(DATE_PATTERN_YYYYMMDD)){
	    		return true;
	    	}else if(date.matches(DATE_PATTERN_YYYY)){
	    		return true;
	    	}
	    }
	    return false ;
	}
	
	/**
	 * <p>�������ƣ�formatDate</p>
	 * <p>����������������ת��Ϊuap��׼�ĸ�ʽYYYY-mm-dd HH:mm:ss</p>
	 * @param date
	 * @return
	 * @author xiaoh
	 * @throws ParseException 
	 * @since  2014-9-14
	 * <p> history 2014-9-14 xiaoh  ����   <p>
	 */
	public static String formatDate(String date){
		if(isNotEmpty(date)&&isNotEmpty(getDateFormatStr(date))){
	         SimpleDateFormat psdf = new SimpleDateFormat(getDateFormatStr(date));
	         Date realDate = null;
			try {
				realDate = psdf.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
	         SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMATER_YYYY_MM_DD_TIME);
	         return sdf.format(realDate);
		}
	     return null; 
	}
	
	/**
	 * <p>�������ƣ�getDateFormatStr</p>
	 * <p>������������ȡ�����ַ����ĸ�??/p>
	 * @param date
	 * @author xiaoh
	 * @since  2014-9-14
	 * <p> history 2014-9-14 xiaoh  ����   <p>
	 */
	public static String getDateFormatStr(String date){
		if(isNotEmpty(date)){
			if(date.matches(DATE_PATTERN_YYYY)){
				return DATE_FORMATER_YYYY;
			}else if(date.matches(DATE_PATTERN_YYYY_MM_DD)){
				return DATE_FORMATER_YYYY_MM_DD;
			}else if(date.matches(DATE_PATTERN_YYYY_MM_DD_1)){
				return DATE_FORMATER_YYYY_MM_DD_1;
			}else if(date.matches(DATE_PATTERN_YYYY_MM_DD_TIME)){
				return DATE_FORMATER_YYYY_MM_DD_TIME;
			}else if(date.matches(DATE_PATTERN_YYYY_MM_DD_TIME_1)){
				return DATE_FORMATER_YYYY_MM_DD_TIME_1;
			}else if(date.matches(DATE_PATTERN_YYYYMM)){
				return DATE_FORMATER_YYYYMM;
			}else if(date.matches(DATE_PATTERN_YYYYMMDD)){
				return DATE_FORMATER_YYYYMMDD;
			}
		}
		return null;
	}
	
	
	/**
	 * <p>�������ƣ�getCurrentDate</p>
	 * <p>������������ȡ��ǰ��??/p>
	 * @param formater PubUitl.DATE_FORMATER_YYYY/PubUtil.DATE_FORMATER_YYYYMM
	 * @author xiaoh
	 * @since  2014-9-14
	 * <p> history 2014-9-14 xiaoh  ����   <p>
	 */
	public static String getCurrentDate(String formater){
		SimpleDateFormat sdf = new SimpleDateFormat(formater);
	    Calendar cal = Calendar.getInstance();
	    String strDate = sdf.format(cal.getTime());
	    return strDate;
	}
	
	/**
	 * <p>�������ƣ�isEmpty</p>
	 * <p>�����������ַ����Ƿ�Ϊ��</p>
	 * @author xiaoh
	 * @since  2014-9-14
	 * <p> history 2014-9-14 xiaoh  ����   <p>
	*/
	public static final boolean isEmpty(String str) {
	 return str == null || str.trim().length() == 0;
	}
	/**
	 * <p>�������ƣ�isNotEmpty</p>
	 * <p>�����������ַ����Ƿ�Ϊ??/p>
	 * @author xiaoh
	 * @since  2014-9-14
	 * <p> history 2014-9-14 xiaoh  ����   <p>
	 */
	public static final boolean isNotEmpty(String str) {
	 return !isEmpty(str);
	}
	
	
	/**
	 * <p>�������ƣ�arrayToList</p>
	 * <p>����������������ת��ΪList</p>
	 * @param array
	 * @author xiaoh
	 * @since  2014-9-14
	 * <p> history 2014-9-14 xiaoh  ����   <p>
	 */
	public static <T> List<T> arrayToList(T[] array) {
		List<T> list = null;
		if(array != null){
			list = new ArrayList<T>();
			for(int i=0; i<array.length; i++){
				list.add(array[i]);
			}
		}
		return list;
	}
	
	/**
	 * <p>�������ƣ�isValidScene</p>
	 * <p>�����������Ƿ�Ϊ�Ϸ�����</p>
	 * @param scene ����
	 * @return
	 * @author xiaoh
	 * @since  2014-9-30
	 * <p> history 2014-9-30 xiaoh  ����   <p>
	 */
	public static boolean isValidScene(Integer scene){
		if(scene==null){
			return false;
		}
		if(scene!=0&&scene!=1&&scene!=2&&scene!=3&&scene!=4){
			return false;
		}
		return true;
	}
	
	/**
	 * <p>�������ƣ�doCompressImage</p>
	 * <p>������������������ͼ</p>
	 * @param image   ͼƬ??
	 * @param width   ����
	 * @param height  �߶�
	 * @param out     ���??
	 * @return		    �ֽ�??
	 * @author xiaoh
	 * @since  2014-10-10
	 * <p> history 2014-10-10 xiaoh  ����   <p>
	 */
	public static ByteArrayInputStream doCompressImage(Blob blob, double width, double height, ByteArrayOutputStream out) {
		ByteArrayInputStream bin = null;
		try {
			InputStream in = blob.getBinaryStream();
			BufferedInputStream ins = new BufferedInputStream(in);
			BufferedImage image=ImageIO.read(ins); 
			
			if (image != null) {
				ImageIcon icon = new ImageIcon(image);
				double rateh = icon.getIconHeight() / height;
				double ratew = icon.getIconWidth() / width;
				double rate = (rateh > ratew) ? rateh : ratew;
				int new_w = (int) (icon.getIconWidth() / rate);
				int new_h = (int) (icon.getIconHeight() / rate);
				BufferedImage tag = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
				tag.getGraphics().drawImage(image, 0, 0, new_w, new_h, null);

				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
				JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
				/* ѹ������ */
				jep.setQuality(1f, true);
				encoder.encode(tag, jep);
				
				bin = new ByteArrayInputStream(out.toByteArray());
				{
					return bin;
				}

			}
		} catch (Exception e) {
			Logger.error(e);
		} finally {

		}
		return null;
	}
	
	public String getProperty(String fileName,String key){
		Properties prop = null;
		setPropFilepath(fileName, 1);
		if(in==null){
//			in = CommonUtils.class.getResourceAsStream(propFilepath);
			try {
				in = new FileInputStream(propFilepath);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				try {
					new File(propFilepath).createNewFile();
					in = new FileInputStream(propFilepath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			prop = new Properties();
			try {
				prop.load(in);
			} catch (IOException e) {
				Logger.error(e);
			}
		}
		String value = "";
        value = prop.getProperty(key);
        try {
			in.close();
			in = null;
		} catch (IOException e) {
			Logger.error(e);
		}
		return value;
	}
	
	public void setProperty(String fileName, String key,String value){
		Properties prop = null;
		FileOutputStream fos = null;
		setPropFilepath(fileName, 1);
		if(in==null){
			prop = new Properties();
			try {
				in = new FileInputStream(propFilepath);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			String path = propFilepath;
			try {
				fos = new FileOutputStream(path, true);
				prop.load(in);
				Enumeration e = prop.propertyNames();
				int i=0;
				while(e.hasMoreElements()){
					String vv = (String) e.nextElement();
					if(vv.equals(key)){
						prop.clear();
						prop.setProperty(vv, value);
						i = 1;
					}
				}
				prop.clear();
				if(i==0){
					prop.setProperty(key, value);
				}
				prop.store(fos, null);
			} catch (IOException e) {
				Logger.error(e);
			} finally{
				try {
					in.close();
					in = null;
					fos.close();
				} catch (IOException e) {
					Logger.error(e);
				}
			}
		}
		
	}
	
	/**
	 * <p>�������ƣ�setPropFilepath</p>
	 * <p>��������������ETL���������ļ�</p>
	 * @param type  1.uap���λ��  2.web���λ��
	 * @author xiaoh
	 * @since  2014-11-17
	 * <p> history 2014-11-17 xiaoh  ����   <p>
	 */
	public void setPropFilepath(String fileName, int type) {
		String  suffix = ".properties";
		if(isNotEmpty(fileName)&&type==1){
			this.propFilepath = UAP_ETL_DB_CONFIG_PATH+ fileName+ suffix;
		}else if(isNotEmpty(fileName)&&type==2){
			this.propFilepath = WEB_ETL_DB_CONFIG_PATH+ETL_DB_CONFIG_NAME;
		}else{
			this.propFilepath = UAP_ETL_DB_CONFIG_PATH+ETL_DB_CONFIG_NAME;
		}
		File f =new File(this.propFilepath);
		if(!f.getParentFile().exists()){
			f.getParentFile().mkdirs();
		}
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static List<String> generatePKs(int count){
		List<String> pks = new ArrayList<String>();
		String uuid = null;
		String prefix = "1001ZZ";
		for(int i=0;i<count;i++){
			uuid = prefix + UUID.randomUUID().toString().toUpperCase().replaceAll("-", "").substring(0, 14);
			if(!pks.contains(uuid)){
				pks.add(uuid);
			}
		}
		return pks;
	}
	
	public void setETL_DB_CONFIG_NAME(String eTL_DB_CONFIG_NAME) {
		ETL_DB_CONFIG_NAME = eTL_DB_CONFIG_NAME;
	}
	
}
