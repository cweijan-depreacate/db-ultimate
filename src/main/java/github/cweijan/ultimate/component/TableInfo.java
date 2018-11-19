package github.cweijan.ultimate.component;

import github.cweijan.ultimate.component.info.ComponentInfo;
import github.cweijan.ultimate.exception.ComponentNotExistsException;

import java.util.*;

public class TableInfo{

    private static Map<String, ComponentInfo> TypeMap;
    private static List<ComponentInfo> componentList;

    public static void putComponent(Class clazz, ComponentInfo componentInfo){

        Objects.requireNonNull(componentInfo, componentInfo.getTableName());
        getTypeMapInstance().put(clazz.getName(), componentInfo);
        getTableNameListInstance().add(componentInfo);

    }

    public static boolean isAlreadyInit(Class clazz){

        return getTypeMapInstance().containsKey(clazz.getName());
    }

    public static ComponentInfo getComponent(Class clazz){

        ComponentInfo componentInfo = getTypeMapInstance().get(clazz.getName());
        if(componentInfo == null){
            throw new ComponentNotExistsException(clazz + " component is not exists!");
        }

        return componentInfo;
    }

    public static String getTableName(Class clazz){

        if(clazz == null){
            throw new NullPointerException("param calzz must not null!");
        }

        ComponentInfo componentInfo = getTypeMapInstance().get(clazz.getName());

        return Optional.ofNullable(componentInfo.getTableName()).orElse(null);
    }

    public static List<ComponentInfo> getComponentList(){

        return componentList;
    }

    private static Map<String, ComponentInfo> getTypeMapInstance(){

        return TypeMap = Optional.ofNullable(TypeMap).orElse(new HashMap<>());
    }

    private static List<ComponentInfo> getTableNameListInstance(){

        return componentList = Optional.ofNullable(componentList).orElse(new ArrayList<>());
    }

}
