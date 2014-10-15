package nc.gaily.pub.vo;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class RelationInfo extends SuperVO {
	private static final long serialVersionUID = 6215609176827681391L;
/**
*默认值
*/
public static final String DEFAULTVALUE="defaultvalue";
/**
*目标字段长度
*/
public static final String DSTCOLLENGTH="dstcollength";
/**
*目标字段类型
*/
public static final String DSTCOLTYPE="dstcoltype";
/**
*目标字段名称
*/
public static final String DSTCOLUMN="dstcolumn";
/**
*目标是否为时间格式
*/
public static final String DSTISDATETIME="dstisdatetime";
/**
*目标是否为参照格式
*/
public static final String DSTISREF="dstisref";
/**
*目标字段参照字段
*/
public static final String DSTREFCOLUMN="dstrefcolumn";
/**
*目标字段参照表
*/
public static final String DSTREFTABLE="dstreftable";
/**
*目标参照表类别字段
*/
public static final String DSTREFTYPECOLUMN="dstreftypecolumn";
/**
*目标表参照类别值
*/
public static final String DSTREFTYPEVALUE="dstreftypevalue";
/**
*子表主键
*/
public static final String ID="id";
/**
*上层单据主键
*/
public static final String PID="pid";
/**
*源字段长度
*/
public static final String SRCCOLLENGTH="srccollength";
/**
*源字段类型
*/
public static final String SRCCOLTYPE="srccoltype";
/**
*源字段名称
*/
public static final String SRCCOLUMN="srccolumn";
/**
*源是否为时间格式
*/
public static final String SRCISDATETIME="srcisdatetime";
/**
*源是否为参照格式
*/
public static final String SRCISREF="srcisref";
/**
*源字段参照字段
*/
public static final String SRCREFCOLUMN="srcrefcolumn";
/**
*源字段参照表
*/
public static final String SRCREFTABLE="srcreftable";
/**
*源参照表类别字段
*/
public static final String SRCREFTYPECOLUMN="srcreftypecolumn";
/**
*源参照表类别值
*/
public static final String SRCREFTYPEVALUE="srcreftypevalue";
/**
*时间戳
*/
public static final String TS="ts";
/** 
* 获取默认值
*
* @return 默认值
*/
public String getDefaultvalue () {
return (String) this.getAttributeValue( RelationInfo.DEFAULTVALUE);
 } 

/** 
* 设置默认值
*
* @param defaultvalue 默认值
*/
public void setDefaultvalue ( String defaultvalue) {
this.setAttributeValue( RelationInfo.DEFAULTVALUE,defaultvalue);
 } 

/** 
* 获取目标字段长度
*
* @return 目标字段长度
*/
public String getDstcollength () {
return (String) this.getAttributeValue( RelationInfo.DSTCOLLENGTH);
 } 

/** 
* 设置目标字段长度
*
* @param dstcollength 目标字段长度
*/
public void setDstcollength ( String dstcollength) {
this.setAttributeValue( RelationInfo.DSTCOLLENGTH,dstcollength);
 } 

/** 
* 获取目标字段类型
*
* @return 目标字段类型
*/
public String getDstcoltype () {
return (String) this.getAttributeValue( RelationInfo.DSTCOLTYPE);
 } 

/** 
* 设置目标字段类型
*
* @param dstcoltype 目标字段类型
*/
public void setDstcoltype ( String dstcoltype) {
this.setAttributeValue( RelationInfo.DSTCOLTYPE,dstcoltype);
 } 

/** 
* 获取目标字段名称
*
* @return 目标字段名称
*/
public String getDstcolumn () {
return (String) this.getAttributeValue( RelationInfo.DSTCOLUMN);
 } 

/** 
* 设置目标字段名称
*
* @param dstcolumn 目标字段名称
*/
public void setDstcolumn ( String dstcolumn) {
this.setAttributeValue( RelationInfo.DSTCOLUMN,dstcolumn);
 } 

/** 
* 获取目标是否为时间格式
*
* @return 目标是否为时间格式
*/
public String getDstisdatetime () {
return (String) this.getAttributeValue( RelationInfo.DSTISDATETIME);
 } 

/** 
* 设置目标是否为时间格式
*
* @param dstisdatetime 目标是否为时间格式
*/
public void setDstisdatetime ( String dstisdatetime) {
this.setAttributeValue( RelationInfo.DSTISDATETIME,dstisdatetime);
 } 

/** 
* 获取目标是否为参照格式
*
* @return 目标是否为参照格式
*/
public String getDstisref () {
return (String) this.getAttributeValue( RelationInfo.DSTISREF);
 } 

/** 
* 设置目标是否为参照格式
*
* @param dstisref 目标是否为参照格式
*/
public void setDstisref ( String dstisref) {
this.setAttributeValue( RelationInfo.DSTISREF,dstisref);
 } 

/** 
* 获取目标字段参照字段
*
* @return 目标字段参照字段
*/
public String getDstrefcolumn () {
return (String) this.getAttributeValue( RelationInfo.DSTREFCOLUMN);
 } 

/** 
* 设置目标字段参照字段
*
* @param dstrefcolumn 目标字段参照字段
*/
public void setDstrefcolumn ( String dstrefcolumn) {
this.setAttributeValue( RelationInfo.DSTREFCOLUMN,dstrefcolumn);
 } 

/** 
* 获取目标字段参照表
*
* @return 目标字段参照表
*/
public String getDstreftable () {
return (String) this.getAttributeValue( RelationInfo.DSTREFTABLE);
 } 

/** 
* 设置目标字段参照表
*
* @param dstreftable 目标字段参照表
*/
public void setDstreftable ( String dstreftable) {
this.setAttributeValue( RelationInfo.DSTREFTABLE,dstreftable);
 } 

/** 
* 获取目标参照表类别字段
*
* @return 目标参照表类别字段
*/
public String getDstreftypecolumn () {
return (String) this.getAttributeValue( RelationInfo.DSTREFTYPECOLUMN);
 } 

/** 
* 设置目标参照表类别字段
*
* @param dstreftypecolumn 目标参照表类别字段
*/
public void setDstreftypecolumn ( String dstreftypecolumn) {
this.setAttributeValue( RelationInfo.DSTREFTYPECOLUMN,dstreftypecolumn);
 } 

/** 
* 获取目标表参照类别值
*
* @return 目标表参照类别值
*/
public String getDstreftypevalue () {
return (String) this.getAttributeValue( RelationInfo.DSTREFTYPEVALUE);
 } 

/** 
* 设置目标表参照类别值
*
* @param dstreftypevalue 目标表参照类别值
*/
public void setDstreftypevalue ( String dstreftypevalue) {
this.setAttributeValue( RelationInfo.DSTREFTYPEVALUE,dstreftypevalue);
 } 

/** 
* 获取子表主键
*
* @return 子表主键
*/
public String getId () {
return (String) this.getAttributeValue( RelationInfo.ID);
 } 

/** 
* 设置子表主键
*
* @param id 子表主键
*/
public void setId ( String id) {
this.setAttributeValue( RelationInfo.ID,id);
 } 

/** 
* 获取上层单据主键
*
* @return 上层单据主键
*/
public String getPid () {
return (String) this.getAttributeValue( RelationInfo.PID);
 } 

/** 
* 设置上层单据主键
*
* @param pid 上层单据主键
*/
public void setPid ( String pid) {
this.setAttributeValue( RelationInfo.PID,pid);
 } 

/** 
* 获取源字段长度
*
* @return 源字段长度
*/
public String getSrccollength () {
return (String) this.getAttributeValue( RelationInfo.SRCCOLLENGTH);
 } 

/** 
* 设置源字段长度
*
* @param srccollength 源字段长度
*/
public void setSrccollength ( String srccollength) {
this.setAttributeValue( RelationInfo.SRCCOLLENGTH,srccollength);
 } 

/** 
* 获取源字段类型
*
* @return 源字段类型
*/
public String getSrccoltype () {
return (String) this.getAttributeValue( RelationInfo.SRCCOLTYPE);
 } 

/** 
* 设置源字段类型
*
* @param srccoltype 源字段类型
*/
public void setSrccoltype ( String srccoltype) {
this.setAttributeValue( RelationInfo.SRCCOLTYPE,srccoltype);
 } 

/** 
* 获取源字段名称
*
* @return 源字段名称
*/
public String getSrccolumn () {
return (String) this.getAttributeValue( RelationInfo.SRCCOLUMN);
 } 

/** 
* 设置源字段名称
*
* @param srccolumn 源字段名称
*/
public void setSrccolumn ( String srccolumn) {
this.setAttributeValue( RelationInfo.SRCCOLUMN,srccolumn);
 } 

/** 
* 获取源是否为时间格式
*
* @return 源是否为时间格式
*/
public String getSrcisdatetime () {
return (String) this.getAttributeValue( RelationInfo.SRCISDATETIME);
 } 

/** 
* 设置源是否为时间格式
*
* @param srcisdatetime 源是否为时间格式
*/
public void setSrcisdatetime ( String srcisdatetime) {
this.setAttributeValue( RelationInfo.SRCISDATETIME,srcisdatetime);
 } 

/** 
* 获取源是否为参照格式
*
* @return 源是否为参照格式
*/
public String getSrcisref () {
return (String) this.getAttributeValue( RelationInfo.SRCISREF);
 } 

/** 
* 设置源是否为参照格式
*
* @param srcisref 源是否为参照格式
*/
public void setSrcisref ( String srcisref) {
this.setAttributeValue( RelationInfo.SRCISREF,srcisref);
 } 

/** 
* 获取源字段参照字段
*
* @return 源字段参照字段
*/
public String getSrcrefcolumn () {
return (String) this.getAttributeValue( RelationInfo.SRCREFCOLUMN);
 } 

/** 
* 设置源字段参照字段
*
* @param srcrefcolumn 源字段参照字段
*/
public void setSrcrefcolumn ( String srcrefcolumn) {
this.setAttributeValue( RelationInfo.SRCREFCOLUMN,srcrefcolumn);
 } 

/** 
* 获取源字段参照表
*
* @return 源字段参照表
*/
public String getSrcreftable () {
return (String) this.getAttributeValue( RelationInfo.SRCREFTABLE);
 } 

/** 
* 设置源字段参照表
*
* @param srcreftable 源字段参照表
*/
public void setSrcreftable ( String srcreftable) {
this.setAttributeValue( RelationInfo.SRCREFTABLE,srcreftable);
 } 

/** 
* 获取源参照表类别字段
*
* @return 源参照表类别字段
*/
public String getSrcreftypecolumn () {
return (String) this.getAttributeValue( RelationInfo.SRCREFTYPECOLUMN);
 } 

/** 
* 设置源参照表类别字段
*
* @param srcreftypecolumn 源参照表类别字段
*/
public void setSrcreftypecolumn ( String srcreftypecolumn) {
this.setAttributeValue( RelationInfo.SRCREFTYPECOLUMN,srcreftypecolumn);
 } 

/** 
* 获取源参照表类别值
*
* @return 源参照表类别值
*/
public String getSrcreftypevalue () {
return (String) this.getAttributeValue( RelationInfo.SRCREFTYPEVALUE);
 } 

/** 
* 设置源参照表类别值
*
* @param srcreftypevalue 源参照表类别值
*/
public void setSrcreftypevalue ( String srcreftypevalue) {
this.setAttributeValue( RelationInfo.SRCREFTYPEVALUE,srcreftypevalue);
 } 

/** 
* 获取时间戳
*
* @return 时间戳
*/
public UFDateTime getTs () {
return (UFDateTime) this.getAttributeValue( RelationInfo.TS);
 } 

/** 
* 设置时间戳
*
* @param ts 时间戳
*/
public void setTs ( UFDateTime ts) {
this.setAttributeValue( RelationInfo.TS,ts);
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("pub.RelationInfo");
  }
}