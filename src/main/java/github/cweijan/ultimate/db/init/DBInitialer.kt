package github.cweijan.ultimate.db.init

import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.core.Query
import github.cweijan.ultimate.core.component.TableInfo
import github.cweijan.ultimate.core.component.info.ComponentInfo
import github.cweijan.ultimate.core.extra.ExtraData
import github.cweijan.ultimate.core.generator.GeneratorAdapter
import github.cweijan.ultimate.db.SqlExecutor
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.db.init.generator.TableInitSqlGenerator
import github.cweijan.ultimate.db.init.generator.struct.TableStruct
import github.cweijan.ultimate.util.Log
import github.cweijan.ultimate.util.StringUtils
import java.sql.Connection
import java.sql.SQLException

/**
 * 用于创建实体对应的不存在的数据表
 */
class DBInitialer(private val dbConfig: DbConfig) {

    private val sqlExecutor: SqlExecutor = SqlExecutor(dbConfig)
    private var connection: Connection = dbConfig.getConnection()
    private var initSqlGenetator: TableInitSqlGenerator = GeneratorAdapter.getInitGenerator(dbConfig.driver)

    /**
     * 创建Bean所对应的表
     */
    fun initalerTable() {

        val extraData = ComponentInfo.init(ExtraData::class.java)
        createTable(extraData)
        var tableStruct = ComponentInfo.init(TableStruct::class.java)
        println(tableStruct)
        TableInfo.componentList.stream().forEach { componentInfo ->
            updateTable(componentInfo)
        }
        if (dbConfig.createNonexistsTable) {
            TableInfo.componentList.stream().forEach { componentInfo ->
                createTable(componentInfo)
            }
        } else {
            TableInfo.componentList.stream().forEach { componentInfo ->
                //                updateTable(componentInfo)
            }
        }

    }


    fun createTable(componentInfo: ComponentInfo?) {

        if(componentInfo?.componentClass==TableStruct::class.java)return

        if (connection.isClosed) connection = dbConfig.getConnection()

        if (componentInfo == null || tableExists(componentInfo.tableName)) return

        if (componentInfo.nonExistsColumn()) {
            Log.debug("${componentInfo.componentClass.name} dont have any columns, skip create table ")
            return
        }

        var sql = "create table ${componentInfo.tableName}("
        val uniques = ArrayList<String>()

        TypeAdapter.getAllField(componentInfo.componentClass).let { fields ->
            fields.forEachIndexed { index, field ->
                if (componentInfo.isTableExcludeField(field) || !TypeAdapter.isAdapterType(field.type)) {
                    return@forEachIndexed
                }
                field.isAccessible = true
                val columnInfo = componentInfo.getColumnInfoByFieldName(field.name)!!
                //生成column
                var columnDefination = "${columnInfo.columnName} ${initSqlGenetator.getColumnTypeByField(field, columnInfo.length)}"
                //生成主键或者非空片段
                columnDefination += when {
                    componentInfo.primaryKey == field.name -> " PRIMARY KEY "
                    columnInfo.nullable -> ""
                    else -> " NOT NULL "
                }
                if (field.name == componentInfo.primaryKey && columnInfo.autoIncrement) {
                    columnDefination += initSqlGenetator.generateAutoIncrementSqlFragment()
                }
                //生成默认值片段
                columnDefination += when {
                    columnInfo.nullable -> ""
                    field.name == componentInfo.primaryKey -> ""
                    else -> initSqlGenetator.generateDefaultSqlFragment(
                            if (StringUtils.isNotEmpty(columnInfo.defaultValue)) {
                                if (TypeAdapter.CHARACTER_TYPE.contains(field.type.name)) {
                                    TypeAdapter.contentWrapper(columnInfo.defaultValue)
                                } else {
                                    columnInfo.defaultValue
                                }
                            } else {
                                TypeAdapter.getDefaultValue(field.type.name)
                            }
                    )
                }
                //生成注释片段
                columnInfo.comment?.let {
                    columnDefination += initSqlGenetator.generateCommentSqlFragment(it)
                }

                //生成唯一索引补丁
                if (columnInfo.unique) {
                    uniques.add(initSqlGenetator.generateUniqueSqlFragment(componentInfo.tableName, columnInfo.columnName, columnDefination)!!)
                }

                //拼接sql
                sql += "$columnDefination,"
            }
        }

        if (sql.lastIndexOf(",") != -1) {
            sql = sql.substring(0, sql.lastIndexOf(","))
        }
        uniques.forEach { uniqueSql -> sql += ",$uniqueSql" }
        sql += " );"

        try {
            sqlExecutor.executeSql(sql)
        } catch (e: Exception) {
            Log.error("Create table ${componentInfo.tableName} error!", e)
            return
        }
        Log.info("Auto create component table ${componentInfo.tableName}")
    }

    /**
     * 检测表是否存在
     *
     * @param tableName 表名
     */
    fun tableExists(tableName: String): Boolean {

        try {
            return connection.metaData.getTables(null, null, tableName, null).next()
        } catch (e: SQLException) {
            Log.error(e.message, e)
        }

        return true
    }

    /**
     * 更新表结构
     */
    private fun updateTable(componentInfo: ComponentInfo?) {

        if(componentInfo?.componentClass==TableStruct::class.java)return

        if (connection.isClosed) connection = dbConfig.getConnection()

        if (componentInfo == null) return

        if (componentInfo.nonExistsColumn()) {
            Log.debug("${componentInfo.componentClass.name} dont have any columns, skip create table ")
            return
        }

        val updateSqlList = HashSet<String>();
        val structList = Query.of(TableStruct::class.java).eq("tableScheme", connection.schema).eq("tableName", componentInfo.tableName).list()

        TypeAdapter.getAllField(componentInfo.componentClass).let { fields ->

            fields.forEachIndexed { _, field ->

                if (componentInfo.isTableExcludeField(field) || !TypeAdapter.isAdapterType(field.type)) {
                    return@forEachIndexed
                }

                field.isAccessible = true

                val columnInfo = componentInfo.getColumnInfoByFieldName(field.name)!!
                //生成column
                val columnType = initSqlGenetator.getColumnTypeByField(field, columnInfo.length)
                var columnDefination = "${columnInfo.columnName} ${initSqlGenetator.getColumnTypeByField(field, columnInfo.length)}"

                //生成默认值片段
                columnDefination += when {
                    columnInfo.nullable -> ""
                    field.name == componentInfo.primaryKey -> ""
                    else -> initSqlGenetator.generateDefaultSqlFragment(
                            if (StringUtils.isNotEmpty(columnInfo.defaultValue)) {
                                if (TypeAdapter.CHARACTER_TYPE.contains(field.type.name)) {
                                    TypeAdapter.contentWrapper(columnInfo.defaultValue)
                                } else {
                                    columnInfo.defaultValue
                                }
                            } else {
                                TypeAdapter.getDefaultValue(field.type.name)
                            }
                    )
                }
                //生成注释片段
                columnInfo.comment?.let {
                    columnDefination += initSqlGenetator.generateCommentSqlFragment(it)
                }

                //如果列不存在,新增
                if (notExistsColumn(structList, columnInfo.columnName)) {
                    updateSqlList.add("ALTER TABLE ${componentInfo.tableName} ADD COLUMN $columnDefination;")
                    return@forEachIndexed
                }

                structList.forEach { struct ->
                    val tempColumnInfo = componentInfo.getColumnInfoByColumnName(struct.columnName)
                    if (tempColumnInfo == null) {
                        updateSqlList.add("ALTER TABLE ${componentInfo.tableName} DROP COLUMN ${struct.columnName};")
                        return@forEachIndexed
                    }
                    if ((struct.characterMaximumLength != null && tempColumnInfo.length != null && struct.characterMaximumLength.toInt() != tempColumnInfo.length) ||
                            (columnInfo.columnName == struct.columnName && !columnType.toLowerCase().contains(struct.dataType.toLowerCase()))
                    ) {
                        updateSqlList.add("ALTER TABLE ${componentInfo.tableName} MODIFY $columnDefination;")
                        return@forEachIndexed
                    }
                }

            }
        }

        try {
            updateSqlList.forEach{sqlExecutor.executeSql(it)}
        } catch (e: Exception) {
            Log.error("Update table ${componentInfo.tableName} error!", e)
            return
        }
        Log.info("Update component table ${componentInfo.tableName}")

    }

    private fun notExistsColumn(tableStructList: List<TableStruct>?, columnName: String): Boolean {

        if (tableStructList == null) return true

        tableStructList.forEach { tableStruct ->
            if (tableStruct.columnName == columnName) return false
        }

        return true
    }

}
