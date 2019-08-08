package github.cweijan.ultimate.springboot.util;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.core.extra.ExtraDataService;
import github.cweijan.ultimate.core.lucene.LuceneQuery;
import github.cweijan.ultimate.core.page.Pagination;
import org.springframework.beans.BeanUtils;
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

    public Query<T> getQuery() {
        return Query.of(componentClass);
    }

    public LuceneQuery<T> getLuceneQuery() {
        return LuceneQuery.of(componentClass);
    }

    public List<T> findByOBject(Object... objects) {
        Query<T> query = getQuery();
        for (Object example : objects) {
            query.read(example);
        }
        return query.list();
    }

    /**
     * 对指定列进行条件查询
     */
    @Transactional(readOnly = true)
    public List<T> findBy(String column, Object value) {
        return getQuery().eq(column, value).list();
    }

    /**
     * 对指定列进行查询,并可进行分页
     */
    @Transactional(readOnly = true)
    public List<T> findBy(String column, Object value, Integer page, Integer pageSize) {
        return getQuery().eq(column, value).page(page).pageSize(pageSize).list();
    }

    /**
     * 获取所有数据
     */
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return getQuery().list();
    }

    /**
     * 分页查询
     *
     * @param page     页码
     * @param pageSize 每页数量
     */
    @Transactional(readOnly = true)
    public Pagination<T> findByPage(Integer page, Integer pageSize) {
        return Query.of(componentClass).page(page).pageSize(pageSize).pageList();
    }

    /**
     * 分页查询
     *
     * @param page   页码
     * @param pageSize 每页数量
     * @param objects  查询参数,自动使用readObject读取
     */
    @Transactional(readOnly = true)
    public Pagination<T> findByPage(Integer page, Integer pageSize, Object... objects) {
        Query<T> query = Query.of(componentClass);
        if (objects != null) {
            for (Object object : objects) {
                query.read(object);
            }
        }
        return query.page(page).pageSize(pageSize).pageList();
    }

    /**
     * 分页查询
     *
     * @param offset   需要跳过的数据量
     * @param pageSize 每页数量
     */
    @Transactional(readOnly = true)
    public Pagination<T> findByOffset(Integer offset, Integer pageSize) {
        return Query.of(componentClass).offset(offset).pageSize(pageSize).pageList();
    }

    /**
     * 分页查询
     *
     * @param offset   需要跳过的数据量
     * @param pageSize 每页数量
     * @param objects  查询参数,自动使用readObject读取
     */
    @Transactional(readOnly = true)
    public Pagination<T> findByOffset(Integer offset, Integer pageSize, Object... objects) {
        Query<T> query = Query.of(componentClass);
        if (objects != null) {
            for (Object object : objects) {
                query.read(object);
            }
        }
        return query.offset(offset).pageSize(pageSize).pageList();
    }

    @Transactional(readOnly = true)
    public T getByPrimaryKey(Object primaryKey) {
        return Query.db.getByPrimaryKey(componentClass, primaryKey);
    }

    @Transactional(readOnly = true)
    public T getByOBject(Object object) {

        List<T> list = getQuery().read(object).list();
        return list.size() > 0 ? list.get(0) : null;
    }

    @Transactional(readOnly = true)
    public T getBy(String column, Object value) {

        return getQuery().eq(column, value).get();
    }

    @Transactional
    public void save(T component) {
        Query.db.insert(component);
    }

    @Transactional
    public void saveIgnore(T component) {

        Query.db.ignoreInsert(component);
    }

    @Transactional
    public void batchSave(List<T> componentList) {
        Query.db.insertList(componentList);
    }

    @Transactional
    public void update(T component) {
        Query.db.update(component);
    }

    @Transactional
    public void saveOrUpdate(T component) {
        Query.db.insertOfUpdate(component);
    }

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

    @Override
    public void afterPropertiesSet() throws Exception {
        ServiceMap.mapService(componentClass.getName(), this);
    }

    public static void copyProperties(Object target, Object source) {
        BeanUtils.copyProperties(target, source);
    }

}
