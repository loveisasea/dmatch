package com.fym.core.dao;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by fengy on 2016/1/27.
 */
public class Dao<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Dao.class);
    private static final int SQLQUERY_INIT_LENGTH = 200;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SessionFactory sessionFactory;

    private Class entityClass;
    private java.lang.reflect.Field idField;


    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    public Dao() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        entityClass = ((Class<T>) params[0]);
        try {
            idField = entityClass.getField("id");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            LOGGER.error("实体没有id字段");
        }
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }


    /**
     * 保存实体
     *
     * @param t 实体
     */
    public void create(T t) {
        Assert.notNull(getCurrentSession(), "session is null");
        getCurrentSession().save(t);
    }

    /**
     * 更新实体
     *
     * @param t 实体
     */
    public void update(T t) {
        Assert.notNull(getCurrentSession(), "session is null");
        getCurrentSession().merge(t);
    }

    /**
     * 删除实体
     *
     * @param t 实体
     */
    public void delete(T t) {
        Assert.notNull(getCurrentSession(), "session is null");
        getCurrentSession().delete(t);
    }

    /**
     * 根据实体Id删除实体
     *
     * @param id 实体Id
     */
    public void delete(Integer id) {
        Query query = getCurrentSession().createSQLQuery("delete from " + entityClass.getSimpleName() + " where id=:id ")
                .setInteger("id", id);
        query.executeUpdate();
    }


    /**
     * 根据过滤条件删除实体
     *
     * @param field 字段名
     * @param value 字段值
     */
    public void delete(String field, Object value) {
        Query query = getCurrentSession().createSQLQuery("delete from " + entityClass.getSimpleName() + " where " + field + "=:" + field)
                .setParameter(field, value);
        query.executeUpdate();
    }


    /**
     * 根据实体主键获取实体
     *
     * @param id 实体主键
     * @return 实体
     */

    public T get(Integer id) {
        Assert.notNull(getCurrentSession(), "session is null");
        return (T) getCurrentSession().get(entityClass, id);
    }

    public boolean exists(Integer id) {
        Query query = getCurrentSession().createSQLQuery(" select count(*) as cnt from "
                + entityClass.getSimpleName() + " where id=:id ")
                .addScalar("cnt", IntegerType.INSTANCE)
                .setInteger("id", id);

        return ((Integer) query.list().get(0)) > 0;
    }


    public boolean exists(String field, Object value) {
        Query query = getCurrentSession().createSQLQuery("select count(*) as cnt from " + entityClass.getSimpleName() + " where " + field + "=:" + field)
                .addScalar("cnt", IntegerType.INSTANCE)
                .setParameter(field, value);
        return ((Integer) query.list().get(0)) > 0;
    }


    /**
     * 根据查询条件,结果集开始和大小,获取实体
     *
     * @param field 字段名 不可以等于null表示如果不需要过滤
     * @param value 字段值
     * @param ord   排序字段,例如"id desc" 不可以等于null表示如果不需要排序
     * @param index 结果集起始位置
     * @param size  结果集大小
     * @return 实体列表
     */
    public List<T> getList(String field, Object value, String ord, int index, int size) {
        Assert.notNull(getCurrentSession(), "session is null");

        StringBuilder builder = new StringBuilder(SQLQUERY_INIT_LENGTH);
//        builder.append(" select ").append(entityClass.getSimpleName()).append(".* as ").append(entityClass.getSimpleName()).append(" from ").append(entityClass.getSimpleName()).append(" where 1=1 ");
        builder.append(" select * from ").append(entityClass.getSimpleName()).append(" where 1=1 ");
        if (field != null) {
            builder.append(" and ").append(field).append("=:").append(field);
        }
        if (ord != null && ord.length() > 0) {
            builder.append(" order by ").append(ord);
        }
        Query query = getCurrentSession().createSQLQuery(builder.toString())
                .addEntity(entityClass.getSimpleName(), entityClass);
        if (field != null) {
            query.setParameter(field, value);
        }
        if (index > 0) {
            query.setFirstResult(index);
        }

        if (size > 0) {
            query.setMaxResults(size);
        }

        return query.list();
    }

    /**
     * 根据查询条件获取实体
     *
     * @param kvs  字段名和过滤值的map
     * @param ord  排序字段 不可以等于null表示如果不需要排序
     * @param idx  结果集起始位置
     * @param size 结果集大小
     * @return 实体列表
     */
    public List<T> getList(Map<String, Object> kvs, String ord,
                           int idx, int size) {
        Assert.notNull(getCurrentSession(), "session is null");

        StringBuilder builder = new StringBuilder(SQLQUERY_INIT_LENGTH);
        builder.append(" select * from ").append(entityClass.getSimpleName()).append(" where 1=1 ");
        if (kvs != null && kvs.size() > 0) {
            Set<String> fields = kvs.keySet();
            for (String field : fields) {
                builder.append(" and ").append(field).append("=:").append(field);
            }
        }
        if (ord != null && ord.length() > 0) {
            builder.append(" order by ").append(ord);
        }
        Query query = getCurrentSession().createSQLQuery(builder.toString())
                .addEntity(entityClass.getSimpleName(), entityClass);
        if (kvs != null && kvs.size() > 0) {
            Set<Map.Entry<String, Object>> entries = kvs.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }

        if (idx > 0) {
            query.setFirstResult(idx);
        }

        if (size > 0) {
            query.setMaxResults(size);
        }

        return query.list();
    }


    /**
     * 根据查询条件获取实体
     *
     * @param field 字段名
     * @param value 字段值
     * @return 实体列表
     */
    public List<T> getList(String field, Object value) {
        return this.getList(field, value, null, 0, 0);
    }

    /**
     * 根据查询条件获取实体
     *
     * @param field 字段名
     * @param value 字段值
     * @return 实体列表
     */
    public List<T> getList(String field, Object value, String order) {
        return this.getList(field, value, order, 0, 0);
    }


    public T getSingleton(String field, Object value) {
        Query query = getCurrentSession()
                .createSQLQuery(" select * from " + entityClass.getSimpleName() + " where " + field + "=:value1")
                .addEntity(entityClass.getSimpleName(), entityClass)
                .setParameter("value1", value);

        List<T> list = query.list();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            if (list.size() > 1) {
                LOGGER.warn(this.entityClass.getSimpleName() + "存在重复项" + field + "=" + value);
            }
            return list.get(0);
        }
    }

    /**
     * 获取所有实体
     *
     * @return 所有实体列表
     */
    public List<T> getAll() {
        return this.getList(null, null, null, 0, 0);
    }


    /**
     * 根据结果集开始和大小获取实体列表
     *
     * @param index   开始位置
     * @param maxSize 结果集大小
     * @return 实体列表
     */
    public List<T> getList(int index, int maxSize) {
        return this.getList(null, null, null, index, maxSize);
    }

    /**
     * 根据id列表获取对应字段的哈希表
     *
     * @param ids id列表
     * @return id和字段的哈希表
     */
    public Map<Integer, String> getMappings(Collection<Integer> ids, String fieldName) {
        List list = null;
        if (ids == null || ids.size() == 0) {
            Query query = getCurrentSession().createSQLQuery("select id," + fieldName + " from " + entityClass.getSimpleName())
                    .addScalar("id", IntegerType.INSTANCE)
                    .addScalar(fieldName, StringType.INSTANCE);
            list = query.list();
        } else {
            Query query = getCurrentSession().createSQLQuery("select id," + fieldName + " from " + entityClass.getSimpleName() + " where id in (:ids)")
                    .addScalar("id", IntegerType.INSTANCE)
                    .addScalar(fieldName, StringType.INSTANCE)
                    .setParameterList("ids", ids);
            list = query.list();
        }
        Map<Integer, String> ret = new HashMap<>(list.size() * 2);
        for (Object obj : list) {
            Object[] objl = (Object[]) obj;
            ret.put((Integer) objl[0], (String) objl[1]);
        }
        return ret;
    }


    /**
     * 根据id列表获取对应实体的哈希表
     *
     * @param ids 主键id列表
     * @return id和实体的哈希表
     */
    public Map<Integer, T> getMappings(Collection<Integer> ids) {
        List<T> list = null;
        if (ids != null && ids.size() != 0) {
            Query query = getCurrentSession().createQuery("from " + entityClass.getSimpleName() + " where id in (:ids)")
                    .setParameterList("ids", ids);
            list = query.list();
        } else {
            Query query = getCurrentSession().createQuery("from " + entityClass.getSimpleName());
            list = query.list();
        }
        Map<Integer, T> ret = new HashMap<>(list.size() * 2);
        try {
            for (T t : list) {
                ret.put((Integer) idField.get(t), t);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            LOGGER.error("object没有id作为主键");
        }
        return ret;
    }

    /**
     * 根据id列表获取对应字段的哈希表
     *
     * @param filterColumnValues 键字段值列表
     * @param filterColumnName   键字段的名称
     * @param valueColumnName    值列的名称
     * @return 键值对的哈希表
     */
    public Map<Object, Object> getMappings(Collection filterColumnValues, String filterColumnName, String valueColumnName) {
        List list = null;
        if (filterColumnValues == null || filterColumnValues.size() == 0) {
            Query query = getCurrentSession().createSQLQuery("select " + filterColumnName + "," + valueColumnName + " from " + entityClass.getSimpleName())
                    .addScalar(filterColumnName)
                    .addScalar(valueColumnName);
            list = query.list();
        } else {
            Query query = getCurrentSession().createSQLQuery("select " + filterColumnName + "," + valueColumnName + " from " + entityClass.getSimpleName() + " where " + filterColumnName + " in (:" + filterColumnName + ")")
                    .addScalar(filterColumnName)
                    .addScalar(valueColumnName)
                    .setParameterList(filterColumnName, filterColumnValues);
            list = query.list();
        }
        Map<Object, Object> ret = new HashMap<>(list.size() * 2);
        for (Object obj : list) {
            Object[] objl = (Object[]) obj;
            ret.put(objl[0], objl[1]);
        }
        return ret;
    }


    public Map<Object, T> getMappingsEx(Collection filterColumnValues, String filterColumnName) {
        List list = null;
        if (filterColumnValues == null) {
            Query query = getCurrentSession().createSQLQuery("select " + filterColumnName + "," + entityClass.getSimpleName() + " from " + entityClass.getSimpleName())
                    .addScalar(filterColumnName)
                    .addEntity(entityClass);
            list = query.list();
        } else if (filterColumnValues.size() == 0) {
            Query query = getCurrentSession().createSQLQuery("select " + filterColumnName + "," + entityClass.getSimpleName() + " from " + entityClass.getSimpleName() + " where 1=0 ")
                    .addScalar(filterColumnName)
                    .addEntity(entityClass);
            list = query.list();
        } else {
            Query query = getCurrentSession().createSQLQuery("select " + filterColumnName + "," + entityClass.getSimpleName() + ".* as " + entityClass.getSimpleName() + " from " + entityClass.getSimpleName() + " where " + filterColumnName + " in (:" + filterColumnName + ")")
                    .addScalar(filterColumnName)
                    .addEntity(entityClass)
                    .setParameterList(filterColumnName, filterColumnValues);
//            Query queryCore = getCurrentSession().createSQLQuery("select " + filterColumnName + "," + entityClass.getSimpleName() + " from " + entityClass.getSimpleName() + " where " + filterColumnName + " in (:" + filterColumnName + ")")
//                    .addScalar(filterColumnName)
//                    .addEntity(entityClass)
//                    .setParameterList(filterColumnName, filterColumnValues);
            list = query.list();
        }
        Map<Object, T> ret = new HashMap<>(list.size() * 2);
        for (Object obj : list) {
            Object[] objl = (Object[]) obj;
            ret.put(objl[0], (T) objl[1]);
        }
        return ret;
    }


    public PQuery<T> createPQuery() {
        PQuery<T> pQuery = new PQuery();
        return pQuery;
    }

    public class PQuery<T> {
        private int seq = 1;
        private List<Field> fields;
        private List<Equal> equals;
        private List<Larger> largers;
        private List<Smaller> smallers;
        private List<NotEqual> notequals;
        private List<In> ins;
        private List<NotIn> notins;

        private int idx = 0;
        private int size = 0;
        private String ord;
        private boolean cnt;


        public List<T> query() {
            return this.queryCore();
        }

        public List queryScale() {
            return this.queryCore();
        }

        public int count() {
            this.cnt = true;
            List ret = this.queryCore();
            return (int) ret.get(0);
        }

        public T querySingleton() {
            List<T> list = this.query();
            if (list == null || list.size() == 0) {
                return null;
            } else {
                if (list.size() > 1) {
                    try {
                        LOGGER.warn(Dao.this.entityClass.getSimpleName() + "存在重复项:<" + Dao.this.objectMapper.writeValueAsString(this) + ">");
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
                return list.get(0);
            }
        }

        private List queryCore() {
            Assert.notNull(getCurrentSession(), "session is null");

            StringBuilder builder = new StringBuilder(SQLQUERY_INIT_LENGTH);
            if (this.cnt) {
                builder.append(" select count(*) as cnt from ").append(entityClass.getSimpleName()).append(" where 1=1 ");
            } else if (this.fields != null && this.fields.size() > 0) {
                builder.append(" select ");
                for (int i = 0; i < this.fields.size(); i++) {
                    builder.append(this.fields.get(i).fieldName);
                    if (i < this.fields.size() - 1) {
                        builder.append(",");
                    }
                }
                builder.append(" from ").append(entityClass.getSimpleName()).append(" where 1 = 1 ");
            } else {
                builder.append(" select * from ").append(entityClass.getSimpleName()).append(" where 1=1 ");
            }
            //等于条件
            if (this.equals != null) {
                for (Equal cond : this.equals) {
                    builder.append(" and ").append(cond.fieldName).append(" = :").append(cond.valueName);
                }
            }
            //大于条件
            if (this.largers != null) {
                for (Larger cond : this.largers) {
                    builder.append(" and ").append(cond.fieldName).append(" > :").append(cond.valueName);
                }
            }
            //小于条件
            if (this.smallers != null) {
                for (Smaller cond : this.smallers) {
                    builder.append(" and ").append(cond.fieldName).append(" < :").append(cond.valueName);
                }
            }
            //不等于条件
            if (this.notequals != null) {
                for (NotEqual cond : this.notequals) {
                    builder.append(" and ").append(cond.fieldName).append(" <> :").append(cond.valueName);
                }
            }
            //值范围条件
            if (this.ins != null) {
                for (In cond : this.ins) {
                    if (cond.values != null && cond.values.size() > 0) {
                        builder.append(" and ").append(cond.fieldName).append(" in (:").append(cond.valueName).append(") ");
                    } else {
                        builder.append(" and 1=2 ");
                    }
                }
            }
            //值不在范围条件
            if (this.notins != null) {
                for (NotIn cond : this.notins) {
                    if (cond.values != null && cond.values.size() > 0) {
                        builder.append(" and ").append(cond.fieldName).append(" not in (:").append(cond.valueName).append(") ");
                    }
                }
            }
            //排序
            if (this.ord != null && this.ord.length() > 0) {
                builder.append(" order by ").append(ord);
            }
            SQLQuery query = getCurrentSession().createSQLQuery(builder.toString());

            if (this.cnt) {
                query.addScalar("cnt", IntegerType.INSTANCE);
            } else if (this.fields != null && this.fields.size() > 0) {
                for (Field field : this.fields) {
                    query.addScalar(field.fieldName, field.type);
                }
            } else {
                query.addEntity(entityClass.getSimpleName(), entityClass);
            }

            //等于条件
            if (this.equals != null) {
                for (Equal cond : this.equals) {
                    query.setParameter(cond.valueName, cond.value);
                }
            }
            //大于条件
            if (this.largers != null) {
                for (Larger cond : this.largers) {
                    query.setParameter(cond.valueName, cond.value);
                }
            }
            //小于条件
            if (this.smallers != null) {
                for (Smaller cond : this.smallers) {
                    query.setParameter(cond.valueName, cond.value);
                }
            }
            //不等于条件
            if (this.notequals != null) {
                for (NotEqual cond : this.notequals) {
                    query.setParameter(cond.valueName, cond.value);
                }
            }
            //值范围条件
            if (this.ins != null) {
                for (In cond : this.ins) {
                    if (cond.values != null && cond.values.size() > 0) {
                        query.setParameterList(cond.valueName, cond.values);
                    }
                }
            }
            //值不在范围条件
            if (this.notins != null) {
                for (NotIn cond : this.notins) {
                    if (cond.values != null && cond.values.size() > 0) {
                        query.setParameterList(cond.valueName, cond.values);
                    }
                }
            }
            //初始位置
            if (this.idx > 0) {
                query.setFirstResult(idx);
            }
            //结果集大小
            if (this.size > 0) {
                query.setMaxResults(size);
            }
            return query.list();
        }


        /**
         * 添加字段
         *
         * @param fieldName 字段名
         * @param type      值类型
         * @return
         */
        public PQuery<T> addScale(String fieldName, org.hibernate.type.Type type)  {

            Field field = new Field();
            field.fieldName = fieldName;
            field.type = type;
            if (this.fields == null) {
                this.fields = new ArrayList<>(2);
            }
            this.fields.add(field);
            return this;
        }

        /**
         * 添加等于条件
         *
         * @param fieldName 字段名
         * @param value     值
         * @return
         */
        public PQuery<T> equal(String fieldName, Object value)  {

            Equal cond = new Equal();
            cond.fieldName = fieldName;
            cond.valueName = fieldName + (this.seq++);
            cond.value = value;
            if (this.equals == null) {
                this.equals = new ArrayList<>(2);
            }
            this.equals.add(cond);
            return this;
        }

        /**
         * 添加大于条件
         *
         * @param fieldName 字段名
         * @param value     值
         * @return
         */
        public PQuery<T> larger(String fieldName, Object value)   {

            Larger cond = new Larger();
            cond.fieldName = fieldName;
            cond.valueName = fieldName + (this.seq++);
            cond.value = value;
            if (this.largers == null) {
                this.largers = new ArrayList<>(2);
            }
            this.largers.add(cond);
            return this;
        }

        /**
         * 添加小于条件
         *
         * @param fieldName 字段名
         * @param value     值
         * @return
         */
        public PQuery<T> smaller(String fieldName, Object value)  {

            Smaller cond = new Smaller();
            cond.fieldName = fieldName;
            cond.valueName = fieldName + (this.seq++);
            cond.value = value;
            if (this.smallers == null) {
                this.smallers = new ArrayList<>(2);
            }
            this.smallers.add(cond);
            return this;
        }

        /**
         * 添加等于条件
         *
         * @param fieldName 字段名
         * @param value     值
         * @return
         */
        public PQuery<T> notEqual(String fieldName, Object value)  {
            NotEqual cond = new NotEqual();
            cond.fieldName = fieldName;
            cond.valueName = fieldName + (this.seq++);
            cond.value = value;
            if (this.notequals == null) {
                this.notequals = new ArrayList<>(2);
            }
            this.notequals.add(cond);
            return this;
        }

        /**
         * 添加值范围条件
         *
         * @param fieldName 字段名
         * @param values    值
         * @return
         */
        public PQuery<T> inCollection(String fieldName, Collection values)  {
            In cond = new In();
            cond.fieldName = fieldName;
            cond.valueName = fieldName + (this.seq++);
            cond.values = values;
            if (this.ins == null) {
                this.ins = new ArrayList<>(2);
            }
            this.ins.add(cond);
            return this;
        }

        /**
         * 添加值范围条件
         *
         * @param fieldName 字段名
         * @param values    值
         * @return
         */
        public PQuery<T> in(String fieldName, Object... values)  {
            In cond = new In();
            cond.fieldName = fieldName;
            cond.valueName = fieldName + (this.seq++);
            if (values == null) {
                cond.values = null;
            }
            cond.values = new ArrayList<>(values.length);
            for (Object value : values) {
                cond.values.add(value);
            }
            if (this.ins == null) {
                this.ins = new ArrayList<>(2);
            }
            this.ins.add(cond);
            return this;
        }


        /**
         * 添加值不在范围条件
         *
         * @param fieldName 字段名
         * @param values    值
         * @return
         */
        public PQuery<T> notInCollection(String fieldName, Collection values)  {
            NotIn cond = new NotIn();
            cond.fieldName = fieldName;
            cond.valueName = fieldName + (this.seq++);
            cond.values = values;
            if (this.notins == null) {
                this.notins = new ArrayList<>(2);
            }
            this.notins.add(cond);
            return this;
        }

        /**
         * 添加值不在范围条件
         *
         * @param fieldName 字段名
         * @param values    值
         * @return
         */
        public PQuery<T> notIn(String fieldName, Object... values)   {
            NotIn cond = new NotIn();
            cond.fieldName = fieldName;
            cond.valueName = fieldName + (this.seq++);
            if (values == null) {
                cond.values = null;
            }
            cond.values = new ArrayList<>(values.length);
            for (Object value : values) {
                cond.values.add(value);
            }
            if (this.notins == null) {
                this.notins = new ArrayList<>(2);
            }
            this.notins.add(cond);
            return this;
        }


        /**
         * 设置排序字段
         *
         * @param ord
         * @return
         */
        public PQuery<T> setOrd(String ord) {
            this.ord = ord;
            return this;
        }

        /**
         * 设置初始索引
         *
         * @param idx
         * @return
         */
        public PQuery<T> setIdx(int idx) {
            this.idx = idx;
            return this;
        }

        /**
         * 设置结果集大小
         *
         * @param size
         * @return
         */
        public PQuery<T> setSize(int size) {
            this.size = size;
            return this;
        }

//        public String toString(){
//            StringBuilder sb = new StringBuilder(Dao.SQLQUERY_INIT_LENGTH);
//            sb.append("equalCond")
//            if(this.equals!=null){
//
//            }
//        }
    }

    private static class In {
        public String fieldName;
        public String valueName;
        public Collection<Object> values;
    }

    private static class Equal {
        public String fieldName;
        public String valueName;
        public Object value;
    }

    private static class Larger {
        public String fieldName;
        public String valueName;
        public Object value;
    }

    private static class Smaller {
        public String fieldName;
        public String valueName;
        public Object value;
    }

    private static class NotEqual {
        public String fieldName;
        public String valueName;
        public Object value;
    }

    private static class NotIn {
        public String fieldName;
        public String valueName;
        public Collection<Object> values;
    }

    private static class Field {
        public String fieldName;
        public String valueName;
        public org.hibernate.type.Type type;
    }
}

