package cn.gaily.pub.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.framework.test.AbstractTestCase;
import nc.vo.pub.BusinessException;
import cn.gaily.pub.util.ExcelImporter;

public class TestExcelUtil extends AbstractTestCase {

	public void test(){
		File file = new File("c:\\test.xls");
		Map<String,List<String>> map = null;
		try {
			map = ExcelImporter.doImport(file);
			List<String> lists = map.get("Sheet1");
			String value = lists.get(0);
			String[] values = value.split(",");
			for(String s :values){
				System.out.println("<"+s.trim()+">"+"×Ö¶ÎÖµ<"+s.trim()+">");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}
}
