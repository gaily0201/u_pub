package cn.gaily.base.ui.action;

import java.awt.event.ActionEvent;

import cn.gaily.base.ui.component.IActionVisible;

import nc.ui.uif2.NCAction;


/**
 * <p>Title: BaseAction</P>
 * <p>Description: ������ť����Ҫ���ư�ť�Ƿ���ʾ��ֱ�Ӽ̳б���ť����дisActionVisible������Ĭ��Ϊtrue</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-12-12
 */
public class BaseAction extends NCAction implements IActionVisible{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5928208893397708863L;

	
	public BaseAction(){
		setBtnName("������ť");
		setCode("baseaction");
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
	}
	
	@Override
	public boolean isActionVisible() {
		return true;
	}

}
