package cn.gaily.base.ui.util;

import java.awt.Color;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.vo.pub.BusinessException;

/**
 * <p>Title: TempletUtil</P>
 * <p>Description: ģ�幤��</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-12-12
 */
public class TempletUtil {


	/**
	 * <p>�������ƣ�setItemEditable</p>
	 * <p>���������������ֶ��Ƿ�ɱ༭</p>
	 * @param editor		���ؼ�
	 * @param item			��������
	 * @param isEditable	״̬	 �ɱ༭true;���ɱ༭false
	 * @author xiaoh
	 * @since  2014-12-12
	 * <p> history 2014-12-12 xiaoh  ����   <p>
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
	 * <p>�������ƣ�setItemsEditable</p>
	 * <p>�������������������ֶ��Ƿ�ɱ༭</p>
	 * @param editor		���ؼ�
	 * @param items			��������
	 * @param isEditable	״̬	 �ɱ༭true;���ɱ༭false
	 * @author xiaoh
	 * @since  2014-12-12
	 * <p> history 2014-12-12 xiaoh  ����   <p>
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
	 * <p>�������ƣ�setItemMustInput</p>
	 * <p>�������������ñ�����</p>
	 * @param editor		���ؼ�
	 * @param item			��������
	 * @param mustInput		״̬	 ����true;������false
	 * @throws BusinessException
	 * @author xiaoh
	 * @since  2014-12-12
	 * <p> history 2014-12-12 xiaoh  ����   <p>
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
	 * <p>�������ƣ�setItemsMustInput</p>
	 * <p>�����������������ñ�����</p>
	 * @param editor		���ؼ�
	 * @param items			��������
	 * @param mustInput		״̬	 ����true;������false
	 * @throws BusinessException
	 * @author xiaoh
	 * @since  2014-12-12
	 * <p> history 2014-12-12 xiaoh  ����   <p>
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
	 * <p>�������ƣ�getBillItem</p>
	 * <p>������������ȡ��������ݱ�ͷ������</p>
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
			throw new BusinessException("δ��ȡ��������,��������Ϊ��"+item+";�ڵ�Ϊ��"+panel.getNodeKey());
		}
		return billItem;
	}
	
}
