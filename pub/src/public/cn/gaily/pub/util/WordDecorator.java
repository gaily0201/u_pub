package cn.gaily.pub.util;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class WordDecorator extends org.eclipse.swt.widgets.Composite {

	private OleFrame oleFrame1;

	private OleClientSite site;

	private String fileName;

	Composite _parent = null;

	private OleAutomation oleAutomation = null;

	private static Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	

	/**
	 * 
	 * whiliang 2014-06-23增加接口：接受所有修订 必须在SWT自有线程中调用
	 */
	public void acceptAllRevisions() {
		if (site != null) {
			if (oleAutomation != null) {
				oleAutomation.dispose();  
				oleAutomation=null; 
			}
			oleAutomation = new OleAutomation(site);
			int[] appId = oleAutomation
					.getIDsOfNames(new String[] { "AcceptAllRevisions" });
			oleAutomation.invoke(appId[0]);
		}
	}

	/**
	 * @Description:判断文件是否修改
	 * @return
	 */
	public boolean isFileModified() {
		if (site!=null && site.isDirty())
			return true;
		else
			return false;
	}

	public void release() {
		if (site.isDirty()) {
		} else {
		}
		if (site != null) {
			site.dispose();
			site = null;
		}

		if (oleFrame1 != null) {
			oleFrame1.dispose();
			oleFrame1 = null;
		}

		if (_parent != null) {
			if (_parent instanceof Shell) {
				Shell shell = (Shell) _parent;
				shell.close();
			}
		}
	}

	public static void showGUI() {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setText("SWT集成Word");
		new WordDecorator(shell, SWT.NULL, "");

		shell.setLayout(new FillLayout());
		shell.layout();
		shell.setSize(d.width, d.height);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	
	public WordDecorator(Composite parent, int style, String fileName) {
		super(parent, style);
		this._parent = parent;
		this.fileName = fileName;
		initGUI();

	}
	
	private void initGUI() {
		try {
			FillLayout thisLayout = new FillLayout(
					org.eclipse.swt.SWT.HORIZONTAL);
			this.setLayout(thisLayout);
			{
				oleFrame1 = new OleFrame(this, SWT.NONE);
				{
					try {
						site = new org.eclipse.swt.ole.win32.OleClientSite(
								oleFrame1, org.eclipse.swt.SWT.NONE, new File(
										this.fileName));// "Word.Document");//
														// Word.Document是Word的ID，
						// 还比如，如果是PDF的话，就可以使用PDF.PdfCtrl.5这样的来打开PDF文件

						site.setBounds(0, 0, 104, 54);
						site.doVerb(org.eclipse.swt.ole.win32.OLE.OLEIVERB_SHOW);
					} catch (org.eclipse.swt.SWTException e) {
						String str = "创建OleClientSite发送错误，原因:" + e.toString();
						System.out.println(str);
						return;
					}
				}
			}

			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void open(File file) {

		try {
			OleClientSite newSite = new org.eclipse.swt.ole.win32.OleClientSite(
					oleFrame1, org.eclipse.swt.SWT.NONE, file);
			newSite.setBounds(0, 0, d.width, d.height);
			newSite.doVerb(org.eclipse.swt.ole.win32.OLE.OLEIVERB_SHOW);
			site.dispose();
			site = newSite;
		} catch (org.eclipse.swt.SWTException e) {
			String str = "创建OleClientSite发送错误，原因:" + e.toString();
			System.out.println(str);
			return;
		}
	}

	public void open(String strName) {
		open(new File(strName));
	}

	public void save(String strName) {
		site.save(new File(strName), true);
	}

}
