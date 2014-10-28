package cn.gaily.pub.test;

import java.sql.Connection;

import nc.bs.framework.test.AbstractTestCase;
import cn.gaily.pub.trigger.TriggerGenerator;
import cn.gaily.pub.util.JdbcUtils;

public class TestTriggerGenerator extends AbstractTestCase {

	public void test(){
		Connection conn = JdbcUtils.getConnection("uap63_TEST", "1", "192.168.1.100", "orcl", "1521");
		TriggerGenerator generator = new TriggerGenerator();
		generator.setconn(conn);
		generator.generate("TEST", null, true);
	}
}
