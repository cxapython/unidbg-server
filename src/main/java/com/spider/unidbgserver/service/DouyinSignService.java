package com.spider.unidbgserver.service;

import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.spider.unidbgserver.vm.DouyinVM;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DouyinSignService {

    public Map<String, String> crack(String url) {
        // 调用so的入口，这是smali写法
        String methodSign = "leviathan(II[B)[B";

        //解析url里面的参数
        String a2 = getUrlParse(url);

        //字符串转md5
        String a3 = stringToMD5(a2);
        String str3 = "00000000000000000000000000000000";
        String str4 = "00000000000000000000000000000000";
        String str5 = "00000000000000000000000000000000";
        StringBuilder sb = new StringBuilder();
        sb.append(a3);
        sb.append(str3);
        sb.append(str4);
        sb.append(str5);

        byte[] data = str2byte(sb.toString());
        float currentTimeMillis = System.currentTimeMillis();
        int timeStamp = (int) (currentTimeMillis / 1000);
        List<Object> list = new ArrayList<>(10);
        list.add(douyinVM.getVm().getJNIEnv()); // 第一个参数是env
        list.add(0); // 第二个参数，实例方法是jobject，静态方法是jclass，直接填0，一般用不到。
        list.add(-1);
        list.add(timeStamp);
        list.add(douyinVM.getVm().addLocalObject(new ByteArray(douyinVM.getVm(), data)));
        // 直接通过地址调用
        Number number = dalvikModule.getModule().callFunction(douyinVM.getEmulator(), 0x57789, list.toArray())[0];
        ByteArray ret = douyinVM.getVm().getObject(number.intValue());
        //通过调用jni静态方法调用
        //      ByteArray ret = Native.callStaticJniMethodObject(emulator, methodSign, -1, timeStamp, new ByteArray(vm, data));
        // 获取地址的值
        byte[] tt = ret.getValue();
        //执行最外层的com.ss.a.b.a.a
        String s = genXGorgon(tt);


        Map<String, String> result = new HashMap<>();
        result.put("X-Khronos", timeStamp + "");
        result.put("X-Gorgon", s);
        return result;
    }

    private String getUrlParse(String str) {
        int indexOf = str.indexOf("?");
        int indexOf2 = str.indexOf("#");
        if (indexOf == -1) {
            return null;
        }
        if (indexOf2 == -1) {
            return str.substring(indexOf + 1);
        }
        if (indexOf2 > indexOf) {
            return null;
        }
        return str.substring(indexOf + 1, indexOf2);

    }


    private String stringToMD5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    private byte[] str2byte(String str) {
        int length = str.length();
        byte[] bArr = new byte[(length / 2)];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return bArr;
    }

    private String genXGorgon(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        char[] charArray = "0123456789abcdef".toCharArray();
        char[] cArr = new char[(bArr.length * 2)];
        for (int i = 0; i < bArr.length; i++) {
            int b2 = bArr[i] & 255;
            int i2 = i * 2;
            cArr[i2] = charArray[b2 >>> 4];
            cArr[i2 + 1] = charArray[b2 & 15];
        }
        return new String(cArr);
    }

    @Resource
    private DouyinVM douyinVM;
    @Resource
    private DalvikModule dalvikModule;

}
