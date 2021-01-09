package bootsrc.rpclib.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 缓存优化的配置项
 *
 * @author bootsrc
 */
public class RpcOption implements Serializable {
    private static final long serialVersionUID = 2964046023113151288L;

    private String methodName;
    private Class<?>[] parameterTypes;
    /**
     * 方法参数名字列表，比如["userId", "itemId"]
     */
    private List<String> parameterNames = new ArrayList<>(4);
    private Class<?> interfaceClass;
    /**
     * 如果参数错误，此时方法调用需要返回的默认值
     */
    private Object defaultResult;
    /**
     * 如果参数错误，是否需要抛出IllegalArgumentException.
     * 默认不抛出异常，只返回defaultResult
     */
    private boolean throwsException;

    /**
     * 自定义本地方法处理，这里直接返回方法调用的数据，无需通过RPC
     */
    private Function<Object[], LocalInvokeResult> function;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public Object getDefaultResult() {
        return defaultResult;
    }

    public void setDefaultResult(Object defaultResult) {
        this.defaultResult = defaultResult;
    }

    public boolean isThrowsException() {
        return throwsException;
    }

    public void setThrowsException(boolean throwsException) {
        this.throwsException = throwsException;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public void setParameterNames(List<String> parameterNames) {
        this.parameterNames = parameterNames;
    }

    public Function<Object[], LocalInvokeResult> getFunction() {
        return function;
    }

    public void setFunction(Function<Object[], LocalInvokeResult> function) {
        this.function = function;
    }
}
