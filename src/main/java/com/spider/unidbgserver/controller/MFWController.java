package com.spider.unidbgserver.controller;

import com.alibaba.fastjson.JSON;
import com.crack.MaFengWo;
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

@Controller
@RequestMapping("/unidbg")
public class MFWController {
    public static MaFengWo mfwInstance;

    //共用一个实例
    static {
        mfwInstance = new MaFengWo();
    }

    @RequestMapping(value = "mfwSign", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String mfwSign(@RequestParam("url") String url) throws IOException {
        //同步代码块
        synchronized (this) {
            Map<String, String> result = mfwInstance.xPreAuthencode(url);
            String jsonString = JSON.toJSONString(result);
            return jsonString;
        }
    }
}