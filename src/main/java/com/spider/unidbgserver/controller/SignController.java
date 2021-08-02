package com.spider.unidbgserver.controller;
import com.alibaba.fastjson.JSON;
import com.github.unidbg.worker.WorkerPool;
import com.github.unidbg.worker.WorkerPoolFactory;
import com.worker.DYWorker;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.*;

import com.github.unidbg.arm.backend.dynarmic.DynarmicLoader;
//多线程池版本，ThreadPool Version
@Controller
@RequestMapping("/unidbg")
public class SignController {

    static {
        DynarmicLoader.forceUseDynarmic();
    }

    final int processors = Runtime.getRuntime().availableProcessors()/2 +2;
    final WorkerPool xgPool = WorkerPoolFactory.create(DYWorker::new, processors);

    final static ExecutorService executor = new ThreadPoolExecutor(10, 20, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(10), new ThreadPoolExecutor.CallerRunsPolicy());

    @RequestMapping(value="dySign",method =  {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String dySign(@RequestParam("url") String url) {
        try{
            System.out.println("url: "+url);
            Future<Map<String, String>> submit = executor.submit(() -> {
                DYWorker worker = xgPool.borrow(1, TimeUnit.MINUTES);
                if (worker != null) {
                    try {
                        return worker.worker(url);
                    }catch (Throwable throwable){
                        System.err.println("DYWorker error: "+throwable);
                    }
                    finally {
                        xgPool.release(worker);
                    }
                } else {
                    System.err.println("DYWorker Borrow failed");
                }

                return null;
            });
            Map<String,String> result= submit.get();
            return JSON.toJSONString(result);

        }catch (Throwable throwable){
            throwable.printStackTrace();
            System.out.println("dySign throwable: "+throwable.toString());
            return null;
        }

    }
}


