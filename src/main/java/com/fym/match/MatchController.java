package com.fym.match;
/**
 *
 * Created by fengy on 2016/5/24.
 */


import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;
import com.fym.game.enm.GameType;
import com.fym.match.cmd.*;
import com.fym.match.obj.Group;
import com.fym.match.obj.IUnit;
import com.fym.match.obj.Match;
import com.fym.match.obj.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping(value = "match")
public class MatchController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchController.class);


    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchEngine matchEngine;


    /**
     * 获取列表
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/join", method = RequestMethod.POST)
    @ResponseBody
    public Object joinMatch(@RequestBody JoinMatchCmd req) throws OpException {

        this.matchService.joinMatch(req.gameTypeStr);
        return new OpResult("已加入匹配<" + req.gameTypeStr + ">");
    }

    /**
     * 获取列表
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/quit", method = RequestMethod.POST)
    @ResponseBody
    public Object quitMatch() throws OpException {

        this.matchService.quitMatch();
        return new OpResult("已退出匹配");
    }


    /**
     * 测试 - 个人匹配
     *
     * @return
     * @throws OpException
     */
    @RequestMapping(value = "/test/person/try", method = RequestMethod.POST)
    @ResponseBody
    public Object tryPersonMatch(@RequestBody TestTryPersonMatchCmd req) throws OpException {
        if (req.person == null) {
            throw new OpException(OpResult.FAIL, "参数<req.person>不能为空");
        }
        Person person = new Person(req.person.pid, req.person.score);
        if (req.gameTypeKey == null) {
            throw new OpException(OpResult.FAIL, "参数<req.gameTypeKey>不能为空");
        }

        Match match = this.matchEngine.tryMatch(person, req.gameTypeKey);
        return new OpResult("已加入个人匹配", match);
    }

    /**
     * 测试 - 团队匹配
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/test/group/try", method = RequestMethod.POST)
    @ResponseBody
    public Object tryGroupMatch(@RequestBody TestTryGroupMatchCmd req) throws OpException {
        if (req.group == null || req.group.persons == null) {
            throw new OpException(OpResult.FAIL, "参数<req.group>不能为空");
        }
        List<Person> persons = new ArrayList<>(req.group.persons.size());
        for (PersonCmd personcmd : req.group.persons) {
            persons.add(new Person(personcmd.pid, personcmd.score));
        }
        Group group = new Group(persons);
        if (req.gameTypeKey == null) {
            throw new OpException(OpResult.FAIL, "参数<req.gameTypeKey>不能为空");
        }
        Match match = this.matchEngine.tryMatch(group, req.gameTypeKey);
        return new OpResult("已加入团队匹配", match);
    }

    /**
     * 获取匹配池状态
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/pool/get", method = RequestMethod.POST)
    @ResponseBody
    public Object getMatchPool(@RequestBody GetMatchPoolCmd req) throws OpException {
        Map<Integer, List<IUnit>> matchPool = this.matchEngine.getMatchPool(req.gameTypeKey);
        return new OpResult("已获取匹配池<" + req.gameTypeKey + ">", matchPool);
    }

    /**
     * 获取所有匹配池
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/pool/getall", method = RequestMethod.POST)
    @ResponseBody
    public Object getAllMatchPool() throws OpException {
        Map<GameType, Map<Integer, List<IUnit>>> matchPools = this.matchEngine.getMatchPools();
        return new OpResult("已获取所有匹配池", matchPools);
    }

    /**
     * 获取所有匹配池
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/pool/clean", method = RequestMethod.POST)
    @ResponseBody
    public Object cleanMatchPool(@RequestBody GetMatchPoolCmd req) throws OpException {
        this.matchEngine.cleanMatchPool(req.gameTypeKey);
        return new OpResult("已清除匹配池<" + req.gameTypeKey + ">");
    }





}
