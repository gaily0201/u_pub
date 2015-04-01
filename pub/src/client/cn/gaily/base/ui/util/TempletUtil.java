package cn.gaily.base.ui.util;

import java.awt.Color;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
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
	 * <p>�������ƣ�setItemVisiable</p>
	 * <p>�������������ñ�ͷ�������ʾ</p>
	 * @param editor	���ؼ�
	 * @param items		��������
	 * @param visiable	״̬	 ��ʾtrue;����ʾfalse
	 * @throws BusinessException
	 * @author xiaoh
	 * @since  2014-12-18
	 * <p> history 2014-12-18 xiaoh  ����   <p>
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
	 * <p>�������ƣ�setItemVisiable</p>
	 * <p>�������������ñ�ͷ�������ʾ</p>
	 * @param editor	���ؼ�
	 * @param item		��������
	 * @param visiable	״̬	 ��ʾtrue;����ʾfalse
	 * @throws BusinessException
	 * @author xiaoh
	 * @since  2014-12-18
	 * <p> history 2014-12-18 xiaoh  ����   <p>
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
	 * <p>�������ƣ�setItemName</p>
	 * <p>������������������ʾ��</p>
	 * @param editor	���ؼ�
	 * @param item		��������
	 * @param name		����
	 * @throws BusinessException
	 * @author xiaoh
	 * @since  2015-4-1
	 * <p> history 2015-4-1 xiaoh  ����   <p>
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
	 * <p>�������ƣ�getBillItem</p>
	 * <p>����������ͨ������ģ�����ݿ��ƻ�ȡ��������ݱ�ͷ������</p>
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
			throw new BusinessException("δ��ȡ��������,��������Ϊ��"+item);
		}
		return billItem;
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
