package cn.gaily.pub.trigger.timer;

import cn.gaily.pub.trigger.TaskExecutor;
import cn.gaily.simplejdbc.SimpleDSMgr;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.vo.pub.BusinessException;

public class ETLExchangeTimer implements IBackgroundWorkPlugin{

	@Override
	public PreAlertObject executeTask(BgWorkingContext bgwc) throws BusinessException {
		
//		TaskExecutor task  = new TaskExecutor();
//		SimpleDSMgr remote = new SimpleDSMgr();
//		SimpleDSMgr local = new SimpleDSMgr();
//		
//		if(remote.conns.isEmpty()){
//			remote.setInitSize(5);
//			remote.initDB("orcl", "192.168.1.100", "uap63_test", "1", "1521");
//			remote.init();
//		}
//		if(local.conns.isEmpty()){
//			local.setInitSize(5);
//			local.initDB("orcl", "192.168.1.100", "uap63", "1", "1521");
//			local.init();
//		}
//		
//		task.execute(remote, local);
		int i = 0;
		System.out.println(i++);
		return null;
	}

}
