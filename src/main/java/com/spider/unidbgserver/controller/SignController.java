package com.spider.unidbgserver.controller;

import com.alibaba.fastjson.JSON;
import com.crack.DouyinSign;
import com.spider.unidbgserver.service.DouyinSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/unidbg")
public class SignController {
    @Autowired(required = false)
    DouyinSign dyInstance;

    @RequestMapping(value = "dySign", method = {RequestMethod.GET, RequestMethod.POST})
    public String dySign(@RequestParam("url") String url) {
        synchronized (this) {
            Map<String, String> result = dyInstance.crack(url);
            String jsonString = JSON.toJSONString(result);

            return jsonString;
        }

    }

    @RequestMapping(value = "dySignNext", method = {RequestMethod.GET, RequestMethod.POST})
    public String dySignNext(@RequestParam("url") String url) {
        synchronized (this) {
            Map<String, String> result = douyinSignService.crack(url);
            String jsonString = JSON.toJSONString(result);

            return jsonString;
        }

    }


    @Resource
    private DouyinSignService douyinSignService;

}