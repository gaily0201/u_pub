package nc.ui.uap.rbac.usermanage.actions;

import java.awt.event.ActionEvent;

import nc.ui.pub.beans.MessageDialog;
import nc.ui.uap.rbac.usermanage.model.PasswordDialog;
import nc.ui.uap.rbac.usermanage.model.UserManageModel;
import nc.ui.uap.rbac.usermanage.view.UserEditorForm;
import nc.ui.uif2.NCAction;
import nc.vo.sm.UserVO;

public class ModifyAction extends NCAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8790051556718152891L;

	
	private UserManageModel model;
	
	public ModifyAction(){
		setBtnName("ÅúÁ¿¸ÄÃÜ");
		setCode("modifyAction");
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object[] objs =  model.getSelectedOperaDatas();
		PasswordDialog dialog = new PasswordDialog(null,"",objs);
		dialog.showModal();
	}
	
	
	public UserManageModel getModel() {
		return model;
	}
	public void setModel(UserManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}
	
//	@Override
//	public boolean isActionEnable() {
//		Object[] objs =  model.getSelectedOperaDatas();
//		if(objs.length<=0){
//			return false;
//		}
//		return true;
//	}
	
	
}
