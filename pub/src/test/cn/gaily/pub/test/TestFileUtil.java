package cn.gaily.pub.test;

import java.io.IOException;

import cn.gaily.pub.util.FileUtil;
import nc.bs.framework.test.AbstractTestCase;

public class TestFileUtil extends AbstractTestCase {

	public void test(){
		try {
			FileUtil.changeCharcter("E:\\workspace\\ws_uapStudio\\_pub", new String[]{"java"}, "UTF-8", "GBK");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
