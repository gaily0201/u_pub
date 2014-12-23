package cn.gaily.base.service;

import nc.bs.framework.common.NCLocator;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.SuperVO;
import nc.vo.uif2.LoginContext;

public class SingleServiceImpl implements ISingleService {

	
	IBaseService service = NCLocator.getInstance().lookup(IBaseService.class);

	
	@Override
	public Object insert(Object object) throws Exception {
		return service.save(new SuperVO[]{(SuperVO) object})[0];
	}

	@Override
	public Object update(Object object) throws Exception {
		return service.update(new SuperVO[]{(SuperVO) object})[0];
	}

	@Override
	public void delete(Object object) throws Exception {
		service.delete(new SuperVO[]{(SuperVO) object});
	}

	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext context) throws Exception {
		return null;
	}

	@Override
	public Object[] queryByWhereSql(String whereSql) throws Exception {
		return null;
	}
}
