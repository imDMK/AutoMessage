package com.github.imdmk.automessage.util;

public final class StringUtil {

    private StringUtil() {
        throw new UnsupportedOperationException("This is utility class.");
    }

    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        }
        catch (NumberFormatException numberFormatException) {
            return false;
        }

        return true;
    }
}
