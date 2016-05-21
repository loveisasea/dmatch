package com.fym.role;

import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;
import com.fym.role.cmd.RoleCreateCmd;
import com.fym.role.cmd.RoleDeleteCmd;
import com.fym.role.cmd.RoleGetCmd;
import com.fym.role.cmd.RoleModifyCmd;
import com.fym.role.obj.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

/**
 * Created by fengy on 2016/1/28.
 */

@Controller
@RequestMapping(value = "role")
public class RoleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);


    @Autowired
    private RoleService roleService;


    /**
     * 新建角色
     *
     * @param req 角色名称，描述，权限列表
     * @return
     * @throws OpException
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public Object create(@RequestBody RoleCreateCmd req) throws OpException {

        Role role = this.roleService.createRole(req);
        return new OpResult("已添加角色", role);
    }

    /**
     * 修改角色
     *
     * @param req 角色id，名称，描述，权限列表
     * @return
     * @throws OpException
     */
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    @ResponseBody
    public Object modify(@RequestBody RoleModifyCmd req) throws OpException {

        Role role = this.roleService.modifyRole(req);
        return new OpResult("已修改角色", role);
    }

    /**
     * 删除角色
     *
     * @param req 角色Id
     * @return
     * @throws OpException
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Object delete(@RequestBody RoleDeleteCmd req) throws OpException {

        this.roleService.deleteRole(req.roleid);
        return new OpResult("已删除角色");
    }


    /**
     * 获取所有角色
     *
     * @return
     * @throws OpException
     */
    @RequestMapping(value = "/getall", method = RequestMethod.POST)
    @ResponseBody
    public Object getAll() throws OpException {
        Collection<Role> roles = this.roleService.getAllRoles(0);
        return new OpResult("已获取角色列表", roles);
    }

    /**
     * 获取所有角色以及权限
     *
     * @return
     * @throws OpException
     */
    @RequestMapping(value = "/getallwp", method = RequestMethod.POST)
    @ResponseBody
    public Object getAllwp() throws OpException {
        Collection<Role> roles = this.roleService.getAllRoles(Role.Field_permissionKeys);
        return new OpResult("已获取带权限的角色列表", roles);
    }


    /**
     * 获取某个角色和权限
     *
     * @param req 角色id
     * @return
     * @throws OpException
     */
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @ResponseBody
    public Object get(@RequestBody RoleGetCmd req) throws OpException {

        Role role= this.roleService.getRole(req.id, Role.Field_permissionKeys);
        return new OpResult("获取角色", role);
    }


}
