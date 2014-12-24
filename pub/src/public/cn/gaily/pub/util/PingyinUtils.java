package cn.gaily.pub.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;


public class PingyinUtils {
	
	/**   
     * 汉字转换为汉语拼音首字母，英文字符不变   
     * @param chines 汉字   
     * @return 拼音
     */      
    public static String converterToFirstSpell(String chines){              
         String pinyinName = "";   
         
         //转化为字符
         char[] nameChar = chines.toCharArray();
         
         //汉语拼音格式输出类   
         HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
         
         //输出设置,大小写,音标方式等   
         defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);       
         defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);       

         for (int i = 0; i < nameChar.length; i++) {
             //如果是中文
        	 if (nameChar[i] > 128) {
                try {
                     pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0);       
                 } catch (BadHanyuPinyinOutputFormatCombination e) {
                     e.printStackTrace();
                 }       
             }else{//为英文字符
                 pinyinName += nameChar[i];
             }
         }       
        return pinyinName;       
     }       
        
    /**   
     * 汉字转换位汉语拼音，英文字符不变   
     * @param chines 汉字   
     * @return 拼音   
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
        System.out.println(converterToFirstSpell("北京长城有限公司"));
    	
        //System.out.println(converterToSpell("欧阳锋"));
    	//System.out.println(converterToFirstSpell("欧阳锋"));
     }       
}