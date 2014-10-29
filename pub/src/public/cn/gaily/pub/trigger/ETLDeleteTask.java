package cn.gaily.pub.trigger;

/**
 * <p>Title: ETLDeleteTask</P>
 * <p>Description: 数据交换删除任务</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-29
 */
public class ETLDeleteTask extends AbstractETLTask{

	@Override
	public void dealDel(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName) {
		
	}

	
	
	@Override
	public void dealAdd(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName) {
	}
	@Override
	public void dealUpdate(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName) {
	}


}
