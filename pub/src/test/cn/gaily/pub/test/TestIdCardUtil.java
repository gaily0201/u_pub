package cn.gaily.pub.test;

import cn.gaily.pub.util.IdCardUtil;
import nc.bs.framework.test.AbstractTestCase;

public class TestIdCardUtil extends AbstractTestCase{

	
	public void test(){
		String id = "362428199207204610";
		boolean valid = IdCardUtil.isVilidate(id);
		String birth = IdCardUtil.getPersonBirth(id).toString();
		String sex  =IdCardUtil.getPersonSex(id);
		System.out.println(valid);
		System.out.println(birth);
		System.out.println(sex);
	}
}
