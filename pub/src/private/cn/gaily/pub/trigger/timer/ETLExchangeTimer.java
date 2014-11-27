package cn.gaily.pub.trigger.timer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.vo.pub.BusinessException;
import cn.gaily.pub.trigger.TaskExecutor;
import cn.gaily.simplejdbc.SimpleDSMgr;

public class ETLExchangeTimer implements IBackgroundWorkPlugin{
	
	ETLBackgrondDbUtil util = null;
	
	@Override
	public PreAlertObject executeTask(BgWorkingContext bgwc) throws BusinessException {
//		SynMaintainImpl.setHasChecked(false);
		Map<String,List<SimpleDSMgr>> allDs = new HashMap<String, List<SimpleDSMgr>>();
		if(util==null){
			util = new ETLBackgrondDbUtil();
		}
		
		SimpleDSMgr remote = null;
		SimpleDSMgr local  = null;
		int count = 0;
		allDs = util.getAllDs();
		Entry<String, List<SimpleDSMgr>> entry = null;
		String fileName = null;
		for(Iterator it=allDs.entrySet().iterator();it.hasNext();){
			entry = (Entry<String, List<SimpleDSMgr>>) it.next();
			fileName = entry.getKey();
			local = entry.getValue().get(0);
			remote =  entry.getValue().get(1);
			if(remote!=null&&local!=null&&remote.conns.size()>=5&&local.conns.size()>=5){
				TaskExecutor task = new TaskExecutor();
				task.execute(local, remote);
			}else{
				for(it=allDs.entrySet().iterator();it.hasNext();){
					entry = (Entry<String, List<SimpleDSMgr>>) it.next();
					fileName = entry.getKey();
					local = entry.getValue().get(0);
					remote =  entry.getValue().get(1);
					remote.realRelease();
					local.realRelease();
				}
				count ++;
				if(count>=10){
					throw new RuntimeException("数据双向同步执行出错, 请检查数据源都配置正确，配置表ETL_SYNINFO中PK为:"+ fileName);
				}
				executeTask(bgwc);
			}
		}
		return null;
	}
}
