package com.fym.match;
/**
 * Owned by Planck System
 * Created by fengy on 2016/5/24.
 */


import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;
import com.fym.core.util.StringUtil;
import com.fym.match.cmd.*;
import com.fym.match.obj.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "match")
public class MatchController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchController.class);


    @Autowired
    private MatchService matchService;


    /**
     * 邀请入队
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/team/invite", method = RequestMethod.POST)
    @ResponseBody
    public Object teamInvite(@RequestBody TeamInviteCmd req) throws OpException {

        Team team = this.matchService.inviteTeam(req.matepid);
        return new OpResult("已邀请<" + req.matepid + ">加入team", team);
    }

    /**
     * 接受邀请
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/team/accept", method = RequestMethod.POST)
    @ResponseBody
    public Object teamAccept(@RequestBody TeamAcceptCmd req) throws OpException {

        Team team = this.matchService.acceptTeam(req.leaderpid);
        return new OpResult("已接受<" + req.leaderpid + ">的邀请，加入team", team);
    }

    /**
     * 拒绝邀请
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/team/reject", method = RequestMethod.POST)
    @ResponseBody
    public Object teamReject(@RequestBody TeamRejectCmd req) throws OpException {

        Team team = this.matchService.rejectTeam(req.leaderpid);
        return new OpResult("已拒绝<" + req.leaderpid + ">的邀请", team);
    }

    /**
     * 退出组队
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/team/quit", method = RequestMethod.POST)
    @ResponseBody
    public Object teamQuit() throws OpException {

        Team team = this.matchService.quitTeam();
        return new OpResult("已退出队伍", team);
    }


    /**
     * 获取列表
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    @ResponseBody
    public Object startMatch(@RequestBody StartMatchCmd req) throws OpException {

        this.matchService.startMatch(req.gameTypeKeys);
        return new OpResult("已加入匹配<" + StringUtil.compact(req.gameTypeKeys) + ">");
    }

    /**
     * 接受匹配
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/accept", method = RequestMethod.POST)
    @ResponseBody
    public Object accept(@RequestBody MatchAcceptCmd req) throws OpException {

        this.matchService.acceptMatch(req.matchuuid);
        return new OpResult("已接受匹配<" + req.matchuuid + ">");
    }

    /**
     * 拒绝匹配
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/reject", method = RequestMethod.POST)
    @ResponseBody
    public Object reject(@RequestBody MatchRejectCmd req) throws OpException {

        this.matchService.rejectMatch(req.matchuuid);
        return new OpResult("已拒绝匹配<" + req.matchuuid + ">");
    }

    /**
     * 退出匹配
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/quit", method = RequestMethod.POST)
    @ResponseBody
    public Object quitMatching() throws OpException {

        this.matchService.quitMatching();
        return new OpResult("已退出匹配");
    }


}
