package cn.gaily.base.ui.util;

import java.awt.Color;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.vo.pub.BusinessException;

/**
 * <p>Title: TempletUtil</P>
 * <p>Description: 模板工具</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-12-12
 */
public class TempletUtil {


	/**
	 * <p>方法名称：setItemEditable</p>
	 * <p>方法描述：控制字段是否可编辑</p>
	 * @param editor		表单控件
	 * @param item			单据名称
	 * @param isEditable	状态	 可编辑true;不可编辑false
	 * @author xiaoh
	 * @since  2014-12-12
	 * <p> history 2014-12-12 xiaoh  创建   <p>
	 */
	public static void setItemEditable(BillForm editor, String item, boolean isEditable) throws BusinessException{
		if(editor==null||item==null||"".equals(item.trim())){
			return ;
		}
		BillCardPanel panel = editor.getBillCardPanel();
		BillItem billItem = getBillItem(panel, item);
		billItem.setEdit(isEditable);
	}
	
	/**
	 * <p>方法名称：setItemsEditable</p>
	 * <p>方法描述：批量控制字段是否可编辑</p>
	 * @param editor		表单控件
	 * @param items			单据名称
	 * @param isEditable	状态	 可编辑true;不可编辑false
	 * @author xiaoh
	 * @since  2014-12-12
	 * <p> history 2014-12-12 xiaoh  创建   <p>
	 */
	public static void setItemsEditable(BillForm editor, String[] items, boolean isEditable) throws BusinessException{
		if(editor==null||items==null||items.length<=0){
			return ;
		}
		BillCardPanel panel = editor.getBillCardPanel();
		BillItem billItem = null;
		for (String item : items) {
			billItem = getBillItem(panel, item);
			billItem.setEdit(isEditable);
		}
	}

	
	/**
	 * <p>方法名称：setItemMustInput</p>
	 * <p>方法描述：设置必输项</p>
	 * @param editor		表单控件
	 * @param item			单据名称
	 * @param mustInput		状态	 必输true;不必输false
	 * @throws BusinessException
	 * @author xiaoh
	 * @since  2014-12-12
	 * <p> history 2014-12-12 xiaoh  创建   <p>
	 */
	public static void setItemMustInput(BillForm editor, String item, boolean mustInput) throws BusinessException{
		if(editor==null||item==null||"".equals(item.trim())){
			return ;
		}
		BillCardPanel panel = editor.getBillCardPanel();
		BillItem billItem = getBillItem(panel, item);
		billItem.setEnabled(mustInput);
		billItem.setNull(mustInput);
		if(mustInput){
			billItem.getCaptionLabel().setForeground(Color.RED);
		}else{
			billItem.getCaptionLabel().setForeground(Color.BLACK);
		}
	}
	
	/**
	 * <p>方法名称：setItemsMustInput</p>
	 * <p>方法描述：批量设置必输项</p>
	 * @param editor		表单控件
	 * @param items			单据名称
	 * @param mustInput		状态	 必输true;不必输false
	 * @throws BusinessException
	 * @author xiaoh
	 * @since  2014-12-12
	 * <p> history 2014-12-12 xiaoh  创建   <p>
	 */
	public static void setItemsMustInput(BillForm editor, String[] items, boolean mustInput) throws BusinessException{
		if(editor==null||items==null||items.length<=0){
			return;
		}
		BillCardPanel panel = editor.getBillCardPanel();
		BillItem billItem = null;
		for (String item : items) {
			billItem = getBillItem(panel, item);
			billItem.setEnabled(mustInput);
			billItem.setNull(mustInput);
			if(mustInput){
				billItem.getCaptionLabel().setForeground(Color.RED);
			}else{
				billItem.getCaptionLabel().setForeground(Color.BLACK);
			}
			
		}
	}
	
	/**
	 * <p>方法名称：setItemVisiable</p>
	 * <p>方法描述：设置表头表体项不显示</p>
	 * @param editor	表单控件
	 * @param items		单据名称
	 * @param visiable	状态	 显示true;不显示false
	 * @throws BusinessException
	 * @author xiaoh
	 * @since  2014-12-18
	 * <p> history 2014-12-18 xiaoh  创建   <p>
	 */
	public static void setItemVisiable(BillForm editor, String[] items, boolean visiable) throws BusinessException{
		if(editor==null||items==null||items.length<=0){
			return;
		}
		BillCardPanel panel = editor.getBillCardPanel();
		BillData bd = editor.getBillCardPanel().getBillData();
		for (String item : items) {
			getBillItem(bd, item).setShow(visiable);
		}
		panel.setBillData(bd);
	}
	
	/**
	 * <p>方法名称：setItemVisiable</p>
	 * <p>方法描述：设置表头表体项不显示</p>
	 * @param editor	表单控件
	 * @param item		单据名称
	 * @param visiable	状态	 显示true;不显示false
	 * @throws BusinessException
	 * @author xiaoh
	 * @since  2014-12-18
	 * <p> history 2014-12-18 xiaoh  创建   <p>
	 */
	public static void setItemVisiable(BillForm editor, String item, boolean visiable) throws BusinessException{
		if(editor==null||item==null){
			return;
		}
		BillCardPanel panel = editor.getBillCardPanel();
		BillData bd = editor.getBillCardPanel().getBillData();
		getBillItem(bd, item).setShow(visiable);
		panel.setBillData(bd);
	}
	
	/**
	 * <p>方法名称：setItemName</p>
	 * <p>方法描述：设置列显示名</p>
	 * @param editor	表单控件
	 * @param item		单据名称
	 * @param name		名称
	 * @throws BusinessException
	 * @author xiaoh
	 * @since  2015-4-1
	 * <p> history 2015-4-1 xiaoh  创建   <p>
	 */
	public static void setItemName(BillForm editor, String item, String name) throws BusinessException{
		if(editor==null||item==null){
			return;
		}
		BillCardPanel panel = editor.getBillCardPanel();
		BillData bd = editor.getBillCardPanel().getBillData();
		getBillItem(bd, item).setName(name);
		panel.setBillData(bd);
	}
	
	/**
	 * <p>方法名称：getBillItem</p>
	 * <p>方法描述：通过单据模板数据控制获取单据项，兼容表头表体项</p>
	 * @param bd
	 * @param item
	 * @author xiaoh
	 * @since  2014-12-12
	 */
	private static BillItem getBillItem(BillData bd, String item) throws BusinessException{
		if(bd==null||item==null){
			return null;
		}
		BillItem billItem = bd.getHeadItem(item);
		if(billItem==null){
			billItem = bd.getBodyItem(item);
		}
		if(billItem==null){
			throw new BusinessException("未获取到单据项,单据项名为："+item);
		}
		return billItem;
	}
	
	/**
	 * <p>方法名称：getBillItem</p>
	 * <p>方法描述：获取单据项，兼容表头表体项</p>
	 * @param panel
	 * @param item
	 * @author xiaoh
	 * @since  2014-12-12
	 */
	private static BillItem getBillItem(BillCardPanel panel, String item) throws BusinessException{
		if(panel==null||item==null){
			return null;
		}
		BillItem billItem = panel.getHeadItem(item);
		if(billItem==null){
			billItem = panel.getBodyItem(item);
		}
		if(billItem==null){
			throw new BusinessException("未获取到单据项,单据项名为："+item+";节点为："+panel.getNodeKey());
		}
		return billItem;
	}
	
	
	
	
}
