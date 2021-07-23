package com.spider.unidbgserver.controller;

import com.alibaba.fastjson.JSON;
import com.crack.DouyinSign;
import com.crack.MaFengWo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/unidbg")
public class MFWController {
    @RequestMapping(value="mfwSign",method =  {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String mfwSign(@RequestParam("url") String url) throws IOException {
        MaFengWo mfw = new MaFengWo();
        Map<String,String> result=mfw.xPreAuthencode(url);
        String jsonString = JSON.toJSONString(result);
        return jsonString;
    }
}