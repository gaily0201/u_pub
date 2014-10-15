package cn.gaily.pub.itf;

import java.util.List;
import java.util.Map;

import nc.vo.pub.BusinessException;

/**
 * <p>Title: IBaseService</P>
 * <p>Description: 基础的服务类</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-9-1
 */
public interface IBaseService {

	/**
	 * <p>方法名称：generatePks</p>
	 * <p>方法描述：批量获取Pk</p>
	 * @param count 数量
	 * @return
	 * @author xiaoh
	 * @since  2014-10-10
	 * <p> history 2014-10-10 xiaoh  创建   <p>
	 */
	public List<String> generatePks(int count);
	
	
	/**
	 * <p>方法名称：getPkorgByName</p>
	 * <p>方法描述：根据单位名称获取Pk_org的值</p>
	 * @param name	 单位名称
	 * @param needRefresh  是否需要重新构建
	 * @author xiaoh
	 * @since  2014-9-14
	 * <p> history 2014-9-14 xiaoh  创建   <p>
	 */
	public String getPkorgByName(String name, boolean needRefresh) throws BusinessException;


	/**
	 * <p>方法名称：getAllNamePkorgMap</p>
	 * <p>方法描述：从ORG_ORGS表中获取所有的[名称-pk_org]对应关系</p>
	 * @return
	 * @author xiaoh
	 * @since  2014-10-14
	 * <p> history 2014-10-14 xiaoh  创建   <p>
	 */
	public Map<String, String> getAllNamePkorgMap();

}
