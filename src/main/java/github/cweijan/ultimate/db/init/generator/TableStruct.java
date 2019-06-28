package github.cweijan.ultimate.db.init.generator;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.core.component.info.ColumnInfo;
import github.cweijan.ultimate.db.DatabaseType;
import github.cweijan.ultimate.db.init.generator.impl.mysql.MysqlTableStruct;

import java.util.List;

/**
 * @author cweijan
 * @version 2019/6/28/028 10:15
 */
public interface TableStruct {

    default List<? extends TableStruct> getTableStruct(DatabaseType databaseType,String schemeName, String tableName) {

        switch (databaseType) {
            case mysql:
                return Query.of(MysqlTableStruct.class).eq("tableScheme", schemeName).eq("tableName", tableName).list();
        }

        return null;
    }

    /**
     * 判断实体的类型和长度是否和数据库的不一致
     *
     * @param columnInfo 实体列
     * @param columnType 实体列数据库类型
     * @return 是否已改变
     */
    boolean columnIsChanged(ColumnInfo columnInfo, String columnType);

    boolean columnNotExists(List<TableStruct> tableStructList, String columnName);

    String getColumnName();

}
