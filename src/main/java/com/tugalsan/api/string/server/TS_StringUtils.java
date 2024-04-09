package com.tugalsan.api.string.server;

import com.tugalsan.api.charset.client.TGS_CharSetCast;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.union.client.TGS_Union;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.*;
import java.util.stream.*;

public class TS_StringUtils {

    public static String camelCase(CharSequence text) {
        var buffer = new StringBuilder();
        var wi = new AtomicInteger(-1);
        TS_StringUtils.toList_spc(text).forEach(word -> {
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
                    buffer.append(TGS_CharSetCast.toLocaleUpperCase(codePointAsStr));
                } else {
                    buffer.append(TGS_CharSetCast.toLocaleLowerCase(codePointAsStr));
                }
            });
        });
        return buffer.toString();
    }

    public static void toLocaleLowerCase(List<String> target) {
        IntStream.range(0, target.size()).parallel()
                .forEach(i -> target.set(i, TGS_CharSetCast.toLocaleLowerCase(target.get(i))));
    }

    public static void toLocaleUpperCase(List<String> target) {
        IntStream.range(0, target.size()).parallel()
                .forEach(i -> target.set(i, TGS_CharSetCast.toLocaleUpperCase(target.get(i))));
    }

    //BYTE-OP-----------------------------------------------------------------------------
    public static byte[] toByte(CharSequence source) {
        return toByte(source, StandardCharsets.UTF_8);
    }

    public static byte[] toByte(CharSequence source, Charset charset) {
        return (source == null ? "" : source).toString().getBytes(charset);
    }

    public static String toString(byte[] source) {
        return toString(source, StandardCharsets.UTF_8);
    }

    public static String toString(byte[] source, Charset charset) {
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
    public static TGS_Union<String> toString(InputStream is0) {
        return toString(is0, StandardCharsets.UTF_8);
    }

    public static TGS_Union<String> toString(InputStream is0, Charset charset) {
        try (var is = is0) {
            var bytes = is.readAllBytes();
            return TGS_Union.of(new String(bytes, charset));
        } catch (IOException ex) {
            return TGS_Union.ofExcuse(ex);
        }
    }

    public static TGS_Union<Boolean> toStream(OutputStream os, CharSequence data) {
        return toStream(os, data, StandardCharsets.UTF_8);
    }

    public static TGS_Union<Boolean> toStream(OutputStream os0, CharSequence data, Charset charset) {
        try (var os = os0) {
            var bytes = data.toString().getBytes(charset);
            os.write(bytes);
            return TGS_Union.of(true);
        } catch (IOException ex) {
            return TGS_Union.ofExcuse(ex);
        }
    }

    //PARSE-BASIC------------------------------------------------------------------------
    public static List<String> toList_spc(CharSequence source) {
        List<String> dst = new ArrayList();
        toList_spc(source, dst);
        return dst;
    }

    public static void toList_spc(CharSequence source, List<String> dst) {
        var delimiterOrRegex = " ";
        source = removeConsecutiveText(source.toString().trim(), " ");
        toList(source, dst, delimiterOrRegex);

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

    public static List<String> toList_tab(CharSequence source) {
        List<String> dst = new ArrayList();
        toList_tab(source, dst);
        return dst;
    }

    public static void toList_tab(CharSequence source, List<String> dst) {
        var delimiterOrRegex = " ";
        source = removeConsecutiveText(source.toString().trim(), "\t");
        toList(source, dst, delimiterOrRegex);

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

    public static String removeConsecutiveText(CharSequence text, CharSequence trimTag) {
        var textStr = text.toString();
        var trimTagStr = trimTag.toString();
        var doubleTrimTag = trimTagStr + trimTagStr;
        while (textStr.contains(doubleTrimTag)) {
            textStr = textStr.replace(doubleTrimTag, trimTag);
        }
        return textStr;
    }

    //PARSE-ADVANCED------------------------------------------------------------------------
    public static List<String> toList(CharSequence source, CharSequence delimiter) {
        return toList(source, delimiter, false);
    }

    public static List<String> toList(CharSequence source, CharSequence delimiterOrRegex, boolean useRegex) {
        List<String> output = new ArrayList();
        toList(source, output, delimiterOrRegex, useRegex);
        return output;
    }

    public static void toList(CharSequence source, List<String> output, CharSequence delimiter) {
        toList(source, output, delimiter, false);
    }

    public static void toList(CharSequence source, List<String> output, CharSequence delimiterOrRegex, boolean useRegex) {
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

    public static String toString(Double input, int charSizeAfterDot) {
        return toString(input, charSizeAfterDot, false);
    }

    public static String toString(Double input, int charSizeAfterDot, boolean remove0sFromTheEnd) {
        if (input == null) {
            return String.valueOf(input);
        }
        return toString(input.doubleValue(), charSizeAfterDot, false);
    }

    public static String toString(double input, int charSizeAfterDot) {
        return toString(input, charSizeAfterDot, false);
    }

    public static String toString(double input, int charSizeAfterDot, boolean remove0sFromTheEnd) {
        var val = String.format("%." + charSizeAfterDot + "f", input);
        if (remove0sFromTheEnd && charSizeAfterDot > 0) {
            while (val.endsWith("0")) {
                val = TGS_StringUtils.removeCharFromEnd(val, 1);
            }
        }
        if (val.endsWith(".") || val.endsWith(",")) {
            val = TGS_StringUtils.removeCharFromEnd(val, 1);
        }
        return val;
    }

    @Deprecated //WHY NOT WORKIN!!!
    public static String removeEmptyLines(CharSequence code) {
//        return code.toString().replaceAll("(?m)^\\s*\\r?\\n|\\r?\\n\\s*(?!.*\\r?\\n)", "");
        return code.toString().replaceAll("(?m)^[ \\t]*\\r?\\n", "");
    }

    public static String removeComments(CharSequence code) {
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
                            switch (c2) {
                                case "/" ->
                                    currentState = insideLineComment;
                                case "*" ->
                                    currentState = insideblockComment_noNewLineYet;
                                default ->
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
