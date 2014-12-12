package cn.gaily.pub.test;
import nc.bs.framework.test.AbstractTestCase;
import cn.gaily.pub.util.CommonUtils;


public class TestCommonUtil extends AbstractTestCase {

	
	public void test(){
		
		
		String date1 = CommonUtils.getCurrentDate(CommonUtils.DATE_FORMATER_YYYY);
		String date2 = CommonUtils.getCurrentDate(CommonUtils.DATE_FORMATER_YYYY_MM_DD);
		String date3 = CommonUtils.getCurrentDate(CommonUtils.DATE_FORMATER_YYYY_MM_DD_TIME);
		String date4 = CommonUtils.getCurrentDate(CommonUtils.DATE_FORMATER_YYYYMM);
		String date5 = CommonUtils.getCurrentDate(CommonUtils.DATE_FORMATER_YYYYMMDD);
		System.out.println(date1);
		System.out.println(date2);
		System.out.println(date3);
		System.out.println(date4);
		System.out.println(date5);
		System.out.println("#############################################");
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
		boolean b1 = CommonUtils.isValidDate(vdate1, CommonUtils.DATE_FORMATER_YYYY);
		boolean b2 = CommonUtils.isValidDate(vdate2, CommonUtils.DATE_FORMATER_YYYY_MM_DD);
		boolean b3 = CommonUtils.isValidDate(vdate3, CommonUtils.DATE_FORMATER_YYYY_MM_DD_TIME);
		boolean b4 = CommonUtils.isValidDate(vdate4, CommonUtils.DATE_FORMATER_YYYYMM);
		boolean b5 = CommonUtils.isValidDate(vdate5, CommonUtils.DATE_FORMATER_YYYYMMDD);
		System.out.println(b1);
		System.out.println(b2);
		System.out.println(b3);
		System.out.println(b4);
		System.out.println(b5);
		System.out.println("#############################################");
		/**
		 	true
			true
			true
			true
			true
		 */
		
		System.out.println(CommonUtils.formatDate("20140914"));
		
		
		System.out.println("#############################################");
		
		System.out.println(CommonUtils.isValidChn("chinese"));
		System.out.println(CommonUtils.isValidChn("жа"));
		
		System.out.println(CommonUtils.isValidInt(-11));
		System.out.println(CommonUtils.isValidFloat(1f));
		System.out.println(CommonUtils.isValidEmail("446029658qq.com"));
		
	}
	
}
