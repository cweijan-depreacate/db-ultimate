package github.cweijan.ultimate.convert;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TypeAdapter{

    private static final List<String> SIMPLE_TYPE = Arrays.asList("chat", "short", "int", "float", "double", "long", String.class.getName(), Integer.class.getName(), Character.class.getName(), Short.class.getName(), Integer.class.getName(), Float.class.getName(), Double.class.getName(), Long.class.getName(), Date.class.getName());

    private static final List<String> CHARACTER_TYPE = Arrays.asList("chat", String.class.getName(), Character.class.getName());

    public static boolean isCharacterType(String typeName){

        return CHARACTER_TYPE.contains(typeName);
    }

    public static boolean isDateType(String typeName){

        return Date.class.getName().equals(typeName);
    }

    public static boolean isSimpleType(String typeName){

        return SIMPLE_TYPE.contains(typeName);
    }

    public static boolean checkNumericType(Class<?> type){

        return false;
    }
}
