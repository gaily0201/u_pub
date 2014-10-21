package cn.gaily.proxy;

import nc.bs.framework.common.NCLocator;
import nc.gaily.pub.vo.AggBaseInfo;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import cn.gaily.itf.IEtlMaintain;

public class EtlMatainProxy implements IPaginationQueryService {

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

}
