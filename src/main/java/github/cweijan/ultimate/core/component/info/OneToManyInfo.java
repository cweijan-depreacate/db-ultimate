package github.cweijan.ultimate.core.component.info;

import java.lang.reflect.Field;

/**
 * @author cweijan
 * @version 2019/9/5 11:21
 */
public class OneToManyInfo {

    private Field oneTomanyField;
    
    private String relationColumn;
    
    private String where;
    
    private Class<?> relationClass;

    public OneToManyInfo(Field oneTomanyField, String relationColumn, String where, Class<?> relationClass) {
        this.oneTomanyField = oneTomanyField;
        this.relationColumn = relationColumn;
        this.where = where;
        this.relationClass = relationClass;
    }

    public Field getOneTomanyField() {
        return oneTomanyField;
    }

    public void setOneTomanyField(Field oneTomanyField) {
        this.oneTomanyField = oneTomanyField;
    }

    public String getRelationColumn() {
        return relationColumn;
    }

    public void setRelationColumn(String relationColumn) {
        this.relationColumn = relationColumn;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public Class<?> getRelationClass() {
        return relationClass;
    }

    public void setRelationClass(Class<?> relationClass) {
        this.relationClass = relationClass;
    }
}
