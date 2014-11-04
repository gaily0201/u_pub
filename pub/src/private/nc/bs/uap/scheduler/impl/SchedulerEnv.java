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
 * ��ȡ�����ļ�����Ҫ��������������Ҫ�������õ�һЩ��Ϣ��
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
	 * �ĵ�����
	 */
	private Document seEnvironment = null;

	// �����������õ��������ļ�
	private String SEENVIRONMENT_FILE = "";

	SchedulerEnv() {
		init();
	}

	/**
	 * �õ�ִ���߳������ܻ��������ִ����������ȱʡΪ20��
	 * <p>
	 * �������ڣ�(2004-9-20 16:52:22)
	 * 
	 * @return int
	 */
	int getMaxExecutors() {
		String nodeValue = getNodeValue("maxExecutors");
		int value = getIntFromString(nodeValue);
		return value == INVALID_ENVIRONMENT_VALUE ? 20 : value;
	}

	/**
	 * JMX���ӷ�������ַ��
	 * 
	 * @return
	 */
	String getJMXConnectorServerURL() {
		String nodeValue = getNodeValue("jmxConnectorServerURL");
		return StringUtil.spaceToNull(nodeValue);
	}

	/**
	 * �õ��û���ʵ�ֵľ�����Զ����ع�����ȫ�޶����ƺ���Ըþ��幤����������ģ���������������Ȩ���������ļ���0,1,2,3,��ת����
	 * ��Ӧ��TaskPriority���͵Ķ���
	 * 
	 * @return Map
	 */
	Map<String, TaskAutoLoader> getAutoLoaders() {
		return getAutoLoadersMap();
	}

	/**
	 * �õ�����������ĵȴ���ʱʱ�䡣
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
	 * �õ��������ĵȴ���ʱʱ�䡣
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
	 * �õ�ִ���߳��Ƿ��Զ�������ȱʡΪfalse��
	 * <p>
	 * �������ڣ�(2004-9-20 16:52:22)
	 * 
	 * @return int
	 */
	boolean isTaskExecutorPoolShrink() {
		String nodeValue = getNodeValue("taskExecutorPoolShrink");
		boolean value = getBooleanFromString(nodeValue);
		return value;
	}

	/**
	 * �õ�����ִ���ߵĵȴ���ʱʱ�䡣
	 * 
	 * @return long
	 */
	long getExecutorIdleTimeout() {
		String nodeValue = getNodeValue("executorIdleTimeout");
		long value = getLongFromString(nodeValue);
		return value == INVALID_ENVIRONMENT_VALUE ? 60000L : value;
	}

	/**
	 * �Ƿ������־��¼��
	 * 
	 * @return boolean true ���м�¼��false �����м�¼
	 */
	boolean isLog() {
		String nodeValue = getNodeValue(getLogChildNode("log"));
		boolean value = getBooleanFromString(nodeValue);
		return value;
	}

	/**
	 * ȡ����־�ļ�����
	 * 
	 * @return
	 */
	String getLogFileName() {
		String nodeValue = getNodeValue(getLogChildNode("logFileName"));
		String defaultName = "ScheduleEngine";
		return nodeValue == null ? defaultName : nodeValue;
	}

	/**
	 * �õ���־��¼���ļ�������ַ���,ȱʡΪ1024k���ַ���
	 * 
	 * @return long
	 */
	int getLogFileMaxSize() {
		String nodeValue = getNodeValue(getLogChildNode("LogFileMaxSize"));
		int value = getIntFromString(nodeValue);
		return value == INVALID_ENVIRONMENT_VALUE ? 1024 : value;
	}

	/**
	 * ���û���������Զ����ع������뵽��������������ļ���, ����Ѵ��ڸü��ع���,����и���
	 * 
	 * @param classFullName
	 *            ���ع�������ȫ�޶�����
	 * @param moduleName
	 *            ����ģ����
	 * @param priority
	 *            �ü��ع�����������������ȼ���
	 */
	void addAutoLoader(TaskAutoLoader tal) throws FileNotFoundException {
		if(seEnvironment==null){
			init();
		}
		// ���ü��ع��������Ϣ���뵽DOM�ĵ�
		NodeList autoLoadersList = getNodeList(AUTOLOADERS_TAG);
		Element autoLoadersRoot = null;
		if (autoLoadersList.getLength() > 0) {
			// ��ԭ�����ݻ��������
			autoLoadersRoot = (Element) autoLoadersList.item(0);
			Element autoLoaderNode = getAutoLoaderNode(tal.getClassFullName());
			// ������иü��ع���������и���
			if (null != autoLoaderNode) {
				updateAutoLoaderNode(autoLoaderNode, tal);
				// д�뵽�ļ���,���˳�
				writeToFile();
				return;
			}
		} else {
			// �ӵ�һ�����ع�����ĿʱҪ���ɼ��ع����ĸ�Ԫ��
			Element root = seEnvironment.getDocumentElement();
			// ���Ӽ��ع����ĸ�Ԫ��
			autoLoadersRoot = seEnvironment.createElement(AUTOLOADERS_TAG);
			root.appendChild(autoLoadersRoot);
		}
		// ������¼ӵļ��ع�����������벢д�뵽�ļ�
		if (autoLoadersRoot != null) {
			appendAutoLoader(autoLoadersRoot, tal);
			// д�뵽�ļ���
			writeToFile();
		}
	}

	/**
	 * ���Զ����ع����Ľڵ��ֵ���и��¡�
	 * 
	 * @param autoLoaderNode
	 *            Ҫ���µ��Զ����ع����ڵ�
	 * @param tal
	 *            �Զ����ع�������
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
	 * �ж��Ƿ��������ļ����Ѿ�����һ����classFullName�������Զ����ع���
	 * 
	 * @return
	 */
	private Element getAutoLoaderNode(String classFullName) {
		// �õ�����ڵ�
		NodeList autoLoadersRoot = getNodeList(AUTOLOADERS_TAG);
		Element autoLoader = null;
		if (autoLoadersRoot.getLength() > 0) {
			Element autoLoadersElement = (Element) autoLoadersRoot.item(0);
			NodeList autoLoaders = getNodeList(autoLoadersElement,
					AUTOLOADER_TAG);
			// the parameter of the autoloader
			String fullName = classFullName;
			// ȡ����ÿ�����ع�����ֵ�ԣ��������봫����ֵ�ԱȽ�
			for (int i = 0; i < autoLoaders.getLength(); i++) {
				Element autoLoaderElement = (Element) autoLoaders.item(i);
				// ��ȫ�޶�����
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
	 * ����classFullName��priority��������Զ����ع����Ľڵ���뵽�����ļ��С�
	 * 
	 * @param autoLoadersRoot
	 *            �Զ����ع������ڵ�
	 * @param tal
	 *            �Զ����ع���
	 * 
	 */
	private void appendAutoLoader(Element autoLoadersRoot, TaskAutoLoader tal) {
		if(seEnvironment==null){
			init();
		}
		// ����autoLoaderԪ��
		Element autoLoader = seEnvironment.createElement(AUTOLOADER_TAG);
		// ����fullName
		Element fullNameElement = seEnvironment
				.createElement(AUTOLOADER_FULLNAME_TAG);
		Text fullNameText = seEnvironment
				.createTextNode(tal.getClassFullName());
		fullNameElement.appendChild(fullNameText);
		autoLoader.appendChild(fullNameElement);
		// ����moduleName
		Element moduleNameElement = seEnvironment
				.createElement(AUTOLOADER_MODULENAME_TAG);
		Text moduleNameText = seEnvironment.createTextNode(tal.getModuleName());
		moduleNameElement.appendChild(moduleNameText);
		autoLoader.appendChild(moduleNameElement);
		// ����priority
		Element priorityElement = seEnvironment
				.createElement(AUTOLOADER_PRIORITY_TAG);
		Text priorityText = seEnvironment.createTextNode(String.valueOf(tal
				.getPriority()));
		priorityElement.appendChild(priorityText);
		autoLoader.appendChild(priorityElement);
		// ����ignoreDataSource
		Element ignoreDsElement = seEnvironment
				.createElement(AUTOLOADER_IGNOREDS_TAG);
		Text ignoreDsText = null;
		if (tal.isIgnoreDs())
			ignoreDsText = seEnvironment.createTextNode("true");
		else
			ignoreDsText = seEnvironment.createTextNode("false");
		ignoreDsElement.appendChild(ignoreDsText);
		autoLoader.appendChild(ignoreDsElement);
		// ����ignoreDataSource
		Element enabledElement = seEnvironment
				.createElement(AUTOLOADER_ENABLED_TAG);
		Text enabledText = null;
		if (tal.isEnabled())
			enabledText = seEnvironment.createTextNode("true");
		else
			enabledText = seEnvironment.createTextNode("false");
		enabledElement.appendChild(enabledText);
		autoLoader.appendChild(enabledElement);

		// ���ü��ع�����ӵ����ع����ĸ�Ԫ����
		autoLoadersRoot.appendChild(autoLoader);
	}

	/**
	 * ����DOM�ĵ�����д�뵽�������������ļ���
	 */
	private void writeToFile() throws FileNotFoundException {

		PrintWriter out = new PrintWriter(new BufferedOutputStream(
				new FileOutputStream(SEENVIRONMENT_FILE)));
		XMLUtil.printDOMTree(out, seEnvironment, 0);
		out.close();
	}

	/**
	 * �õ������Զ����ع�������ȫ�޶����ƺ���������������ȼ����ֵ��
	 * 
	 * @return
	 */
	private Map<String, TaskAutoLoader> getAutoLoadersMap() {
		Map<String, TaskAutoLoader> autoLoadersMap = new HashMap<String, TaskAutoLoader>();
		// ��autoLoader��ȱʡʵ�ּ��뵽MAP��
		// autoLoadersMap.put("nc.bs.scheduler.impl.DefaultAutoLoader",TaskPriority.GENERAL);
		// ��ȡ�����ļ��еļ��ع���
		// �õ�����ڵ�
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

			// ȡ����ÿ�����ع�����ֵ�ԣ�������ѹ�뵽MAP��
			for (int i = 0; i < autoLoaders.getLength(); i++) {
				Element autoLoader = (Element) autoLoaders.item(i);
				// ��ȫ�޶�����
				fullName = getAutoLoaderChildValue(autoLoader,
						AUTOLOADER_FULLNAME_TAG);
				// ����ģ������
				moduleName = getAutoLoaderChildValue(autoLoader,
						AUTOLOADER_MODULENAME_TAG);
				moduleName = StringUtil.spaceToNull(moduleName);
				// Ĭ��ģ����Ϊuap��
				if (null == moduleName)
					moduleName = "uap";
				// ���ȼ���
				String priorityStr = getAutoLoaderChildValue(autoLoader,
						AUTOLOADER_PRIORITY_TAG);
				if (priorityStr != null)
					taskPriority = getTaskPriority(getIntFromString(priorityStr));
				else
					taskPriority = TaskPriority.GENERAL;
				// �Ƿ��������Դ
				String ignoreDs = getAutoLoaderChildValue(autoLoader,
						AUTOLOADER_IGNOREDS_TAG);
				if (ignoreDs != null)
					isIgnoreDs = UFBoolean.valueOf(ignoreDs);

				// �Ƿ�����
				String enabled = getAutoLoaderChildValue(autoLoader,
						AUTOLOADER_ENABLED_TAG);
				if (enabled != null)
					isEnabled = UFBoolean.valueOf(enabled);

				// put ��Map��
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
	 * �������ļ���0,1,2,3�õ���Ӧ��TaskPriority��������ǷǷ����룬������Ϊ��ͨ ����
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
	 * �õ���������ļ��ع������ӽڵ�
	 * 
	 * @param str
	 *            �ӽڵ�����
	 * @return ���ӽڵ�
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
	 * �õ������������־���ӽڵ㡣
	 * 
	 * @param str
	 *            �ӽڵ�����
	 * @return ���ӽڵ�
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
	 * ���ַ���ת������Ӧ�Ĳ������͡�
	 * 
	 * @param str
	 *            ����ò������͵��ַ���
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
	 * ���ַ���ת������Ӧ�ĳ�������
	 * 
	 * @param str
	 *            ����ó��������ַ���
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
	 * ���ַ���ת������Ӧ��������
	 * 
	 * @param str
	 *            ������������ַ���
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
	 * �õ��Ը���������ʾ�Ľڵ��б��еĵ�һ���ڵ��ֵ
	 * 
	 * @param name
	 *            ����һ���ڵ��б�
	 * @return һ���ڵ��б�ĵ�һ���ڵ��ֵ
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
	 * �õ��Ը���������ʾ�Ľڵ��б��еĵ�һ���ڵ��ֵ
	 * 
	 * @param name
	 *            ����һ���ڵ��б�
	 * @return һ���ڵ��б�ĵ�һ���ڵ��ֵ
	 */
	private String getNodeValue(String name) {
		return getNodeValue(name, 0);
	}

	/**
	 * �õ��Ը���������ʾ�ĸ��ڵ��б�
	 * 
	 * @param name
	 *            ����ýڵ��б�
	 * @return �ڵ��б�
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
	 * �õ��Ըýڵ��ֵ
	 * 
	 * @param node
	 *            �ڵ�
	 * @return �ڵ��ֵ
	 */
	private String getNodeValue(Node node) {
		if (node != null && node.getFirstChild() != null) {
			return node.getFirstChild().getNodeValue().trim();
		}
		return null;
	}

	/**
	 * ��ʼ��������
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
			seEnvironment = XMLUtil.getDocument("C:\\scheduleengine.xml"); //TODO Ŀ¼�������Ķ�ȡ������
		} catch (Exception e) {
			Messages.log.warn(Messages.getString("ConfigFileIOError"), e);

			return;
		}
	}

}