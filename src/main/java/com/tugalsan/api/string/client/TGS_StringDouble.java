package com.tugalsan.api.string.client;

import com.tugalsan.api.charset.client.TGS_CharSetCast;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TGS_StringDouble {

    public static String dim(TGS_CharSetCast.Locale2Cast locale2Cast) {
        return locale2Cast == TGS_CharSetCast.Locale2Cast.TURKISH ? "," : ".";
    }

    public static boolean may(CharSequence inputText) {
        return inputText.toString().contains(dim(TGS_CharSetCast.Locale2Cast.TURKISH)) || inputText.toString().contains(dim(TGS_CharSetCast.Locale2Cast.OTHER));
    }

    public String dim() {
        return dim(locale2Cast);
    }

    public double val() {
        return Double.parseDouble(left + "." + rightZeros() + right);
    }

    public String rightZeros() {
        return IntStream.range(0, right_zero_onTheFront).mapToObj(i -> "0").collect(Collectors.joining());
    }

    private TGS_StringDouble(long left, long right, int right_zero_onTheFront, TGS_CharSetCast.Locale2Cast locale2Cast) {
        this.left = left;
        this.right = right;
        this.right_zero_onTheFront = right_zero_onTheFront;
        this.locale2Cast = locale2Cast;
    }
    final public long left, right;
    final public int right_zero_onTheFront;
    final public TGS_CharSetCast.Locale2Cast locale2Cast;

    public static TGS_StringDouble of(long left, long right, int right_zero_onTheFront, TGS_CharSetCast.Locale2Cast locale2Cast) {
        return new TGS_StringDouble(left, right, right_zero_onTheFront, locale2Cast);
    }

    public static Optional<TGS_StringDouble> of(CharSequence inputText) {
        return of(inputText, TGS_CharSetCast.LOCALE2CAST);
    }

    public static Optional<TGS_StringDouble> of(CharSequence inputText, TGS_CharSetCast.Locale2Cast locale2Set) {
        //VALIDATE
        if (inputText.length() == 1) {
            return Optional.empty();
        }
        var turkish = locale2Set == TGS_CharSetCast.Locale2Cast.TURKISH;
        var internationalText = (turkish ? inputText.toString().replace(",", ".") : inputText.toString()).trim();
        var idx = internationalText.indexOf(".");
        if (idx == -1 || idx == internationalText.length() - 1) {//IT HAS TO BE DOUBLE!!!
            return Optional.empty();
        }
        //FETCH LEFT
        var left = internationalText.substring(0, idx);
        var leftLng = TGS_UnSafe.call(() -> Long.valueOf(left), e -> null);
        if (leftLng == null) {
            return Optional.empty();
        }
        //FETCH RIGHT
        var right = internationalText.substring(idx + 1);
        var rightLng = TGS_UnSafe.call(() -> Long.valueOf(right), e -> null);
        if (rightLng == null) {
            return Optional.empty();
        }
        //CALC right_zero_onTheFront
        var right_zero_onTheFront = right.length() - String.valueOf(rightLng).length();
        var obj = of(leftLng, rightLng, right_zero_onTheFront, locale2Set);
        return TGS_UnSafe.call(() -> {
            obj.val();//if not throws
            return Optional.of(obj);
        }, e -> Optional.empty()
        );
    }
}
