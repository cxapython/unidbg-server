package com.spider.unidbgserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CheckHealthController {

    @RequestMapping("/check_health")
    @ResponseBody
    public String check_health(){
        return "ok";
    }
}
