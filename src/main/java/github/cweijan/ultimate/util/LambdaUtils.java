package github.cweijan.ultimate.util;

import github.cweijan.ultimate.core.FieldQuery;
import org.jetbrains.annotations.Nullable;

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

    private static Map<Class<?>, SerializedLambda> CLASS_LAMDBA_CACHE = new ConcurrentHashMap<>();

    public static String getFieldName(FieldQuery<?> fieldQuery) {
        return resolveFieldName(getSerializedLambda(fieldQuery).getImplMethodName());
    }

    private static SerializedLambda getSerializedLambda(Serializable fn) {
        return CLASS_LAMDBA_CACHE.computeIfAbsent(fn.getClass(), fnClass -> {
            try {
                for (Method method : fnClass.getDeclaredMethods()) {
                    if (method.getName().equals("writeReplace")) {
                        method.setAccessible(true);
                        return (SerializedLambda) method.invoke(fn);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
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
