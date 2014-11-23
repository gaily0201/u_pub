package cn.gaily.pub.test;

import java.sql.Connection;
import nc.bs.framework.test.AbstractTestCase;
import cn.gaily.pub.trigger.DefaultETLTask;
import cn.gaily.pub.trigger.AbstractETLTask;
import cn.gaily.pub.trigger.TriggerGenerator;
import cn.gaily.simplejdbc.SimpleJdbc;
import cn.gaily.simplejdbc.SimpleDSMgr;

public class TestTriggerGenerator extends AbstractTestCase {

	
	public void test(){
//		createTrigger();
		execTask();
	}
	
	public void execTask(){
		long start = System.currentTimeMillis();
		
		SimpleDSMgr remote = new SimpleDSMgr();
		SimpleDSMgr local = new SimpleDSMgr();
		if(remote.conns.isEmpty()){
			remote.setInitSize(5);
			remote.initDB("orcl", "192.168.1.100", "uap63_test", "1", "1521");
			remote.init();
		}
		if(local.conns.isEmpty()){
			local.setInitSize(5);
			local.initDB("orcl", "192.168.1.100", "uap63", "1", "1521");
			local.init();
		}
		
		AbstractETLTask task = DefaultETLTask.getInstance();
		task.execute(local, remote, "CRPAS_GAJ_AJXX_SACW_B");
		long end = System.currentTimeMillis();
		System.out.println(end-start);
	}
	
	
	public static void createTrigger(){
//		Connection conn = SimpleJdbc.getConnection("uap63_test", "1", "192.168.1.100", "orcl", "1521");
//		TriggerGenerator generator = new TriggerGenerator();
//		generator.setconn(conn);
//		generator.generate("CRPAS_BAOBU_h", null, true);
	}
	
}
