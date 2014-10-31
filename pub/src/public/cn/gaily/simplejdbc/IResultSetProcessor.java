package cn.gaily.simplejdbc;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>Title: IResultSetProcessor</P>
 * <p>Description: 结果集处理器接口</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-31
 */
public interface IResultSetProcessor extends Serializable{

	public Object process(ResultSet rs) throws SQLException;
}
