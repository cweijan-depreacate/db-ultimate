package github.cweijan.ultimate.convert;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TypeAdapter{

    private static final List<String> SIMPLE_TYPE = Arrays.asList("chat", "short", "int", "float", "double", "long", String.class.getName(), Integer.class.getName(), Character.class.getName(), Short.class.getName(), Integer.class.getName(), Float.class.getName(), Double.class.getName(), Long.class.getName(), Date.class.getName());

    public static boolean isSimpleType(String typeName){

        return SIMPLE_TYPE.contains(typeName);
    }

    public static boolean checkNumericType(Class<?> type){

        return false;
    }
}
