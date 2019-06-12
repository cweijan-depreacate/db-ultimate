package github.cweijan.ultimate.convert;

import github.cweijan.ultimate.util.Log;

class EnumConvert {

    static Enum valueOfEnum(Class clazz, String name){
        try {
            return Enum.valueOf(clazz,name);
        } catch (Exception e) {
            Log.error(e.getMessage());
            return null;
        }
    }

}
