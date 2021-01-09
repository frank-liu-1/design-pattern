package bootsrc.rpclib.core.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Description:
 * 在Spring应用中需要在<code>@Configuration</code>位置配置<code>@EnableRpcLib</code>
 *
 * @author bootsrc
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcLibConfiguration.class})
public @interface EnableRpcLib {
}