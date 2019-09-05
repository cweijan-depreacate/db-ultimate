package github.cweijan.ultimate.core.component.info;

import java.lang.reflect.Field;

/**
 * @author cweijan
 * @version 2019/9/5 11:25
 */
public class OneToOneInfo {
    private Field oneToOneField;
    private String relationColumn;
    private Class<?> relationClass;

    public OneToOneInfo(Field oneToOneField, String relationColumn, Class<?> relationClass) {
        this.oneToOneField = oneToOneField;
        this.relationColumn = relationColumn;
        this.relationClass = relationClass;
    }

    public Field getOneToOneField() {
        return oneToOneField;
    }

    public void setOneToOneField(Field oneToOneField) {
        this.oneToOneField = oneToOneField;
    }

    public String getRelationColumn() {
        return relationColumn;
    }

    public void setRelationColumn(String relationColumn) {
        this.relationColumn = relationColumn;
    }

    public Class<?> getRelationClass() {
        return relationClass;
    }

    public void setRelationClass(Class<?> relationClass) {
        this.relationClass = relationClass;
    }
}
