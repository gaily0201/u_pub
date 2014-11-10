package cn.gaily.pub.test;

import nc.bs.framework.test.AbstractTestCase;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cn.gaily.pub.util.XMLUtil;

public class TestXMLUtil extends AbstractTestCase {

	public void test(){
//		Map<String,String > headMap = new HashMap<String,String>();
//		headMap.put("key1", "value1");
//		headMap.put("key2", "value2");
//		headMap.put("key3", "value3");
//		headMap.put("key4", "value4");
//		
//		List<String> resultList = new ArrayList<String>();
		
//		Document doc = XMLUtil.buildXMLDocument(headMap, resultList);
//		XMLUtil.writeXML("c:\\test.xml", doc, null);
		
		Document doc = DocumentHelper.createDocument();
		Element root = DocumentHelper.createElement("root");
		Element list = DocumentHelper.createElement("list");
		list.addAttribute("desc", "√Ë ˆ");
		Element elem = DocumentHelper.createElement("prop");
		elem.addAttribute("prop1", " Ù–‘1");
		elem.addAttribute("prop2", " Ù–‘2");
		elem.addAttribute("prop3", " Ù–‘3");
		
		list.add(elem);
		root.add(list);
		doc.add(root);
		System.out.println(doc.asXML());
		XMLUtil.writeXML("c:\\test.xml", doc, null);
	}
}
