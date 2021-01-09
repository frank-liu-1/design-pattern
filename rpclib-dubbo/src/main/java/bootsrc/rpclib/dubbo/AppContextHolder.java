package bootsrc.rpclib.dubbo;

import bootsrc.rpclib.core.consts.RpcLibConstants;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.rpc.Filter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * ApplicationContext Holder。
 * 可提供静态方法获取ApplicationContext。
 * 我们可以同时执行自定义Dubbo Filter里的初始化逻辑
 * <p>
 * 这里设置<code>@Component("rpcAppContextHolder")</code>
 * 是为了防止与主程序中的别的AppContextHolder的bean name相同而产生冲突从而导致
 * Spring启动失败。
 *
 * @author bootsrc
 */
@Component("rpcAppContextHolder")
public class AppContextHolder implements ApplicationContextAware {
    private static ApplicationContext appContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
        Filter filter = ExtensionLoader.getExtensionLoader(Filter.class).getExtension(RpcLibConstants.FILTER_EXTENSION_NAME);
        RpcLibFilter filterObj = (RpcLibFilter) filter;
        filterObj.init();
    }

    public static ApplicationContext getAppContext() {
        return appContext;
    }

    public static boolean isAppContextNotNull() {
        return !Objects.isNull(AppContextHolder.appContext);
    }
}
