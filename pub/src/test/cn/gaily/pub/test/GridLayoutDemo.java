package cn.gaily.pub.test;
import nc.bs.framework.test.AbstractTestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
 
/**
 * SWT FillLayout布局使用demo 发邮件
 * @author xwalker
 *
 */
public class GridLayoutDemo extends AbstractTestCase{
    private Shell shell;
    private Text toAddrText;
    private Text topicText;
    private Text ccText;
    private Text labelText;
    private Button sendBtn;
    private Button timSendBtn;
    private Button saveBtn;
    private Composite composite;
    private Group group;
     
    public void open() {
        Display display = Display.getDefault();
        createContents();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }
    /**
     * 创建窗口组件
     */
    protected void createContents(){
        shell=new Shell();
        shell.setText("发邮件");
        shell.setSize(800, 450);
        shell.setLayout(new GridLayout(5, false));
         
        Label toLabel = new Label(shell, SWT.NONE);
        toLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        toLabel.setText("收件人");
         
        toAddrText = new Text(shell, SWT.BORDER);
        toAddrText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
         
        composite = new Composite(shell, SWT.NONE);
        composite.setLayout(new FillLayout(SWT.HORIZONTAL));
        GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 4);
        gd_composite.widthHint = 171;
        composite.setLayoutData(gd_composite);
         
        group = new Group(composite, SWT.NONE);
        group.setText("联系人");
        group.setLayout(new FillLayout(SWT.HORIZONTAL));
         
        List list = new List(group, SWT.BORDER);
        insertData(list);
        Label topiclabel = new Label(shell, SWT.NONE);
        topiclabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        topiclabel.setText("主题");
         
        topicText = new Text(shell, SWT.BORDER);
        topicText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
         
        Label ccLabel = new Label(shell, SWT.NONE);
        ccLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        ccLabel.setText("抄送");
         
        ccText = new Text(shell, SWT.BORDER);
        ccText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
         
        Label contentLabel = new Label(shell, SWT.NONE);
        contentLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        contentLabel.setText("内容");
         
        labelText = new Text(shell, SWT.BORDER);
        GridData gd_labelText = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);
        gd_labelText.heightHint = 232;
        labelText.setLayoutData(gd_labelText);
        new Label(shell, SWT.NONE);
         
        sendBtn = new Button(shell, SWT.NONE);
        sendBtn.setText("发送");
         
        timSendBtn = new Button(shell, SWT.NONE);
        timSendBtn.setText("定时发送");
         
        saveBtn = new Button(shell, SWT.NONE);
        saveBtn.setText("存草稿");
        new Label(shell, SWT.NONE);
         
        shell.open();
    }
 
    private void insertData(List list) {
        for(int i=1;i<=20;i++){
            list.add("联系人"+i);
        }
         
    }
    public void test() {
        GridLayoutDemo demo=new GridLayoutDemo();
        demo.open();
    }
}