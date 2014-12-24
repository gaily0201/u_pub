package cn.gaily.pub.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;


public class PingyinUtils {
	
	/**   
     * ����ת��Ϊ����ƴ������ĸ��Ӣ���ַ�����   
     * @param chines ����   
     * @return ƴ��
     */      
    public static String converterToFirstSpell(String chines){              
         String pinyinName = "";   
         
         //ת��Ϊ�ַ�
         char[] nameChar = chines.toCharArray();
         
         //����ƴ����ʽ�����   
         HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
         
         //�������,��Сд,���귽ʽ��   
         defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);       
         defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);       

         for (int i = 0; i < nameChar.length; i++) {
             //���������
        	 if (nameChar[i] > 128) {
                try {
                     pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0);       
                 } catch (BadHanyuPinyinOutputFormatCombination e) {
                     e.printStackTrace();
                 }       
             }else{//ΪӢ���ַ�
                 pinyinName += nameChar[i];
             }
         }       
        return pinyinName;       
     }       
        
    /**   
     * ����ת��λ����ƴ����Ӣ���ַ�����   
     * @param chines ����   
     * @return ƴ��   
     */      
    public static String converterToSpell(String chines){               
        String pinyinName = "";       
        char[] nameChar = chines.toCharArray();       
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();       
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);       
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE); 
        int m = 0;
        String pinyin = null;
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {       
            	try {
            		pinyin = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0];
                	 m++;
                	 if(m==1){
                		 pinyinName += pinyin;
                	 }else{
                		 pinyin = String.valueOf(pinyin.charAt(0)).toUpperCase()+String.valueOf(pinyin).substring(1, pinyin.length());
                		 pinyinName += pinyin;
                	 }
                 } catch (BadHanyuPinyinOutputFormatCombination e) {       
                     e.printStackTrace();       
                 }       
             }else{       
                 pinyinName += nameChar[i];       
             }       
         }       
        return pinyinName;       
     }       
    
    public static String convertToFirstFullSpell(String chines){               
        String pinyinName = "";       
        char[] nameChar = chines.toCharArray();       
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();       
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);       
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        int m = 0;
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                	m ++;
                	if(m==1){
                		pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0];
                	}else{
                		pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0);
                	}
                 } catch (BadHanyuPinyinOutputFormatCombination e) {       
                     e.printStackTrace();       
                 }
             }else{
                 pinyinName += nameChar[i];       
             }
         }
        return pinyinName;       
     }
    
    
    
    
    public static void main(String[] args) {       
        System.out.println(converterToFirstSpell("�����������޹�˾"));
    	
        //System.out.println(converterToSpell("ŷ����"));
    	//System.out.println(converterToFirstSpell("ŷ����"));
     }       
}