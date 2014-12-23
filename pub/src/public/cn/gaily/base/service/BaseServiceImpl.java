package cn.gaily.base.service;

import java.util.List;

import nc.impl.pubapp.pattern.data.bill.BillDelete;
import nc.impl.pubapp.pattern.data.bill.BillInsert;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.BillQuery;
import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.impl.pubapp.pattern.data.vo.VODelete;
import nc.impl.pubapp.pattern.data.vo.VOInsert;
import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.impl.pubapp.pattern.data.vo.VOUpdate;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.impl.pubapp.pattern.rule.ICompareRule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.impl.pubapp.pub.smart.SmartServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.IAttributeMeta;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.ITableMeta;
import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.pubapp.bill.pagination.PaginationTransferObject;
import nc.vo.pubapp.bill.pagination.util.PaginationUtils;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.uif2.LoginContext;
import nc.vo.wfengine.core.application.IWorkflowGadget;
import nc.vo.wfengine.core.application.WfGadgetContext;
import cn.gaily.pub.util.CommonUtils;

/**
 * <p>Title: BaseServiceImpl</P>
 * <p>Description: 基础服务实现</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * @version 1.0
 */
public class BaseServiceImpl extends SmartServiceImpl implements IBaseService, IWorkflowGadget  {
	
	@Override
	public <T extends SuperVO> T[] save(T[] vos) throws BusinessException{
		if (vos != null && vos.length > 0) {
			VOInsert<T> bo = new VOInsert<T>();
			return bo.insert(vos);
		}
		return null;
	}
	
	@Override
	public <T extends SuperVO> T[] update(T[] vos) throws BusinessException {
		if (vos != null && vos.length > 0) {
			VOUpdate<T> bo = new VOUpdate<T>();
			return bo.update(vos);
		}
		return null;
	}

	@Override
	public <T extends SuperVO> void delete(T[] vos) throws BusinessException {
		if (vos != null && vos.length > 0) {
			VODelete<T> bo = new VODelete<T>();
			bo.delete(vos);
		}
	}

	@Override
	public <T extends AbstractBill> T[] save(T[] vos) throws BusinessException{
		try{
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<T> transferTool = new BillTransferTool<T>(vos);
			T[] mergedVO = transferTool.getClientFullInfoBill();
			// 调用BillInsert
			BillInsert<T> bo = new BillInsert<T>();
			T[] retvos = bo.insert(mergedVO);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		}catch(Exception e){
			ExceptionUtils.marsh(e);
		}
		return null;
	}
	
	@Override
	public <T extends AbstractBill> T[] update(T[] vos) throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<T> transTool = new BillTransferTool<T>(vos);
			// 补全前台VO
			T[] fullBills = transTool.getClientFullInfoBill();
			// 获得修改前vo
			T[] originBills = transTool.getOriginBills();
			BillUpdate<T> bo = new BillUpdate<T>();
			T[] retBills = bo.update(fullBills, originBills);
			// 构造返回数据
			retBills = transTool.getBillForToClient(retBills);
			return retBills;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}
	
	@Override
	public <T extends AbstractBill> void delete(T[] vos) throws BusinessException {
		try{
			// 加锁 比较ts
			BillTransferTool<T> transferTool = new BillTransferTool<T>(vos);
			T[] fullBills = transferTool.getClientFullInfoBill();
			BillDelete<T> bo = new BillDelete<T>();
			bo.delete(fullBills);
		}catch(Exception e){
			ExceptionUtils.marsh(e);
		}
	}
	
	@Override
	public <T extends AbstractBill> T[] saveBills(T[] bills)  throws BusinessException{
		try{
			
			T[] retvos = null ;
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<T> transferTool = new BillTransferTool<T>(bills);
			T[] mergedVO = transferTool.getClientFullInfoBill();
			
			//执行前规则
//			AroundProcesser<T>	processor = this.buildAddProcessor(bills);
//			if(processor != null){
//				retvos = processor.before(mergedVO);
//			}
			// 调用BillInsert
			BillInsert<T> bo = new BillInsert<T>();
			retvos = bo.insert(mergedVO);
			// 执行后规则
//			if(processor != null){
//				retvos = processor.after(retvos);
//			}
			
			//保存业务日志
//			saveLogs(retvos, logs, false);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		}catch(Exception e){
			ExceptionUtils.marsh(e);
		}
		return null;
	}
	
	@Override
	public <T extends AbstractBill> T[] updateBills(T[] bills) throws BusinessException{
		try {
			
			T[] retBills = null ;
			
			// 加锁 + 检查ts
			BillTransferTool<T> transTool = new BillTransferTool<T>(bills);
			// 补全前台VO
			T[] fullBills = transTool.getClientFullInfoBill();
			// 获得修改前vo
			T[] originBills = transTool.getOriginBills();
			
			//执行前规则
//			CompareAroundProcesser<T>	processor = this.buildUpdateProcessor(bills);
//			if(processor != null){
//				retBills = processor.before(fullBills,originBills);
//			}
			BillUpdate<T> bo = new BillUpdate<T>();
			retBills = bo.update(fullBills, originBills);
			// 执行后规则
//			if(processor != null){
//				retBills = processor.after(fullBills,originBills);
//			}
			
			// 构造返回数据
			retBills = transTool.getBillForToClient(retBills);
			return retBills;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	@Override
	public <T extends AbstractBill> void deleteBills(T[] bills) throws BusinessException{
		try{
			
			T[]  retVos = null; 
			// 加锁 比较ts
			BillTransferTool<T> transferTool = new BillTransferTool<T>(bills);
			T[] fullBills = transferTool.getClientFullInfoBill();
			
			// 执行前规则
//			AroundProcesser<T> processor = this.buildDeleteProcessor(bills);
//			if(processor != null){
//				retVos  = processor.before(fullBills);
//			}
			BillDelete<T> bo = new BillDelete<T>();
			bo.delete(fullBills);
			
			// 执行后规则
//			if(processor != null){
//				retVos = processor.after(retVos);
//			}
			//保存业务日志
//			saveLogs(bills, logs, false);
		}catch(Exception e){
			ExceptionUtils.marsh(e);
		}
	}
	
//	@Override
//	public <T extends AbstractBill> T[] saveBills(T[] bills, MzAsFile[] files, MZBusinessLog[] logs) throws BusinessException {
//		T[] returnVo = this.saveBills(bills, logs);
//		returnVo = this.saveOrUpdateFiles(returnVo, files);
//		return returnVo;
//	}

//	@Override
//	public <T extends AbstractBill> T[] updateBills(T[] bills, MzAsFile[] files, MZBusinessLog[] logs) throws BusinessException {
//		T[] returnVo = this.updateBills(bills, logs);
//		returnVo = this.saveOrUpdateFiles(returnVo, files);
//		return returnVo;
//	}

//	@Override
//	public <T extends AbstractBill> T[] deleteBills(T[] bills, MzAsFile[] files, MZBusinessLog[] logs) throws BusinessException {
//		return null;
//	}
	
//	private <T extends AbstractBill> T[] saveOrUpdateFiles(T[] bills, MzAsFile[] files) throws BusinessException{
//		String pk = null;
//		if (bills.length > 0) {
//			pk = (String) bills[0].getParent().getPrimaryKey();
//		}
//		if (pk == null) {
//			return bills;
//		}
//		List<MzAsFile> addList = new ArrayList<MzAsFile>();
//		List<MzAsFile> updateList = new ArrayList<MzAsFile>();
//		List<MzAsFile> delList = new ArrayList<MzAsFile>();
//		for (int i = 0; i < files.length; i++) {
//			MzAsFile file = (MzAsFile) files[i];
//			if (file.getStatus() == FileStatus.ADD) {
//				file.setP_uuid(pk);
//				addList.add(file);
//			} else if (file.getStatus() == FileStatus.UPDATE) {
//				updateList.add(file);
//			} else if (file.getStatus() == FileStatus.DEL) {
//				delList.add(file);
//			}
//		}
//		MzAsFile[] addFiles = new MzAsFile[addList.size()];
//		MzAsFile[] updateFiles = new MzAsFile[updateList.size()];
//		MzAsFile[] delFiles = new MzAsFile[delList.size()];
//		addFiles = addList.toArray(addFiles);
//		updateFiles = updateList.toArray(updateFiles);
//		delFiles = delList.toArray(delFiles);
//		try {
//			if (addFiles.length > 0) {
//				this.save(addFiles);
//			}
//			if (updateFiles.length > 0) {
//				this.update(updateFiles);
//			}
//			if (delFiles.length > 0) {
//				this.delete(delFiles);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new BusinessException(e.getMessage());
//		}
//		return bills;
//	}

	
	/**
	 * <p>方法名称：buildAddProcessor</p>
	 * <p>方法描述：根据单据对象构造保存处理器，负责保存前后规则的执行</p>
	 * @param vos     单据对象数组
	 * @return AroundProcesser 
	 * @author chencd
	 * @since  2013-9-24
	 * <p> history 2013-9-24 chencd  创建   <p>
	 */
	private <T extends AbstractBill> AroundProcesser<T> buildAddProcessor(T[] vos) throws BusinessException{
		if(vos == null || vos.length == 0){
			return null ;
		}	
		AroundProcesser<T>  processor = new AroundProcesser<T>(null);
		IRule<T> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processor.addBeforeRule(rule);
		rule = new nc.bs.pubapp.pub.rule.CreateBillCodeRule();
		
		String billno = this.getBillNoCode(vos) ;   
		
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setCodeItem(billno != null ? billno : this.getParentPKName(vos));
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setOrgItem("pk_org");
		processor.addBeforeRule(rule);
		rule = new nc.bs.pubapp.pub.rule.FieldLengthCheckRule();
		processor.addBeforeRule(rule);
		if(hasChildren(vos)){
			rule = new nc.bs.pubapp.pub.rule.CheckNotNullRule();
			processor.addBeforeRule(rule);
		}	
		IRule<T>  billCodeCheckRule = this.buildBillCodeCheckRule(billno,"pk_group","pk_org");
		processor.addAfterRule(billCodeCheckRule);
		return processor ;
	}
	
	/**
	 * <p>方法名称：getBillNoCode</p>
	 * <p>方法描述：获取单据号字段的属性名称</p>
	 * @param bills   单据数组
	 * @return String
	 * @author chencd
	 * @since  2013-9-24
	 * <p> history 2013-9-24 chencd  创建   <p>
	 */
	private <T extends AbstractBill>  String getBillNoCode(T[] bills ) {
		if(bills != null && bills.length >0){
		  IAttributeMeta  meta = 	bills[0].getParent().getMetaData().getAttribute("aobb0010") ;
		  if(meta != null){
			  return meta.getName();
		  }
		}
		return null ;  
	}
	
	/**
	 * <p>方法名称：getParentPKName</p>
	 * <p>方法描述：获取单据主表主键字段的名称</p>
	 * @param bills    单据数组
	 * @return  String
	 * @author chencd
	 * @since  2013-9-24
	 * <p> history 2013-9-24 chencd  创建   <p>
	 */
	private <T extends AbstractBill> String getParentPKName(T[] bills){
		if(bills != null && bills.length >0){
			  IAttributeMeta  meta = 	bills[0].getParent().getMetaData().getPrimaryAttribute() ;
			  if(meta != null){
				  return meta.getName();
			  }
			}
			return null ;  
	}
	
	/**
	 * <p>方法名称：buildBillCodeCheckRule</p>
	 * <p>方法描述：构造一个单据单位检查规则</p>
	 * @param billType   单据类型
	 * @param billCode   单据代码
	 * @param pk_group   集团
	 * @param pk_org     组织
	 * @return IRule
	 * @author chencd
	 * @since  2013-9-24
	 * <p> history 2013-9-24 chencd  创建   <p>
	 */
	private <T extends AbstractBill> IRule<T> buildBillCodeCheckRule(String billCode,String pk_group,String pk_org) {
		IRule<T> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCodeItem(billCode);
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setGroupItem(pk_group);
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem(pk_org);
        return rule ;
	}
	
	/**
	 * <p>方法名称：hasChildren</p>
	 * <p>方法描述：判定对象是否存在子表</p>
	 * @param vos    业务单据对象
	 * @return boolean
	 * @author chencd
	 * @since  2013-9-24
	 * <p> history 2013-9-24 chencd  创建   <p>
	 */
	private <T extends AbstractBill> boolean hasChildren(T[] vos) {
		return vos[0].getMetaData().getChildren() != null  && vos[0].getMetaData().getChildren().length>0 ;
	}
	
	/**
	 * <p>方法名称：buildUpdateProcessor</p>
	 * <p>方法描述：根据单据对象构造更新处理器，负责在更新前后规则的执行</p>
	 * @param vos  单据对象数组
	 * @return AroundProcesser
	 * @author chencd
	 * @since  2013-9-24
	 * <p> history 2013-9-24 chencd  创建   <p>
	 */
	private <T extends AbstractBill> CompareAroundProcesser<T> buildUpdateProcessor(T[] vos) throws BusinessException {
		if(vos == null || vos.length == 0){
			return null ;
		}
		CompareAroundProcesser<T>  processor = new CompareAroundProcesser<T>(null);
		IRule<T> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processor.addBeforeRule(rule);
		
		String billno = this.getBillNoCode(vos) ;   
		ICompareRule<T> ruleCom = new nc.bs.pubapp.pub.rule.UpdateBillCodeRule();
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setCodeItem(billno != null ? billno : this.getParentPKName(vos));
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setOrgItem("pk_org");
		processor.addBeforeRule(ruleCom);
		rule = new nc.bs.pubapp.pub.rule.FieldLengthCheckRule();
		processor.addBeforeRule(rule);
		
		IRule<T>  billCodeCheckRule = this.buildBillCodeCheckRule(billno,"pk_group","pk_org");
		processor.addAfterRule(billCodeCheckRule);
		return processor ;
	}

	/**
	 * <p>方法名称：buildDeleteProcessor</p>
	 * <p>方法描述：根据单据对象构造删除处理器，负责在删除前后规则的执行</p>
	 * @param vos  单据对象数组
	 * @return AroundProcesser
	 * @author chencd
	 * @since  2013-9-24
	 * <p> history 2013-9-24 chencd  创建   <p>
	 */
	private <T extends AbstractBill> AroundProcesser<T> buildDeleteProcessor(T[] vos) throws BusinessException {
		AroundProcesser<T>  processor = new AroundProcesser<T>(null);
		return processor ;
	}
	
	private Class getClassFromName(String className){
		Class clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return clazz;
	}

	@Override
	public <T extends AbstractBill> T[] queryByQueryScheme(Class<T> clazz, IQueryScheme queryScheme) throws BusinessException {
		T[] bills = null;
		try {
			BillLazyQuery<T> query = new BillLazyQuery<T>(clazz);
			bills = query.query(queryScheme, null);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return bills;
	}
	
	@Override
	public PaginationTransferObject queryByPageQueryScheme(IQueryScheme queryScheme, int pageSize, String clazz) throws BusinessException{
		String[] allPks = this.queryPKSByScheme(queryScheme, clazz);
		List<String> firstQueryPks = PaginationUtils.getFistPageNeedQryList(allPks, pageSize);
		Object[] firstPageData = this.queryBillByPK(firstQueryPks.toArray(new String[0]), clazz);
		PaginationTransferObject paginationTransferObject = new PaginationTransferObject();
		paginationTransferObject.setAllPks(allPks);
		paginationTransferObject.setFirstPageData(firstPageData);
		return paginationTransferObject;
	}
	
	@Override
	public <T extends AbstractBill> T[] queryBillByPK(Class<T> clazz, String[] billIds) throws BusinessException{
		T[] bills = null;
		BillQuery<T> query = new BillQuery<T>(clazz);
		bills = query.query(billIds);
		return PaginationUtils.filterNotExistBills(bills, billIds);
	}
	
	@Override
	public <T extends AbstractBill> T[] queryBillByPK(String[] billIds, String clazz) throws BusinessException {
		T[] bills = null;
		BillQuery<T> query = null;
		try {
			query = new BillQuery<T>((Class<T>) Class.forName(clazz));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		bills = query.query(billIds);
		return PaginationUtils.filterNotExistBills(bills, billIds);
	}
	
	@Override
	public <T extends SuperVO> T[] querySuperVOByPK(Class<T> clazz, String[] pks) throws BusinessException{
		T[] vos = null;
		VOQuery<T> query = new VOQuery<T>(clazz);
		vos = query.query(pks);
		return vos;
	}
	
	@Override
	public <T extends SuperVO> T[] querySuperVOByWhere(Class<T> clazz, String whereSql) throws BusinessException{
		T[] vos = null;
		VOQuery<T> query = new VOQuery<T>(clazz);
		vos = query.queryWithWhereKeyWord(whereSql, null);
		return vos;
	}

	@Override
	public <T extends AbstractBill> String[] queryPKSByScheme(Class<T> clazz, IQueryScheme queryScheme) throws BusinessException{
		StringBuffer sql = new StringBuffer();
		QuerySchemeProcessor processor = new QuerySchemeProcessor(queryScheme);
		String mainAlias = processor.getMainTableAlias();
		String pkName = null;
		sql.append(" select distinct ");
		sql.append(mainAlias);
		sql.append(".");
		try {
			pkName = clazz.newInstance().getMetaData().getParent().getPrimaryAttribute().getName();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		sql.append(pkName);
		sql.append(processor.getFinalFromWhere());
		DataAccessUtils dao = new DataAccessUtils();
		IRowSet rowset = dao.query(sql.toString());
		String[] keys = rowset.toOneDimensionStringArray();
		return keys;
	}
	
	@Override
	public <T extends AbstractBill> String[] queryPKSByScheme(IQueryScheme queryScheme, String clazz) throws BusinessException {
		String sql = null;
		try {
			sql = buildQuerySql(queryScheme, (Class<T>)Class.forName(clazz));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		DataAccessUtils dao = new DataAccessUtils();
		IRowSet rowset = dao.query(sql);
		String[] keys = rowset.toOneDimensionStringArray();
		return keys;
	}
	
	private <T extends AbstractBill> String buildQuerySql(IQueryScheme queryScheme, Class<T> clazz) throws Exception {
		QuerySchemeProcessor processor = null;
	    String mainTableAlias = null;
	    boolean isCreateByUAP = true; // 处理不是UAP平台构造的QueryScheme对象实例的情况：是否是UAP平台构造
	    if (IQueryScheme.KEY_SQL_TABLE_LIST!=null) {
	    	processor = new QuerySchemeProcessor(queryScheme);
	    	mainTableAlias = processor.getMainTableAlias();
	    }else{
	    	isCreateByUAP = false;
	    }
	    String pkName = null;
	    String tableName = null;
	    tableName = mainTableAlias;
	    StringBuffer sql = new StringBuffer();
	    String where = null;
	    StringBuffer condition = new StringBuffer();
	    StringBuffer tableNames = new StringBuffer();
	    T Instance = clazz.newInstance();
	    ITableMeta[] tables = null;
	    if (isCreateByUAP) {
	      pkName =  Instance.getMetaData().getParent().getPrimaryAttribute().getName();
	      where = processor.getFinalFromWhere();
	    }else{
	      IVOMeta meta = Instance.getMetaData().getParent();
	      pkName = meta.getPrimaryAttribute().getName();
	      Class<?> parentClazz = Instance.getMetaData().getParent().getClass();
	      tables = ((SuperVO)parentClazz.newInstance()).getMetaData().getStatisticInfo().getTables();
	      for(int i=0; i<tables.length; i++){
	    	  tableNames.append(tables[i].getName()).append(",");
	    	  if(tables.length > 1){
	    		  condition.append(" and ").append(tables[i]).append(".").append(pkName).append("=");
	    		  condition.append(tables[i+1]).append(".").append(pkName).append(" ");
	    	  }
	      }
	    }
	    sql.append("select distinct ");
	    sql.append(mainTableAlias).append(".").append(pkName).append(" ");
	    if (isCreateByUAP) {
	      sql.append(where);
	    }else{
	      sql.append(" from ");
	      sql.append(tableNames.deleteCharAt(tableNames.length()-1)).append(" where 1=1 ");
	      if(tables != null && tables.length > 1){
	    	  sql.append(condition.toString()).append(" ");
	      }
	    }
	    // 附加的SQL
	    String add_sql = (String) queryScheme.get("ADD_SQL");
	    if (add_sql != null && !"".equals(add_sql)) {
	      sql.append(add_sql);
	    }
	    return sql.toString().replaceAll("'%", "'");
	}



	@Override
	public <T extends AbstractBill> String[] queryPKSByWhere(Class<T> clazz, String sqlWhere)  throws BusinessException{
		try {
			IBillMeta meta = clazz.newInstance().getMetaData();
			Class<? extends ISuperVO> pClazz = meta.getVOClass(meta.getParent());
			return querySuperVOPKSByWhere(pClazz, sqlWhere);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public <T extends AbstractBill> T[] queryAggVOByWhere(Class<T> clazz, String sqlWhere) throws BusinessException{
		String[] billIds = queryPKSByWhere(clazz, sqlWhere);
		if(billIds == null || billIds.length < 0){
			return null;
		}
		T[] bills = null;
		BillQuery<T> query = new BillQuery<T>(clazz);
		bills = query.query(billIds);
		return bills;
	}
	
	/**
	 * <p>方法名称：querySuperVOPKSByWhere</p>
	 * <p>方法描述：根据Where条件查询SuperVO对象的主键</p>
	 * @param clazz
	 * @param sqlWhere
	 * @return
	 * @throws BusinessException
	 * @throws Exception 
	 */
	private <T extends ISuperVO> String[] querySuperVOPKSByWhere(Class<T> clazz, String sqlWhere)  throws Exception{
		if (!CommonUtils.isEmpty(sqlWhere)) {
		  T cInstance = clazz.newInstance();
	      IVOMeta meta = cInstance.getMetaData();
	      ITableMeta[] tables = meta.getStatisticInfo().getTables();
	      String pkName = meta.getPrimaryAttribute().getName();
	      String tableName = (String) clazz.getMethod("getTableName").invoke(cInstance);
	      StringBuffer sql = new StringBuffer();
	      sql.append("select distinct ");
	      sql.append(tableName).append(".");
	      sql.append(pkName).append(" ");
	      sql.append("from ");
	      if(tables.length > 1){
	    	  for(int i=0; i<tables.length; i++){
	    		  sql.append(tables[i]).append(",");
	    	  }
	    	  sql.deleteCharAt(sql.length()-1);
	      }else{
	    	  sql.append(tableName);
	      }
	      sql.append(" ").append(sqlWhere);
	      DataAccessUtils utils = new DataAccessUtils();
	      IRowSet rowset = utils.query(sql.toString());
	      return (null != rowset && rowset.size() > 0) ? rowset
	          .toOneDimensionStringArray() : null;
	    }
	    return null;
	}

//	@Override
//	public List<?> execFunc(String funcName, PubSQLParameter params) throws BusinessException{
//		try {
//			return execProcedure(funcName, params,	PubSQLParameter.SQL_TYPE_FUNCTIION);
//		} catch (DbException e) {
//			throw new BusinessException(e);
//		}
//	}
//
//	@Override
//	public List<?> execProc(String procName, PubSQLParameter params) throws BusinessException{
//		try {
//			return execProcedure(procName, params, PubSQLParameter.SQL_TYPE_PRODURE);
//		} catch (DbException e) {
//			throw new BusinessException(e);
//		}
//	}
//
//	@Override
//	public List<?> execReportFunc(String funcName, PubSQLParameter params) {
////	    List<String>  result = (List<String>) this.execFunc(funcName, params);
////	    return (result != null && result.size()>0) ? PubSQLResult.parse(result.get(0)) : null ;
//        return null ;
//	}
	
//	/**
//	   * <p>方法名称：execProcedure</p>
//	   * <p>方法描述：执行一个存储过程或者是函数</p>
//	   * @param funcName      存储过程或者是函数的名称
//	   * @param param         存储过程或者是函数的参数对象
//	   * @param sqlObjType    过程类型： 存储过程或者是函数，参考，PubSQLParameter中静态变量
//	   * @return 执行结果列表
//	   * @throws DbException
//	   */
//	  private List<?> execProcedure(String funcName, PubSQLParameter param,
//	      int sqlObjType) throws DbException {
//	    String sql = null;
//	    try {
//	      sql = buildCallSQL(funcName, param, sqlObjType);
//	    } catch (SQLException e1) {
//	      throw new OracleException("构造Oracle存储过程或函数执行语句错误！", e1);
//	    }
//	    PersistenceManager pm = null;
//	    Connection conn = null;
//	    CallableStatement cstmt = null;
//	    List<?> result = null;
//	    try {
//	      pm = PersistenceManager.getInstance();
//	      conn = pm.getJdbcSession().getConnection();
//	      cstmt = conn.prepareCall(sql);
//	      setSQLParameter(cstmt, param, sqlObjType);
//	      cstmt.execute();
//	      result = getExecuteResult(cstmt, param);
//	    } catch (SQLException e) {
//	      throw new OracleException("执行Oracle函数出错！" + e.getMessage(), e);
//	    } finally {
//	      if (pm != null) {
//	        try {
//	          if(cstmt != null ){
//	            cstmt.close();
//	          }
//	          if(conn != null){
//	            conn.close();
//	          }
//	        } catch (SQLException e) {
//	          throw new OracleException("执行Oracle函数时关闭连接出错！", e);
//	        }
//	        if(pm != null){
//	          pm.release();  
//	        }
//	      }
//	    }
//	    return result;
//	  }
	  
//	  /**
//	   * <p>方法名称：getExecuteResult</p>
//	   * <p>方法描述：过程执行完毕以后，根据参数设置，获取返回参数，包括函数的返回值和存储过程的输出变量</p>
//	   * @param cstmt    调用过程的Statement对象
//	   * @param param    调用时传入的参数对象，这里使用输出参数（包括函数的返回值和存储过程的输出变量）
//	   * @return List    返回结果列表，如果无返回参数则返回空列表，如果有多个则按照返回（输出）的顺序排列
//	   * @throws SQLException
//	   */
//	  @SuppressWarnings({ "rawtypes", "unchecked" })
//	  private List getExecuteResult(CallableStatement cstmt, PubSQLParameter param)
//	      throws SQLException {
//	    List result = new ArrayList();
//	    if (param == null) {
//	      return result;
//	    }
//	    int size = param.size();
//	    //获取输出参数个数
//	    for (int i = 0; i < size; i++) {
//	      if (param.getSQLParameter(i).getInOrOut() == PubSQLParameter.SQL_PARAM_OUT) {
//	        result.add(cstmt.getObject(i + 1));
//	      }
//	    }
//	    return result;
//	  }

//	  /**
//	   * <p>方法名称：setSQLParameter</p>
//	   * <p>方法描述：给过程调用设置参数</p>
//	   * @param cstmt       过程调用的Statement对象
//	   * @param param       过程调用参数
//	   * @param sqlObjType  过程的类型：是函数或者是存储过程，可参考PubSQLParameter中静态变量
//	   * @throws SQLException
//	   */
//	  private void setSQLParameter(CallableStatement cstmt, PubSQLParameter param,
//	      int sqlObjType) throws SQLException {
//	    if (param == null) {
//	      return;
//	    }
//	    int size = param.size();
//	    for (int i = 0; i < size; i++) {
//	      SQLParameter item = param.getSQLParameter(i);
//	      if (item.getInOrOut() == PubSQLParameter.SQL_PARAM_IN) {
//	        if (null != item.getValue()) {
//	          cstmt.setObject(i + 1, item.getValue(), item.getType());
//	        } else {
//	          cstmt.setNull(i + 1, item.getType());
//	        }
//	      } else if (item.getInOrOut() == PubSQLParameter.SQL_PARAM_OUT) {
//	        cstmt.registerOutParameter(i + 1, item.getType());
//	      }
//	    }
//	  }

//	  /**
//	   * <p>方法名称：setSQLParameter</p>
//	   * <p>方法描述：给预处理语句设置参数</p>
//	   * @param stmt      预处理语句对象
//	   * @param param     SQL参数对象
//	   * @param update    是否是更新操作(:::)
//	   * @throws SQLException
//	   */
//	  private void setSQLParameter(PreparedStatement stmt, PubSQLParameter param,
//	      boolean update) throws SQLException {
//	    if (param == null || param.size() == 0) {
//	      return;
//	    }
//	    int size = param.size();
//	    for (int i = 0; i < size; i++) {
//	      SQLParameter item = param.getSQLParameter(i);
//	      stmt.setObject(i + 1, item.getValue());
//	    }
//	  }
//
//	  private String buildCallSQL(String funcName, PubSQLParameter param, int type)
//	      throws SQLException {
//	    StringBuffer sql = new StringBuffer();
//	    if (PubSQLParameter.SQL_TYPE_FUNCTIION == type) {
//	      sql.append("{?=call ");
//	    } else if (PubSQLParameter.SQL_TYPE_PRODURE == type) {
//	      sql.append("{ call ");
//	    }
//	    sql.append(funcName).append(" ");
//	    int size = (param == null) ? 0 : param.size();
//	    sql.append("(");
//	    if (PubSQLParameter.SQL_TYPE_FUNCTIION == type) {
//	      if (param == null || size < 1) {
//	        throw new SQLException("调用Oracle函数执行方法至少应传入一个返回参数，请参考execFunc声明！");
//	      } else {
//	        for (int i = 1; i < size; i++) {
//	          sql.append("?").append(",");
//	        }
//	      }
//	    } else if (PubSQLParameter.SQL_TYPE_PRODURE == type) {
//	      if (size > 0) {
//	        for (int i = 0; i < size; i++) {
//	          sql.append("?").append(",");
//	        }
//	      }
//	    } else {
//	      throw new SQLException("指定的数据库对象类型不支持，TYPE=" + type);
//	    }
//	    if (sql.toString().endsWith(",")) {
//	      sql.deleteCharAt(sql.length() - 1);
//	    }
//	    sql.append(")");
//	    sql.append("}");
//	    return sql.toString();
//	  }
	

//	@Override
//	public String getSequenceValue(String seqName) throws BusinessException {
//		return this.getSequenceValue(seqName, -1, false);
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public String getSequenceValue(String seqName, int seqLength,	boolean isAddTime) throws BusinessException {
//		 PubSQLParameter param=new PubSQLParameter();
//		  
//		    param.addParam(PubSQLParameter.SQL_PARAM_OUT,java.sql.Types.VARCHAR,null);
//		    param.addParam(java.sql.Types.VARCHAR, seqName);
//		    param.addParam(java.sql.Types.INTEGER, seqLength);
//		    
//		    if (isAddTime) {
//		      param.addParam(java.sql.Types.VARCHAR, "Y");
//		    } else {
//		      param.addParam(java.sql.Types.VARCHAR, "N");
//		    }
//		    List<String> list = (List<String>) this.execFunc(Constant.PROC_NAME.FUNC_SEQ_NEXT_VALUE, param);
//		    return  list.isEmpty() ? null : list.get(0);
//	}


	@Override
	public <T extends AbstractBill> T[] save(T[] clientFullVOs, T[] originBills) throws BusinessException {
		for(T clientFullVO : clientFullVOs){
	        clientFullVO.getParentVO().setAttributeValue("approvestatus", BillStatusEnum.COMMIT.value());
	        clientFullVO.getParentVO().setStatus(VOStatus.UPDATED);
		}
		// 数据持久化
		T[] returnVos =new BillUpdate<T>().update(clientFullVOs, originBills);
		return returnVos;
	}

	@Override
	public <T extends AbstractBill> T[] unsave(T[] clientFullVOs, T[] originBills) throws BusinessException {
		 // 把VO持久化到数据库中
	    for(T clientBill : clientFullVOs) {
	        clientBill.getParentVO().setAttributeValue("approvestatus", BillStatusEnum.FREE.value());
	        clientBill.getParentVO().setStatus(VOStatus.UPDATED);
	    }
	    BillUpdate<T> update = new BillUpdate<T>();
	    T[] returnVos = update.update(clientFullVOs, originBills);
	    return returnVos;
	}

	@Override
	public <T extends AbstractBill> T[] approve(T[] clientFullVOs, T[] originBills) throws BusinessException {
		for(int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<T> update = new BillUpdate<T>();
	    T[] returnVos = update.update(clientFullVOs, originBills);
		return returnVos;
	}

	@Override
	public <T extends AbstractBill> T[] unapprove(T[] clientFullVOs, T[] originBills) throws BusinessException {
		for(int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<T> update = new BillUpdate<T>();
	    T[] returnVos = update.update(clientFullVOs, originBills);
	    return returnVos;
	}

	@Override
	public Object doAfterRunned(WfGadgetContext gc) throws BusinessException {
		return null;
	}

	@Override
	public Object undoAfterRunned(WfGadgetContext gc) throws BusinessException {
		return null;
	}

	@Override
	public Object doBeforeActive(WfGadgetContext gc) throws BusinessException {
		return null;
	}
	
	@Override
	public Object undoBeforeActive(WfGadgetContext gc) throws BusinessException {
		return null;
	}

	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException {
		BatchSaveAction saveAction = new BatchSaveAction();
	    BatchOperateVO retData = saveAction.batchSave(batchVO);
	    return retData;
	}

	@Override
	public ISuperVO[] queryByDataVisibilitySetting(LoginContext context, Class<? extends ISuperVO> clz) throws BusinessException {
		return selectByWhereSql(null, clz);
	}

	@Override
	public ISuperVO[] selectByWhereSql(String whereSql, Class<? extends ISuperVO> clz) throws BusinessException {
		return super.selectByWhereSql(whereSql, clz);
	}


}
