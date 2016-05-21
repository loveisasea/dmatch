package com.fym.role;

/**
 * Owned by Planck System
 * Created by fengy on 2016/2/14.
 */

import com.fym.core.err.OpResult;
import com.fym.core.err.OpException;
import com.fym.role.obj.PermissionGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

@Controller
@RequestMapping(value = "permission")
public class PermissionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionController.class);


    @Autowired
    private PermissionCom permissionCom;


    /**
     * 获取所有权限
     *
     * @return 权限列表
     * @throws OpException
     */
    @RequestMapping(value = "/getall", method = RequestMethod.POST)
    @ResponseBody
    public Object getAllPermissions() throws OpException {
        Collection<PermissionGroup> permissions = this.permissionCom.getAllPermissions();
        return new OpResult("已获取权限列表", permissions);
    }


}