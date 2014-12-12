package cn.gaily.base.ui.action;

import java.awt.event.ActionEvent;

import cn.gaily.base.ui.component.IActionVisible;

import nc.ui.uif2.NCAction;


/**
 * <p>Title: BaseAction</P>
 * <p>Description: 基础按钮，需要控制按钮是否显示可直接继承本按钮，重写isActionVisible方法，默认为true</p>
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
		setBtnName("基础按钮");
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
