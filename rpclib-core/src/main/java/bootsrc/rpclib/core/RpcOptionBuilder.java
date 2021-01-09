package bootsrc.rpclib.core;

import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * RpcOption Builder
 *
 * @author bootsrc
 * @see RpcOption
 */
public class RpcOptionBuilder {
    private String methodName;
    private List<Class<?>> parameterTypes = new ArrayList<>();
    /**
     * 方法参数名字列表，比如["userId", "itemId"]
     */
    private final List<String> parameterNames = new ArrayList<>(4);
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

    private RpcOptionBuilder() {
    }

    public static RpcOptionBuilder builder() {
        return new RpcOptionBuilder();
    }

    public static Method obtainMethod(RpcOption rpcOption) {
        Class<?>[] parameterTypes = rpcOption.getParameterTypes();
        Class<?> interfaceClass = rpcOption.getInterfaceClass();
        String methodName = rpcOption.getMethodName();
        if (Objects.isNull(rpcOption.getParameterTypes())) {
            return ReflectionUtils.findMethod(interfaceClass, methodName);
        }
        return ReflectionUtils.findMethod(interfaceClass, methodName, parameterTypes);
    }

    public RpcOptionBuilder methodName(String methodName) {
        if (Objects.isNull(methodName)) {
            this.methodName = null;
            return this;
        }
        this.methodName = methodName.trim();
        return this;
    }

    public RpcOptionBuilder addParamTypeAndName(Class<?> paramType, String paramName) {
        String paramNameValue = Objects.isNull(paramName) ? null : paramName.trim();
        this.parameterTypes.add(paramType);
        this.parameterNames.add(paramNameValue);
        return this;
    }

    public RpcOptionBuilder addParameterType(Class<?>... parameterTypes) {
        if (this.parameterTypes == null) {
            this.parameterTypes = new ArrayList<>();
        }
        this.parameterTypes.addAll(Arrays.asList(Objects.requireNonNull(parameterTypes)));
        return this;
    }

    public RpcOptionBuilder interfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
        return this;
    }

    public RpcOptionBuilder defaultResult(Object defaultResult) {
        this.defaultResult = defaultResult;
        return this;
    }

    public RpcOptionBuilder throwsException(boolean throwsException) {
        this.throwsException = throwsException;
        return this;
    }

    public RpcOptionBuilder addParameterName(String... parameterNames) {
        this.parameterNames.addAll(Arrays.asList(Objects.requireNonNull(parameterNames)));
        return this;
    }

    public RpcOptionBuilder function(Function<Object[], LocalInvokeResult> function) {
        this.function = function;
        return this;
    }

    public Method obtainMethod() {
        if (CollectionUtils.isEmpty(parameterTypes)) {
            return ReflectionUtils.findMethod(interfaceClass, methodName);
        }
        return ReflectionUtils.findMethod(interfaceClass, methodName, parameterTypes.toArray(new Class[0]));
    }

    public RpcOption build() {
        RpcOption rpcOption = new RpcOption();
        rpcOption.setMethodName(methodName);
        if (CollectionUtils.isNotEmpty(parameterTypes)) {
            rpcOption.setParameterTypes(parameterTypes.toArray(new Class[0]));
        }
        rpcOption.setInterfaceClass(interfaceClass);
        rpcOption.setDefaultResult(defaultResult);
        rpcOption.setThrowsException(throwsException);
        rpcOption.setParameterNames(parameterNames);
        rpcOption.setFunction(function);
        return rpcOption;
    }
}