package com.crack;

import com.crack.utils.Tools;
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.arm.backend.DynarmicFactory;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.memory.Memory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String lasturl = allParams + Tools.encodeUrl("&zzzghostsigh=" + zzzghostsigh);
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
}
