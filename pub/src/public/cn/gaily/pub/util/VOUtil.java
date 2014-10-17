package cn.gaily.pub.util;

import nc.vo.pub.IAttributeMeta;
import nc.vo.pub.SuperVO;

/**
 * <p>Title: VOUtil</P>
 * <p>Description: </p>
 * <p>Copyright: 用友政务软件有限公司 Copyright (c) 2014</p>
 * <p>VOUtil.getVOColumnType(VOName, savePath)</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-16
 */
public class VOUtil {

	/**
	 * <p>方法名称：getVOColumnType</p>
	 * <p>方法描述：获取VO列的类型，导出到excel文件??启动服务器后执行,  参照类需要单独对??/p>
	 * @param voClass 	vo实体??
	 * @param savePath	保存路径, 加上.xls后缀
	 * @author xiaoh
	 * @since  2014-10-16
	 * <p> history 2014-10-16 xiaoh  创建   <p>
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
