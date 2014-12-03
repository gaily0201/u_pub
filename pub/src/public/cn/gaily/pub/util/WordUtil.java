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
	 * word文档
	 */
	private Dispatch doc = null;
	/**
	 * word运行程序对象
	 */
	private ActiveXComponent word;
	/**
	 * 所有word文档
	 */
	private Dispatch documents;
	// 不保存待定的更改。
	static final int wdDoNotSaveChanges = 0;
	// PDF 格式
	static final int wdFormatPDF = 17;

	/**
	 * 构造函数
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
	 * 方法名称：setSaveOnExit
	 * </p>
	 * <p>
	 * 方法描述：退出时是否保存
	 * </p>
	 * 
	 * @param saveOnExit true-退出时保存文件，false-退出时不保存文件
	 * @since 2013-12-31
	 */
	public void setSaveOnExit(boolean saveOnExit) {
		this.saveOnExit = saveOnExit;
	}

	/**
	 * <p>
	 * 方法名称：getSaveOnExit
	 * </p>
	 * <p>
	 * 方法描述：得到参数：退出时是否保存
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
	 * 方法名称：open
	 * </p>
	 * <p>
	 * 方法描述：打开word文件
	 * </p>
	 * 
	 * @param inputDoc
	 *            文件的路径，物理路径
	 * @return
	 * @since 2013-12-31
	 */
	public Dispatch open(String inputDoc) {
		doc = Dispatch.call(documents, "Open", inputDoc).toDispatch();
		return doc;
	}

	/**
	 * <p>
	 * 方法名称：select
	 * </p>
	 * <p>
	 * 方法描述：选定内容
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
	 * 方法名称：moveUp
	 * </p>
	 * <p>
	 * 方法描述：向上移动光标
	 * </p>
	 * 
	 * @param selection
	 *            光标对象
	 * @param count
	 *            移动的距离
	 * @since 2013-12-31
	 */
	public void moveUp(Dispatch selection, int count) {
		for (int i = 0; i < count; i++)
			Dispatch.call(selection, "MoveUp");
	}

	/**
	 * <p>
	 * 方法名称：moveDown
	 * </p>
	 * <p>
	 * 方法描述：向下移动光标
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
	 * 方法名称：moveLeft
	 * </p>
	 * <p>
	 * 方法描述：向左移动光标
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
	 * 方法名称：moveRight
	 * </p>
	 * <p>
	 * 方法描述：
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
	 * 方法名称：moveStart
	 * </p>
	 * <p>
	 * 方法描述：把插入点移动到文件首位置
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
	 * 方法名称：moveLast
	 * </p>
	 * <p>
	 * 方法描述：把插入点移动到文件末位置
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
	 * 方法名称：find
	 * </p>
	 * <p>
	 * 方法描述：从选定内容或插入点开始查找文本
	 * </p>
	 * 
	 * @param selection
	 * @param toFindText
	 * @return boolean true-查找到并选中该文本，false-未查找到文本
	 * @since 2013-12-31
	 */
	public boolean find(Dispatch selection, String toFindText) {
		// 从selection所在位置开始查询
		Dispatch find = Dispatch.call(selection, "Find").toDispatch();
		// 设置要查找的内容
		Dispatch.put(find, "Text", toFindText);
		// 向前查找
		Dispatch.put(find, "Forward", "True");
		// 设置格式
		Dispatch.put(find, "Format", "True");
		// 大小写匹配
		Dispatch.put(find, "MatchCase", "True");
		// 全字匹配
		Dispatch.put(find, "MatchWholeWord", "True");
		// 查找并选中
		return Dispatch.call(find, "Execute").getBoolean();
	}

	/**
	 * <p>
	 * 方法名称：replace
	 * </p>
	 * <p>
	 * 方法描述：把选定内容替换为设定文本
	 * </p>
	 * 
	 * @param selection
	 * @param newText
	 * @author gaomeng
	 */
	public void replace(Dispatch selection, String newText) {
		// 设置替换文本
		Dispatch.put(selection, "Text", newText);
	}

	/**
	 * <p>
	 * 方法名称：replaceAll
	 * </p>
	 * <p>
	 * 方法描述：全局替换,支持表格的新建替换
	 * </p>
	 * 
	 * @param selection
	 * @param oldText
	 * @param replaceObj
	 * @since 2013-12-31
	 */
	@SuppressWarnings("rawtypes")
	public void replaceAll(Dispatch selection, String oldText, Object replaceObj) {
		// 移动到文件开头
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
	 * 方法名称：replaceImage
	 * </p>
	 * <p>
	 * 方法描述：替换图片
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
	 * 方法名称：createTable
	 * </p>
	 * <p>
	 * 方法描述：根据List<String[]>创建Table
	 * </p>
	 * 
	 * @param selection
	 * @param tableName
	 *            table的标签
	 * @param dataList
	 *            表格的内容，包含
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
	 * 方法名称：replaceTable
	 * </p>
	 * <p>
	 * 方法描述：替换表格中的内容(目前没有使用的，没有进行完善)
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
		// 要填充的列
		String[] cols = (String[]) dataList.get(0);
		// 表格序号
		String tbIndex = tableName.substring(tableName.lastIndexOf("@") + 1);
		// 从第几行开始填充
		int fromRow = Integer.parseInt(tableName.substring(
				tableName.lastIndexOf("$") + 1, tableName.lastIndexOf("@")));
		// 所有表格
		Dispatch tables = Dispatch.get(doc, "Tables").toDispatch();
		// 要填充的表格
		Dispatch table = Dispatch.call(tables, "Item", new Variant(tbIndex))
				.toDispatch();
		// 表格的所有行
		Dispatch rows = Dispatch.get(table, "Rows").toDispatch();
		// 填充表格
		for (int i = 1; i < dataList.size(); i++) {
			// 某一行数据
			String[] datas = (String[]) dataList.get(i);
			// 在表格中添加一行
			if (Dispatch.get(rows, "Count").getInt() < fromRow + i - 1)
				Dispatch.call(rows, "Add");
			// 填充该行的相关列
			for (int j = 0; j < datas.length; j++) {
				// 得到单元格
				Dispatch cell = Dispatch.call(table, "Cell",
						Integer.toString(fromRow + i - 1), cols[j])
						.toDispatch();
				// 选中单元格
				Dispatch.call(cell, "Select");
				// 设置格式
				Dispatch font = Dispatch.get(selection, "Font").toDispatch();
				Dispatch.put(font, "Bold", "0");
				Dispatch.put(font, "Italic", "0");
				// 输入数据
				Dispatch.put(selection, "Text", datas[j]);
			}
		}
	}

	/**
	 * <p>
	 * 方法名称：save
	 * </p>
	 * <p>
	 * 方法描述：保存文件
	 * </p>
	 * 
	 * @param outputPath
	 *            文件的保存路径
	 * @param type
	 *            文件的保存类型，2为pdf类型
	 * @return 文件的路径
	 * @since 2013-12-31
	 */
	public String save(String outputPath, String type) {
		int itype = 0;
		if (type.equals("2")) {
			itype = 17;
			outputPath = outputPath.replaceAll("doc", "pdf");// word保存为pdf文件
		}
		Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] {
				outputPath, new Variant(itype) }, new int[1]);
		return outputPath;
	}

	/**
	 * <p>
	 * 方法名称：close
	 * </p>
	 * <p>
	 * 方法描述：关闭文件
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
	 * 方法名称：quit
	 * </p>
	 * <p>
	 * 方法描述：退出word程序
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
	 * 方法名称：replaceByMap
	 * </p>
	 * <p>
	 * 方法描述：根据传入的map替换map中key标签的值
	 * </p>
	 * 
	 * @param inputpath
	 *            替换文件的路径
	 * @param data
	 *            要替换的数据
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
						newValue = "未查到相关数据数据";
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
	 * 方法名称：joinDoc
	 * </p>
	 * <p>
	 * 方法描述：
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
				// 创建临时文件,临时文件名称,使用主表主键作为文件名
				// file=File.createTempFile("hdpt", ".doc");
				file = File.createTempFile(pk, ".doc");
				path = file.getAbsolutePath();
				// 打开第一个文件
				doc = open(filepaths[0]);
				// 文件
				replaceByMap(mapdata);
			}
			// 保存新的word文件
			path = save(path, type);
			return path;

		} catch (Exception e) {
			throw new RuntimeException("合并word文件出错.原因:" + e.getMessage());
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
			// 打开第一个文件并追加第一组文件
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
			// 追加文件
			for (int i = 1; i < listpaths.size(); i++) {
				join(listpaths.get(i), listdata.get(i));
			}

			// 保存新的word文件
			save(path, type);
			return path;

		} catch (Exception e) {
			throw new RuntimeException("合并word文件出错.原因:" + e.getMessage());
		} finally {
			close(doc);
		}
	}

	private void join(String[] filepaths, List<HashMap<String, Object>> listdata) {
		if (filepaths.length == 0 || filepaths.length != listdata.size()) {
			return;
		}

		// 追加文件
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
	 * 打印当前word文档
	 * 
	 */
	public void printFile() {
		if (doc != null) {
			Dispatch.call(doc, "PrintOut");
		}
	}

}
