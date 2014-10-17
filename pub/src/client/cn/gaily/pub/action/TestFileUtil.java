package cn.gaily.pub.action;

import java.io.IOException;

import cn.gaily.pub.util.FileUtil;
import nc.bs.framework.test.AbstractTestCase;

public class TestFileUtil extends AbstractTestCase {

	public void test(){
		try {
			FileUtil.changeCharcter("C:\\Users\\ctt\\git\\u_pub\\pub\\src\\public\\cn\\gaily\\pub\\util\\test", 
							new String[]{"java"}, "UTF-8", "GBK");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
	