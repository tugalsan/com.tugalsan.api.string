package com.tugalsan.api.string.client;

import com.tugalsan.api.charset.client.TGS_CharSet;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.*;

public class TGC_StringUtils {

    @Deprecated //NOT WORKING ON UTF letters
    public String camelCase(CharSequence text) {
        var buffer = new StringBuilder();
        var wi = new AtomicInteger(-1);
        TGC_StringUtils.toList_spc(text).forEach(word -> {
            if (wi.incrementAndGet() != 0) {
                buffer.append(" ");
            }
            var ci = new AtomicInteger(-1);
            word.chars().mapToObj(ch -> String.valueOf(ch)).forEach(chAsStr -> {
                if (ci.incrementAndGet() == 0) {
                    buffer.append(TGS_CharSet.cmn().languageDefault().toUpperCase(chAsStr));
                } else {
                    buffer.append(TGS_CharSet.cmn().languageDefault().toLowerCase(chAsStr));
                }
            });
        });
        return buffer.toString();
    }

    public static native boolean matches(CharSequence regExp, CharSequence value) /*-{ return value.search(new RegExp(regExp)) != -1; }-*/;
//    private static String regexChars = ".$|()[{^?*+\\";

    public static void toList(CharSequence src, List<String> output, CharSequence delimiterOrRegex) {
        var srcStr = src.toString();
        var delimiterOrRegexStr = delimiterOrRegex.toString();
        output.clear();
        output.addAll(new ArrayList(Arrays.asList(srcStr.split(delimiterOrRegexStr))));
        while (srcStr.endsWith(delimiterOrRegexStr)) {
            output.add("");
            srcStr = srcStr.substring(0, srcStr.length() - delimiterOrRegexStr.length());
        }
    }

    public static List<String> toList(CharSequence src, CharSequence delimiterOrRegex) {
        List<String> dst = new ArrayList();
        TGC_StringUtils.toList(src, dst, delimiterOrRegex);
        return dst;
    }

    public static List<String> toList_spc(CharSequence src) {
        List<String> dst = new ArrayList();
        toList_spc(src, dst);
        return dst;
    }

    public static void toList_spc(CharSequence src, List<String> dst) {
        var delimiterOrRegex = " ";
        src = TGS_StringUtils.removeConsecutive(src.toString().trim(), " ");
        TGC_StringUtils.toList(src, dst, delimiterOrRegex);

        var from = 0;
        var to = dst.size();
        var by = 1;
        IntStream.iterate(to - 1, i -> i - by).limit(to - from).forEach(i -> {
            var str = dst.get(i);
            if (TGS_StringUtils.isNullOrEmpty(str)) {
                dst.remove(i);
            }
        });
    }

    public static String toString(double input, int charSizeAfterDot) {
        var inputStr = String.valueOf(input).replace(",", "S").replace(".", "S");
        var splits = inputStr.split("S");
//        d("toString->input:[" + input + "], charSizeAfterDot:[" + charSizeAfterDot + "], inputStr:[" + inputStr + "], splits.length:" + splits.length);
//        IntStream.range(0, splits.length).forEachOrdered(i -> d("toString->splits(" + i + "):[" + splits[i] + "]"));
        if (splits.length == 0) {
//            d("toString->ERROR: splits.length == 0");
            return null;
        }
        if (splits.length > 2) {
//            d("toString->ERROR: splits.length > 2");
            return null;
        }
        if (splits.length == 1) {
//            d("toString->OK: splits.length == 1");
            var prefix = splits[0];
            if (charSizeAfterDot == 0) {
                return prefix;
            }
            var suffix = "0";
            while (suffix.length() < charSizeAfterDot) {
                suffix += "0";
            }
            return TGS_StringUtils.concat(prefix, ".", suffix);
        }
        //if (splits.length == 2)...
        var prefix = splits[0];
        var suffix = splits[1];
        if (charSizeAfterDot == 0) {
            return prefix;
        }
//        d("toString->prefix:[" + prefix + "], suffix:[" + suffix + "]");
        if (suffix.length() <= charSizeAfterDot) {
//            d("toString->suffix.length():" + suffix.length() + ", charSizeAfterDot:" + charSizeAfterDot + ", suffix.length() <= charSizeAfterDot:" + (suffix.length() <= charSizeAfterDot));
            while (suffix.length() < charSizeAfterDot) {
                suffix += "0";
            }
            return TGS_StringUtils.concat(prefix, ".", suffix);
        }
        //if (suffix.length() > charSizeAfterDot)...
//        d("toString->suffix.length():" + suffix.length() + ", charSizeAfterDot:" + charSizeAfterDot + ", suffix.length() > charSizeAfterDot:" + (suffix.length() > charSizeAfterDot));
        suffix = suffix.substring(0, charSizeAfterDot);
        return TGS_StringUtils.concat(prefix, ".", suffix);
    }

//    private static native void d(String text) /*-{console.log('%c ' + text, 'color: gray; font-weight: bold; background-color: #242424;');}-*/;
}
