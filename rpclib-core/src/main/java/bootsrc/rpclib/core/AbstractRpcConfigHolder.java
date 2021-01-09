package bootsrc.rpclib.core;

import bootsrc.rpclib.core.util.EnvironmentUtils;
import bootsrc.rpclib.core.util.RpcMethodUtils;
import bootsrc.rpclib.core.util.RpcStringUtils;
import bootsrc.rpclib.core.util.SamplingUtils;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 缓存优化的配置的持有者抽象类
 * 使用rpc-cache-sdk.jar的项目需要继承此类.
 *
 * @author bootsrc
 */
public abstract class AbstractRpcConfigHolder implements RpcConfigHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRpcConfigHolder.class);
    private final Map<String, RpcOption> configs = new HashMap<>(64);

    @Resource
    private SamplingUtils samplingUtils;

    public AbstractRpcConfigHolder() {
        initConfigs();
        LOGGER.info("rpc-cache-sdk configs_count={},\nconfigs={}", getConfigs().size(), JSON.toJSONString(getConfigs()));
    }

    /**
     * get configs
     * Map.key: String, value is: {@link bootsrc.rpclib.core.util.RpcMethodUtils#toMethodKey(Method)}
     * Map.value: RpcOption
     *
     * @return config Map
     */
    @Override
    public Map<String, RpcOption> getConfigs() {
        return configs;
    }

    /**
     * 测试环境，设置为true,启用日志，会打印出从缓存中取的数据.
     * 生产环境，设置为false,不打印日志，不会打印从缓存中取到的数据.
     *
     * @return 是否启用日志
     */
    @Override
    public boolean isLogEnabled() {
        return EnvironmentUtils.isDemo() || samplingUtils.hit();
    }

    protected void register(RpcOption rpcOption) {
        Method method = RpcOptionBuilder.obtainMethod(rpcOption);
        String methodKey = RpcMethodUtils.toMethodKey(method);
        if (validate(method, methodKey, rpcOption)) {
            getConfigs().put(methodKey, rpcOption);
        }
    }

    private boolean validate(Method method, String methodKey, RpcOption option) {
        int parameterCount = method.getParameterCount();
        if (RpcStringUtils.isBlank(option.getMethodName())
                || Objects.isNull(option.getInterfaceClass())
                || option.getParameterNames() == null
                || parameterCount != option.getParameterNames().size()
                || parameterCount != option.getParameterTypes().length) {
            LOGGER.error("OptimizeConfigHolder config error, method={}", methodKey);
            return false;
        }
        return true;
    }
}
