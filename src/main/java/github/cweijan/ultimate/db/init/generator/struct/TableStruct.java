package github.cweijan.ultimate.db.init.generator.struct;

import github.cweijan.ultimate.annotation.Table;

import java.math.BigInteger;

/**
 * @author cwj
 * @version  2019/6/26/026 10:37
 */
@Table("information_schema.columns")
public class TableStruct {

    private String tableScheme;
    private String tableName;
    private  String columnName;
    private String isNullable;
    private String dataType;
    private BigInteger characterMaximumLength;
    private String columnComment;

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }

    public BigInteger getCharacterMaximumLength() {
        return characterMaximumLength;
    }

    public void setCharacterMaximumLength(BigInteger characterMaximumLength) {
        this.characterMaximumLength = characterMaximumLength;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getIsNullable() {
        return isNullable;
    }

    public void setIsNullable(String isNullable) {
        this.isNullable = isNullable;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getTableScheme() {
        return tableScheme;
    }

    public void setTableScheme(String tableScheme) {
        this.tableScheme = tableScheme;
    }
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
