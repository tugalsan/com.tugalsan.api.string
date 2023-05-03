package com.tugalsan.api.string.client;

import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.util.Optional;

public class TGS_StringDouble {

    public static String dim(boolean turkish) {
        return turkish ? "," : ".";
    }

    public static boolean has(CharSequence inputText, boolean turkish) {
        var internationalText = (turkish ? inputText.toString().replace(",", ".") : inputText.toString()).trim();
        return internationalText.contains(dim(turkish));
    }

    final public long left, right;
    final public boolean turkish;

    public String dim() {
        return dim(turkish);
    }

    public double val() {
        return Double.parseDouble(left + dim() + right);
    }

    private TGS_StringDouble(long left, long right, boolean turkish) {
        this.left = left;
        this.right = right;
        this.turkish = turkish;
    }

    public static TGS_StringDouble of(long left, long right, boolean turkish) {
        return new TGS_StringDouble(left, right, turkish);
    }

    public static Optional<TGS_StringDouble> ofDbl(CharSequence inputText, boolean turkish) {
        //VALIDATE
        if (inputText.length() == 1) {
            return Optional.empty();
        }
        var internationalText = (turkish ? inputText.toString().replace(",", ".") : inputText.toString()).trim();
        var idx = internationalText.indexOf(".");
        if (idx == -1 || idx == internationalText.length() - 1) {//IT HAS TO BE DOUBLE!!!
            return Optional.empty();
        }
        //FETCH LEFT
        var left = internationalText.substring(0, idx);
        var leftLng = TGS_UnSafe.call(() -> Long.parseLong(left), e -> null);
        if (leftLng == null) {
            return Optional.empty();
        }
        //FETCH RIGHT
        var right = internationalText.substring(idx + 1);
        var rightLng = TGS_UnSafe.call(() -> Long.parseLong(right), e -> null);
        if (rightLng == null) {
            return Optional.empty();
        }
        var obj = of(leftLng, rightLng, turkish);
        return TGS_UnSafe.call(() -> {
            obj.val();//if not throws
            return Optional.of(obj);
        }, e -> Optional.empty()
        );
    }
}
