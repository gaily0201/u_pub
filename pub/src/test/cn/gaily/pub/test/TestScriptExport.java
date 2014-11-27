package cn.gaily.pub.test;

import java.io.IOException;

import nc.bs.framework.test.AbstractTestCase;
import cn.gaily.pub.script.ScriptExporter;

public class TestScriptExport extends AbstractTestCase {

	
	public void test() throws Exception{
		writeFile();
	}
	
	public void writeFile() throws IOException{
		ScriptExporter export = new ScriptExporter();
		long starttime = System.currentTimeMillis();
		for(int i=0;i<10000;i++){ //2s
			export.writeFile("c:\\testWriteFile.log", "ceshi²âÊÔceshi²âÊÔceshi²âÊÔceshi²âÊÔceshi²âÊÔceshi²âÊÔceshi²âÊÔceshi²âÊÔceshi²âÊÔceshi²âÊÔceshi²âÊÔceshi²âÊÔceshi²âÊÔceshi²âÊÔceshi²âÊÔceshi²âÊÔceshi²âÊÔceshi²âÊÔ", true);
		}
		long endtime = System.currentTimeMillis();
		System.out.println((endtime-starttime)/1000+"s");
	}
}
