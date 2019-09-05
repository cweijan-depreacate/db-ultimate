package github.cweijan.ultimate.core.component.info;

/**
 * @author cweijan
 * @version 2019/9/5 11:26
 */
public class ForeignKeyInfo {
    private String foreignKey;
    private String joinKey;

    public ForeignKeyInfo(String foreignKey, String joinKey) {
        this.foreignKey = foreignKey;
        this.joinKey = joinKey;
    }

    public String getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }

    public String getJoinKey() {
        return joinKey;
    }

    public void setJoinKey(String joinKey) {
        this.joinKey = joinKey;
    }
}
