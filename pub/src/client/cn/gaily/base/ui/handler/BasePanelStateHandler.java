package cn.gaily.base.ui.handler;

import nc.ui.pubapp.uif2app.event.AppUiStateChangeEvent;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.view.BillForm;

//��ʹ��ͳһ�Ĺ���TempletUtil���Ʊ���Ϳɷ�༭
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
