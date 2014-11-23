package cn.gaily.pub.trigger;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import cn.gaily.simplejdbc.SimpleDSMgr;

/**
 * <p>Title: DefaultETLTask</P>
 * <p>Description: 默认的任务实现</p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-29
 */
public class DefaultETLTask extends AbstractETLTask {

	public static DefaultETLTask instance = null;
	
	private DefaultETLTask(){}
	
	public static DefaultETLTask getInstance(){
		if(instance ==null){
			instance = new DefaultETLTask();
		}
		return instance;
	}
	
	@Override
	public void doexecute(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName, String pkName, Map<String, Object> valueMap,Map<String,String> colNameTypeMap) {
	}

	@Override
	public int doBatch(SimpleDSMgr srcMgr, SimpleDSMgr tarMgr, String tableName, String pkName,
						ArrayBlockingQueue<Map<String, Object>> valueList,
						Map<String, String> colNameTypeMap, Boolean canBatch) {
		return 0;
	}

}
