package cn.gaily.base.ui.proxy;

import nc.bs.framework.common.NCLocator;
import nc.vo.pub.SuperVO;
import nc.vo.uif2.LoginContext;
import cn.gaily.base.service.IBaseService;
import cn.gaily.base.service.ISingleService;

public class SingleServiceProxy implements ISingleService{
	
	ISingleService service = NCLocator.getInstance().lookup(ISingleService.class);
	IBaseService base = NCLocator.getInstance().lookup(IBaseService.class);
	
	private String voClass = null;
	
	@Override
	public Object insert(Object object) throws Exception {
		return service.insert(object);
	}

	@Override
	public Object update(Object object) throws Exception {
		return service.update(object);
	}

	@Override
	public void delete(Object object) throws Exception {
		service.delete(object);
	}

	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext context) throws Exception {
		return service.queryByDataVisibilitySetting(context);
	}

	@Override
	public Object[] queryByWhereSql(String whereSql) throws Exception {
		return queryByWhereSql(getVoClass(), whereSql);
	}

	public String getVoClass() {
		return voClass;
	}
	public void setVoClass(String voClass) {
		this.voClass = voClass;
	}

	public Object[] queryByWhereSql(String voClass, String whereSql) throws Exception {
		Class c = null;
		if(voClass!=null){
			c  = Class.forName(voClass);
			return base.querySuperVOByWhere(c,whereSql);
		}
		return null;
	}
	
}
