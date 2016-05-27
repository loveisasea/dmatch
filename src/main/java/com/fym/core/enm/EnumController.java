package com.fym.core.enm;

/**
 * Owned by Planck System
 * Created by fengy on 2016/2/14.
 */

import com.fym.core.enm.cmd.EnumGetCmd;
import com.fym.core.err.OpResult;
import com.fym.core.err.OpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "core/enum")
public class EnumController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnumController.class);


    /**
     * 获取所有权限
     *
     * @return 权限列表
     * @throws OpException
     */
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @ResponseBody
    public Object getDatadicts(@RequestBody EnumGetCmd req) throws OpException {
//        Map<Object, String> enums = EnumHelperEx.get(req.name);
        return new OpResult("已获取enums");
    }

    /**
     * 获取所有权限
     *
     * @return 权限列表
     * @throws OpException
     */
    @RequestMapping(value = "/getall", method = RequestMethod.POST)
    @ResponseBody
    public Object getAllDatadicts() throws OpException {
//        Map all = EnumHelperEx.getAll();
//        return new OpResult("已获取enums", EnumHelperEx.getall());
        return new OpResult("已获取enums");
    }

}