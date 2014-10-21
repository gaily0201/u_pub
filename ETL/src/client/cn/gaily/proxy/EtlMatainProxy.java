package cn.gaily.proxy;

import nc.bs.framework.common.NCLocator;
import nc.gaily.pub.vo.AggBaseInfo;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import cn.gaily.itf.IEtlMaintain;

public class EtlMatainProxy implements IEtlMaintain,IPaginationQueryService,IQueryService,IDataOperationService {

	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		 AggBaseInfo[] rets = null;
		 IEtlMaintain service = NCLocator.getInstance().lookup(IEtlMaintain.class);
	        try {
	            rets = service.queryBillByPK(pks);
	        }
	        catch (BusinessException e) {
	            ExceptionUtils.wrappException(e);
	        }
	        return rets;
	}

	@Override
	public Object[] queryByWhereSql(String whereSql) throws Exception {
		return null;
	}

	@Override
	public IBill[] insert(IBill[] value) throws BusinessException {
		return null;
	}

	@Override
	public IBill[] update(IBill[] value) throws BusinessException {
		return null;
	}

	@Override
	public IBill[] delete(IBill[] value) throws BusinessException {
		return null;
	}

	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		return null;
	}

	@Override
	public void delete(AggBaseInfo[] clientFullVOs, AggBaseInfo[] originBills)
			throws BusinessException {
		
	}

	@Override
	public AggBaseInfo[] insert(AggBaseInfo[] clientFullVOs,
			AggBaseInfo[] originBills) throws BusinessException {
		return null;
	}

	@Override
	public AggBaseInfo[] update(AggBaseInfo[] clientFullVOs,
			AggBaseInfo[] originBills) throws BusinessException {
		return null;
	}

	@Override
	public String[] queryPKs(IQueryScheme queryScheme) throws BusinessException {
		return null;
	}

	@Override
	public AggBaseInfo[] queryBillByPK(String[] pks) throws BusinessException {
		return null;
	}

	@Override
	public AggBaseInfo[] save(AggBaseInfo[] clientFullVOs,
			AggBaseInfo[] originBills) throws BusinessException {
		return null;
	}

	@Override
	public AggBaseInfo[] unsave(AggBaseInfo[] clientFullVOs,
			AggBaseInfo[] originBills) throws BusinessException {
		return null;
	}

}
