package com.spider.unidbgserver.configuration;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.arm.backend.DynarmicFactory;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmClass;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.memory.Memory;
import com.spider.unidbgserver.jni.DouyinJniNext;
import com.spider.unidbgserver.vm.DouyinVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class DouyinConfiguration {

    @Bean
    public DouyinVM douyinVM() {
        DouyinVM douyinVM = new DouyinVM();

        AndroidEmulator emulator = AndroidEmulatorBuilder
                .for32Bit()
                .addBackendFactory(new DynarmicFactory(true))
                .setProcessName("com.ss.android.ugc.aweme")
                .build();

        Memory memory = emulator.getMemory(); // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23));// 设置系统类库解析
        VM vm = emulator.createDalvikVM(); // 创建Android虚拟机

        vm.setJni(new DouyinJniNext());
        vm.setVerbose(true);// 设置是否打印Jni调用细节
        return douyinVM;
    }


    @Bean
    public DalvikModule douyinModule(DouyinVM vm) {
        // 自行修改文件路径,loadLibrary是java加载so的方法
        DalvikModule dm = vm.getVm().loadLibrary(new File("./libcms.so"), false); // 加载libcms.so到unicorn虚拟内存，加载成功以后会默认调用init_array等函数
        dm.callJNI_OnLoad(vm.getEmulator());// 手动执行JNI_OnLoad函数
        Module module = dm.getModule();// 加载好的libcms.so对应为一个模块

        //leviathan所在的类，调用resolveClass解析该class对象
        DvmClass nativeClazz = vm.getVm().resolveClass("com/ss/sys/ces/a");
        try {
            nativeClazz.callStaticJniMethod(vm.getEmulator(), "leviathan(II[ )[B", -1, 123456,
                    new ByteArray(vm.getVm(), "".getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dm;
    }


    private static Logger logger = LoggerFactory.getLogger(DouyinConfiguration.class);
}
