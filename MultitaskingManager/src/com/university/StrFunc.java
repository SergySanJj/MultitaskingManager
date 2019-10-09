package com.university;

import static java.lang.Character.*;

public class StrFunc {
    public static String getFirstNumber(String str) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (isDigit(str.charAt(i)) || str.charAt(i) == '.' || str.charAt(i) == '-' || isLetter(str.charAt(i)))
                res.append(str.charAt(i));
        }
        return res.toString();
    }

    public static String[] parseNumValues(String str) {
        String[] values = str.split(" ");
        for (int i = 0; i < values.length; i++) {
            values[i] = getFirstNumber(values[i]);
        }
        return values;
    }
}
