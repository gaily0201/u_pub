package cn.gaily.base.ui.component;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.StandAloneToftPanelActionContainer;
import nc.ui.uif2.components.ITabbedPaneAwareComponent;
import nc.ui.uif2.model.AppEventConst;

/**
 * <p>Title: BaseStandAloneToftPanelActionContainer </P>
 * <p>Description: 按钮容器，增加按钮可见状态的控制 </p>
 * @version 1.0
 * @since 2014-4-15
 */
public class BaseStandAloneToftPanelActionContainer extends StandAloneToftPanelActionContainer {

	/**
	 * 是否响应所有事件，默认为false，只在界面加载及界面编辑态变化时，才去更新按钮可见状态。
	 */
	private boolean handleAllEvent = false;

	private List<ActionDesc> hideActions = new ArrayList<ActionDesc>();

	private List<ActionDesc> hideEditActions = new ArrayList<ActionDesc>();

	public void setHandleAllEvent(boolean handleAllEvent) {
		this.handleAllEvent = handleAllEvent;
	}

	public BaseStandAloneToftPanelActionContainer(
			ITabbedPaneAwareComponent tabbedPaneAwareComponent) {
		super(tabbedPaneAwareComponent);
	}

	@Override
	public List<Action> getActions() {
		List<Action> actions = super.getActions();
		List<ActionDesc> currentHideActions = null;
		if (getModel() != null
				&& (getModel().getUiState() == UIState.ADD || getModel()
						.getUiState() == UIState.EDIT)) {
			currentHideActions = this.hideEditActions;
		} else {
			currentHideActions = this.hideActions;
		}

		for (int i = 0; i < currentHideActions.size(); i++) {
			ActionDesc acitonDesc = currentHideActions.get(i);
			int index = acitonDesc.getIndex();
			actions.add(actions.size() < index ? actions.size() : index,
					acitonDesc.getAction());
		}
		currentHideActions.clear();
		for (int i = 0; i < actions.size(); i++) {
			Action action = actions.get(i);
			if (action instanceof IActionVisible) {
				IActionVisible acitonVisible = (IActionVisible) action;
				if (!acitonVisible.isActionVisible()) {
					currentHideActions.add(new ActionDesc(action, i));
				}
			}
		}
		for (int i = 0; i < currentHideActions.size(); i++) {
			ActionDesc acitonDesc = currentHideActions.get(i);
			actions.remove(acitonDesc.getAction());
		}
		return actions;
	}

	@Override
	public void handleEvent(AppEvent event) {
		if (handleAllEvent) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					BaseStandAloneToftPanelActionContainer.super
							.handleEvent(new AppEvent(
									AppEventConst.UISTATE_CHANGED));
				}
			});
		} else {
			super.handleEvent(event);
		}
	}

	class ActionDesc {
		private Action action;
		private int index;

		public ActionDesc(Action action, int index) {
			super();
			this.action = action;
			this.index = index;
		}

		public Action getAction() {
			return action;
		}

		public void setAction(Action action) {
			this.action = action;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

	}
}
