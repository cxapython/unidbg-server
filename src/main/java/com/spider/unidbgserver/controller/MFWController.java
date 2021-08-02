package com.spider.unidbgserver.controller;

import com.alibaba.fastjson.JSON;
import com.github.unidbg.arm.backend.dynarmic.DynarmicLoader;
import com.github.unidbg.worker.WorkerPool;
import com.github.unidbg.worker.WorkerPoolFactory;
import com.worker.MFWWorker;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

//@Controller
//@RequestMapping("/unidbg")
//public class MFWController {
//    @RequestMapping(value="mfwSign",method =  {RequestMethod.GET,RequestMethod.POST})
//    @ResponseBody
//    public String mfwSign(@RequestParam("url") String url) throws IOException {
//        MaFengWo mfw = new MaFengWo();
//        Map<String,String> result=mfw.xPreAuthencode(url);
//        String jsonString = JSON.toJSONString(result);
//        return jsonString;
//    }
//}

@Controller
@RequestMapping("/unidbg")
public class MFWController {

    static {
        DynarmicLoader.forceUseDynarmic();
    }

    final int processors = Runtime.getRuntime().availableProcessors()/2 +2;
    final WorkerPool xgPool = WorkerPoolFactory.create(MFWWorker::new, processors);

    final static ExecutorService executor = new ThreadPoolExecutor(10, 20, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(10), new ThreadPoolExecutor.CallerRunsPolicy());

    @RequestMapping(value="mfwSign",method =  {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String mfwSign(@RequestParam("url") String url) {
        try{
            Future<Map<String, String>> submit = executor.submit(() -> {
                MFWWorker worker = xgPool.borrow(1, TimeUnit.MINUTES);
                if (worker != null) {
                    try {
                        return worker.worker(url);
                    }catch (Throwable throwable){
                        System.err.println("MFWWorker error: "+throwable);
                    }
                    finally {
                        xgPool.release(worker);
                    }
                } else {
                    System.err.println("MFWWorker Borrow failed");
                }

                return null;
            });
            Map<String,String> result= submit.get();
            return JSON.toJSONString(result);

        }catch (Throwable throwable){
            throwable.printStackTrace();
//            System.out.println("mfwSign throwable: "+throwable.toString());
            return null;
        }

    }
}

