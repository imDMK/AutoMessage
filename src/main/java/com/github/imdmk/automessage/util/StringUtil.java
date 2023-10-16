package com.github.imdmk.automessage.util;

public final class StringUtil {

    public static String NEW_LINE = "<newline>";
    public static String GRAY_COLOR = "<gray>";

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
