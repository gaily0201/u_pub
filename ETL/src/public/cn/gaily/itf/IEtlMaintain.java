package cn.gaily.itf;

import nc.gaily.pub.vo.AggBaseInfo;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;

public interface IEtlMaintain {
	 public void delete(AggBaseInfo[] clientFullVOs,AggBaseInfo[] originBills) throws BusinessException;

	    public AggBaseInfo[] insert(AggBaseInfo[] clientFullVOs,AggBaseInfo[] originBills) throws BusinessException;
	  
	    public AggBaseInfo[] update(AggBaseInfo[] clientFullVOs,AggBaseInfo[] originBills) throws BusinessException;


	    public String[] queryPKs(IQueryScheme queryScheme)
	      throws BusinessException;
	    public AggBaseInfo[] queryBillByPK(String[] pks)
	      throws BusinessException ;

	  public AggBaseInfo[] save(AggBaseInfo[] clientFullVOs,AggBaseInfo[] originBills)
	      throws BusinessException ;

	  public AggBaseInfo[] unsave(AggBaseInfo[] clientFullVOs,AggBaseInfo[] originBills)
	      throws BusinessException ;
}
