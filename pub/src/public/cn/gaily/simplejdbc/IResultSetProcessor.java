package cn.gaily.simplejdbc;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>Title: IResultSetProcessor</P>
 * <p>Description: ������������ӿ�</p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-31
 */
public interface IResultSetProcessor extends Serializable{

	public Object process(ResultSet rs) throws SQLException;
}
