package nc.ui.uap.rbac.usermanage.model;

import java.awt.Dialog;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uap.sf.facility.SFServiceFacility;
import nc.bs.uif2.BusinessExceptionAdapter;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.itf.uap.rbac.userpassword.IUserPasswordChecker;
import nc.itf.uap.rbac.userpassword.IUserPasswordManage;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPasswordField;
import nc.ui.pub.hotkey.HotkeyUtil;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.IProductCode;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
import nc.vo.uap.rbac.userpassword.PasswordSecurityLevelFinder;
import nc.vo.uap.rbac.userpassword.PasswordSecurityLevelVO;
import nc.vo.uap.rbac.util.RbacUserPwdUtil;


@SuppressWarnings("serial")
public class PasswordDialog extends UIDialog {
	private javax.swing.JPanel ivjUIDialogContentPane = null;

	private UILabel ivjUILabel1 = null;
	
	private UILabel ivjUILabel2 = null;

	private UILabel ivjUILabel3 = null;

	private UIButton ivjCancelButton = null;

	private UIPasswordField ivjNewPassword = null;

	private UIPasswordField ivjNewPassword2 = null;

	private UICheckBox checkbox = null;
	
	private UIButton ivjOKButton = null;

	private Object[] vos = null;
	
	IvjEventHandler ivjEventHandler = new IvjEventHandler();


	class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == PasswordDialog.this.getOKButton())
				connEtoC1(e);
			if (e.getSource() == PasswordDialog.this.getCancelButton())
				connEtoC2(e);
		};
	};

	public PasswordDialog(Dialog parent) {
		super(parent);
		initialize();
	}

	public PasswordDialog(Dialog parent, String title) {
		super(parent, title);
		initialize();
	}
	
	public PasswordDialog(Dialog parent, String title,Object[] vos) {
		super(parent, title);
		initialize();
		this.vos  = vos;
	}

	public PasswordDialog(java.awt.Frame owner) {
		super(owner);
		initialize();
	}

	private boolean changeUserPassword() {
		nc.vo.sm.UserVO user = WorkbenchEnvironment.getInstance().getLoginUser();
		try {
			user = SFServiceFacility.getUserQueryService().findUserByCode(user.getUser_code(),
					WorkbenchEnvironment.getInstance().getDSName());// (NCAppletStub.getInstance().getParameter("USER_ID"));
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			MessageDialog.showErrorDlg(this,
					nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000019")/*
																							 * @res
																							 * "错误"
																							 */, nc.ui.ml.NCLangRes
							.getInstance().getStrByID("smcomm", "UPP1005-000098")/*
																				 * @res
																				 * "找不到您的用户ID，请联系系统管理员！"
																				 */);
			return false;
		}
		String expresslyPwd = new String(getNewPassword().getPassword()).trim();
		String stmp = WorkbenchEnvironment.getServerTime().toString();
		user.setPwdparam(stmp.substring(0, stmp.indexOf(" ")).trim());
		try {
			IUserPasswordManage passWordManageService = NCLocator.getInstance().lookup(IUserPasswordManage.class);
			passWordManageService.changeUserPassWord(user,expresslyPwd);
			// 将环境变量中的用户对象的密码也修改过来
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			MessageDialog.showErrorDlg(this,
					nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000019")/*
																							 * @res
																							 * "错误"
																							 */, nc.ui.ml.NCLangRes
							.getInstance().getStrByID("smcomm", "UPP1005-000095")/*
																				 * @res
																				 * "保存密码时出错，修改失败！"
																				 */);
			return false;
		}
		return true;
	}

	private void connEtoC1(java.awt.event.ActionEvent arg1) {
		try {
			this.onOKButtonClicked();
		} catch (BusinessException ivjExc) {
			throw new BusinessExceptionAdapter(ivjExc);
		}
	}

	private void connEtoC2(java.awt.event.ActionEvent arg1) {
		try {
			this.onCancelButtonClicked();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	private nc.ui.pub.beans.UIButton getCancelButton() {
		if (ivjCancelButton == null) {
			try {
				ivjCancelButton = new nc.ui.pub.beans.UIButton();
				ivjCancelButton.setName("CancelButton");
				HotkeyUtil.setHotkeyAndText(ivjCancelButton, 'C', nc.ui.ml.NCLangRes.getInstance().getStrByID(
						IProductCode.PRODUCTCODE_COMMON, "UC001-0000008"));/*
																			 * @res
																			 * "取消"
																			 */
				ivjCancelButton.setLocation(220, 154);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjCancelButton;
	}

	
	private nc.ui.pub.beans.UICheckBox getCheckBox(){
		if(checkbox==null){
			try{
				checkbox = new nc.ui.pub.beans.UICheckBox();
				checkbox.setName("checkbox");
				checkbox.setBounds(113, 22, 15, 15);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return checkbox;
	}
	
	private nc.ui.pub.beans.UIPasswordField getNewPassword() {
		if (ivjNewPassword == null) {
			try {
				ivjNewPassword = new nc.ui.pub.beans.UIPasswordField();
				ivjNewPassword.setAllowOtherCharacter(true);
				ivjNewPassword.setName("NewPassword");
				ivjNewPassword.setBounds(113, 56, 139, 20);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjNewPassword;
	}

	private nc.ui.pub.beans.UIPasswordField getNewPassword2() {
		if (ivjNewPassword2 == null) {
			try {
				ivjNewPassword2 = new nc.ui.pub.beans.UIPasswordField();
				ivjNewPassword2.setAllowOtherCharacter(true);
				ivjNewPassword2.setName("NewPassword2");
				ivjNewPassword2.setBounds(113, 90, 139, 20);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjNewPassword2;
	}

	private nc.ui.pub.beans.UIButton getOKButton() {
		if (ivjOKButton == null) {
			try {
				ivjOKButton = new UIButton();
				ivjOKButton.setName("OKButton");
				HotkeyUtil.setHotkeyAndText(ivjOKButton, 'O', nc.ui.ml.NCLangRes.getInstance().getStrByID(
						IProductCode.PRODUCTCODE_COMMON, "UC001-0000044"));/*
																			 * @res
																			 * "确定"
																			 */
				ivjOKButton.setLocation(125, 154);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjOKButton;
	}



	private javax.swing.JPanel getUIDialogContentPane() {
		if (ivjUIDialogContentPane == null) {
			try {
				ivjUIDialogContentPane = new javax.swing.JPanel();
				ivjUIDialogContentPane.setName("UIDialogContentPane");
				ivjUIDialogContentPane.setLayout(null);
				getUIDialogContentPane().add(getUILabel1(), getUILabel1().getName());
				getUIDialogContentPane().add(getUILabel2(), getUILabel2().getName());
				getUIDialogContentPane().add(getUILabel3(), getUILabel3().getName());
				getUIDialogContentPane().add(getOKButton(), getOKButton().getName());
				getUIDialogContentPane().add(getCancelButton(), getCancelButton().getName());
				getUIDialogContentPane().add(getCheckBox(), getCheckBox().getName());
				getUIDialogContentPane().add(getNewPassword(), getNewPassword().getName());
				getUIDialogContentPane().add(getNewPassword2(), getNewPassword2().getName());
				getUIDialogContentPane().add(getTipLable1(), getTipLable1().getName());
				getUIDialogContentPane().add(getTipLable2(), getTipLable2().getName());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIDialogContentPane;
	}

	private nc.ui.pub.beans.UILabel getUILabel1() {
		if (ivjUILabel1 == null) {
			try {
				ivjUILabel1 = new nc.ui.pub.beans.UILabel();
				ivjUILabel1.setName("UILabel1");
				ivjUILabel1.setText("是否修改所有：");
				ivjUILabel1.setBounds(28, 22, 90, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabel1;
	}
	
	private nc.ui.pub.beans.UILabel getUILabel2() {
		if (ivjUILabel2 == null) {
			try {
				ivjUILabel2 = new nc.ui.pub.beans.UILabel();
				ivjUILabel2.setName("UILabel2");
				ivjUILabel2.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000100")/*
																											 * @res
																											 * "新密码："
																											 */);
				ivjUILabel2.setBounds(28, 56, 90, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabel2;
	}

	private nc.ui.pub.beans.UILabel getUILabel3() {
		if (ivjUILabel3 == null) {
			try {
				ivjUILabel3 = new nc.ui.pub.beans.UILabel();
				ivjUILabel3.setName("UILabel3");
				ivjUILabel3.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000101")/*
																											 * @res
																											 * "确认新密码："
																											 */);
				ivjUILabel3.setBounds(28, 91, 90, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUILabel3;
	}

	private UILabel tipLable1;

	private UILabel getTipLable1() {
		if (tipLable1 == null) {
			tipLable1 = new UILabel();
			tipLable1.setBounds(258, 56, 120, 22);
		}
		return tipLable1;
	}

	private UILabel tipLable2;

	private UILabel getTipLable2() {
		if (tipLable2 == null) {
			tipLable2 = new UILabel();
			tipLable2.setBounds(258, 90, 120, 22);
		}
		return tipLable2;
	}

	private void handleException(java.lang.Throwable exception) {

		Logger.error("error", exception);
	}

	private void initConnections() throws java.lang.Exception {
		getOKButton().addActionListener(ivjEventHandler);
		getCancelButton().addActionListener(ivjEventHandler);
	}

	private void initialize() {
		try {
			setName("PasswordDialog");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(308, 189);
			setTitle(nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000102")/*
																							 * @res
																							 * "修改用户密码"
																							 */);
			setContentPane(getUIDialogContentPane());
			initConnections();
			this.vos  = null;
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	private void onCancelButtonClicked() {

		close();
	}

	private void onOKButtonClicked() throws BusinessException {
		String password = new String(getNewPassword().getPassword()).trim();
		String password2 = new String(getNewPassword2().getPassword()).trim();
		boolean isSelect = getCheckBox().isSelected();
		
		UserVO loginUser = WorkbenchEnvironment.getInstance().getLoginUser();
		IUserPasswordManage userPWDManage = NCLocator.getInstance().lookup(IUserPasswordManage.class);
		IUserManageQuery userQuery = NCLocator.getInstance().lookup(IUserManageQuery.class);
		Object[] objs = null;
		if(isSelect){
			objs = userQuery.queryUserByClause(" user_type=1 ");
		}else{
			objs = getVos();
		}
		if(objs==null||objs.length<=0){
			MessageDialog.showWarningDlg(null, "提示", "请选择要操作的用户");
			return;
		}
		UserVO user = null;
		for(int i=0;i<objs.length;i++){
			user = (UserVO) objs[i];
			PasswordSecurityLevelVO pwdLevel = PasswordSecurityLevelFinder.getPWDLV(user);
			if (!verifyPassord(password, password2, pwdLevel, user)){
				return;
			}
			userPWDManage.changeUserPassWord(user, password);
			
			if (user.equals(loginUser)) {
				changeUserPassword();
				WorkbenchEnvironment.getInstance().getLoginUser().setUser_password(RbacUserPwdUtil.getEncodedPassword(user,password));
				close();
			}
		}
		MessageDialog.showHintDlg(null, "提示", "修改完成");
		this.close();
	}

	private boolean verifyPassord(String sPassword, String sCheckPassword, PasswordSecurityLevelVO pwdLevel,
			nc.vo.sm.UserVO user) {

		if (StringUtil.isEmpty(sPassword)) {
			MessageDialog.showWarningDlg(this,
					nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000070")/*
																							 * @res
																							 * "警告"
																							 */, nc.ui.ml.NCLangRes
							.getInstance().getStrByID("smcomm", "UPP1005-000103")/*
																				 * @res
																				 * "密码不允许为空，请重新输入！"
																				 */);
			getNewPassword().setText("");
			getNewPassword2().setText("");
			return false;
		}
		
		if (!sPassword.equals(sCheckPassword)) {
			nc.ui.pub.beans.MessageDialog.showErrorDlg(this, nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm",
					"UPP1005-000104")/* @res "用友软件" */, nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm",
					"UPP1005-000107")/* @res "密码不一致" */);
			return false;
		}
		if (user == null) {
			MessageDialog.showErrorDlg(this,
					nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000019")/*
																							 * @res
																							 * "错误"
																							 */, nc.ui.ml.NCLangRes
							.getInstance().getStrByID("smcomm", "UPP1005-000098")/*
																				 * @res
																				 * "找不到您的用户ID，请联系系统管理员！"
																				 */);
			return false;
		}
		if (pwdLevel == null) {

		} else {
			IUserPasswordChecker upchecher = (IUserPasswordChecker) NCLocator.getInstance().lookup(
					IUserPasswordChecker.class.getName());
			try {
				upchecher.checkNewpassword(user, sPassword, pwdLevel, WorkbenchEnvironment.getInstance().getUserType());
			} catch (BusinessException be) {
				MessageDialog.showErrorDlg(this, nc.ui.ml.NCLangRes.getInstance()
						.getStrByID("smcomm", "UPP1005-000019")/* @res "错误" */, be.getMessage());
				return false;
			}
		}
		return true;
	}
	
	public Object[] getVos() {
		return vos;
	}
	
}
