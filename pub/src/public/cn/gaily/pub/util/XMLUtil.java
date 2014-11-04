package cn.gaily.pub.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.vfs.FileObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.helpers.DefaultHandler;
import common.Logger;

/**
 * <p>Title: XMLUtil</P>
 * <p>Description: XML简单工具</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-11-4
 *  * XML 文件格式如下:
 * <root>
 * 		<heads>
 * 			<head id="1" propertyName="" name="序号"/>
 * 			<head id="2" propertyName="" name="网吧名称"/>
 * 					.
 * 					.
 * 					.
 * 		</heads>
 * 		<list desc="描述">
 * 			<bean>
 * 				<property id="1" value="1">
 * 				</property>
 * 				<property id="2">
 * 					开心网吧
 * 				</property>
 * 						.
 * 						.
 * 						.
 * 			</bean>
 * 		</list>
 * </root> 
 * 
 * 1.如果value只是短字符串可以直接写在value属性中
 * 2.如果value值比较大应该写在<value>之间
 * 
 */
public class XMLUtil {
	private static Logger log = Logger.getLogger(XMLUtil.class);
	
	public static final OutputFormat PRETTY_PRINT_FORMAT = OutputFormat.createPrettyPrint();
	public static final OutputFormat COMPACT_FORMAT = OutputFormat.createCompactFormat();
	private static final String ROOT = "root";
	private static final String HEADS = "heads";
	private static final String HEAD = "head";
	private static final String ID = "id";
	private static final String HEAD_PROPERTY_NAME = "propertyName";
	private static final String HEAD_NAME = "name";
	private static final String LIST = "list";
	private static final String BEAN = "bean";
	private static final String PROPERTY = "property";
	private static final String PROPERTY_VALUE = "value";
	private static final int PROPERTY_VALUE_LIMIT = 20; 
	
	public static <E> Document buildXMLDocument(Map<String, String> headMap,List<E> resultList){
		if(headMap == null){
			throw new IllegalArgumentException("headMap参数不能为空!");
		}
		if(resultList == null){
			throw new IllegalArgumentException("resultList参数不能为空!");
		}
		log.info("exportXML......");
		Document doc = DocumentHelper.createDocument();
		Element root = DocumentHelper.createElement(ROOT);
		Element heads = buildHeadsElement(headMap);
		Element list = buildListElement(headMap.keySet(), resultList);
		root.add(heads);
		root.add(list);
		doc.add(root);
		return doc;
	}
	
	private static Element buildHeadsElement(Map<String, String> headMap){
		Iterator<Entry<String, String>> it = headMap.entrySet().iterator();
		Element heads = DocumentHelper.createElement(HEADS);
		int index = 1;
		while(it.hasNext()){
			Entry<String, String> entry = it.next();
			Element head = DocumentHelper.createElement(HEAD);
			head.addAttribute(ID, String.valueOf(index));
			head.addAttribute(HEAD_PROPERTY_NAME, entry.getKey());
			head.addAttribute(HEAD_NAME,entry.getValue());
			heads.add(head);
			index++;
		}
		return heads;
	}
 	
	private static <E> Element buildListElement(Set<String> propertySet,List<E> resultList){
		Element list = DocumentHelper.createElement(LIST);
		for(E bean : resultList){
			Element beanElement = buildBeanElement(propertySet, bean);
			list.add(beanElement);
		}
		return list;
	}
	
	private static <E> Element buildBeanElement(Set<String> propertySet,E bean){
		Element beanElement = DocumentHelper.createElement(BEAN);
		int id = 1;
		for(String property : propertySet){
			Element proElement = buildPropertyElement(bean, property, id);
			beanElement.add(proElement);
			id++;
		}
		return beanElement;
	}
	
	private static <E> Element buildPropertyElement(E bean,String property,int id){
		Element propertyElement = DocumentHelper.createElement(PROPERTY);
		Object value ="";
		try {
			value = PropertyUtils.getProperty(bean,property);
		} catch (Exception e1) {
			log.error("调用PropertyUtils.getProperty时出错["+property+"]",e1);
		}
		propertyElement.addAttribute(ID, String.valueOf(id));
		if(value == null){
			propertyElement.addText("null");
		} else if (value.toString().length() > PROPERTY_VALUE_LIMIT){
			propertyElement.addText(value.toString());
		} else {
			propertyElement.addAttribute(PROPERTY_VALUE, value.toString());
		}
		return propertyElement;
	}
	
	public static boolean writeXML(String file,Document xmlDoc,OutputFormat format){
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			log.error(file+"文件不存在:", e);
		}
		return writeXML(output, xmlDoc, format);
	}
	
	
	public static boolean writeXML(OutputStream output,Document xmlDoc,OutputFormat format){
		log.info("writeXML......");
		OutputFormat prettyFormat = OutputFormat.createPrettyPrint();
		XMLWriter writer = null;
		boolean success = true;
		try {
			writer = new XMLWriter(output,prettyFormat);
			writer.write(xmlDoc);
			writer.flush();
		} catch (UnsupportedEncodingException e) {
			log.error("输出格式不对:", e);
			success = false;
		} catch (IOException e) {
			log.error("xml输出时发生错误:", e);
			success = false;
		} finally {
			if(writer != null){
				try {
					writer.close();
					// TODO 需要验证xmlDoc对象要不要调用clear方法,清空数据.因为当大数据量时会导致内存溢出
					if(output != null){
						output.close();
					}
					if(xmlDoc != null){
						xmlDoc.clearContent();
					}
					System.gc();	//建议JVM进行垃圾回收
				} catch (IOException e) {
					log.error("output输出流关闭错误:", e);
					success = false;
				}
			}
		}
		log.info("write finished!");
		return success;
	}
	
	 /**
	   * Checks an xml file is well formed.
	   *
	   * @param file
	   *          The file to check
	   * @return true if the file is well formed.
	   */
	  public static final boolean isXMLFileWellFormed( FileObject file ) {
	    boolean retval = false;
	    try {
	      retval = isXMLWellFormed( file.getContent().getInputStream() );
	    } catch ( Exception e ) {
	      throw new RuntimeException( e );
	    }

	    return retval;
	  }

	  /**
	   * Checks an xml string is well formed.
	   *
	   * @param is
	   *          inputstream
	   * @return true if the xml is well formed.
	   */
	  public static final boolean isXMLWellFormed( InputStream is ) {
	    boolean retval = false;
	    try {
	      SAXParserFactory factory = SAXParserFactory.newInstance();

	      // Parse the input.
	      SAXParser saxParser = factory.newSAXParser();
	      saxParser.parse(is, new DefaultHandler());
	      retval = true;
	    } catch ( Exception e ) {
	      throw new RuntimeException( e );
	    }
	    return retval;
	  }
}
