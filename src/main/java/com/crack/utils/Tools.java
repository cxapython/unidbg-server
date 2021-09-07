package com.crack.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Tools {

    public static String encodeUrl(String value) {
        return encodeUrl(value, "UTF-8");
    }

    public static String encodeUrl(String value, String charsetName) {
        if (value.isEmpty()) {
            return "";
        }
        String encoded = "";
        try {
            encoded = URLEncoder.encode(value, charsetName);
        } catch (UnsupportedEncodingException ignore) {
            ignore.printStackTrace();
        }
        StringBuilder buf = new StringBuilder(encoded.length());
        int i = 0;
        while (i < encoded.length()) {
            char focus = encoded.charAt(i);
            if (focus == '*') {
                buf.append("%2A");
            } else if (focus == '+') {
                buf.append("%20");
            } else if (focus == '%' && i + 1 < encoded.length() && encoded.charAt(i + 1) == '7' && encoded.charAt(i + 2) == 'E') {
                buf.append("~");
                i += 2;
            } else {
                buf.append(focus);
            }
            i++;
        }
        return buf.toString();
    }

}