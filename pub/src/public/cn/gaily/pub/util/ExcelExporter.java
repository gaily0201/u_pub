package cn.gaily.pub.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import nc.bs.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * <p>Title: ExcelExportUtils</P>
 * <p>Description: </p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
 * <p>		ExcelExportUtils export = new ExcelExportUtils();
			export.setData(datas);
			export.exportExcelFile(false,"c:\\test.xls");
	</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-16
 */
public class ExcelExporter {
	private Object[][] m_data = null; // Excel����

	private String[] m_colnames = null; // ����

	HSSFWorkbook wb = null;

	HSSFSheet hs = null;

	private HSSFCellStyle titleCellStyle = null;

	public ExcelExporter() {
		super();
	}

	
	/**
	 * �ɹ���������1;
	 * �ļ���д���󷵻�-1;
	 * ��������0;
	 * @return
	 */

	public int exportExcelFile(boolean isAppend, String filePath) {
		boolean isCreNew = false;
		if (isAppend) {
			try {
				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(
						filePath));
				wb = new HSSFWorkbook(fs);
				hs = wb.getSheetAt(0);
				fillData2Sheet(hs);
				return write2File(filePath);
			} catch (IOException e) {
				// ����ļ�����??���½�һ??
				isCreNew = true;
			}
		} else {
			isCreNew = true;
		}

		if (isCreNew) {
			if (wb == null)
				wb = new HSSFWorkbook(); // ��������??
			String stname = nc.ui.ml.NCLangRes.getInstance().getStrByID(
					"10100108", "UPP10100108-000876")/* @res "��" */
					+ (wb.getNumberOfSheets() + 1);
			hs = wb.createSheet(); // ������
			// ����Encode,��֤������ʾ  zhouzhenga
//			wb.setSheetName(0, stname, HSSFWorkbook.ENCODING_UTF_16);
			fillTitle2Sheet(hs);
			fillData2Sheet(hs);
			return write2File(filePath);
		}
		return -1;
	}

	// ���Excel�ı���
	private void fillTitle2Sheet(HSSFSheet hsheet) {
		if (m_colnames != null) {
			HSSFRow row = hsheet.createRow(0);
			row.setHeight((short) (row.getHeight() * 2));
			HSSFCell cell = null;
			for (int i = 0; i < m_colnames.length; i++) {
				cell = row.createCell((short) i);
				cell.setCellStyle(getTitleCellStyle());
				if (m_colnames[i] != null)
					cell.setCellValue(m_colnames[i].toString());
			}
		}
	}

	// �õ�����ķ��
	private HSSFCellStyle getTitleCellStyle() {
		if (titleCellStyle == null) {
			titleCellStyle = wb.createCellStyle();
			titleCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			titleCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			titleCellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS);
			titleCellStyle
					.setFillBackgroundColor(HSSFColor.GREY_40_PERCENT.index);
			HSSFFont font = wb.createFont();
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			titleCellStyle.setFont(font);

		}
		return titleCellStyle;
	}

	// ���sheet����
	private void fillData2Sheet(HSSFSheet hsheet) {
		if (m_data != null) {
			HSSFRow row = null;
			HSSFCell cell = null;
			int rowid = hsheet.getLastRowNum();
			for (int i = 0; i < m_data.length; i++) {
				row = hsheet.createRow(rowid + i + 1);
				Object[] rowObj = m_data[i];
				for (int j = 0; j < rowObj.length; j++) {
					cell = row.createCell((short) j);
					if (rowObj[j] != null)
						cell.setCellValue(rowObj[j].toString());
				}
			}
		}
	}

	// ��workbookд���ļ�
	private int write2File(String filePath) {
		FileOutputStream fileOut = null;
		try {// д���ļ�
			fileOut = new FileOutputStream(filePath);
			wb.write(fileOut);// ��Workbook����������ļ�workbook.xls??
			return 1;
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return -1;
		} finally {
			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (IOException e) {
					Logger.error(e.getMessage(), e);
				}
			}
		}
	}

	public void setColNames(String[] colnames) {
		m_colnames = colnames;
	}

	public void setData(Object[][] data) {
		m_data = data;
	}

}
