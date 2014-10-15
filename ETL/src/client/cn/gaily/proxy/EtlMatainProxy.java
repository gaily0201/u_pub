package cn.gaily.proxy;

import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.model.IQueryService;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;

public class EtlMatainProxy implements IDataOperationService, IQueryService,
		IPaginationQueryService {

	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		return null;
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

}
