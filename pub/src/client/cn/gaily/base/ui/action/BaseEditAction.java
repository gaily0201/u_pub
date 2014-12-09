package cn.gaily.base.ui.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.EditAction;

public class BaseEditAction extends EditAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1188233986990892392L;

	private ShowUpableBillForm editor;
	
	public BaseEditAction(){
		this.setBtnName("ÐÞ¸Ä");
		this.setCode("edit");
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
	}

	public ShowUpableBillForm getEditor() {
		return editor;
	}
	public void setEditor(ShowUpableBillForm editor) {
		this.editor = editor;
	}
	
	
}
