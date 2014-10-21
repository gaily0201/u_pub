package nc.ui.bd.ref;

/**
 * 表参照－其他参照基类。 创建日期：(2001-8-23 20:26:54) 模型里未处理栏目
 * 
 * @author：张扬
 *  
 */

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.RuntimeEnv;
import nc.bs.logging.Logger;
import nc.ui.bd.mmpub.DataDictionaryReader;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.vo.bd.ref.IFilterStrategy;
import nc.vo.bd.ref.RefColumnDispConvertVO;
import nc.vo.bd.ref.RefQueryResultVO;
import nc.vo.bd.ref.RefQueryVO;
import nc.vo.bd.ref.RefVO_mlang;
import nc.vo.bd.ref.RefcolumnVO;
import nc.vo.bd.ref.ReftableVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.logging.Debug;
import nc.vo.ml.MultiLangContext;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.formulaset.FormulaParseFather;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.sm.nodepower.OrgnizeTypeVO;

public abstract class AbstractRefModel implements IRefModel {

	protected IRefModelHandler modelHandler;

	private IRefMaintenanceHandler refMaintenanceHandler;
	// 是否增加档案启用过滤条件开关
	private boolean isAddEnableStateWherePart = false;

	private boolean isAddEnvWherePart = true;

	private String dataPowerColumn = null;
	// 版本CheckBox名称
	private String versionDataLabelName = null;
	
	private boolean isBlur = false;

	/**
	 * AbstractRefModel 构造子注解。
	 */
	public AbstractRefModel() {
		super();
		try {
			Class c;
			// 加载服务器类。
			if (RuntimeEnv.getInstance().isRunningInServer())
				c = Class.forName("nc.ui.bd.ref.impl.RefModelHandlerForServer");
			// 加载客户端类
			else {
				c = Class.forName("nc.ui.bd.ref.RefModelHandlerForClient");
			}

			Constructor cs = c
					.getConstructor(new Class[] { AbstractRefModel.class });
			modelHandler = (IRefModelHandler) cs.newInstance(this);
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	private String para1 = null;

	private String para2 = null;

	private String para3 = null;

	private ArrayList hiddenFieldList = new ArrayList();

	// V55需求，是否大小写敏感
	private boolean isCaseSensitve = false;//v63默认为不区分大小写,杨小强

	//
	private Vector vecAllColumnNames = null;

	private String pk_org = null;

	// V6权限资源主键
	private String powerResourceID = null;

	// v6权限操作标示
	private String powerOperator = null;

	private String[] filterRefNodeNames = null;

	private String[] m_strFieldCode = null; // 业务表的字段名

	private String[] m_strFieldName = null; // 业务表的字段中文名

	// 业务表字段类型
	private int[] m_intFieldType = null;

	private String m_strPkFieldCode = null;

	private String m_strTableName = null; // 业务表名
	
	private String m_blurQueryTableName = null;//全文检索模糊匹配表名

	private java.lang.String m_strBlurValue = null;

	private String m_strRefTitle = null;

	protected Vector m_vecData = null;

	private int m_iDefaultFieldCount = 2;

	private String m_strOrderPart = null;

	protected java.lang.String m_strPk_corp = null;

	protected String m_strRefCodeField = null;

	protected String m_strRefNameField = null;

	protected Vector m_vecSelectedData = null;

	private String m_strStrPatch = "";

	private String m_strWherePart = null;

	private boolean m_bCacheEnabled = true;
	
	private boolean m_queryFromServer = false;//为导入功能加的接口，改入时设置为true，查询数据走后台

	private Hashtable m_htCodeIndex = null;

	private String m_orgTypeCode = OrgnizeTypeVO.COMPANY_TYPE;

	// 当前参照所在界面的功能节点号
	private String m_fun_code = null;

	// 增加从临时表取数的方式 2004-06-10
	private boolean isFromTempTable = false;

	// 是否包含模糊匹配Sql
	private boolean isIncludeBlurPart = true;

	// sxj 返回值是否包含下级节点
	private boolean isIncludeSub = false;

	private boolean isPKMatch = false;

	// sxj 2003-10-31
	// 匹配数据时，如果匹配不上，是否刷新数据再匹配一次
	private boolean m_isRefreshMatch = true;

	// 是否显示停用数据
	private boolean m_isDisabledDataShow = false;

	// 增加用户可以自定义setPk匹配的列。 2004-09-23
	private String m_matchField = null;

	// 数据匹配
	private HashMap m_matchHM = new HashMap();

	// 支持助记码2003-02-19
	// 支持助记码组 2004-01-12
	private String[] m_mnecodes = null;

	// sxj 2003-11-14
	private String querySql = null;

	// 高级过滤类名
	private String refFilterDlgClaseName = null;

	private String refQueryDlgClaseName = null;

	// sxj
	private Object userPara = null;

	// 参照setpk匹配数据时，是否包含参照WherePart的开关
	private boolean isMatchPkWithWherePart = false;

	// 参照的编辑状态,该状态和界面的可编辑性一致
	private boolean m_isRefEnable = true;

	// 参照对公式解析的支持
	// 格式举例 formula[0][0] ="字段";formula[0][1]="公式内容"
	private String[][] formulas = null;

	// 树表参照查询是否启用(不是高级查询)
	boolean isLocQueryEnable = true;

	// 是否允许选择非末级
	private boolean m_bNotLeafSelectedEnabled = true;

	// isUseDataPower 2002-09-09
	private boolean m_bUserDataPower = true;

	// 动态列接口名字
	private String m_dynamicColClassName = null;

	// sxj 2003-04-10
	// 提供按给定的Pks进行过滤

	private int m_filterStrategy = IFilterStrategy.INSECTION;

	private String[] m_filterPks = null;

	// htDispConvert2002-08-30
	private Hashtable m_htDispConvertor = null;

	// sxj 2003-10-28 动态列启用开关
	private boolean m_isDynamicCol = false;

	private int[] m_iShownColumns = null;

	// 支持助记码多条记录的匹配2003-02-21
	private boolean m_mnecodeInput = false;

	private String[] m_strBlurFields = null;

	private java.lang.String m_strDataSource = null;

	// 动态列名称
	private String[] m_strDynamicFieldNames = null;

	private java.lang.String m_strGroupPart = null;

	private String[] m_strHiddenFieldCode = null;

	private java.lang.String m_strOriginWherePart;

	// 新增参照ID(这里是名称)
	protected String m_strRefNodeName = null;

	// 用户调用动态列接口时，要使用到的参数。
	private Object m_userParameter = null;

	private String refTempDataWherePart = null;

	// 模糊匹配时，默认把code+助记码列（数组）作为匹配的列。该参数决定是否只用助记码列来匹配
	private boolean isOnlyMneFields = false;

	// 增补的wherePart
	private String m_addWherePart = null;

	// 处理特殊列显示转换
	private Object[][] m_columnDispConverter = null;

	// 用户
	private String m_pk_user = null;

	FormulaHandler formulaHandler = null;

	DefFieldsHandler defFieldHandler = null;

	private EventListenerList eListenerList = new EventListenerList();

	private Map eventListMap = new HashMap();

	private transient ChangeEvent changeEvent = null;

	// 参照数据缓存Key前缀字符串
	private String refDataCachekeyPreStr = "REFDATACACHE";

	private DDReaderVO ddReaderVO = null;

	// 对于自定义参照数据权限的支持.数据权限优先按资源ID查询。
	private String resourceID = null;

	private String classResouceID = null;

	public String getClassResouceID() {
		return classResouceID;
	}

	public void setClassResouceID(String classResouceID) {
		this.classResouceID = classResouceID;
	}

	// 停用数据条件
	private String disabledDataWherePart = null;

	// 该条件会根据环境变量变化，同时在setpk时不带此条件
	private String envWherePart = null;

	// 是否要改变关联表的顺序（为缓存）
	private boolean isChangeTableSeq = true;

	// 维护按钮是否可用
	private boolean isMaintainButtonEnabled = true;

	// 记录详细信息显示组件
	private String refCardInfoComponentImplClass = null;

	// 参照显示字段
	private String refShowNameField = null;

	// 自定义项字段,必须是表名.字段名
	private String[] defFields = null;

	// 对齐方式Map
	private Map<String, Integer> alignMap = null;

	// 界面控制自定义组件名称,该实现类必须继承JComponent，且包含AbstractRefMOdel的构造子
	private String uiControlComponentClassName = null;

	// 集团
	private String pk_group = null;
	// 是否多语种名称参照
	private boolean isMutilLangNameRef = true;

	private Map<String, AbstractRefModel> filterRefMap = null;
	// 历史版本数据
	private boolean isHistoryVersionDataShow = false;

	private boolean isHistoryVersionRef = false;
	
	//搜索全部：全包含的开关，如果为false，则使用左包含
	private boolean isAllMatching = true;

	private RefQueryVO queryVO = new RefQueryVO();
	// 手工输入时要加入的过滤条件，该条件只对用Sql语句匹配数据的参照，例如树表型参照。
	// v60新增，只要是物料档案多版本参照，在手工输入时只选中最新版本。
	private String inputMatchWherePart = null;
	// 权限动作编码
	private String dataPowerOperation_code = IRefConst.DATAPOWEROPERATION_CODE;
	// 快捷输入开关
	private boolean isShortCutInput = true;

	public boolean isShortCutInput() {
		return isShortCutInput;
	}

	public void setShortCutInput(boolean isShortCutInput) {
		this.isShortCutInput = isShortCutInput;
	}

	public String getDataPowerOperation_code() {
		return dataPowerOperation_code;
	}

	public void setDataPowerOperation_code(String dataPowerOperation_code) {
		if (dataPowerOperation_code == null) {
			this.dataPowerOperation_code = IRefConst.DATAPOWEROPERATION_CODE;
		} else {
			this.dataPowerOperation_code = dataPowerOperation_code;
		}

	}

	public String getInputMatchWherePart() {
		return inputMatchWherePart;
	}

	public void setInputMatchWherePart(String inputMatchWherePart) {
		this.inputMatchWherePart = inputMatchWherePart;
	}

	public void setFilterRefMap(Map<String, AbstractRefModel> filterRefMap) {
		this.filterRefMap = filterRefMap;
	}

	public Map<String, AbstractRefModel> getFilterRefMap() {

		return filterRefMap;
	}

	public AbstractRefModel getFilterRefModel(String refNodeName) {
		if (getFilterRefMap() == null) {
			return null;
		}
		return getFilterRefMap().get(refNodeName);

	}

	public boolean isMutilLangNameRef() {
		return isMutilLangNameRef;
	}

	public void setMutilLangNameRef(boolean isMutilLangNameRef) {
		this.isMutilLangNameRef = isMutilLangNameRef;
	}

	private class DDReaderVO {
		String tableName = null;

		String[] fieldNames = null;

		/**
		 * @return 返回 fieldNames。
		 */
		public String[] getFieldNames() {
			return fieldNames;
		}

		/**
		 * @param fieldNames
		 *            要设置的 fieldNames。
		 */
		public void setFieldNames(String[] fieldNames) {
			this.fieldNames = fieldNames;
		}

		/**
		 * @return 返回 tableName。
		 */
		public String getTableName() {
			return tableName;
		}

		/**
		 * @param tableName
		 *            要设置的 tableName。
		 */
		public void setTableName(String tableName) {
			this.tableName = tableName;
		}
	}

	/**
	 * @return 返回 m_columnDispConverterClass。
	 */
	public Object[][] getColumnDispConverter() {
		return m_columnDispConverter;
	}

	/**
	 * @param dispConverterClass
	 *            要设置的 m_columnDispConverterClass。
	 */
	public void setColumnDispConverter(Object[][] dispConverter) {
		m_columnDispConverter = dispConverter;
	}

	/**
	 * 增加where子句 创建日期：(2001-8-16 12:42:02)
	 * 
	 * @param newWherePart
	 *            java.lang.String
	 */
	public void addWherePart(String newWherePart) {
		m_addWherePart = newWherePart;

		// clearData();
		resetSelectedData_WhenDataChanged();
	}

	/**
	 * 创建日期：(2004-9-23 9:03:51)
	 * 
	 * @param newM_matchField
	 *            java.lang.String 自定义的列。用SetPk方法设的值在该列中匹配。 不建议使用。
	 */
	public void setMatchField(java.lang.String newM_matchField) {
		m_matchField = newM_matchField;
	}

	/**
	 * 此处插入方法说明。
	 * 
	 * /** 清除数据及缓存。 创建日期：(2001-8-23 21:01:00)
	 */
	public void clearData() {

		clearModelData();
		clearCacheData();

	}

	/**
	 * 删除参照缓存数据
	 */
	public void clearCacheData() {
		modelHandler.clearCacheData();
	}

	protected void clearModelData() {
		m_vecData = null;
		setSelectedData(null);

	}

	protected void clearDataPowerCache() {
		modelHandler.clearDataPowerCache();
	}

	/**
	 * 将一行数据转化为VO,如不使用VO可以虚实现。 创建日期：(2001-8-13 16:34:11)
	 * 
	 * @return nc.vo.pub.ValueObject
	 * @param vData
	 *            java.util.Vector
	 */
	public nc.vo.pub.ValueObject convertToVO(java.util.Vector vData) {
		return null;
	}

	/**
	 * 将多行数据转化为VO数组。 创建日期：(2001-8-13 16:34:11)
	 * 
	 * @return nc.vo.pub.ValueObject[]
	 * @param vData
	 *            java.util.Vector
	 */
	public nc.vo.pub.ValueObject[] convertToVOs(java.util.Vector vData) {
		if (vData == null || vData.size() == 0)
			return null;
		nc.vo.pub.ValueObject[] vos = new nc.vo.pub.ValueObject[vData.size()];
		for (int i = 0; i < vData.size(); i++) {
			Vector vRecord = (Vector) vData.elementAt(i);
			vos[i] = convertToVO(vRecord);
		}
		return vos;
	}

	/**
	 * 模糊字段值。 创建日期：(2001-8-17 11:17:42)
	 * 
	 * @return java.lang.String
	 */
	public String getBlurValue() {
		return m_strBlurValue;
	}

	/**
	 * 获取缓存或数据库表中的参照数据－－二维Vector。
	 * <p>
	 * <strong>最后修改人：sxj</strong>
	 * <p>
	 * <strong>最后修改日期：2006-7-12</strong>
	 * <p>
	 * 
	 * @param
	 * @return Vector
	 * @exception BusinessException
	 * @since NC5.0
	 */
	public final Vector getRefData() {
		return modelHandler.getRefData();
	}

	/**
	 * 获取参照数据－－二维Vector。 自定义参照可以覆盖
	 * 
	 * @return java.util.Vector
	 */

	public java.util.Vector getData() {

		String sql = getRefSql();

		Vector v = null;
		if (isCacheEnabled()) {
			/** 从缓存读数据 */
			v = modelHandler.getFromCache(getRefDataCacheKey(),
					getRefCacheSqlKey());
		}

		if (v == null) {

			// 从数据库读--也可以在此定制数据

			try {

				if (isFromTempTable()) {
					v = modelHandler.queryRefDataFromTemplateTable(sql);
				} else {
					// 同时读取参照栏目数据
					v = getQueryResultVO();
				}

			} catch (Exception e) {
				Debug.debug(e.getMessage(), e);
			}

		}

		if (v != null && isCacheEnabled()) {
			/** 加入到缓存中 */
			modelHandler.putToCache(getRefDataCacheKey(), getRefCacheSqlKey(), v);
		}

		m_vecData = v;
		return m_vecData;
	}

	public void putToCache(Vector v) {
		if (v != null && isCacheEnabled()) {
			/** 加入到缓存中 */
			modelHandler.putToCache(getRefDataCacheKey(), getRefCacheSqlKey(),
					v);
		}
	}

	public Vector getConvertedData1(boolean isDataFromCache, Vector v,
			boolean isDefConverted) {
		return getConvertedData(isDataFromCache, v, isDefConverted);
	}

	/**
	 * <p>
	 * <strong>最后修改人：sxj</strong>
	 * <p>
	 * <strong>最后修改日期：2006-7-12</strong>
	 * <p>
	 * 
	 * @param
	 * @return void
	 * @exception BusinessException
	 * @since NC5.0
	 */
	protected Vector getConvertedData(boolean isDataFromCache, Vector v,
			boolean isDefConverted) {
		if (v == null) {
			return v;
		}
		Vector vecData = v;

		// 与总账协商的结果,按传入Pks过滤
//		if (vecData != null && vecData.size() > 0) {
//
//			vecData = RefPubUtil.getFilterPKsVector(vecData, this);
//
//		}
		// 多语言处理
		// 缓存中的数据已经处理过了.
		if (!isDataFromCache) {
			setMlangValues(vecData, getRefVO_mlang());
			if (getFormulas() != null) {
				getFormulaHandler().setFormulaValues(vecData, getFormulas());
			}
			// 统计型自定义项pk-->name
			if (isDefConverted) {
				getDefFieldHandler().setDefFieldValues(vecData);
			}

		}
		// 特殊处理结束
		// try to convert
		handleDispConvertor(vecData);
		// end converts
		// 处理特殊显示列。
		if (getColumnDispConverter() != null) {
			setColDispValues(vecData, getColumnDispConverter());
		}
		return vecData;
	}

	/**
	 * 枚举类型显示值的转换
	 * <p>
	 * <strong>最后修改人：sxj</strong>
	 * <p>
	 * <strong>最后修改日期：2006-3-9</strong>
	 * <p>
	 * 
	 * @param
	 * @return void
	 * @exception
	 * @since NC5.0
	 */
	private void handleDispConvertor(Vector v) {
		if (getDispConvertor() != null) {
			Hashtable convert = getDispConvertor();
			Enumeration e = convert.keys();
			while (e.hasMoreElements()) {
				String convertColumn = (String) e.nextElement();
				Hashtable conv = (Hashtable) convert.get(convertColumn);
				Integer Idx = (Integer) getHtCodeIndex().get(convertColumn);
				if (Idx == null || Idx.intValue() < 0)
					continue;
				int idx = Idx.intValue();
				if (v != null) {
					int rows = v.size();
					for (int i = 0; i < rows; i++) {
						Vector vRow = (Vector) v.elementAt(i);
						if (vRow != null) {
							Object oldValue = vRow.elementAt(idx);
							if (oldValue == null)
								continue;
							Object newValue = conv.get(oldValue.toString());
							if (newValue != null) {
								vRow.setElementAt(newValue, idx);
								// 如果需求，可以转换为原值，目前先保持翻译后的值
								// Object obj =
								// getRefValueVO(vRow.elementAt(idx),
								// newValue);
								// vRow.setElementAt(obj, idx);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 默认显示字段中的显示字段数----表示显示前几个字段
	 */
	public int getDefaultFieldCount() {
		return m_iDefaultFieldCount;
	}

	/**
	 * 得到一个字段在所有字段中的下标。 创建日期：(2001-8-16 15:39:23)
	 * 
	 * @return int
	 * @param fieldList
	 *            java.lang.String[]
	 * @param field
	 *            java.lang.String
	 */
	public int getFieldIndex(String field) {

		if (field == null || field.trim().length() == 0
				|| getHtCodeIndex() == null || getHtCodeIndex().size() == 0)
			return -1;
		Object o = getHtCodeIndex().get(field.trim());
		if (o == null) {
			// 加入动态列
			int index = m_htCodeIndex.size();
			if (isDynamicCol() && getDynamicFieldNames() != null) {
				for (int i = 0; i < getDynamicFieldNames().length; i++) {
					m_htCodeIndex.put(getDynamicFieldNames()[i].trim(),Integer.valueOf(index + i));
				}
			}
			o = getHtCodeIndex().get(field.trim());
		}

		return (o == null) ? -1 : ((Integer) o).intValue();
	}

	/**
	 * 此处插入方法说明。 创建日期：(01-6-17 18:35:14)
	 * 
	 * @return java.util.Hashtable
	 */
	public Hashtable getHtCodeIndex() {
		if (m_htCodeIndex == null || m_htCodeIndex.size() == 0) {
			m_htCodeIndex = new Hashtable();
			if (getFieldCode() != null)
				for (int i = 0; i < getFieldCode().length; i++) {
					m_htCodeIndex.put(getFieldCode()[i].trim(), Integer.valueOf(i));
				}

			if (getHiddenFieldCode() != null) {
				int index = 0;
				if (getFieldCode() != null) {
					index = getFieldCode().length;
				}
				for (int i = 0; i < getHiddenFieldCode().length; i++) {
					if (getHiddenFieldCode()[i] != null) {
						m_htCodeIndex.put(getHiddenFieldCode()[i].trim(),
								Integer.valueOf(index + i));
					} else {
						Logger.debug("Waring: The RefModel has some errors.");
					}
				}
			}

		}
		return m_htCodeIndex;
	}

	/**
	 * Order子句。
	 * 
	 * @return java.lang.String
	 */
	public String getOrderPart() {
		if (m_strOrderPart == null && getFieldCode() != null
				&& getFieldCode().length > 0)
			m_strOrderPart = getFieldCode()[0];
		return m_strOrderPart;
	}

	/**
	 * 得到公司主键－－默认参照使用。 创建日期：(2001-8-17 11:17:03)
	 * 
	 * @return java.lang.String
	 */
	public String getPk_corp() {
		if (m_strPk_corp != null)
			return m_strPk_corp;

		return modelHandler.getPk_corp();

	}

	/**
	 * 返回值－－主键字段
	 * 
	 * @return java.lang.String
	 */
	public String getPkValue() {
		String strValue = null;
		Object objValue = getValue(getPkFieldCode());
		if (objValue != null) {
			strValue = objValue.toString();
		}

		return strValue;
	}

	/**
	 * 返回值数组－－主键字段
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String[] getPkValues() {

		Object[] oDatas = getValues(getPkFieldCode());
		String[] sDatas = objs2Strs(oDatas);
		return sDatas;
	}

	/**
	 * 参照编码字段。 创建日期：(2001-8-13 16:19:24)
	 * 
	 * @return java.lang.String
	 */
	public String getRefCodeField() {
		if (m_strRefCodeField == null && getFieldCode() != null
				&& getFieldCode().length > 0)
			m_strRefCodeField = getFieldCode()[0];
		return m_strRefCodeField;
	}

	/**
	 * 返回值－－编码字段 创建日期：(2001-8-13 16:19:24)
	 * 
	 * @return java.lang.String
	 */
	public String getRefCodeValue() {

		String strValue = null;
		String[] strValues = getRefCodeValues();
		if (strValues != null && strValues.length > 0) {
			strValue = strValues[0];
		}
		return strValue;
	}

	/**
	 * 返回值数组－－编码字段 创建日期：(2001-8-13 16:19:24)
	 * 
	 * @return java.lang.String[]
	 */
	public java.lang.String[] getRefCodeValues() {
		Object[] oDatas = getValues(getRefCodeField());
		String[] sDatas = objs2Strs(oDatas);
		return sDatas;
	}

	protected String[] objs2Strs(Object[] oDatas) {
		if (oDatas == null)
			return null;
		String[] sDatas = new String[oDatas.length];
		for (int i = 0; i < oDatas.length; i++) {
			if (oDatas[i] == null)
				sDatas[i] = null;
			else
				sDatas[i] = oDatas[i].toString().trim();
		}
		return sDatas;
	}

	/**
	 * 参照名称字段。 创建日期：(2001-8-13 16:19:24)
	 * 
	 * @return java.lang.String
	 */
	public String getRefNameField() {
		if (m_strRefNameField == null && getFieldCode() != null
				&& getFieldCode().length > 1)
			m_strRefNameField = getFieldCode()[1];
		return m_strRefNameField;
	}

	/**
	 * 返回值－名称字段 创建日期：(2001-8-13 16:19:24)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getRefNameValue() {

		String strValue = null;
		String[] strValues = getRefNameValues();
		if (strValues != null && strValues.length > 0) {
			strValue = strValues[0];
		}
		return strValue;
	}

	/**
	 * 返回值－名称字段 创建日期：(2001-8-13 16:19:24)
	 * 
	 * @return java.lang.String[]
	 */
	public java.lang.String[] getRefNameValues() {
		// 名称列，取翻译后的值
		Object[] oDatas = getValues(getRefNameField(), getSelectedData(), false);

		String[] sDatas = objs2Strs(oDatas);

		return sDatas;
	}

	public java.lang.String[] getRefShowNameValues() {
		// 名称列，取翻译后的值
		Object[] oDatas = getValues(getRefShowNameField(), getSelectedData(),
				false);

		return objs2Strs(oDatas);
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-8-15 17:30:17)
	 * 
	 * @return java.lang.String
	 */
	public String getRefSql() {
		// todo
		String sql = null;
		if (isLargeDataRef() && canUseDB())
			sql = getSpecialSql(getStrPatch(), getFieldCode(),
					getHiddenFieldCode(), getTableName(), getWherePart(),
					getGroupPart(), getOrderPart());

		else
			sql = getSql(getStrPatch(), getFieldCode(), getHiddenFieldCode(),
					getTableName(), getWherePart(), getGroupPart(),
					getOrderPart());
		return sql;
	}

	/**
	 * 返回选择数据－－二维数组。 创建日期：(2001-8-23 19:10:29)
	 * 
	 * @return java.util.Vector
	 */
	public java.util.Vector getSelectedData() {
		return m_vecSelectedData;
	}

	/**
	 * DISTINCT子句
	 * 
	 * @return java.lang.String
	 */
	public String getStrPatch() {
		return m_strStrPatch;
	}

	/**
	 * 返回值－根据参数字段 创建日期：(2001-8-13 16:19:24)
	 * 
	 * @return java.lang.Object
	 */
	public Object getValue(String field) {
		Object[] values = getValues(field);

		Object value = null;

		if (values != null && values.length > 0) {
			value = values[0];
		}

		return value;
	}

	/**
	 * 返回值数组－根据参数字段 创建日期：(2001-8-13 16:19:24)
	 * 
	 * @return java.lang.Object[]
	 */
	public java.lang.Object[] getValues(String field) {

		return getValues(field, true);
	}

	/**
	 * 取参照选中记录给定列的值
	 * 
	 * <p>
	 * <strong>最后修改人：sxj</strong>
	 * <p>
	 * <strong>最后修改日期：2006-11-16</strong>
	 * <p>
	 * 
	 * @param field
	 *            列
	 * @param 是否是原值
	 *            。列可能已被翻译（例如定义了公式，定义了多语翻译，定义了枚举类型的对应值）
	 * 
	 * @return java.lang.Object[]
	 * @exception BusinessException
	 * @since NC5.0
	 */
	public java.lang.Object[] getValues(String field, boolean isOriginValue) {

		return getValues(field, getSelectedData(), isOriginValue);
	}

	/**
	 * 
	 */
	public java.lang.Object[] getValues(String field, Vector datas) {

		return getValues(field, datas, true);
	}

	protected java.lang.Object[] getValues(String field, Vector datas,
			boolean isOriginValue) {
		int col = getFieldIndex(field);
		if (datas == null || datas.size() == 0)
			return null;
		if (col < 0) {
			throw new RuntimeException("This Attribute is not in the Ref ");
		}
		Vector vRecord = null;
		Object[] oData = new Object[datas.size()];
		for (int i = 0; i < datas.size(); i++) {
			vRecord = (Vector) datas.elementAt(i);
			if (vRecord == null || vRecord.size() == 0 || col >= vRecord.size())
				oData[i] = null;
			else {
				oData[i] = getValueOfRefvalueVO(vRecord.elementAt(col),
						isOriginValue);
			}
		}
		return oData;
	}

	private Object getValueOfRefvalueVO(Object obj, boolean isOriginValue) {
		Object value = obj;
		if (obj instanceof RefValueVO && isOriginValue) {
			value = ((RefValueVO) obj).getOriginValue();
		}
		/**
		 * V55 数据库中查出的原始类型为BigDecimal，为NC处理简便，参照对外统一转换为UFDouble
		 * 
		 */

		value = RefPubUtil.bigDecimal2UFDouble(value);

		return value;

	}

	/**
	 * 获取已经读出的参照数据－－二维Vector。 创建日期：(2001-8-23 18:39:24)
	 * 
	 * @return java.util.Vector
	 */
	public java.util.Vector getVecData() {
		return m_vecData;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-8-25 12:06:11)
	 * 
	 * @return nc.vo.pub.ValueObject
	 */
	public nc.vo.pub.ValueObject getVO() {
		if (getSelectedData() != null && getSelectedData().size() > 0)
			return convertToVO(((Vector) getSelectedData().elementAt(0)));
		else
			return null;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-8-25 12:06:11)
	 * 
	 * @return nc.vo.pub.ValueObject
	 */
	public nc.vo.pub.ValueObject[] getVOs() {
		return convertToVOs(getSelectedData());
	}

	/**
	 * getWherePart 方法注解。
	 */
	public String getWherePart() {
		return m_strWherePart;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-8-23 21:03:06)
	 * 
	 * @return boolean
	 */
	public boolean isCacheEnabled() {
		return m_bCacheEnabled;
	}

	/**
	 * 匹配界面输入数据－－选择数据也改变了。 创建日期：(2001-8-23 18:39:24)
	 * 
	 * @return java.util.Vector
	 */
	public Vector matchBlurData(String strBlurValue) {
		Vector v = null;
		if (strBlurValue != null && strBlurValue.trim().length() > 0) {
			v = matchData(getBlurFields(), new String[] { strBlurValue });
			// 如果是模糊查询的结果或者是编码等多条匹配，处理叶子节点是否可以选中
			// 用于树形参照通过模糊匹配变成表型，过滤掉非叶子节点

			if (!isNotLeafSelectedEnabled()
					&& (!RefPubUtil.isNull(strBlurValue) || isMnecodeInput())) {

				v = filterNotLeafNode(v);
			}

		}
		// 清空已选择数据
		setSelectedData(v);
		return v;

	}

	/**
	 * 获取匹配数据 匹配成功 数据－>m_vecSelectedData 匹配不成功 null－>m_vecSelectedData
	 * 创建日期：(2001-8-23 18:39:24)
	 * 
	 * @return java.util.Vector sxj 修改如果value= null or value.length()=0
	 *         不做匹配,提高效率，防止重复reloadData()
	 */
	public Vector matchData(String field, String value) {
		return matchData(new String[] { field }, new String[] { value });
	}

	/**
	 * 匹配主键数据－－选择数据也改变了。 创建日期：(2001-8-23 18:39:24)
	 * 
	 * @return java.util.Vector sxj 2004-06-08 如果是用Pk 进行匹配，则在最大集内进行匹配。 根据
	 *         getMatchField()提供的列进行匹配
	 */
	public Vector matchPkData(String strPkValue) {

		if (strPkValue == null || strPkValue.trim().length() == 0) {
			setSelectedData(null);
			return null;

		}
		return matchPkData(new String[] { strPkValue });
	}

	/**
	 * 重新载入数据。 1.使用缓存手动调用如刷新按钮。 创建日期：(2001-8-23 21:14:19)
	 * 
	 * @return java.util.Vector
	 */
	public java.util.Vector reloadData() {

		reloadData1();
		return getRefData();
	}

	// 重载数据前的处理
	public void reloadData1() {
		// 刷新前已选择的数据
		String[] selectedPKs = getPkValues();

		clearData();
		// 清数据权限缓存
		clearDataPowerCache();

		modelHandler.getLRUMap().clear();
		
		modelHandler.fireDBCache();
		
		// 刷新后重新匹配数据。
		if (selectedPKs != null && selectedPKs.length > 0) {
			matchPkData(selectedPKs);
			fireChange();
		}
		
	}

	/**
	 * 设置模糊值。 创建日期：(2001-8-17 12:57:37)
	 */
	public void setBlurValue(String strBlurValue) {
		m_strBlurValue = strBlurValue;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-8-23 21:03:06)
	 */
	public void setCacheEnabled(boolean cacheEnabled) {
		m_bCacheEnabled = cacheEnabled;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-8-24 10:32:33)
	 * 
	 * @param vData
	 *            java.util.Vector
	 */
	public void setData(Vector vData) {
		m_vecData = vData;
		// 默认参照支持自定义数据
		if (isCacheEnabled()) {
			String sql = getRefSql();
			modelHandler.putToCache(getRefDataCacheKey(), sql, vData);
		}
	}

	/**
	 * 默认显示字段中的显示字段数----表示显示前几个字段
	 */
	public void setDefaultFieldCount(int iDefaultFieldCount) {
		m_iDefaultFieldCount = iDefaultFieldCount;
	}

	/**
	 * Order子句。
	 */
	public void setOrderPart(String strOrderPart) {
		m_strOrderPart = strOrderPart;
	}

	/**
	 * 设置公司主键－－默认参照使用。 自定义参照不支持动态改变环境变量动态改变WherePart
	 * 
	 * @return java.lang.String
	 * @deprecated since V6 请用 setPk_org(String pk_org)
	 */
	public void setPk_corp(String strPk_corp) {
		// if (strPk_corp == null) {
		// return;
		// }
		// 切换公司，主体账簿主键也会改变（对于会计科目参照等主体账簿）
		// m_pk_glOrgBook = null;
		// 自定义参照，用字符串替换算法来动态改变Wherepart,对于系统默认参照
		// setPk_corpForDefRef(strPk_corp, m_strPk_corp);
		// m_strPk_corp = strPk_corp;
		// 系统默认参照
		// 重置WherePart,也可能有其他信息也要改变，
		// resetWherePart();
	}

	/**
	 * 设置参照编码字段。 创建日期：(2001-8-13 16:19:24)
	 */
	public void setRefCodeField(String strRefCodeField) {
		m_strRefCodeField = strRefCodeField;
	}

	/**
	 * 设置参照名称字段。 创建日期：(2001-8-13 16:19:24)
	 */
	public void setRefNameField(String strRefNameField) {
		m_strRefNameField = strRefNameField;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-8-25 18:58:00)
	 * 
	 * @param vecSelectedData
	 *            java.util.Vector
	 */
	public void setSelectedData(Vector vecSelectedData) {
		m_vecSelectedData = vecSelectedData;
	}

	public void setSelectedDataAndConvertData(Vector vecSelectedData) {

		if (vecSelectedData == null) {
			setSelectedData(vecSelectedData);
			return;
		}
		Vector matchVec = getConvertedData(false, vecSelectedData, false);
		setSelectedData(matchVec);
		putLruMap(matchVec);
	}

	/**
	 * 此处插入方法说明。 创建日期：(01-6-19 21:15:22)
	 * 
	 * @param newStrPatch
	 *            java.lang.String
	 */
	public void setStrPatch(java.lang.String newStrPatch) {
		m_strStrPatch = newStrPatch;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-8-16 12:42:02)
	 * 
	 * @param newWherePart
	 *            java.lang.String
	 */
	public void setWherePart(String newWherePart) {

		setWherePart(newWherePart, true);

	}

	public void setWherePart(String newWherePart, boolean isResetSelectedData) {

		if (newWherePart != null) {
			newWherePart = newWherePart.trim();
			int index = newWherePart.toLowerCase().indexOf("where ");
			if (index == 0)
				newWherePart = newWherePart.substring(index + 5, newWherePart
						.length());
		}
		if (m_strWherePart != null && newWherePart != null
				&& m_strWherePart.equalsIgnoreCase(newWherePart))// 如果值相等就不做resetSelectedData_WhenDataChanged
			return;
		m_strWherePart = newWherePart;
		// 同时修改原始where
		m_strOriginWherePart = m_strWherePart;
		if (isResetSelectedData)
			resetSelectedData_WhenDataChanged();

	}

	/**
	 * 当Where 条件改变后，清空m_vecData，重新匹配已经设置的pks.
	 */
	private void resetSelectedData_WhenDataChanged() {

		String[] selectedPKs = getPkValues();
		// 清数据。
		// clearModelData();
		clearData();

		if (selectedPKs != null && selectedPKs.length > 0) {
			matchPkData(selectedPKs);
		}

		Vector selectedData = getSelectedData();

		setSelectedData(selectedData);
		// 通知UIRefPane,刷新界面
		fireChange();
	}

	/**
	 * java.util.Vector 过滤非末级节点
	 */
	public Vector filterNotLeafNode(Vector vec) {
		return vec;
	}

	/**
	 * 模糊字段名。 创建日期：(01-4-4 0:57:23)
	 * 
	 * @return java.lang.String
	 */
	public String[] getBlurFields() {

		if (m_strBlurFields == null || m_strBlurFields.length == 0) {

			if (isOnlyMneFields()) {
				return m_strBlurFields = getMnecodes();
			} else if (getFieldCode() != null) {
				ArrayList al = new ArrayList();
				int len = getFieldCode().length;

				al.add(getRefCodeField());
				if (len >= 2) {
					al.add(getRefNameField());
					if (isMutilLangNameRef() && getLangStrSuffix() != null) {
						al.add(getRefNameField() + getLangStrSuffix());
					}
				}

				if (getMnecodes() != null) {
					for (int i = 0; i < getMnecodes().length; i++) {
						if (al.contains(getMnecodes()[i])) {
							continue;
						}
						al.add(getMnecodes()[i]);
					}
				}
				m_strBlurFields = new String[al.size()];
				al.toArray(m_strBlurFields);

			}
		}

		return m_strBlurFields;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-11-9 8:54:00)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDataSource() {
		return m_strDataSource;
	}

	/**
	 * 用于参照内容转换的影射表 如attrib1属性1－上海 2－南京3－北京 Hashtable conv=new Hashtable();
	 * Hashtable contents=new Hashtable(); contents.put("1","上海");
	 * contents.put("2","南京"); contents.put("3","北京");
	 * conv.put("attrib1",contents); return conv; 童志杰2002-08-30
	 */
	public java.util.Hashtable getDispConvertor() {
		if (getDdReaderVO() != null && m_htDispConvertor == null) {
			m_htDispConvertor = getDDReaderMap(getDdReaderVO().getTableName(),
					getDdReaderVO().getFieldNames());
		}
		return m_htDispConvertor;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-10-28 19:40:50)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDynamicColClassName() {
		return m_dynamicColClassName;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-10-30 8:34:51)
	 * 
	 * @return java.lang.String[]
	 */
	public java.lang.String[] getDynamicFieldNames() {
		return m_strDynamicFieldNames;
	}

	/**
	 * 此处插入方法说明。 创建日期：(01-4-3 20:08:40)
	 * 
	 * @return java.util.Hashtable
	 * @param tableName
	 *            java.lang.String
	 */
	public Map getFieldCNName() {
		return modelHandler.getFieldCNName();
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-11-8 13:31:41)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getGroupPart() {
		return m_strGroupPart;
	}

	/**
	 * 不显示字段列表
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String[] getHiddenFieldCode() {
		return m_strHiddenFieldCode;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-11-9 9:38:40)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getOriginWherePart() {
		return m_strOriginWherePart;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-11-8 10:36:45)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getRefNodeName() {
		return m_strRefNodeName == null ? getClass().getName()
				: m_strRefNodeName;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-8-29 10:08:50)
	 * 
	 * @return int[]
	 */
	public int[] getShownColumns() {
		if ((m_iShownColumns == null || m_iShownColumns.length == 0)
				&& getDefaultFieldCount() > 0) {
			m_iShownColumns = new int[getDefaultFieldCount()];
			for (int i = 0; i < getDefaultFieldCount(); i++) {
				m_iShownColumns[i] = i;
			}
		}
		return m_iShownColumns;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-10-28 19:54:29)
	 * 
	 * @return java.lang.Object
	 */
	public java.lang.Object getUserParameter() {
		return m_userParameter;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-10-28 19:38:34)
	 * 
	 * @return boolean
	 */
	public boolean isDynamicCol() {
		return m_isDynamicCol;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-2-21 16:27:33)
	 * 
	 * @return boolean
	 */
	public boolean isMnecodeInput() {
		return m_mnecodeInput;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-4-10 20:39:56)
	 * 
	 * @return boolean
	 */
	public boolean isNotLeafSelectedEnabled() {
		return m_bNotLeafSelectedEnabled;
	}

	/**
	 * 是否允许使用数据权限
	 * 
	 * @return boolean
	 */
	public boolean isUseDataPower() {
		return m_bUserDataPower;
	}

	/**
	 * 设置模糊字段。 创建日期：(2001-8-17 12:57:37)
	 */
	public void setBlurFields(String[] strBlurFields) {
		m_strBlurFields = strBlurFields;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-11-9 8:54:00)
	 * 
	 * @param newDataSource
	 *            java.lang.String
	 */
	public void setDataSource(java.lang.String newDataSource) {
		m_strDataSource = newDataSource;
	}

	/**
	 * 用于参照内容转换的影射表 如attrib1属性1－上海 2－南京3－北京 Hashtable conv=new Hashtable();
	 * Hashtable contents=new Hashtable(); contents.put("1","上海");
	 * contents.put("2","南京"); contents.put("3","北京");
	 * conv.put("attrib1",contents); return conv; 童志杰2002-08-30
	 */
	public void setDispConvertor(java.util.Hashtable newDispConvertor) {
		m_htDispConvertor = newDispConvertor;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-10-28 19:40:50)
	 * 
	 * @param newColClassName
	 *            java.lang.String
	 */
	public void setDynamicColClassName(java.lang.String newColClassName) {
		m_dynamicColClassName = newColClassName;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-10-30 8:34:51)
	 * 
	 * @param newDynamicFieldNames
	 *            java.lang.String[]
	 */
	public void setDynamicFieldNames(java.lang.String[] newDynamicFieldNames) {
		m_strDynamicFieldNames = newDynamicFieldNames;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-4-10 18:32:43)
	 * 
	 * @param newM_filterPks
	 *            java.lang.String[]
	 * 
	 *            设置过滤的Pks
	 */
	public void setFilterPks(java.lang.String[] newM_filterPks) {
		setFilterPks(newM_filterPks, IFilterStrategy.INSECTION);
	}

	/**
	 * 此处插入方法说明。 创建日期：(2005-9-02 18:32:43)
	 * 
	 * @param newM_filterPks
	 *            java.lang.String[]
	 * 
	 * 
	 *            设置过滤的Pks
	 */
	public void setFilterPks(java.lang.String[] newM_filterPks,
			int filterStrategy) {
		m_filterPks = newM_filterPks;
		// 过滤策略
		m_filterStrategy = filterStrategy;

		String[] selectedPKs = getPkValues();

		clearData();
		
		if (isCacheEnabled() && selectedPKs != null) {
			String lruKey = getLRUKey(selectedPKs);
			modelHandler.getLRUMap().put(lruKey, null);
		}

		if (selectedPKs != null && selectedPKs.length > 0) {
			matchPkData(selectedPKs);
		}

		Vector selectedData = getSelectedData();

		setSelectedData(selectedData);
		// 通知UIRefPane,刷新界面
		fireChange();

	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-11-8 13:31:41)
	 * 
	 * @param newGroupPart
	 *            java.lang.String
	 */
	public void setGroupPart(java.lang.String newGroupPart) {
		m_strGroupPart = newGroupPart;
	}

	/**
	 * 此处插入方法说明。 创建日期：(01-6-25 10:53:54)
	 * 
	 * @param newHiddenFieldCode
	 *            java.lang.String[]
	 */
	public void setHiddenFieldCode(java.lang.String[] newHiddenFieldCode) {
		getHtCodeIndex().clear();
		hiddenFieldList.clear();
		hiddenFieldList.addAll(Arrays.asList(newHiddenFieldCode));
		m_strHiddenFieldCode = newHiddenFieldCode;
	}

	/**
	 * 此处插入方法说明。 创建日期：(01-6-17 18:35:14)
	 * 
	 * @return java.util.Hashtable
	 */
	public void setHtCodeIndex(Hashtable ht) {
		m_htCodeIndex = ht;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-10-28 19:38:34)
	 * 
	 * @param newDynamicCol
	 *            boolean
	 */
	public void setIsDynamicCol(boolean newDynamicCol) {
		m_isDynamicCol = newDynamicCol;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-2-21 16:27:33)
	 * 
	 * @param newM_mnecodeInput
	 *            boolean
	 */
	public void setMnecodeInput(boolean newM_mnecodeInput) {
		m_mnecodeInput = newM_mnecodeInput;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-4-10 20:39:56)
	 * 
	 * @param newM_bNotLeafSelectedEnabled
	 *            boolean
	 */
	public void setNotLeafSelectedEnabled(boolean newM_bNotLeafSelectedEnabled) {
		m_bNotLeafSelectedEnabled = newM_bNotLeafSelectedEnabled;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-11-8 10:36:45)
	 * 
	 * @param newRefNodeName
	 *            java.lang.String
	 */
	public void setRefNodeName(java.lang.String newRefNodeName) {
		m_strRefNodeName = newRefNodeName;
	}

	public void setRefNodeName(String refNodeName, String pk_corp) {
		m_strRefNodeName = refNodeName;
		m_strPk_corp = pk_corp;
	}

	/**
	 * 为了适配三种DefaultModel添加的方法，在DefaultModel里被覆盖 对于自定义model不能直接调用该方法，请覆盖它
	 */
	public void setRefTitle(java.lang.String newRefTitle) {
		m_strRefTitle = newRefTitle;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-8-29 10:08:50)
	 * 
	 * @return int[]
	 */
	public void setShownColumns(int[] iShownColumns) {
		m_iShownColumns = iShownColumns;
		return;
	}

	/**
	 * 为了适配三种DefaultModel添加的方法，在DefaultModel里被覆盖 对于自定义model不能直接调用该方法，请覆盖它
	 */
	public void setTableName(java.lang.String newTableName) {
		m_strTableName = newTableName;
	}

	/**
	 * 设置是否使用数据权限。 创建日期：(2001-8-23 21:03:06)
	 */
	public void setUseDataPower(boolean useDataPower) {
		m_bUserDataPower = useDataPower;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-10-28 19:54:29)
	 * 
	 * @param newParameter
	 *            java.lang.Object
	 */
	public void setUserParameter(java.lang.Object newParameter) {
		m_userParameter = newParameter;
	}

	/**
	 * 根据记录位置，列名称插入列值
	 */
	public void setValue(int recordIndex, String field, Object value) {
		Vector vecData = getSelectedData();
		if (vecData == null || recordIndex >= vecData.size()) {
			return;
		}
		Vector vRecord = (Vector) vecData.get(recordIndex);
		int col = getFieldIndex(field);

		//
		if (isDynamicCol()) {
			if (vRecord == null || vRecord.size() == 0 || col < 0)
				return;

			if (value != null) {

				if (col >= vRecord.size()) {
					vRecord.add(value);
				} else {
					vRecord.setElementAt(value, col);
				}
				return;

			}
		}

		if (vRecord == null || vRecord.size() == 0 || col < 0
				|| col >= vRecord.size())
			return;

		if (value != null) {
			vRecord.setElementAt(value, col);

		}

	}

	/**
	 * sxj 2004-5-26 新增 改变WherePart时是否清除数据
	 */
	public void addWherePart(String newWherePart, boolean isRrefreshData) {
		addWherePart(newWherePart);
		if (isRrefreshData) {
			clearModelData();
		}

	}

	/**
	 * 此处插入方法说明。 创建日期：(2004-6-1 10:21:59)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getFilterDlgClaseName() {
		return refFilterDlgClaseName;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2004-9-23 9:03:51)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getMatchField() {
		if (m_matchField == null) {
			return getPkFieldCode();
		}
		return m_matchField;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2004-11-10 10:31:47)
	 * 
	 * @return java.util.HashMap
	 */
	private java.util.HashMap getMatchHM() {
		return m_matchHM;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-2-19 9:24:17)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String[] getMnecodes() {
		return m_mnecodes;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2004-4-16 10:31:17)
	 * 
	 * @return java.lang.Object
	 */
	public java.lang.Object getPara() {
		return userPara;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-11-14 15:42:52)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getQuerySql() {
		return querySql;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-11-15 10:51:44)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getRefQueryDlgClaseName() {
		return refQueryDlgClaseName;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2004-4-14 16:01:25)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getTempDataWherePart() {
		return refTempDataWherePart;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2004-6-10 12:09:05)
	 * 
	 * @return boolean
	 */
	public boolean isFromTempTable() {
		return isFromTempTable;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2004-4-14 16:13:36)
	 * 
	 * @return boolean
	 */
	public boolean isIncludeSub() {
		return isIncludeSub;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-10-31 15:57:17)
	 * 
	 * @return boolean
	 */
	public boolean isRefreshMatch() {
		return m_isRefreshMatch;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2004-3-17 12:53:23)
	 * 
	 * @return boolean
	 * @deprecated
	 */
	public boolean isSealedDataShow() {
		return m_isDisabledDataShow;
	}

	public boolean isDisabledDataShow() {
		return m_isDisabledDataShow;
	}

	/**
	 * 获取匹配数据 匹配成功 数据－>m_vecSelectedData 匹配不成功 null－>m_vecSelectedData
	 * 创建日期：(2001-8-23 18:39:24)
	 * 
	 * @return java.util.Vector sxj 修改如果value= null or value.length()=0
	 *         不做匹配,提高效率，防止重复reloadData() sxj 2004-11-11 add
	 */
	public Vector matchData(String field, String[] values) {
		return matchData(new String[] { field }, values);
	}

	/**
	 * 匹配主键数据－－选择数据也改变了。 创建日期：(2001-8-23 18:39:24)
	 * 
	 * @return java.util.Vector sxj 2004-06-08 如果是用Pk 进行匹配，则在最大集内进行匹配。 根据
	 *         getMatchField()提供的列进行匹配
	 */
	public Vector matchPkData(String[] strPkValues) {

		Vector v = null;
		String matchsql = null;
		matchsql = getMatchSql(strPkValues);
		Vector matchVec = getMatchVectorFromLRU(strPkValues);
		if (matchVec != null) {

			setSelectedData(matchVec);

			return matchVec;
		}
		try {
			if (isNonSqlMatch(matchsql)) {
				v = matchData(getMatchField(), strPkValues);
			} else {

				if (isFromTempTable()) {
					v = modelHandler.queryRefDataFromTemplateTable(matchsql);
				} else {
					v = modelHandler.matchPK(getDataSource(), matchsql);
				}
			}

		} catch (Exception e) {
			Debug.error(e.getMessage(), e);
		}

		setSelectedDataAndConvertData(v);

		return getSelectedData();
	}

	/**
	 * 
	 */
	public boolean isNonSqlMatch(String matchsql) {
		return modelHandler.isNonSqlMatch(matchsql);
	}

	public String getMatchSql(String[] strPkValues) {
		setPKMatch(true);
		boolean originSealedDataShow = isSealedDataShow();
		boolean originUseDataPower = isUseDataPower();

		if (!isRefEnable()) {
			// 如果参照不可编辑，不启用数据权限控制;包含封存数据 ---〉V5新需求
			setUseDataPower(false);
			setSealedDataShow(true);

		}

		String matchsql;
		// pk匹配时，带distinct,防止关联表出现重复数据的问题。
		String strPatch = IRefConst.DISTINCT;
		String strWherePart = null;

		String[] fieldCodes = getFieldCode();

		if (fieldCodes != null && fieldCodes.length > 0) {
			if (fieldCodes[0] != null
					&& strPatch.indexOf(fieldCodes[0].toLowerCase()) >= 0) {
				strPatch = "";
			}
		}
		if (isMatchPkWithWherePart()) {
			strWherePart = getWherePart();
		}

		if (isLargeDataRef() && canUseDB())
			matchsql = getSpecialMatchSql(new String[] { getMatchField() },
					strPkValues, strPatch, getFieldCode(),
					getHiddenFieldCode(), getTableName(), strWherePart,
					getOrderPart());
		// 批处理Sql
		else
			matchsql = getSql_Match(new String[] { getMatchField() },
					strPkValues, strPatch, getFieldCode(),
					getHiddenFieldCode(), getTableName(), strWherePart,
					getOrderPart());// 批处理Sql

		setPKMatch(false);

		setSealedDataShow(originSealedDataShow);

		setUseDataPower(originUseDataPower);

		return matchsql;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2004-6-1 10:21:59)
	 * 
	 * @param newFilterDlgClaseName
	 *            java.lang.String
	 */
	public void setFilterDlgClaseName(java.lang.String newFilterDlgClaseName) {
		refFilterDlgClaseName = newFilterDlgClaseName;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2004-6-10 12:09:05)
	 * 
	 * @param newFromTempTable
	 *            boolean
	 */
	public void setFromTempTable(boolean newFromTempTable) {
		isFromTempTable = newFromTempTable;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2004-4-14 16:13:36)
	 * 
	 * @param newIncludeSub
	 *            boolean
	 */
	public void setIncludeSub(boolean newIncludeSub) {
		isIncludeSub = newIncludeSub;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-10-31 15:57:17)
	 * 
	 * @param newRefreshMatch
	 *            boolean
	 */
	public void setIsRefreshMatch(boolean newRefreshMatch) {
		m_isRefreshMatch = newRefreshMatch;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-2-19 9:24:17)
	 * 
	 * @param newM_mnecode
	 *            java.lang.String
	 */
	public void setMnecode(java.lang.String[] newM_mnecodes) {
		m_mnecodes = newM_mnecodes;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2004-4-16 10:31:17)
	 * 
	 * @param newPara
	 *            java.lang.Object
	 */
	public void setPara(java.lang.Object newPara) {
		userPara = newPara;
	}

	/**
	 * sxj 2004-5-26 新增 改变WherePart时是否清除数据
	 */
	public void setPara(java.lang.Object newPara, boolean isRrefreshData) {
		setPara(newPara);
		if (isRrefreshData) {
			clearModelData();
		}
	}

	/**
	 * sxj 2004-5-26 新增 改变WherePart时是否清除数据
	 */
	public void setPk_corp(String strPk_corp, boolean isRrefreshData) {
		// setPk_corp(strPk_corp);
		// if (isRrefreshData) {
		// clearModelData();
		// }
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-11-14 15:42:52)
	 * 
	 * @param newQuerySql
	 *            java.lang.String
	 */
	public void setQuerySql(java.lang.String newQuerySql) {
		querySql = newQuerySql;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-11-15 10:51:44)
	 * 
	 * @param newRefQueryDlgClaseName
	 *            java.lang.String
	 */
	public void setRefQueryDlgClaseName(java.lang.String newRefQueryDlgClaseName) {
		refQueryDlgClaseName = newRefQueryDlgClaseName;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2004-3-17 12:53:23)
	 * 
	 * @param newSealedDataShow
	 *            boolean
	 * @deprecated
	 */
	public void setSealedDataShow(boolean newSealedDataShow) {
		this.m_isDisabledDataShow = newSealedDataShow;
	}

	public void setDisabledDataShow(boolean isDisabledDataShow) {
		this.m_isDisabledDataShow = isDisabledDataShow;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2004-4-14 16:01:25)
	 * 
	 * @param newTempDataWherePart
	 *            java.lang.String
	 */
	public void setTempDataWherePart(java.lang.String newTempDataWherePart) {
		refTempDataWherePart = newTempDataWherePart;
	}

	/**
	 * @return 返回 m_orgTypeCode。
	 */
	public String getOrgTypeCode() {
		return m_orgTypeCode;
	}

	/*
	 * 
	 */
	public String getDataPowerSubSql(String strTableName,
			String dataPowerField, String resourceID) {
		String tableName = strTableName;
		if (strTableName != null) {
			tableName = strTableName.trim();
		}
		String powerSql = modelHandler.getDataPowerSubSql(tableName,
				dataPowerField, this, resourceID);
		return powerSql;
	}

	protected String getDataPowerSqlKey(String strTableName,
			String dataPowerField) {
		String pk_org = null;
		String tableName = strTableName;
		if (strTableName != null) {
			tableName = strTableName.trim();
		}
		String dataPowerKey = tableName + "_" + dataPowerField
				+ getDataPowerOperation_code() + pk_org;
		return dataPowerKey;
	}

	/**
	 * @return 返回 m_fun_code。
	 */
	public String getFun_code() {
		return m_fun_code;
	}

	/**
	 * @param m_fun_code
	 *            要设置的 m_fun_code。
	 */
	public void setFun_code(String m_fun_code) {

		boolean isEqual = modelHandler.equals(this.m_fun_code, m_fun_code);

		this.m_fun_code = m_fun_code;

		// 同时要重新初始化参照的设置
		if (!isEqual) {
			setRefNodeName(getRefNodeName());
		}
	}

	/**
	 * 为多语言添加此方法, 请覆盖此方法返回要翻译字段数组。
	 */
	protected RefVO_mlang[] getRefVO_mlang() {

		return null;

	}

	/**
	 * * 为多语言添加此方法, 并提供默认实现.
	 * 
	 * vecData：参照中的数据（全集），请用参数传入的vecData,不要用getData()方法得到，否则会死循环。
	 * vecData中的数据为每一行Record，每一个Record也是一个Vector.
	 * 
	 * 返回值为Vector，它的数据为翻译后的Object[],注意放置Object[]的顺序一定和refVO_mlangs的顺序一致。
	 * 
	 * 创建日期：(2005-3-30 16:19:24)
	 * 
	 * @return Vector
	 */
	protected Vector getValues_mlang(Vector vecData, RefVO_mlang[] refVO_mlangs) {
		// 要翻译的列

		Vector datas = new Vector();

		if (vecData != null && vecData.size() > 0) {
			int recordAccout = vecData.size(); // 行记录数。
			if (refVO_mlangs != null) {
				for (int i = 0; i < refVO_mlangs.length; i++) {
					RefVO_mlang refVO_mlang = refVO_mlangs[i];
					String resid = "";
					String[] resIDFieldNames = refVO_mlangs[i]
							.getResIDFieldNames();

					Object[] oData_mlang = new Object[recordAccout];

					for (int j = 0; j < recordAccout; j++) {

						Vector record = (Vector) vecData.get(j); // 行记录
						resid = "";
						if (resIDFieldNames != null) {
							resid += refVO_mlang.getPreStr();
							for (int k = 0; k < resIDFieldNames.length; k++) {
								Object obj = record
										.get(getFieldIndex(resIDFieldNames[k]));
								if (obj == null) {
									break;
								}
								resid += obj.toString();
							}
						}
						Object oStr = record.get(getFieldIndex(refVO_mlang
								.getFieldName()));
						String dirName = refVO_mlang.getDirName();
						if (refVO_mlang.getDirFieldName() != null) {
							Object oDirFieldName = record
									.get(getFieldIndex(refVO_mlang
											.getDirFieldName()));
							if (oDirFieldName != null) {
								dirName = oDirFieldName.toString();
							}
						}

						String value = (oStr == null ? null : oStr.toString());
						// 根据资源ID取翻译后的数据
						String str_multiLang = NCLangRes4VoTransl
								.getNCLangRes()
								.getString(dirName, value, resid);

						oData_mlang[j] = str_multiLang;

					}
					datas.add(oData_mlang);
				}

			}
		}

		return datas;

	}

	/**
	 * 为多语言添加此方法, 设置翻译后的值到参照数据中. 设置特定列的值 创建日期：(2005-3-30 16:19:24)
	 * 
	 */
	private void setMlangValues(Vector vecData, RefVO_mlang[] refVO_mlangs) {

		if (vecData == null || vecData.size() == 0 || refVO_mlangs == null) {
			return;
		}
		Vector vec_malng = getValues_mlang(vecData, refVO_mlangs);
		if (vec_malng == null || vec_malng.size() != refVO_mlangs.length) {
			return;
		}
		String[] fieldNames = new String[refVO_mlangs.length];
		for (int i = 0; i < refVO_mlangs.length; i++) {
			fieldNames[i] = refVO_mlangs[i].getFieldName();
		}
		setValuesByFieldName(vecData, vec_malng, fieldNames);

	}

	class DefFieldsHandler {

		/**
		 * 设有公式的参照，按公式取值。 创建日期：(2005-3-30 16:19:24)
		 * 
		 */
		void setDefFieldValues(Vector vecData) {

			String[] defFields = getShowDefFields();

			if (vecData == null || vecData.size() == 0 || defFields == null
					|| defFields.length == 0) {
				return;
			}
			Vector convertedData = null;

			setValuesByFieldName(vecData, convertedData, defFields);

		}
	}

	class FormulaHandler {

		private FormulaParseFather parse = null;

		/**
		 * 设有公式的参照，按公式取值。 创建日期：(2005-3-30 16:19:24)
		 * 
		 */
		void setFormulaValues(Vector vecData, String[][] formulas) {

			if (vecData == null || vecData.size() == 0 || formulas == null) {
				return;
			}
			String[][] showFormulas = getShowFieldFormulas(formulas);
			if (showFormulas == null) {
				return;
			}
			Object[][] datas = getValues_formulas(vecData, showFormulas);

			if (datas == null || datas.length != showFormulas.length) {
				return;
			}
			Vector v = new Vector();
			String[] fieldNames = new String[datas.length];
			for (int i = 0; i < datas.length; i++) {
				v.add(datas[i]);
				fieldNames[i] = (String) showFormulas[i][0];
			}
			setValuesByFieldName(vecData, v, fieldNames);

		}

		private String[][] getShowFieldFormulas(String[][] formulas) {
			List<String[]> list = new ArrayList<String[]>();
			for (int i = 0; i < formulas.length; i++) {
				String formulaField = formulas[i][0];
				if (isShowField(formulaField)) {
					list.add(formulas[i]);
				}
			}
			String[][] showFormulas = null;
			if (list.size() > 0) {
				showFormulas = new String[list.size()][];
				list.toArray(showFormulas);
			}
			return showFormulas;
		}

		private Object[][] getValues_formulas(Vector vecData,
				String[][] formulas) {
			// 要翻译的列

			Object[][] datas = null;
			int flen = formulas.length;
			if (vecData != null && vecData.size() > 0 && flen > 0) {

				int recordAccout = vecData.size(); // 行记录数。
				datas = new Object[flen][recordAccout];
				ArrayList list = new ArrayList();
				String formulaField = null;
				String strFormula = null;
				for (int i = 0; i < flen; i++) {
					list.clear();
					formulaField = formulas[i][0];
					strFormula = formulas[i][1];

					for (int j = 0; j < recordAccout; j++) {
						Vector record = (Vector) vecData.get(j);
						list.add(getFormulaValue(record, formulaField));
					}
					datas[i] = getFormulaValues(formulaField, strFormula, list);
				}
			}

			return datas;

		}

		/**
		 * <p>
		 * <strong>最后修改人：sxj</strong>
		 * <p>
		 * <strong>最后修改日期：2006-9-19</strong>
		 * <p>
		 * 
		 * @param
		 * @return String
		 * @exception BusinessException
		 * @since NC5.0
		 */
		private String getFormulaValue(Vector record, String formulaField) {
			int fieldIndex = getFieldIndex(formulaField);
			if (fieldIndex == -1) {
				return null;
			}
			Object obj = record.get(fieldIndex);

			String value = null;

			if (obj instanceof RefValueVO && obj != null) {

				Object originValue = ((RefValueVO) obj).getOriginValue();

				value = originValue == null ? null : originValue.toString();

			} else {
				if (obj != null) {
					value = obj.toString();
				}
			}
			return value;
		}

		private Object[] getFormulaValues(String fieldName, String formulas,
				List givenvalues) {
			FormulaParseFather parse = getParse();
			// String express = formula;
			String[] expresses = StringUtil.toArray(formulas, ";");
			parse.setExpressArray(expresses);
			parse.addVariable(fieldName, givenvalues);
			Object[][] values = parse.getValueOArray();
			return values[values.length - 1];
		}

		/**
		 * @return 返回 parse。
		 */
		private FormulaParseFather getParse() {
			if (parse == null) {
				parse = new nc.ui.pub.formulaparse.FormulaParse();
			}
			return parse;
		}

	}

	/*
	 * _ add by hetian 2005-04-11 ______________________________________
	 */
	/**
	 * 判断该参照是否是大数据表
	 * 
	 * @return
	 */
	public boolean isLargeDataRef() {
		if (m_strRefNodeName == null || m_strRefNodeName.equals(""))
			return false;
		// if (IRefConst.LARGE_NODE_NAME.indexOf(m_strRefNodeName) > 0
		// && isChangeTableSeq()) {
		// return true;
		// }
		if (IRefConst.LARGE_NODE_NAME.indexOf(m_strRefNodeName) >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * 改变主表
	 * 
	 * @param table
	 * @return
	 */
	public String changeBaseTable(String table) {

		if (table == null || table.indexOf("join") < 0)
			return table;
		StringTokenizer token = new StringTokenizer(table);
		String firstTable = "";
		String secondTable = "";
		String lastElement = "";
		String joinStr = "";
		String onStr = "";
		boolean isJoin = false;
		boolean isOn = false;
		int index = 0;
		while (token.hasMoreTokens()) {
			String element = token.nextToken();
			if (lastElement.equalsIgnoreCase("join")) {
				secondTable = element;
				isJoin = false;
			}
			if (element.equalsIgnoreCase("on")) {
				isOn = true;
			}
			if (isJoin) {
				joinStr += new StringBuffer().append(" ").append(element)
						.append(" ").toString();
			}
			if (isOn) {
				onStr += new StringBuffer().append(" ").append(element).append(
						" ").toString();
			}
			if (index == 0) {
				firstTable = new StringBuffer().append(element).append(" ")
						.toString();
				isJoin = true;
			}

			lastElement = element;
			index++;
		}

		return secondTable + joinStr + firstTable + onStr;
	}

	/**
	 * 贺扬
	 * 
	 * @return
	 */
	public String getSpecialSql(String strPatch, String[] strFieldCode,
			String[] hiddenFields, String strTableName, String strWherePart,
			String group, String order) {
		if (strTableName == null || strTableName.trim().length() == 0)
			return null;
		strTableName = changeBaseTable(strTableName);
		String basSQL = buildBaseSql(strPatch, strFieldCode, hiddenFields,
				strTableName, strWherePart);
		StringBuffer buffer = new StringBuffer(basSQL);

		if (getQuerySql() != null) {
			addQueryCondition(buffer);
		}
		// 把or改成union
		addSpecialBlurCriteria(buffer, basSQL);
		if (group != null) {
			buffer.append(" group by ").append(filterColumn(group));
		}
		// 加入ORDER子句
		if (order != null && order.trim().length() != 0) {
			buffer.append("order by ").append(filterColumn(order));
		}
		return buffer.toString();

	}

	private void addSpecialBlurCriteria(StringBuffer buffer, String basSQL) {
		if (getBlurValue() != null && isIncludeBlurPart()) {
			if (getBlurValue().indexOf('*') != -1
					|| getBlurValue().indexOf('%') != -1
					|| getBlurValue().indexOf('?') != -1) {
				String[] blurfields = getBlurFields();
				buffer.append(" and (");
				buffer.append("( ").append(blurfields[0]).append(" like '")
						.append(
								getBlurValue().replace('*', '%').replace('?',
										'_')
										+ "' )");
				for (int j = 1; j < blurfields.length; j++) {
					buffer.append(" or ( ").append(blurfields[j]).append(
							" like '").append(
							getBlurValue().replace('*', '%').replace('?', '_')
									+ "' )");
				}
				buffer.append(")");
			} else {
				if (isMnecodeInput() && getMnecodes() != null) {
					buffer.append(" and ( ").append(getRefCodeField()).append(
							" = '").append(getBlurValue()).append("' ");
					for (int i = 0; i < getMnecodes().length; i++) {
						buffer.append(" union ");
						buffer.append(basSQL);
						buffer.append(" and ").append(getMnecodes()[i]).append(
								"='").append(getBlurValue()).append("' ");
					}
				}
			}
		}
	}

	/**
	 * 得到匹配语句
	 * 
	 * @return
	 */
	public String getSpecialMatchSql(String[] fieldNames, String values[],
			String strPatch, String[] strFieldCode, String[] hiddenFields,
			String strTableName, String strWherePart, String strOrderField) {
		if (strTableName == null || strTableName.trim().length() == 0)
			return null;
		if (!isPKMatch()) {
			strTableName = changeBaseTable(strTableName);
		}
		if (strOrderField != null) {
			strOrderField = filterColumn(strOrderField);
		}
		return getSql_Match(fieldNames, values, strPatch, strFieldCode,
				hiddenFields, strTableName, strWherePart, strOrderField);

	}

	/**
	 * 构造基本 SQL
	 */
	public String buildBaseSql(String patch, String[] columns,
			String[] hiddenColumns, String tableName, String whereCondition) {
		StringBuffer whereClause = new StringBuffer();
		StringBuffer sql = new StringBuffer("select ").append(patch)
				.append(" ");
		int columnCount = columns == null ? 0 : columns.length;
		addQueryColumn(columnCount, sql, columns, hiddenColumns);
		// 加入FROM子句
		sql.append(" from ").append(tableName);
		// 加入WHERE子句
		if (whereCondition != null && whereCondition.trim().length() != 0) {
			whereClause.append(" where (").append(whereCondition).append(" )");
		} else
			whereClause.append(" where 11=11 ");

		appendAddWherePartCondition(whereClause);

		addDataPowerCondition(getTableName(), whereClause);
		addDisabledDataWherePart(whereClause);
		addEnvWherePart(whereClause);
		sql.append(" ").append(whereClause.toString());

		return sql.toString();
	}

	protected void addDataPowerCondition(String tableName,
			StringBuffer whereClause) {
		if (isUseDataPower()) {

			String powerSql = getDataPowerSubSql(tableName,
					getDataPowerColumn(), getResourceID());

			if (powerSql != null) {
				whereClause.append(" and (").append(powerSql).append(")");
			}

		}

	}

	private void appendAddWherePartCondition(StringBuffer whereClause) {

		if (getAddWherePart() == null) {
			return;
		}

		if (isPKMatch() && !isMatchPkWithWherePart()) {

			return;

		}
		whereClause.append(" ").append(getAddWherePart());

	}

	/**
	 * 添加列条件
	 * 
	 * @param iSelectFieldCount
	 * @param strSql
	 * @param strFieldCode
	 * @param hiddenFields
	 */
	public void addQueryColumn(int iSelectFieldCount, StringBuffer strSql,
			String[] strFieldCode, String[] hiddenFields) {

		int nameFieldIndex = getFieldIndex(getRefNameField());

		for (int i = 0; i < iSelectFieldCount; i++) {
			if (isMutilLangNameRef() && i == nameFieldIndex && !strFieldCode[nameFieldIndex].equalsIgnoreCase("null")) {

				strSql.append(getLangNameColume(getRefNameField()));

			} else {
				strSql.append(strFieldCode[i]);
			}

			if (i < iSelectFieldCount - 1)
				strSql.append(",");
		}
		// 加入隐藏字段
		if (hiddenFields != null && hiddenFields.length > 0) {
			for (int k = 0; k < hiddenFields.length; k++) {
				if (hiddenFields[k] != null
						&& hiddenFields[k].trim().length() > 0) {
					strSql.append(",");
					strSql.append(hiddenFields[k]);
				}
			}
		}
	}

	/**
	 * 添加启用停用数据条件
	 * 
	 * @param whereClause
	 */
	private void addDisabledDataWherePart(StringBuffer whereClause) {

		if (isAddEnableStateWherePart()) {

			String wherePart = getDisableDataWherePart(isDisabledDataShow());
			if (wherePart != null) {
				whereClause.append(" and (").append(wherePart).append(") ");

			}
		}
	}

	/**
	 * 是否显示停用的数据
	 * 
	 * @param isEnable
	 * @return
	 */
	protected String getDisableDataWherePart(boolean isDisableDataShow) {

		if (isDisableDataShow) {

			return "(enablestate = 1 or enablestate = 2 or enablestate = 3)";

		} else {
			return "enablestate = 2";
		}
	}

	/**
	 * @deprecated
	 */
	public String getDisabledDataWherePart() {

		return disabledDataWherePart;

	}

	/**
	 * 设置停用条件
	 * 
	 * @deprecated
	 */
	public void setDisabledDataWherePart(String disabledDataWherePart) {
		this.disabledDataWherePart = disabledDataWherePart;
	}

	/**
	 * 过滤表名
	 * 
	 * @param column
	 * @return
	 */
	public String filterColumn(String column) {
		return column.substring(column.indexOf(".") + 1, column.length());
	}

	/**
	 * 构造SQL语句
	 * 
	 * @author 张扬 贺扬修改
	 */
	protected String getSql(String strPatch, String[] strFieldCode,
			String[] hiddenFields, String strTableName, String strWherePart,
			String strGroupField, String strOrderField) {
		if (strTableName == null || strTableName.trim().length() == 0) {
			return null;
		}

		String basSQL = buildBaseSql(strPatch, strFieldCode, hiddenFields,
				strTableName, strWherePart);
		StringBuffer sqlBuffer = new StringBuffer(basSQL);
		if (getQuerySql() != null) {
			addQueryCondition(sqlBuffer);
		}
		if (getBlurValue() != null && isIncludeBlurPart()) {
			String blurSql = addBlurWherePart();
			sqlBuffer.append(blurSql);
		}

		//
		// 加入Group子句
		if (strGroupField != null) {
			sqlBuffer.append(" group by ").append(strGroupField).toString();
		}
		// 加入ORDER子句
		if (strOrderField != null && strOrderField.trim().length() != 0) {
			sqlBuffer.append(" order by ").append(strOrderField).toString();
		}
		return sqlBuffer.toString();
	}

	protected void addQueryCondition(StringBuffer sqlBuffer) {
		sqlBuffer.append(" and (").append(getPkFieldCode()).append(" in (")
				.append(getQuerySql()).append("))").toString();
	}

	/**
	 * @param sqlBuffer
	 */
	public String addBlurWherePart() {
		return modelHandler.addBlurWherePart();
	}

	/**
	 */
	protected String getSql_Match(String[] fieldNames, String[] values,
			String strPatch, String[] strFieldCode, String[] hiddenFields,
			String strTableName, String strWherePart, String strOrderField) {
		if (strTableName == null || strTableName.trim().length() == 0)
			return null;

		String basSQL = null;

		basSQL = buildBaseSql(strPatch, strFieldCode, hiddenFields,
				strTableName, strWherePart);

		StringBuffer buffer = new StringBuffer(basSQL);

		buffer.append(" and (");
		if (isPKMatch()) {
			buffer.append(getWherePartByFieldsAndValues(fieldNames, values));
		} else {
			String[] toLowCasefieldNames = new String[fieldNames.length];
			String[] toLowCaseValues = new String[values.length];
			for (int i = 0; i < toLowCasefieldNames.length; i++) {
				toLowCasefieldNames[i] = RefPubUtil.toLowerDBFunctionWrapper(
						this, fieldNames[i]);
			}
			for (int i = 0; i < toLowCaseValues.length; i++) {
				toLowCaseValues[i] = RefPubUtil.toLowerCaseStr(this, values[i]);
			}

			buffer.append(getWherePartByFieldsAndValues(toLowCasefieldNames,
					toLowCaseValues));
		}
		buffer.append(") ");
		// 加入ORDER子句
		if (strOrderField != null) {
			buffer.append(" order by ").append(strOrderField).toString();
		}
		return buffer.toString();
	}

	public Hashtable getConvertHT(String tableName, String[] fieldNames) {
		// 只设置一个标识
		setDdReaderVO(tableName, fieldNames);
		// 读取值域
		return null;
	}

	/**
	 * <p>
	 * <strong>最后修改人：sxj</strong>
	 * <p>
	 * <strong>最后修改日期：2006-7-20</strong>
	 * <p>
	 * 
	 * @param
	 * @return Hashtable
	 * @exception BusinessException
	 * @since NC5.0
	 */
	private Hashtable getDDReaderMap(String tableName, String[] fieldNames) {
		DataDictionaryReader ddReader = new DataDictionaryReader(tableName);
		Hashtable convert = new Hashtable();

		for (int i = 0; i < fieldNames.length; i++) {
			// 如果列名已经包含表名,去掉表名.值域列没有表名
			String newfieldName = fieldNames[i];
			if (newfieldName.indexOf(".") > 0) {
				newfieldName = newfieldName.substring(
						newfieldName.indexOf(".") + 1, newfieldName.length());
			}
			String[] keys = ddReader.getQzsm(newfieldName);
			Hashtable contents = new Hashtable();
			for (int j = 0; j < keys.length; j++) {
				if (keys[j] != null) {
					contents.put(ddReader
							.getNumberByQzsm(newfieldName, keys[j]).toString(),
							keys[j]);
				}
			}

			convert.put(fieldNames[i], contents);
		}
		return convert;
	}

	/**
	 * @return 返回 isMatchPkWithWherePart。
	 * 
	 */
	public boolean isMatchPkWithWherePart() {
		return isMatchPkWithWherePart;
	}

	/**
	 * @param isMatchPkWithWherePart
	 *            要设置的 isMatchPkWithWherePart。
	 */
	public void setMatchPkWithWherePart(boolean isMatchPkWithWherePart) {
		this.isMatchPkWithWherePart = isMatchPkWithWherePart;
	}

	/**
	 * @return 返回 m_isRefEnable。
	 */
	public boolean isRefEnable() {
		return m_isRefEnable;
	}

	/**
	 * 参照是否可编辑 规则： 1、如果参照可以编辑 在matchPK时，包含数据权限和不包含封存数据 2、如果参照不能编辑
	 * 在matchPK时，不包含数据权限和包含封存数据
	 * 3、对于规则1，如果isUseDataPower()=false,就不包含数据权限；如果isSealedDataShow=true,就包含封存数据
	 */
	public void setisRefEnable(boolean refEnable) {
		m_isRefEnable = refEnable;

	}

	/**
	 * @return 返回 formulas。
	 */
	public String[][] getFormulas() {
		return formulas;
	}

	/**
	 * @param formulas
	 *            要设置的 formulas。
	 */
	public void setFormulas(String[][] formulas) {
		this.formulas = formulas;
	}

	/**
	 * @return 返回 isLocQueryEnable。
	 */
	public boolean isLocQueryEnable() {
		return isLocQueryEnable;
	}

	/**
	 * @param isLocQueryEnable
	 *            要设置的 isLocQueryEnable。
	 */
	public void setLocQueryEnable(boolean isLocQueryEnable) {
		this.isLocQueryEnable = isLocQueryEnable;
	}

	/**
	 * @return 返回 m_filterStrategy。
	 */
	public int getFilterStrategy() {
		return m_filterStrategy;
	}

	/**
	 * @return 返回 m_filterPks。
	 */
	public String[] getFilterPks() {
		return m_filterPks;
	}

	/**
	 * 获取匹配数据 匹配成功 数据－>m_vecSelectedData 匹配不成功 null－>m_vecSelectedData
	 * 创建日期：(2001-8-23 18:39:24)
	 * 
	 * @return java.util.Vector sxj 修改如果value= null or value.length()=0
	 *         不做匹配,提高效率，防止重复reloadData() sxj 2004-11-11 add
	 */
	public Vector matchData(String[] fields, String[] values) {

		Vector vMatchData = getMatchedRecords(fields, values);

		// 使用缓存，并且匹配不到数据，刷新数据再执行一次

		if (isCacheEnabled() && vMatchData == null && isRefreshMatch()
				&& values != null) {
			clearData();
			modelHandler.fireDBCache();
			vMatchData = getMatchedRecords(fields, values);
		}

		// boolean isDefConverted = true;
		// if (vMatchData == null || vMatchData.size() == 1) {
		// isDefConverted = false;
		// }
		// if (vMatchData != null) {
		// vMatchData = getConvertedData(false, vMatchData, isDefConverted);
		// }
		setSelectedData(vMatchData);

		return vMatchData;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2005-10-21 14:43:00)
	 * 
	 * @param fieldNames
	 *            java.lang.String[]
	 * @param values
	 *            java.lang.String
	 */
	protected Vector getMatchedRecords(String[] fieldNames, String[] values) {

		Vector vMatchedRecords = null;
		// sxj
		if (values == null || values.length == 0) {
			return vMatchedRecords;
		}
		//
		if (fieldNames == null || fieldNames.length == 0) {

			return vMatchedRecords;
		}
		// 要比较的值放到HashMap
		getMatchHM().clear();
		for (int i = 0; i < values.length; i++) {
			if (values[i] != null) {
				getMatchHM().put(RefPubUtil.toLowerCaseStr(this, values[i]),
						RefPubUtil.toLowerCaseStr(this, values[i]));
			}
		}
		vMatchedRecords = new Vector();
		// 在全集中匹配数据
		setIncludeBlurPart(false);
		// 重新载入数据
		Vector data = null;

		data = getRefData();

		if (data != null) {
			for (int i = 0; i < data.size(); i++) {
				Vector vRecord = (Vector) data.elementAt(i);
				if (vRecord != null)

					for (int j = 0; j < fieldNames.length; j++) {
						int col = getFieldIndex(fieldNames[j]);
						if (col < 0 || col >= vRecord.size()) {
							continue;
						}
						if (vRecord.elementAt(col) != null
								&& getMatchHM().get(
										RefPubUtil.toLowerCaseStr(this, vRecord
												.elementAt(col).toString()
												.trim())) != null) {
							vMatchedRecords.addElement(vRecord);
							break;
						}
					}

			}
		}

		if (vMatchedRecords != null && vMatchedRecords.size() == 0)
			vMatchedRecords = null;
		setIncludeBlurPart(true); // 恢复原状态

		return vMatchedRecords;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2005-10-21 17:12:01)
	 * 
	 * @return boolean
	 */
	public boolean isOnlyMneFields() {
		return isOnlyMneFields;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2005-10-21 17:12:01)
	 * 
	 * @param newIsOnlyMneFields
	 *            boolean
	 */
	public void setOnlyMneFields(boolean newIsOnlyMneFields) {
		isOnlyMneFields = newIsOnlyMneFields;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2003-2-19 9:24:17)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getWherePartByFieldsAndValues(String[] fields,
			String[] values) {
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				String tempStr = values[i];
				values[i] = tempStr.replaceAll("'", "''");
			}
		}
		return modelHandler.getWherePartByFieldsAndValues(fields, values);
	}

	/**
	 * @return Returns the m_addWherePart.
	 */
	public String getAddWherePart() {
		return m_addWherePart;
	}

	/**
	 * 设有公式的参照，按公式取值。 创建日期：(2005-3-30 16:19:24)
	 * 
	 */
	public void setColDispValues(Vector vecData, Object[][] dispConverter) {

		if (vecData == null || vecData.size() == 0 || dispConverter == null) {
			return;
		}

		// 列显示转换器循环
		for (int i = 0; i < dispConverter.length; i++) {

			// 列显示转化配置ＶＯ
			RefColumnDispConvertVO convertVO = (RefColumnDispConvertVO) dispConverter[i][0];
			if (convertVO == null) {
				continue;
			}
			// 实现类名称
			String className = dispConverter[i][1].toString();

			// 给列显示转化配置ＶＯ设置数据主键值
			// convertVO.setPks(pks);
			convertVO.setRefData(vecData);
			// 处理列显示转换
			m_vecData = modelHandler.setColDispValue(vecData, convertVO,
					className);
		}
	}

	/*
	 * 
	 */
	public ReftableVO getRefTableVO(String pk_corp) {

		return modelHandler.getRefTableVO(pk_corp);
	}

	/*
	 * 栏目信息数据放到缓存.
	 */
	public void setReftableVO2Cache(ReftableVO vo, String pk_org) {

		modelHandler.setReftableVO2Cache(vo, pk_org);
	}

	public String getReftableVOCacheKey(String pk_org) {
		String refNodeName = getRefNodeName();

		// 目前还是按当前登录公司来保存栏目信息,以后有需求可以加入对主体账簿的支持

		String key = refNodeName + pk_org;
		return key;
	}

	/*
	 * 查询参照数据以及参照栏目信息数据 1、栏目数据直接放入缓存. 2、参照数据返回
	 */
	public Vector getQueryResultVO() {
		boolean isQueryRefColumn = !modelHandler
				.isReftableVOCached(getPk_corp());

		RefQueryVO queryVO = getRefQueryVO();

		// 目前还是按当前登录公司来保存栏目信息,以后有需求可以加入对主体账簿的支持
		queryVO.setPk_org(getPk_corp());
		RefQueryResultVO resultVO = null;

		resultVO = modelHandler.queryRefdataByQueryVO(queryVO);

		if (resultVO == null) {
			return null;
		}
		Vector v = resultVO.getRefData();
		if (isQueryRefColumn) {
			setReftableVO2Cache(resultVO.getRefTableVO(), getPk_corp());
		}

		return v;
	}

	/**
	 * 参照标题 创建日期：(01-4-4 0:57:23)
	 * 
	 * @return java.lang.String
	 */
	public String getRefTitle() {
		if (m_strRefTitle != null) {
			return m_strRefTitle;
		}

		m_strRefTitle = modelHandler.getRefNodeName_mLang(getRefNodeName());

		if (m_strRefTitle == null) {
			m_strRefTitle = getRefNodeName();

		}
		return m_strRefTitle;
	}

	/**
	 * @param m_pk_user
	 *            要设置的 m_pk_user。
	 */
	public void setPk_user(String m_pk_user) {
		this.m_pk_user = m_pk_user;
	}

	/*
	 * 给名称字段赋值
	 */
	public void resetFieldName() {
		if (getFieldCode() != null && getTableName() != null
				&& (getFieldName() == null || getFieldName().length == 0)) {

			String[] name = new String[getFieldCode().length];

			for (int i = 0; i < getFieldCode().length; i++) {

				String fieldCode = modelHandler
						.getFieldCodeWithTableName(getFieldCode()[i]);

				String resid = modelHandler.getResID(fieldCode);

				name[i] = modelHandler.getRefMultiLangStr(resid, fieldCode);

			}
			setFieldName(name);
		}
	}

	/**
	 * @return 返回 formulaHandler。
	 */
	private FormulaHandler getFormulaHandler() {
		if (formulaHandler == null) {
			formulaHandler = new FormulaHandler();
		}
		return formulaHandler;
	}

	private DefFieldsHandler getDefFieldHandler() {
		if (defFieldHandler == null) {
			defFieldHandler = new DefFieldsHandler();
		}
		return defFieldHandler;
	}

	/**
	 * 设置自定义数据到指定vecData中的field列。 创建日期：(2005-3-30 16:19:24)
	 * 
	 * @return java.lang.Object[]
	 */
	private void setValuesByFieldName(Vector vecData, Vector newVecData,
			String[] fieldNames) {

		int col = -1;
		for (int i = 0; i < fieldNames.length; i++) {

			col = getFieldIndex(fieldNames[i]);
			Object[] values = (Object[]) newVecData.get(i);
			if (values == null) {
				continue;
			}
			for (int j = 0; j < values.length; j++) {
				Vector vRecord = (Vector) vecData.get(j);
				if (vRecord == null || vRecord.size() == 0 || col < 0
						|| col >= vRecord.size()) {
					continue;
				}

				Object obj = getRefValueVO(vRecord.elementAt(col), values[j]);

				vRecord.setElementAt(obj, col);

			}
		}
	}

	/**
	 * <p>
	 * <strong>最后修改人：sxj</strong>
	 * <p>
	 * <strong>最后修改日期：2006-5-17</strong>
	 * <p>
	 * 
	 * @param
	 * @return void
	 * @exception BusinessException
	 * @since NC5.0
	 */
	private RefValueVO getRefValueVO(Object originValue, Object newValue) {
		RefValueVO valueVO = new RefValueVO();
		valueVO.setOriginValue(originValue);
		valueVO.setNewValue(newValue);
		return valueVO;
	}

	/**
	 * @return 返回 m_pk_user。
	 */
	public String getPk_user() {
		if (m_pk_user != null)
			return m_pk_user;

		return modelHandler.getPk_user();

	}

	/**
	 * 只添加不重复的监听类。
	 * <p>
	 * <strong>最后修改人：sxj</strong>
	 * <p>
	 * <strong>最后修改日期：2006-8-10</strong>
	 * <p>
	 * 
	 * @param
	 * @return void
	 * @exception BusinessException
	 * @since NC5.0
	 */
	public void addChangeListener(ChangeListener l) {

		if (eventListMap.get(l) == null) {
			eListenerList.add(ChangeListener.class, l);
			eventListMap.put(l, l);
		}
	}

	public void removeChangeListener(ChangeListener l) {
		eListenerList.add(ChangeListener.class, l);
		eventListMap.remove(l);
	}

	public ChangeListener[] getChangeListeners() {
		return (ChangeListener[]) eListenerList
				.getListeners(ChangeListener.class);
	}

	public void fireChange() {
		Object[] listeners = eListenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				if (changeEvent == null) {
					changeEvent = new ChangeEvent(this);
				}
				((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
			}
		}
	}

	/**
	 * 显示字段列表 创建日期：(01-4-4 0:57:23)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String[] getFieldCode() {
		return m_strFieldCode;
	}

	/**
	 * 显示字段中文名 创建日期：(01-4-4 0:57:23)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String[] getFieldName() {
		return m_strFieldName;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-8-31 18:59:51)
	 * 
	 * @return java.lang.String
	 */
	public String getGroupCode() {
		return IRefConst.GROUPCORP;
	}

	/**
	 * 主键字段名
	 * 
	 * @return java.lang.String
	 */
	public String getPkFieldCode() {
		return m_strPkFieldCode;
	}

	/**
	 * 参照标题 创建日期：(01-4-4 0:57:23)
	 * 
	 * @return java.lang.String
	 */

	/**
	 * 参照数据库表或者视图名 创建日期：(01-4-4 0:57:23)
	 * 
	 * @return java.lang.String
	 */
	public String getTableName() {
		return m_strTableName;
	}

	/**
	 * 此处插入方法说明。 创建日期：(01-3-22 13:22:29)
	 * 
	 * @param newFieldCode
	 *            java.lang.String[]
	 */
	public void setFieldCode(java.lang.String[] newFieldCode) {
		getHtCodeIndex().clear();
		m_strFieldCode = newFieldCode;
	}

	/**
	 * 此处插入方法说明。 创建日期：(01-3-22 13:22:29)
	 * 
	 * @param newFieldName
	 *            java.lang.String[]
	 */
	public void setFieldName(java.lang.String[] newFieldName) {
		m_strFieldName = newFieldName;
	}

	/**
	 * 主键字段名
	 */
	public void setPkFieldCode(String strPkFieldCode) {
		m_strPkFieldCode = strPkFieldCode;
	}

	/**
	 * @return 返回 m_intFieldType。
	 */
	public int[] getIntFieldType() {
		return m_intFieldType;
	}

	/**
	 * @param fieldType
	 *            要设置的 m_intFieldType。
	 */
	public void setIntFieldType(int[] fieldType) {
		m_intFieldType = fieldType;
	}

	public String getRefDataCacheKey() {
		String key = refDataCachekeyPreStr
				+ getDataPowerSqlKey(getTableName(), getRefNodeName());

		return key;
	}

	public String getFieldShowName(String fieldCode) {
		int index = getFieldIndex(fieldCode);
		if (index < 0 || index > getFieldName().length - 1) {
			return null;
		} else {
			return getFieldName()[index];
		}
	}

	/**
	 * @return 返回 ddReaderVO。
	 */
	private DDReaderVO getDdReaderVO() {
		return ddReaderVO;
	}

	/**
	 * @param ddReaderVO
	 *            要设置的 ddReaderVO。
	 */
	public void setDdReaderVO(String tableName, String[] fieldnames) {
		if (ddReaderVO == null) {
			ddReaderVO = new DDReaderVO();
		}
		ddReaderVO.setTableName(tableName);
		ddReaderVO.setFieldNames(fieldnames);
	}

	/**
	 * @return 返回 resourceID。
	 */
	public String getResourceID() {
		return resourceID;
	}

	/**
	 * @param resourceID
	 *            要设置的 resourceID。
	 */
	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}

	/**
	 * @return 返回 isPKMatch。
	 */
	public boolean isPKMatch() {
		return isPKMatch;
	}

	/**
	 * @param isPKMatch
	 *            要设置的 isPKMatch。
	 */
	public void setPKMatch(boolean isPKMatch) {
		this.isPKMatch = isPKMatch;
	}

	/**
	 * @return 返回 isIncludeBlurPart。
	 */
	public boolean isIncludeBlurPart() {
		return isIncludeBlurPart;
	}

	/**
	 * @param isIncludeBlurPart
	 *            要设置的 isIncludeBlurPart。
	 */
	public void setIncludeBlurPart(boolean isIncludeBlurPart) {
		this.isIncludeBlurPart = isIncludeBlurPart;
	}

	/**
	 * @return 返回 envWherePart。
	 */
	protected String getEnvWherePart() {
		return envWherePart;
	}

	/**
	 * 设置条件时，要利用参照的环境变量的方法去拼条件，否则无法根据环境变量的变化而变化。 例如：
	 * 
	 * @param envWherePart
	 *            要设置的 envWherePart。
	 */
	public void setEnvWherePart(String envWherePart) {
		this.envWherePart = envWherePart;
	}

	/**
	 * 
	 */
	private void addEnvWherePart(StringBuffer whereClause) {

		if (!isAddEnvWherePart()) {
			return;
		}
		// setpk ,不包含此条件
		if (isPKMatch() && !isMatchPkWithWherePart()) {
			return;
		}
		String wherePart = getEnvWherePart();
		if (wherePart != null && wherePart.trim().length() > 0) {

			whereClause.append(" and (").append(wherePart).append(") ");

		}

	}

	/**
	 * @return Returns the isChangeTableSeq.
	 */
	public boolean isChangeTableSeq() {
		return isChangeTableSeq;
	}

	/**
	 * @param isChangeTableSeq
	 *            The isChangeTableSeq to set.
	 */
	public void setChangeTableSeq(boolean isChangeTableSeq) {
		this.isChangeTableSeq = isChangeTableSeq;
	}

	/**
	 * @return 返回 isMaintainButtonEnabled。
	 */
	public boolean isMaintainButtonEnabled() {
		return isMaintainButtonEnabled;
	}

	/**
	 * @param isMaintainButtonEnabled
	 *            要设置的 isMaintainButtonEnabled。
	 */
	public void setMaintainButtonEnabled(boolean isMaintainButtonEnabled) {
		this.isMaintainButtonEnabled = isMaintainButtonEnabled;
	}

	/**
	 * @return 返回 refCardInfoComponentImplClass。
	 */
	public String getRefCardInfoComponentImplClass() {
		return refCardInfoComponentImplClass;
	}

	/**
	 * @param refCardInfoComponentImplClass
	 *            要设置的 refCardInfoComponentImplClass。
	 */
	public void setRefCardInfoComponentImplClass(
			String refCardInfoComponentImplClass) {
		this.refCardInfoComponentImplClass = refCardInfoComponentImplClass;
	}

	public String getRefShowNameField() {
		return refShowNameField == null ? getRefNameField() : refShowNameField;
	}

	public void setRefShowNameField(String refShowNameField) {
		this.refShowNameField = refShowNameField;
	}

	// 初始化自定义项字段
	// defFields.length==0表示初始化过了。
	public String[] getDefFields() {
		return defFields;
	}

	/**
	 * 栏目显示的自定义项
	 * 
	 * @return
	 */
	public String[] getShowDefFields() {
		String[] defFieldNames = null;
		if (getDefFields() == null || getDefFields().length == 0) {
			return defFieldNames;
		}

		Map<String, RefcolumnVO> map = modelHandler.getRefColumnVOsMap();

		List<String> list = new ArrayList<String>();
		for (int i = 0; i < getDefFields().length; i++) {
			if (map.get(getDefFields()[i]) != null
					&& map.get(getDefFields()[i]).getIscolumnshow()
							.booleanValue()) {
				list.add(getDefFields()[i]);
			}
		}

		if (list.size() > 0) {
			String[] fieldNames = new String[list.size()];
			list.toArray(fieldNames);
			defFieldNames = fieldNames;
		}

		return defFieldNames;
	}

	public void setDefFields(String[] defFields) {
		this.defFields = defFields;
	}

	public Map<String, Integer> getAlignMap() {
		return alignMap;
	}

	private void setAlignMap(Map<String, Integer> alignMap) {
		this.alignMap = alignMap;
	}

	/**
	 * 栏目对齐方式
	 * 
	 * @param fieldName
	 *            栏目名称 要对齐的列（列名要和fieldCodes中的一致）
	 * @param alignment
	 *            对齐方式 常量见SwingConstants，例如 SwingConstants.RIGHT
	 */
	public void addFieldAlignment(String fieldName, int alignment) {
		if (getAlignMap() == null) {
			setAlignMap(new HashMap<String, Integer>());
		}
		getAlignMap().put(fieldName, Integer.valueOf(alignment));
	}

	public boolean canUseDB() {
		return modelHandler.canUseDB();
	}

	public Vector getCacheValue(String sql) {
		return modelHandler.getCacheValue(sql);
	}

	public Vector queryMain(String dsName, String sql) {
		return modelHandler.queryMain(dsName, sql);
	}

	public void removeCacheValue(String sql) {
		modelHandler.removeCache(sql);
	}

	public void setCacheValue(String sql, Vector value) {
		modelHandler.setCacheValue(sql, value);
	}

	public String getCodeRuleFromPara(String orgTypeCode, String pk_GlOrgBook,
			String codingRule, UFBoolean boolean1) {
		return modelHandler.getCodeRuleFromPara(orgTypeCode, pk_GlOrgBook,
				codingRule, boolean1);
	}

	public String getCodeRuleFromPara(String codingRule) {
		return modelHandler.getCodeRuleFromPara(codingRule);
	}

	public String getNumberCodingRule(String codingRule) {
		return modelHandler.getNumberCodingRule(codingRule);
	}

	public String[] getDefFields(String[] fieldCode) {
		return modelHandler.getDefFields(fieldCode);
	}

	public String getPara1() {
		return para1;
	}

	public void setPara1(String para1) {
		this.para1 = para1;
	}

	public String getPara2() {
		return para2;
	}

	public void setPara2(String para2) {
		this.para2 = para2;
	}

	public String getPara3() {
		return para3;
	}

	public void setPara3(String para3) {
		this.para3 = para3;
	}

	protected IRefModelHandler getModelHandler() {
		return modelHandler;
	}

	public String getUiControlComponentClassName() {
		return uiControlComponentClassName;
	}

	public void setUiControlComponentClassName(
			String uiControlComponentClassName) {
		this.uiControlComponentClassName = uiControlComponentClassName;
	}

	/**
	 * 界面控制组件变化时调用该方法，在子类实现具体业务逻辑。
	 * 
	 * @param eventData
	 *            暂时返回发生变化的组件名称
	 */
	public void UiControlClassValueChanged(UIControlClassEvent event) {

	}

	public boolean isShowField(String fieldName) {
		boolean isShowField = false;
		int[] showColumns = getShownColumns();
		int index = getFieldIndex(fieldName);
		for (int i = 0; i < showColumns.length; i++) {
			if (showColumns[i] == index) {
				isShowField = true;
				break;
			}
		}
		return isShowField;
	}

	public String[] getShowFields() {
		String[] showFields = new String[getShownColumns().length];
		int fieldCodeLen = getFieldCode().length;
		for (int i = 0; i < showFields.length; i++) {
			// 容错
			if (getShownColumns()[i] < fieldCodeLen) {
				showFields[i] = getFieldCode()[getShownColumns()[i]];
			}

		}
		return showFields;
	}
	


	public boolean isHiddenField(String fieldCode) {

		return hiddenFieldList.contains(fieldCode);
	}

	// 输入时是否大小写敏感
	public boolean isCaseSensitve() {
		return isCaseSensitve;
	}

	public void setCaseSensive(boolean isCaseSensitve) {
		this.isCaseSensitve = isCaseSensitve;
	}

	public Vector getAllColumnNames() {

		if (vecAllColumnNames != null) {
			return vecAllColumnNames;
		}

		vecAllColumnNames = new Vector();
		if (getFieldCode() != null && getFieldCode().length > 0) {
			if (getFieldName() == null || getFieldName().length == 0) {
				for (int i = 0; i < getFieldCode().length; i++) {
					vecAllColumnNames.addElement(getFieldCode()[i]);
				}
			} else {
				if (getFieldName().length >= getFieldCode().length) {
					for (int i = 0; i < getFieldCode().length; i++) {
						vecAllColumnNames.addElement(getFieldName()[i]);
					}
				} else {
					for (int i = 0; i < getFieldName().length; i++) {
						vecAllColumnNames.addElement(getFieldName()[i]);
					}
					for (int i = getFieldName().length; i < getFieldCode().length; i++) {
						vecAllColumnNames.addElement(getFieldCode()[i]);
					}
				}
			}

		}

		if (getHiddenFieldCode() != null)
			for (int i = 0; i < getHiddenFieldCode().length; i++) {
				vecAllColumnNames.addElement(getHiddenFieldCode()[i]);
			}

		// 加入动态列
		if (isDynamicCol()) {

			String[] dynamicColNames = getDynamicFieldNames();
			if (getDynamicFieldNames() != null) {

				for (int i = 0; i < dynamicColNames.length; i++) {

					// 加入到显示的列名中
					vecAllColumnNames.addElement(dynamicColNames[i]);
				}
			}
		}
		return vecAllColumnNames;
	}

	public String getPk_org() {

		// return pk_org == null ? IOrgConst.GLOBEORG : pk_org;
		return pk_org == null ? InvocationInfoProxy.getInstance().getGroupId()
				: pk_org;
	}

	public void setPk_org(String pk_org) {
		if (this.pk_org != null && pk_org != null && this.pk_org.equalsIgnoreCase(pk_org)) {
			return;
		}
		this.pk_org = pk_org;
		// 系统默认参照
		// 重置WherePart,也可能有其他信息也要改变，
		// resetWherePart();
		reset();
		setRefNodeName(getRefNodeName());
		resetSelectedData_WhenDataChanged();

	}

	public String getPowerOperator() {
		return powerOperator;
	}

	public void setPowerOperator(String powerOperator) {
		this.powerOperator = powerOperator;
	}

	public String getPowerResourceID() {
		return powerResourceID;
	}

	public void setPowerResourceID(String powerResourceID) {
		this.powerResourceID = powerResourceID;
	}

	public String[] getFilterRefNodeNames() {
		return filterRefNodeNames;
	}

	public void setFilterRefNodeName(String[] filterRefNodeNames) {
		this.filterRefNodeNames = filterRefNodeNames;
	}

	/**
	 * 过滤参照值变化事件，子类要覆盖该方法。
	 * 
	 * @param event
	 */
	public void filterValueChanged(ValueChangedEvent changedValue) {

	}

	public String getPk_group() {
		if (pk_group != null)

			return pk_group;

		return modelHandler.getPk_group();

	}

	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * 请子类覆盖此方法以完成参照初始化工作 当环境变量改变后，需要重新初始化，已获得正确的环境变量条件
	 * 替代以前默认的初始化方法setRefNodeName();
	 * 
	 * since V60
	 */
	public void reset() {

	}

	private String getLangStrSuffix() {
		String suffix = null;
		int langSeq = MultiLangContext.getInstance().getCurrentLangSeq()
				.intValue();
		if (langSeq != 1) {
			suffix = String.valueOf(langSeq);
		}
		return suffix;
	}

	 public String getLangNameColume(String nameField) {

		    if (getLangStrSuffix() == null) {

	           return nameField;

	       }

	       String currLangName = nameField + getLangStrSuffix();

	       StringBuffer sb = new StringBuffer();

	       sb.append(" case when isnull(").append(currLangName).append(

	              ",'~')='~' then ").append(nameField).append(" else ").append(
	              currLangName).append(" end ");
	       
	       return sb.toString();
	    }



	// 增加过滤参照的过滤条件
	// since v60
	public void addFilterRefWherePart(String refNodeName, String wherePart) {
		if (getFilterRefModel(refNodeName) != null) {
			getFilterRefModel(refNodeName).addWherePart(wherePart);
		}
	}

	public boolean isHistoryVersionDataShow() {
		return isHistoryVersionDataShow;
	}

	public void setHistoryVersionDataShow(boolean isHistoryVersionDataShow) {
		this.isHistoryVersionDataShow = isHistoryVersionDataShow;
	}

	protected RefQueryVO getRefQueryVO() {
		boolean isQueryRefColumn = !modelHandler
				.isReftableVOCached(getPk_corp());

		queryVO.setDataSourceName(getDataSource());
		queryVO.setRefNodeName(getRefNodeName());
		queryVO.setQuerySql(getRefSql());
		queryVO.setObj(getPara());
		queryVO.setQueryRefColumn(isQueryRefColumn);
		return queryVO;
	}

	private String getLRUKey(String[] strPkValues) {
		StringBuffer sb = new StringBuffer();
		String dsName = getDataSource();
		if (dsName == null) {
			dsName = InvocationInfoProxy.getInstance().getUserDataSource();
		}
		sb.append(dsName).append(getRefNodeName()).append(
				getMatchSql(strPkValues));

		if (strPkValues != null && strPkValues.length > 0) {
			for (int i = 0; i < strPkValues.length; i++) {
				sb.append(strPkValues[i]);
			}

		}
		return sb.toString();
	}

	public Vector getMatchVectorFromLRU(String[] strPkValues) {
		if (isCacheEnabled()) {
			LRUMap<String, Vector> lruMap = modelHandler.getLRUMap();

			String lruKey = getLRUKey(strPkValues);

			Vector matchVec = lruMap.get(lruKey);
			return matchVec;
		}
		return null;
	}

	private void putLruMap(Vector vecData) {
		if (isCacheEnabled()) {
			String[] pkValues = getPkValues();
			if (pkValues == null) {
				return;
			}
			LRUMap<String, Vector> lruMap = modelHandler.getLRUMap();
			String lruKey = getLRUKey(pkValues);
			lruMap.put(lruKey, vecData);
		}
	}

	protected String getRefCacheSqlKey() {
		String sql = getRefSql();
		return sql == null ? getClass().getName() : sql;
	}

	public IRefMaintenanceHandler getRefMaintenanceHandler() {
		return refMaintenanceHandler;
	}

	public void setRefMaintenanceHandler(
			IRefMaintenanceHandler refMaintenanceHandler) {
		this.refMaintenanceHandler = refMaintenanceHandler;
	}

	public boolean isAddEnableStateWherePart() {
		return isAddEnableStateWherePart;
	}

	public void setAddEnableStateWherePart(boolean isAddEnableStateWherePart) {
		this.isAddEnableStateWherePart = isAddEnableStateWherePart;
	}

	public boolean isAddEnvWherePart() {
		return isAddEnvWherePart;
	}

	public void setAddEnvWherePart(boolean isAddEnvWherePart) {
		this.isAddEnvWherePart = isAddEnvWherePart;
	}

	public String getDataPowerColumn() {
		if (dataPowerColumn == null) {
			return getPkFieldCode();
		}
		return dataPowerColumn;
	}

	public void setDataPowerColumn(String dataPowerColumn) {
		this.dataPowerColumn = dataPowerColumn;
	}

	public boolean isHistoryVersionRef() {
		return isHistoryVersionRef;
	}

	public void setHistoryVersionRef(boolean isHistoryVersionRef) {
		this.isHistoryVersionRef = isHistoryVersionRef;
	}

	public String getVersionDataLabelName() {
		return versionDataLabelName;
	}

	public void setVersionDataLabelName(String versionDataLabelName) {
		this.versionDataLabelName = versionDataLabelName;
	}

	// for query templet
	public String getPKFieldRefSql() {
		// todo
		return getSql("", new String[] { getPkFieldCode() }, null,
				getTableName(), getWherePart(), null, null);

	}

	public void setBlurQueryTableName(String m_blurQueryTableName) {
		this.m_blurQueryTableName = m_blurQueryTableName;
	}

	public String getBlurQueryTableName() {
		return m_blurQueryTableName;
	}
	
	public void setBlur(boolean isBlur) {
		this.isBlur = isBlur;
	}

	public boolean isBlur() {
		return isBlur;
	}

	public void setAllMatching(boolean isAllMatching) {
		this.isAllMatching = isAllMatching;
	}

	public boolean isAllMatching() {
		return isAllMatching;
	}

	public void setQueryFromServer(boolean queryFromServer) {
		this.m_queryFromServer = queryFromServer;
	}

	public boolean isQueryFromServer() {
		return m_queryFromServer;
	}

}