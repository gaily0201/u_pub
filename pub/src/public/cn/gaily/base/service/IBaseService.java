package cn.gaily.base.service;

import java.util.List;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pubapp.bill.pagination.PaginationTransferObject;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

/**
 * <p>Title: IBaseService</P>
 * <p>Description: ��������ӿ�</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * @version 1.0
 */
public interface IBaseService {

	/**
	 * <p>�������ƣ�save</p>
	 * <p>��������������SuperVO</p>
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public <T extends SuperVO> T[] save(T[] vos) throws BusinessException;
	
	/**
	 * <p>�������ƣ�update</p>
	 * <p>��������������SuperVO</p>
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public <T extends SuperVO> T[] update(T[] vos) throws BusinessException;
	
	/**
	 * <p>�������ƣ�delete</p>
	 * <p>����������ɾ��SuperVO</p>
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public <T extends SuperVO> void delete(T[] vos) throws BusinessException;
	
	/**
	 * <p>�������ƣ�save</p>
	 * <p>��������������AggVO</p>
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public <T extends AbstractBill> T[] save(T[] vos) throws BusinessException;
	
	/**
	 * <p>�������ƣ�update</p>
	 * <p>��������������AggVO</p>
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public <T extends AbstractBill> T[] update(T[] vos) throws BusinessException;
	
	/**
	 * <p>�������ƣ�delete</p>
	 * <p>����������ɾ��AggVO</p>
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public <T extends AbstractBill> void delete(T[] vos) throws BusinessException;
	
	/**
	 * <p>�������ƣ�saveBills</p>
	 * <p>�������������浥��</p>
	 * @param bills ��Ҫ����ĵ��ݶ���
	 * @return
	 * @throws BusinessException 
	 */
	public <T extends AbstractBill>  T[] saveBills(T[] bills) throws BusinessException;

	/**
	 * <p>�������ƣ�deleteBills</p>
	 * <p>����������ɾ�����ݣ��߼�ɾ��</p>
	 * @param bills ��Ҫɾ���ĵ��ݶ���
	 * @throws BusinessException 
	 */
	public <T extends AbstractBill> void deleteBills(T[] bills) throws BusinessException;
	
	/**
	 * <p>�������ƣ�updateBills</p>
	 * <p>�������������µ��ݣ�ҵ�񵥾ݸ���</p>
	 * @param bills ��Ҫ���µĵ��ݶ���
	 * @return
	 * @throws BusinessException 
	 */
	public <T extends AbstractBill>  T[] updateBills(T[] bills) throws BusinessException;
	
	/**
	 * <p>�������ƣ�saveBills</p>
	 * <p>��������������������ĵ���</p>
	 * @param bills ���ݶ���
	 * @param files ����
	 * @param logs ��־
	 * @return
	 * @throws BusinessException
	 */
//	public <T extends AbstractBill>  T[] saveBills(T[] bills,MzAsFile[] files) throws BusinessException;
	
	/**
	 * <p>�������ƣ�updateBills</p>
	 * <p>�������������´������ĵ���</p>
	 * @param bills ���ݶ���
	 * @param files ����
	 * @param logs ��־
	 * @return
	 * @throws BusinessException
	 */
//	public <T extends AbstractBill>  T[] updateBills(T[] bills,MzAsFile[] files) throws BusinessException;
	
	/**
	 * <p>�������ƣ�deleteBills</p>
	 * <p>����������ɾ���������ĵ���</p>
	 * @param bills ���ݶ���
	 * @param files ����
	 * @param logs ��־
	 * @return
	 * @throws BusinessException
	 */
//	public <T extends AbstractBill>  T[] deleteBills(T[] bills,MzAsFile[] files) throws BusinessException;
	
	/**
	 * <p>�������ƣ�save</p>
	 * <p>�����������ύ</p>
	 * @param clientFullVOs
	 * @param originBills
	 * @param logs
	 * @return
	 * @throws BusinessException
	 */
	public <T extends AbstractBill>  T[] save(T[] clientFullVOs, T[] originBills) throws BusinessException ;
	
	/**
	 * <p>�������ƣ�unsave</p>
	 * <p>�����������ջ�</p>
	 * @param clientFullVOs
	 * @param originBills
	 * @param logs
	 * @return
	 * @throws BusinessException
	 */
	public <T extends AbstractBill>  T[] unsave(T[] clientFullVOs, T[] originBills) throws BusinessException ;
	
	/**
	 * <p>�������ƣ�approve</p>
	 * <p>��������������</p>
	 * @param clientFullVOs
	 * @param originBills
	 * @param logs
	 * @return
	 * @throws BusinessException
	 */
	public <T extends AbstractBill>  T[] approve(T[] clientFullVOs, T[] originBills) throws BusinessException ;
	
	/**
	 * <p>�������ƣ�unapprove</p>
	 * <p>��������������</p>
	 * @param clientFullVOs
	 * @param originBills
	 * @param logs
	 * @return
	 * @throws BusinessException
	 */
	public <T extends AbstractBill>  T[] unapprove(T[] clientFullVOs, T[] originBills) throws BusinessException ;
	
	
	/**
	 * <p>�������ƣ�queryByQueryScheme</p>
	 * <p>�������������ݲ�ѯ������ѯ���ݣ���ʹ�÷�ҳ��</p>
	 * @param queryScheme
	 * @return
	 * @throws BusinessException
	 */
	public <T extends AbstractBill>  T[] queryByQueryScheme(Class<T> clazz, IQueryScheme queryScheme) throws BusinessException;
   
	/**
	 * <p>�������ƣ�queryByPageQueryScheme</p>
	 * <p>������������ҳ��ѯ</p>
	 * @param queryScheme ��ѯ������Ϣ
	 * @param pageSize ҳ������
	 * @param clazz ����AggVO��
	 * @return
	 */
	public PaginationTransferObject queryByPageQueryScheme(IQueryScheme queryScheme, int pageSize, String clazz) throws BusinessException;
	
   /**
    * <p>�������ƣ�queryBillByPK</p>
    * <p>�����������������������ѯ����</p>
    * @param clazz ��ǰ��ѯ�����Ӧ��VO��
    * @param billIds ��������
    * @return
    * @throws BusinessException 
    */
	public <T extends AbstractBill>  T[] queryBillByPK(Class<T> clazz, String[] billIds) throws BusinessException;
	
	/**
	 * <p>�������ƣ�queryBillByPK</p>
	 * <p>�����������������������ѯ����</p>
	 * @param billIds ��������
	 * @param clazz ��ǰ��ѯ�����Ӧ��VO���ȫ·��
	 * @return
	 * @throws BusinessException
	 */
	public <T extends AbstractBill>  T[] queryBillByPK(String[] billIds, String clazz) throws BusinessException;
	
	/**
	 * <p>�������ƣ�querySuperVOByPK</p>
	 * <p>��������������������ѯSuperVO</p>
	 * @param clazz ��ǰSuperVO��
	 * @param pks ��������
	 * @return
	 * @throws BusinessException
	 */
	public <T extends SuperVO> T[] querySuperVOByPK(Class<T> clazz, String[] pks) throws BusinessException;
	
	/**
	 * <p>�������ƣ�querySuperVOByWhere</p>
	 * <p>�������������ݴ������where�ؼ��ֵ�sql������ѯSuperVO</p>
	 * @param clazz ��ǰSuperVO��
	 * @param whereSql  ������where �ؼ��ֺ���ʱ������Լ�����������Ψһ����Ҫ д�ľ���Ҫ��ѯ���ֶ�����vo�����Լ�dr=0����
	 * @return
	 * @throws BusinessException
	 */
	public <T extends SuperVO> T[] querySuperVOByWhere(Class<T> clazz, String whereSql) throws BusinessException;

   /**
    * <p>�������ƣ�queryPKSByScheme</p>
    * <p>�������������ݲ�ѯ������ѯ��������������</p>
    * @param clazz ��ǰ��ѯ�����Ӧ��VO��
    * @param queryScheme ��ѯ����
    * @return
    * @throws BusinessException 
    */
	public <T extends AbstractBill>  String[] queryPKSByScheme(Class<T> clazz, IQueryScheme queryScheme) throws BusinessException;
	
	/**
	 * <p>�������ƣ�queryPKSByScheme</p>
	 * <p>�������������ݲ�ѯ������ѯ��������������</p>
	 * @param queryScheme ��ѯ����
	 * @param clazz ��ǰ��ѯ�����Ӧ��VO���ȫ·��
	 * @return
	 * @throws BusinessException
	 */
	public <T extends AbstractBill>  String[] queryPKSByScheme(IQueryScheme queryScheme, String clazz) throws BusinessException;

	
   /**
    * <p>�������ƣ�queryPKSByWhere</p>
    * <p>��������������SQL������ѯ����</p>
    * @param clazz ��ѯ��AggVO��
    * @param sqlWhere ��where�ؼ��ֵ�����
    * @return
    * @throws BusinessException 
    */
	public <T extends AbstractBill> String[] queryPKSByWhere(Class<T> clazz, String sqlWhere) throws BusinessException;
	
	/**
	 * <p>�������ƣ�queryAggVOByWhere</p>
	 * <p>��������������SQL������ѯAggVO</p>
	 * @param clazz ��ѯ��AggVO��
	 * @param sqlWhere ��where�ؼ��ֵ�����
	 * @return
	 * @throws BusinessException
	 */
	public <T extends AbstractBill> T[] queryAggVOByWhere(Class<T> clazz, String sqlWhere) throws BusinessException;

	 /**
	   * <p>�������ƣ�execFunc</p>
	   * <p>����������ִ��һ������</p>
	   * <p><pre>
	   * ʹ�÷�����     
	   *      example1 : ��һ����������ΪSI_FUNC_GET_SALARY_YEAR(YEAR NUMBER),��ʹ�õ��÷������£�
	   *      // other logic 
	   *      ISIBaseService srv = .../// ��ȡ�������˵ķ������
	   *      PubSQLParameter param = new PubSQLParameter();
	   *      param.addParam(PubSQLParameter.SQL_PARAM_OUT,java.sql.Types.DOUBLE,null);
	   *      param.addParam(PubSQLParameter.SQL_PARAM_IN,java.sql.Types.Integer,2008);
	   *      List  list = srv.execFunc("SI_FUNC_GET_SALARY_YEAR",param);
	   *      double salary = list.isEmpty()? 0d:(double)list.get(0);
	   *      // other logic 
	   *      
	   *      ע�⣺
	   *           1�����ں����ĵ��ã�����Ӧ�ô���һ�������������Ϊ�����ķ���ֵ
	   *           2�������ڰ��еĺ���������ĺ������Ʊ��������������  PKG_COMMON_K.SI_FUNC_GET_SALARY_YEAR
	   *    </pre>
	   * @param funcName   ����������
	   * @param param      �����Ĳ���
	   * @return List  ����ֵ
	   * @throws BusinessException
	   */
//	public List<?> execFunc(String funcName, PubSQLParameter params) throws BusinessException;

	/**
	   * <p>�������ƣ�execProc</p>
	   * <p>����������ִ��һ���洢����</p>
	   * <p><pre>
	   * ʹ�÷�����     
	   *      example1 : ��һ���洢��������ΪSI_PROC_GENERATE_PLAN(UNIT_ID varchar2,MONTH NUMBER,ERROR_MSG VARCHAR2),��ʹ�õ��÷������£�
	   *      // other logic 
	   *      ISIBaseService srv = .../// ��ȡ�������˵ķ������
	   *      PubSQLParameter param = new PubSQLParameter();
	   *      param.addParam(PubSQLParameter.SQL_PARAM_IN,java.sql.Types.VARCHAR,'100101010');
	   *      param.addParam(PubSQLParameter.SQL_PARAM_IN,java.sql.Types.Integer,200806);
	   *      param.addParam(PubSQLParameter.SQL_PARAM_OUT,java.sql.Types.VARCHAR,null);
	   *      List  list = srv.execFunc("SI_PROC_GENERATE_PLAN",param);
	   *      String result = list.isEmpty()? null:(String)list.get(0);
	   *      // other logic 
	   *      
	   *      ע�⣺1�������ڰ��еĴ洢���̣�����Ĵ洢�������Ʊ��������������  PKG_COMMON_K.SI_PROC_GENERATE_PLAN
	   * </pre>
	   * @param procName    �洢�������ƣ���Ҫʱ��������
	   * @param param       �洢���̲�������������������������
	   * @return List       �洢���̷������ֵ
	   * @throws BusinessException
	   */
//	public List<?> execProc(String procName, PubSQLParameter params) throws BusinessException;

	/**
	   * <p>�������ƣ�execReportFunc</p>
	   * <p>����������ִ��һ���������(�����洢���̺ͺ���)�����������ʽ���Բο� ISIBaseService.execFunc(..)</p>
	   * <p>���ӿ��ṩ��Ŀ���Ǵ����ӡ��������Դ���ݲ�ѯ�����⣬����UAP��ӡ����Դ��Ҫһ��Map��ʽ�Ĳ�����������<br>
	   * ��Ա������SQL���ֱ��д���ڵ��õ�ACTION�У���������ӡ���ݵĲ������ݷ�������ʱ�������µ���JAVA�࣬Ϊ��<br>
	   * �����ⲿ�ֹ������ؽ����ݲ�ѯ���̷�װ���洢�����У���SQL��ִ��ͳһ��Oracle����PKG_A_COMMON.SI_FUNC_COMMON_QUERY_JSON<br>
	   * ������������Աֻ��Ҫ���Լ��Ĵ洢��������֯SQL����ز���������֯��ɵ�SQL��������Oracle����������JSON��ʽ������<br>
	   * ��PubSQLResult��������ݽ��з��������Map��ʽ�������Թ�����ҵ���Ӧ�á�</p>
	   * @param procName   �������ƣ� �����Ǵ洢���̣�Ҳ�����Ǻ���
	   * @param param      ���̵�ִ�в�������������������������
	   * @return PubSQLResult
	   * @throws BusinessException
	   */
//	public List<?> execReportFunc(String funcName, PubSQLParameter params) throws BusinessException;

    /**
     * <p>�������ƣ�getSequenceValue</p>
     * <p>������������ȡ���е���һ��ֵ�������е�ֵ�����κδ���ֱ�ӷ���</p>
     * @param seqName   ��������
     * @return          ���е�ֵ
     * @throws BusinessException
     */
//    public String getSequenceValue(String seqName) throws BusinessException ;
    
	/**
	 * <p>�������ƣ�getSequenceValue</p>
	 * <p>������������ȡ���е���һ��ֵ���Է��ص����и���ָ���ĳ��Ƚ�����䴦�����isAddTimeΪtrue��������ʱ�䣬�����е�ֵΪ1��ָ���ĳ���Ϊ20���ַ�<br>
	 * ��isAddTimeΪ���򣬷��ص�ֵ��ʽΪ��20121212241334000001</p>
	 * @param seqName      ���е�����
	 * @param seqLength    ����ֵ����䳤��
	 * @param isAddTime    ����ֵ����Ƿ����ʱ��ǰ׺
	 * @return ����ֵ
	 * @throws BusinessException
	 */
//	public String getSequenceValue(String seqName, int seqLength , boolean isAddTime) throws BusinessException;

}