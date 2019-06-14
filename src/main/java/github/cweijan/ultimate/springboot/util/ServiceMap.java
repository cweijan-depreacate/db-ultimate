package github.cweijan.ultimate.springboot.util;

import java.util.HashMap;
import java.util.Map;

public class ServiceMap {

    private static Map<String, ServiceInject> serviceInjectMap=new HashMap<>();

    static void mapService(String className,ServiceInject serviceInject){
        serviceInjectMap.put(className,serviceInject);
    }
    public static <T> ServiceInject<T> get(Class<T> componentClass){
        ServiceInject serviceInject = serviceInjectMap.get(componentClass.getName());
        if(serviceInject==null){
            throw new NullPointerException("Can't not suitable component service!");
        }
        return serviceInject;
    }

}
