package bootsrc.rpclib.dubbo;

import bootsrc.rpclib.core.LocalInvokeResult;
import bootsrc.rpclib.core.RpcConfigHolder;
import bootsrc.rpclib.core.RpcOption;
import bootsrc.rpclib.core.consts.RpcLibConstants;
import bootsrc.rpclib.core.util.EnvironmentUtils;
import bootsrc.rpclib.core.util.RpcMethodUtils;
import com.alibaba.fastjson.JSON;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 核心过滤器
 * 通过RpcCacheFilter来拦截RPC，获取缓存值, 如果缓存中有数据，直接返回，
 * 无需去调用远程的provider。
 *
 * @author bootsrc
 */
public class RpcLibFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcLibFilter.class);

    private RpcConfigHolder rpcConfigHolder;

    public void init() {
        rpcConfigHolder = AppContextHolder.getAppContext().getBean(RpcConfigHolder.class);
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Result result;
        if (AppContextHolder.isAppContextNotNull()) {
            try {
                String methodName = invocation.getMethodName();
                Class<?>[] parameterTypes = invocation.getParameterTypes();
                Class<?> interfaceClass = invoker.getInterface();

                Method method;
                String methodKey;
                try {
                    method = interfaceClass.getMethod(methodName, parameterTypes);
                    methodKey = RpcMethodUtils.toMethodKey(method);
                } catch (NoSuchMethodException e) {
                    LOGGER.error(e.getMessage(), e);
                    return invoker.invoke(invocation);
                }

                if (rpcConfigHolder.getConfigs().containsKey(methodKey)) {
                    CompletableFuture<AppResponse> future = new CompletableFuture<>();
                    String referenceGroup = invoker.getUrl().getParameter(CommonConstants.GROUP_KEY);
                    RpcOption rpcOption = rpcConfigHolder.getConfigs().get(methodKey);
                    Object[] arguments = invocation.getArguments();
                    LocalInvokeResult localInvokeResult = localInvoke(rpcOption, method, arguments);
                    if (rpcConfigHolder.isLogEnabled()) {
                        // method.toGenericString()里面已经包含了interface class name
                        LOGGER.info("jar={},env={},group={},refGroup={}\nmethod={}\nparamNames={}\nargs={}\nrpcLibResult={}"
                                , RpcLibConstants.JAR_VERSION, EnvironmentUtils.getEnv(), EnvironmentUtils.getGroup()
                                , referenceGroup, methodKey, JSON.toJSONString(rpcOption.getParameterNames())
                                , JSON.toJSONString(arguments), JSON.toJSONString(localInvokeResult));
                    }

                    // 如果localInvokeResult为null或者被指定了需要远程调用，则执行RPC调用
                    if (Objects.isNull(localInvokeResult) || localInvokeResult.isNeedInvokeRemote()) {
                        result = invoker.invoke(invocation);
                        return result;
                    }

                    // 处理程序主动设置的异常 {@link LocalInvokeResult#setException(Throwable)}
                    if (!Objects.isNull(localInvokeResult.getException())) {
                        future.complete(new AppResponse(localInvokeResult.getResultValue()));
                        return new AsyncRpcResult(future, invocation);
                    }

                    if (!localInvokeResult.isParamValid()) {
                        if (rpcOption.isThrowsException()) {
                            future.completeExceptionally(new IllegalArgumentException(localInvokeResult.getParamErrMsg()));
                            return new AsyncRpcResult(future, invocation);
                        } else {
                            future.complete(new AppResponse(rpcOption.getDefaultResult()));
                            return new AsyncRpcResult(future, invocation);
                        }
                    }

                    boolean completed = future.complete(new AppResponse(localInvokeResult.getResultValue()));
                    return new AsyncRpcResult(future, invocation);
                }
            } catch (Exception bodyEx) {
                LOGGER.error(bodyEx.getMessage(), bodyEx);
            }
        }

        result = invoker.invoke(invocation);
        return result;
    }

    private LocalInvokeResult localInvoke(RpcOption rpcOption, Method method, Object[] arguments) {
        LocalInvokeResult invokeResult = new LocalInvokeResult();
        invokeResult.setParamValid(true);
        // 参数检验
        try {
            for (int i = 0; i < method.getParameters().length; i++) {
                if (Objects.isNull(arguments[i])) {
                    invokeResult.setParamValid(false);
                    invokeResult.setParamErrMsg(String.format("%s is null", rpcOption.getParameterNames().get(i)));
                    break;
                }
            }
        } catch (Exception validateEx) {
            LOGGER.error("localInvoke_paramValidate_error_cause_of_config_error:" + validateEx.getMessage(), validateEx);
            invokeResult.setNeedInvokeRemote(true);
            return invokeResult;
        }

        if (!invokeResult.isParamValid()) {
            invokeResult.setResultValue(rpcOption.getDefaultResult());
            invokeResult.setNeedInvokeRemote(false);

            return invokeResult;
        }

        if (!Objects.isNull(rpcOption.getFunction())) {
            try {
                invokeResult = rpcOption.getFunction().apply(arguments);
            } catch (Exception callbackEx) {
                LOGGER.error("localInvoke_callback_exe_failed:" + callbackEx.getMessage(), callbackEx);
                invokeResult.setNeedInvokeRemote(true);
            }
            return invokeResult;
        }

        invokeResult.setResultValue(null);
        invokeResult.setNeedInvokeRemote(true);
        return invokeResult;
    }
}
