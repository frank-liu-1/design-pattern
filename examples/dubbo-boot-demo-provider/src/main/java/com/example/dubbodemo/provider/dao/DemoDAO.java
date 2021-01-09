package com.example.dubbodemo.provider.dao;

import org.springframework.stereotype.Repository;

/**
 * Date: 2021/1/1 17:00
 * Description:
 *
 * @author
 */
@Repository
public class DemoDAO {
    public String hello(String input) {
        return "Hello " + input;
    }
}
