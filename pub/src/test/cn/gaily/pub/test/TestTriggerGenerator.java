package cn.gaily.pub.test;

import java.sql.Connection;

import nc.bs.framework.test.AbstractTestCase;
import cn.gaily.pub.trigger.SimpleDSMgr;
import cn.gaily.pub.trigger.ETLBaseTask;
import cn.gaily.pub.trigger.TriggerGenerator;
import cn.gaily.pub.util.JdbcUtils;

public class TestTriggerGenerator extends AbstractTestCase {

	
	public static void test(){
//		createTrigger();
		execTask();
	}
	
	public static void execTask(){
		long start = System.currentTimeMillis();
		SimpleDSMgr remote = new SimpleDSMgr();
		SimpleDSMgr local = new SimpleDSMgr();
		
		if(remote.conns.isEmpty()){
			remote.setInitSize(5);
			remote.initDB("orcl", "192.168.1.100", "uap63", "1", "1521");
			remote.init();
		}
		if(local.conns.isEmpty()){
			local.setInitSize(5);
			local.initDB("orcl", "192.168.1.100", "uap63_test", "1", "1521");
			local.init();
		}
		
		ETLBaseTask task = new ETLBaseTask();
		task.dealAdd(local, remote, "TEST");
		long end = System.currentTimeMillis();
		System.out.println(end-start);
	}
	
	
	public static void createTrigger(){
		Connection conn = JdbcUtils.getConnection("uap63_TEST", "1", "192.168.1.100", "orcl", "1521");
		TriggerGenerator generator = new TriggerGenerator();
		generator.setconn(conn);
		generator.generate("TEST", null, true);
	}
	
}
