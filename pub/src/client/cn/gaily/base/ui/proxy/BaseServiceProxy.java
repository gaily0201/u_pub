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
 * <p>Description: 基础代理类</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * @version 1.0
 */
public abstract class BaseServiceProxy<T extends AbstractBill> implements IDataOperationService, IQueryService, IPaginationQueryService, ISingleBillService<T>, IPageQueryService {

	/**
	 * 单据VO
	 */
	private String billClazz = null;
	private LoginContext context;
	
	/**
	 * <p>方法名称：operateBill</p>
	 * <p>方法描述：删除单据，支持批量</p>
	 * @param bill 要删除的单据
	 * @return
	 * @throws Exception
	 */
	@Override
	public T operateBill(T bill) throws Exception {
		delete(new AbstractBill[]{ bill });
		return bill;
	}
	
	/**
	 * <p>方法名称：queryObjectByPks</p>
	 * <p>方法描述：分页查询单据</p>
	 * @param pks 单据主键数组
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		Object[] rets = null;
		if(this.getBillClazz() == null){
			throw new IllegalArgumentException("请在代理类中注入当前单据的VO类");
		}
		try{
			rets = getMainService().queryBillByPK(pks, this.getBillClazz());
		}catch(BusinessException e){
			ExceptionUtils.wrappException(e);
		}
		return rets;
	}
	
	/**
	 * <p>方法名称：queryByPageQueryScheme</p>
	 * <p>方法描述：分页查询</p>
	 * @param queryScheme 查询条件信息
	 * @param pageSize 页单据数
	 * @return
	 * @throws Exception
	 */
	@Override
	public PaginationTransferObject queryByPageQueryScheme(IQueryScheme queryScheme, int pageSize) throws Exception {
		return this.getMainService().queryByPageQueryScheme(queryScheme, pageSize, billClazz);
	}

	/**
	 * <p>方法名称：queryByQueryScheme</p>
	 * <p>方法描述：根据查询方案查询单据（不使用分页）</p>
	 * @param queryScheme
	 * @return
	 * @throws Exception
	 */
	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme) throws Exception {
		return getMainService().queryByQueryScheme((Class<T>)Class.forName(billClazz), queryScheme);
	}

	/**
	 * <p>方法名称：insert</p>
	 * <p>方法描述：新增单据，包含BP层和日志</p>
	 * @param value 单据数组
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
	 * <p>方法名称：update</p>
	 * <p>方法描述：更新单据，包含BP层和日志</p>
	 * @param value 单据数组
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
	 * <p>方法名称：delete</p>
	 * <p>方法描述：删除单据，包含BP层和日志</p>
	 * @param value 单据数组
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
	 * <p>方法名称：insert</p>
	 * <p>方法描述：保存单据，保存单据的同时要求保存单据的附件描述信息和业务日志</p>
	 * @param files          附件描述信息 
	 * @return               保存后的单据对象
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
	 * <p>方法名称：insertWhitOutBP</p>
	 * <p>方法描述：保存单据，适合不存在单据类型和BP的情况</p>
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
	 * <p>方法名称：updateWhitOutBP</p>
	 * <p>方法描述：更新单据，适合不存在单据类型和BP的情况</p>
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
	 * <p>方法名称：deleteWhitOutBP</p>
	 * <p>方法描述：删除单据，适合不存在单据类型和BP的情况</p>
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
	 * <p>方法名称：queryPKSByWhere</p>
	 * <p>方法描述：根据SQL条件查询主键</p>
	 * @param clazz 查询的VO类
	 * @param sqlWhere SQL条件，含where条件
	 * @return
	 * @throws BusinessException
	 */
	public <T extends AbstractBill> String[] queryPKSByWhere(Class<T> clazz, String sqlWhere) throws BusinessException{
		return getMainService().queryPKSByWhere(clazz, sqlWhere);
	}
	
	/**
	 * <p>方法名称：querySuperVOByWhere</p>
	 * <p>方法描述：根据传入的有where关键字的sql条件查询SuperVO</p>
	 * @param clazz 查询的VO类
	 * @param sqlWhere 必须有where 关键字和临时表表名以及其他条件，唯一不需要 写的就是要查询的字段名、vo表名以及dr=0条件
	 * @return
	 * @throws BusinessException
	 */
	public <T extends SuperVO> T[] querySuperVOByWhere(Class<T> clazz, String sqlWhere) throws BusinessException{
		return getMainService().querySuperVOByWhere(clazz, sqlWhere);
	}
	
	/**
	 * <p>方法名称：queryAggVOByWhere</p>
	 * <p>方法描述：根据SQL条件查询AggVO</p>
	 * @param clazz 查询的AggVO类
	 * @param sqlWhere 含where关键字的条件
	 * @return
	 * @throws BusinessException
	 */
	public <T extends AbstractBill> T[] queryAggVOByWhere(Class<T> clazz, String sqlWhere) throws BusinessException{
		return getMainService().queryAggVOByWhere(clazz, sqlWhere);
	}
	
	/**
	 * <p>方法名称：beforeInsert</p>
	 * <p>方法描述：保存前逻辑</p>
	 * @param value
	 * @return
	 */
	protected IBill[] beforeInsert(IBill[] value){
		return value;
	}
	
	/**
	 * <p>方法名称：afterInsert</p>
	 * <p>方法描述：保存后逻辑</p>
	 * @param value
	 * @return
	 */
	protected IBill[] afterInsert(IBill[] value){
		return value;
	}
	
	/**
	 * <p>方法名称：beforeUpdate</p>
	 * <p>方法描述：更新前逻辑</p>
	 * @param value
	 * @return
	 */
	protected IBill[] beforeUpdate(IBill[] value){
		return value;
	}
	
	/**
	 * <p>方法名称：afterUpdate</p>
	 * <p>方法描述：更新后逻辑</p>
	 * @param value
	 * @return
	 */
	protected IBill[] afterUpdate(IBill[] value){
		return value;
	}
	
	/**
	 * <p>方法名称：beforeDelete</p>
	 * <p>方法描述：删除前逻辑</p>
	 * @param value
	 * @return
	 */
	protected IBill[] beforeDelete(IBill[] value){
		return value;
	}
	
	/**
	 * <p>方法名称：afterDelete</p>
	 * <p>方法描述：删除后逻辑</p>
	 * @param value
	 * @return
	 */
	protected IBill[] afterDelete(IBill[] value){
		return value;
	}
	
	/**
	 * <p>方法名称：createBusinessLogs</p>
	 * <p>方法描述：生产业务日志抽象类,如果不需要记录日志,直接返回null;
	 * <p>			需要记录日志的,可以调用createBusinessLogs(int billLength, String billType, String businessType, List<String> notes),</p>
	 * <p>			也可以自行构造业务对象</P>
	 * @param value 单据数组
	 * @return
	 */
//	protected abstract MZBusinessLog[] createBusinessLogs(IBill[] value);
	
	/**
	 * <p>方法名称：createBusinessLogs</p>
	 * <p>方法描述：生成业务日志</p>
	 * @param billLength  单据数组的长度
	 * @param billType  单据类型
	 * @param businessType  业务类型，参考代码表AOBT0001
	 * @param notes  业务变更记录列表，与单据一一对应
	 * @return
	 * @throws BusinessException 
	 */
//	protected MZBusinessLog[] createBusinessLogs(int billLength, String billType, String businessType, List<String> notes) throws BusinessException{
//		if(notes.size() != billLength){
//			throw new BusinessException("业务日志记录明细个数与单据长度不匹配,无法生成业务日志！");
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
	 * <p>方法名称：getMainService</p>
	 * <p>方法描述：远程调用基础服务接口</p>
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
