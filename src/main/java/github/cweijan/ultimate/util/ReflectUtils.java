package github.cweijan.ultimate.util;

import org.springframework.beans.BeanUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author cweijan
 * @version 2019/8/16 17:21
 */
public class ReflectUtils {

    private static HashMap<Class<?>, Field[]> fieldCache = new HashMap<>();
    private static boolean cacheField;

    /**
     * 是否对Field进行缓存,默认不缓存,生产环境建议开启
     */
    public static void enableCache(boolean cacheField) {
        ReflectUtils.cacheField = cacheField;
    }

    /**
     * 将列表转换为另一个列表
     *
     * @param objectList  对象列表
     * @param targetClass 需要转换成的class
     */
    public static <T> List<T> convertList(List<?> objectList, Class<T> targetClass) {
        return objectList.stream().map(source -> convert(source, targetClass)).collect(Collectors.toList());
    }

    /**
     * 将源对象的属性赋值到新的
     *
     * @param source      源对象
     * @param targetClass 目标class
     */
    public static <T> T convert(Object source, Class<T> targetClass) {
        if (source == null || targetClass == null) return null;
        if (source.getClass() == targetClass) return (T) source;
        if (Number.class.isAssignableFrom(targetClass) || targetClass.isPrimitive()) {
            return convertPrimitiveValue(source, targetClass);
        }
        T instance = BeanUtils.instantiateClass(targetClass);
        for (Field targetFiled : cacheField ? fieldCache.computeIfAbsent(targetClass, ReflectUtils::getFieldArray) : getFieldArray(targetClass)) {
            Object sourceValue = getFieldValue(source, targetFiled.getName());
            if (sourceValue != null) {
                if (Collection.class.isAssignableFrom(targetFiled.getType())) {
                    setFieldValue(instance, targetFiled, convertList((List<?>) sourceValue, getGenericType(targetFiled)));
                } else {
                    setFieldValue(instance, targetFiled, sourceValue);
                }
            }
        }
        return instance;
    }

    /**
     * 获取类的field,包括父类的
     */
    public static Field[] getFieldArray(Class<?> clazz) {
        if (clazz == null) return new Field[]{};
        Field[] fields = clazz.getDeclaredFields();
        clazz = clazz.getSuperclass();
        while (clazz != null && clazz != Object.class) {
            Field[] tempFields = clazz.getDeclaredFields();
            Field[] newFields = new Field[fields.length + tempFields.length];
            System.arraycopy(fields, 0, newFields, 0, fields.length);
            System.arraycopy(tempFields, 0, newFields, fields.length, tempFields.length);
            fields = newFields;
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * 获取类的field,包括父类的
     */
    public static Field[] getFieldArrayIfCache(Class<?> clazz) {
        return cacheField ? fieldCache.computeIfAbsent(clazz, ReflectUtils::getFieldArray) : getFieldArray(clazz);
    }

    /**
     * 为对象的field进行赋值
     *
     * @param instance    实例对象
     * @param fieldName   field名称
     * @param targetValue field进行的赋值
     */
    public static void setFieldValue(Object instance, String fieldName, Object targetValue) {
        if (instance == null) return;
        setFieldValue(instance, getField(instance.getClass(), fieldName), targetValue);
    }

    /**
     * 为对象的field进行赋值
     *
     * @param instance    实例对象
     * @param field       field对象
     * @param targetValue field进行的赋值
     */
    public static void setFieldValue(Object instance, Field field, Object targetValue) {
        if (instance == null || field == null) return;
        try {
            for (PropertyDescriptor descriptor : Introspector.getBeanInfo(instance.getClass()).getPropertyDescriptors()) {
                Method writeMethod = descriptor.getWriteMethod();
                if (descriptor.getName().equalsIgnoreCase(field.getName()) && writeMethod != null) {
                    if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                        writeMethod.setAccessible(true);
                    }
                    writeMethod.invoke(instance, targetValue);
                    return;
                }
            }
            field.setAccessible(true);
            field.set(instance, targetValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取实例对象的field
     *
     * @param instance  实例对象
     * @param fieldName field名称
     * @return field值
     */
    public static Object getFieldValue(Object instance, String fieldName) {
        if (instance == null) return null;
        return getFieldValue(instance, getField(instance.getClass(), fieldName));
    }

    /**
     * 获取实例对象的field
     *
     * @param instance 实例对象
     * @param field    field对象
     * @return field值
     */
    public static Object getFieldValue(Object instance, Field field) {
        if (instance == null || field == null) return null;
        try {
            for (PropertyDescriptor descriptor : Introspector.getBeanInfo(instance.getClass()).getPropertyDescriptors()) {
                Method readMethod = descriptor.getReadMethod();
                if (descriptor.getName().equalsIgnoreCase(field.getName()) && readMethod != null) {
                    if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                        readMethod.setAccessible(true);
                    }
                    return readMethod.invoke(instance);
                }
            }
            field.setAccessible(true);
            return field.get(instance);
        } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 根据fieldName获取class对象的field
     *
     * @param clazz     class对象
     * @param fieldName 要获取的fieldName
     * @return 返回已经设置为accessible的field
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        if (clazz == null || StringUtils.isEmpty(fieldName)) return null;

        Field[] fields = cacheField ? fieldCache.computeIfAbsent(clazz, ReflectUtils::getFieldArray) : getFieldArray(clazz);
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().equalsIgnoreCase(fieldName)) return field;
        }

        return null;
    }

    /**
     * 获取Field的泛型
     */
    public static Class<?> getGenericType(Field field) {
        List<Class<?>> genericTypeList = getGenericTypeArray(field);
        return (genericTypeList == null || genericTypeList.size() < 1) ? null : genericTypeList.get(0);
    }

    /**
     * 获取class对象的泛型
     */
    public static Class<?> getGenericType(Class<?> clazz) {
        List<Class<?>> genericTypeList = getGenericTypeArray(clazz);
        return (genericTypeList == null || genericTypeList.size() < 1) ? null : genericTypeList.get(0);
    }

    /**
     * 获取Field的泛型列表
     */
    public static List<Class<?>> getGenericTypeArray(Field field) {
        if (field == null) return null;
        return getGenericTypeList(field.getGenericType());
    }

    /**
     * 获取class对象的泛型列表
     */
    public static List<Class<?>> getGenericTypeArray(Class<?> clazz) {
        if (clazz == null) return null;
        return getGenericTypeList(clazz.getGenericSuperclass());
    }

    /**
     * 根据type查找泛型,没有泛型则返回空
     */
    private static List<Class<?>> getGenericTypeList(Type type) {
        if (ParameterizedType.class.isAssignableFrom(type.getClass())) {
            return Stream.of(((ParameterizedType) type).getActualTypeArguments()).map(tempType -> {
                if (Class.class.isAssignableFrom(tempType.getClass())) {
                    return (Class<?>) tempType;
                } else {
                    return tempType.getClass();
                }
            }).collect(Collectors.toList());
        }
        return null;
    }

    private static Pattern numberPattern = Pattern.compile("^[+-]?\\d+$");
    private static Pattern floatPattern = Pattern.compile("^[+-]?\\d+\\.?\\d+$");

    /**
     * 对基础类型进行转换
     *
     * @param source      原始数据
     * @param targetClass 目标类型
     * @return 转换后的类型
     */
    @SuppressWarnings({"unchecked","ConstantConditions"})
    private static <T> T convertPrimitiveValue(Object source, Class<T> targetClass) {
        if (source == null || targetClass == null) return null;
        if (source.getClass() == targetClass) return (T) source;
        String originText = String.valueOf(source);
        String name = targetClass.getName();
        if (name.equals("java.lang.String")) return (T) originText;

        boolean isNumber = numberPattern.matcher(originText).find();
        boolean isFloat = floatPattern.matcher(originText).find();
        if (!isNumber && !isFloat) return null;

        switch (name) {
            case "int":
            case "java.lang.Integer":
                if (isNumber) return (T) Integer.valueOf(originText);
                if (isFloat) return (T) Integer.valueOf(Double.valueOf(originText).intValue());
                break;
            case "long":
            case "java.lang.Long":
                if (isNumber) return (T) Long.valueOf(originText);
                if (isFloat) return (T) Long.valueOf(Double.valueOf(originText).intValue());
                break;
            case "double":
            case "java.lang.Double":
                return (T) Double.valueOf(originText);
            case "float":
            case "java.lang.Float":
                return (T) Float.valueOf(originText);
            case "byte":
            case "java.lang.Byte":
                if (isNumber) return (T) Byte.valueOf(originText);
                if (isFloat) return (T) Byte.valueOf((byte) Double.valueOf(originText).intValue());
                break;
            case "short":
            case "java.lang.Short":
                if (isNumber) return (T) Short.valueOf(originText);
                if (isFloat) return (T) Short.valueOf((short) Double.valueOf(originText).intValue());
                break;
        }

        return null;
    }

}
