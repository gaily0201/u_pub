package cn.gaily.pub.trigger;

import java.io.Serializable;

public class ETLLogVO implements Serializable {
	private static final long serialVersionUID = 6076019999662722591L;

	public static String SRCDBNAME = "srcdbname";
	public static String SRCUSERNAME="srcusername";
	public static String SRCPORT="srcport";
	public static String SRCIP="srcip";
	public static String DESTDBNAME="destdbname";
	public static String DESTUSERNAME="destusername";
	public static String DESTPORT="destport";
	public static String DESTIP="destip";
	public static String TABLENAME="tablename";
	public static String TYPE="type";					//ÐÂÔö É¾³ý ÐÞ¸Ä
	public static String STARTTIME="starttime";
	public static String ENDTIME="endtime";
	public static String RESULT="result";
	
	
	public ETLLogVO setSrcdbname(String value){
		this.setSRCDBNAME(value);
		return this;
	}
	public ETLLogVO setSrcusername(String value){
		this.setSRCUSERNAME(value);
		return this;
	}
	public ETLLogVO setSrcport(String value){
		this.setSRCPORT(value);
		return this;
	}
	public ETLLogVO setSrcip(String value){
		this.setSRCIP(value);
		return this;
	}
	public ETLLogVO setDestdbname(String value){
		this.setDESTDBNAME(value);
		return this;
	}
	public ETLLogVO setDestusername(String value){
		this.setDESTUSERNAME(value);
		return this;
	}
	public ETLLogVO setDestport(String value){
		this.setDESTPORT(value);
		return this;
	}
	public ETLLogVO setDestip(String value){
		this.setDESTIP(value);
		return this;
	}
	
	public ETLLogVO setType(String value){
		this.setTYPE(value);
		return this;
	}
	public ETLLogVO setStarttime(String value){
		this.setSTARTTIME(value);
		return this;
	}
	public ETLLogVO setEndtime(String value){
		this.setENDTIME(value);
		return this;
	}
	public ETLLogVO setResult(String value){
		this.setRESULT(value);
		return this;
	}
	
	public ETLLogVO getLog(){
		return this;
	}
	
	public String getSRCDBNAME() {
		return SRCDBNAME;
	}
	public void setSRCDBNAME(String sRCDBNAME) {
		SRCDBNAME = sRCDBNAME;
	}
	public String getSRCUSERNAME() {
		return SRCUSERNAME;
	}
	public void setSRCUSERNAME(String sRCUSERNAME) {
		SRCUSERNAME = sRCUSERNAME;
	}
	public String getSRCPORT() {
		return SRCPORT;
	}
	public void setSRCPORT(String sRCPORT) {
		SRCPORT = sRCPORT;
	}
	public String getSRCIP() {
		return SRCIP;
	}
	public void setSRCIP(String sRCIP) {
		SRCIP = sRCIP;
	}
	public String getDESTDBNAME() {
		return DESTDBNAME;
	}
	public void setDESTDBNAME(String dESTDBNAME) {
		DESTDBNAME = dESTDBNAME;
	}
	public String getDESTUSERNAME() {
		return DESTUSERNAME;
	}
	public void setDESTUSERNAME(String dESTUSERNAME) {
		DESTUSERNAME = dESTUSERNAME;
	}
	public String getDESTPORT() {
		return DESTPORT;
	}
	public void setDESTPORT(String dESTPORT) {
		DESTPORT = dESTPORT;
	}
	public String getDESTIP() {
		return DESTIP;
	}
	public void setDESTIP(String dESTIP) {
		DESTIP = dESTIP;
	}
	public String getTABLENAME() {
		return TABLENAME;
	}
	public void setTABLENAME(String tABLENAME) {
		TABLENAME = tABLENAME;
	}
	public String getTYPE() {
		return TYPE;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public String getSTARTTIME() {
		return STARTTIME;
	}
	public void setSTARTTIME(String sTARTTIME) {
		STARTTIME = sTARTTIME;
	}
	public String getENDTIME() {
		return ENDTIME;
	}
	public void setENDTIME(String eNDTIME) {
		ENDTIME = eNDTIME;
	}
	public String getRESULT() {
		return RESULT;
	}
	public void setRESULT(String rESULT) {
		RESULT = rESULT;
	}
	
}
