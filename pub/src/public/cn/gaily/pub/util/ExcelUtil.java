package cn.gaily.pub.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.trade.excelimport.parser.CSVUtil;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * <p>Title: ExcelUtil</P>
 * <p>Description: Excel工具</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-13
 */
public class ExcelUtil {
	
	String ENTER = "\n";
	
	int BILL_TEMPLATE_POSITION_BODY = 1;
	
	String COMMA = ",";
	
	static String QUOTE = "\"";
	
	String HEAD_TAB_KEY = "headTab";
	
	static String SHEET = "Sheet1";
	
	String CSV_SUFFIX = ".csv";
	
	String XLS_SUFFIX = ".xls";
	
	String XLSX_SUFFIX = ".xlsx";
	
	static String NOTICE_LINE_FLAG = "*";
	
	String SEMICOLON = ";";
	
	String DOT = ".";
	
	
	/**
	 * <p>方法名称：doImport</p>
	 * <p>方法描述：	导入数据入口</p>
	 * @param file	传入文件
	 * @return		返回Map<String, key:sheet名称,value:list<String>,行以,,分隔，列以,分隔
	 * @throws IOException
	 * @throws BusinessException
	 * @author xiaoh
	 * @since  2014-10-14
	 * <p> history 2014-10-14 xiaoh  创建   <p>
	 */
	public static Map<String, List<String>> doImport(File file) throws IOException, BusinessException{
		HSSFWorkbook workbook = (HSSFWorkbook) read(file);
		Map<String, List<String>> lines = read(workbook);
		return lines;
	}
	
	
	/**
	 * <p>方法名称：read</p>
	 * <p>方法描述：暂不实现.XLSX后缀文件</p>
	 * @param file
	 * @return
	 * @throws IOException
	 * @author xiaoh
	 * @since  2014-10-13
	 * <p> history 2014-10-13 xiaoh  创建   <p>
	 */
	private static Workbook read(File file) throws IOException{
		InputStream in = new FileInputStream(file);
		
		if(file.getPath().endsWith(".xlsx")){
//			return new XSSFWorkbook(in);
			return null;
		}else{
			return new HSSFWorkbook(new POIFSFileSystem(in));
		}
	}
	
	/**
	 * <p>方法名称：read</p>
	 * <p>方法描述：解析工作薄方法</p>
	 * @param workbook 工作薄
	 * @return 		   返回Map, key:sheet名称,value:list<String>每行数据String,以,分隔
	 * @throws BusinessException
	 * @author xiaoh
	 * @since  2014-10-13
	 * <p> history 2014-10-13 xiaoh  创建   <p>
	 */
	private static Map<String, List<String>> read(HSSFWorkbook workbook) throws BusinessException {
		List<String> lines = null;
		Map<String, List<String>> allLines = new HashMap<String, List<String>>();
		if (workbook == null) return allLines;
		for(int m=0;m<workbook.getNumberOfSheets();m++){
			lines = new ArrayList<String>();
			HSSFSheet sheet = workbook.getSheetAt(m);
			// Sheet不存在的情况
			if (sheet == null) {
				throw new BusinessException("sheet不存在");
			}
			int firstDataRowNum = getDataStartIndex(sheet);
			int num = sheet.getLastRowNum();// 末行行号
			HSSFRow row = sheet.getRow(firstDataRowNum);// 首行行号
			for (int i = firstDataRowNum; i < num + 1; i++) {
				if (row == null) {
					continue;
				}
				boolean isNullRow = sheet.getRow(i) == null;// 是否是空行
				String[] values = getSingleLineData(sheet, i, 0, row.getLastCellNum() - 1);
				String line = getParsedLine(values);
				lines.add(line);
				boolean isBlankRow = isBlankRow(isNullRow, values);
				if (isBlankRow) {// 空行意味着下一行即是子表数据区
					row = sheet.getRow(i + 1);
				}
			}
			allLines.put(sheet.getSheetName(), lines);
		}
		return allLines;
	}
	
	private static int getDataStartIndex(HSSFSheet sheet) throws BusinessException{
		HSSFRow row = sheet.getRow(0);
		if(row == null){
			return -1;
		}
		String[] values = getSingleLineData(sheet, 0, 0, row.getLastCellNum() - 1);
		String line = getParsedLine(values);
		if(isStartWithNoticeLineFlag(line)){
			return 1;
		}
		return 0;
	}
	
	private static boolean isStartWithNoticeLineFlag(String line) {
		if (StringUtils.isEmpty(line)) {
			return false;
		}
		if (line.startsWith(NOTICE_LINE_FLAG)
				|| line.startsWith(QUOTE + NOTICE_LINE_FLAG)) {
			return true;
		}
		return false;
	}
	
	private static String getParsedLine(String[] values){
		StringBuilder sb = new StringBuilder();
		for (String string : values) {
			sb.append(CSVUtil.csvEncode(string));
			sb.append(",");
		}
		return sb.toString();
	}
	
	private static boolean isBlankRow(boolean isNullRow, String[] values) {
		if (isNullRow) {
			return true;
		}
		if (ArrayUtils.isEmpty(values)) {
			return true;
		}
		boolean result = true;
		for (String value : values) {
			if (!StringUtil.isEmpty(value)) {
				result = false;
				break;
			}
		}
		return result;
	}
	
	private static String[] getSingleLineData(HSSFSheet sheet, int rowNum, int colFrom, int colTo) throws BusinessException {
		String[] dataLine = new String[colTo - colFrom + 1];
		if (rowNum < 0 || colFrom < 0 || colTo < 0) {
			return dataLine;
		}
		HSSFRichTextString richString = null;
		for (int col = 0; col < colTo - colFrom + 1; col++) {
			try{
				richString = getCell(sheet, rowNum, colFrom + col).getRichStringCellValue();
			}catch(Exception e){
				throw new BusinessException("请确定数据格式都是文本格式");
			}
			if (richString == null) {
				dataLine[col] = StringUtils.EMPTY;
			} else {
				dataLine[col] = richString.toString().trim();
			}
		}
		return dataLine;
	}
	
	private static HSSFCell getCell(HSSFSheet sheet, int rowNum, int colNum) {
		if (rowNum < 0 || colNum < 0) {
			return null;
		}
		HSSFRow row = getRow(sheet, rowNum);
		HSSFCell cell = row.getCell(colNum);
		if (cell == null) {
			cell = row.createCell(colNum);
		}
		return cell;
	}
	
	private static HSSFRow getRow(HSSFSheet sheet, int rowNum) {
		if (rowNum < 0) {
			return null;
		}
		HSSFRow row = sheet.getRow(rowNum);
		if (row == null) {
			row = sheet.createRow(rowNum);
		}
		return row;
	}
	
	
}
