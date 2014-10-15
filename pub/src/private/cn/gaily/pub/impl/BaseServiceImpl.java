package cn.gaily.pub.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.generator.IdGenerator;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.pub.BusinessException;
import cn.gaily.pub.itf.IBaseService;
import cn.gaily.pub.util.CommonUtil;

public class BaseServiceImpl implements IBaseService {

	IdGenerator generator = NCLocator.getInstance().lookup(IdGenerator.class);
	
	IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
	
	Logger logger = Logger.getLogger(BaseServiceImpl.class);
	
	/**
	 * 根据名称查询pk_org, key:name, value:pk_org
	 */
	Map<String, String> orgNamePkMap = new HashMap<String,String>();
	
	
	
	@Override
	public List<String> generatePks(int count) {
		String[] pks = generator.generate(count);
		return CommonUtil.arrayToList(pks);
	}
	
	@Override
	public String getPkorgByName(String name, boolean needRefresh) throws BusinessException {
		if(CommonUtil.isEmpty(name)){
			return null;
		}
		if(needRefresh==false&&!orgNamePkMap.isEmpty()){
			return orgNamePkMap.get(name);
		}
		orgNamePkMap = new HashMap<String, String>();
		String querySql = "SELECT NAME,PK_ORG FROM ORG_ORGS";
		List<Map<String,String>> list = (ArrayList<Map<String,String>>)query.executeQuery(querySql,new MapListProcessor());
		Map map = null;
		String oname = null;
		String pk_org = null;
		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();i++){
				map = list.get(i);
				oname = (String) map.get("name");
				pk_org = (String)map.get("pk_org");
				if(CommonUtil.isNotEmpty(oname)&&CommonUtil.isNotEmpty(pk_org)){
					orgNamePkMap.put(oname, pk_org);
				}
			}
		}
		return orgNamePkMap.get(name);
	}

	@Override
	public Map<String, String> getAllNamePkorgMap() {
		String sql = "SELECT NAME,PK_ORG,CODE FROM ORG_ORGS";
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		try {
			list = (ArrayList<Map<String,String>>)query.executeQuery(sql,new MapListProcessor());
		} catch (BusinessException e) {
			logger.error(e);
		}
		Map<String,String> returnMap = new HashMap<String,String>();
		Map map = null;
		String oname = null;
		String pk_org = null;
		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();i++){
				map = list.get(i);
				oname = (String) map.get("name");
				pk_org = (String)map.get("pk_org");
				if(CommonUtil.isNotEmpty(oname)&&CommonUtil.isNotEmpty(pk_org)){
					returnMap.put(oname, pk_org);
				}
			}
		}
		return returnMap;
	}
}
