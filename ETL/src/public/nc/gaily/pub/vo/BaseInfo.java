package nc.gaily.pub.vo;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class BaseInfo extends SuperVO {
/**
*批量大小
*/
public static final String BATCHSIZE="batchsize";
/**
*创建时间
*/
public static final String BILLDATE="billdate";
/**
*创建人
*/
public static final String BILLMAKER="billmaker";
/**
*目标数据源
*/
public static final String DSTDATASOURCE="dstdatasource";
/**
*目标表名
*/
public static final String DSTTABNAME="dsttabname";
/**
*主表主键
*/
public String id;
/**
*是否批量
*/
public static final String ISBATCH="isbatch";
/**
*修改时间
*/
public static final String MODIFIEDTIME="modifiedtime";
/**
*修改人
*/
public static final String MODIFIER="modifier";
/**
*备注
*/
public static final String REMARK="remark";
/**
*源数据源
*/
public static final String SRCDATASOURCE="srcdatasource";
/**
*源表名称
*/
public static final String SRCTABNAME="srctabname";
/**
*时间戳
*/
public UFDateTime ts;
/** 
* 获取批量大小
*
* @return 批量大小
*/
public Integer getBatchsize () {
return (Integer) this.getAttributeValue( BaseInfo.BATCHSIZE);
 } 

/** 
* 设置批量大小
*
* @param batchsize 批量大小
*/
public void setBatchsize ( Integer batchsize) {
this.setAttributeValue( BaseInfo.BATCHSIZE,batchsize);
 } 

/** 
* 获取创建时间
*
* @return 创建时间
*/
public UFDate getBilldate () {
return (UFDate) this.getAttributeValue( BaseInfo.BILLDATE);
 } 

/** 
* 设置创建时间
*
* @param billdate 创建时间
*/
public void setBilldate ( UFDate billdate) {
this.setAttributeValue( BaseInfo.BILLDATE,billdate);
 } 

/** 
* 获取创建人
*
* @return 创建人
*/
public String getBillmaker () {
return (String) this.getAttributeValue( BaseInfo.BILLMAKER);
 } 

/** 
* 设置创建人
*
* @param billmaker 创建人
*/
public void setBillmaker ( String billmaker) {
this.setAttributeValue( BaseInfo.BILLMAKER,billmaker);
 } 

/** 
* 获取目标数据源
*
* @return 目标数据源
*/
public String getDstdatasource () {
return (String) this.getAttributeValue( BaseInfo.DSTDATASOURCE);
 } 

/** 
* 设置目标数据源
*
* @param dstdatasource 目标数据源
*/
public void setDstdatasource ( String dstdatasource) {
this.setAttributeValue( BaseInfo.DSTDATASOURCE,dstdatasource);
 } 

/** 
* 获取目标表名
*
* @return 目标表名
*/
public String getDsttabname () {
return (String) this.getAttributeValue( BaseInfo.DSTTABNAME);
 } 

/** 
* 设置目标表名
*
* @param dsttabname 目标表名
*/
public void setDsttabname ( String dsttabname) {
this.setAttributeValue( BaseInfo.DSTTABNAME,dsttabname);
 } 

/** 
* 获取主表主键
*
* @return 主表主键
*/
public String getId () {
return this.id;
 } 

/** 
* 设置主表主键
*
* @param id 主表主键
*/
public void setId ( String id) {
this.id=id;
 } 

/** 
* 获取是否批量
*
* @return 是否批量
*/
public String getIsbatch () {
return (String) this.getAttributeValue( BaseInfo.ISBATCH);
 } 

/** 
* 设置是否批量
*
* @param isbatch 是否批量
*/
public void setIsbatch ( String isbatch) {
this.setAttributeValue( BaseInfo.ISBATCH,isbatch);
 } 

/** 
* 获取修改时间
*
* @return 修改时间
*/
public UFDateTime getModifiedtime () {
return (UFDateTime) this.getAttributeValue( BaseInfo.MODIFIEDTIME);
 } 

/** 
* 设置修改时间
*
* @param modifiedtime 修改时间
*/
public void setModifiedtime ( UFDateTime modifiedtime) {
this.setAttributeValue( BaseInfo.MODIFIEDTIME,modifiedtime);
 } 

/** 
* 获取修改人
*
* @return 修改人
*/
public String getModifier () {
return (String) this.getAttributeValue( BaseInfo.MODIFIER);
 } 

/** 
* 设置修改人
*
* @param modifier 修改人
*/
public void setModifier ( String modifier) {
this.setAttributeValue( BaseInfo.MODIFIER,modifier);
 } 

/** 
* 获取备注
*
* @return 备注
*/
public String getRemark () {
return (String) this.getAttributeValue( BaseInfo.REMARK);
 } 

/** 
* 设置备注
*
* @param remark 备注
*/
public void setRemark ( String remark) {
this.setAttributeValue( BaseInfo.REMARK,remark);
 } 

/** 
* 获取源数据源
*
* @return 源数据源
*/
public String getSrcdatasource () {
return (String) this.getAttributeValue( BaseInfo.SRCDATASOURCE);
 } 

/** 
* 设置源数据源
*
* @param srcdatasource 源数据源
*/
public void setSrcdatasource ( String srcdatasource) {
this.setAttributeValue( BaseInfo.SRCDATASOURCE,srcdatasource);
 } 

/** 
* 获取源表名称
*
* @return 源表名称
*/
public String getSrctabname () {
return (String) this.getAttributeValue( BaseInfo.SRCTABNAME);
 } 

/** 
* 设置源表名称
*
* @param srctabname 源表名称
*/
public void setSrctabname ( String srctabname) {
this.setAttributeValue( BaseInfo.SRCTABNAME,srctabname);
 } 

/** 
* 获取时间戳
*
* @return 时间戳
*/
public UFDateTime getTs () {
return this.ts;
 } 

/** 
* 设置时间戳
*
* @param ts 时间戳
*/
public void setTs ( UFDateTime ts) {
this.ts=ts;
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("pub.BaseInfo");
  }
}