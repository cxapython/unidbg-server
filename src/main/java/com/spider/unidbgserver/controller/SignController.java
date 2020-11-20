package com.spider.unidbgserver.controller;
import com.alibaba.fastjson.JSON;
import com.crack.DouyinSign;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Controller
public class SignController {
    private static final Logger logger = LoggerFactory.getLogger(SignController.class);

    @RequestMapping(value="dySign",method =  {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String dySign(@RequestParam("url") String url) {
//        System.out.println("url为"+url);
        DouyinSign jnitest = new DouyinSign();
        Map<String,String> result=jnitest.crack(url);
        String jsonString = JSON.toJSONString(result);

        return jsonString;
    }

}