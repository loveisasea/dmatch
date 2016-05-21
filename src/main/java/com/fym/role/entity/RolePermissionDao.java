package com.fym.role.entity;

import com.fym.core.dao.Dao;
import org.springframework.stereotype.Repository;

/**
 * Created by fengy on 2016/1/28.
 */


@Repository("rolePermissionDao")
public class RolePermissionDao extends Dao<RolePermission> {

//    public List<RolePermission> getList(Integer roleid) {
//        Query query = getCurrentSession().createQuery("from RolePermission where roleid=:roleid")
//                .setInteger("roleid", roleid);
//
//        List list = query.list();
//        return list;
//    }
//
//    public List<RolePermission> getList(Collection<Integer> roleids) {
//        if (roleids != null && roleids.size() > 0) {
//            Query query = getCurrentSession().createQuery("from RolePermission where roleid in (:roleids)")
//                    .setParameterList("roleids", roleids);
//            List list = query.list();
//            return list;
//        } else {
//            return new ArrayList<RolePermission>();
//        }
//
//    }
//
//    public Boolean exits(Collection<Integer> roleids, Collection<String> permissions) {
//        if (roleids.size() == 0) {
//            return false;
//        }
//        if (permissions.size() == 0) {
//            return false;
//        }
//        Query query = getCurrentSession().createQuery("from RolePermission where roleid in (:roleids) and permission in(:permissions)")
//                .setParameterList("permissions", permissions)
//                .setParameterList("roleids", roleids);
//        List list = query.list();
//        return list.size() > 0;
//    }
//
//    public void deleteByRoleId(Integer roleid) {
//        Query query = getCurrentSession().createSQLQuery("delete from RolePermission where roleid=:roleid")
//                .setInteger("roleid", roleid);
//        query.executeUpdate();
//    }
//
//    public void deleteByPermission(String permission) {
//        Query query = getCurrentSession().createSQLQuery("delete from RolePermission where permission=:permission")
//                .setString("permission", permission);
//        query.executeUpdate();
//    }
//
//    public List<Integer> getRoleIdList(String permission) {
//        Query query = getCurrentSession().createSQLQuery("select roleid from RolePermission where permission=:permission")
//                .addScalar("roleid", IntegerType.INSTANCE)
//                .setString("permission", permission);
//        List list = query.list();
//        return list;
//    }
//
//
//    public List<Integer> getRoleIdList(Collection<String> permissions) {
//        if(permissions!=null&& permissions.size()>0) {
//            Query query = getCurrentSession().createSQLQuery("select roleid from RolePermission where permission in (:permissions)")
//                    .addScalar("roleid", IntegerType.INSTANCE)
//                    .setParameterList("permissions", permissions);
//            List list = query.list();
//            return list;
//        }
//        else{
//            return new ArrayList<>();
//        }
//    }

}
