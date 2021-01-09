package com.example.dubbodemo.main.config;

import bootsrc.rpclib.core.config.EnableRpcLib;
import bootsrc.rpclib.core.consts.RpcLibConstants;
import com.example.dubbodemo.api.CommonConstants;
import com.example.dubbodemo.api.DemoService;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bootsrc
 * @date 2020-08-11 4:21 下午
 */
@Configuration
@EnableRpcLib
public class Config {
//    @Bean
//    public RegistryConfig registryConfig() {
//        RegistryConfig registryConfig = new RegistryConfig("zookeeper://127.0.0.1:2181");
//        Map<String, String> params = new HashMap<>();
////        设置了registry-type=service，会导致服务注册失败
//        params.put("registry-type", "service");
//        registryConfig.setParameters(params);
//        registryConfig.setSimplified(true);
//        return registryConfig;
//    }

    @Bean
    public ReferenceBean<DemoService> demoServiceReferenceBean() {
        ReferenceBean<DemoService> referenceBean = new ReferenceBean<>();
        referenceBean.setInterface(DemoService.class);
        referenceBean.setProvidedBy(CommonConstants.APPLICATION_DEMO_SERVICE);
        // referenceBean.setFilter("rpc-lib");或者下面这种方法启用RpcLibFilter拦截
        referenceBean.setFilter(RpcLibConstants.FILTER_EXTENSION_NAME);
        return referenceBean;
    }
}
