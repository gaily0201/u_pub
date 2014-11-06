package cn.gaily.pub.trigger.timer;

import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.vo.pub.BusinessException;
import cn.gaily.pub.trigger.TaskExecutor;
import cn.gaily.pub.util.CommonUtil;
import cn.gaily.simplejdbc.SimpleDSMgr;

public class ETLExchangeTimer implements IBackgroundWorkPlugin{

	SimpleDSMgr remote = new SimpleDSMgr();
	SimpleDSMgr local  = new SimpleDSMgr();

	@Override
	public PreAlertObject executeTask(BgWorkingContext bgwc) throws BusinessException {
		
		int count  = 0;
		
		while(remote.conns.size()<=0||local.conns.size()<=0){
			CommonUtil common = new CommonUtil();
			common.setPropFilepath("/cn/gaily/pub/trigger/dbinfo.properties");
			String rdbname = common.getProperty("remote.dbname");
			String rusername = common.getProperty("remote.username");
			String rpassword = common.getProperty("remote.password");
			String rport = common.getProperty("remote.port");
			String rip = common.getProperty("remote.ip");
			String ldbname = common.getProperty("local.dbname");
			String lusername = common.getProperty("local.username");
			String lpassword = common.getProperty("local.password");
			String lport = common.getProperty("local.port");
			String lip = common.getProperty("local.ip");
			
			if(CommonUtil.isNotEmpty(rdbname)){
				remote.setDbName(rdbname);
			}
			if(CommonUtil.isNotEmpty(rusername)){
				remote.setUserName(rusername);
			}
			if(CommonUtil.isNotEmpty(rpassword)){
				remote.setPassword(rpassword);
			}
			if(CommonUtil.isNotEmpty(rport)){
				remote.setPort(rport);
			}
			if(CommonUtil.isNotEmpty(rip)){
				remote.setIp(rip);
			}
			if(CommonUtil.isNotEmpty(ldbname)){
				local.setDbName(ldbname);
			}
			if(CommonUtil.isNotEmpty(lusername)){
				local.setUserName(lusername);
			}
			if(CommonUtil.isNotEmpty(lpassword)){
				local.setPassword(lpassword);
			}
			if(CommonUtil.isNotEmpty(lport)){
				local.setPort(lport);
			}
			if(CommonUtil.isNotEmpty(lip)){
				local.setIp(lip);
			}
			remote.init();
			local.init();
			
			count++;
			if(count>=10){
				throw new RuntimeException("获取数据源失败！");
			}
			
		}
		
		TaskExecutor task = new TaskExecutor();
		task.execute(remote, local);
		return null;
	}
	
	
}
