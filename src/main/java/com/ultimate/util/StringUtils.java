package com.ultimate.util;

/**
 字符串操作工具类
 */
public class StringUtils{

    public static boolean isEmpty(String string){

        int strLen;
        if(string == null || (strLen = string.length()) == 0){
            return true;
        }
        for(int i = 0; i < strLen; i++){
            if(!Character.isWhitespace(string.charAt(i))){
                return false;
            }
        }
        return true;
    }

    public static boolean isNotEmpty(String string){

        return !isEmpty(string);
    }

}
