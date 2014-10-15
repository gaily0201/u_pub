package cn.gaily.pub.itf;

import java.util.List;
import java.util.Map;

import nc.vo.pub.BusinessException;

/**
 * <p>Title: IBaseService</P>
 * <p>Description: �����ķ�����</p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-9-1
 */
public interface IBaseService {

	/**
	 * <p>�������ƣ�generatePks</p>
	 * <p>����������������ȡPk</p>
	 * @param count ����
	 * @return
	 * @author xiaoh
	 * @since  2014-10-10
	 * <p> history 2014-10-10 xiaoh  ����   <p>
	 */
	public List<String> generatePks(int count);
	
	
	/**
	 * <p>�������ƣ�getPkorgByName</p>
	 * <p>�������������ݵ�λ���ƻ�ȡPk_org��ֵ</p>
	 * @param name	 ��λ����
	 * @param needRefresh  �Ƿ���Ҫ���¹���
	 * @author xiaoh
	 * @since  2014-9-14
	 * <p> history 2014-9-14 xiaoh  ����   <p>
	 */
	public String getPkorgByName(String name, boolean needRefresh) throws BusinessException;


	/**
	 * <p>�������ƣ�getAllNamePkorgMap</p>
	 * <p>������������ORG_ORGS���л�ȡ���е�[����-pk_org]��Ӧ��ϵ</p>
	 * @return
	 * @author xiaoh
	 * @since  2014-10-14
	 * <p> history 2014-10-14 xiaoh  ����   <p>
	 */
	public Map<String, String> getAllNamePkorgMap();

}
