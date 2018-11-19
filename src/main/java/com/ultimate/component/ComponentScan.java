package com.ultimate.component;

import com.ultimate.annotation.Table;
import com.ultimate.component.info.ComponentInfo;
import com.ultimate.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 扫描实体类
 */
public class ComponentScan{

    public void scan(String... scanPackages){

        for(String scanPackage : scanPackages){
            Class[] classes = scanTableClasses(scanPackage);
        }
    }

    private boolean IsComponent(Class clazz){

        Table table = (Table) clazz.getAnnotation(Table.class);

        return null != table;
    }

    /**
     Scans all classes accessible from the context class loader which belong to the given package and subpackages.

     @param packageName The base package
     @return The classes
     */
    public Class[] scanTableClasses(String packageName){

        Log.getLogger().debug("scan component classes for package " + packageName);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources;
        List<File> dirs = new ArrayList<>();
        try{
            resources = classLoader.getResources(path);
            while(resources.hasMoreElements()){
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }
        } catch(IOException e){
            Log.getLogger().error(e.getMessage(), e);
        }

        ArrayList<Class> classes = new ArrayList<>();
        for(File directory : dirs){
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[0]);
    }

    /**
     Recursive method used to find all classes in a given directory and sub dirs.

     @param directory   The base directory
     @param packageName The package name for classes found inside the base directory
     @return The classes
     */
    private List<Class> findClasses(File directory, String packageName){

        List<Class> classes = new ArrayList<>();
        File[] files = directory.listFiles();
        if(files == null){
            return classes;
        }
        for(File file : files){
            if(file.isDirectory()){
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if(file.getName().endsWith(".class")){
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz;
                try{
                    clazz = Class.forName(className);
                } catch(ClassNotFoundException e){
                    Log.getLogger().error("fail load " + className + "!");
                    continue;
                }
                if(IsComponent(clazz)){
                    classes.add(clazz);
                    ComponentInfo.init(clazz);
                }
            }
        }

        return classes;
    }

}
