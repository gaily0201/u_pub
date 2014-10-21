package cn.gaily.itf;

import java.util.ArrayList;
import java.util.HashMap;
import cn.gaily.pub.itf.IBaseService;

/**
 * <p>Title: IETLService</P>
 * <p>Description: </p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-21
 */
public interface IETLService extends IBaseService{
	
	public ArrayList<HashMap<String,String>> queryColumnInfo(String tableName,String dataSourceName);
	
}
