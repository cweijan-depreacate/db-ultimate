package com.ultimate.component;

import com.ultimate.component.info.ComponentInfo;
import com.ultimate.exception.ComponentNotExistsException;

import java.util.*;

public class TableInfo{

    private static Map<String, ComponentInfo> TypeMap;
    private static List<ComponentInfo> componentList;

    public static void putComponent(Class clazz, ComponentInfo componentInfo){

        Objects.requireNonNull(componentInfo, componentInfo.getTableName());
        getTypeMapInstance().put(clazz.getName(), componentInfo);
        getTableNameListInstance().add(componentInfo);

    }

    public static ComponentInfo getComponent(Class clazz){

        if(clazz == null){
            throw new NullPointerException("param clazz must not null!");
        }

        ComponentInfo componentInfo = getTypeMapInstance().get(clazz.getName());
        if(componentInfo==null){
            throw new ComponentNotExistsException(clazz+" component is not exists!");
        }

        return componentInfo;
    }

    public static String getTableName(Class clazz){

        if(clazz == null){
            throw new NullPointerException("param calzz must not null!");
        }

        ComponentInfo componentInfo = getTypeMapInstance().get(clazz.getName());
        if(componentInfo != null){
            return componentInfo.getTableName();
        }

        return null;
    }

    public static List<ComponentInfo> getComponentList(){

        return componentList;
    }

    private static Map<String, ComponentInfo> getTypeMapInstance(){

        if(TypeMap == null){
            TypeMap = new HashMap<>();
        }

        return TypeMap;
    }

    private static List<ComponentInfo> getTableNameListInstance(){

        if(componentList == null){
            componentList = new ArrayList<>();
        }

        return componentList;
    }

}
