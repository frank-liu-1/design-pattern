package com.example.dubbodemo.main.controller;

import com.example.dubbodemo.api.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("")
public class HomeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
    //方式1
//    @DubboReference(interfaceClass = DemoService.class)
//    private DemoService demoService;

    //方式2， 用@Bean注册bean，这里自动注入bean即可
    @Resource
    private DemoService demoService;

    @RequestMapping("/hello")
    public String hello(String input) {
        String result = null;
        try {
            result = demoService.hello(input);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return result;
    }
}
