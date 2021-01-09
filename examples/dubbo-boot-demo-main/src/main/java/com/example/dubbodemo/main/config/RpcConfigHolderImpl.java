package com.example.dubbodemo.main.config;

import bootsrc.rpclib.core.AbstractRpcConfigHolder;
import bootsrc.rpclib.core.LocalInvokeResult;
import bootsrc.rpclib.core.RpcOption;
import bootsrc.rpclib.core.RpcOptionBuilder;
import com.example.dubbodemo.api.DemoService;
import com.example.dubbodemo.main.jedis.JedisMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * Date: 2020/12/31 14:57
 * Description:
 *
 * @author bootsrc
 */
@Component
public class RpcConfigHolderImpl extends AbstractRpcConfigHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcConfigHolderImpl.class);

    @Resource
    private JedisMock jedisMock;
    
    @Override
    public void initConfigs() {
        DemoServiceHello();
    }

    private void DemoServiceHello() {
        try {
            RpcOption rpcOption = RpcOptionBuilder.builder()
                    .interfaceClass(DemoService.class)
                    .addParamTypeAndName(String.class, "input")
                    .methodName("hello")
                    .throwsException(true)
                    .defaultResult(null)
                    .function((Object[] args) -> {
                        String input = (String) args[0];
                        // 模拟从Redis等缓存中直接读取数据
                        String result = jedisMock.get(input);

                        LocalInvokeResult localInvokeResult = new LocalInvokeResult();
                        localInvokeResult.setParamValid(true);
                        localInvokeResult.setException(null);
                        localInvokeResult.setResultValue(result);
                        localInvokeResult.setNeedInvokeRemote(Objects.isNull(result));
                        return localInvokeResult;
                    }).build();

            register(rpcOption);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
