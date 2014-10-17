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
*������С
*/
public static final String BATCHSIZE="batchsize";
/**
*����ʱ��
*/
public static final String BILLDATE="billdate";
/**
*������
*/
public static final String BILLMAKER="billmaker";
/**
*Ŀ������Դ
*/
public static final String DSTDATASOURCE="dstdatasource";
/**
*Ŀ�����
*/
public static final String DSTTABNAME="dsttabname";
/**
*��������
*/
public String id;
/**
*�Ƿ�����
*/
public static final String ISBATCH="isbatch";
/**
*�޸�ʱ��
*/
public static final String MODIFIEDTIME="modifiedtime";
/**
*�޸���
*/
public static final String MODIFIER="modifier";
/**
*��ע
*/
public static final String REMARK="remark";
/**
*Դ����Դ
*/
public static final String SRCDATASOURCE="srcdatasource";
/**
*Դ������
*/
public static final String SRCTABNAME="srctabname";
/**
*ʱ���
*/
public UFDateTime ts;
/** 
* ��ȡ������С
*
* @return ������С
*/
public Integer getBatchsize () {
return (Integer) this.getAttributeValue( BaseInfo.BATCHSIZE);
 } 

/** 
* ����������С
*
* @param batchsize ������С
*/
public void setBatchsize ( Integer batchsize) {
this.setAttributeValue( BaseInfo.BATCHSIZE,batchsize);
 } 

/** 
* ��ȡ����ʱ��
*
* @return ����ʱ��
*/
public UFDate getBilldate () {
return (UFDate) this.getAttributeValue( BaseInfo.BILLDATE);
 } 

/** 
* ���ô���ʱ��
*
* @param billdate ����ʱ��
*/
public void setBilldate ( UFDate billdate) {
this.setAttributeValue( BaseInfo.BILLDATE,billdate);
 } 

/** 
* ��ȡ������
*
* @return ������
*/
public String getBillmaker () {
return (String) this.getAttributeValue( BaseInfo.BILLMAKER);
 } 

/** 
* ���ô�����
*
* @param billmaker ������
*/
public void setBillmaker ( String billmaker) {
this.setAttributeValue( BaseInfo.BILLMAKER,billmaker);
 } 

/** 
* ��ȡĿ������Դ
*
* @return Ŀ������Դ
*/
public String getDstdatasource () {
return (String) this.getAttributeValue( BaseInfo.DSTDATASOURCE);
 } 

/** 
* ����Ŀ������Դ
*
* @param dstdatasource Ŀ������Դ
*/
public void setDstdatasource ( String dstdatasource) {
this.setAttributeValue( BaseInfo.DSTDATASOURCE,dstdatasource);
 } 

/** 
* ��ȡĿ�����
*
* @return Ŀ�����
*/
public String getDsttabname () {
return (String) this.getAttributeValue( BaseInfo.DSTTABNAME);
 } 

/** 
* ����Ŀ�����
*
* @param dsttabname Ŀ�����
*/
public void setDsttabname ( String dsttabname) {
this.setAttributeValue( BaseInfo.DSTTABNAME,dsttabname);
 } 

/** 
* ��ȡ��������
*
* @return ��������
*/
public String getId () {
return this.id;
 } 

/** 
* ������������
*
* @param id ��������
*/
public void setId ( String id) {
this.id=id;
 } 

/** 
* ��ȡ�Ƿ�����
*
* @return �Ƿ�����
*/
public String getIsbatch () {
return (String) this.getAttributeValue( BaseInfo.ISBATCH);
 } 

/** 
* �����Ƿ�����
*
* @param isbatch �Ƿ�����
*/
public void setIsbatch ( String isbatch) {
this.setAttributeValue( BaseInfo.ISBATCH,isbatch);
 } 

/** 
* ��ȡ�޸�ʱ��
*
* @return �޸�ʱ��
*/
public UFDateTime getModifiedtime () {
return (UFDateTime) this.getAttributeValue( BaseInfo.MODIFIEDTIME);
 } 

/** 
* �����޸�ʱ��
*
* @param modifiedtime �޸�ʱ��
*/
public void setModifiedtime ( UFDateTime modifiedtime) {
this.setAttributeValue( BaseInfo.MODIFIEDTIME,modifiedtime);
 } 

/** 
* ��ȡ�޸���
*
* @return �޸���
*/
public String getModifier () {
return (String) this.getAttributeValue( BaseInfo.MODIFIER);
 } 

/** 
* �����޸���
*
* @param modifier �޸���
*/
public void setModifier ( String modifier) {
this.setAttributeValue( BaseInfo.MODIFIER,modifier);
 } 

/** 
* ��ȡ��ע
*
* @return ��ע
*/
public String getRemark () {
return (String) this.getAttributeValue( BaseInfo.REMARK);
 } 

/** 
* ���ñ�ע
*
* @param remark ��ע
*/
public void setRemark ( String remark) {
this.setAttributeValue( BaseInfo.REMARK,remark);
 } 

/** 
* ��ȡԴ����Դ
*
* @return Դ����Դ
*/
public String getSrcdatasource () {
return (String) this.getAttributeValue( BaseInfo.SRCDATASOURCE);
 } 

/** 
* ����Դ����Դ
*
* @param srcdatasource Դ����Դ
*/
public void setSrcdatasource ( String srcdatasource) {
this.setAttributeValue( BaseInfo.SRCDATASOURCE,srcdatasource);
 } 

/** 
* ��ȡԴ������
*
* @return Դ������
*/
public String getSrctabname () {
return (String) this.getAttributeValue( BaseInfo.SRCTABNAME);
 } 

/** 
* ����Դ������
*
* @param srctabname Դ������
*/
public void setSrctabname ( String srctabname) {
this.setAttributeValue( BaseInfo.SRCTABNAME,srctabname);
 } 

/** 
* ��ȡʱ���
*
* @return ʱ���
*/
public UFDateTime getTs () {
return this.ts;
 } 

/** 
* ����ʱ���
*
* @param ts ʱ���
*/
public void setTs ( UFDateTime ts) {
this.ts=ts;
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("pub.BaseInfo");
  }
}