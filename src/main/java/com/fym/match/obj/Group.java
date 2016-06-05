package com.fym.match.obj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/25.
 */
public class Group implements IUnit {
    /**
     * 分数
     */
    public int score;


    /**
     * 玩家id
     */
    public List<Person> persons;


    public Group(Collection<Person> persons) {
        this.persons = new ArrayList<>(persons);
        int sum = 0;
        for (Person person : this.persons) {
            sum += person.getScore();
        }
        this.score = sum / persons.size() + persons.size() * 10;
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public int getSize() {
        return this.persons.size();
    }

}
 
