package com.example.dubbodemo.main.jedis;

import org.springframework.stereotype.Component;

/**
 * Date: 2021/1/1 17:07
 * Description:
 * 模拟jedis，这里可以读到dubbo provider写入到redis中的数据
 * @author bootsrc
 */
@Component
public class JedisMock {
    public String get(String key) {
        return "hello " + key;
    }
}
