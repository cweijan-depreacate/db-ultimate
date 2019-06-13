package github.cweijan.ultimate.springboot.helper;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.core.extra.ExtraDataService;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class ServiceInject<T> {

    private Class <T> entityClass;
    public ServiceInject() {
        try {
            entityClass=(Class <T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("must set generic type for service : "+getClass().getName());
        }
    }
    public Query<T> getQuery(){
        return Query.of(entityClass);
    }

    public final List<T> findByExample(Object... examples){
        Query<T> query = getQuery();
        for (Object example : examples) {
            query.readObject(example);
        }
        return query.list();
    }

    @Transactional(readOnly=true)
    public List<T> findBy(String column,Object value){
        return getQuery().eq(column,value).list();
    }

    @Transactional(readOnly=true)
    public List<T> findBy(String column,Object value,Integer page,Integer pageSize){
        return getQuery().eq(column,value).page(page).pageSize(pageSize).list();
    }

    @Transactional(readOnly=true)
    public List<T> findAll(){
        return getQuery().list();
    }

    @Transactional(readOnly=true)
    public List<T> findAllLimit(int page,int pageSize){
        return Query.of(entityClass).page(page).pageSize(pageSize).list();
    }

    @Transactional(readOnly=true)
    public T get(Object primaryKey) {
        return Query.db.getByPrimaryKey(entityClass,primaryKey);
    }

    @Transactional(readOnly=true)
    public T getByExample(Object example) {

        List<T> list = getQuery().readObject(example).list();
        return list.size()>0?list.get(0):null;
    }

    @Transactional
    public void save(T entity){
        Query.db.insert(entity);
    }

    @Transactional
    public void batchSave(List<T> entityList){
        Query.db.insertList(entityList);
    }

    @Transactional
    public void update(T entity){
        Query.db.update(entity);
    }

    @Transactional
    public void saveOrUpdate(T entity){
        Query.db.insertOfUpdate(entity);
    }

    @Transactional
    public void delete(Object primaryKey) {
        Query.db.deleteByPrimaryKey(entityClass,primaryKey);
    }

    @Transactional
    public void batchDeleteByExample(Object example) {
        getQuery().readObject(example).executeDelete();
    }

    @Transactional
    public void saveExtra(Object key, Object extraObject){
        ExtraDataService.save(key,extraObject,entityClass.getName());
    }

    @Transactional(readOnly = true)
    public void getExtra(Object key, Object extraObject){
        ExtraDataService.getExtraData(key,extraObject.getClass(),entityClass.getName());
    }

    @Transactional(readOnly = true)
    public Integer getCount(){
        return getQuery().count();
    }


}
