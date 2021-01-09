package bootsrc.rpclib.core.consts;

/**
 * rpc-cache-sdk常量
 *
 * @author bootsrc
 */
public class RpcLibConstants {
    /**
     * 自定义Dubbo Filter的扩展名。<br>
     * 只有在Spring xml配置文件中配置了<code> &lt;dubbo:reference filter="rpc-lib"/&gt; </code>
     * 的服务调用才会被Filter拦截。
     */
    public static final String FILTER_EXTENSION_NAME = "rpc-lib";
    /**
     * rpc-cache-sdk.jar的jar包版本号。用于在local invoke时打印版本信息。
     */
    public static final String JAR_VERSION = "1.0";
}
