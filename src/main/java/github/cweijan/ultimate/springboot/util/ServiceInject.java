package github.cweijan.ultimate.springboot.util;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.core.extra.ExtraDataService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.List;

@CacheConfig()
public abstract class ServiceInject<T> implements InitializingBean {

    private Class <T> componentClass;
    public ServiceInject() {
        try {
            componentClass=(Class <T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("must set generic type for service : "+getClass().getName());
        }
    }
    public ServiceInject(Class<T> componentClass) {
        this.componentClass=componentClass;
    }
    public Query<T> getQuery(){
        return Query.of(componentClass);
    }

    public List<T> findByExample(Object... examples){
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
    public List<T> findByPage(Integer page, Integer pageSize){
        return Query.of(componentClass).page(page).pageSize(pageSize).list();
    }

    @Transactional(readOnly=true)
    public T get(Object primaryKey) {
        return Query.db.getByPrimaryKey(componentClass,primaryKey);
    }

    @Transactional(readOnly=true)
    public T getByExample(Object example) {

        List<T> list = getQuery().readObject(example).list();
        return list.size()>0?list.get(0):null;
    }

    @Transactional
    public void save(T component){
        Query.db.insert(component);
    }

    @Transactional
    public void batchSave(List<T> componentList){
        Query.db.insertList(componentList);
    }

    @Transactional
    public void update(T component){
        Query.db.update(component);
    }

    @Transactional
    public void saveOrUpdate(T component){
        Query.db.insertOfUpdate(component);
    }

    @Transactional
    public void delete(Object primaryKey) {
        Query.db.deleteByPrimaryKey(componentClass,primaryKey);
    }

    @Transactional
    public void batchDeleteByExample(Object example) {
        getQuery().readObject(example).executeDelete();
    }

    @Transactional
    public void saveExtra(Object key, Object extraObject){
        ExtraDataService.save(key,extraObject,componentClass.getName());
    }

    @Transactional(readOnly = true)
    public void getExtra(Object key, Object extraObject){
        ExtraDataService.getExtraData(key,extraObject.getClass(),componentClass.getName());
    }

    @Transactional(readOnly = true)
    public Integer getCount(){
        return getQuery().count();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ServiceMap.mapService(componentClass.getName(),this);
    }

    public static void copyProperties(Object target,Object source) {
        BeanUtils.copyProperties(target, source);
    }

}
