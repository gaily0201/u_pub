package cn.gaily.pub.test;

import java.awt.Dimension;
import java.awt.Toolkit;

import nc.bs.framework.test.AbstractTestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import cn.gaily.pub.util.WordDecorator;

public class TestWordDecorator extends AbstractTestCase {
	public static void test() {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setText("SWTºØ≥…Word");
		
		shell.setLayout(new GridLayout(1, false));
		shell.layout();
		shell.setSize(d.width, d.height);
		
		Button button = new Button(shell,SWT.BORDER);
		button.setText("≤‚ ‘∞¥≈•");
		button.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		button.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
			}
			@Override
			public void mouseDown(MouseEvent e) {
				System.out.println("abcdefg");
			}
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		
		Composite composite = new WordDecorator(shell, SWT.NULL, "c:\\a.docx");
		composite.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,true, 2, 4));
		
		WordDecorator w = new WordDecorator(shell, SWT.NULL, "c:\\a.docx");
		w.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,true,true,2,4));
		
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
