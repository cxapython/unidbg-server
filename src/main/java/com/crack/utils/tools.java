package com.crack.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class tools {

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

    public static void main(String[] args) {
        System.out.println(encodeUrl("app_code=com.mfw.roadbook&app_ver=9.3.7&app_version_code=734&brand=google&channel_id=MFW&dev_ver=D1907.0&device_id=40%3A4E%3A36%3AB1%3A47%3A3E&device_type=android&hardware_model=Pixel&has_notch=0&is_special=1&mfwsdk_ver=20140507&o_lat=40.008224&o_lng=116.350234&oauth_consumer_key=5&oauth_nonce=ba9f536e-d7cb-47fc-b5d7-d8104e89b4cb&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1626418363&oauth_token=0_0969044fd4edf59957f4a39bce9200c6&oauth_version=1.0&open_udid=40%3A4E%3A36%3AB1%3A47%3A3E&screen_height=1794&screen_scale=2.88&screen_width=1080&sys_ver=8.1.0&time_offset=480&user_id=30044554&x_auth_mode=client_auth"));
    }
}