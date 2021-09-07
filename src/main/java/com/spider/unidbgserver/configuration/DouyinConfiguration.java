package com.spider.unidbgserver.configuration;

import com.github.unidbg.AndroidEmulator;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

@Configuration
public class DouyinConfiguration {

    @Bean(name = "douyinVM")
    public DouyinVM douyinVM() throws FileNotFoundException {
        DouyinVM douyinVM = new DouyinVM();

        AndroidEmulator emulator = AndroidEmulatorBuilder
                .for32Bit()
                .addBackendFactory(new DynarmicFactory(true))
                .setProcessName("com.ss.android.ugc.aweme")
                .build();

        Memory memory = emulator.getMemory(); // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23));// 设置系统类库解析

        VM vm = emulator.createDalvikVM(ResourceUtils.getFile("classpath:example_binaries\\douyin10_6.apk")); // 创建Android虚拟机
//        VM vm = emulator.createDalvikVM(new File("example_binaries\\douyin10_6.apk")); // 创建Android虚拟机
        vm.setJni(new DouyinJniNext());
        vm.setVerbose(true);// 设置是否打印Jni调用细节

        douyinVM.setVm(vm);
        douyinVM.setEmulator(emulator);

        return douyinVM;
    }


    @Bean("douyinModule")
    public DalvikModule douyinModule(@Qualifier("douyinVM") DouyinVM douyinVM) throws FileNotFoundException {
        // 自行修改文件路径,loadLibrary是java加载so的方法
        DalvikModule dm = douyinVM.getVm()
                .loadLibrary(ResourceUtils.getFile("classpath:example_binaries\\libcms.so"), false); // 加载libcms.so到unicorn虚拟内存，加载成功以后会默认调用init_array等函数
        dm.callJNI_OnLoad(douyinVM.getEmulator());// 手动执行JNI_OnLoad函数
        //leviathan所在的类，调用resolveClass解析该class对象
        DvmClass nativeClazz = douyinVM.getVm().resolveClass("com/ss/sys/ces/a");
        try {
            nativeClazz.callStaticJniMethod(douyinVM.getEmulator(), "leviathan(II[B)[B", -1, 123456,
                    new ByteArray(douyinVM.getVm(), "".getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dm;
    }


    private static Logger logger = LoggerFactory.getLogger(DouyinConfiguration.class);
}
