package github.cweijan.ultimate.util;

import org.springframework.beans.BeanUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author cweijan
 * @version 2019/8/16 17:21
 */
public class ReflectUtils {


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
        T instance = BeanUtils.instantiateClass(targetClass);
        for (Field targetFiled : getFieldArray(targetClass)) {
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
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                fields.add(field);
            }
            clazz = clazz.getSuperclass(); //得到父类,然后赋给自己
        }

        return fields.toArray(new Field[]{});
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
        } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
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

        for (Field declaredField : clazz.getDeclaredFields()) {
            declaredField.setAccessible(true);
            if (declaredField.getName().equalsIgnoreCase(fieldName)) return declaredField;
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

}
