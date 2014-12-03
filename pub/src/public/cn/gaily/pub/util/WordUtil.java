package cn.gaily.pub.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class WordUtil {
	
	private boolean saveOnExit;
	/**
	 * word�ĵ�
	 */
	private Dispatch doc = null;
	/**
	 * word���г������
	 */
	private ActiveXComponent word;
	/**
	 * ����word�ĵ�
	 */
	private Dispatch documents;
	// ����������ĸ��ġ�
	static final int wdDoNotSaveChanges = 0;
	// PDF ��ʽ
	static final int wdFormatPDF = 17;

	/**
	 * ���캯��
	 */
	public WordUtil() {
		saveOnExit = false;
		word = new ActiveXComponent("Word.Application");
		Dispatch.put(word, "Visible", new Variant(false));
		documents = word.getProperty("Documents").toDispatch();
	}

	public WordUtil(boolean visible) {
		saveOnExit = false;
		word = new ActiveXComponent("Word.Application");
		Dispatch.put(word, "Visible", new Variant(visible));
		documents = word.getProperty("Documents").toDispatch();
	}

	/**
	 * <p>
	 * �������ƣ�setSaveOnExit
	 * </p>
	 * <p>
	 * �����������˳�ʱ�Ƿ񱣴�
	 * </p>
	 * 
	 * @param saveOnExit true-�˳�ʱ�����ļ���false-�˳�ʱ�������ļ�
	 * @since 2013-12-31
	 */
	public void setSaveOnExit(boolean saveOnExit) {
		this.saveOnExit = saveOnExit;
	}

	/**
	 * <p>
	 * �������ƣ�getSaveOnExit
	 * </p>
	 * <p>
	 * �����������õ��������˳�ʱ�Ƿ񱣴�
	 * </p>
	 * 
	 * @return
	 * @since 2013-12-31
	 */
	public boolean getSaveOnExit() {
		return saveOnExit;
	}

	/**
	 * <p>
	 * �������ƣ�open
	 * </p>
	 * <p>
	 * ������������word�ļ�
	 * </p>
	 * 
	 * @param inputDoc
	 *            �ļ���·��������·��
	 * @return
	 * @since 2013-12-31
	 */
	public Dispatch open(String inputDoc) {
		doc = Dispatch.call(documents, "Open", inputDoc).toDispatch();
		return doc;
	}

	/**
	 * <p>
	 * �������ƣ�select
	 * </p>
	 * <p>
	 * ����������ѡ������
	 * </p>
	 * 
	 * @return
	 * @since 2013-12-31
	 */
	public Dispatch select() {
		return word.getProperty("Selection").toDispatch();
	}

	/**
	 * <p>
	 * �������ƣ�moveUp
	 * </p>
	 * <p>
	 * ���������������ƶ����
	 * </p>
	 * 
	 * @param selection
	 *            ������
	 * @param count
	 *            �ƶ��ľ���
	 * @since 2013-12-31
	 */
	public void moveUp(Dispatch selection, int count) {
		for (int i = 0; i < count; i++)
			Dispatch.call(selection, "MoveUp");
	}

	/**
	 * <p>
	 * �������ƣ�moveDown
	 * </p>
	 * <p>
	 * ���������������ƶ����
	 * </p>
	 * 
	 * @param selection
	 * @param count
	 */
	public void moveDown(Dispatch selection, int count) {
		for (int i = 0; i < count; i++)
			Dispatch.call(selection, "MoveDown");
	}

	/**
	 * <p>
	 * �������ƣ�moveLeft
	 * </p>
	 * <p>
	 * ���������������ƶ����
	 * </p>
	 * 
	 * @param selection
	 * @param count
	 * @since 2013-12-31
	 */
	public void moveLeft(Dispatch selection, int count) {
		for (int i = 0; i < count; i++)
			Dispatch.call(selection, "MoveLeft");
	}

	/**
	 * <p>
	 * �������ƣ�moveRight
	 * </p>
	 * <p>
	 * ����������
	 * </p>
	 * 
	 * @param selection
	 * @param count
	 * @since 2013-12-31
	 */
	public void moveRight(Dispatch selection, int count) {
		for (int i = 0; i < count; i++)
			Dispatch.call(selection, "MoveRight");
	}

	/**
	 * <p>
	 * �������ƣ�moveStart
	 * </p>
	 * <p>
	 * �����������Ѳ�����ƶ����ļ���λ��
	 * </p>
	 * 
	 * @param selection
	 * @since 2013-12-31
	 */
	public void moveStart(Dispatch selection) {
		Dispatch.call(selection, "HomeKey", new Variant(6));
	}

	/**
	 * <p>
	 * �������ƣ�moveLast
	 * </p>
	 * <p>
	 * �����������Ѳ�����ƶ����ļ�ĩλ��
	 * </p>
	 * 
	 * @param selection
	 * @since 2013-12-31
	 */
	public void moveLast(Dispatch selection) {
		Dispatch.call(selection, "EndKey", new Variant(6));
	}

	/**
	 * <p>
	 * �������ƣ�find
	 * </p>
	 * <p>
	 * ������������ѡ�����ݻ����㿪ʼ�����ı�
	 * </p>
	 * 
	 * @param selection
	 * @param toFindText
	 * @return boolean true-���ҵ���ѡ�и��ı���false-δ���ҵ��ı�
	 * @since 2013-12-31
	 */
	public boolean find(Dispatch selection, String toFindText) {
		// ��selection����λ�ÿ�ʼ��ѯ
		Dispatch find = Dispatch.call(selection, "Find").toDispatch();
		// ����Ҫ���ҵ�����
		Dispatch.put(find, "Text", toFindText);
		// ��ǰ����
		Dispatch.put(find, "Forward", "True");
		// ���ø�ʽ
		Dispatch.put(find, "Format", "True");
		// ��Сдƥ��
		Dispatch.put(find, "MatchCase", "True");
		// ȫ��ƥ��
		Dispatch.put(find, "MatchWholeWord", "True");
		// ���Ҳ�ѡ��
		return Dispatch.call(find, "Execute").getBoolean();
	}

	/**
	 * <p>
	 * �������ƣ�replace
	 * </p>
	 * <p>
	 * ������������ѡ�������滻Ϊ�趨�ı�
	 * </p>
	 * 
	 * @param selection
	 * @param newText
	 * @author gaomeng
	 */
	public void replace(Dispatch selection, String newText) {
		// �����滻�ı�
		Dispatch.put(selection, "Text", newText);
	}

	/**
	 * <p>
	 * �������ƣ�replaceAll
	 * </p>
	 * <p>
	 * ����������ȫ���滻,֧�ֱ����½��滻
	 * </p>
	 * 
	 * @param selection
	 * @param oldText
	 * @param replaceObj
	 * @since 2013-12-31
	 */
	@SuppressWarnings("rawtypes")
	public void replaceAll(Dispatch selection, String oldText, Object replaceObj) {
		// �ƶ����ļ���ͷ
		moveStart(selection);
		if (oldText.startsWith("table") || replaceObj instanceof List) {
			replaceTable(selection, oldText, (List) replaceObj);
		} else {
			String newText = (String) replaceObj;
			if (oldText.indexOf("image") != -1
					|| newText.lastIndexOf(".bmp") != -1
					|| newText.lastIndexOf(".jpg") != -1
					|| newText.lastIndexOf(".gif") != -1)
				while (find(selection, oldText)) {
					replaceImage(selection, newText);
					Dispatch.call(selection, "MoveRight");
				}
			else
				while (find(selection, oldText)) {
					replace(selection, newText);
					Dispatch.call(selection, "MoveRight");
				}
		}
		moveStart(selection);
	}

	/**
	 * <p>
	 * �������ƣ�replaceImage
	 * </p>
	 * <p>
	 * �����������滻ͼƬ
	 * </p>
	 * 
	 * @param selection
	 * @param imagePath
	 * @since 2013-12-31
	 */
	public void replaceImage(Dispatch selection, String imagePath) {
		Dispatch.call(Dispatch.get(selection, "InLineShapes").toDispatch(),
				"AddPicture", imagePath);
	}

	/**
	 * <p>
	 * �������ƣ�createTable
	 * </p>
	 * <p>
	 * ��������������List<String[]>����Table
	 * </p>
	 * 
	 * @param selection
	 * @param tableName
	 *            table�ı�ǩ
	 * @param dataList
	 *            �������ݣ�����
	 * @since 2013-12-31
	 */
	public void createTable(Dispatch selection, String tableName,
			List<String[]> dataList) {
		moveStart(selection);
		if (find(selection, tableName)) {
			int row = dataList.size();
			Dispatch tables = Dispatch.get(selection, "Tables").toDispatch();
			Dispatch range = Dispatch.get(selection, "Range").toDispatch();
			Dispatch newTable = Dispatch.call(tables, "Add", range,
					new Variant(row), new Variant(dataList.get(0).length),
					new Variant(1)).toDispatch();
			for (int i = 0; i < row; i++) {
				String[] str = dataList.get(i);
				for (int j = 0; j < str.length; j++) {
					String s = str[j];
					Dispatch cell = Dispatch.call(newTable, "Cell",
							new Variant(i + 1), new Variant(j + 1))
							.toDispatch();
					Dispatch.call(cell, "Select");
					Dispatch.put(selection, "Text", s);
				}
			}
			Dispatch.call(selection, "MoveRight");
		}
	}

	/**
	 * <p>
	 * �������ƣ�replaceTable
	 * </p>
	 * <p>
	 * �����������滻����е�����(Ŀǰû��ʹ�õģ�û�н�������)
	 * </p>
	 * 
	 * @param selection
	 * @param tableName
	 * @param dataList
	 * @since 2013-12-31
	 */
	@SuppressWarnings("rawtypes")
	public void replaceTable(Dispatch selection, String tableName, List dataList) {
		if (dataList.size() <= 1) {
			System.out.println("Empty table!");
			return;
		}
		// Ҫ������
		String[] cols = (String[]) dataList.get(0);
		// ������
		String tbIndex = tableName.substring(tableName.lastIndexOf("@") + 1);
		// �ӵڼ��п�ʼ���
		int fromRow = Integer.parseInt(tableName.substring(
				tableName.lastIndexOf("$") + 1, tableName.lastIndexOf("@")));
		// ���б��
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		// Ҫ���ı��
		Dispatch table = Dispatch.call(tables, "Item", new Variant(tbIndex))
				.toDispatch();
		// ����������
		Dispatch rows = Dispatch.get(table, "Rows").toDispatch();
		// �����
		for (int i = 1; i < dataList.size(); i++) {
			// ĳһ������
			String[] datas = (String[]) dataList.get(i);
			// �ڱ�������һ��
			if (Dispatch.get(rows, "Count").getInt() < fromRow + i - 1)
				Dispatch.call(rows, "Add");
			// �����е������
			for (int j = 0; j < datas.length; j++) {
				// �õ���Ԫ��
				Dispatch cell = Dispatch.call(table, "Cell",
						Integer.toString(fromRow + i - 1), cols[j])
						.toDispatch();
				// ѡ�е�Ԫ��
				Dispatch.call(cell, "Select");
				// ���ø�ʽ
				Dispatch font = Dispatch.get(selection, "Font").toDispatch();
				Dispatch.put(font, "Bold", "0");
				Dispatch.put(font, "Italic", "0");
				// ��������
				Dispatch.put(selection, "Text", datas[j]);
			}
		}
	}

	/**
	 * <p>
	 * �������ƣ�save
	 * </p>
	 * <p>
	 * ���������������ļ�
	 * </p>
	 * 
	 * @param outputPath
	 *            �ļ��ı���·��
	 * @param type
	 *            �ļ��ı������ͣ�2Ϊpdf����
	 * @return �ļ���·��
	 * @since 2013-12-31
	 */
	public String save(String outputPath, String type) {
		int itype = 0;
		if (type.equals("2")) {
			itype = 17;
			outputPath = outputPath.replaceAll("doc", "pdf");// word����Ϊpdf�ļ�
		}
		Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] {
				outputPath, new Variant(itype) }, new int[1]);
		return outputPath;
	}

	/**
	 * <p>
	 * �������ƣ�close
	 * </p>
	 * <p>
	 * �����������ر��ļ�
	 * </p>
	 * 
	 * @param doc
	 * @since 2013-12-31
	 */
	public void close(Dispatch doc) {
		Dispatch.call(doc, "Close", new Variant(saveOnExit));
	}

	/**
	 * <p>
	 * �������ƣ�quit
	 * </p>
	 * <p>
	 * �����������˳�word����
	 * </p>
	 * 
	 * @since 2013-12-31
	 */
	public void quit() {
		word.invoke("Quit", new Variant[0]);
		ComThread.Release();
	}

	/**
	 * <p>
	 * �������ƣ�replaceByMap
	 * </p>
	 * <p>
	 * �������������ݴ����map�滻map��key��ǩ��ֵ
	 * </p>
	 * 
	 * @param inputpath
	 *            �滻�ļ���·��
	 * @param data
	 *            Ҫ�滻������
	 * @param type
	 * @since 2013-12-31
	 */
	@SuppressWarnings("unchecked")
	public void replaceByMap(Map<String, Object> data) {
		String oldText;
		Object newValue;
		try {
			Dispatch selection = select();
			@SuppressWarnings("rawtypes")
			Iterator keys = data.keySet().iterator();
			while (keys.hasNext()) {
				oldText = (String) keys.next();
				if (oldText.contains("tab")) {
					newValue = data.get(oldText);
					List<String[]> table = (List<String[]>) newValue;
					oldText = "$" + oldText + "$";
					if (table.size() > 0) {
						createTable(selection, oldText,
								(List<String[]>) newValue);
					} else {
						newValue = "δ�鵽�����������";
						replaceAll(selection, oldText, String.valueOf(newValue));
					}
				} else {
					newValue = data.get(oldText);
					oldText = "$" + oldText + "$";
					replaceAll(selection, oldText, String.valueOf(newValue));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * �������ƣ�joinDoc
	 * </p>
	 * <p>
	 * ����������
	 * </p>
	 * 
	 * @param pk
	 * @param filepaths
	 * @param mapdata
	 * @param type
	 * @return
	 * @since 2013-12-31
	 */
	public String joinDoc(String pk, String[] filepaths,
			Map<String, Object> mapdata, String type) {
		String path = null;
		File file = null;
		if (filepaths.length == 0) {
			return path;
		}
		try {
			if (filepaths[0] != null && filepaths.length == 1) {
				// ������ʱ�ļ�,��ʱ�ļ�����,ʹ������������Ϊ�ļ���
				// file=File.createTempFile("hdpt", ".doc");
				file = File.createTempFile(pk, ".doc");
				path = file.getAbsolutePath();
				// �򿪵�һ���ļ�
				doc = open(filepaths[0]);
				// �ļ�
				replaceByMap(mapdata);
			}
			// �����µ�word�ļ�
			path = save(path, type);
			return path;

		} catch (Exception e) {
			throw new RuntimeException("�ϲ�word�ļ�����.ԭ��:" + e.getMessage());
		} finally {
			// try{
			// if(file!=null && file.exists()){
			// file.delete();
			// }
			// }catch(Exception e){
			//
			// }
			close(doc);
		}
	}

	public String joinDoc(List<String[]> listpaths,
			List<ArrayList<HashMap<String, Object>>> listdata, String type) {
		String path = null;
		try {
			path = File.createTempFile("temp", ".doc").getAbsolutePath();
			// �򿪵�һ���ļ���׷�ӵ�һ���ļ�
			doc = open(listpaths.get(0)[0]);
			replaceByMap(listdata.get(0).get(0));
			for (int i = 1; i < listpaths.get(0).length; i++) {
				Dispatch.invoke(word.getProperty("Selection").toDispatch(),
						"insertFile", Dispatch.Method,
						new Object[] { listpaths.get(0)[i], "",
								new Variant(false), new Variant(false),
								new Variant(false) }, new int[3]);
				replaceByMap(listdata.get(0).get(i));
			}
			// ׷���ļ�
			for (int i = 1; i < listpaths.size(); i++) {
				join(listpaths.get(i), listdata.get(i));
			}

			// �����µ�word�ļ�
			save(path, type);
			return path;

		} catch (Exception e) {
			throw new RuntimeException("�ϲ�word�ļ�����.ԭ��:" + e.getMessage());
		} finally {
			close(doc);
		}
	}

	private void join(String[] filepaths, List<HashMap<String, Object>> listdata) {
		if (filepaths.length == 0 || filepaths.length != listdata.size()) {
			return;
		}

		// ׷���ļ�
		for (int i = 0; i < filepaths.length; i++) {
			Dispatch.invoke(word.getProperty("Selection").toDispatch(),
					"insertFile", Dispatch.Method, new Object[] { filepaths[i],
							"", new Variant(false), new Variant(false),
							new Variant(false) }, new int[3]);
			replaceByMap(listdata.get(i));
		}
	}

	/** */
	/**
	 * ��ӡ��ǰword�ĵ�
	 * 
	 */
	public void printFile() {
		if (doc != null) {
			Dispatch.call(doc, "PrintOut");
		}
	}

}
