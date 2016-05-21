package com.fym.role;

import com.fym.core.err.OpResult;
import com.fym.role.cmd.RoleCreateCmd;
import com.fym.role.cmd.RoleModifyCmd;
import com.fym.role.entity.Rolebs;
import com.fym.role.entity.RolePermission;
import com.fym.role.entity.RolePermissionDao;
import com.fym.core.err.OpException;
import com.fym.role.entity.RolebsDao;
import com.fym.role.obj.Role;
import com.fym.core.util.BitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Transactional(rollbackFor = Exception.class)
@Service("roleService")
public class RoleServiceImpl implements RoleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private RolePermissionDao rolePermissionDao;

    @Autowired
    private RolebsDao rolebsDao;

    @Autowired
    private PermissionCom permissionCom;


    @Override
    public Role createRole(RoleCreateCmd roleCmd) throws OpException {

        if (roleCmd.name == null || roleCmd.name.trim().length() == 0) {
            throw new OpException(OpResult.FAIL, "请输入角色名称");
        }
        roleCmd.name = roleCmd.name.trim();
        // 是否没有重复的角色名
        Rolebs rolebsOld = this.rolebsDao.getSingleton("name", roleCmd.name);
        if (rolebsOld != null) {
            throw new OpException(OpResult.FAIL, "已存在同名角色");
        }

        Rolebs rolebsNew = new Rolebs();
        rolebsNew.name = roleCmd.name;
        rolebsNew.description = roleCmd.description == null ? null : roleCmd.description.trim();
        this.rolebsDao.create(rolebsNew);

        this.modifyRolePermissions(rolebsNew.id, roleCmd.permissions);
//		this.opLogAPIService.createOpLog(loginPassword, OpLog.CAT_ROLE, "角色" + rolebs.name, OpLog.OPTYPE_ADD);


        return this.fillRole(this.genRole(rolebsNew), Role.Field_permissionKeys);
    }


    @Override
    public Role modifyRole(RoleModifyCmd roleCmd) throws OpException {
        // 是否已存在对应的角色
        Rolebs rolebsOld = this.rolebsDao.get(roleCmd.id);
        if (rolebsOld == null) {
            throw new OpException(OpResult.FAIL, "原有角色Id" + roleCmd.id + "不存在");
        }

        //名称
        if (roleCmd.name != null && !roleCmd.name.trim().equals(rolebsOld.name)) {
            // 是否没有重复的角色名
            roleCmd.name = roleCmd.name.trim();
            Rolebs rolebsExisting = this.rolebsDao.getSingleton("name", roleCmd.name);
            if (rolebsExisting != null && !rolebsExisting.id.equals(rolebsOld.id)) {
                throw new OpException(OpResult.FAIL, "已存在同名角色");
            }
            rolebsOld.name = roleCmd.name;
        }

        //描述
        if (roleCmd.description != null && !roleCmd.description.trim().equals(rolebsOld.description)) {
            rolebsOld.description = roleCmd.description.trim();
        }
        this.rolebsDao.update(rolebsOld);


        this.modifyRolePermissions(rolebsOld.id, roleCmd.permissions);
//		this.opLogAPIService.createOpLog(loginPassword, OpLog.CAT_ROLE, "角色" + rolebs.name, OpLog.OPTYPE_ADD);

        return this.fillRole(this.genRole(rolebsOld), Role.Field_permissionKeys);
    }

    @Override
    public Role getRole(Integer roleid, int field) throws OpException {
        Rolebs rolebs = this.getRoleCore(roleid);
        return this.fillRole(this.genRole(rolebs), field);
    }

    // 根据角色列表和来源取权限
    @Override
    public Collection<String> getPermissionItems(Collection<Integer> roleids) {
        List<RolePermission> list = this.rolePermissionDao.createPQuery().inCollection("roleid", roleids).query();
        List<String> permissions = new ArrayList<>(list.size());
        for (RolePermission rp : list) {
            permissions.add(rp.permission);
        }
        Collection<String> ret = this.permissionCom.getItems(permissions);
        return ret;
    }

    // 根据角色列表和来源取权限
    @Override
    public Collection<String> getPermissionItems(Collection<Integer> roleids, Collection<String> permItems) {
        if (permItems == null) {
            return null;
        }
        if (roleids == null) {
            return null;
        }

        Set<String> permR = new HashSet<>();
        //先从数据库取权限并缓存到hash
        List<RolePermission> list = this.rolePermissionDao.createPQuery().inCollection("roleid", roleids).query();
        for (RolePermission rp : list) {
            permR.add(rp.permission);
        }

        List<String> ret = new ArrayList<>(permItems.size());
        for (String permItem : permItems) {
            List<String> pf = this.permissionCom.getPermissionKeys(permItem);
            for (String permission : pf) {
                if (permR.contains(permission)) {
                    ret.add(permItem);
                    break;
                }
            }
        }
        return ret;
    }

    // 检查用户是否有对应的权限
    // 找不到权限被视为用户没有该权限
    @Override
    public boolean checkPermission(Collection<Integer> roleids, String permItem) {
        // 从数据库和缓存permissionsCache检查
        List<String> pf = this.permissionCom.getPermissionKeys(permItem);
        return this.rolePermissionDao.createPQuery()
                .inCollection("roleid", roleids)
                .inCollection("permission", pf)
                .count() > 0;
    }

    @Override
    public List<Role> getAllRoles(int field) throws OpException {
        List<Rolebs> rolebses = this.rolebsDao.getAll();
        List<Role> roles = this.genRoles(rolebses);
        this.fillRoles(roles, field);
        return roles;
    }


    @Override
    public void deleteRole(Integer roleid) throws OpException {

        Rolebs rolebs = this.rolebsDao.get(roleid);
        this.modifyRolePermissions(roleid, Collections.EMPTY_LIST);
        if (rolebs != null) {
            this.rolebsDao.delete(roleid);
        }
    }


    private Rolebs getRoleCore(Integer roleid) throws OpException {
        Rolebs rolebs = this.rolebsDao.get(roleid);
        if (rolebs == null) {
            throw new OpException(OpResult.FAIL, "找不到该角色");
        }
        return rolebs;
    }


    private Comparator<Rolebs> roleComparator = new Comparator<Rolebs>() {
        @Override
        public int compare(Rolebs o1, Rolebs o2) {
            if (o1 == null || o1.id == null) {
                return -1;
            } else if (o2 == null || o2.id == null) {
                return 1;
            } else {
                if (o1.id > o2.id) {
                    return -1;
                } else if (o1.id < o2.id) {
                    return 1;
                } else {
                    return 0;
                }
            }

        }
    };


    private void modifyRolePermissions(Integer roleid, Collection<String> permissionKeys) throws OpException {

        if (permissionKeys == null) {
            return;
        }

        //检查权限是否存在
        //注释掉使代码鲁棒性更好
//        for (String pKey : permissions) {
//            if (!this.permissionCom.permissionExists(pKey)) {
//                throw new OpException(OpResult.INVALID, "权限" + pKey + "并不存在");
//            }
//        }

        // 权限处理
        this.rolePermissionDao.delete("id", roleid);
        if (permissionKeys.size() > 0) {
            for (String pName : permissionKeys) {
                RolePermission rp = new RolePermission();
                rp.roleid = (roleid);
                rp.permission = (pName);
                this.rolePermissionDao.create(rp);
            }
        }

        try {
            Rolebs rolebs = this.rolebsDao.get(roleid);
//			this.opLogAPIService.createOpLog(loginPassword, OpLog.CAT_ROLE, "角色拥有的权限" + rolebs.name, OpLog.OPTYPE_MODIFY);
        } catch (Exception e) {
            LOGGER.error("错误：记录日志出错。");
            LOGGER.error(e.getMessage());
        }
    }

    private Role fillRole(Role role, int field) throws OpException {
        role._field = field;
        if (BitUtil.include(field, Role.Field_permissionKeys)) {
            role.permissions = new HashSet<>();
            List<RolePermission> rolepermissions = this.rolePermissionDao.createPQuery().equal("roleid", role.rolebs.id).query();
            Set<String> permissions = new HashSet<>(rolepermissions.size() * 2);
            for (RolePermission permissionitem : rolepermissions) {
                role.permissions.add(permissionitem.permission);
            }
        }
        return role;
    }

    private Collection<Role> fillRoles(Collection<Role> roles, int field) {
        for (Role role : roles) {
            role._field = field;
        }

        if (BitUtil.include(field, Role.Field_permissionKeys)) {
            List<RolePermission> rolepermissions = this.rolePermissionDao.getAll();
            Map<Integer, Set<String>> roleidPermissionMap = new HashMap<>();
            for (RolePermission rolepermission : rolepermissions) {
                Set<String> s = roleidPermissionMap.get(rolepermission.roleid);
                if (s == null) {
                    s = new HashSet<>();
                    roleidPermissionMap.put(rolepermission.roleid, s);
                }
                s.add(rolepermission.permission);
            }

            for (Role role : roles) {
                if (role.permissions == null) {
                    Set<String> strings = roleidPermissionMap.get(role.rolebs.id);
                    if (strings != null) {
                        role.permissions = strings;
                    } else {
                        role.permissions = Collections.EMPTY_SET;
                    }
                }
            }
        }
        return roles;
    }

    private List<Role> genRoles(List<Rolebs> rolebss) {
        List<Role> ret = new ArrayList<>(rolebss.size());
        for (Rolebs rolebs : rolebss) {
            Role role = new Role(rolebs);
            ret.add(role);
        }
        return ret;
    }

    private Role genRole(Rolebs rolebs) {
        Role role = new Role(rolebs);
        return role;
    }


}
