package com.tugalsan.api.string.client;

import com.tugalsan.api.charset.client.TGS_CharSetCast;
import com.tugalsan.api.charset.client.TGS_CharSetLocale;
import com.tugalsan.api.charset.client.TGS_CharSetLocaleTypes;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TGS_StringDouble {

    public static boolean may(CharSequence inputText) {
        if (inputText.toString().contains(TGS_CharSetCast.turkish().dim())) {
            return true;
        }
        if (inputText.toString().contains(TGS_CharSetCast.english().dim())) {
            return true;
        }
        return false;
    }

    public String dim() {
        return TGS_CharSetCast.typed(type).dim();
    }

    public double val() {
        return Double.parseDouble(left + "." + rightZeros() + right);
    }

    public String rightZeros() {
        return IntStream.range(0, right_zero_onTheFront).mapToObj(i -> "0").collect(Collectors.joining());
    }

    private TGS_StringDouble(long left, long right, int right_zero_onTheFront, TGS_CharSetLocaleTypes type) {
        this.left = left;
        this.right = right;
        this.right_zero_onTheFront = right_zero_onTheFront;
        this.type = type;
    }
    final public long left, right;
    final public int right_zero_onTheFront;
    final public TGS_CharSetLocaleTypes type;

    public static TGS_StringDouble of(long left, long right, int right_zero_onTheFront, TGS_CharSetLocaleTypes type) {
        return new TGS_StringDouble(left, right, right_zero_onTheFront, type);
    }

    public static TGS_UnionExcuse<TGS_StringDouble> of(CharSequence inputText) {
        return of(inputText, TGS_CharSetLocale.cmn().currentTypeGet());
    }

    public static TGS_UnionExcuse<TGS_StringDouble> of(CharSequence inputText, TGS_CharSetLocaleTypes type) {
        //VALIDATE
        if (inputText.length() == 1) {
            return TGS_UnionExcuse.ofExcuse(TGS_StringDouble.class.getSimpleName(), "of(CharSequence inputText, TGS_CharSetLocaleTypes type)", "inputText.length() == 1");
        }
        var turkish = type == TGS_CharSetLocaleTypes.TURKISH;
        var internationalText = (turkish ? inputText.toString().replace(",", ".") : inputText.toString()).trim();
        var idx = internationalText.indexOf(".");
        if (idx == -1 || idx == internationalText.length() - 1) {//IT HAS TO BE DOUBLE!!!
            return TGS_UnionExcuse.ofExcuse(TGS_StringDouble.class.getSimpleName(), "of(CharSequence inputText, TGS_CharSetLocaleTypes type)", "idx == -1 || idx == internationalText.length() - 1");
        }
        //FETCH LEFT
        var left = internationalText.substring(0, idx);
        var leftLng = TGS_UnSafe.call(() -> Long.valueOf(left), e -> null);
        if (leftLng == null) {
            return TGS_UnionExcuse.ofExcuse(TGS_StringDouble.class.getSimpleName(), "of(CharSequence inputText, TGS_CharSetLocaleTypes type)", "leftLng == null");
        }
        //FETCH RIGHT
        var right = internationalText.substring(idx + 1);
        var rightLng = TGS_UnSafe.call(() -> Long.valueOf(right), e -> null);
        if (rightLng == null) {
            return TGS_UnionExcuse.ofExcuse(TGS_StringDouble.class.getSimpleName(), "of(CharSequence inputText, TGS_CharSetLocaleTypes type)", "rightLng == null");
        }
        //CALC right_zero_onTheFront
        var right_zero_onTheFront = right.length() - String.valueOf(rightLng).length();
        var obj = of(leftLng, rightLng, right_zero_onTheFront, type);
        return TGS_UnSafe.call(() -> {
            obj.val();//if not throws
            return TGS_UnionExcuse.of(obj);
        }, e -> TGS_UnionExcuse.ofExcuse(e));
    }
}
