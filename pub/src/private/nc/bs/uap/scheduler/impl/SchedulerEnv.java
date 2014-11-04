/*
 * @(#)SchedulerEnv.java 1.0 2004-10-12
 *
 * Copyright 2005 UFIDA Software Co. Ltd. All rights reserved.
 * UFIDA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.bs.uap.scheduler.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import nc.bs.framework.common.RuntimeEnv;

import nc.bs.uap.scheduler.Messages;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.jcom.xml.XMLUtil;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.uap.scheduler.TaskPriority;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * 读取配置文件，主要包括调度引擎需要进行配置的一些信息。
 * 
 * <DL>
 * <DT><B>Provider:</B></DT>
 * <DD>NC-UAP</DD>
 * </DL>
 * 
 * @author yanglei
 * @since 3.0
 */
public class SchedulerEnv {

	public static final String CONFIG_FILE_SCHE_ENG = "scheduleengine.xml";

	public static final String CONFIG_FILE_DEFAULT_TASKS = "scheduleengine_defaulttasks.xml";

	private static final String AUTOLOADERS_TAG = "autoLoaders";

	private static final String AUTOLOADER_TAG = "autoLoader";

	private static final String AUTOLOADER_FULLNAME_TAG = "classQualifiedName";

	private static final String AUTOLOADER_MODULENAME_TAG = "moduleName";

	private static final String AUTOLOADER_PRIORITY_TAG = "priority";

	private static final String AUTOLOADER_IGNOREDS_TAG = "ignoreDataSource";

	private static final String AUTOLOADER_ENABLED_TAG = "enabled";

	private static final int INVALID_ENVIRONMENT_VALUE = -1;

	/**
	 * 文档对象
	 */
	private Document seEnvironment = null;

	// 调度引擎所用的主配置文件
	private String SEENVIRONMENT_FILE = "";

	SchedulerEnv() {
		init();
	}

	/**
	 * 得到执行者池中所能缓存的最大的执行者数量，缺省为20。
	 * <p>
	 * 创建日期：(2004-9-20 16:52:22)
	 * 
	 * @return int
	 */
	int getMaxExecutors() {
		String nodeValue = getNodeValue("maxExecutors");
		int value = getIntFromString(nodeValue);
		return value == INVALID_ENVIRONMENT_VALUE ? 20 : value;
	}

	/**
	 * JMX连接服务器地址。
	 * 
	 * @return
	 */
	String getJMXConnectorServerURL() {
		String nodeValue = getNodeValue("jmxConnectorServerURL");
		return StringUtil.spaceToNull(nodeValue);
	}

	/**
	 * 得到用户所实现的具体的自动加载工厂完全限定名称和针对该具体工厂的所创建模块名及任务的优先权，由配置文件的0,1,2,3,来转换成
	 * 相应的TaskPriority类型的对象。
	 * 
	 * @return Map
	 */
	Map<String, TaskAutoLoader> getAutoLoaders() {
		return getAutoLoadersMap();
	}

	/**
	 * 得到任务调度器的等待超时时间。
	 * 
	 * @return long
	 */
	public long getSchedulerIdleTimeout() {
		String nodeValue = getNodeValue("schedulerIdleTimeout");
		long value = getLongFromString(nodeValue);
		Messages.log.debug("idle timeout " + value + " " + nodeValue);
		return value == INVALID_ENVIRONMENT_VALUE ? 2000L : value;
	}

	/**
	 * 得到服务器的等待超时时间。
	 * 
	 * @return
	 */
	public long getServerTimeout() {
		String nodeValue = getNodeValue("serverTimeout");
		long value = getLongFromString(nodeValue);
		Messages.log.debug("server timeout " + value + " " + nodeValue);
		return value == INVALID_ENVIRONMENT_VALUE ? 3600000L : value;
	}

	/**
	 * 得到执行者池是否自动收缩，缺省为false。
	 * <p>
	 * 创建日期：(2004-9-20 16:52:22)
	 * 
	 * @return int
	 */
	boolean isTaskExecutorPoolShrink() {
		String nodeValue = getNodeValue("taskExecutorPoolShrink");
		boolean value = getBooleanFromString(nodeValue);
		return value;
	}

	/**
	 * 得到任务执行者的等待超时时间。
	 * 
	 * @return long
	 */
	long getExecutorIdleTimeout() {
		String nodeValue = getNodeValue("executorIdleTimeout");
		long value = getLongFromString(nodeValue);
		return value == INVALID_ENVIRONMENT_VALUE ? 60000L : value;
	}

	/**
	 * 是否进行日志记录。
	 * 
	 * @return boolean true 进行记录，false 不进行记录
	 */
	boolean isLog() {
		String nodeValue = getNodeValue(getLogChildNode("log"));
		boolean value = getBooleanFromString(nodeValue);
		return value;
	}

	/**
	 * 取得日志文件名。
	 * 
	 * @return
	 */
	String getLogFileName() {
		String nodeValue = getNodeValue(getLogChildNode("logFileName"));
		String defaultName = "ScheduleEngine";
		return nodeValue == null ? defaultName : nodeValue;
	}

	/**
	 * 得到日志记录的文件的最大字符数,缺省为1024k个字符。
	 * 
	 * @return long
	 */
	int getLogFileMaxSize() {
		String nodeValue = getNodeValue(getLogChildNode("LogFileMaxSize"));
		int value = getIntFromString(nodeValue);
		return value == INVALID_ENVIRONMENT_VALUE ? 1024 : value;
	}

	/**
	 * 将用户所定义的自动加载工厂加入到调度引擎的配置文件中, 如果已存在该加载工厂,则进行更新
	 * 
	 * @param classFullName
	 *            加载工厂的完全限定名称
	 * @param moduleName
	 *            所属模块名
	 * @param priority
	 *            该加载工厂所加载任务的优先级别
	 */
	void addAutoLoader(TaskAutoLoader tal) throws FileNotFoundException {
		if(seEnvironment==null){
			init();
		}
		// 将该加载工厂相关信息加入到DOM文档
		NodeList autoLoadersList = getNodeList(AUTOLOADERS_TAG);
		Element autoLoadersRoot = null;
		if (autoLoadersList.getLength() > 0) {
			// 在原来数据基础上添加
			autoLoadersRoot = (Element) autoLoadersList.item(0);
			Element autoLoaderNode = getAutoLoaderNode(tal.getClassFullName());
			// 如果已有该加载工厂，则进行更新
			if (null != autoLoaderNode) {
				updateAutoLoaderNode(autoLoaderNode, tal);
				// 写入到文件中,并退出
				writeToFile();
				return;
			}
		} else {
			// 加第一个加载工厂条目时要生成加载工厂的根元素
			Element root = seEnvironment.getDocumentElement();
			// 增加加载工厂的根元素
			autoLoadersRoot = seEnvironment.createElement(AUTOLOADERS_TAG);
			root.appendChild(autoLoadersRoot);
		}
		// 如果是新加的加载工厂，将其加入并写入到文件
		if (autoLoadersRoot != null) {
			appendAutoLoader(autoLoadersRoot, tal);
			// 写入到文件中
			writeToFile();
		}
	}

	/**
	 * 将自动加载工厂的节点的值进行更新。
	 * 
	 * @param autoLoaderNode
	 *            要更新的自动加载工厂节点
	 * @param tal
	 *            自动加载工厂属性
	 */
	private void updateAutoLoaderNode(Element autoLoaderNode, TaskAutoLoader tal) {
		((Element) autoLoaderNode.getElementsByTagName(AUTOLOADER_FULLNAME_TAG)
				.item(0)).getFirstChild().setNodeValue(tal.getClassFullName());
		((Element) autoLoaderNode.getElementsByTagName(
				AUTOLOADER_MODULENAME_TAG).item(0)).getFirstChild()
				.setNodeValue(tal.getModuleName());
		((Element) autoLoaderNode.getElementsByTagName(AUTOLOADER_PRIORITY_TAG)
				.item(0)).getFirstChild().setNodeValue(
				String.valueOf(tal.getPriority()));
		((Element) autoLoaderNode.getElementsByTagName(AUTOLOADER_IGNOREDS_TAG)
				.item(0)).getFirstChild().setNodeValue(
				String.valueOf(tal.isIgnoreDs()));
		((Element) autoLoaderNode.getElementsByTagName(AUTOLOADER_ENABLED_TAG)
				.item(0)).getFirstChild().setNodeValue(
				String.valueOf(tal.isEnabled()));

	}

	/**
	 * 判断是否在配置文件中已经存在一个以classFullName命名的自动加载工厂
	 * 
	 * @return
	 */
	private Element getAutoLoaderNode(String classFullName) {
		// 得到其根节点
		NodeList autoLoadersRoot = getNodeList(AUTOLOADERS_TAG);
		Element autoLoader = null;
		if (autoLoadersRoot.getLength() > 0) {
			Element autoLoadersElement = (Element) autoLoadersRoot.item(0);
			NodeList autoLoaders = getNodeList(autoLoadersElement,
					AUTOLOADER_TAG);
			// the parameter of the autoloader
			String fullName = classFullName;
			// 取出其每个加载工厂的值对，并将其与传出的值对比较
			for (int i = 0; i < autoLoaders.getLength(); i++) {
				Element autoLoaderElement = (Element) autoLoaders.item(i);
				// 完全限定名称
				String existFullName = getAutoLoaderChildValue(
						autoLoaderElement, AUTOLOADER_FULLNAME_TAG);
				if (fullName.trim().equalsIgnoreCase(existFullName.trim())) {
					autoLoader = autoLoaderElement;
					break;
				}
			}
		}
		return autoLoader;
	}

	/**
	 * 将以classFullName和priority所代表的自动加载工厂的节点插入到配置文件中。
	 * 
	 * @param autoLoadersRoot
	 *            自动加载工厂根节点
	 * @param tal
	 *            自动加载工厂
	 * 
	 */
	private void appendAutoLoader(Element autoLoadersRoot, TaskAutoLoader tal) {
		if(seEnvironment==null){
			init();
		}
		// 创建autoLoader元素
		Element autoLoader = seEnvironment.createElement(AUTOLOADER_TAG);
		// 创建fullName
		Element fullNameElement = seEnvironment
				.createElement(AUTOLOADER_FULLNAME_TAG);
		Text fullNameText = seEnvironment
				.createTextNode(tal.getClassFullName());
		fullNameElement.appendChild(fullNameText);
		autoLoader.appendChild(fullNameElement);
		// 创建moduleName
		Element moduleNameElement = seEnvironment
				.createElement(AUTOLOADER_MODULENAME_TAG);
		Text moduleNameText = seEnvironment.createTextNode(tal.getModuleName());
		moduleNameElement.appendChild(moduleNameText);
		autoLoader.appendChild(moduleNameElement);
		// 创建priority
		Element priorityElement = seEnvironment
				.createElement(AUTOLOADER_PRIORITY_TAG);
		Text priorityText = seEnvironment.createTextNode(String.valueOf(tal
				.getPriority()));
		priorityElement.appendChild(priorityText);
		autoLoader.appendChild(priorityElement);
		// 创建ignoreDataSource
		Element ignoreDsElement = seEnvironment
				.createElement(AUTOLOADER_IGNOREDS_TAG);
		Text ignoreDsText = null;
		if (tal.isIgnoreDs())
			ignoreDsText = seEnvironment.createTextNode("true");
		else
			ignoreDsText = seEnvironment.createTextNode("false");
		ignoreDsElement.appendChild(ignoreDsText);
		autoLoader.appendChild(ignoreDsElement);
		// 创建ignoreDataSource
		Element enabledElement = seEnvironment
				.createElement(AUTOLOADER_ENABLED_TAG);
		Text enabledText = null;
		if (tal.isEnabled())
			enabledText = seEnvironment.createTextNode("true");
		else
			enabledText = seEnvironment.createTextNode("false");
		enabledElement.appendChild(enabledText);
		autoLoader.appendChild(enabledElement);

		// 将该加载工厂添加到加载工厂的根元素上
		autoLoadersRoot.appendChild(autoLoader);
	}

	/**
	 * 将该DOM文档内容写入到调度引擎配置文件中
	 */
	private void writeToFile() throws FileNotFoundException {

		PrintWriter out = new PrintWriter(new BufferedOutputStream(
				new FileOutputStream(SEENVIRONMENT_FILE)));
		XMLUtil.printDOMTree(out, seEnvironment, 0);
		out.close();
	}

	/**
	 * 得到所有自动加载工厂的完全限定名称和所加载任务的优先级别的值对
	 * 
	 * @return
	 */
	private Map<String, TaskAutoLoader> getAutoLoadersMap() {
		Map<String, TaskAutoLoader> autoLoadersMap = new HashMap<String, TaskAutoLoader>();
		// 将autoLoader的缺省实现加入到MAP中
		// autoLoadersMap.put("nc.bs.scheduler.impl.DefaultAutoLoader",TaskPriority.GENERAL);
		// 读取配置文件中的加载工厂
		// 得到其根节点
		NodeList autoLoadersRoot = getNodeList(AUTOLOADERS_TAG);
		if (autoLoadersRoot.getLength() > 0) {
			Element autoLoadersElement = (Element) autoLoadersRoot.item(0);
			NodeList autoLoaders = getNodeList(autoLoadersElement,
					AUTOLOADER_TAG);
			String fullName = null;
			String moduleName = null;
			TaskPriority taskPriority = TaskPriority.GENERAL;
			UFBoolean isIgnoreDs = UFBoolean.valueOf(true);
			UFBoolean isEnabled = UFBoolean.valueOf(false);

			// 取出其每个加载工厂的值对，并将其压入到MAP中
			for (int i = 0; i < autoLoaders.getLength(); i++) {
				Element autoLoader = (Element) autoLoaders.item(i);
				// 完全限定名称
				fullName = getAutoLoaderChildValue(autoLoader,
						AUTOLOADER_FULLNAME_TAG);
				// 所属模块名称
				moduleName = getAutoLoaderChildValue(autoLoader,
						AUTOLOADER_MODULENAME_TAG);
				moduleName = StringUtil.spaceToNull(moduleName);
				// 默认模块名为uap。
				if (null == moduleName)
					moduleName = "uap";
				// 优先级别
				String priorityStr = getAutoLoaderChildValue(autoLoader,
						AUTOLOADER_PRIORITY_TAG);
				if (priorityStr != null)
					taskPriority = getTaskPriority(getIntFromString(priorityStr));
				else
					taskPriority = TaskPriority.GENERAL;
				// 是否忽略数据源
				String ignoreDs = getAutoLoaderChildValue(autoLoader,
						AUTOLOADER_IGNOREDS_TAG);
				if (ignoreDs != null)
					isIgnoreDs = UFBoolean.valueOf(ignoreDs);

				// 是否启用
				String enabled = getAutoLoaderChildValue(autoLoader,
						AUTOLOADER_ENABLED_TAG);
				if (enabled != null)
					isEnabled = UFBoolean.valueOf(enabled);

				// put 到Map中
				if (fullName != null) {
					autoLoadersMap.put(fullName, new TaskAutoLoader(fullName,
							moduleName, taskPriority.intValue(), isIgnoreDs
									.booleanValue(), isEnabled.booleanValue()));
					fullName = null;
				}
			}
		}
		// Logger.debug("autoLoadersRoot " +autoLoadersRoot.getLength());
		Messages.log.debug("getAutoLoadersMap " + autoLoadersMap.size());
		return autoLoadersMap;
	}

	/**
	 * 从配置文件的0,1,2,3得到相应的TaskPriority对象，如果是非法输入，则设置为普通 级别
	 * 
	 * @param priority
	 * @return
	 */
	TaskPriority getTaskPriority(int priority) {

		TaskPriority taskPriority = TaskPriority.GENERAL;
		switch (priority) {
			case 0 :
				taskPriority = TaskPriority.LOW;
				break;
			case 1 :
				taskPriority = TaskPriority.GENERAL;
				break;
			case 2 :
				taskPriority = TaskPriority.HIGH;
				break;
			case 3 :
				taskPriority = TaskPriority.HIGHEST;
				break;
			default :
				break;
		}
		return taskPriority;
	}

	/**
	 * 得到调度引擎的加载工厂的子节点
	 * 
	 * @param str
	 *            子节点名称
	 * @return 该子节点
	 */
	private String getAutoLoaderChildValue(Element autoLoader,
			String autoLoaderChildTag) {
		NodeList fullNameNode = autoLoader
				.getElementsByTagName(autoLoaderChildTag);
		if (fullNameNode.getLength() > 0) {
			return getNodeValue(fullNameNode.item(0));
		} else
			return null;
	}

	/**
	 * 得到调度引擎的日志的子节点。
	 * 
	 * @param str
	 *            子节点名称
	 * @return 该子节点
	 */
	private Node getLogChildNode(String logChildNodeName) {
		String logTag = "scheduleEngineLog";
		NodeList logList = getNodeList(logTag);
		Node logNode = null;
		if (logList.getLength() > 0) {
			Element logElement = (Element) logList.item(0);
			NodeList logChildNode = logElement
					.getElementsByTagName(logChildNodeName);
			if (logChildNode.getLength() > 0)
				logNode = logChildNode.item(0);
		}
		return logNode;
	}

	/**
	 * 将字符串转换成相应的布而类型。
	 * 
	 * @param str
	 *            代表该布而类型的字符串
	 * @return
	 */
	private boolean getBooleanFromString(String str) {
		boolean value = false;
		try {
			if (null != str)
				value = Boolean.valueOf(str).booleanValue();
		} catch (NumberFormatException e) {
			value = false;
		}
		return value;
	}

	/**
	 * 将字符串转换成相应的长整数。
	 * 
	 * @param str
	 *            代表该长整数的字符串
	 * @return
	 */
	long getLongFromString(String str) {
		long value = INVALID_ENVIRONMENT_VALUE;
		try {
			if (null != str)
				value = Long.parseLong(str);
			if (value < 0 && value != INVALID_ENVIRONMENT_VALUE)
				value = -value;
		} catch (NumberFormatException e) {
			value = INVALID_ENVIRONMENT_VALUE;
		}
		return value;
	}

	/**
	 * 将字符串转换成相应的整数。
	 * 
	 * @param str
	 *            代表该整数的字符串
	 * @return
	 */
	int getIntFromString(String str) {
		int value = INVALID_ENVIRONMENT_VALUE;
		try {
			if (null != str)
				value = Integer.parseInt(str);
			if (value < 0 && value != INVALID_ENVIRONMENT_VALUE)
				value = -value;
		} catch (NumberFormatException e) {
			value = INVALID_ENVIRONMENT_VALUE;
		}
		return value;
	}

	/**
	 * 得到以该名称所表示的节点列表中的第一个节点的值
	 * 
	 * @param name
	 *            代表一个节点列表
	 * @return 一个节点列表的第一个节点的值
	 */
	private String getNodeValue(String name, int index) {
		NodeList nodeList = getNodeList(name);
		int len = nodeList.getLength();
		String nodeValue = null;
		if (len > 0 && index < len) {
			nodeValue = getNodeValue(nodeList.item(index));
		}
		return nodeValue;
	}

	/**
	 * 得到以该名称所表示的节点列表中的第一个节点的值
	 * 
	 * @param name
	 *            代表一个节点列表
	 * @return 一个节点列表的第一个节点的值
	 */
	private String getNodeValue(String name) {
		return getNodeValue(name, 0);
	}

	/**
	 * 得到以该名称所表示的根节点列表
	 * 
	 * @param name
	 *            代表该节点列表
	 * @return 节点列表
	 */
	private NodeList getNodeList(String name) {
		if(seEnvironment==null){
			init();
		}
		return seEnvironment.getElementsByTagName(name);
	}

	private NodeList getNodeList(Element root, String name) {
		return root.getElementsByTagName(name);
	}

	/**
	 * 得到以该节点的值
	 * 
	 * @param node
	 *            节点
	 * @return 节点的值
	 */
	private String getNodeValue(Node node) {
		if (node != null && node.getFirstChild() != null) {
			return node.getFirstChild().getNodeValue().trim();
		}
		return null;
	}

	/**
	 * 初始化环境。
	 * 
	 */
	private void init() {
		try {
			String ncHome = RuntimeEnv.getInstance().getProperty(
					RuntimeEnv.SERVER_LOCATION_PROPERTY);
			if (null == ncHome)
				ncHome = System.getProperty("user.dir");
			StringBuffer seEnvFile = new StringBuffer(ncHome);
			seEnvFile.append(File.separator);
			seEnvFile.append("ierp");
			seEnvFile.append(File.separator);
			seEnvFile.append("bin");
			seEnvFile.append(File.separator);
			seEnvFile.append(CONFIG_FILE_SCHE_ENG);

			SEENVIRONMENT_FILE = seEnvFile.toString();
			Messages.log.debug("schedule engine config file url: "
					+ SEENVIRONMENT_FILE);
//			seEnvironment = XMLUtil.getDocument(SEENVIRONMENT_FILE);
			seEnvironment = XMLUtil.getDocument("C:\\scheduleengine.xml"); //TODO 目录中有中文读取不出来
		} catch (Exception e) {
			Messages.log.warn(Messages.getString("ConfigFileIOError"), e);

			return;
		}
	}

}