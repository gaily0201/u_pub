package cn.gaily.pub.convert;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.vo.pub.BusinessException;
import cn.gaily.pub.itf.IBaseService;
import cn.gaily.pub.util.CommonUtil;
import cn.gaily.pub.util.ExcelUtil;

/**
 * <p>Title: DepartmentConverter</P>
 * <p>Description: Excel??¦Ë?????????</p>
 * <p>Copyright: ????????????????? Copyright (c) 2014</p>
 * @author xiaoh
 * @version 1.0
 * @since 2014-10-14
 */
public class DepartmentConverter {

	
	private static IBaseService service = NCLocator.getInstance().lookup(IBaseService.class);

	/**
	 * ??¦Ë???-??¦Ë?????Map
	 */
	private static Map<String,String> orgNameMap = new HashMap<String,String>();
	/**
	 * ??¦Ë???-??¦Ë??????Map
	 */
	private static Map<String,String> orgCodeMap = new HashMap<String,String>();
	
	@SuppressWarnings("unused")
	private static void beforeConvert(File sourceFile,String sheetName) throws IOException, BusinessException{
		Map<String,List<String>> map = ExcelUtil.doImport(sourceFile);
		List<String> values = map.get(sheetName);
		if(values==null||values.size()<=0){
			return;
		}
		String[] rows = null;
		String[] rowValues = null;
		String source = null;
		String target = null;
		String pk_org = null;
		Map<String,String> namePkorgMap = service.getAllNamePkorgMap();
		for(String value :values){
			rows = value.split(",,");
			if(rows.length<=0){
				continue;
			}
			rowValues = value.split(",");
			if(rowValues.length<=0||rowValues.length!=2){ //TODO attention: just allowed two cols now,
														  //			    1:source vlaue, 2:target value
				continue;
			}
			source = rowValues[0];
			target = rowValues[1];
			if(CommonUtil.isEmpty(source)||CommonUtil.isEmpty(target)){
				continue;
			}
			
			orgNameMap.put(source, target);
			pk_org = namePkorgMap.get(target);
			if(CommonUtil.isNotEmpty(pk_org)){
				orgCodeMap.put(source, pk_org);
			}
		}
		return;
	}

	public static String convert(String value,int type) throws BusinessException{
		if(orgNameMap.isEmpty()||orgCodeMap.isEmpty()){
			throw new BusinessException("???????DepartmentConverter.beforeConvert(File sourceFile,String sheetName)????????????.");
		}
		if(type!=0&&type!=1){
			throw new BusinessException("????????,??????[0/1],0??????????, 1???????pk_org, 2??????????.");
		}
		
		if(type==0){
			return orgNameMap.get(value);
		}else if(type==1){
			return orgCodeMap.get(value);
		}
		return null;
	}

}
