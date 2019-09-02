package github.cweijan.ultimate.springboot.util;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.core.extra.ExtraDataService;
import github.cweijan.ultimate.core.lucene.LuceneQuery;
import github.cweijan.ultimate.core.page.Pagination;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class ServiceInject<T> implements InitializingBean {

    private Class<T> componentClass;

    public ServiceInject() {
        try {
            componentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("must set generic type for service : " + getClass().getName());
        }
    }

    public ServiceInject(Class<T> componentClass) {
        this.componentClass = componentClass;
    }

    /**
     * 获取查询对象
     *
     * @return {@link Query}
     */
    public Query<T> getQuery() {
        return Query.of(componentClass);
    }

    /**
     * 获取Lucene查询对象
     *
     * @return {@link LuceneQuery}
     */
    public LuceneQuery<T> getLuceneQuery() {
        return LuceneQuery.of(componentClass);
    }

    /**
     * 根据实体对象参数进行查询
     *
     * @param params 任意JavaBean
     * @return 实体列表
     */
    public List<T> findByParam(Object... params) {
        return getQuery().read(params).list();
    }

    /**
     * 根据指定进行条件查询
     *
     * @param column 列名
     * @param value  列值
     */
    @Transactional(readOnly = true)
    public List<T> findBy(String column, Object value) {
        return getQuery().eq(column, value).list();
    }

    /**
     * 对指定列进行查询,并可进行分页
     *
     * @param column 列名
     * @param value  列值
     * @param page 页码
     * @param pageSize 每页大小
     */
    @Transactional(readOnly = true)
    public List<T> findBy(String column, Object value, Integer page, Integer pageSize) {
        return getQuery().eq(column, value).page(page).pageSize(pageSize).list();
    }

    /**
     * 查询所有数据
     */
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return getQuery().list();
    }

    /**
     * 页码分页查询,实体查询对象为任意JavaBean,以FieldName作为column,FieldValue作为Value
     *
     * @param page     页码
     * @param pageSize 每页数量
     * @param objects  实体查询参数
     * @return 分页对象 {@link Pagination}
     */
    @Transactional(readOnly = true)
    public Pagination<T> findByPage(Integer page, Integer pageSize, Object... objects) {
        return getQuery().read(objects).pageList(page,pageSize);
    }

    /**
     * 偏移量分页查询,实体查询对象为任意JavaBean,以FieldName作为column,FieldValue作为Value
     *
     * @param offset   需要跳过的数据量
     * @param pageSize 每页数量
     * @param objects  实体查询参数
     * @return 分页对象 @{@link Pagination}
     */
    @Transactional(readOnly = true)
    public Pagination<T> findByOffset(Integer offset, Integer pageSize, Object... objects) {
        Query<T> query = Query.of(componentClass);
        if (objects != null) {
            for (Object object : objects) {
                query.read(object);
            }
        }
        return query.offset(offset).limit(pageSize).pageList();
    }

    /**
     * 根据主键获取实体,实体必须使用{@link github.cweijan.ultimate.annotation.Primary}标注主键Field
     *
     * @param primaryKey 主键值
     * @return 实体
     */
    @Transactional(readOnly = true)
    public T getByPrimaryKey(Object primaryKey) {
        return Query.db.getByPrimaryKey(componentClass, primaryKey);
    }

    /**
     * 根据查询参数进行get查询
     *
     * @param param 查询参数
     * @return 数据实体
     */
    @Transactional(readOnly = true)
    public T getByParam(Object param) {

        List<T> list = getQuery().read(param).list();
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据指定列进行get查询
     *
     * @param column 列名称
     * @param value  列值
     * @return
     */
    @Transactional(readOnly = true)
    public T getBy(String column, Object value) {

        return getQuery().eq(column, value).get();
    }

    /**
     * 保存实体
     *
     * @param component 数据实体
     */
    @Transactional
    public void save(T component) {
        Query.db.insert(component);
    }

    /**
     * 数据不存在才进行保存
     *
     * @param component 数据实体
     */
    @Transactional
    public void saveIgnore(T component) {

        Query.db.ignoreInsert(component);
    }

    /**
     * 批量保存
     *
     * @param componentList 数据列表
     */
    @Transactional
    public void batchSave(List<T> componentList) {
        Query.db.insertList(componentList);
    }

    /**
     * 更新实体
     *
     * @param component
     */
    @Transactional
    public void update(T component) {
        Query.db.update(component);
    }

    /**
     * 根据实体里面某个列作为基准进行更新
     *
     * @param columnName 基准列
     * @param component  实体
     */
    @Transactional
    public void updateBy(String columnName, T component) {
        Query.db.updateBy(columnName, component);
    }

    /**
     * 如果实体主键为空则进行报错,不为空则进行更新
     *
     * @param component 实体
     */
    @Transactional
    public void saveOrUpdate(T component) {
        Query.db.insertOfUpdate(component);
    }

    /**
     * 根据主键进行删除
     *
     * @param primaryKey 主键值
     */
    @Transactional
    public void delete(Object primaryKey) {
        Query.db.deleteByPrimaryKey(componentClass, primaryKey);
    }

    @Transactional
    public void batchDeleteByExample(Object example) {
        getQuery().read(example).executeDelete();
    }

    @Transactional
    public void saveExtra(Object key, Object extraObject) {
        ExtraDataService.save(key, extraObject, componentClass.getName());
    }

    @Transactional(readOnly = true)
    public <E> E getExtra(Object key, Class<E> extraType) {
        return ExtraDataService.getExtraData(key, extraType, componentClass.getName());
    }

    @Transactional(readOnly = true)
    public Integer getCount() {
        return getQuery().count();
    }

    /**
     * 使用sql进行查询,支持参数,可防止sql注入
     *
     * @param sql    sql
     * @param params 参数列表
     * @return
     */
    public List<T> findBySql(String sql, Object... params) {
        return Query.db.findBySql(sql, params, componentClass);
    }

    /**
     * 使用sql进行get查询
     *
     * @param sql
     * @param params 参数列表
     * @return
     */
    public T getBySql(String sql, Object... params) {
        return Query.db.getBySql(sql, params, componentClass);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ServiceMap.mapService(componentClass.getName(), this);
    }

}
