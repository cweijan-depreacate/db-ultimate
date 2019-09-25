package github.cweijan.ultimate.util;

import github.cweijan.ultimate.core.FieldQuery;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cweijan
 * @version 2019/9/25 14:39
 */
public class LambdaUtils {

    private static Map<Class, SerializedLambda> CLASS_LAMDBA_CACHE = new ConcurrentHashMap<>();

    public static String getFieldName(FieldQuery<?> fieldQuery) {
        SerializedLambda serializedLambda = getSerializedLambda(fieldQuery);
        return resolveFieldName(serializedLambda.getImplMethodName());
    }

    private static SerializedLambda getSerializedLambda(Serializable fn) {
        SerializedLambda lambda = CLASS_LAMDBA_CACHE.get(fn.getClass());
        if (lambda == null) {
            try {
                Method method = fn.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(Boolean.TRUE);
                lambda = (SerializedLambda) method.invoke(fn);
                CLASS_LAMDBA_CACHE.put(fn.getClass(), lambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lambda;
    }

    private static String resolveFieldName(String getMethodName) {
        if (getMethodName.startsWith("get")) {
            getMethodName = getMethodName.substring(3);
        } else if (getMethodName.startsWith("is")) {
            getMethodName = getMethodName.substring(2);
        } else {
            return null;
        }
        return getMethodName.substring(0, 1).toLowerCase() + getMethodName.substring(1);
    }

}
