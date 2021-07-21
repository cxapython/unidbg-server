package com.crack;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidARMEmulator;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmClass;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.utils.Inspector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class TTEncrypt extends AbstractJni {
    private final AndroidEmulator emulator;
    private final Module module;
    private final VM vm;
    private final DvmClass TTEncryptUtils;

    private TTEncrypt() {
        emulator = new AndroidARMEmulator("com.xxx.offical"); // 创建模拟器实例，要模拟32位或者64位，在这里区分
        Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23, new String[0]));
        vm = emulator.createDalvikVM(null);
        vm.setJni(this);
        vm.setVerbose(true);
        DalvikModule dm = vm.loadLibrary(new File("/Users/chennan/javaproject/unidbg-server/src/main/resources/example_binaries/libttEncrypt.so"), false);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();
        TTEncryptUtils = vm.resolveClass("com/bytedance/frameworks/core/encrypt/TTEncryptUtils", new DvmClass[0]);
    }

    private void destroy() throws IOException {
        emulator.close();
        System.out.println("destroy");
    }

    public static void main(String[] args) throws Exception {
        args = new String[]{"1606199751140","548590668454936","m9jc405bmajk2fk3","77:67:74:82:3a:96"};

        TTEncrypt test = new TTEncrypt();
        String str = "{\"_gen_time\":\"" + args[0] + "\",\"header\":{\"access\":\"wifi\",\"aid\":1128,\"app_version\":\"5.5.0\",\"appkey\":\"57bfa27c67e58e7d920028d3\",\"build_serial\":\"41488569\",\"carrier\":\"China Mobile GSM\",\"channel\":\"aweGW\",\"clientudid\":\"bcf9cca3-5644-4828-a79e-c627f6e47da6\",\"cpu_abi\":\"armeabi-v7a\",\"density_dpi\":192,\"device_brand\":\"samsung\",\"device_id\":\"\",\"device_manufacturer\":\"samsung\",\"device_model\":\"SM-G925F\",\"display_density\":\"mdpi\",\"display_name\":\"抖音短视频\",\"language\":\"zh\",\"manifest_version_code\":550,\"mc\":\"" + args[3] + "\",\"mcc_mnc\":\"46000\",\"not_request_sender\":0,\"openudid\":\"" + args[2] + "\",\"os\":\"Android\",\"os_api\":22,\"os_version\":\"5.1.1\",\"packageX\":\"com.ss.android.ugc.aweme\",\"region\":\"CN\",\"release_build\":\"2132ca7_20190320\",\"resolution\":\"1280x720\",\"rom\":\"eng.se.infra.20181117.120021\",\"rom_version\":\"samsung-user 5.1.1 20171130.276299 release-keys\",\"sdk_version\":\"2.5.5.8\",\"serial_number\":\"41488569\",\"sig_hash\":\"aea615ab910015038f73c47e45d21466\",\"sim_region\":\"cn\",\"sim_serial_number\":[{\"sim_serial_number\":\"70459549640190877299\"}],\"timezone\":28800,\"tz_name\":\"Asia\\\\/Shanghai\",\"tz_offset\":0,\"udid\":\"" + args[1] + "\",\"update_version_code\":5502,\"version_code\":550},\"magic_tag\":\"ss_app_log\"}";
        test.ttEncrypt(str);
        test.destroy();
    }

    private void ttEncrypt(String str) throws IOException {
        long start = System.currentTimeMillis();
        byte[] bArr2 = str.getBytes("UTF-8");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(8192);
        GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        gZIPOutputStream.write(bArr2);
        gZIPOutputStream.close();
        bArr2 = byteArrayOutputStream.toByteArray();
        ByteArray ret = TTEncryptUtils.callStaticJniMethodObject(this.emulator, "ttEncrypt([BI)[B", new Object[]{new ByteArray(vm,bArr2), bArr2.length});
        byte[] array =  ret.getValue();
        Inspector.inspect(array, "hex="+array.toString());
    }
}
