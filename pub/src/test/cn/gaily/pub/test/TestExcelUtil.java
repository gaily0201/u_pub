package cn.gaily.pub.test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import nc.bs.framework.test.AbstractTestCase;
import nc.vo.pub.BusinessException;
import cn.gaily.pub.util.ExcelImporter;

public class TestExcelUtil extends AbstractTestCase {

	public void test(){
		File file = new File("");
		Map<String,List<String>> map = null;
		try {
			map = ExcelImporter.doImport(file);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}
}
