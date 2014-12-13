package nc.ui.pubapp.uif2app.tangramlayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.md.MDBaseQueryFacade;
import nc.md.data.access.NCObject;
import nc.md.model.IAttribute;
import nc.md.model.IBean;
import nc.md.model.IBusinessEntity;
import nc.md.model.MetaDataException;
import nc.md.model.type.IEnumType;
import nc.md.util.MDUtil;
import nc.ui.pub.beans.ActionsBar.ActionsBarSeparator;
import nc.ui.pubapp.uif2app.actions.FileDocManageAction;
import nc.ui.pubapp.uif2app.actions.FirstLineAction;
import nc.ui.pubapp.uif2app.actions.LastLineAction;
import nc.ui.pubapp.uif2app.actions.NextLineAction;
import nc.ui.pubapp.uif2app.actions.PreLineAction;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class UECardLayoutToolbarPanel extends CardLayoutToolbarPanel {

  private static final long serialVersionUID = -252296303661979629L;

  private boolean needFileDocAction = true; //是否需要附件按钮，默认true, 不需要可在xml中注入该属性为false
  
  private FileDocManageAction docManageAction;

  private FirstLineAction firstLineAction;

  private LastLineAction lastLineAction;

  private NextLineAction nextLineAction;

  private PreLineAction preLineAction;

  private String note;
  
  public UECardLayoutToolbarPanel() {
    super();
    this.initDefaultRightActions();
  }

  @Override
  public void handleEvent(AppEvent event) {
	  super.handleEvent(event);
		if(note!=null){
			this.setRightText(note);
		}else{
			this.setRightText("");
		}
  }

  @Override
  public void setModel(AbstractUIAppModel model) {
	  this.setActions(this.getDefaultRightActions());
		super.setModel(model);
		this.firstLineAction.setModel((BillManageModel) model);
		this.preLineAction.setModel((BillManageModel) model);
		this.nextLineAction.setModel((BillManageModel) model);
		this.lastLineAction.setModel((BillManageModel) model);
		if(needFileDocAction){
			 this.docManageAction.setModel(model);
		}
  }

  // 工具栏上的按钮状态管理交给按钮自己管理，退化这个方法，把按钮状态管理逻辑拿掉
  @Override
  protected void onUIStateChanged(AppEvent event) {
    // super.onUIStateChanged(event);
    // if (this.getTextPnl().getShowMode() == TextPanel.ACTION_SHOW) {
    // if (this.getModel() instanceof IAppModelEx) {
    // AppUiState uiState = ((IAppModelEx) this.getModel()).getAppUiState();
    // if (uiState == AppUiState.ADD || uiState == AppUiState.EDIT) {
    // this.getTextPnl().setLblVisible(false);
    // }
    // else {
    // this.getTextPnl().setLblVisible(true);
    // }
    // }
    // }
  }

  private List<Action> getDefaultRightActions() {
	  List<Action> defaultActions = new ArrayList<Action>();
	    if(needFileDocAction){
	    	this.docManageAction = new FileDocManageAction();
		    this.docManageAction.setEnabled(false);
	    	defaultActions.add(this.docManageAction);
	    }
	    defaultActions.add(new ActionsBarSeparator());
	    defaultActions.add(this.firstLineAction);
	    defaultActions.add(this.preLineAction);
	    defaultActions.add(this.nextLineAction);
	    defaultActions.add(this.lastLineAction);
	    return defaultActions;
  }

  private void initDefaultRightActions() {
	  this.firstLineAction = new FirstLineAction();
	    this.firstLineAction.setEnabled(false);

	    this.preLineAction = new PreLineAction();
	    this.preLineAction.setEnabled(false);

	    this.nextLineAction = new NextLineAction();
	    this.nextLineAction.setEnabled(false);

	    this.lastLineAction = new LastLineAction();
	    this.lastLineAction.setEnabled(false);
  }
  
  /**
   * 在表头右侧菜单栏中增加除默认按钮外，其它扩展按钮
   * 
   * @param exActions 扩展的按钮
   */
  public void setRightExActions(List<Action> exActions) {
    List<Action> defaultRightActions = this.getDefaultRightActions();
    defaultRightActions.addAll(exActions);
    this.setActions(defaultRightActions);
  }

  public boolean isNeedFileDocAction() {
		return needFileDocAction;
	}

	public void setNeedFileDocAction(boolean needFileDocAction) {
		this.needFileDocAction = needFileDocAction;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	
	public String getNote() {
		return note;
	}
  
	
	@Override
	public void setTitleAction(Action action) {
		
		super.setTitleAction(action);
	}

	
	
}
