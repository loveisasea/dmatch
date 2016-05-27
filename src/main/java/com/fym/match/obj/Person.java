package com.fym.match.obj;

import java.util.ArrayList;
import java.util.List;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/25.
 */
public class Person implements IUnit {

    /**
     * 玩家id
     */
    private int pid;

    /**
     * 分值
     */
    private int score;

    public Person(int pid, int score) {
        this.pid = pid;
        this.score = score;
    }

    @Override
    public int getScore() {
        return this.pid;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public List<Person> getPersons() {
        List<Person> ret = new ArrayList<>(1);
        ret.add(this);
        return ret;
    }
}
 
