package cn.gaily.base.ui.handler;

import nc.ui.pubapp.uif2app.event.AppUiStateChangeEvent;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.view.BillForm;

//可使用统一的工具TempletUtil控制必输和可否编辑
public class BasePanelStateHandler implements IAppEventHandler<AppUiStateChangeEvent> {

	private BillForm editor;

	@Override
	public void handleAppEvent(AppUiStateChangeEvent e) {
		
	}

	
	public BillForm getEditor() {
		return editor;
	}
	public void setEditor(BillForm editor) {
		this.editor = editor;
	}
		
}
