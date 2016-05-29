package com.fym.match.obj;

/**
 *
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
        return this.score;
    }

    @Override
    public int getSize() {
        return 1;
    }

}
 
