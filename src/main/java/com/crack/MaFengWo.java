package com.crack;

import com.github.unidbg.arm.backend.DynarmicFactory;
import com.github.unidbg.linux.android.AndroidARMEmulator;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.memory.Memory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static com.crack.utils.tools.*;

@Component
public class MaFengWo extends AbstractJni {
    private final AndroidEmulator emulator;
    private final Module module;
    private final VM vm;

    static {
        //防止打成jar包的时候找不到文件
        String soPath = "example_binaries/libmfw.so";
        String appPath = "example_binaries/mafengwo.apk";
        ClassPathResource classPathResource = new ClassPathResource(soPath);
        ClassPathResource appPathResource = new ClassPathResource(appPath);

        try {
            InputStream inputStream = classPathResource.getInputStream();
            Files.copy(inputStream, Paths.get("./libmfw.so"), StandardCopyOption.REPLACE_EXISTING);
            InputStream appinputStream = appPathResource.getInputStream();
            Files.copy(appinputStream, Paths.get("./mafengwo.apk"), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> mfwObj() {
        Map<String, String> obj = new HashMap<>();
        return obj;
    }

    public void destroy() throws IOException {
        emulator.close();
    }


    public MaFengWo() {
//        emulator = new AndroidARMEmulator("com.mfw.roadbook"); // 创建模拟器实例，要模拟32位或者64位，在这里区分
        emulator = AndroidEmulatorBuilder.for32Bit().addBackendFactory(new DynarmicFactory(true)).setProcessName("com.mfw.roadbook").build();
        final Memory memory = emulator.getMemory(); // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23));// 设置系统类库解析
        vm = emulator.createDalvikVM(new File("./mafengwo.apk")); // 创建Android虚拟机
        vm.setJni(this);
        //vm.setVerbose(true);// 设置是否打印Jni调用细节
        // 自行修改文件路径,loadLibrary是java加载so的方法
        DalvikModule dm = vm.loadLibrary(new File("./libmfw.so"), true); // 加载libmfw.so到unicorn虚拟内存，加载成功以后会默认调用init_array等函数
        dm.callJNI_OnLoad(emulator);// 手动执行JNI_OnLoad函数
        module = dm.getModule();// 加载好的libmfw.so对应为一个模块
    }

    public Map<String, String> xPreAuthencode(String allParams) {
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv());//第一个参数是env
        list.add(0); //第二个参数，实例方法是object,静态方法jclazz，直接填0，一般用不到
        Object custom = null;
        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(custom); //context
        list.add(vm.addLocalObject(context));
        list.add(vm.addLocalObject(new StringObject(vm, allParams)));
        list.add(vm.addLocalObject(new StringObject(vm, "com.mfw.roadbook")));
        Number number = module.callFunction(emulator, 0x2e235, list.toArray())[0];
        String zzzghostsigh = vm.getObject(number.intValue()).getValue().toString();
        String lasturl = allParams + encodeUrl("&zzzghostsigh=" + zzzghostsigh);
        String oauth_signature = xAuthencode(lasturl);
        Map<String, String> result = mfwObj();
        result.put("zzzghostsigh", zzzghostsigh);
        result.put("oauth_signature", oauth_signature);
        return result;

    }

    public String xAuthencode(String allParams) {
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv());//第一个参数是env
        list.add(0); //第二个参数，实例方法是object,静态方法jclazz，直接填0，一般用不到
        Object custom = null;
        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(custom); //context
        list.add(vm.addLocalObject(context));
        list.add(vm.addLocalObject(new StringObject(vm, allParams)));
        list.add(vm.addLocalObject(new StringObject(vm, "")));
        list.add(vm.addLocalObject(new StringObject(vm, "com.mfw.roadbook")));
        list.add(vm.addLocalObject(null));
        Number number = module.callFunction(emulator, 0x2e2fd, list.toArray())[0];
        String result = vm.getObject(number.intValue()).getValue().toString();
        return result;

    }

//    public static void main(String[] args){
//        MaFengWo mfw = new MaFengWo();
//        //String fullurl = "GET&https%3A%2F%2Fmapi.mafengwo.cn%2Fuser%2Ffriend%2Fget_friends_list%2Fv1&app_code%3Dcom.mfw.roadbook%26app_ver%3D9.3.7%26app_version_code%3D734%26brand%3Dgoogle%26channel_id%3DMFW%26dev_ver%3DD1907.0%26device_id%3D40%253A4E%253A36%253AB1%253A47%253A3E%26device_type%3Dandroid%26hardware_model%3DPixel%26has_notch%3D0%26jsondata%3D%257B%2522user_id%2522%253A%252213096%2522%252C%2522page%2522%253A%257B%2522boundary%2522%253A%252240%2522%252C%2522num%2522%253A%252220%2522%257D%252C%2522type%2522%253A%2522follows%2522%257D%26mfwsdk_ver%3D20140507%26o_lat%3D40.008481%26o_lng%3D116.350205%26oauth_consumer_key%3D5%26oauth_nonce%3Dea6cad67-a5fe-4564-89ce-b2a78b0e05de%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1626421913%26oauth_token%3D0_0969044fd4edf59957f4a39bce9200c6%26oauth_version%3D1.0%26open_udid%3D40%253A4E%253A36%253AB1%253A47%253A3E%26screen_height%3D1794%26screen_scale%3D2.88%26screen_width%3D1080%26sys_ver%3D8.1.0%26time_offset%3D480%26x_auth_mode%3Dclient_auth";
//        String fullurl ="GET&https%3A%2F%2Fmapi.mafengwo.cn%2Fuser%2Fprofile%2Fget_profile%2Fv2&app_code%3Dcom.mfw.roadbook%26app_ver%3D10.0.0%26app_version_code%3D837%26brand%3Dgoogle%26channel_id%3DMFW-WDJPPZS-1%26dev_ver%3DD2001.0%26device_id%3DA4%253A50%253A46%253A36%253AA8%253A43%26device_type%3Dandroid%26hardware_model%3DPixel%26has_notch%3D0%26mfwsdk_ver%3D20140507%26o_coord%3Dwgs%26o_lat%3D40.008221%26o_lng%3D116.350234%26oauth_consumer_key%3D5%26oauth_nonce%3D00b85005-ab89-401f-9037-aa9b3f37198e%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1626430325%26oauth_token%3D0_0969044fd4edf59957f4a39bce9200c6%26oauth_version%3D1.0%26open_udid%3DA4%253A50%253A46%253A36%253AA8%253A43%26patch_ver%3D1.6%26screen_height%3D1794%26screen_scale%3D2.88%26screen_width%3D1080%26sys_ver%3D8.1.0%26time_offset%3D480%26user_id%3D30044554%26x_auth_mode%3Dclient_auth";
//        Map<String,String> result=mfw.xPreAuthencode(fullurl);
//
//        System.out.println(result.toString());
//    }


}
