package com.tugalsan.api.string.client;

public class TGS_StringIntegerUtils {
    
    private TGS_StringIntegerUtils(){
        
    }

    public static String make2Chars(int value) {
        return value > 9 ? "" + value : "0" + value;
    }

    public static String make4Chars(int value) {
        var sValue = String.valueOf(value);
        if (value > 999) {
            return sValue.substring(sValue.length() - 4, sValue.length());
        } else if (value > 99) {
            return TGS_StringUtils.cmn().concat("0", sValue);
        } else if (value > 9) {
            return TGS_StringUtils.cmn().concat("00", sValue);
        } else {
            return TGS_StringUtils.cmn().concat("000", sValue);
        }
    }
}
