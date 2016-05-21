package com.fym.role.entity;

import com.fym.core.dao.Dao;
import org.springframework.stereotype.Repository;

/**
 * Created by fengy on 2016/1/28.
 * ！！！！！注意！！！！！！！
 * 为了避免和数据库已有表冲突，实体是Role，数据库表名是rolemes
 */
@Repository
public class RolebsDao extends Dao<Rolebs> {
//    /**
//     * 根据角色id获取角色列表
//     * @param roleids 角色id列表
//     * @return 角色列表
//     */
//    public List<Rolebs> getRoles(List<Integer> roleids) {
//        if (roleids != null && roleids.size() > 0) {
//            Query query = getCurrentSession().createQuery("from Rolebs where Id in (:roleids)")
//                    .setParameterList("roleids", roleids);
//            List list = query.list();
//            return list;
//        }
//        else {
//            return new ArrayList<Rolebs>();
//        }
//    }
//
//    /**
//     * 根据角色名获取角色
//     * @param roleName 角色名
//     * @return 角色
//     */
//    public Rolebs getByName(String roleName) {
//        Query query = getCurrentSession().createQuery("from Rolebs where name=:name")
//                .setString("name", roleName);
//        List list = query.list();
//        return (Rolebs) (list.size() > 0 ? list.get(0) : null);
//    }
//
//    /**
//     * 删除角色
//     * @param id 角色id
//     */
//    public void delete(Integer id) {
//        Query query = getCurrentSession().createSQLQuery("delete from rolee where id=:id ")
//                .setInteger("id", id);
//        query.executeUpdate();
//
//    }
//
//    /**
//     * 检查是否所有的roleid都存在
//     * @param roleids
//     * @return
//     */
//    public boolean checkRoleIdExists(Collection<Integer> roleids){
//        if(roleids==null || roleids.size()==0){
//            return true;
//        }
//        Query query = getCurrentSession().createSQLQuery("select count(*) as cnt from Rolebs where id in (:roleids)")
//                .addScalar("cnt", IntegerType.INSTANCE)
//                .setParameterList("roleids", roleids);
//
//        Integer cnt= (Integer) query.list().get(0);
//        return cnt ==roleids.size();
//    }
}
