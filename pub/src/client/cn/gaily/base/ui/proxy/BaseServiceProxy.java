package cn.gaily.base.ui.proxy;

import nc.bs.framework.common.NCLocator;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.model.pagination.IPageQueryService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pubapp.bill.pagination.PaginationTransferObject;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.vo.uif2.LoginContext;
import cn.gaily.base.service.IBaseService;

/**
 * <p>Title: BaseServiceProxy</P>
 * <p>Description: ����������</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * @version 1.0
 */
public abstract class BaseServiceProxy<T extends AbstractBill> implements IDataOperationService, IQueryService, IPaginationQueryService, ISingleBillService<T>, IPageQueryService {

	/**
	 * ����VO
	 */
	private String billClazz = null;
	private LoginContext context;
	
	/**
	 * <p>�������ƣ�operateBill</p>
	 * <p>����������ɾ�����ݣ�֧������</p>
	 * @param bill Ҫɾ���ĵ���
	 * @return
	 * @throws Exception
	 */
	@Override
	public T operateBill(T bill) throws Exception {
		delete(new AbstractBill[]{ bill });
		return bill;
	}
	
	/**
	 * <p>�������ƣ�queryObjectByPks</p>
	 * <p>������������ҳ��ѯ����</p>
	 * @param pks ������������
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		Object[] rets = null;
		if(this.getBillClazz() == null){
			throw new IllegalArgumentException("���ڴ�������ע�뵱ǰ���ݵ�VO��");
		}
		try{
			rets = getMainService().queryBillByPK(pks, this.getBillClazz());
		}catch(BusinessException e){
			ExceptionUtils.wrappException(e);
		}
		return rets;
	}
	
	/**
	 * <p>�������ƣ�queryByPageQueryScheme</p>
	 * <p>������������ҳ��ѯ</p>
	 * @param queryScheme ��ѯ������Ϣ
	 * @param pageSize ҳ������
	 * @return
	 * @throws Exception
	 */
	@Override
	public PaginationTransferObject queryByPageQueryScheme(IQueryScheme queryScheme, int pageSize) throws Exception {
		return this.getMainService().queryByPageQueryScheme(queryScheme, pageSize, billClazz);
	}

	/**
	 * <p>�������ƣ�queryByQueryScheme</p>
	 * <p>�������������ݲ�ѯ������ѯ���ݣ���ʹ�÷�ҳ��</p>
	 * @param queryScheme
	 * @return
	 * @throws Exception
	 */
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme) throws Exception {
		return getMainService().queryByQueryScheme((Class<T>)Class.forName(billClazz), queryScheme);
	}

	/**
	 * <p>�������ƣ�insert</p>
	 * <p>�����������������ݣ�����BP�����־</p>
	 * @param value ��������
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public IBill[] insert(IBill[] value) throws BusinessException {
		IBill[] retVo = null;
		beforeInsert(value);
		retVo = getMainService().saveBills((AbstractBill[])value);
		afterInsert(retVo);
		return retVo;
	}
	
	/**
	 * <p>�������ƣ�update</p>
	 * <p>�������������µ��ݣ�����BP�����־</p>
	 * @param value ��������
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public IBill[] update(IBill[] value) throws BusinessException {
		IBill[] retVo = null;
		beforeUpdate(value);
		retVo = getMainService().updateBills((AbstractBill[])value);
		afterUpdate(retVo);
		return retVo;
	}
	
	/**
	 * <p>�������ƣ�delete</p>
	 * <p>����������ɾ�����ݣ�����BP�����־</p>
	 * @param value ��������
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public IBill[] delete(IBill[] value) throws BusinessException {
		beforeDelete(value);
		getMainService().deleteBills((AbstractBill[])value);
		afterDelete(value);
		return value;
	}
	
	/**
	 * <p>�������ƣ�insert</p>
	 * <p>�������������浥�ݣ����浥�ݵ�ͬʱҪ�󱣴浥�ݵĸ���������Ϣ��ҵ����־</p>
	 * @param files          ����������Ϣ 
	 * @return               �����ĵ��ݶ���
	 * @throws BusinessException
	 */
	public T[] insert(T[] clientVos) throws BusinessException {
		T[]  retVos = null ;
		beforeInsert(clientVos);
		retVos = getMainService().saveBills(clientVos);
		afterInsert(retVos);
		return retVos;
	}
	
	
	/**
	 * <p>�������ƣ�insertWhitOutBP</p>
	 * <p>�������������浥�ݣ��ʺϲ����ڵ������ͺ�BP�����</p>
	 * @param value
	 * @return
	 * @throws BusinessException
	 */
	public IBill[] insertWhitOutBP(IBill[] value) throws BusinessException {
		IBill[] retVo = null;
		beforeInsert(value);
		retVo = getMainService().save((AbstractBill[])value);
		afterInsert(retVo);
		return retVo;
	}
	
	/**
	 * <p>�������ƣ�updateWhitOutBP</p>
	 * <p>�������������µ��ݣ��ʺϲ����ڵ������ͺ�BP�����</p>
	 * @param value
	 * @return
	 * @throws BusinessException
	 */
	public IBill[] updateWhitOutBP(IBill[] value) throws BusinessException {
		IBill[] retVo = null;
		beforeUpdate(value);
		retVo = getMainService().update((AbstractBill[])value);
		afterUpdate(retVo);
		return retVo;
	}
	
	/**
	 * <p>�������ƣ�deleteWhitOutBP</p>
	 * <p>����������ɾ�����ݣ��ʺϲ����ڵ������ͺ�BP�����</p>
	 * @param value
	 * @return
	 * @throws BusinessException
	 */
	public IBill[] deleteWhitOutBP(IBill[] value) throws BusinessException {
		beforeDelete(value);
		getMainService().delete((AbstractBill[])value);
		afterDelete(value);
		return value;
	}
	
	/**
	 * <p>�������ƣ�queryPKSByWhere</p>
	 * <p>��������������SQL������ѯ����</p>
	 * @param clazz ��ѯ��VO��
	 * @param sqlWhere SQL��������where����
	 * @return
	 * @throws BusinessException
	 */
	public <T extends AbstractBill> String[] queryPKSByWhere(Class<T> clazz, String sqlWhere) throws BusinessException{
		return getMainService().queryPKSByWhere(clazz, sqlWhere);
	}
	
	/**
	 * <p>�������ƣ�querySuperVOByWhere</p>
	 * <p>�������������ݴ������where�ؼ��ֵ�sql������ѯSuperVO</p>
	 * @param clazz ��ѯ��VO��
	 * @param sqlWhere ������where �ؼ��ֺ���ʱ������Լ�����������Ψһ����Ҫ д�ľ���Ҫ��ѯ���ֶ�����vo�����Լ�dr=0����
	 * @return
	 * @throws BusinessException
	 */
	public <T extends SuperVO> T[] querySuperVOByWhere(Class<T> clazz, String sqlWhere) throws BusinessException{
		return getMainService().querySuperVOByWhere(clazz, sqlWhere);
	}
	
	/**
	 * <p>�������ƣ�queryAggVOByWhere</p>
	 * <p>��������������SQL������ѯAggVO</p>
	 * @param clazz ��ѯ��AggVO��
	 * @param sqlWhere ��where�ؼ��ֵ�����
	 * @return
	 * @throws BusinessException
	 */
	public <T extends AbstractBill> T[] queryAggVOByWhere(Class<T> clazz, String sqlWhere) throws BusinessException{
		return getMainService().queryAggVOByWhere(clazz, sqlWhere);
	}
	
	/**
	 * <p>�������ƣ�beforeInsert</p>
	 * <p>��������������ǰ�߼�</p>
	 * @param value
	 * @return
	 */
	protected IBill[] beforeInsert(IBill[] value){
		return value;
	}
	
	/**
	 * <p>�������ƣ�afterInsert</p>
	 * <p>����������������߼�</p>
	 * @param value
	 * @return
	 */
	protected IBill[] afterInsert(IBill[] value){
		return value;
	}
	
	/**
	 * <p>�������ƣ�beforeUpdate</p>
	 * <p>��������������ǰ�߼�</p>
	 * @param value
	 * @return
	 */
	protected IBill[] beforeUpdate(IBill[] value){
		return value;
	}
	
	/**
	 * <p>�������ƣ�afterUpdate</p>
	 * <p>�������������º��߼�</p>
	 * @param value
	 * @return
	 */
	protected IBill[] afterUpdate(IBill[] value){
		return value;
	}
	
	/**
	 * <p>�������ƣ�beforeDelete</p>
	 * <p>����������ɾ��ǰ�߼�</p>
	 * @param value
	 * @return
	 */
	protected IBill[] beforeDelete(IBill[] value){
		return value;
	}
	
	/**
	 * <p>�������ƣ�afterDelete</p>
	 * <p>����������ɾ�����߼�</p>
	 * @param value
	 * @return
	 */
	protected IBill[] afterDelete(IBill[] value){
		return value;
	}
	
	/**
	 * <p>�������ƣ�createBusinessLogs</p>
	 * <p>��������������ҵ����־������,�������Ҫ��¼��־,ֱ�ӷ���null;
	 * <p>			��Ҫ��¼��־��,���Ե���createBusinessLogs(int billLength, String billType, String businessType, List<String> notes),</p>
	 * <p>			Ҳ�������й���ҵ�����</P>
	 * @param value ��������
	 * @return
	 */
//	protected abstract MZBusinessLog[] createBusinessLogs(IBill[] value);
	
	/**
	 * <p>�������ƣ�createBusinessLogs</p>
	 * <p>��������������ҵ����־</p>
	 * @param billLength  ��������ĳ���
	 * @param billType  ��������
	 * @param businessType  ҵ�����ͣ��ο������AOBT0001
	 * @param notes  ҵ������¼�б��뵥��һһ��Ӧ
	 * @return
	 * @throws BusinessException 
	 */
//	protected MZBusinessLog[] createBusinessLogs(int billLength, String billType, String businessType, List<String> notes) throws BusinessException{
//		if(notes.size() != billLength){
//			throw new BusinessException("ҵ����־��¼��ϸ�����뵥�ݳ��Ȳ�ƥ��,�޷�����ҵ����־��");
//		}
//		MZBusinessLog[] logs = new MZBusinessLog[billLength];
//		for(int i=0; i<billLength; i++){
//			MZBusinessLog log = new MZBusinessLog();
//			log.setAolx0001(billType);
//			log.setAolx0004(new UFDate());
//			log.setAolx0005(businessType);
//			log.setAolx0006(notes.get(i));
//			logs[i] = log;
//		}
//		return logs;
//	}

	/**
	 * <p>�������ƣ�getMainService</p>
	 * <p>����������Զ�̵��û�������ӿ�</p>
	 * @return
	 */
	protected IBaseService getMainService(){
		return NCLocator.getInstance().lookup(IBaseService.class);
	}

	public String getBillClazz() {
		return billClazz;
	}

	public void setBillClazz(String billClazz) {
		this.billClazz = billClazz;
	}

	public LoginContext getContext() {
		return context;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}
	
}
