package cn.gaily.base.ui.util;

import cn.gaily.pub.util.ExcelExporter;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.pubapp.pub.smart.IBillQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.ui.ml.NCLangRes;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.IAttributeMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;

/**
 * <p>Title: VOUtil</P>
 * <p>Description: </p>
 * <p>Copyright: ��������������޹�˾ Copyright (c) 2014</p>
 * <p>VOUtil.getVOColumnType(VOName, savePath)</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-16
 */
public class VOUtil {

	/**
	 * <p>�������ƣ�getVOColumnType</p>
	 * <p>������������ȡVO�е����ͣ�������excel�ļ�??������������ִ��,  ��������Ҫ������??/p>
	 * @param voClass 	voʵ��??
	 * @param savePath	����·��, ����.xls��׺
	 * @author xiaoh
	 * @since  2014-10-16
	 * <p> history 2014-10-16 xiaoh  ����   <p>
	 */
	public static void getVOColumnType(Class<?> voClass, String savePath){
		try {
			SuperVO vo  = (SuperVO) voClass.newInstance();
			IAttributeMeta[] metas = vo.getMetaData().getAttributes();
			String[][] datas =null;
			for(int i=0;i<metas.length;i++){
				if(datas==null){
					datas = new String[metas.length][2];
				}
				datas[i][0] = metas[i].getName();
				datas[i][1] = metas[i].getJavaType().toString();
			}
			ExcelExporter export = new ExcelExporter();
			export.setData(datas);
			export.exportExcelFile(false, savePath);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>�������ƣ�refresh</p>
	 * <p>����������ˢ�µ���</p>
	 * @param model
	 * @throws Exception
	 * @author xiaoh
	 * @since  2014-10-31
	 * <p> history 2014-10-31 xiaoh  ����   <p>
	 */
	public static void refresh(AbstractAppModel model) throws Exception{
		Object obj = model.getSelectedData();
	    if (obj != null) {
	      if (obj instanceof SuperVO) {
	        SuperVO oldVO = (SuperVO) obj;
	        SuperVO newVO = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPK(oldVO.getClass(),
	                oldVO.getPrimaryKey(), false);
	        if (newVO == null) {
	          throw new BusinessException(NCLangRes.getInstance().getStrByID("uif2", "RefreshSingleAction-000000"));
	        }
	        model.directlyUpdate(newVO);
	      }
	      else if (obj instanceof AbstractBill) {
	        AbstractBill oldVO = (AbstractBill) obj;
	        String pk = oldVO.getParentVO().getPrimaryKey();
	        IBillQueryService billQuery = NCLocator.getInstance().lookup(IBillQueryService.class);
	        AggregatedValueObject newVO = billQuery.querySingleBillByPk(oldVO.getClass(), pk);
	        if (newVO == null) {
	          throw new BusinessException(NCLangRes.getInstance().getStrByID(
	              "uif2", "RefreshSingleAction-000000"));
	        }
	        model.directlyUpdate(newVO);
	      }
	      else {
	        Logger.debug("Ŀǰֻ֧��SuperVO�ṹ������");
	      }
	    }
	    
	    ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("pubapp_0", "0pubapp-0267"), model.getContext());
	}
}
