package cn.gaily.base.ui.validate;

import nc.bs.uif2.validation.IValidationService;
import nc.bs.uif2.validation.ValidationException;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import cn.gaily.base.ui.proxy.BaseServiceProxy;

public class BaseValidateService implements IValidationService {

	private ShowUpableBillForm editor;
	private BaseServiceProxy service;
	
	@Override
	public void validate(Object obj) throws ValidationException {

	}

	
	public ShowUpableBillForm getEditor() {
		return editor;
	}
	public void setEditor(ShowUpableBillForm editor) {
		this.editor = editor;
	}
	public BaseServiceProxy getService() {
		return service;
	}
	public void setService(BaseServiceProxy service) {
		this.service = service;
	}
	
}
