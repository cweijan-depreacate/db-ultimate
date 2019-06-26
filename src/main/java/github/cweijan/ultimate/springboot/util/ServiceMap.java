package github.cweijan.ultimate.springboot.util;

import github.cweijan.ultimate.core.component.ComponentScan;

import java.util.HashMap;
import java.util.Map;

public class ServiceMap {

    private static Map<String, ServiceInject> serviceInjectMap=new HashMap<>();

    static void mapService(String className,ServiceInject serviceInject){
        serviceInjectMap.put(className,serviceInject);
    }
    public static <T> ServiceInject<T> get(Class<T> componentClass){
        ServiceInject serviceInject = serviceInjectMap.get(componentClass.getName());
        if(serviceInject==null && ComponentScan.isComponent(componentClass)){
            return new ServiceInject(componentClass) {};
        }
        return serviceInject;
    }

}
