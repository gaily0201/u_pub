package cn.gaily.pub.util;

import nc.vo.pub.IAttributeMeta;
import nc.vo.pub.SuperVO;

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
	
}
