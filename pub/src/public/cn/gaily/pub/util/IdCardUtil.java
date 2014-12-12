package cn.gaily.pub.util;

import nc.vo.pub.lang.UFDate;

/**
 * <p>Title: IdCardUtil</P>
 * <p>Description: ���֤У��ͻ�ȡ�Ա𹤾���</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * @version 1.0
 */
public class IdCardUtil {
	
	// ÿλ��Ȩ����   
    private static int power[] = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
	
	/**
	 * <p>�������ƣ�getPersonSex</p>
	 * <p>����������ͨ�����֤�Ż�ȡ��Ա�Ա�</p>
	 * @param idCard
	 * @return
	 * @author zhulb
	 * @since 2013-8-31
	 */
	public static String getPersonSex(String idCard) {
	  if(idCard != null && idCard.length() == 18){
		    if(Integer.valueOf(idCard.substring(16, 17))%2 == 1){
		      return "��";//��
		    }else{
		      return "Ů";//Ů
		    }
		  }
	    return "";
	}
	
	/**
	 * <p>�������ƣ�getPersonBirth</p>
	 * <p>����������ͨ�����֤�Ż�ȡ��Ա��������</p>
	 * @param idCard
	 * @return
	 */
	public static UFDate getPersonBirth(String idCard){
		UFDate birthTime = null;
		if(idCard != null && idCard.length() == 18){
			StringBuffer birth = new StringBuffer();
			birth.append(idCard.substring(6, 10)).append("-").append(idCard.substring(10, 12));
			birth.append("-").append(idCard.substring(12, 14)).append(" 00:00:00");
			birthTime = new UFDate(birth.toString());
			return birthTime;
		}
		return null;
	}

	/**
	 * <p>�������ƣ�isVilidate</p>
	 * <p>����������У�����֤�źϷ���</p>
	 * @param idCard
	 * @return
	 */
	public static boolean isVilidate(String idcard) {
		 // ��18λΪ��   
        if (idcard.length() != 18) {   
            return false;   
        }   
        // ��ȡǰ17λ   
        String idcard17 = idcard.substring(0, 17);   
        // ��ȡ��18λ   
        String idcard18Code = idcard.substring(17, 18);   
        char c[] = null;   
        String checkCode = "";   
        // �Ƿ�Ϊ����   
        if (isDigital(idcard17)) {   
            c = idcard17.toCharArray();   
        } else {   
            return false;   
        }   
  
        if (null != c) {   
            int bit[] = new int[idcard17.length()];   
  
            bit = converCharToInt(c);   
  
            int sum17 = 0;   
  
            sum17 = getPowerSum(bit);   
  
            // ����ֵ��11ȡģ�õ���������У�����ж�   
            checkCode = getCheckCodeBySum(sum17);   
            if (null == checkCode) {   
                return false;   
            }   
            // �����֤�ĵ�18λ���������У�����ƥ�䣬����Ⱦ�Ϊ��   
            if (!idcard18Code.equalsIgnoreCase(checkCode)) {   
                return false;   
            }   
        }   
        return true;   
	}
	
	/**  
     * ������֤  
     *   
     * @param str  
     * @return  
     */  
    private static boolean isDigital(String str) {   
        return str == null || "".equals(str) ? false : str.matches("^[0-9]*$");   
    }   
  
    /**  
     * �����֤��ÿλ�Ͷ�Ӧλ�ļ�Ȩ�������֮���ٵõ���ֵ  
     *   
     * @param bit  
     * @return  
     */  
    private static int getPowerSum(int[] bit) {   
  
        int sum = 0;   
  
        if (power.length != bit.length) {   
            return sum;   
        }   
  
        for (int i = 0; i < bit.length; i++) {   
            for (int j = 0; j < power.length; j++) {   
                if (i == j) {   
                    sum = sum + bit[i] * power[j];   
                }   
            }   
        }   
        return sum;   
    }   
  
    /**  
     * ����ֵ��11ȡģ�õ���������У�����ж�  
     *   
     * @param checkCode  
     * @param sum17  
     * @return У��λ  
     */  
    private static String getCheckCodeBySum(int sum17) {   
        String checkCode = null;   
        switch (sum17 % 11) {   
        case 10:   
            checkCode = "2";   
            break;   
        case 9:   
            checkCode = "3";   
            break;   
        case 8:   
            checkCode = "4";   
            break;   
        case 7:   
            checkCode = "5";   
            break;   
        case 6:   
            checkCode = "6";   
            break;   
        case 5:   
            checkCode = "7";   
            break;   
        case 4:   
            checkCode = "8";   
            break;   
        case 3:   
            checkCode = "9";   
            break;   
        case 2:   
            checkCode = "x";   
            break;   
        case 1:   
            checkCode = "0";   
            break;   
        case 0:   
            checkCode = "1";   
            break;   
        }   
        return checkCode;   
    }   
  
    /**  
     * ���ַ�����תΪ��������  
     *   
     * @param c  
     * @return  
     * @throws NumberFormatException  
     */  
    private static int[] converCharToInt(char[] c) throws NumberFormatException {   
        int[] a = new int[c.length];   
        int k = 0;   
        for (char temp : c) {   
            a[k++] = Integer.parseInt(String.valueOf(temp));   
        }   
        return a;   
    } 
}
