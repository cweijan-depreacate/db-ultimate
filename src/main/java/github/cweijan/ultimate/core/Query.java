package github.cweijan.ultimate.core;

import github.cweijan.ultimate.core.component.TableInfo;
import github.cweijan.ultimate.core.component.info.ComponentInfo;
import github.cweijan.ultimate.core.excel.ExcelOperator;
import github.cweijan.ultimate.core.page.Pagination;
import github.cweijan.ultimate.util.Json;
import github.cweijan.ultimate.util.LambdaUtils;
import github.cweijan.ultimate.util.Log;
import github.cweijan.ultimate.util.StringUtils;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static github.cweijan.ultimate.core.query.QueryType.*;

/**
 * @author cweijan
 * @version 2019/9/2 17:45
 */
public class Query<T> {
    private ComponentInfo component;
    private QueryCondition queryCondition;
    private Class<T> componentClass;
    /**
     * 底层Api对象
     */
    public static DbUltimate db;

    public ComponentInfo getComponent() {
        return this.component;
    }

    public QueryCondition getQueryCondition() {
        return queryCondition;
    }

    /**
     * 执行统计
     *
     * @return 统计列表, 每一个列表是一个分组, 每个分组包含各种调用的统计api
     */
    public List<Map> statistic() {
        if (db == null) Intrinsics.throwUninitializedPropertyAccessException("db");
        return db.findBySql(db.getSqlGenerator().generateSelectSql(this), this.queryCondition.consumeParams(), Map.class);
    }


    /**
     * 统计指定列的总数
     *
     * @param column          指定列
     * @param countColumnName 统计列的别名
     * @return this query
     * @see Query#statistic()
     */
    public Query<T> countDistinct(String column, String countColumnName) {
        this.queryCondition.countDistinct(column, countColumnName);
        return this;
    }

    /**
     * 统计指定列的总数
     *
     * @param column 指定列
     * @return this query
     * @see Query#statistic()
     */
    public Query<T> countDistinct(String column) {
        return countDistinct(column, null);
    }


    /**
     * 统计,对指定进行求和
     *
     * @param column        指定列
     * @param sumColumnName 求和列的别名
     * @return this query
     * @see Query#statistic()
     */
    public Query<T> sum(String column, String sumColumnName) {
        this.queryCondition.sum(column, sumColumnName);
        return this;
    }


    /**
     * 统计,对指定进行求和
     *
     * @param column 指定列
     * @return this query
     * @see Query#statistic()
     */
    public Query<T> sum(String column) {
        return sum(column, null);
    }


    /**
     * 统计指定列的平均值
     *
     * @param column        指定列
     * @param avgColumnName 平均列的别名
     * @return this query
     * @see Query#statistic()
     */
    public Query<T> avg(String column, String avgColumnName) {
        this.queryCondition.avg(column, avgColumnName);
        return this;
    }


    /**
     * 统计指定列的平均值
     *
     * @param column 指定列
     * @return this query
     * @see Query#statistic()
     */
    public Query<T> avg(String column) {
        return avg(column, null);
    }


    /**
     * 统计指定列的最小值
     *
     * @param column        指定列
     * @param minColumnName 最小列的别名
     * @return this query
     * @see Query#statistic()
     */
    public Query<T> min(String column, String minColumnName) {
        this.queryCondition.min(column, minColumnName);
        return this;
    }


    /**
     * 统计指定列的最小值
     *
     * @param column 指定列
     * @return this query
     * @see Query#statistic()
     */
    public Query<T> min(String column) {
        return min(column, null);
    }


    /**
     * 统计,查询指定列的最大值
     *
     * @param column        指定列
     * @param maxColumnName 最大值列的别名
     * @return this query
     * @see Query#statistic()
     */
    public Query<T> max(String column, String maxColumnName) {
        this.queryCondition.max(column, maxColumnName);
        return this;
    }


    /**
     * 统计,查询指定列的最大值
     *
     * @param column 指定列
     * @return this query
     * @see Query#statistic()
     */
    public Query<T> max(String column) {
        return max(column, null);
    }


    /**
     * 连表查询
     *
     * @param tableName 要连接的表
     * @param on        连接条件
     * @return this query
     */
    public Query<T> join(String tableName, String on) {
        Intrinsics.checkParameterIsNotNull(tableName, "tableName");
        String segment = " join " + tableName + ' ';
        if (StringUtils.isNotEmpty(on)) {
            segment = segment + "on " + on + ' ';
        }
        this.queryCondition.getJoinTables().add(segment);
        return this;
    }


    /**
     * 直接拼接where语句
     *
     * @param whereSql 条件语句
     * @return this query
     */
    public Query<T> where(String whereSql) {
        this.queryCondition.setWhereSql(whereSql);
        return this;
    }


    /**
     * 根据指定列进行分组
     *
     * @param column 指定列
     * @return this query
     */
    public Query<T> groupBy(String column) {
        this.queryCondition.groupBy(column);
        return this;
    }


    /**
     * 统计接口增加显示Column
     *
     * @param column 要增加显示的column
     * @return this query
     */
    public Query<T> addShowColumn(String column) {
        this.queryCondition.addShowColumn(column);
        return this;
    }


    /**
     * 增加having查询
     *
     * @param havingSql having语句片段
     * @return this query
     */
    public Query<T> having(String havingSql) {
        this.queryCondition.having(havingSql);
        return this;
    }


    /**
     * 对指定列进行更新,需要调用{@link Query#executeUpdate()}执行更新
     *
     * @param column 要更新的列
     * @param value  更新后的值
     * @return this query
     */
    public Query<T> update(String column, Object value) {
        this.queryCondition.update(column, value);
        return this;
    }

    /**
     * 对指定列进行更新,需要调用{@link Query#executeUpdate()}执行更新
     *
     * @param fieldQuery 指定列,example:Student::getName
     * @param value      更新后的值
     * @return this query
     */
    public Query<T> update(FieldQuery<T> fieldQuery, Object value) {
        this.queryCondition.update(LambdaUtils.getFieldName(fieldQuery), value);
        return this;
    }


    /**
     * !=查询
     *
     * @return this query
     */
    public Query<T> notEq(String column, Object value) {
        this.queryCondition.addAndCondition(column, not_equlas, value);
        return this;
    }

    /**
     * !=查询
     *
     * @return this query
     */
    public Query<T> notEq(FieldQuery<T> fieldQuery, Object value) {
        this.queryCondition.addAndCondition(LambdaUtils.getFieldName(fieldQuery), not_equlas, value);
        return this;
    }

    /**
     * or !=查询
     *
     * @return this query
     */
    public Query<T> orNotEq(String column, Object value) {
        this.queryCondition.addOrCondition(column, not_equlas, value);
        return this;
    }

    /**
     * or !=查询
     *
     * @param fieldQuery 指定列,example:Student::getName
     * @return this query
     */
    public Query<T> orNotEq(FieldQuery<T> fieldQuery, Object value) {
        this.queryCondition.addOrCondition(LambdaUtils.getFieldName(fieldQuery), not_equlas, value);
        return this;
    }


    /**
     * like查询
     *
     * @return this query
     */
    public Query<T> like(String column, Object content) {
        this.queryCondition.addAndCondition(column, like, content);
        return this;
    }

    /**
     * like查询
     *
     * @param fieldQuery 指定列,example:Student::getName
     * @return this query
     */
    public Query<T> like(FieldQuery<T> fieldQuery, Object content) {
        this.queryCondition.addAndCondition(LambdaUtils.getFieldName(fieldQuery), like, content);
        return this;
    }


    /**
     * like查询
     *
     * @return this query
     */
    public Query<T> search(String column, Object content) {
        this.queryCondition.addAndCondition(column, like, content);
        return this;
    }


    /**
     * like查询
     *
     * @param fieldQuery 指定列,example:Student::getName
     * @return this query
     */
    public Query<T> search(FieldQuery<T> fieldQuery, Object content) {
        this.queryCondition.addAndCondition(LambdaUtils.getFieldName(fieldQuery), like, content);
        return this;
    }

    /**
     * great equals then
     *
     * @return this query
     */
    public Query<T> ge(String column, Object value) {
        this.queryCondition.addAndCondition(column, great_equlas, value);
        return this;
    }


    /**
     * less equals then, sql column &lt; = relationClass
     *
     * @return this query
     */
    public Query<T> le(String column, Object value) {
        this.queryCondition.addAndCondition(column, less_equals, value);
        return this;
    }


    /**
     * =查询
     *
     * @param fieldQuery 指定列,example:Student::getName
     * @param value      列值
     * @return this query
     */
    public Query<T> eq(FieldQuery<T> fieldQuery, Object value) {
        this.queryCondition.addAndCondition(LambdaUtils.getFieldName(fieldQuery), equals, value);
        return this;
    }

    /**
     * =查询
     *
     * @param column 指定列,驼峰命名会自动转为下划线
     * @param value  列值
     * @return this query
     */
    public Query<T> eq(String column, Object value) {
        this.queryCondition.addAndCondition(column, equals, value);
        return this;
    }


    /**
     * in查询
     *
     * @return this query
     */
    public Query<T> in(String column, List<?> value) {
        this.queryCondition.in(column, value);
        return this;
    }

    /**
     * in查询
     *
     * @param fieldQuery 指定列,example:Student::getName
     * @return this query
     */
    public Query<T> in(FieldQuery<T> fieldQuery, List<?> value) {
        this.queryCondition.in(LambdaUtils.getFieldName(fieldQuery), value);
        return this;
    }

    /**
     * in查询
     *
     * @return this query
     */
    public Query<T> in(String column, Object[] value) {
        this.queryCondition.in(column, Arrays.asList(value));
        return this;
    }

    /**
     * in查询
     *
     * @param fieldQuery 指定列,example:Student::getName
     * @return this query
     */
    public Query<T> in(FieldQuery<T> fieldQuery, Object[] value) {
        this.queryCondition.in(LambdaUtils.getFieldName(fieldQuery), Arrays.asList(value));
        return this;
    }

    /**
     * 生成查询: or colum=value
     *
     * @param fieldQuery 指定列,example:Student::getName
     * @param value      列值
     * @return this query
     */
    public Query<T> orEq(FieldQuery<T> fieldQuery, Object value) {
        this.queryCondition.addOrCondition(LambdaUtils.getFieldName(fieldQuery), equals, value);
        return this;
    }

    /**
     * 生成查询: or colum=value
     *
     * @param column 指定列
     * @param value  列值
     * @return this query
     */
    public Query<T> orEq(String column, Object value) {
        this.queryCondition.addOrCondition(column, equals, value);
        return this;
    }

    /**
     * 根据当前query对象去获取数据总量
     *
     * @return 数据总量
     */
    public int count() {
        if (db == null) Intrinsics.throwUninitializedPropertyAccessException("db");
        return db.getCount(this);
    }


    /**
     * 设置分页每页大小
     *
     * @return this query
     */
    public Query<T> pageSize(Integer pageSize) {
        this.queryCondition.setPageSize(pageSize);
        return this;
    }

    /**
     * 设置分页每页大小
     *
     * @return this query
     */
    public Query<T> limit(Integer limit) {
        this.queryCondition.setPageSize(limit);
        return this;
    }


    /**
     * 设置页码
     *
     * @return this query
     */
    public Query<T> page(Integer page) {
        this.queryCondition.setPage(page);
        return this;
    }


    /**
     * 列为空查询，该查询直接拼接sql，需要防止sql注入
     *
     * @return this query
     */
    public Query<T> isNull(String column) {
        this.queryCondition.addAndCondition(column, isNull, "");
        return this;
    }

    /**
     * 列为空查询，该查询直接拼接sql，需要防止sql注入
     *
     * @param fieldQuery 指定列,example:Student::getName
     * @return this query
     */
    public Query<T> isNull(FieldQuery<T> fieldQuery) {
        this.queryCondition.addAndCondition(LambdaUtils.getFieldName(fieldQuery), isNull, "");
        return this;
    }


    /**
     * 列不为空查询，该查询直接拼接sql，需要防止sql注入
     *
     * @return this query
     */
    public Query<T> isNotNull(String column) {
        this.queryCondition.addAndCondition(column, isNotNull, "");
        return this;
    }

    /**
     * 列不为空查询，该查询直接拼接sql，需要防止sql注入
     *
     * @param fieldQuery 指定列,example:Student::getName
     * @return this query
     */
    public Query<T> isNotNull(FieldQuery<T> fieldQuery) {
        this.queryCondition.addAndCondition(LambdaUtils.getFieldName(fieldQuery), isNotNull, "");
        return this;
    }


    /**
     * 根据指定列进行排序
     *
     * @param column 指定列
     * @return this query
     */
    public Query<T> orderBy(String column) {
        this.queryCondition.orderBy(column);
        return this;
    }

    /**
     * 根据指定列进行排序
     *
     * @param fieldQuery 指定列,example:Student::getName
     * @return this query
     */
    public Query<T> orderBy(FieldQuery<T> fieldQuery) {
        this.queryCondition.orderBy(LambdaUtils.getFieldName(fieldQuery));
        return this;
    }


    /**
     * 根据指定列进行倒序排序
     *
     * @param column 指定列
     * @return this query
     */
    public Query<T> orderDescBy(String column) {
        this.queryCondition.orderDescBy(column);
        return this;
    }

    /**
     * 根据指定列进行倒序排序
     *
     * @param fieldQuery 指定列
     * @return this query
     */
    public Query<T> orderDescBy(FieldQuery<T> fieldQuery) {
        this.queryCondition.orderDescBy(LambdaUtils.getFieldName(fieldQuery));
        return this;
    }


    /**
     * 进行list查询,并转为json字符串
     *
     * @return json字符串
     */
    public String listJson() {
        return Json.toJson(this.list());
    }


    /**
     * 进行get查询,并将结果转为json字符串
     *
     * @return json字符串
     */
    public String getJson() {
        return Json.toJson(this.get());
    }


    /**
     * 读取excel并将其转为java列表
     *
     * @param inputPath excel文件绝对路径
     * @return 实体列表
     */
    public List<T> inputExcel(String inputPath) throws IOException {
        Intrinsics.checkParameterIsNotNull(inputPath, "inputPath");
        return this.inputExcel(new FileInputStream(new File(inputPath)));
    }


    /**
     * 读取excel并将其转为java列表
     *
     * @param inputStream excel输入流
     * @return 实体列表
     */
    public List<T> inputExcel(InputStream inputStream) throws IOException {
        Intrinsics.checkParameterIsNotNull(inputStream, "inputStream");
        return ExcelOperator.inputExcel(inputStream, this.componentClass);
    }

    /**
     * 执行list查询并将其导出为Excel
     *
     * @param exportPath 导出路径
     * @return
     */
    public boolean ouputExcel(String exportPath) {
        Intrinsics.checkParameterIsNotNull(exportPath, "exportPath");
        Pair<ArrayList<Object>[], List<String>> pair = this.component.getExcelHeaderAndValues(this.list());
        return ExcelOperator.outputExcel(pair.component2(), pair.component1(), exportPath);
    }


    /**
     * 读取参数对象数组,作为条件查询
     *
     * @param paramArray 参数对象数组
     */
    public Query<T> read(Object[] paramArray) {
        this.queryCondition.read(paramArray);
        return this;
    }


    /**
     * 读取参数对象,作为条件查询
     *
     * @param paramObject 参数对象
     */
    public Query<T> read(Object paramObject) {
        this.queryCondition.read(paramObject);
        return this;
    }


    /**
     * 执行查询,返回list
     */
    public List<T> list() {
        if (db == null) Intrinsics.throwUninitializedPropertyAccessException("db");
        return db.find(this);
    }


    /**
     * 查询,返回{@link Pagination}对象
     */
    public Pagination<T> pageList() {
        if (db == null) Intrinsics.throwUninitializedPropertyAccessException("db");
        return queryCondition.calculatePagination(db.find(this), db.getCount(this));
    }


    /**
     * 分页查询, 返回{@link Pagination}对象
     *
     * @param page     页码
     * @param pageSize 每页数量
     */
    public Pagination<T> pageList(Integer page, Integer pageSize) {
        return this.page(page).pageSize(pageSize).pageList();
    }


    /**
     * 偏移查询,返回{@link Pagination}对象
     *
     * @param offset 偏移量
     * @param limit  最大数量
     */
    public Pagination<T> offsetList(Integer offset, Integer limit) {
        return this.offset(offset).limit(limit).pageList();
    }


    /**
     * 查询一条记录
     */
    public T get() {
        if (db == null) Intrinsics.throwUninitializedPropertyAccessException("db");
        return db.getByQuery(this);
    }

    /**
     * 根据条件执行更新
     *
     * @return 更新的行数
     */
    public Integer executeUpdate() {
        if (db == null) Intrinsics.throwUninitializedPropertyAccessException("db");
        return db.update(this);
    }

    /**
     * 根据条件执行删除操作
     *
     * @return 删除的行数
     */
    public Integer executeDelete() {
        if (db == null) Intrinsics.throwUninitializedPropertyAccessException("db");
        return db.delete(this);
    }


    /**
     * 标注方法名称,无实际作用
     *
     * @param mark 标注名
     */
    public Query<T> mark(String mark) {
        return this;
    }


    /**
     * 设置偏移量
     */
    public Query<T> offset(Integer offset) {
        this.queryCondition.setOffset(offset);
        return this;
    }

    public Class<T> getComponentClass() {
        return this.componentClass;
    }

    public Query(Class<T> componentClass) {
        super();
        Intrinsics.checkParameterIsNotNull(componentClass, "componentClass");
        this.componentClass = componentClass;
        this.component = TableInfo.getComponent(this.componentClass);
        this.queryCondition = new QueryCondition(this.component);
    }


    /**
     * 根据class创建Query对象
     */
    public static <T> Query<T> of(Class<T> componentClass) {
        Intrinsics.checkParameterIsNotNull(componentClass, "componentClass");
        return new Query<>(componentClass);
    }

}
