package com.example.dubbodemo.provider.service;

import com.example.dubbodemo.api.DemoService;
import com.example.dubbodemo.provider.dao.DemoDAO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @autor bootsrc
 */
@Service
public class DemoServiceImpl implements DemoService {
    private Map<String, String> jedis = new HashMap<>(16);

    @Resource
    private DemoDAO demoDAO;

    @Override
    public String hello(String input) {
        if (Objects.isNull(input)) {
            throw new IllegalArgumentException("input is null");
        }
        String value = jedis.get(input);
        if (Objects.isNull(value)) {
            value = demoDAO.hello(input);
            if (!Objects.isNull(value)) {
                jedis.put(input, value);
            }
        }
        return value;
    }
}
