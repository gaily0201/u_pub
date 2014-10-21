package cn.gaily.action;

import nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction;

public class RefreshAction extends DefaultRefreshAction{
	private static final long serialVersionUID = -4055115939751569616L;
	
	public RefreshAction() {
		this.setBtnName("Ë¢ÐÂ");
		this.setCode("refresh");
	}
	
	@Override
	protected boolean isActionEnable() {
		return true;
	}
}
