package github.cweijan.ultimate.db.init.generator.impl.mysql;

import github.cweijan.ultimate.annotation.Table;
import github.cweijan.ultimate.core.component.info.ColumnInfo;
import github.cweijan.ultimate.db.init.generator.TableStruct;

import java.math.BigInteger;
import java.util.List;

/**
 * @author cwj
 * @version  2019/6/26/026 10:37
 */
@Table("information_schema.columns")
public class MysqlTableStruct implements TableStruct {

    private String tableSchema;
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

    @Override
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

    public String getTableSchema() {
        return tableSchema;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public boolean columnIsChanged(ColumnInfo columnInfo, String columnType) {

        if(!columnType.toLowerCase().contains(dataType.toLowerCase()))return true;
        if(columnInfo.getNullable() != isNullable.toUpperCase().equals("YES"))return true;

        return (characterMaximumLength != null && columnInfo.getLength() != null
                && characterMaximumLength.intValue() != columnInfo.getLength());
    }

}
