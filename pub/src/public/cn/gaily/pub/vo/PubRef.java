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
*备用字段1
*/
public static final String ATTR01="attr01";
/**
*备用字段2
*/
public static final String ATTR02="attr02";
/**
*备用字段3
*/
public static final String ATTR03="attr03";
/**
*备用字段4
*/
public static final String ATTR04="attr04";
/**
*备用字段5
*/
public static final String ATTR05="attr05";
/**
*主类别编码
*/
public static final String BASECODE="basecode";
/**
*主类别名称
*/
public static final String BASENAME="basename";
/**
*子类别编码
*/
public static final String CHILDCODE="childcode";
/**
*子类别名称
*/
public static final String CHILDNAME="childname";
/**
*排序字段
*/
public static final String ORDERPART="orderpart";
/**
*主键
*/
public static final String PK="pk";
/**
*时间戳
*/
public static final String TS="ts";
/** 
* 获取备用字段1
*
* @return 备用字段1
*/
public String getAttr01 () {
return (String) this.getAttributeValue( PubRef.ATTR01);
 } 

/** 
* 设置备用字段1
*
* @param attr01 备用字段1
*/
public void setAttr01 ( String attr01) {
this.setAttributeValue( PubRef.ATTR01,attr01);
 } 

/** 
* 获取备用字段2
*
* @return 备用字段2
*/
public String getAttr02 () {
return (String) this.getAttributeValue( PubRef.ATTR02);
 } 

/** 
* 设置备用字段2
*
* @param attr02 备用字段2
*/
public void setAttr02 ( String attr02) {
this.setAttributeValue( PubRef.ATTR02,attr02);
 } 

/** 
* 获取备用字段3
*
* @return 备用字段3
*/
public String getAttr03 () {
return (String) this.getAttributeValue( PubRef.ATTR03);
 } 

/** 
* 设置备用字段3
*
* @param attr03 备用字段3
*/
public void setAttr03 ( String attr03) {
this.setAttributeValue( PubRef.ATTR03,attr03);
 } 

/** 
* 获取备用字段4
*
* @return 备用字段4
*/
public String getAttr04 () {
return (String) this.getAttributeValue( PubRef.ATTR04);
 } 

/** 
* 设置备用字段4
*
* @param attr04 备用字段4
*/
public void setAttr04 ( String attr04) {
this.setAttributeValue( PubRef.ATTR04,attr04);
 } 

/** 
* 获取备用字段5
*
* @return 备用字段5
*/
public String getAttr05 () {
return (String) this.getAttributeValue( PubRef.ATTR05);
 } 

/** 
* 设置备用字段5
*
* @param attr05 备用字段5
*/
public void setAttr05 ( String attr05) {
this.setAttributeValue( PubRef.ATTR05,attr05);
 } 

/** 
* 获取主类别编码
*
* @return 主类别编码
*/
public String getBasecode () {
return (String) this.getAttributeValue( PubRef.BASECODE);
 } 

/** 
* 设置主类别编码
*
* @param basecode 主类别编码
*/
public void setBasecode ( String basecode) {
this.setAttributeValue( PubRef.BASECODE,basecode);
 } 

/** 
* 获取主类别名称
*
* @return 主类别名称
*/
public String getBasename () {
return (String) this.getAttributeValue( PubRef.BASENAME);
 } 

/** 
* 设置主类别名称
*
* @param basename 主类别名称
*/
public void setBasename ( String basename) {
this.setAttributeValue( PubRef.BASENAME,basename);
 } 

/** 
* 获取子类别编码
*
* @return 子类别编码
*/
public String getChildcode () {
return (String) this.getAttributeValue( PubRef.CHILDCODE);
 } 

/** 
* 设置子类别编码
*
* @param childcode 子类别编码
*/
public void setChildcode ( String childcode) {
this.setAttributeValue( PubRef.CHILDCODE,childcode);
 } 

/** 
* 获取子类别名称
*
* @return 子类别名称
*/
public String getChildname () {
return (String) this.getAttributeValue( PubRef.CHILDNAME);
 } 

/** 
* 设置子类别名称
*
* @param childname 子类别名称
*/
public void setChildname ( String childname) {
this.setAttributeValue( PubRef.CHILDNAME,childname);
 } 

/** 
* 获取排序字段
*
* @return 排序字段
*/
public String getOrderpart () {
return (String) this.getAttributeValue( PubRef.ORDERPART);
 } 

/** 
* 设置排序字段
*
* @param orderpart 排序字段
*/
public void setOrderpart ( String orderpart) {
this.setAttributeValue( PubRef.ORDERPART,orderpart);
 } 

/** 
* 获取主键
*
* @return 主键
*/
public String getPk () {
return (String) this.getAttributeValue( PubRef.PK);
 } 

/** 
* 设置主键
*
* @param pk 主键
*/
public void setPk ( String pk) {
this.setAttributeValue( PubRef.PK,pk);
 } 

/** 
* 获取时间戳
*
* @return 时间戳
*/
public UFDateTime getTs () {
return (UFDateTime) this.getAttributeValue( PubRef.TS);
 } 

/** 
* 设置时间戳
*
* @param ts 时间戳
*/
public void setTs ( UFDateTime ts) {
this.setAttributeValue( PubRef.TS,ts);
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("pub.pubref");
  }
}