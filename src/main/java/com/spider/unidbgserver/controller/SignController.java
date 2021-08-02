package com.spider.unidbgserver.controller;
import com.alibaba.fastjson.JSON;
import com.crack.DouyinSign;
import com.github.unidbg.worker.Worker;
import com.github.unidbg.worker.WorkerPool;
import com.github.unidbg.worker.WorkerPoolFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    final WorkerPool xgPool = WorkerPoolFactory.create(XGWorker::new, processors);

    final static ExecutorService executorXG = Executors.newFixedThreadPool(50);

    @RequestMapping(value="dySign",method =  {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String dySign(@RequestParam("url") String url) {
        try{
            System.out.println("url: "+url);
            Future<Map<String, String>> submit = executorXG.submit(() -> {
                XGWorker worker = xgPool.borrow(1, TimeUnit.MINUTES);
                if (worker != null) {
                    try {
                        return worker.worker(url);
                    }catch (Throwable throwable){
                        System.err.println("XGWorker error: "+throwable);
                    }
                    finally {
                        xgPool.release(worker);
                    }
                } else {
                    System.err.println("XGWorker Borrow failed");
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


class XGWorker implements Worker{
    private final DouyinSign douyinSign;

    public XGWorker() {
        douyinSign = new DouyinSign();
        System.out.println("Create: " + douyinSign);
    }

    public void close() throws IOException {
        System.out.println("XGWorker close()");
        douyinSign.destroy();
    }


    public Map<String, String> worker(String... args) {
        System.out.println("XGWorker worker: " + Thread.currentThread().getName() + Thread.currentThread().getId());
        String url = args[0];
        return douyinSign.crack(url);
    }
}