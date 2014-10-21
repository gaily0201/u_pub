package cn.gaily.pub.test;

import java.io.File;
import java.io.IOException;

import nc.bs.framework.test.AbstractTestCase;
import cn.gaily.pub.util.FileUtil;

public class TestFileUtil extends AbstractTestCase {

	public void test(){
//		try {
//			FileUtil.changeCharcter("E:\\workspace\\ws_uapStudio\\_pub", new String[]{"java"}, "UTF-8", "GBK");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		try {
//			FileUtil.saveBlobFile(new File("E:\\share\\法律文书\\公安文书"), "aj_flws_jcy", 0, 0);
//			FileUtil.saveBlobFile(new File("E:\\share\\法律文书\\公安文书"), "aj_flws_sfj", 0, 0);
//			FileUtil.saveBlobFile(new File("E:\\share\\法律文书\\公安文书"), "aj_flws_fy", 0, 0);
//			FileUtil.saveBlobFile(new File("E:\\share\\法律文书\\公安文书"), "aj_flws_bak", 0, 0);
			FileUtil.saveBlobFile(new File("E:\\share\\法律文书\\扫描件"), "scantable", 0, 0);
			FileUtil.saveBlobFile(new File("E:\\share\\法律文书\\扫描件"), "scantable_lsyj", 0, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
