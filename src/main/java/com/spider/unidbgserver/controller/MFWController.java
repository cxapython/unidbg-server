package com.spider.unidbgserver.controller;

import com.alibaba.fastjson.JSON;
import com.crack.MaFengWo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/unidbg")
public class MFWController {
    @Autowired(required = false)
    MaFengWo mfwInstance;

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