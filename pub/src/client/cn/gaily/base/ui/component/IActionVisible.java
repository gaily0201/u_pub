package cn.gaily.base.ui.component;

/**
 * <p>Title: IActionVisible</P>
 * <p>Description: 控制动作(按钮)可见状态的接口，NCAction通过实现该接口，来控制自身可见状态。
 * 					该接口需配合BaseStandAloneToftPanelActionContainer一起使用。
 * </p>
 * @version 1.0
 * @since 2014-4-15
 */
public interface IActionVisible {

	/**
	 * <p>方法名称：isActionVisible</p>
	 * <p>方法描述：按钮是否可见，用法类似于actionEnable</p>
	 * @return
	 * @since 2014-4-15
	 */
	public boolean isActionVisible();

}
