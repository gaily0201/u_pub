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
*Ĭ��ֵ
*/
public static final String DEFAULTVALUE="defaultvalue";
/**
*Ŀ���ֶγ���
*/
public static final String DSTCOLLENGTH="dstcollength";
/**
*Ŀ���ֶ�����
*/
public static final String DSTCOLTYPE="dstcoltype";
/**
*Ŀ���ֶ�����
*/
public static final String DSTCOLUMN="dstcolumn";
/**
*Ŀ���Ƿ�Ϊʱ���ʽ
*/
public static final String DSTISDATETIME="dstisdatetime";
/**
*Ŀ���Ƿ�Ϊ���ո�ʽ
*/
public static final String DSTISREF="dstisref";
/**
*Ŀ���ֶβ����ֶ�
*/
public static final String DSTREFCOLUMN="dstrefcolumn";
/**
*Ŀ���ֶβ��ձ�
*/
public static final String DSTREFTABLE="dstreftable";
/**
*Ŀ����ձ�����ֶ�
*/
public static final String DSTREFTYPECOLUMN="dstreftypecolumn";
/**
*Ŀ���������ֵ
*/
public static final String DSTREFTYPEVALUE="dstreftypevalue";
/**
*�ӱ�����
*/
public static final String ID="id";
/**
*�ϲ㵥������
*/
public static final String PID="pid";
/**
*Դ�ֶγ���
*/
public static final String SRCCOLLENGTH="srccollength";
/**
*Դ�ֶ�����
*/
public static final String SRCCOLTYPE="srccoltype";
/**
*Դ�ֶ�����
*/
public static final String SRCCOLUMN="srccolumn";
/**
*Դ�Ƿ�Ϊʱ���ʽ
*/
public static final String SRCISDATETIME="srcisdatetime";
/**
*Դ�Ƿ�Ϊ���ո�ʽ
*/
public static final String SRCISREF="srcisref";
/**
*Դ�ֶβ����ֶ�
*/
public static final String SRCREFCOLUMN="srcrefcolumn";
/**
*Դ�ֶβ��ձ�
*/
public static final String SRCREFTABLE="srcreftable";
/**
*Դ���ձ�����ֶ�
*/
public static final String SRCREFTYPECOLUMN="srcreftypecolumn";
/**
*Դ���ձ����ֵ
*/
public static final String SRCREFTYPEVALUE="srcreftypevalue";
/**
*ʱ���
*/
public static final String TS="ts";
/** 
* ��ȡĬ��ֵ
*
* @return Ĭ��ֵ
*/
public String getDefaultvalue () {
return (String) this.getAttributeValue( RelationInfo.DEFAULTVALUE);
 } 

/** 
* ����Ĭ��ֵ
*
* @param defaultvalue Ĭ��ֵ
*/
public void setDefaultvalue ( String defaultvalue) {
this.setAttributeValue( RelationInfo.DEFAULTVALUE,defaultvalue);
 } 

/** 
* ��ȡĿ���ֶγ���
*
* @return Ŀ���ֶγ���
*/
public String getDstcollength () {
return (String) this.getAttributeValue( RelationInfo.DSTCOLLENGTH);
 } 

/** 
* ����Ŀ���ֶγ���
*
* @param dstcollength Ŀ���ֶγ���
*/
public void setDstcollength ( String dstcollength) {
this.setAttributeValue( RelationInfo.DSTCOLLENGTH,dstcollength);
 } 

/** 
* ��ȡĿ���ֶ�����
*
* @return Ŀ���ֶ�����
*/
public String getDstcoltype () {
return (String) this.getAttributeValue( RelationInfo.DSTCOLTYPE);
 } 

/** 
* ����Ŀ���ֶ�����
*
* @param dstcoltype Ŀ���ֶ�����
*/
public void setDstcoltype ( String dstcoltype) {
this.setAttributeValue( RelationInfo.DSTCOLTYPE,dstcoltype);
 } 

/** 
* ��ȡĿ���ֶ�����
*
* @return Ŀ���ֶ�����
*/
public String getDstcolumn () {
return (String) this.getAttributeValue( RelationInfo.DSTCOLUMN);
 } 

/** 
* ����Ŀ���ֶ�����
*
* @param dstcolumn Ŀ���ֶ�����
*/
public void setDstcolumn ( String dstcolumn) {
this.setAttributeValue( RelationInfo.DSTCOLUMN,dstcolumn);
 } 

/** 
* ��ȡĿ���Ƿ�Ϊʱ���ʽ
*
* @return Ŀ���Ƿ�Ϊʱ���ʽ
*/
public String getDstisdatetime () {
return (String) this.getAttributeValue( RelationInfo.DSTISDATETIME);
 } 

/** 
* ����Ŀ���Ƿ�Ϊʱ���ʽ
*
* @param dstisdatetime Ŀ���Ƿ�Ϊʱ���ʽ
*/
public void setDstisdatetime ( String dstisdatetime) {
this.setAttributeValue( RelationInfo.DSTISDATETIME,dstisdatetime);
 } 

/** 
* ��ȡĿ���Ƿ�Ϊ���ո�ʽ
*
* @return Ŀ���Ƿ�Ϊ���ո�ʽ
*/
public String getDstisref () {
return (String) this.getAttributeValue( RelationInfo.DSTISREF);
 } 

/** 
* ����Ŀ���Ƿ�Ϊ���ո�ʽ
*
* @param dstisref Ŀ���Ƿ�Ϊ���ո�ʽ
*/
public void setDstisref ( String dstisref) {
this.setAttributeValue( RelationInfo.DSTISREF,dstisref);
 } 

/** 
* ��ȡĿ���ֶβ����ֶ�
*
* @return Ŀ���ֶβ����ֶ�
*/
public String getDstrefcolumn () {
return (String) this.getAttributeValue( RelationInfo.DSTREFCOLUMN);
 } 

/** 
* ����Ŀ���ֶβ����ֶ�
*
* @param dstrefcolumn Ŀ���ֶβ����ֶ�
*/
public void setDstrefcolumn ( String dstrefcolumn) {
this.setAttributeValue( RelationInfo.DSTREFCOLUMN,dstrefcolumn);
 } 

/** 
* ��ȡĿ���ֶβ��ձ�
*
* @return Ŀ���ֶβ��ձ�
*/
public String getDstreftable () {
return (String) this.getAttributeValue( RelationInfo.DSTREFTABLE);
 } 

/** 
* ����Ŀ���ֶβ��ձ�
*
* @param dstreftable Ŀ���ֶβ��ձ�
*/
public void setDstreftable ( String dstreftable) {
this.setAttributeValue( RelationInfo.DSTREFTABLE,dstreftable);
 } 

/** 
* ��ȡĿ����ձ�����ֶ�
*
* @return Ŀ����ձ�����ֶ�
*/
public String getDstreftypecolumn () {
return (String) this.getAttributeValue( RelationInfo.DSTREFTYPECOLUMN);
 } 

/** 
* ����Ŀ����ձ�����ֶ�
*
* @param dstreftypecolumn Ŀ����ձ�����ֶ�
*/
public void setDstreftypecolumn ( String dstreftypecolumn) {
this.setAttributeValue( RelationInfo.DSTREFTYPECOLUMN,dstreftypecolumn);
 } 

/** 
* ��ȡĿ���������ֵ
*
* @return Ŀ���������ֵ
*/
public String getDstreftypevalue () {
return (String) this.getAttributeValue( RelationInfo.DSTREFTYPEVALUE);
 } 

/** 
* ����Ŀ���������ֵ
*
* @param dstreftypevalue Ŀ���������ֵ
*/
public void setDstreftypevalue ( String dstreftypevalue) {
this.setAttributeValue( RelationInfo.DSTREFTYPEVALUE,dstreftypevalue);
 } 

/** 
* ��ȡ�ӱ�����
*
* @return �ӱ�����
*/
public String getId () {
return (String) this.getAttributeValue( RelationInfo.ID);
 } 

/** 
* �����ӱ�����
*
* @param id �ӱ�����
*/
public void setId ( String id) {
this.setAttributeValue( RelationInfo.ID,id);
 } 

/** 
* ��ȡ�ϲ㵥������
*
* @return �ϲ㵥������
*/
public String getPid () {
return (String) this.getAttributeValue( RelationInfo.PID);
 } 

/** 
* �����ϲ㵥������
*
* @param pid �ϲ㵥������
*/
public void setPid ( String pid) {
this.setAttributeValue( RelationInfo.PID,pid);
 } 

/** 
* ��ȡԴ�ֶγ���
*
* @return Դ�ֶγ���
*/
public String getSrccollength () {
return (String) this.getAttributeValue( RelationInfo.SRCCOLLENGTH);
 } 

/** 
* ����Դ�ֶγ���
*
* @param srccollength Դ�ֶγ���
*/
public void setSrccollength ( String srccollength) {
this.setAttributeValue( RelationInfo.SRCCOLLENGTH,srccollength);
 } 

/** 
* ��ȡԴ�ֶ�����
*
* @return Դ�ֶ�����
*/
public String getSrccoltype () {
return (String) this.getAttributeValue( RelationInfo.SRCCOLTYPE);
 } 

/** 
* ����Դ�ֶ�����
*
* @param srccoltype Դ�ֶ�����
*/
public void setSrccoltype ( String srccoltype) {
this.setAttributeValue( RelationInfo.SRCCOLTYPE,srccoltype);
 } 

/** 
* ��ȡԴ�ֶ�����
*
* @return Դ�ֶ�����
*/
public String getSrccolumn () {
return (String) this.getAttributeValue( RelationInfo.SRCCOLUMN);
 } 

/** 
* ����Դ�ֶ�����
*
* @param srccolumn Դ�ֶ�����
*/
public void setSrccolumn ( String srccolumn) {
this.setAttributeValue( RelationInfo.SRCCOLUMN,srccolumn);
 } 

/** 
* ��ȡԴ�Ƿ�Ϊʱ���ʽ
*
* @return Դ�Ƿ�Ϊʱ���ʽ
*/
public String getSrcisdatetime () {
return (String) this.getAttributeValue( RelationInfo.SRCISDATETIME);
 } 

/** 
* ����Դ�Ƿ�Ϊʱ���ʽ
*
* @param srcisdatetime Դ�Ƿ�Ϊʱ���ʽ
*/
public void setSrcisdatetime ( String srcisdatetime) {
this.setAttributeValue( RelationInfo.SRCISDATETIME,srcisdatetime);
 } 

/** 
* ��ȡԴ�Ƿ�Ϊ���ո�ʽ
*
* @return Դ�Ƿ�Ϊ���ո�ʽ
*/
public String getSrcisref () {
return (String) this.getAttributeValue( RelationInfo.SRCISREF);
 } 

/** 
* ����Դ�Ƿ�Ϊ���ո�ʽ
*
* @param srcisref Դ�Ƿ�Ϊ���ո�ʽ
*/
public void setSrcisref ( String srcisref) {
this.setAttributeValue( RelationInfo.SRCISREF,srcisref);
 } 

/** 
* ��ȡԴ�ֶβ����ֶ�
*
* @return Դ�ֶβ����ֶ�
*/
public String getSrcrefcolumn () {
return (String) this.getAttributeValue( RelationInfo.SRCREFCOLUMN);
 } 

/** 
* ����Դ�ֶβ����ֶ�
*
* @param srcrefcolumn Դ�ֶβ����ֶ�
*/
public void setSrcrefcolumn ( String srcrefcolumn) {
this.setAttributeValue( RelationInfo.SRCREFCOLUMN,srcrefcolumn);
 } 

/** 
* ��ȡԴ�ֶβ��ձ�
*
* @return Դ�ֶβ��ձ�
*/
public String getSrcreftable () {
return (String) this.getAttributeValue( RelationInfo.SRCREFTABLE);
 } 

/** 
* ����Դ�ֶβ��ձ�
*
* @param srcreftable Դ�ֶβ��ձ�
*/
public void setSrcreftable ( String srcreftable) {
this.setAttributeValue( RelationInfo.SRCREFTABLE,srcreftable);
 } 

/** 
* ��ȡԴ���ձ�����ֶ�
*
* @return Դ���ձ�����ֶ�
*/
public String getSrcreftypecolumn () {
return (String) this.getAttributeValue( RelationInfo.SRCREFTYPECOLUMN);
 } 

/** 
* ����Դ���ձ�����ֶ�
*
* @param srcreftypecolumn Դ���ձ�����ֶ�
*/
public void setSrcreftypecolumn ( String srcreftypecolumn) {
this.setAttributeValue( RelationInfo.SRCREFTYPECOLUMN,srcreftypecolumn);
 } 

/** 
* ��ȡԴ���ձ����ֵ
*
* @return Դ���ձ����ֵ
*/
public String getSrcreftypevalue () {
return (String) this.getAttributeValue( RelationInfo.SRCREFTYPEVALUE);
 } 

/** 
* ����Դ���ձ����ֵ
*
* @param srcreftypevalue Դ���ձ����ֵ
*/
public void setSrcreftypevalue ( String srcreftypevalue) {
this.setAttributeValue( RelationInfo.SRCREFTYPEVALUE,srcreftypevalue);
 } 

/** 
* ��ȡʱ���
*
* @return ʱ���
*/
public UFDateTime getTs () {
return (UFDateTime) this.getAttributeValue( RelationInfo.TS);
 } 

/** 
* ����ʱ���
*
* @param ts ʱ���
*/
public void setTs ( UFDateTime ts) {
this.setAttributeValue( RelationInfo.TS,ts);
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("pub.RelationInfo");
  }
}