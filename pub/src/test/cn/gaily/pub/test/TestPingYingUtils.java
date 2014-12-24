package cn.gaily.pub.test;

import cn.gaily.pub.util.PingyinUtils;
import nc.bs.framework.test.AbstractTestCase;
import net.sourceforge.pinyin4j.PinyinHelper;

public class TestPingYingUtils extends AbstractTestCase{

	
	public void test(){
		System.out.println(PingyinUtils.converterToFirstSpell("中国大陆"));
		System.out.println(PingyinUtils.converterToSpell("中国大陆"));
		System.out.println(PingyinUtils.convertToFirstFullSpell("中国大陆"));
	}
}
