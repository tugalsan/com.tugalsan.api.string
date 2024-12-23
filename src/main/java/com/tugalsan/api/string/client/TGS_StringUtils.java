package com.tugalsan.api.string.client;

import com.google.gwt.core.shared.GwtIncompatible;
import com.tugalsan.api.charset.client.*;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class TGS_StringUtils {

    //UserHappy
    public static Common cmn() {
        return memoryHappy().cmn;
    }

    public static OnlyJreHappy jre() {
        return memoryHappy().jre;
    }

    public static OnlyGwtHappy gwt() {
        return memoryHappy().gwt;
    }

    //MemoryHappy
    private static TGS_StringUtils memoryHappy() {
        if (holder == null) {
            holder = new TGS_StringUtils();
        }
        return holder;
    }
    private static volatile TGS_StringUtils holder = null;

    private TGS_StringUtils() {

    }
    final private Common cmn = new Common();
    final private OnlyJreHappy jre = new OnlyJreHappy();
    final private OnlyGwtHappy gwt = new OnlyGwtHappy();

    //OnlyGwtHappy
    public static class OnlyGwtHappy {

        @Deprecated //NOT WORKING ON UTF letters
        public String camelCase(CharSequence text) {
            var buffer = new StringBuilder();
            var wi = new AtomicInteger(-1);
            toList_spc(text).forEach(word -> {
                if (wi.incrementAndGet() != 0) {
                    buffer.append(" ");
                }
                var ci = new AtomicInteger(-1);
                word.chars().mapToObj(ch -> String.valueOf(ch)).forEach(chAsStr -> {
                    if (ci.incrementAndGet() == 0) {
                        buffer.append(TGS_CharSetCast.current().toUpperCase(chAsStr));
                    } else {
                        buffer.append(TGS_CharSetCast.current().toLowerCase(chAsStr));
                    }
                });
            });
            return buffer.toString();
        }

        //private String regexChars = ".$|()[{^?*+\\";
        public native boolean matches(CharSequence regExp, CharSequence value) /*-{ return value.search(new RegExp(regExp)) != -1; }-*/;

        public void toList(CharSequence src, List<String> output, CharSequence delimiterOrRegex) {
            var srcStr = src.toString();
            var delimiterOrRegexStr = delimiterOrRegex.toString();
            output.clear();
            output.addAll(new ArrayList(Arrays.asList(srcStr.split(delimiterOrRegexStr))));
            while (srcStr.endsWith(delimiterOrRegexStr)) {
                output.add("");
                srcStr = srcStr.substring(0, srcStr.length() - delimiterOrRegexStr.length());
            }
        }

        public List<String> toList(CharSequence src, CharSequence delimiterOrRegex) {
            List<String> dst = new ArrayList();
            toList(src, dst, delimiterOrRegex);
            return dst;
        }

        public List<String> toList_spc(CharSequence src) {
            List<String> dst = new ArrayList();
            toList_spc(src, dst);
            return dst;
        }

        public void toList_spc(CharSequence src, List<String> dst) {
            var delimiterOrRegex = " ";
            src = TGS_StringUtils.cmn().removeConsecutive(src.toString().trim(), " ");
            toList(src, dst, delimiterOrRegex);

            var from = 0;
            var to = dst.size();
            var by = 1;
            IntStream.iterate(to - 1, i -> i - by).limit(to - from).forEach(i -> {
                var str = dst.get(i);
                if (TGS_StringUtils.cmn().isNullOrEmpty(str)) {
                    dst.remove(i);
                }
            });
        }

        public String toString(double input, int charSizeAfterDot) {
            var inputStr = String.valueOf(input).replace(",", "S").replace(".", "S");
            var splits = inputStr.split("S");
            if (splits.length == 0) {
                return null;
            }
            if (splits.length > 2) {
                return null;
            }
            if (splits.length == 1) {
                var prefix = splits[0];
                if (charSizeAfterDot == 0) {
                    return prefix;
                }
                var suffix = "0";
                while (suffix.length() < charSizeAfterDot) {
                    suffix += "0";
                }
                return TGS_StringUtils.cmn().concat(prefix, ".", suffix);
            }
            var prefix = splits[0];
            var suffix = splits[1];
            if (charSizeAfterDot == 0) {
                return prefix;
            }
            if (suffix.length() <= charSizeAfterDot) {
                while (suffix.length() < charSizeAfterDot) {
                    suffix += "0";
                }
                return TGS_StringUtils.cmn().concat(prefix, ".", suffix);
            }
            suffix = suffix.substring(0, charSizeAfterDot);
            return TGS_StringUtils.cmn().concat(prefix, ".", suffix);
        }
    }

    //OnlyJreHappy
    public static class OnlyJreHappy {

        @GwtIncompatible
        public String camelCase(CharSequence text) {
            var buffer = new StringBuilder();
            var wi = new AtomicInteger(-1);
            toList_spc(text).forEach(word -> {
                if (wi.incrementAndGet() != 0) {
                    buffer.append(" ");
                }
                var ci = new AtomicInteger(-1);
                word.codePoints().forEachOrdered(codePoint -> {
                    String codePointAsStr;
                    if (Character.isBmpCodePoint(codePoint)) {
                        codePointAsStr = String.valueOf((char) codePoint);
                    } else {
                        codePointAsStr = String.valueOf(codePoint);
                    }
                    if (ci.incrementAndGet() == 0) {
                        buffer.append(TGS_CharSetCast.current().toUpperCase(codePointAsStr));
                    } else {
                        buffer.append(TGS_CharSetCast.current().toLowerCase(codePointAsStr));
                    }
                });
            });
            return buffer.toString();
        }

        @GwtIncompatible
        public void toLocaleLowerCase(List<String> target) {
            IntStream.range(0, target.size()).parallel()
                    .forEach(i -> target.set(i, TGS_CharSetCast.current().toLowerCase(target.get(i))));
        }

        @GwtIncompatible
        public void toLocaleUpperCase(List<String> target) {
            IntStream.range(0, target.size()).parallel()
                    .forEach(i -> target.set(i, TGS_CharSetCast.current().toUpperCase(target.get(i))));
        }

        //BYTE-OP-----------------------------------------------------------------------------
        @GwtIncompatible
        public byte[] toByte(CharSequence source) {
            return toByte(source, StandardCharsets.UTF_8);
        }

        @GwtIncompatible
        public byte[] toByte(CharSequence source, Charset charset) {
            return (source == null ? "" : source).toString().getBytes(charset);
        }

        @GwtIncompatible
        public String toString(byte[] source) {
            return toString(source, StandardCharsets.UTF_8);
        }

        @GwtIncompatible
        public String toString(byte[] source, Charset charset) {
            if (source == null) {
                return "";
            }
            var r = new String(source, charset);
            if (r.isEmpty()) {
                return r;
            }
            {//LATIN5 TO UTF8 MIGRATION FIX
                var WRONG_CHAR_LENGTH = 6;
                var WRONG_CHAR_TAG = 65000;
                var foundAWrongChar = false;
                if (r.length() >= WRONG_CHAR_LENGTH) {
                    for (var i = 0; i < WRONG_CHAR_LENGTH; i++) {
                        var c = r.charAt(i);
                        if (c + 0 > WRONG_CHAR_TAG) {
                            foundAWrongChar = true;
                            break;
                        }
                    }
                }
                if (foundAWrongChar) {
                    if (r.length() >= WRONG_CHAR_LENGTH) {
                        r = r.substring(WRONG_CHAR_LENGTH + 1);
                    } else {
                        r = "";
                    }
                }
            }
            return r;
        }

        //STREAM-OP-----------------------------------------------------------------------------
        @GwtIncompatible
        public String toString(InputStream is0) {
            return toString(is0, StandardCharsets.UTF_8);
        }

        @GwtIncompatible
        public String toString(InputStream is0, Charset charset) {
            return TGS_UnSafe.call(() -> {
                try (var is = is0) {
                    var bytes = is.readAllBytes();
                    return new String(bytes, charset);
                }
            });
        }

        @GwtIncompatible
        public void toStream(OutputStream os, CharSequence data) {
            toStream(os, data, StandardCharsets.UTF_8);
        }

        @GwtIncompatible
        public void toStream(OutputStream os0, CharSequence data, Charset charset) {
            TGS_UnSafe.run(() -> {
                try (var os = os0) {
                    var bytes = data.toString().getBytes(charset);
                    os.write(bytes);
                }
            });
        }

        //PARSE-BASIC------------------------------------------------------------------------
        @GwtIncompatible
        public List<String> toList_spc(CharSequence source) {
            List<String> dst = new ArrayList();
            toList_spc(source, dst);
            return dst;
        }

        @GwtIncompatible
        public void toList_spc(CharSequence source, List<String> dst) {
            var delimiterOrRegex = " ";
            source = removeConsecutiveText(source.toString().trim(), " ");
            toList(source, dst, delimiterOrRegex);

            var from = 0;
            var to = dst.size();
            var by = 1;
            IntStream.iterate(to - 1, i -> i - by).limit(to - from).forEach(i -> {
                var str = dst.get(i);
                if (TGS_StringUtils.cmn().isNullOrEmpty(str)) {
                    dst.remove(i);
                }
            });
        }

        @GwtIncompatible
        public List<String> toList_tab(CharSequence source) {
            List<String> dst = new ArrayList();
            toList_tab(source, dst);
            return dst;
        }

        @GwtIncompatible
        public void toList_tab(CharSequence source, List<String> dst) {
            var delimiterOrRegex = " ";
            source = removeConsecutiveText(source.toString().trim(), "\t");
            toList(source, dst, delimiterOrRegex);

            var from = 0;
            var to = dst.size();
            var by = 1;
            IntStream.iterate(to - 1, i -> i - by).limit(to - from).forEach(i -> {
                var str = dst.get(i);
                if (TGS_StringUtils.cmn().isNullOrEmpty(str)) {
                    dst.remove(i);
                }
            });
        }

        @GwtIncompatible
        public List<String> toList_ln(CharSequence source) {
            List<String> dst = new ArrayList();
            toList_tab(source, dst);
            return dst;
        }

        @GwtIncompatible
        public void toList_ln(CharSequence source, List<String> dst) {
            var delimiterOrRegex = " ";
            source = removeConsecutiveText(source.toString().trim(), "\n");
            toList(source, dst, delimiterOrRegex);

            var from = 0;
            var to = dst.size();
            var by = 1;
            IntStream.iterate(to - 1, i -> i - by).limit(to - from).forEach(i -> {
                var str = dst.get(i);
                if (TGS_StringUtils.cmn().isNullOrEmpty(str)) {
                    dst.remove(i);
                }
            });
        }

        @GwtIncompatible
        public String removeConsecutiveText(CharSequence text, CharSequence trimTag) {
            var textStr = text.toString();
            var trimTagStr = trimTag.toString();
            var doubleTrimTag = trimTagStr + trimTagStr;
            while (textStr.contains(doubleTrimTag)) {
                textStr = textStr.replace(doubleTrimTag, trimTag);
            }
            return textStr;
        }

        //PARSE-ADVANCED------------------------------------------------------------------------
        @GwtIncompatible
        public List<String> toList(CharSequence source, CharSequence delimiter) {
            return toList(source, delimiter, false);
        }

        @GwtIncompatible
        public List<String> toList(CharSequence source, CharSequence delimiterOrRegex, boolean useRegex) {
            List<String> output = new ArrayList();
            toList(source, output, delimiterOrRegex, useRegex);
            return output;
        }

        @GwtIncompatible
        public void toList(CharSequence source, List<String> output, CharSequence delimiter) {
            toList(source, output, delimiter, false);
        }

        @GwtIncompatible
        public void toList(CharSequence source, List<String> output, CharSequence delimiterOrRegex, boolean useRegex) {
            var sourceStr = source.toString();
            var delimiterOrRegexStr = delimiterOrRegex.toString();
            output.clear();
            var r = useRegex ? sourceStr.split(delimiterOrRegexStr) : sourceStr.split(Pattern.quote(delimiterOrRegexStr));
            output.addAll(new ArrayList(Arrays.asList(r)));
            if (!useRegex) {
                while (sourceStr.endsWith(delimiterOrRegexStr)) {
                    output.add("");
                    sourceStr = sourceStr.substring(0, sourceStr.length() - delimiterOrRegexStr.length());
                }
            }
        }

        @GwtIncompatible
        public String toString(Double input, int charSizeAfterDot) {
            return toString(input, charSizeAfterDot, false);
        }

        @GwtIncompatible
        public String toString(Double input, int charSizeAfterDot, boolean remove0sFromTheEnd) {
            if (input == null) {
                return String.valueOf(input);
            }
            return toString(input.doubleValue(), charSizeAfterDot, false);
        }

        @GwtIncompatible
        public String toString(double input, int charSizeAfterDot) {
            return toString(input, charSizeAfterDot, false);
        }

        @GwtIncompatible
        public String toString(double input, int charSizeAfterDot, boolean remove0sFromTheEnd) {
            var val = String.format("%." + charSizeAfterDot + "f", input);
            if (remove0sFromTheEnd && charSizeAfterDot > 0) {
                while (val.endsWith("0")) {
                    val = TGS_StringUtils.cmn().removeCharFromEnd(val, 1);
                }
            }
            if (val.endsWith(".") || val.endsWith(",")) {
                val = TGS_StringUtils.cmn().removeCharFromEnd(val, 1);
            }
            return val;
        }

        @Deprecated //WHY NOT WORKIN!!!
        @GwtIncompatible
        public String removeEmptyLines(CharSequence code) {
//        return code.toString().replaceAll("(?m)^\\s*\\r?\\n|\\r?\\n\\s*(?!.*\\r?\\n)", "");
            return code.toString().replaceAll("(?m)^[ \\t]*\\r?\\n", "");
        }

        @GwtIncompatible
        public String removeComments(CharSequence code) {
            final var outsideComment = 0;
            final var insideLineComment = 1;
            final var insideblockComment = 2;
            final var insideblockComment_noNewLineYet = 3; // we want to have at least one new line in the result if the block is not inline.
            var currentState = outsideComment;
            var endResult = new StringBuilder();
            try (var s = new Scanner(code.toString())) {
                s.useDelimiter("");
                while (s.hasNext()) {
                    var c = s.next();
                    switch (currentState) {
                        case outsideComment:
                            if (c.equals("/") && s.hasNext()) {
                                var c2 = s.next();
                                if (c2.equals("/")) {
                                    currentState = insideLineComment;
                                } else if (c2.equals("*")) {
                                    currentState = insideblockComment_noNewLineYet;
                                } else {
                                    endResult.append(c).append(c2);
                                }
                            } else {
                                endResult.append(c);
                            }
                            break;
                        case insideLineComment:
                            if (c.equals("\n")) {
                                currentState = outsideComment;
                                endResult.append("\n");
                            }
                            break;
                        case insideblockComment_noNewLineYet:
                            if (c.equals("\n")) {
                                endResult.append("\n");
                                currentState = insideblockComment;
                            }
                        case insideblockComment:
                            while (c.equals("*") && s.hasNext()) {
                                var c2 = s.next();
                                if (c2.equals("/")) {
                                    currentState = outsideComment;
                                    break;
                                }

                            }
                    }
                }
            }
            return endResult.toString().strip().trim();
        }
    }

    //Common
    public static class Common extends CommonGwt {

//        @Override
//        @GwtIncompatible
//        public void println(CharSequence name) {
//            OnlyJreHappy.println(name);
//        }
    }

    //CommonGwt
    public static class CommonGwt {

        //    @Deprecated //String.valueOf is calling [Object.324o234u2 rather than calling toString
//    public String concat(Object... s) {
//        return String.join("", String.valueOf(s));
//    }
        public String concat(CharSequence... s) {
            return String.join("", s);
        }

        public String concat(List<String> lst) {
            return toString(lst, "");
        }

        public String reverse(CharSequence data) {
            return new StringBuilder(data).reverse().toString();
        }

        public boolean isNullOrEmptyOrHidden(CharSequence text) {
            return toNullIfEmptyOrHidden(text) == null;
        }

        public boolean isNullOrEmpty(CharSequence text) {
            return toNullIfEmpty(text) == null;
        }

        public boolean isPresent(CharSequence text) {
            return !isNullOrEmpty(text);
        }

        public boolean isPresentAndShowing(CharSequence text) {
            return !isNullOrEmptyOrHidden(text);
        }

        public String toNullIfEmptyOrHidden(CharSequence text) {
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

        public String toNullIfEmpty(CharSequence text) {
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

        public String toEmptyIfNull(CharSequence text) {
            if (text == null) {
                return "";
            }
            var textStr = text.toString();
            if (textStr.trim().isEmpty()) {
                return "";
            }
            return textStr;
        }

        public String toString(Throwable e) {
            if (e == null) {
                return null;
            }
            var prefix = TGS_StringUtils.cmn().concat("ERROR CAUSE: '", e.toString(), "'\n", "ERROR TREE:\n");
            var sj = new StringJoiner("\n", prefix, "");
            Arrays.stream(e.getStackTrace()).forEachOrdered(ste -> sj.add(ste.toString()));
            return sj.toString();
        }

        public String toString(float[] v, CharSequence delim) {
            if (v == null) {
                return "null";
            }
            var sj = new StringJoiner(delim);
            IntStream.range(0, v.length).forEachOrdered(i -> sj.add(String.valueOf(v[i])));
            return sj.toString();
        }

        public String toString(double[] v, CharSequence delim) {
            if (v == null) {
                return "null";
            }
            var sj = new StringJoiner(delim);
            IntStream.range(0, v.length).forEachOrdered(i -> sj.add(String.valueOf(v[i])));
            return sj.toString();
        }

        public String toString(byte[] v, CharSequence delim) {
            return v == null ? "null" : new String(v, StandardCharsets.UTF_8);
        }

        public String toString(boolean[] v, CharSequence delim) {
            if (v == null) {
                return "null";
            }
            var sj = new StringJoiner(delim);
            IntStream.range(0, v.length).forEachOrdered(i -> sj.add(String.valueOf(v[i])));
            return sj.toString();
        }

        public String toString(int[] v, CharSequence delim) {
            if (v == null) {
                return "null";
            }
            var sj = new StringJoiner(delim);
            IntStream.range(0, v.length).forEachOrdered(i -> sj.add(String.valueOf(v[i])));
            return sj.toString();
        }

        public String toString(List v, CharSequence delim) {
            return toString(v, delim, 0);
        }

        public String toString(List v, CharSequence delim, int offset) {
            if (v == null) {
                return "null";
            }
            var sj = new StringJoiner(delim);
            IntStream.range(offset, v.size()).forEachOrdered(i -> sj.add(String.valueOf(v.get(i))));
            return sj.toString();
        }

        public String toString(List v, CharSequence delim, CharSequence prefix, CharSequence suffix) {
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

        public String toString(List v, CharSequence tagStart, CharSequence tagEnd) {
            if (v == null) {
                return "null";
            }
            var sj = new StringJoiner(", ", tagStart, tagEnd);
            v.stream().forEachOrdered(o -> sj.add(String.valueOf(o)));
            return sj.toString();
        }

        public String toString_ln(List v) {
            return toString(v, "\n");
        }

        public String removeConsecutive(CharSequence source, CharSequence key) {
            var sourceStr = source.toString();
            var sb = new StringBuilder();
            sb.append(key).append(key);
            while (sourceStr.contains(sb)) {
                sourceStr = sourceStr.replace(sb, key);
            }
            return sourceStr;
        }

        public String toString(Object[] data, CharSequence delimiter) {
            if (data == null) {
                return "null";
            }
            var sj = new StringJoiner(delimiter);
            Arrays.stream(data).forEachOrdered(o -> sj.add(String.valueOf(o)));
            return sj.toString();
        }

        public String toString_tab(List<String> data) {
            return toString(data, "\t");
        }

        public String toString_spc(List<String> data) {
            return toString(data, " ");
        }

        public String toString_tab(Object[] data) {
            return toString(data, "\t");
        }

        public String toString_ln(Object[] data) {
            return toString(data, "\n");
        }

        public String toString_spc(Object[] data) {
            return toString(data, " ");
        }

        public int count(CharSequence text, CharSequence whatToCount) {
            var textStr = text.toString();
            return textStr.length() - textStr.replace(whatToCount, "").length();
        }

        public long count(CharSequence text, char whatToCount) {
            return text.chars().filter(ch -> ch == '.').count();
        }

        public String getBetween(CharSequence srcOrg, CharSequence fromTagOrg, CharSequence toTagOrg, boolean matchCase) {
            if (srcOrg == null) {
                return null;
            }
            var src = matchCase ? srcOrg.toString() : TGS_CharSetCast.current().toUpperCase(srcOrg);
            var fromTag = matchCase ? fromTagOrg.toString() : TGS_CharSetCast.current().toUpperCase(fromTagOrg);
            var toTag = matchCase ? toTagOrg.toString() : TGS_CharSetCast.current().toUpperCase(toTagOrg);
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

        public String toString(float flt) {
            return toString((double) flt);
        }

        public String toString(float flt, TGS_CharSetLocaleTypes type) {
            return toString((double) flt, type);
        }

        public String toString(double dbl) {
            return toString(dbl, TGS_CharSetCast.current().localType);
        }

        public String toString(double dbl, TGS_CharSetLocaleTypes type) {
            var turkish = type.equals(TGS_CharSetCast.typed(type));
            var dblStr = String.valueOf(dbl);
            if (!turkish) {
                return dblStr;
            }
            if (!dblStr.contains(".")) {
                return dblStr;
            }
            var idx = dblStr.lastIndexOf(".");
            return TGS_StringUtils.cmn().concat(dblStr.substring(0, idx), ",", dblStr.substring(idx + 1));
        }

        public String trimIfNotNull(CharSequence text) {
            return text == null ? null : text.toString().trim();
        }

        public String removeHidden(CharSequence text) {
            return TGS_CharSet.cmn().removeHidden(text);
        }

        public String removeCharFromEnd(CharSequence text, int charCount) {
            var str = text.toString();
            return str.substring(0, str.length() - charCount);
        }

        public String removeCharFromStart(CharSequence text, int charCount) {
            var str = text.toString();
            return str.substring(charCount);
        }

        public String removePrefix(CharSequence text, String prefix) {
            var str = text.toString();
            if (str.startsWith(prefix)) {
                return removeCharFromStart(text, prefix.length());
            }
            return str;
        }

        public String removeSuffix(CharSequence text, String suffix) {
            var str = text.toString();
            if (str.endsWith(suffix)) {
                return removeCharFromEnd(text, suffix.length());
            }
            return str;
        }

        public String toStringFromCodePoints(int codePoint) {
            return new String(Character.toChars(codePoint));
        }

        public String[] parseToLines(CharSequence content) {
            return content.toString().split("\\r?\\n");
        }

        public List<String> parseByEmptyLines(CharSequence content) {
            var lines = parseToLines(content);
            List<String> result = new ArrayList();
            var sb = new StringBuilder();
            for (var line : lines) {
                if (!isNullOrEmpty(line)) {
                    if (!isEmpty(sb)) {
                        sb.append("\n");
                    }
                    sb.append(line);
                    continue;
                }
                if (!isEmpty(sb)) {
                    result.add(sb.toString());
                    sb.setLength(0);
                }
            }
            if (!isEmpty(sb)) {
                result.add(sb.toString());
                sb.setLength(0);
            }
            return result;
        }

        public boolean isEmpty(StringBuilder sb) {//GWT DOES NOT LIKE sb.isEmpty()
            return sb.length() == 0;
        }
    }
}
