package com.spider.unidbgserver.controller;

import com.alibaba.fastjson.JSON;
import com.crack.DouyinSign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("/unidbg")
public class SignController {
    @Autowired(required = false)
    DouyinSign dyInstance;

    @RequestMapping(value = "dySign", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String dySign(@RequestParam("url") String url) {
        synchronized (this) {
            Map<String, String> result = dyInstance.crack(url);
            String jsonString = JSON.toJSONString(result);

            return jsonString;
        }

    }

}