package cn.gaily.base.ui.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;

public class BaseAddAction extends NCAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1188233986990892392L;

	
	private BillManageModel model;
	
	public BaseAddAction(){
		this.setBtnName("ÐÂÔö");
		this.setCode("add");
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		getModel().setUiState(UIState.ADD);
	}

	public BillManageModel getModel() {
		return model;
	}
	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}
	
}
