package com.fym.role;


import com.fym.core.err.OpException;
import com.fym.role.cmd.RoleCreateCmd;
import com.fym.role.cmd.RoleModifyCmd;
import com.fym.role.obj.Role;

import java.util.Collection;
import java.util.List;

public interface RoleService {


    /**
     * 添加角色
     *
     * @param roleCmd 要添加的角色
     * @return 角色id
     * @throws OpException
     */
    Role createRole(RoleCreateCmd roleCmd) throws OpException;

    /**
     * 修改角色
     *
     * @param roleCmd 将要修改成的角色
     * @throws OpException
     */
    Role modifyRole(RoleModifyCmd roleCmd) throws OpException;

    /**
     * 删除角色
     *
     * @param roleid 角色id
     * @throws OpException
     */
    void deleteRole(Integer roleid) throws OpException;

    /**
     * 获取带上权限信息的角色
     *
     * @param roleid 角色id
     * @param field   掩码
     * @return 带上权限的角色信息
     * @throws OpException
     */
    Role getRole(Integer roleid, int field) throws OpException;


    /**
     * 根据角色id获取角色们所有的子权限
     *
     * @param roleids 角色ids
     * @return 子权限哈希表
     */
    Collection<String> getPermissionItems(Collection<Integer> roleids);

    /**
     * 从给定的子权限列表筛选出角色拥有的权限
     *
     * @param roleids   给定的角色Ids
     * @param permItems 给定的子权限列表
     * @return 筛选后的子权限列表
     */
    Collection<String> getPermissionItems(Collection<Integer> roleids, Collection<String> permItems);

    /**
     * 判断角色是否拥有子权限
     *
     * @param roleids  角色ids
     * @param permItem 子权限
     * @return true:有，false：无
     */
    boolean checkPermission(Collection<Integer> roleids, String permItem);

    /**
     * 获取所有角色
     *
     * @param field 掩码
     * @return 角色列表
     */
    List<Role> getAllRoles(int field) throws OpException;



}
