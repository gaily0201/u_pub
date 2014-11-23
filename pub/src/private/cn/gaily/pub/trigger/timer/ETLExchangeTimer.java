package cn.gaily.pub.trigger.timer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.MapProcessor;
import nc.vo.pub.BusinessException;
import cn.gaily.pub.trigger.TaskExecutor;
import cn.gaily.pub.util.CommonUtils;
import cn.gaily.simplejdbc.SimpleDSMgr;

public class ETLExchangeTimer implements IBackgroundWorkPlugin{

	SimpleDSMgr remote = new SimpleDSMgr();
	SimpleDSMgr local  = new SimpleDSMgr();

	@Override
	public PreAlertObject executeTask(BgWorkingContext bgwc) throws BusinessException {
		
		int count  = 0;
		
		while(remote.conns.size()<=0||local.conns.size()<=0){
			CommonUtils common = new CommonUtils();
			common.setPropFilepath(1);
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
			
			if(CommonUtils.isNotEmpty(rdbname)&&CommonUtils.isNotEmpty(rusername)
					&&CommonUtils.isNotEmpty(rpassword) &&CommonUtils.isNotEmpty(rport)
					&&CommonUtils.isNotEmpty(rip)&&
					CommonUtils.isNotEmpty(ldbname)&&CommonUtils.isNotEmpty(lusername)
					&&CommonUtils.isNotEmpty(lpassword) &&CommonUtils.isNotEmpty(lport)
					&&CommonUtils.isNotEmpty(lip)){
				remote.setDbName(rdbname);
				remote.setUserName(rusername);
				remote.setPassword(rpassword);
				remote.setPort(rport);
				remote.setIp(rip);
				local.setDbName(ldbname);
				local.setUserName(lusername);
				local.setPassword(lpassword);
				local.setPort(lport);
				local.setIp(lip);
				
				remote.init();
				local.init();
			}else{
				List<SimpleDSMgr> list = getProperties();
				remote = list.get(0);
				local = list.get(1);
				remote.init();
				local.init();
				writeProp(remote, local);
			}

			count++;
			if(count>=10){
				throw new RuntimeException("获取数据源失败！");
			}

		}
		
		TaskExecutor task = new TaskExecutor();
		task.execute(remote, local);
		return null;
	}
	
	private List<SimpleDSMgr> getProperties(){
		String sql = "SELECT A.SRCNAME, A.SCRIP, A.SRCPORT, A.SRCUSERNAME, A.SRCPASSWORD, A.TARNAME, A.TARUSERNAME, A.TARIP, A.TARPORT, A.TARPASSWORD FROM ETL_SYNINFO A WHERE A.DR = 0";
		SimpleDSMgr s1 = new SimpleDSMgr();
		SimpleDSMgr s2 = new SimpleDSMgr();
		try {
			JdbcSession session = new JdbcSession();
			Map<String,String> result = (Map<String,String>) session.executeQuery(sql, new MapProcessor());
			String srcname = result.get("srcname");
			String scrip = result.get("scrip");
			String srcport = result.get("srcport");
			String srcusername = result.get("srcusername");
			String srcpassword = result.get("srcpassword");
			s1.setDbName(srcname);
			s1.setIp(scrip);
			s1.setPort(srcport);
			s1.setUserName(srcusername);
			s1.setPassword(srcpassword);
			
			String tarname = result.get("tarname");
			String tarip = result.get("tarip");
			String tarport = result.get("tarport");
			String tarusername = result.get("tarusername");
			String tarpassword = result.get("tarpassword");
			s2.setDbName(tarname);
			s2.setIp(tarip);
			s2.setPort(tarport);
			s2.setUserName(tarusername);
			s2.setPassword(tarpassword);
		} catch (DbException e) {
			e.printStackTrace();
		}
		return Arrays.asList(new SimpleDSMgr[]{s1,s2});
	}
	
	private void writeProp(SimpleDSMgr remotee, SimpleDSMgr locall) {
		CommonUtils common = new CommonUtils();
		common.setPropFilepath(1);
		common.setProperty("remote.dbname", remotee.getDbName());
		common.setProperty("remote.username", remotee.getUserName());
		common.setProperty("remote.password", remotee.getPassword());
		common.setProperty("remote.ip", remotee.getIp());
		common.setProperty("remote.port", remotee.getPort());
		common.setProperty("local.dbname", locall.getDbName());
		common.setProperty("local.username", locall.getUserName());
		common.setProperty("local.password", locall.getPassword());
		common.setProperty("local.ip", locall.getIp());
		common.setProperty("local.port", locall.getPort());
	}
	
}
