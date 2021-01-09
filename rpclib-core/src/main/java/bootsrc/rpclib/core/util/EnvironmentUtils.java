package bootsrc.rpclib.core.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Description:
 * 获取env环境变量值如： cerpv1,cerpv2,cerpedgj,cerpdemo
 *
 * @author bootsrc
 */
@Component
public class EnvironmentUtils implements InitializingBean {
    public static final String ENV_DEMO = "demo";

    /**
     * env
     */
    @Value("${rpc.lib.env:demo}")
    private String env;
    /**
     * 当前进程所在的用户Dubbo分组
     */
    @Value("${rpc.lib.group:}")
    private String group;

    private static String envValue;
    private static boolean demo = false;
    private static String groupValue;

    @Override
    public void afterPropertiesSet() {
        envValue = env;
        demo = ENV_DEMO.equals(env);
        groupValue = group;
    }

    /**
     * 获取env环境变量值如： v1,v2,demo
     *
     * @return env值
     */
    public static String getEnv() {
        return envValue;
    }

    /**
     * 获取当前进程的用户分组，比如g1，g2，或者"", 默认为""
     *
     * @return group
     */
    public static String getGroup() {
        return groupValue;
    }

    /**
     * 判断是否为demo环境
     *
     * @return 是否为demo环境
     */
    public static boolean isDemo() {
        return demo;
    }
}
