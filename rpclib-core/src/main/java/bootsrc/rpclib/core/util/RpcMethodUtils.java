package bootsrc.rpclib.core.util;

import java.lang.reflect.Method;

/**
 * method utils
 *
 * @author bootsrc
 */
public class RpcMethodUtils {
    /**
     * generate id key for method variable. Refer to {@link Method#toString()}
     * performance is higher than {@link Method#toString()}
     *
     * @param method method
     * @return method identity key string
     */
    public static String toMethodKey(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getDeclaringClass().getTypeName()).append('#');
        sb.append(method.getName());
        sb.append('(');
        separateWithCommas(method.getParameterTypes(), sb);
        sb.append(')');
        return sb.toString();
    }

    private static void separateWithCommas(Class<?>[] types, StringBuilder sb) {
        for (int j = 0; j < types.length; j++) {
            sb.append(types[j].getTypeName());
            if (j < (types.length - 1)) {
                sb.append(",");
            }
        }
    }
}
