package cn.gaily.pub.test;
import nc.bs.framework.test.AbstractTestCase;
import cn.gaily.pub.util.CommonUtil;


public class TestCommonUtil extends AbstractTestCase {

	
	public void commonUtil(){
		
		
		String date1 = CommonUtil.getCurrentDate(CommonUtil.DATE_FORMATER_YYYY);
		String date2 = CommonUtil.getCurrentDate(CommonUtil.DATE_FORMATER_YYYY_MM_DD);
		String date3 = CommonUtil.getCurrentDate(CommonUtil.DATE_FORMATER_YYYY_MM_DD_TIME);
		String date4 = CommonUtil.getCurrentDate(CommonUtil.DATE_FORMATER_YYYYMM);
		String date5 = CommonUtil.getCurrentDate(CommonUtil.DATE_FORMATER_YYYYMMDD);
		System.out.println(date1);
		System.out.println(date2);
		System.out.println(date3);
		System.out.println(date4);
		System.out.println(date5);
		/**
		 	2014
			2014-09-14
			2014-09-14 15:04:55
			201409
			20140914
		 */
		
		String vdate1 = new String("2014");
		String vdate2 = new String("2014-09-14");
		String vdate3 = new String("2014-09-14 15:04:55");
		String vdate4 = new String("201409");
		String vdate5 = new String("20140914");
		boolean b1 = CommonUtil.isValidDate(vdate1, CommonUtil.DATE_FORMATER_YYYY);
		boolean b2 = CommonUtil.isValidDate(vdate2, CommonUtil.DATE_FORMATER_YYYY_MM_DD);
		boolean b3 = CommonUtil.isValidDate(vdate3, CommonUtil.DATE_FORMATER_YYYY_MM_DD_TIME);
		boolean b4 = CommonUtil.isValidDate(vdate4, CommonUtil.DATE_FORMATER_YYYYMM);
		boolean b5 = CommonUtil.isValidDate(vdate5, CommonUtil.DATE_FORMATER_YYYYMMDD);
		System.out.println(b1);
		System.out.println(b2);
		System.out.println(b3);
		System.out.println(b4);
		System.out.println(b5);
		/**
		 	true
			true
			true
			true
			true
		 */
		
		System.out.println(CommonUtil.formatDate("20140914"));
	}
	
}
