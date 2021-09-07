package com.spider.unidbgserver.jni;

import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.api.ApplicationInfo;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class DouyinJni extends AbstractJni {
    private static final String SO_PATH = "/Users/chennan/javaproject/unidbg-server/src/main/resources/example_binaries/libcms.so";

    static {
        String soPath = "example_binaries/libcms.so";
        ClassPathResource classPathResource = new ClassPathResource(soPath);
        try {
            InputStream inputStream = classPathResource.getInputStream();
            Files.copy(inputStream, Paths.get("./libcms.so"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> dyObj() {
        Map<String, String> obj = new HashMap<>();
        return obj;
    }


    @Override
    public DvmObject callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "java/lang/Thread->currentThread()Ljava/lang/Thread;":
                return vm.resolveClass("java/lang/Thread").newObject(Thread.currentThread());
            case "android/provider/Settings$Secure->getString(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;":
                return new StringObject(vm, "");
            case "java/net/NetworkInterface->getByName(Ljava/lang/String;)Ljava/net/NetworkInterface;":
                return vm.resolveClass("java/net/NetworkInterface").newObject(null);
            case "android/app/Application->getApplicationInfo()Landroid/content/pm/ApplicationInfo;":
                return new ApplicationInfo(vm);
            case "android/net/Uri->parse(Ljava/lang/String;)Landroid/net/Uri;":
                return vm.resolveClass("android/net/Uri").newObject(null);
            case "java/lang/System->getProperty(Ljava/lang/String;)Ljava/lang/String;":
                return new StringObject(vm, "6");
        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }

    @Override
    public DvmObject callObjectMethodV(BaseVM vm, DvmObject dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "java/lang/Thread->getStackTrace()[Ljava/lang/StackTraceElement;":
                StackTraceElement[] elements = Thread.currentThread().getStackTrace();
                DvmObject[] objs = new DvmObject[elements.length];
                for (int i = 0; i < elements.length; i++) {
                    objs[i] = vm.resolveClass("java/lang/StackTraceElement").newObject(elements[i]);
                }
                return new ArrayObject(objs);
            case "java/lang/StackTraceElement->getClassName()Ljava/lang/String;":
                StackTraceElement element = (StackTraceElement) dvmObject.getValue();
                return new StringObject(vm, element.getClassName());
            case "android/app/Application->getApplicationInfo()Landroid/content/pm/ApplicationInfo;":
                return new ApplicationInfo(vm);
            case "android/app/Application->getPackageName()Ljava/lang/String;":
                return new StringObject(vm, "com.ss.android.ugc.aweme");
            case "android/content/ContentResolver->call(Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;Landroid/os/Bundle;)Landroid/os/Bundle;":
                return vm.resolveClass("android/os/Bundle").newObject(null);
            case "android/os/Bundle->getString(Ljava/lang/String;)Ljava/lang/String;":
                return vm.resolveClass("android/os/Bundle").newObject(null);
            case "android/os/Bundle->getBytes(Ljava/lang/String;)[B":
                new ByteArray(vm, "sss".getBytes());
            case "java/net/NetworkInterface->getHardwareAddress()[B":
                return new ByteArray(vm, "".getBytes());
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public DvmObject<?> getObjectField(BaseVM vm, DvmObject<?> dvmObject, String signature) {
        if ("android/app/ApplicationInfo->sourceDir:Ljava/lang/String;".equals(signature)) {
            return new StringObject(vm, SO_PATH);
        }
        if ("android/content/pm/ApplicationInfo->sourceDir:Ljava/lang/String;".equals(signature)) {
            return new StringObject(vm, SO_PATH);
        }
        return super.getObjectField(vm, dvmObject, signature);
    }

    public boolean callStaticBooleanMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        return signature.equals("android/os/Debug->isDebuggerConnected()Z");
    }

    public static String genXGorgon(byte[] bArr) {
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

    public static byte[] str2byte(String str) {
        int length = str.length();
        byte[] bArr = new byte[(length / 2)];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return bArr;
    }

    public static String stringToMD5(String plainText) {
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

    public static String getUrlParse(String str) {
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

}
