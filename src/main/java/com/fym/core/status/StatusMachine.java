package com.fym.core.status;


import com.fym.core.err.OpResult;
import com.fym.core.err.OpException;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by fengy on 2016/3/23.
 * 任务状态机
 */
public class StatusMachine {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(StatusMachine.class);


    //TaskStatus和对应的ProceedStatus的哈希
    Map<Object, ProceedStatus> proceedStatusMap = new HashMap<>();


    //TaskStatus, ProceedType和对应的ProceedStatus的哈希
    Map<Object, Map<Object, ProceedStatus>> proceedStatusProceedMap = new HashMap<>();


    public void add(Object oldTaskStatusKey, Object proceedTypeKey, Object newTaskStatusKey) {

        ProceedStatus oldPS = this.proceedStatusMap.get(oldTaskStatusKey);
        if (oldPS == null) {
            oldPS = new ProceedStatus();
            oldPS.status = oldTaskStatusKey;
            this.proceedStatusMap.put(oldTaskStatusKey, oldPS);
        }
        if (!oldPS.proceeds.contains(proceedTypeKey)) {
            oldPS.proceeds.add(proceedTypeKey);
        }

        Map<Object, ProceedStatus> proceedPoss = this.proceedStatusProceedMap.get(oldTaskStatusKey);
        if (proceedPoss == null) {
            proceedPoss = new HashMap<>();
            this.proceedStatusProceedMap.put(oldTaskStatusKey, proceedPoss);
        }
        ProceedStatus newPS = proceedPoss.get(proceedTypeKey);
        if (newPS == null) {
            newPS = this.proceedStatusMap.get(newTaskStatusKey);
            if (newPS == null) {
                newPS = new ProceedStatus();
                newPS.status = newTaskStatusKey;
                this.proceedStatusMap.put(newTaskStatusKey, newPS);
            }
        }
        proceedPoss.put(proceedTypeKey, newPS);
    }


    /**
     * 获取当前状态下的操作
     *
     * @param taskStatusKey 当前任务状态
     * @return
     * @throws OpException
     */
    public ProceedStatus get(Object taskStatusKey) throws OpException {
        ProceedStatus proceedStatus = this.proceedStatusMap.get(taskStatusKey);
        if (proceedStatus == null) {
            throw new OpException(OpResult.FAIL, "任务状态<" + taskStatusKey + ">非法");
        }
        return proceedStatus.copy();
    }


    /**
     * 是否能够支持状态跳转
     *
     * @param taskStatusKey
     * @param proceedTypeKey
     * @return
     * @throws OpException
     */
    public ProceedStatus checkProceed(Object taskStatusKey, Object proceedTypeKey) throws OpException {
        if (taskStatusKey == null) {
            throw new OpException(OpResult.FAIL, "任务状态为空");
        }
        if (proceedTypeKey == null) {
            throw new OpException(OpResult.FAIL, "操作为空");
        }
        Map<Object, ProceedStatus> map1 = this.proceedStatusProceedMap.get(taskStatusKey);
        if (map1 == null) {
            throw new OpException(OpResult.FAIL, "状态<" + taskStatusKey + ">不存在或者没有对应的操作");
        }
        ProceedStatus proceedStatus = map1.get(proceedTypeKey);
        if (proceedStatus == null) {
            throw new OpException(OpResult.FAIL, "不存在在状态<" + taskStatusKey + ">进行<" + proceedTypeKey + ">的操作");
        }
        return proceedStatus.copy();
    }
}


