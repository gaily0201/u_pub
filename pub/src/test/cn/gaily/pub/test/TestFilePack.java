package cn.gaily.pub.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import nc.bs.framework.test.AbstractTestCase;
import cn.gaily.pub.util.FilePackage;

public class TestFilePack extends AbstractTestCase {

	public void test(){
		FilePackage age = new FilePackage();
		File file = new File("c:\\aa.pdf");
		try {
//			age.zipFileWithDir(new File[]{file}, "c:\\aa.zip");
			age.unzipFile(new File("c:\\aa.zip"), "c:\\");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
