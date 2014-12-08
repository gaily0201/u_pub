package cn.gaily.pub.test;

import nc.bs.framework.test.AbstractTestCase;
import cn.gaily.pub.util.WordUtil;

public class TestWordUtil extends AbstractTestCase{

	public void test(){
		WordUtil word = new WordUtil();
		word.open("c:\\a.docx");
		
		word.printFile();
	}
}
