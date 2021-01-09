package com.example.dubbodemo.provider;

import com.example.dubbodemo.api.DemoService;
import org.apache.dubbo.config.ServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ProviderAppListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private DemoService demoService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ServiceConfig<DemoService> serviceConfig = new ServiceConfig<>();
        serviceConfig.setInterface(DemoService.class);
        serviceConfig.setRef(demoService);
        // 这里增加了一个参数executes=100. 我们测试下
        // 设置了dubbo.registry.simplified=true后，executes=100还在不在zookeeper里
//        serviceConfig.setExecutes(100);

        serviceConfig.export();
    }
}
