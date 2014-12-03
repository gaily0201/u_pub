package cn.gaily.pub.test;

import java.awt.Dimension;
import java.awt.Toolkit;

import nc.bs.framework.test.AbstractTestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import cn.gaily.pub.util.WordDecorator;

public class TestWordDecorator extends AbstractTestCase {
	public static void test() {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setText("SWT集成Word");
		
		shell.setLayout(new GridLayout(1, false));
		shell.layout();
		shell.setSize(d.width, d.height);
//
		ToolBar bar = new ToolBar(shell, SWT.NONE);
		ToolItem item = new ToolItem(bar, SWT.NONE);
		bar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		item.setText("测试工具栏");

		WordDecorator w = new WordDecorator(shell, SWT.NULL, "c:\\test.doc");
		w.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true, true));
		

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
