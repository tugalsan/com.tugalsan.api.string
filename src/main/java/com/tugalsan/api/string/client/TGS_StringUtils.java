package com.tugalsan.api.string.client;

import com.tugalsan.api.charset.client.TGS_CharSetCast;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;

public class TGS_StringUtils {

    public static String concat(Object... s) {
        return String.join("", String.valueOf(s));
    }

    public static String concat(CharSequence... s) {
        return String.join("", s);
    }

    public static String concat(List<String> lst) {
        return toString(lst, "");
    }

    public static String reverse(CharSequence data) {
        return new StringBuilder(data).reverse().toString();
    }

    public static boolean isNullOrEmptyOrHidden(CharSequence text) {
        return toNullIfEmptyOrHidden(text) == null;
    }

    public static boolean isNullOrEmpty(CharSequence text) {
        return toNullIfEmpty(text) == null;
    }

    public static boolean isPresent(CharSequence text) {
        return !isNullOrEmpty(text);
    }

    public static boolean isPresentAndShowing(CharSequence text) {
        return !isNullOrEmptyOrHidden(text);
    }

    public static String toNullIfEmptyOrHidden(CharSequence text) {
        if (text == null) {
            return null;
        }
        var textStr = removeHidden(text.toString().trim());
        if (textStr.isEmpty()) {
            return null;
        }
        if (Objects.equals(textStr, "null")) {
            return null;
        }
        return textStr;
    }

    public static String toNullIfEmpty(CharSequence text) {
        if (text == null) {
            return null;
        }
        var textStr = text.toString().trim();
        if (textStr.isEmpty()) {
            return null;
        }
        if (Objects.equals(textStr, "null")) {
            return null;
        }
        return textStr;
    }

    public static String toEmptyIfNull(CharSequence text) {
        if (text == null) {
            return "";
        }
        var textStr = text.toString();
        if (textStr.trim().isEmpty()) {
            return "";
        }
        return textStr;
    }

    public static String toString(Throwable e) {
        if (e == null) {
            return null;
        }
        var prefix = TGS_StringUtils.concat("ERROR CAUSE: '", e.toString(), "'\n", "ERROR TREE:\n");
        var sj = new StringJoiner("\n", prefix, "");
        Arrays.stream(e.getStackTrace()).forEachOrdered(ste -> sj.add(ste.toString()));
        return sj.toString();
    }

    public static String toString(float[] v, CharSequence delim) {
        if (v == null) {
            return "null";
        }
        var sj = new StringJoiner(delim);
        IntStream.range(0, v.length).forEachOrdered(i -> sj.add(String.valueOf(v[i])));
        return sj.toString();
    }

    public static String toString(double[] v, CharSequence delim) {
        if (v == null) {
            return "null";
        }
        var sj = new StringJoiner(delim);
        IntStream.range(0, v.length).forEachOrdered(i -> sj.add(String.valueOf(v[i])));
        return sj.toString();
    }

    public static String toString(byte[] v, CharSequence delim) {
        return v == null ? "null" : new String(v, StandardCharsets.UTF_8);
    }

    public static String toString(boolean[] v, CharSequence delim) {
        if (v == null) {
            return "null";
        }
        var sj = new StringJoiner(delim);
        IntStream.range(0, v.length).forEachOrdered(i -> sj.add(String.valueOf(v[i])));
        return sj.toString();
    }

    public static String toString(int[] v, CharSequence delim) {
        if (v == null) {
            return "null";
        }
        var sj = new StringJoiner(delim);
        IntStream.range(0, v.length).forEachOrdered(i -> sj.add(String.valueOf(v[i])));
        return sj.toString();
    }

    public static String toString(List v, CharSequence delim) {
        return toString(v, delim, 0);
    }

    public static String toString(List v, CharSequence delim, int offset) {
        if (v == null) {
            return "null";
        }
        var sj = new StringJoiner(delim);
        IntStream.range(offset, v.size()).forEachOrdered(i -> sj.add(String.valueOf(v.get(i))));
        return sj.toString();
    }

    public static String toString(List v, CharSequence delim, CharSequence prefix, CharSequence suffix) {
        if (v == null) {
            return "null";
        }
        var sj = new StringJoiner(delim);
        IntStream.range(0, v.size()).forEachOrdered(i -> {
            sj.add(
                    concat(prefix, String.valueOf(v.get(i)), suffix)
            );
        });
        return sj.toString();
    }

    public static String toString(List v, CharSequence tagStart, CharSequence tagEnd) {
        if (v == null) {
            return "null";
        }
        var sj = new StringJoiner(", ", tagStart, tagEnd);
        v.stream().forEachOrdered(o -> sj.add(String.valueOf(o)));
        return sj.toString();
    }

    public static String toString_ln(List v) {
        return toString(v, "\n");
    }

    public static String removeConsecutive(CharSequence source, CharSequence key) {
        var sourceStr = source.toString();
        var sb = new StringBuilder();
        sb.append(key).append(key);
        while (sourceStr.contains(sb)) {
            sourceStr = sourceStr.replace(sb, key);
        }
        return sourceStr;
    }

    public static String toString(Object[] data, CharSequence delimiter) {
        if (data == null) {
            return "null";
        }
        var sj = new StringJoiner(delimiter);
        Arrays.stream(data).forEachOrdered(o -> sj.add(String.valueOf(o)));
        return sj.toString();
    }

    public static String toString_tab(List<String> data) {
        return toString(data, "\t");
    }

    public static String toString_spc(List<String> data) {
        return toString(data, " ");
    }

    public static String toString_tab(Object[] data) {
        return toString(data, "\t");
    }

    public static String toString_spc(Object[] data) {
        return toString(data, " ");
    }

    public static int count(CharSequence text, CharSequence whatToCount) {
        var textStr = text.toString();
        return textStr.length() - textStr.replace(whatToCount, "").length();
    }

    public static long count(CharSequence text, char whatToCount) {
        return text.chars().filter(ch -> ch == '.').count();
    }

    public static String getBetween(CharSequence srcOrg, CharSequence fromTagOrg, CharSequence toTagOrg, boolean matchCase) {
        if (srcOrg == null) {
            return null;
        }
        var src = matchCase ? srcOrg.toString() : TGS_CharSetCast.toLocaleUpperCase(srcOrg);
        var fromTag = matchCase ? fromTagOrg.toString() : TGS_CharSetCast.toLocaleUpperCase(fromTagOrg);
        var toTag = matchCase ? toTagOrg.toString() : TGS_CharSetCast.toLocaleUpperCase(toTagOrg);
        var idxFrom = src.indexOf(fromTag);
        if (idxFrom == -1) {
            return null;
        }
        if (idxFrom + 1 > src.length()) {
            return null;
        }
        var idxTo = src.indexOf(toTag, idxFrom + 1);
        if (idxTo == -1) {
            idxTo = src.length();
        }
        return idxFrom == idxTo ? "" : srcOrg.toString().substring(idxFrom + 1, idxTo);
    }

    public static String toString(float flt) {
        return toString((double) flt);
    }

    public static String toString(float flt, TGS_CharSetCast.Locale2Cast locale2Cast) {
        return toString((double) flt, locale2Cast);
    }

    public static String toString(double dbl) {
        return toString(dbl, TGS_CharSetCast.LOCALE2CAST);
    }

    public static String toString(double dbl, TGS_CharSetCast.Locale2Cast locale2Cast) {
        var turkish = locale2Cast == TGS_CharSetCast.Locale2Cast.TURKISH;
        var dblStr = String.valueOf(dbl);
        if (!turkish) {
            return dblStr;
        }
        if (!dblStr.contains(".")) {
            return dblStr;
        }
        var idx = dblStr.lastIndexOf(".");
        return TGS_StringUtils.concat(dblStr.substring(0, idx), ",", dblStr.substring(idx + 1));
    }

    public static String trimIfNotNull(CharSequence text) {
        return text == null ? null : text.toString().trim();
    }

    public static String removeHidden(CharSequence text) {
        if (text == null) {
            return null;
        }
        return text.toString().replace("\n", "").replace("\r", "").replaceAll("\\p{C}", "?");
    }

    public static String removeCharFromEnd(CharSequence text, int charCount) {
        var str = text.toString();
        return str.substring(0, str.length() - charCount);
    }

    public static String removeCharFromStart(CharSequence text, int charCount) {
        var str = text.toString();
        return str.substring(charCount);
    }

    public static String removePrefix(CharSequence text, String prefix) {
        var str = text.toString();
        if (str.startsWith(prefix)) {
            return removeCharFromStart(text, prefix.length());
        }
        return str;
    }

    public static String removeSuffix(CharSequence text, String suffix) {
        var str = text.toString();
        if (str.endsWith(suffix)) {
            return removeCharFromEnd(text, suffix.length());
        }
        return str;
    }

    public static String toStringFromCodePoints(int p) {
        return new String(Character.toChars(p));
    }
}
