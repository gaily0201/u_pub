package cn.gaily.impl;

import nc.gaily.pub.vo.AggBaseInfo;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import cn.gaily.itf.IEtlMaintain;

public class EtlMaintainImpl implements IEtlMaintain {

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
