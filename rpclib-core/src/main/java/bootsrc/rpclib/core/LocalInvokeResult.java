package bootsrc.rpclib.core;

import java.io.Serializable;

/**
 * 本地调用方法的返回Model
 *
 * @author bootsrc
 */
public class LocalInvokeResult implements Serializable {
    private static final long serialVersionUID = 1524394241352583659L;
    /**
     * RPC method参数arguments验证信息
     */
    private String paramErrMsg;
    /**
     * method参数arguments是否验证通过
     */
    private boolean paramValid;
    /**
     * 本地调用方法得到的结果
     */
    private Object resultValue;
    /**
     * 是否需要调用远程方法
     */
    private boolean needInvokeRemote;
    /**
     * function中主动抛出的异常,这种异常一般是想阻止远程调用
     */
    private Throwable exception;

    public String getParamErrMsg() {
        return paramErrMsg;
    }

    public void setParamErrMsg(String paramErrMsg) {
        this.paramErrMsg = paramErrMsg;
    }

    public boolean isParamValid() {
        return paramValid;
    }

    public void setParamValid(boolean paramValid) {
        this.paramValid = paramValid;
    }

    public Object getResultValue() {
        return resultValue;
    }

    public void setResultValue(Object resultValue) {
        this.resultValue = resultValue;
    }

    public boolean isNeedInvokeRemote() {
        return needInvokeRemote;
    }

    public void setNeedInvokeRemote(boolean needInvokeRemote) {
        this.needInvokeRemote = needInvokeRemote;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}
