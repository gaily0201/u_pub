package cn.gaily.pub.vo;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class PubRef extends SuperVO {
/**
*�����ֶ�1
*/
public static final String ATTR01="attr01";
/**
*�����ֶ�2
*/
public static final String ATTR02="attr02";
/**
*�����ֶ�3
*/
public static final String ATTR03="attr03";
/**
*�����ֶ�4
*/
public static final String ATTR04="attr04";
/**
*�����ֶ�5
*/
public static final String ATTR05="attr05";
/**
*��������
*/
public static final String BASECODE="basecode";
/**
*���������
*/
public static final String BASENAME="basename";
/**
*��������
*/
public static final String CHILDCODE="childcode";
/**
*���������
*/
public static final String CHILDNAME="childname";
/**
*�����ֶ�
*/
public static final String ORDERPART="orderpart";
/**
*����
*/
public static final String PK="pk";
/**
*ʱ���
*/
public static final String TS="ts";
/** 
* ��ȡ�����ֶ�1
*
* @return �����ֶ�1
*/
public String getAttr01 () {
return (String) this.getAttributeValue( PubRef.ATTR01);
 } 

/** 
* ���ñ����ֶ�1
*
* @param attr01 �����ֶ�1
*/
public void setAttr01 ( String attr01) {
this.setAttributeValue( PubRef.ATTR01,attr01);
 } 

/** 
* ��ȡ�����ֶ�2
*
* @return �����ֶ�2
*/
public String getAttr02 () {
return (String) this.getAttributeValue( PubRef.ATTR02);
 } 

/** 
* ���ñ����ֶ�2
*
* @param attr02 �����ֶ�2
*/
public void setAttr02 ( String attr02) {
this.setAttributeValue( PubRef.ATTR02,attr02);
 } 

/** 
* ��ȡ�����ֶ�3
*
* @return �����ֶ�3
*/
public String getAttr03 () {
return (String) this.getAttributeValue( PubRef.ATTR03);
 } 

/** 
* ���ñ����ֶ�3
*
* @param attr03 �����ֶ�3
*/
public void setAttr03 ( String attr03) {
this.setAttributeValue( PubRef.ATTR03,attr03);
 } 

/** 
* ��ȡ�����ֶ�4
*
* @return �����ֶ�4
*/
public String getAttr04 () {
return (String) this.getAttributeValue( PubRef.ATTR04);
 } 

/** 
* ���ñ����ֶ�4
*
* @param attr04 �����ֶ�4
*/
public void setAttr04 ( String attr04) {
this.setAttributeValue( PubRef.ATTR04,attr04);
 } 

/** 
* ��ȡ�����ֶ�5
*
* @return �����ֶ�5
*/
public String getAttr05 () {
return (String) this.getAttributeValue( PubRef.ATTR05);
 } 

/** 
* ���ñ����ֶ�5
*
* @param attr05 �����ֶ�5
*/
public void setAttr05 ( String attr05) {
this.setAttributeValue( PubRef.ATTR05,attr05);
 } 

/** 
* ��ȡ��������
*
* @return ��������
*/
public String getBasecode () {
return (String) this.getAttributeValue( PubRef.BASECODE);
 } 

/** 
* ������������
*
* @param basecode ��������
*/
public void setBasecode ( String basecode) {
this.setAttributeValue( PubRef.BASECODE,basecode);
 } 

/** 
* ��ȡ���������
*
* @return ���������
*/
public String getBasename () {
return (String) this.getAttributeValue( PubRef.BASENAME);
 } 

/** 
* �������������
*
* @param basename ���������
*/
public void setBasename ( String basename) {
this.setAttributeValue( PubRef.BASENAME,basename);
 } 

/** 
* ��ȡ��������
*
* @return ��������
*/
public String getChildcode () {
return (String) this.getAttributeValue( PubRef.CHILDCODE);
 } 

/** 
* ������������
*
* @param childcode ��������
*/
public void setChildcode ( String childcode) {
this.setAttributeValue( PubRef.CHILDCODE,childcode);
 } 

/** 
* ��ȡ���������
*
* @return ���������
*/
public String getChildname () {
return (String) this.getAttributeValue( PubRef.CHILDNAME);
 } 

/** 
* �������������
*
* @param childname ���������
*/
public void setChildname ( String childname) {
this.setAttributeValue( PubRef.CHILDNAME,childname);
 } 

/** 
* ��ȡ�����ֶ�
*
* @return �����ֶ�
*/
public String getOrderpart () {
return (String) this.getAttributeValue( PubRef.ORDERPART);
 } 

/** 
* ���������ֶ�
*
* @param orderpart �����ֶ�
*/
public void setOrderpart ( String orderpart) {
this.setAttributeValue( PubRef.ORDERPART,orderpart);
 } 

/** 
* ��ȡ����
*
* @return ����
*/
public String getPk () {
return (String) this.getAttributeValue( PubRef.PK);
 } 

/** 
* ��������
*
* @param pk ����
*/
public void setPk ( String pk) {
this.setAttributeValue( PubRef.PK,pk);
 } 

/** 
* ��ȡʱ���
*
* @return ʱ���
*/
public UFDateTime getTs () {
return (UFDateTime) this.getAttributeValue( PubRef.TS);
 } 

/** 
* ����ʱ���
*
* @param ts ʱ���
*/
public void setTs ( UFDateTime ts) {
this.setAttributeValue( PubRef.TS,ts);
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("pub.pubref");
  }
}