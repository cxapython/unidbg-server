package com.spider.unidbgserver.controller;
import com.alibaba.fastjson.JSON;
import com.crack.DouyinSign;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@Controller
@RequestMapping("/unidbg")
public class SignController {
    @RequestMapping(value="dySign",method =  {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String dySign(@RequestParam("url") String url) {
        DouyinSign jnitest = new DouyinSign();
        Map<String,String> result=jnitest.crack(url);
        String jsonString = JSON.toJSONString(result);

        return jsonString;
    }

}