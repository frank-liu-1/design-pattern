package bootsrc.rpclib.core;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 缓存优化的配置的持有者interface
 *
 * @author bootsrc
 */
public interface RpcConfigHolder {
    /**
     * init configs
     */
    void initConfigs();

    /**
     * get configs
     * Map.key: String, value is: {@link bootsrc.rpclib.core.util.RpcMethodUtils#toMethodKey(Method)}
     * Map.value: RpcOption
     *
     * @return config Map
     */
    Map<String, RpcOption> getConfigs();

    /**
     * 测试环境，设置为true,启用日志，会打印出从缓存中取的数据.
     * 生产环境，设置为false,不打印日志，不会打印从缓存中取到的数据.
     *
     * @return 是否启用日志
     */
    boolean isLogEnabled();
}
